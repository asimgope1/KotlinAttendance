package com.example.tron

import android.Manifest
import android.app.*
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.location.Geocoder
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkRequest
import android.os.*
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import androidx.room.*
import com.google.android.gms.location.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URL
import java.text.SimpleDateFormat
import java.util.*

// DataStore extension
//private val Context.dataStore by preferencesDataStore(name = "session")
private val CLIENT_URL = stringPreferencesKey("client_url")

// Room Database Entities and DAO
@Entity(tableName = "locations")
data class LocationEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val staffSl: String,
    val latitude: Double,
    val longitude: Double,
    val locationName: String,
    val timestamp: Long,
    val isSynced: Boolean = false,
    val accuracy: Float = 0f,
    val batteryLevel: Int = 0,
    val networkType: String = ""
)

@Dao
interface LocationDao {
    @Insert
    suspend fun insert(location: LocationEntity): Long

    @Query("SELECT * FROM locations WHERE isSynced = 0 ORDER BY timestamp DESC")
    fun getUnsyncedLocations(): Flow<List<LocationEntity>>

    @Query("SELECT COUNT(*) FROM locations WHERE isSynced = 0")
    fun getUnsyncedCount(): Flow<Int>

    @Query("UPDATE locations SET isSynced = 1 WHERE id IN (:ids)")
    suspend fun markAsSynced(ids: List<Long>)

    @Query("DELETE FROM locations WHERE isSynced = 1 AND timestamp < :olderThan")
    suspend fun cleanupOldLocations(olderThan: Long = System.currentTimeMillis() - 30 * 24 * 60 * 60 * 1000)
}

