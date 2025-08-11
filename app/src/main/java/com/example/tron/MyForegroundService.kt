package com.example.tron

import android.Manifest
import android.app.*
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Geocoder
import android.os.*
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.google.android.gms.location.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.first
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URL
import java.text.SimpleDateFormat
import java.util.*

class MyForegroundService : Service() {

    private var secondsElapsed = 0
    private lateinit var handler: Handler
    private lateinit var runnable: Runnable
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private var staffSl: String = "unknown"
    private var clientUrl: String = ""
    private val serviceScope = CoroutineScope(Dispatchers.Main)

//    private val Context.dataStore by preferencesDataStore(name = "settings")
    private val CLIENT_URL = stringPreferencesKey("client_url")

    override fun onCreate() {
        super.onCreate()
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
        TODO("Not yet implemented")
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
            .setContentTitle("Sensor Monitor Running")
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
        if (
            ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
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

        val fullUrl = if (clientUrl.endsWith("/")) {
            clientUrl + "api/livelocation"
        } else {
            "$clientUrl/api/livelocation"
        }

        CoroutineScope(Dispatchers.IO).launch {
            try {
                Log.d("MyForegroundService", "🌐 Endpoint: $fullUrl")

                val cityName = getCityName(latitude, longitude)
                val url = URL(fullUrl)
                val conn = url.openConnection() as HttpURLConnection

                conn.requestMethod = "POST"
                conn.setRequestProperty("Content-Type", "application/json")
                conn.doOutput = true

                val sdfDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                val sdfTime = SimpleDateFormat("HH:mm", Locale.getDefault())
                val now = Date()

                val jsonBody = """
                    {
                        "staf_sl": "$staffSl",
                        "log_dt": "${sdfDate.format(now)}",
                        "log_time": "${sdfTime.format(now)}",
                        "log_longitude": "$longitude",
                        "log_lattitude": "$latitude",
                        "log_location": "$cityName"
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
                Log.d("MyForegroundService", "📌 Sent city: $cityName")

            } catch (e: Exception) {
                Log.e("MyForegroundService", "❌ API request failed: ${e.message}", e)
            }
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
}