@Database(entities = [LocationEntity::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun locationDao(): LocationDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "location_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}

class LocationRepository(private val locationDao: LocationDao) {
    suspend fun saveLocation(location: LocationEntity): Long {
        return locationDao.insert(location)
    }

    fun getUnsyncedLocations(): Flow<List<LocationEntity>> {
        return locationDao.getUnsyncedLocations()
    }

    fun getUnsyncedCount(): Flow<Int> {
        return locationDao.getUnsyncedCount()
    }

    suspend fun markLocationsAsSynced(ids: List<Long>) {
        locationDao.markAsSynced(ids)
    }

    suspend fun cleanupOldData() {
        locationDao.cleanupOldLocations()
    }
}

class MyForegroundService : Service() {

    private var secondsElapsed = 0
    private lateinit var handler: Handler
    private lateinit var runnable: Runnable
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private var staffSl: String = "unknown"
    private var clientUrl: String = ""
    private val serviceScope = CoroutineScope(Dispatchers.Main)
    private lateinit var repository: LocationRepository
    private var isOnline = true

    override fun onCreate() {
        super.onCreate()
        val database = AppDatabase.getInstance(this)
        repository = LocationRepository(database.locationDao())

        // Network monitoring
        val connectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        connectivityManager.registerNetworkCallback(
            NetworkRequest.Builder().build(),
            object : ConnectivityManager.NetworkCallback() {
                override fun onAvailable(network: Network) {
                    isOnline = true
                    Log.d("MyForegroundService", "📶 Network available, syncing unsynced locations")
                    CoroutineScope(Dispatchers.IO).launch {
                        syncAllUnsyncedLocations()
                    }
                }

                override fun onLost(network: Network) {
                    isOnline = false
                    Log.d("MyForegroundService", "📶 Network lost")
                }
            }
        )

        createNotificationChannel()
        handler = Handler(Looper.getMainLooper())
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        staffSl = intent?.getStringExtra("staf_sl") ?: "unknown"

        serviceScope.launch {
            try {
                clientUrl = withContext(Dispatchers.IO) { loadClientUrl() }
                Log.d("MyForegroundService", "🌐 Loaded client URL: $clientUrl")
                startTimer()
            } catch (e: Exception) {
                Log.e("MyForegroundService", "⚠️ Failed to load client URL: ${e.message}")
            }
        }

        return START_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacks(runnable)
        serviceScope.cancel()
        Log.d("MyForegroundService", "🛑 Service destroyed")
    }

    override fun onBind(p0: Intent?): IBinder? {
        return null
    }

    private suspend fun loadClientUrl(): String {
        return try {
            val prefs = applicationContext.dataStore.data.first()
            prefs[CLIENT_URL] ?: ""
        } catch (e: Exception) {
            Log.e("MyForegroundService", "⚠️ Failed to load clientUrl: ${e.message}")
            ""
        }
    }

    private fun startTimer() {
        runnable = object : Runnable {
            override fun run() {
                secondsElapsed++

                if (secondsElapsed == 1) {
                    val notification = createNotification("Running time: 00:01")
                    startForeground(1, notification)
                    requestRealtimeLocation()
                }

                Log.d("MyForegroundService", "⏱ Running time: $secondsElapsed seconds")

                if (secondsElapsed % 10 == 0) {
                    requestRealtimeLocation()
                }

                updateNotification(secondsElapsed)
                handler.postDelayed(this, 1000)
            }
        }

        handler.post(runnable)
    }

    private fun updateNotification(seconds: Int) {
        val minutes = seconds / 60
        val secondsDisplay = seconds % 60
        val content = "Running time: %02d:%02d".format(minutes, secondsDisplay)
        val manager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        manager.notify(1, createNotification(content))
    }

    private fun createNotification(content: String): Notification {
        return NotificationCompat.Builder(this, "foreground_channel")
            .setContentTitle("Location Tracker Running")
            .setContentText(content)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setOnlyAlertOnce(true)
            .setOngoing(true)
            .build()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                "foreground_channel",
                "Foreground Service",
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "Used for continuous location tracking"
            }
            val manager = getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(channel)
        }
    }

    private fun requestRealtimeLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
        ) {
            Log.w("MyForegroundService", "🚫 Location permissions not granted.")
            return
        }

        val locationRequest = LocationRequest.create().apply {
            interval = 10000
            fastestInterval = 5000
            priority = Priority.PRIORITY_HIGH_ACCURACY
            maxWaitTime = 15000
            numUpdates = 1
        }

        val locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                val location = locationResult.lastLocation
                if (location != null) {
                    Log.d("MyForegroundService", "📍 Got location: ${location.latitude}, ${location.longitude}")
                    sendLocationToServer(location.latitude, location.longitude)
                } else {
                    Log.w("MyForegroundService", "⚠️ Location is null.")
                }
                fusedLocationClient.removeLocationUpdates(this)
            }
        }

        fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper())
    }

    private fun sendLocationToServer(latitude: Double, longitude: Double) {
        if (clientUrl.isBlank()) {
            Log.e("MyForegroundService", "❌ Client URL is blank, cannot send location.")
            return
        }

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val cityName = getCityName(latitude, longitude)
                val timestamp = System.currentTimeMillis()

                // Save location to database first (mark as unsynced)
                val locationEntity = LocationEntity(
                    staffSl = staffSl,
                    latitude = latitude,
                    longitude = longitude,
                    locationName = cityName,
                    timestamp = timestamp,
                    accuracy = 0f,
                    batteryLevel = getBatteryLevel(),
                    networkType = getNetworkType(),
                    isSynced = false
                )

                val locationId = repository.saveLocation(locationEntity)
                Log.d("MyForegroundService", "💾 Saved location to DB with ID: $locationId")

                // Now try to sync to server
                syncLocationToServer(locationEntity, locationId)

            } catch (e: Exception) {
                Log.e("MyForegroundService", "❌ Failed to save location to DB: ${e.message}", e)
            }
        }
    }

    private suspend fun syncLocationToServer(locationEntity: LocationEntity, locationId: Long) {
        val fullUrl = if (clientUrl.endsWith("/")) {
            clientUrl + "api/livelocation"
        } else {
            "$clientUrl/api/livelocation"
        }

        try {
            Log.d("MyForegroundService", "🌐 Endpoint: $fullUrl")

            val url = URL(fullUrl)
            val conn = url.openConnection() as HttpURLConnection

            conn.requestMethod = "POST"
            conn.setRequestProperty("Content-Type", "application/json")
            conn.doOutput = true
            conn.connectTimeout = 10000
            conn.readTimeout = 10000

            val sdfDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val sdfTime = SimpleDateFormat("HH:mm", Locale.getDefault())
            val now = Date(locationEntity.timestamp)
            val sdfTripId = SimpleDateFormat("yyyyMMddHHmmss", Locale.getDefault())
            val tripId = sdfTripId.format(now).toLong() // numeric trip_id



            val jsonBody = """
        {
            "staf_sl": "${locationEntity.staffSl}",
            "log_dt": "${sdfDate.format(now)}",
            "log_time": "${sdfTime.format(now)}",
            "log_longitude": "${locationEntity.longitude}",
            "log_lattitude": "${locationEntity.latitude}",
            "log_location": "${locationEntity.locationName}",
          "trip_id":$tripId


        }
    """.trimIndent()

            Log.d("MyForegroundService", "📦 Request body: $jsonBody")

            OutputStreamWriter(conn.outputStream).use { writer ->
                writer.write(jsonBody)
                writer.flush()
            }

            val responseCode = conn.responseCode
            val inputStream = if (responseCode in 200..299) conn.inputStream else conn.errorStream
            val response = inputStream.bufferedReader().use { it.readText() }

            Log.d("MyForegroundService", "✅ API response [$responseCode]: $response")

            // Parse the JSON response to check actual API status
            val isSuccess = try {
                // Simple JSON parsing to check if status is "success"
                response.contains("\"status\":\"success\"") ||
                        response.contains("'status':'success'")
            } catch (e: Exception) {
                false
            }

            if (responseCode in 200..299 && isSuccess) {
                // Mark as synced in database only if API returns success
                repository.markLocationsAsSynced(listOf(locationId))
                Log.d("MyForegroundService", "✅ Location synced successfully: $locationId")
            } else {
                Log.e("MyForegroundService", "❌ API error: $responseCode - $response")
                // Don't mark as synced if API returned failure status
            }

        } catch (e: Exception) {
            Log.e("MyForegroundService", "❌ API request failed: ${e.message}", e)
        }
    }
    private suspend fun syncAllUnsyncedLocations() {
        try {
            val unsyncedLocations = repository.getUnsyncedLocations().first()
            Log.d("MyForegroundService", "🔄 Syncing ${unsyncedLocations.size} unsynced locations")

            for (location in unsyncedLocations) {
                syncLocationToServer(location, location.id)
                delay(1000) // Add delay between sync attempts to avoid rate limiting
            }

        } catch (e: Exception) {
            Log.e("MyForegroundService", "❌ Failed to sync unsynced locations: ${e.message}", e)
        }
    }

    private fun getCityName(latitude: Double, longitude: Double): String {
        return try {
            val geocoder = Geocoder(this, Locale.getDefault())
            val addresses = geocoder.getFromLocation(latitude, longitude, 1)

            if (!addresses.isNullOrEmpty()) {
                addresses[0].getAddressLine(0) ?: "Unknown Address"
            } else {
                "Unknown Address"
            }
        } catch (e: Exception) {
            Log.e("MyForegroundService", "🌐 Geocoder failed: ${e.message}")
            "Location Error"
        }
    }

    private fun getBatteryLevel(): Int {
        val batteryIntent = registerReceiver(null, IntentFilter(Intent.ACTION_BATTERY_CHANGED))
        return batteryIntent?.getIntExtra(BatteryManager.EXTRA_LEVEL, -1) ?: -1
    }

    private fun getNetworkType(): String {
        val connectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkInfo = connectivityManager.activeNetworkInfo
        return networkInfo?.typeName ?: "UNKNOWN"
    }
}

// Sync utility for manual sync from Dashboard
object SyncUtils {
    suspend fun syncUnsyncedLocations(context: Context): SyncResult {
        return try {
            val database = AppDatabase.getInstance(context)
            val repository = LocationRepository(database.locationDao())

            val unsynced = repository.getUnsyncedLocations().first()
            if (unsynced.isEmpty()) {
                return SyncResult(success = true, message = "No data to sync", count = 0)
            }

            // Load client URL directly from DataStore
            val clientUrl = try {
                val prefs = context.dataStore.data.first()
                prefs[CLIENT_URL] ?: ""
            } catch (e: Exception) {
                Log.e("SyncUtils", "⚠️ Failed to load clientUrl: ${e.message}")
                ""
            }

            if (clientUrl.isBlank()) {
                return SyncResult(success = false, message = "No client URL configured", count = 0)
            }

            Log.d("SyncUtils", "🔄 Syncing ${unsynced.size} locations")

            var successfullySynced = 0

            for (location in unsynced) {
                try {
                    val fullUrl = if (clientUrl.endsWith("/")) {
                        clientUrl + "api/livelocation"
                    } else {
                        "$clientUrl/api/livelocation"
                    }

                    val url = URL(fullUrl)
                    val conn = url.openConnection() as HttpURLConnection

                    conn.requestMethod = "POST"
                    conn.setRequestProperty("Content-Type", "application/json")
                    conn.doOutput = true
                    conn.connectTimeout = 10000
                    conn.readTimeout = 10000

                    val sdfDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                    val sdfTime = SimpleDateFormat("HH:mm", Locale.getDefault())
                    val now = Date(location.timestamp)
                    val sdfTripId = SimpleDateFormat("yyyyMMddHHmmss", Locale.getDefault())
                    val tripId = sdfTripId.format(now).toLong() // numeric trip_id


                    val jsonBody = """
                        {
                            "staf_sl": "${location.staffSl}",
                            "log_dt": "${sdfDate.format(now)}",
                            "log_time": "${sdfTime.format(now)}",
                            "log_longitude": "${location.longitude}",
                            "log_lattitude": "${location.latitude}",
                            "log_location": "${location.locationName}",
                            "trip_id":$tripId


                        }
                    """.trimIndent()

                    OutputStreamWriter(conn.outputStream).use { writer ->
                        writer.write(jsonBody)
                        writer.flush()
                    }

                    val responseCode = conn.responseCode
                    if (responseCode in 200..299) {
                        successfullySynced++
                    } else {
                        Log.e("SyncUtils", "❌ Sync failed for location ${location.id}: $responseCode")
                    }

                } catch (e: Exception) {
                    Log.e("SyncUtils", "❌ Sync failed for location ${location.id}: ${e.message}")
                }

                delay(500) // Small delay between requests
            }

            if (successfullySynced > 0) {
                // Mark successfully synced locations
                val syncedIds = unsynced.take(successfullySynced).map { it.id }
                repository.markLocationsAsSynced(syncedIds)
            }

            SyncResult(
                success = successfullySynced > 0,
                message = "Synced $successfullySynced of ${unsynced.size} locations",
                count = successfullySynced
            )

        } catch (e: Exception) {
            Log.e("SyncUtils", "❌ Sync process failed: ${e.message}", e)
            SyncResult(success = false, message = "Sync failed: ${e.message}", count = 0)
        }
    }

    fun getUnsyncedCount(context: Context): Flow<Int> {
        val database = AppDatabase.getInstance(context)
        val repository = LocationRepository(database.locationDao())
        return repository.getUnsyncedCount()
    }
}

data class SyncResult(val success: Boolean, val message: String, val count: Int)