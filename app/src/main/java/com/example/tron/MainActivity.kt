package com.example.tron

import android.app.Application
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.TimePicker
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.Crossfade
import androidx.compose.material3.CardElevation
import androidx.compose.material3.CardDefaults
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData

import android.app.Service
import android.os.IBinder
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Geocoder
import java.text.SimpleDateFormat
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.PowerManager
import android.provider.Settings
import android.util.Log
import android.Manifest
import android.R.id.message
import android.content.ContentResolver
import android.provider.OpenableColumns
import android.telephony.TelephonyManager
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.*
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.graphics.*
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.RequestBody
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.compose.animation.core.tween
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.DropdownMenuItem


import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.unit.*
import androidx.core.content.ContextCompat
import androidx.core.view.WindowCompat
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.*
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.*
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.GET
import retrofit2.http.Query
import retrofit2.http.Multipart
import retrofit2.http.Part


import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.runtime.Composable
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp

import androidx.compose.ui.text.style.TextOverflow
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.ResponseBody
import okhttp3.logging.HttpLoggingInterceptor
import org.json.JSONObject
import org.w3c.dom.Text
import retrofit2.HttpException
import java.util.Date
import java.util.Locale

// ---------------- MODERNIZED THEME ----------------
private val DarkColorScheme = darkColorScheme(
    primary = Color(0xFF1E88E5), // Modern blue primary
    secondary = Color(0xFF00C853), // Vibrant green accent
    tertiary = Color(0xFFFF4081), // Pink for highlights
    background = Color(0xFF121212), // Darker background
    surface = Color(0xFF1E1E1E), // Elevated surfaces
    onPrimary = Color.White,
    onSecondary = Color.White,
    onTertiary = Color.White,
    onBackground = Color(0xFFE0E0E0),
    onSurface = Color(0xFFE0E0E0),
    error = Color(0xFFEF5350)
)

private val LightColorScheme = lightColorScheme(
    primary = Color(0xFF1976D2), // Deep blue
    secondary = Color(0xFF388E3C), // Forest green
    tertiary = Color(0xFFD81B60), // Deep pink
    background = Color(0xFFF5F5F5), // Light gray background
    surface = Color.White,
    onPrimary = Color.White,
    onSecondary = Color.White,
    onTertiary = Color.White,
    onBackground = Color(0xFF212121),
    onSurface = Color(0xFF212121),
    error = Color(0xFFD32F2F)
)

@Composable
fun TronTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        shapes = Shapes(
            extraSmall = RoundedCornerShape(4.dp),
            small = RoundedCornerShape(8.dp),
            medium = RoundedCornerShape(16.dp),
            large = RoundedCornerShape(24.dp),
            extraLarge = RoundedCornerShape(32.dp)
        ),
        content = content
    )
}

val Shapes = Shapes(
    small = RoundedCornerShape(8.dp),
    medium = RoundedCornerShape(16.dp),
    large = RoundedCornerShape(24.dp)
)

val Typography = Typography(
    displayLarge = androidx.compose.ui.text.TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 57.sp,
        lineHeight = 64.sp,
        letterSpacing = (-0.25).sp
    ),
    // Add other text styles as needed
)

// ---------------- DATASTORE ----------------
val Context.dataStore by preferencesDataStore("session")






data class StaffData(
    val staf_sl: Int?,
    val dept_nm: String?,
    val desg_nm: String?,
    val loc_cd: Int?,
    val div_sl: Int?,
    val staf_nm: String?,
    val emp_code: String?,
    val device_code: String?,
    val trackon: String?,
    val r_usr_sl: String?,
    val staf_image: String?,
    val tot: Int?,
    val PRESENT: Double?,
    val present_per: Double?,
    val ABSENT: Double?,
    val absent_per: Double?,
    val HALFDAY: Double?,
    val halfday_per: Double?,
    val LEAVEDAY: Double?,
    val leave_per: Double?,
    val WOFF: Double?,
    val woff_per: Double?
)

data class StaffInfo(
    val staf_sl: Int?,
    val staf_nm: String,
    val dept_nm: String,
    val desg_nm: String,
    val emp_code: String?,
    val present_per: Double?
)

data class TourResult(
    val isSuccess: Boolean,
    val message: String
)

// ---------------- TOUR VIEWMODEL ----------------
// ---------------- TOUR MODELS ----------------
// In your TripDetail data class, ensure TierSl is Int
data class TripDetail(
    val sl: Int? = null,
    val TravelDate: String = "",
    val TravelFrom: String = "",  // This is LOCATION (starting point)
    val TravelTo: String = "",    // This is LOCATION (destination)
    val TravelMode: String = "",
    val km: String = "",
    val Particular: String = "",
    val NightHalt: String = "no",
    val TierSl: Int = 1,
    val Location: String = "",    // This might be redundant with TravelFrom/TravelTo
    val TourId: Int? = null,
    val ApprovedTotalKM: String = "",
    val WorkTime: String = ""
)

// In your Tour data class, ensure RequestAdvance is Int
data class Tour(
    val TourId: Int? = null,
    val Sl: Int? = null,
    val TourTittle: String = "",
    val TourDescription: String = "",
    val TourFrom: String = "",
    val TourTo: String = "",
    val TourFromTime: String = "",
    val TourToTime: String = "",
    val RequestAdvance: Int = 0, // This should be Int (0 or 1)
    val RequestedAdvanceAmount: String = "",
    val StageSl: Int? = null,
    val StageName: String? = null,
    val loc_cd: String? = null,
    val staf_sl: String? = null,
    val TourType: String = "Local" // Change from String? to String with default

)

data class Expense(
    val Sl: Int = 0, // This is the expense ID
    val slno: Int = 0, // Sequence number
    val ExpenseDate: String = "",
    val ExpenseSl: Int = 1,
    val Location: String = "",
    val ExpenseFrom: String = "",
    val ExpenseTo: String = "",
    val Amount: Double = 0.0,
    val Particular: String = "",
    val BillFileBase64: String = "",
    val FileName: String = "",
    val MimeType: String = "",
    var fileUri: Uri? = null,
    val ExpenseName: String = "",
    val BillPath: String = "", // Add BillPath
    val ApprovedAmount: String = "" // Add ApprovedAmount
)



data class TourDetailResponse(
    val status: String,
    val Code: String,
    val msg: String,
    val data_value: List<TourDetail>
)


data class TourDetail(
    val RequestedAdvanceAmount: Double,
    val RequestAdvance: Int,
    val TourTittle: String,
    val TourDescription: String,
    val TourFrom: String,
    val TourFromTime: String,
    val TourTo: String,
    val TourToTime: String,
    val StageSl: Int,
    val TourId: Int,
    val ActionName: String,
    val Advance_Request: String,
    val travel_details: List<TravelDetail>,
    val expense_details: List<ExpenseDetail>,
    val logs_details: List<LogDetail>
)


data class TravelDetail(
    val sl: Int,
    val dt: String,
    val ft: String,
    val tt: String,
    val TravelMode: String,
    val Particular: String,
    val TotalKm: String,
    val Location: String,
    val TierSl: Int,
    val NightHalt: String,
    val ApprovedTotalKM: String,
    val ApprovedWorkTime: String,
    val WorkTime: String,
    val TourId: Int
)






data class TourRequest(
    val staf_sl: Int,
    val TourTittle: String,
    val TourDescription: String,
    val TourFrom: String,
    val TourFromTime: String,
    val TourTo: String,
    val TourToTime: String,
    val RequestAdvance: Double,
    val RequestedAdvanceAmount: Double,
    val loc_cd: String,
    val TourTravel: List<TripDetail>,
    val TourExpense: List<Expense>
)


data class ExpenseDetail(
    val slno: Int,
    val Sl: Int,
    val dt: String,
    val ExpenseName: String,
    val ExpenseSl: Int,
    val Location: String,
    val fdt: String,
    val tdt: String,
    val Amount: String,
    val ApprovedAmount: String,
    val Particular: String,
    val BillPath: String
)


data class LogDetail(
    val Sl: Int,
    val Created: String,
    val Stage: String,
    val Staf: String,
    val Comments: String
)

data class TourListResponse(
    val status: String,
    val data_value: List<Tour>
)

data class LocationResponse(
    val status: String,
    val Code: String,
    val msg: String,
    val data_value: List<LocationItem>
)


data class LocationItem(
    val LocationName: String,
    val TierSl: Int
)

data class ExpenseResponse(
    val status: String,
    val Code: String,
    val msg: String,
    val data_value: List<ExpenseItem>
)


data class ExpenseItem(
    val ExpenseSl: Int,
    val ExpenseName: String,
    val LocationSpecified: Int, // Changed from String to Int
    val BillMandatory: Int,     // Changed from String to Int
    val FromDateToDate: Int     // Changed from String to Int
)



data class ApiResponse(
    val Status: String,
    val Message: String
)

class SessionViewModel(app: Application) : AndroidViewModel(app) {

    private val ds = app.dataStore

    // 🔹 StateFlows
    val isTracking = ds.data.map { it[IS_TRACKING] == true }
        .stateIn(viewModelScope, SharingStarted.Eagerly, false)

    val clientUrl = ds.data.map { it[CLIENT_URL] }
        .stateIn(viewModelScope, SharingStarted.Eagerly, null)

    val username = ds.data.map { it[USERNAME] }
        .stateIn(viewModelScope, SharingStarted.Eagerly, null)

    val isLoggedIn = ds.data.map { it[IS_LOGGED_IN] == true }
        .stateIn(viewModelScope, SharingStarted.Eagerly, false)

    val staffSl = ds.data.map { it[STAFF_SL] }
        .stateIn(viewModelScope, SharingStarted.Eagerly, null)

    val deptName = ds.data.map { it[DEPT_NAME] }
        .stateIn(viewModelScope, SharingStarted.Eagerly, null)

    val designation = ds.data.map { it[DESIGNATION] }
        .stateIn(viewModelScope, SharingStarted.Eagerly, null)

    val empCode = ds.data.map { it[EMP_CODE] }
        .stateIn(viewModelScope, SharingStarted.Eagerly, null)

    val presentPer = ds.data.map { it[PRESENT_PER] }
        .stateIn(viewModelScope, SharingStarted.Eagerly, "0.0")

    val trackingStartTime = ds.data.map { it[TRACKING_START_TIME] ?: 0L }
        .stateIn(viewModelScope, SharingStarted.Eagerly, 0L)
    // inside companion object
    val LOC_CD = stringPreferencesKey("loc_cd")

    // add this property near other flows
    val locCd = ds.data.map { it[LOC_CD] }
        .stateIn(viewModelScope, SharingStarted.Eagerly, null)

    // add saver function
    suspend fun saveLocCd(code: String) = ds.edit {
        it[LOC_CD] = code
        Log.d("SessionViewModel", "Saved loc_cd: $code")
    }

    // 🔹 Optional in-memory cache for quick access
    private val _staffInfo = mutableStateOf<StaffInfo?>(null)
    val staffInfo: StaffInfo? get() = _staffInfo.value


    fun saveStaffInfo(data: StaffInfo?) {
        _staffInfo.value = data
    }

    // 🔹 Save functions
    suspend fun saveStaffDetails(data: StaffData) = ds.edit {
        it[STAFF_SL] = data.staf_sl.toString()
        it[DEPT_NAME] = data.dept_nm.toString()
        it[DESIGNATION] = data.desg_nm.toString()
        it[EMP_CODE] = data.emp_code.toString()
        it[PRESENT_PER] = data.present_per?.toString() ?: "0.0"
        it[LOC_CD] = data.loc_cd?.toString() ?: ""
        Log.d("SessionViewModel", "Staff details saved. SL: ${data.staf_sl}")
        Log.d("SessionViewModel", "Staff details saved. SL: ${data.staf_sl}, LOC_CD: ${data.loc_cd}")
        Log.d("SessionViewModel", "Staff details saved. SL: ${staffInfo}")
    }

    suspend fun saveClientUrl(url: String) = ds.edit {
        it[CLIENT_URL] = url
        Log.d("SessionViewModel", "✅ Client URL saved: $url")
    }

    suspend fun saveUsername(name: String) = ds.edit {
        it[USERNAME] = name
    }

    suspend fun setLoginState(state: Boolean) = ds.edit {
        it[IS_LOGGED_IN] = state
    }

    suspend fun clearSession() = ds.edit {
        it.clear()
        Log.d("SessionViewModel", "🧹 Session cleared")
    }

    suspend fun setTrackingActive(active: Boolean) = ds.edit {
        it[IS_TRACKING] = active
    }

    suspend fun setTrackingStartTime(timeMillis: Long) = ds.edit {
        it[TRACKING_START_TIME] = timeMillis
    }

    suspend fun clearTrackingStartTime() = ds.edit {
        it.remove(TRACKING_START_TIME)
    }

    // 🔹 Preference Keys
    companion object {
        val CLIENT_URL = stringPreferencesKey("client_url")
        val USERNAME = stringPreferencesKey("username")
        val IS_LOGGED_IN = booleanPreferencesKey("is_logged_in")
        val STAFF_SL = stringPreferencesKey("staff_sl")
        val DEPT_NAME = stringPreferencesKey("dept_name")
        val DESIGNATION = stringPreferencesKey("designation")
        val EMP_CODE = stringPreferencesKey("emp_code")
        val PRESENT_PER = stringPreferencesKey("present_per")
        val IS_TRACKING = booleanPreferencesKey("is_tracking")
        val TRACKING_START_TIME = longPreferencesKey("tracking_start_time")
    }
}






private fun parseApiDate(dateString: String): String {
    return try {
        val inputFormats = listOf(
            "dd/MM/yyyy", // ADD THIS FORMAT FIRST
            "yyyy-MM-dd'T'HH:mm:ss",
            "MM/dd/yyyy",
            "yyyy-MM-dd"
        )
        val outputFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        for (pattern in inputFormats) {
            try {
                val inputFormat = SimpleDateFormat(pattern, Locale.getDefault())
                val date = inputFormat.parse(dateString)
                if (date != null) {
                    return outputFormat.format(date)
                }
            } catch (e: Exception) {
                continue
            }
        }
        Log.e("parseApiDate", "Failed to parse date: $dateString")
        dateString // Fallback
    } catch (e: Exception) {
        Log.e("parseApiDate", "Error parsing date: $dateString", e)
        dateString
    }
}

private fun parseApiDateForDisplay(dateString: String): String {
    return try {
        val inputFormats = listOf(
            "dd/MM/yyyy", // ADD THIS FORMAT FIRST
            "MM/dd/yyyy",
            "yyyy-MM-dd'T'HH:mm:ss",
            "yyyy-MM-dd"
        )
        val outputFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        for (pattern in inputFormats) {
            try {
                val inputFormat = SimpleDateFormat(pattern, Locale.getDefault())
                val date = inputFormat.parse(dateString)
                if (date != null) {
                    return outputFormat.format(date)
                }
            } catch (e: Exception) {
                continue
            }
        }
        Log.e("parseApiDateForDisplay", "Failed to parse date: $dateString")
        dateString // Fallback
    } catch (e: Exception) {
        Log.e("parseApiDateForDisplay", "Error parsing date: $dateString", e)
        dateString
    }
}

private fun parseApiTime(timeString: String): String {
    return try {
        if (timeString.isBlank()) return ""

        val inputFormats = listOf(
            "HH:mm:ss",
            "HH:mm",
            "hh:mm a",
            "hh:mm:ss a"
        )

        for (format in inputFormats) {
            try {
                val inputFormat = SimpleDateFormat(format, Locale.getDefault())
                val date = inputFormat.parse(timeString)
                if (date != null) {
                    val outputFormat = SimpleDateFormat("hh:mm a", Locale.getDefault())
                    return outputFormat.format(date)
                }
            } catch (e: Exception) {
                continue
            }
        }
        timeString
    } catch (e: Exception) {
        Log.e("parseApiTime", "Error parsing time: $timeString", e)
        timeString
    }
}

private fun formatTimeForApi(time: String): String {
    return try {
        if (time.isBlank()) return ""

        val inputFormats = listOf(
            "hh:mm a",
            "hh:mm:ss a",
            "HH:mm:ss",
            "HH:mm"
        )

        for (format in inputFormats) {
            try {
                val inputFormat = SimpleDateFormat(format, Locale.getDefault())
                val date = inputFormat.parse(time)
                if (date != null) {
                    val outputFormat = SimpleDateFormat("HH:mm:ss", Locale.getDefault())
                    return outputFormat.format(date)
                }
            } catch (e: Exception) {
                continue
            }
        }
        time
    } catch (e: Exception) {
        Log.e("formatTimeForApi", "Error formatting time: $time", e)
        time
    }
}

class TourViewModel(application: Application) : AndroidViewModel(application) {

    var loading by mutableStateOf(false)
        private set
    var isLoadingDetails by mutableStateOf(false)
        private set

    var tourList by mutableStateOf<List<Tour>>(emptyList())
        private set

    var locations by mutableStateOf<List<LocationItem>>(emptyList())
        private set

    var expenses by mutableStateOf<List<ExpenseItem>>(emptyList())
        private set

    var currentTour by mutableStateOf<Tour?>(null)
        private set

    var tripDetails by mutableStateOf<List<TripDetail>>(emptyList())
        private set

    var expenseDetails by mutableStateOf<List<Expense>>(emptyList())
        private set


    // Add this to your TourViewModel
    var selectedTour by mutableStateOf<Tour?>(null)
        private set

    fun selectTour(tour: Tour) {
        selectedTour = tour
    }

    fun clearSelectedTour() {
        selectedTour = null
        currentTour = null
        tripDetails = emptyList()
        expenseDetails = emptyList()
    }

    fun validateExpenses(expenseDetails: List<Expense>, expenseTypes: List<ExpenseItem>): String? {
        expenseDetails.forEach { expense ->
            val expenseType = expenseTypes.find { it.ExpenseName == expense.ExpenseName }
            expenseType?.let { type ->
                // Check if bill is mandatory but no file attached
                if (type.BillMandatory == 1 && expense.fileUri == null && expense.BillFileBase64.isEmpty() && expense.BillPath.isEmpty()) {
                    return "Bill is mandatory for expense: ${expense.ExpenseName}"
                }

                // Check if location is specified but empty
                if (type.LocationSpecified == 1 && expense.Location.isBlank()) {
                    return "Location is required for expense: ${expense.ExpenseName}"
                }

                // Check if date range is required but empty
                if (type.FromDateToDate == 1 && (expense.ExpenseFrom.isBlank() || expense.ExpenseTo.isBlank())) {
                    return "From and To dates are required for expense: ${expense.ExpenseName}"
                }
            }
        }
        return null
    }


    // -------------------- API SERVICE --------------------
    private fun getApiService(clientUrl: String): TourApiService {
        val loggingInterceptor = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

        val client = OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .build()

        return Retrofit.Builder()
            .baseUrl(clientUrl)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(TourApiService::class.java)
    }
    private fun getExpenseFiles(expenseDetails: List<Expense>): List<MultipartBody.Part> {
        return expenseDetails.mapNotNull { expense ->
            expense.fileUri?.let { uri ->
                try {
                    val file = File(uri.path!!)
                    if (file.exists()) {
                        val requestFile = file.asRequestBody("application/pdf".toMediaType())
                        MultipartBody.Part.createFormData(
                            "files",
                            file.name,
                            requestFile
                        )
                    } else {
                        null
                    }
                } catch (e: Exception) {
                    Log.e("TourViewModel", "Error creating file part", e)
                    null
                }
            }
        }
    }

    // -------------------- FETCHING DATA --------------------
    suspend fun fetchTourList(staffSl: String, clientUrl: String) {
        Log.d("TourViewModel", "Fetching tour list with staffSl=$staffSl")
        Log.d("TourViewModel", "Fetching tour list with clientUrl=$clientUrl")
        loading = true
        try {
            val apiService = getApiService(clientUrl)
            val response = apiService.getTourList(staffSl)
            Log.d("TourViewModel", "Tour list response: $response")

            if (response.status == "success") {
                tourList = response.data_value
            }
        } catch (e: Exception) {
            Log.e("TourViewModel", "Error fetching tour list", e)
        } finally {
            loading = false
        }
    }


    suspend fun fetchTourDetails(tourId: Int, clientUrl: String) {
        isLoadingDetails = true // Set loading to true when starting
        try {
            val apiService = getApiService(clientUrl)
            val response = apiService.getTourDetails(tourId)
            Log.d("TourViewModel", "Tour details response: $response")

            if (response.status == "success" && response.data_value.isNotEmpty()) {
                val tourDetail = response.data_value[0]

                // Convert to Tour object with proper date parsing
                // Convert to Tour object with proper date parsing
                val tour = Tour(
                    TourId = tourDetail.TourId,
                    Sl = tourDetail.TourId,
                    TourTittle = tourDetail.TourTittle,
                    TourDescription = tourDetail.TourDescription,
                    TourFrom = parseApiDate(tourDetail.TourFrom), // Now handles DD/MM/YYYY
                    TourTo = parseApiDate(tourDetail.TourTo),
                    TourFromTime = parseApiTime(tourDetail.TourFromTime), // Convert 24h to 12h
                    TourToTime = parseApiTime(tourDetail.TourToTime),
                    RequestAdvance = tourDetail.RequestAdvance,
                    RequestedAdvanceAmount = tourDetail.RequestedAdvanceAmount.toString(),
                    StageSl = tourDetail.StageSl,
                    StageName = tourDetail.Advance_Request,
                    loc_cd = "", // You might need to get this from session
                    staf_sl = "", // You might need to get this from session
                    TourType = "Local" // Add default value
                )

                // Convert travel details to TripDetail list
                val tripDetails = tourDetail.travel_details.map { travel ->
                    TripDetail(
                        sl = travel.sl,
                        TravelDate = parseApiDateForDisplay(travel.dt), // Format: "10/10/2025" to "2025-10-10"
                        TravelFrom = travel.ft,
                        TravelTo = travel.tt,
                        TravelMode = travel.TravelMode,
                        km = travel.TotalKm.trim(),
                        Particular = travel.Particular,
                        NightHalt = travel.NightHalt,
                        TierSl = travel.TierSl,
                        Location = travel.Location,
                        TourId = travel.TourId,
                        ApprovedTotalKM = travel.ApprovedTotalKM.trim(),
                        WorkTime = travel.WorkTime.trim()
                    )
                }

                // Convert expense details to Expense list
                val expenseDetails = tourDetail.expense_details.map { expense ->
                    Expense(
                        Sl = expense.Sl,
                        slno = expense.slno,
                        ExpenseDate = parseApiDateForDisplay(expense.dt),
                        ExpenseSl = expense.ExpenseSl,
                        ExpenseName = expense.ExpenseName,
                        Location = expense.Location,
                        ExpenseFrom = parseApiDateForDisplay(expense.fdt),
                        ExpenseTo = parseApiDateForDisplay(expense.tdt),
                        Amount = expense.Amount.trim().toDoubleOrNull() ?: 0.0,
                        Particular = expense.Particular,
                        BillPath = expense.BillPath,
                        ApprovedAmount = expense.ApprovedAmount.trim()
                    )
                }

                // Update state
                currentTour = tour
                this.tripDetails = tripDetails
                this.expenseDetails = expenseDetails

                Log.d("TourViewModel", "Loaded ${tripDetails.size} trip details")
                Log.d("TourViewModel", "Loaded ${expenseDetails.size} expense details")
            }
        } catch (e: Exception) {
            Log.e("TourViewModel", "Error fetching tour details", e)
        }
        finally {
            isLoadingDetails = false
        }
    }



    // Existing function for Tour dates

    fun fetchLocations(locCd: String, clientUrl: String) {
        viewModelScope.launch {
            try {
                val apiService = getApiService(clientUrl)
                val response = apiService.getLocations(locCd)
                if (response.status == "success") {
                    locations = response.data_value
                }
            } catch (e: Exception) {
                Log.e("TourViewModel", "Error fetching locations", e)
            }
        }
    }


    fun fetchExpenses(staffSl: String, clientUrl: String) {
        viewModelScope.launch {
            try {
                val apiService = getApiService(clientUrl)
                val response = apiService.getExpenses(staffSl)
                if (response.status == "success") {
                    expenses = response.data_value
                }
            } catch (e: Exception) {
                Log.e("TourViewModel", "Error fetching expenses", e)
            }
        }
    }

    private fun buildExpensePartsForCreate(expenseDetails: List<Expense>): List<MultipartBody.Part> {
        val parts = mutableListOf<MultipartBody.Part>()

        expenseDetails.forEachIndexed { index, expense ->
            // Add all expense fields
            parts.add(createFormDataPart("Expenses[$index].ExpenseSl", expense.ExpenseSl.toString()))
            parts.add(createFormDataPart("Expenses[$index].ExpenseDate", expense.ExpenseDate))
            parts.add(createFormDataPart("Expenses[$index].ExpenseFrom", expense.ExpenseFrom))
            parts.add(createFormDataPart("Expenses[$index].ExpenseTo", expense.ExpenseTo))
            parts.add(createFormDataPart("Expenses[$index].Location", expense.Location))
            parts.add(createFormDataPart("Expenses[$index].Amount", expense.Amount.toString()))
            parts.add(createFormDataPart("Expenses[$index].Particular", expense.Particular))
            parts.add(createFormDataPart("Expenses[$index].Sl", expense.Sl.toString()))

            // Handle file upload
            expense.fileUri?.let { uri ->
                try {
                    val context = getApplication<Application>().applicationContext
                    val inputStream = context.contentResolver.openInputStream(uri)
                    inputStream?.use { stream ->
                        // Create a temporary file
                        val file = File.createTempFile("expense_${index}_", ".pdf")
                        file.outputStream().use { fileStream ->
                            stream.copyTo(fileStream)
                        }

                        val requestFile = file.asRequestBody("application/pdf".toMediaTypeOrNull())
                        parts.add(
                            MultipartBody.Part.createFormData(
                                "Expenses[$index].File",
                                file.name,
                                requestFile
                            )
                        )
                        Log.d("TourViewModel", "Added file: ${file.name} for expense $index")
                    }
                } catch (e: Exception) {
                    Log.e("TourViewModel", "Error adding file for expense $index", e)
                }
            }
        }

        return parts
    }




    // -------------------- CREATE / UPDATE --------------------
// -------------------- CREATE TOUR --------------------
    suspend fun createTour(
        tour: Tour,
        tripDetails: List<TripDetail>,
        expenseDetails: List<Expense>,
        clientUrl: String
    ): TourResult {
        loading = true
        return try {
            val validationError = validateExpenses(expenseDetails, expenses)
            if (validationError != null) {
                Log.e("TourViewModel", "Expense validation failed: $validationError")
                return TourResult(false, validationError)
            }

            val apiService = getApiService(clientUrl)

            val travelList = tripDetails.map { trip ->
                """{ 
     "TravelDate": "${trip.TravelDate}", 
     "TravelFrom": "${formatTimeForCurl(trip.TravelFrom)}", 
     "TravelTo": "${formatTimeForCurl(trip.TravelTo)}", 
     "TravelMode": "${trip.TravelMode}", 
     "km": "${trip.km}", 
     "Particular": "${trip.Particular}", 
     "NightHalt": "${trip.NightHalt}", 
     "TierSl": ${trip.TierSl}, 
     "Location": "${trip.Location}" ,
     "travel_type": "${tour.TourType ?: "Local"}"
   }"""
            }

            val jsonContent = """{ 
 "staf_sl": ${tour.staf_sl?.toIntOrNull() ?: 1}, 
 "TourTittle": "${tour.TourTittle}", 
 "TourDescription": "${tour.TourDescription}", 
 "TourFrom": "${tour.TourFrom}", 
 "TourFromTime": "${formatTimeForCurl(tour.TourFromTime)}", 
 "TourTo": "${tour.TourTo}", 
 "TourToTime": "${formatTimeForCurl(tour.TourToTime)}", 
 "RequestAdvance": ${tour.RequestAdvance}, 
 "RequestedAdvanceAmount": ${tour.RequestedAdvanceAmount.toIntOrNull() ?: 0}, 
 "loc_cd": "${tour.loc_cd ?: "1"}", 
 "TourTravel": [${travelList.joinToString(",")}]
}"""

            val tourDataBody = jsonContent.toRequestBody("text/plain".toMediaTypeOrNull())
            val expenseParts = buildExpensePartsForCreate(expenseDetails)
            Log.d("TourViewModel", "Create Tour Response: ${jsonContent}")

            val response = apiService.createTourWithCompleteRequest(tourDataBody, expenseParts)
            Log.d("TourViewModel", "Create Tour Response: ${response.Status} - ${response.Message}")

            if (response.Status == "Success") {
                TourResult(true, response.Message ?: "Tour created successfully!")
            } else {
                TourResult(false, response.Message ?: "Failed to create tour.")
            }

        } catch (e: Exception) {
            Log.e("TourViewModel", "Error creating tour", e)
            TourResult(false, e.localizedMessage ?: "An unexpected error occurred.")
        } finally {
            loading = false
        }
    }


    // -------------------- UPDATE TOUR --------------------
    suspend fun updateTour(
        tour: Tour,
        tripDetails: List<TripDetail>,
        expenseDetails: List<Expense>,
        clientUrl: String,
        context: Context
    ): TourResult {
        return try {
            val api = getApiService(clientUrl)

            // ✅ Build travel list
            val travelList = tripDetails.map { trip ->
                """{
                "TravelDate": "${trip.TravelDate}",
                "TravelFrom": "${trip.TravelFrom}",
                "TravelTo": "${trip.TravelTo}",
                "TravelMode": "${trip.TravelMode}",
                "km": "${trip.km}",
                "Particular": "${trip.Particular}",
                "NightHalt": "${trip.NightHalt}",
                "TierSl": ${trip.TierSl},
                "Location": "${trip.Location}",
                "travel_type": "${tour.TourType ?: "Local"}"
            }"""
            }

            val jsonContent = """{
            "staf_sl": ${tour.staf_sl?.toIntOrNull() ?: 1},
            "TourTittle": "${tour.TourTittle}",
            "TourDescription": "${tour.TourDescription}",
            "TourFrom": "${tour.TourFrom}",
            "TourFromTime": "${formatTimeForApi(tour.TourFromTime)}",
            "TourTo": "${tour.TourTo}",
            "TourToTime": "${formatTimeForApi(tour.TourToTime)}",
            "RequestAdvance": ${tour.RequestAdvance},
            "RequestedAdvanceAmount": ${tour.RequestedAdvanceAmount.toDoubleOrNull() ?: 0.0},
            "loc_cd": "${tour.loc_cd ?: "1"}",
            "TourTravel": [${travelList.joinToString(",")}],
            "TourType": "${tour.TourType}"
        }"""
            Log.d("TourViewModel", "Create Tour Response: ${jsonContent}")

            val tourDataBody = jsonContent.toRequestBody("text/plain".toMediaTypeOrNull())
            val expenseParts = buildExpensePartsForUpdate(expenseDetails)

            val response = api.updateTourWithBody(tour.TourId ?: 0, tourDataBody, expenseParts)
            val resultString = response.string()

            val isSuccess = parseApiResponse(resultString)
            val message = extractMessageFromResponse(resultString)

            // ✅ Return proper TourResult
            if (isSuccess) {
                TourResult(true, message ?: "Tour updated successfully.")
            } else {
                TourResult(false, message ?: "Failed to update tour.")
            }

        } catch (e: HttpException) {
            val errorBody = e.response()?.errorBody()?.string()
            val message = extractMessageFromResponse(errorBody)
            Log.e("TourViewModel", "❌ HTTP Error updating tour", e)
            TourResult(false, message ?: "Server error (${e.code()})")

        } catch (e: Exception) {
            Log.e("TourViewModel", "❌ Unexpected error updating tour", e)
            TourResult(false, "Unexpected error: ${e.message}")
        }
    }




    private fun buildExpensePartsForUpdate(expenseDetails: List<Expense>): List<MultipartBody.Part> {
        val parts = mutableListOf<MultipartBody.Part>()

        expenseDetails.forEachIndexed { index, expense ->
            try {
                Log.d("TourViewModel", "Processing expense $index: ${expense.ExpenseName}")

                // Add expense fields
                parts.add(MultipartBody.Part.createFormData("Expenses[$index].ExpenseSl", expense.ExpenseSl.toString()))
                parts.add(MultipartBody.Part.createFormData("Expenses[$index].ExpenseDate", expense.ExpenseDate))
                parts.add(MultipartBody.Part.createFormData("Expenses[$index].ExpenseFrom", expense.ExpenseFrom))
                parts.add(MultipartBody.Part.createFormData("Expenses[$index].ExpenseTo", expense.ExpenseTo))
                parts.add(MultipartBody.Part.createFormData("Expenses[$index].Location", expense.Location))
                parts.add(MultipartBody.Part.createFormData("Expenses[$index].Amount", expense.Amount.toString()))
                parts.add(MultipartBody.Part.createFormData("Expenses[$index].Particular", expense.Particular))
                parts.add(MultipartBody.Part.createFormData("Expenses[$index].Sl", expense.Sl.toString()))

                // Handle file upload if exists
                expense.fileUri?.let { uri ->
                    Log.d("TourViewModel", "Processing file for expense $index: $uri")

                    try {
                        val context = getApplication<Application>().applicationContext
                        val inputStream = context.contentResolver.openInputStream(uri)
                        if (inputStream == null) {
                            Log.e("TourViewModel", "Could not open input stream for URI: $uri")
                            return@let
                        }

                        inputStream.use { stream ->
                            // Get original file name
                            val fileName = context.contentResolver.getFileName(uri) ?: "expense_$index.pdf"
                            Log.d("TourViewModel", "File name: $fileName")

                            // Create temporary file
                            val file = File.createTempFile("expense_${index}_", ".pdf", context.cacheDir)
                            file.outputStream().use { fileStream ->
                                stream.copyTo(fileStream)
                            }

                            Log.d("TourViewModel", "Temp file created: ${file.absolutePath}, size: ${file.length()} bytes")

                            if (file.exists() && file.length() > 0) {
                                val requestFile = file.asRequestBody("application/pdf".toMediaTypeOrNull())
                                val filePart = MultipartBody.Part.createFormData(
                                    "Expenses[$index].File",
                                    fileName,
                                    requestFile
                                )
                                parts.add(filePart)
                                Log.d("TourViewModel", "✅ Successfully added file part for expense $index")
                            } else {
                                Log.e("TourViewModel", "❌ Temp file is empty or doesn't exist")
                            }
                        }
                    } catch (e: Exception) {
                        Log.e("TourViewModel", "❌ Error processing file for expense $index", e)
                    }
                }
            } catch (e: Exception) {
                Log.e("TourViewModel", "❌ Error creating expense part for index $index", e)
            }
        }

        Log.d("TourViewModel", "Total parts created: ${parts.size}")
        return parts
    }

    private fun createFormDataPart(name: String, value: String): MultipartBody.Part {
        return MultipartBody.Part.createFormData(name, value)
    }

    private fun formatTimeForCurl(time: String): String {
        if (time.isBlank()) return ""

        return try {
            // Convert to "2:02 PM" format exactly like curl
            val inputFormats = listOf(
                "HH:mm:ss",
                "HH:mm",
                "hh:mm a",
                "hh:mm:ss a",
                "h:mm a",
                "h:mm:ss a"
            )

            for (format in inputFormats) {
                try {
                    val inputFormat = SimpleDateFormat(format, Locale.getDefault())
                    val date = inputFormat.parse(time)
                    if (date != null) {
                        val outputFormat = SimpleDateFormat("h:mm a", Locale.getDefault())
                        return outputFormat.format(date)
                    }
                } catch (e: Exception) {
                    continue
                }
            }

            // If no format works, try to convert common patterns
            when {
                // Handle "12:00 AM" -> "12:00 AM" (keep as is)
                time.contains("AM", ignoreCase = true) || time.contains("PM", ignoreCase = true) -> time
                // Handle "14:00" -> "2:00 PM"
                time.contains(":") -> {
                    val parts = time.split(":")
                    if (parts.size >= 2) {
                        val hour = parts[0].toIntOrNull() ?: 0
                        val minute = parts[1].take(2).toIntOrNull() ?: 0

                        val amPm = if (hour >= 12) "PM" else "AM"
                        val displayHour = when {
                            hour == 0 -> 12
                            hour > 12 -> hour - 12
                            else -> hour
                        }
                        return "$displayHour:${minute.toString().padStart(2, '0')} $amPm"
                    }
                    time
                }
                else -> time
            }
        } catch (e: Exception) {
            Log.e("TourViewModel", "Error formatting time: $time", e)
            time
        }
    }






    private fun isValidTime(time: String): Boolean {
        if (time.isBlank()) return true // Allow empty if optional
        return try {
            SimpleDateFormat("HH:mm:ss", Locale.getDefault()).parse(time) != null
        } catch (e: Exception) {
            false
        }
    }






    private fun parseApiResponse(responseBody: String): Boolean {
        return try {
            Log.d("TourViewModel", "Parsing response: $responseBody")

            // Remove any BOM or extra characters
            val cleanResponse = responseBody.trim()

            // Try to parse as JSON
            try {
                val jsonObject = JSONObject(cleanResponse)
                val status = jsonObject.optString("Status", "").lowercase(Locale.getDefault())
                val message = jsonObject.optString("Message", "").lowercase(Locale.getDefault())

                Log.d("TourViewModel", "JSON Response - Status: $status, Message: $message")

                status == "success" || message.contains("success")
            } catch (e: Exception) {
                // If not JSON, check for success string
                cleanResponse.contains("success", ignoreCase = true)
            }
        } catch (e: Exception) {
            Log.e("TourViewModel", "Error parsing response", e)
            false
        }
    }


    private fun extractMessageFromResponse(responseBody: String?): String? {
        if (responseBody.isNullOrBlank()) return null
        return try {
            val json = JSONObject(responseBody.trim())
            json.optString("Message", json.optString("Status", null))
        } catch (e: Exception) {
            // Fallback: just return raw text
            responseBody.takeIf { it.isNotBlank() }
        }
    }


    private fun formatTimeForApi(time: String): String {
        if (time.isBlank()) return ""

        return try {
            // Convert from "10:00:00" to "10:00 AM" format
            val inputFormats = listOf(
                "HH:mm:ss",
                "HH:mm",
                "hh:mm a",
                "hh:mm:ss a"
            )

            for (format in inputFormats) {
                try {
                    val inputFormat = SimpleDateFormat(format, Locale.getDefault())
                    val date = inputFormat.parse(time)
                    if (date != null) {
                        val outputFormat = SimpleDateFormat("h:mm a", Locale.getDefault())
                        return outputFormat.format(date)
                    }
                } catch (e: Exception) {
                    continue
                }
            }

            // If no format works, return as-is
            time
        } catch (e: Exception) {
            Log.e("TourViewModel", "Error formatting time: $time", e)
            time
        }
    }



    private fun formatDateForApi(date: String): String {
        return try {
            val inputFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val outputFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val parsedDate = inputFormat.parse(date)
            parsedDate?.let { outputFormat.format(it) } ?: date
        } catch (e: Exception) {
            Log.e("TourViewModel", "Error formatting date: $date", e)
            date
        }
    }

    // Extension function to get file name from URI
    fun ContentResolver.getFileName(uri: Uri): String? {
        var fileName: String? = null
        try {
            // First try to get the file name from the content resolver
            query(uri, null, null, null, null)?.use { cursor ->
                if (cursor.moveToFirst()) {
                    val nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                    if (nameIndex != -1) {
                        fileName = cursor.getString(nameIndex)
                    }
                }
            }

            // If still null, try to extract from URI path
            if (fileName.isNullOrEmpty()) {
                uri.path?.let { path ->
                    fileName = path.substringAfterLast('/')
                }
            }
        } catch (e: Exception) {
            Log.e("ContentResolver", "Error getting file name from URI: $uri", e)
        }
        return fileName
    }


    // -------------------- STATE MANAGEMENT --------------------


    fun updateCurrentTour(tour: Tour?) {
        currentTour = tour
    }

    fun updateTripDetails(details: List<TripDetail>) {
        tripDetails = details
    }

    fun updateExpenseDetails(details: List<Expense>) {
        expenseDetails = details
    }

    fun clearCurrentTour() {
        currentTour = null
        tripDetails = emptyList()
        expenseDetails = emptyList()
    }


}

// ---------------- API MODELS ----------------
data class CompanyRequest(val compcode: String)
data class CompanyResponse(val status: String, val data_value: List<ClientData>)
data class ClientData(val client_url: String)

data class TourData(
    val staf_sl: Int,
    val TourId: Int? = null,
    val TourTittle: String,
    val TourDescription: String,
    val TourFrom: String,
    val TourFromTime: String,
    val TourTo: String,
    val TourToTime: String,
    val RequestAdvance: Int,
    val RequestedAdvanceAmount: Int,
    val loc_cd: String,
    val TourTravel: List<TravelData>
)

data class TravelData(
    val TravelDate: String,
    val TravelFrom: String,
    val TravelTo: String,
    val TravelMode: String,
    val km: String,
    val Particular: String,
    val NightHalt: String,
    val TierSl: Int,
    val Location: String
)

data class ExpenseData(
    val ExpenseSl: Int,
    val ExpenseDate: String,
    val ExpenseFrom: String?,
    val ExpenseTo: String?,
    val Location: String?,
    val Amount: Double,
    val Particular: String?,
    val Sl: Int
)


data class LoginRequest(val userid: String, val password: String,val imei:String)
data class LoginResponse(
    val status: String,
    val Code: String,
    val msg: String,
    val data: List<StaffData>? = null // Make data nullable and use proper field name
)

data class UserData(val staf_nm: String, val emp_code: String)

// ---------------- API SERVICE ----------------
interface ApiService {
    @POST("ptadmin/api/validcode")
    suspend fun validateCompany(@Body req: CompanyRequest): CompanyResponse

    @POST("api/loginimei")
    suspend fun login(@Body req: LoginRequest): LoginResponse
}
// ---------------- TOUR API SERVICE ----------------
// ---------------- TOUR API SERVICE ----------------
interface TourApiService {
    @GET("api/staftourlist")
    suspend fun getTourList(
        @Query("staf_sl") staffSl: String
    ): TourListResponse

    @GET("api/GetTourDetails")
    suspend fun getTourDetails(
        @Query("TourId") tourId: Int
    ): TourDetailResponse

    // FIXED: Change from POST to GET with Query parameters
    @GET("api/tourlocationlist")
    suspend fun getLocations(@Query("loc_cd") locCd: String): LocationResponse


    @GET("api/tourexpenselist")
    suspend fun getExpenses(@Query("staf_sl") stafSl: String): ExpenseResponse

    @Multipart
    @POST("api/AddTourDetails")
    suspend fun createTourWithCompleteRequest(
        @Part("TourData") tourData: RequestBody,
        @Part expenses: List<MultipartBody.Part>
    ): ApiResponse

    // ✅ Edit Tour API
    @Multipart
    @POST("api/EditTourDetails")
    suspend fun updateTourWithBody(
        @Query("TourId") tourId: Int, // This matches curl: ?TourId=62
        @Part("TourData") tourData: RequestBody,
        @Part expenses: List<MultipartBody.Part>
    ): ResponseBody

}


class MainActivity : ComponentActivity() {

    // Add this at class level to store device ID
    private var deviceIdentifier: String = ""

    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
            if (granted) readDeviceIdentifier()
            else {
                Toast.makeText(this, "READ_PHONE_STATE denied", Toast.LENGTH_SHORT).show()
                // Use ANDROID_ID as fallback even without permission
                deviceIdentifier = getAndroidId()
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)

        // ✅ Request or read IMEI (safe)
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE)
            != PackageManager.PERMISSION_GRANTED
        ) {
            requestPermissionLauncher.launch(Manifest.permission.READ_PHONE_STATE)
        } else {
            readDeviceIdentifier()
        }

        setContent {
            TronTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    AppNavigation()
                }
            }
        }
    }

    // ---- Function to read IMEI or fallback to ANDROID_ID ----
    private fun readDeviceIdentifier() {
        try {
            val tm = getSystemService(TELEPHONY_SERVICE) as TelephonyManager

            val imei: String? = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                try {
                    tm.imei
                } catch (e: SecurityException) {
                    null
                }
            } else {
                @Suppress("DEPRECATION")
                tm.deviceId
            }

            deviceIdentifier = if (!imei.isNullOrEmpty()) imei else getAndroidId()

            Log.d("DeviceIdentifier", "Device ID / IMEI: $deviceIdentifier")
            // Store in shared preferences for easy access
            getSharedPreferences("device_prefs", Context.MODE_PRIVATE)
                .edit()
                .putString("device_id", deviceIdentifier)
                .apply()

        } catch (e: Exception) {
            Log.e("DeviceIdentifier", "Error reading IMEI: ${e.message}")
            deviceIdentifier = getAndroidId()
        }
    }

    // ---- Get ANDROID_ID as fallback ----
    private fun getAndroidId(): String {
        return try {
            Settings.Secure.getString(contentResolver, Settings.Secure.ANDROID_ID)
        } catch (e: Exception) {
            // Final fallback - generate random ID
            "android_${System.currentTimeMillis()}"
        }
    }

    // ---- Function to get device identifier for login ----
    fun getDeviceIdentifier(): String {
        return deviceIdentifier.ifEmpty {
            getSharedPreferences("device_prefs", Context.MODE_PRIVATE)
                .getString("device_id", "") ?: getAndroidId()
        }
    }
}

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val vm: SessionViewModel = viewModel()
    val loggedIn by vm.isLoggedIn.collectAsState()

    LaunchedEffect(loggedIn) {
        navController.navigate(if (loggedIn) "dashboard" else "server") {
            popUpTo(0) { inclusive = true }
        }
    }

    NavHost(
        navController = navController,
        startDestination = "server",
        modifier = Modifier.fillMaxSize()
    ) {
        composable("server") {
            ServerSetupScreen(
                onSuccess = { navController.navigate("login") },
                modifier = Modifier.fillMaxSize()
            )
        }
        composable("login") {
            LoginScreen(
                onLogin = {
                    navController.navigate("dashboard") {
                        popUpTo("login") { inclusive = true }
                    }
                },
                onChangeServer = { navController.navigate("server") },
                modifier = Modifier.fillMaxSize()
            )
        }
        composable("dashboard") {
            DashboardScreen(
                onLogout = {
                    navController.navigate("server") {
                        popUpTo("dashboard") { inclusive = true }
                    }
                },
                onNavigateToTour = {
                    navController.navigate("tour")
                },
                modifier = Modifier.fillMaxSize()
            )
        }
        // ADD TOUR SCREEN DESTINATION
        composable("tour") {
            TourScreen(
                onBack = { navController.popBackStack() },
                modifier = Modifier.fillMaxSize()
            )
        }
    }
}

// ---------------- TOUR SCREEN ----------------
@OptIn(ExperimentalMaterial3Api::class, ExperimentalAnimationApi::class)
@Composable
fun TourScreen(
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    tourViewModel: TourViewModel = viewModel(),
    sessionViewModel: SessionViewModel = viewModel()
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    val staffSl by sessionViewModel.staffSl.collectAsState()
    val clientUrl by sessionViewModel.clientUrl.collectAsState()

    var showTourList by remember { mutableStateOf(true) }
    var editMode by remember { mutableStateOf(false) }
    var showLoadingOverlay by remember { mutableStateOf(false) }
    var clickedTourId by remember { mutableStateOf<Int?>(null) }

    // Load tour list when showing list
    LaunchedEffect(showTourList) {
        if (showTourList) {
            staffSl?.let { sl ->
                clientUrl?.let { url ->
                    tourViewModel.fetchTourList(sl, url)
                }
            }
        }
    }

    // Handle tour selection and loading - FIXED VERSION
    LaunchedEffect(tourViewModel.selectedTour) {
        val selectedTour = tourViewModel.selectedTour
        if (selectedTour != null && clientUrl != null) {
            try {
                // Show loading immediately when tour is selected
                showLoadingOverlay = true
                clickedTourId = selectedTour.TourId

                // Switch to form screen
                editMode = true
                showTourList = false

                // Fetch tour details
                tourViewModel.fetchTourDetails(selectedTour.TourId!!, clientUrl!!)
            } catch (e: Exception) {
                e.printStackTrace()
                showLoadingOverlay = false
                clickedTourId = null
                Toast.makeText(context, "Failed to load tour details", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // Observe when details are loaded to hide overlay - SIMPLIFIED
    LaunchedEffect(tourViewModel.currentTour, tourViewModel.tripDetails) {
        if (showLoadingOverlay) {
            // Hide loading when we have both tour and trip details
            if (tourViewModel.currentTour != null && tourViewModel.tripDetails.isNotEmpty()) {
                // Small delay for smooth transition
                delay(300)
                showLoadingOverlay = false
                clickedTourId = null
            }
        }
    }

    // Fallback timeout to prevent infinite loading
    LaunchedEffect(showLoadingOverlay) {
        if (showLoadingOverlay) {
            delay(10000) // 10 seconds timeout
            if (showLoadingOverlay) {
                showLoadingOverlay = false
                clickedTourId = null
                Toast.makeText(context, "Loading timeout", Toast.LENGTH_SHORT).show()
            }
        }
    }

    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = when {
                            showTourList -> "My Tours"
                            editMode -> "Edit Tour"
                            else -> "Create Tour"
                        }
                    )
                },
                navigationIcon = {
                    IconButton(onClick = {
                        if (showTourList) {
                            onBack()
                        } else {
                            showTourList = true
                            editMode = false
                            showLoadingOverlay = false
                            clickedTourId = null
                            tourViewModel.clearSelectedTour()
                        }
                    }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
        floatingActionButton = {
            if (showTourList) {
                FloatingActionButton(
                    onClick = {
                        showTourList = false
                        editMode = false
                        showLoadingOverlay = false
                        clickedTourId = null
                        tourViewModel.clearSelectedTour()
                    },
                    containerColor = MaterialTheme.colorScheme.primary,
                    shape = CircleShape
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Create New Tour", tint = Color.White)
                }
            }
        },
        floatingActionButtonPosition = FabPosition.End,
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            Crossfade(
                targetState = showTourList,
                modifier = Modifier.fillMaxSize()
            ) { isListVisible ->
                if (isListVisible) {
                    TourListScreen(
                        tourList = tourViewModel.tourList,
                        loading = tourViewModel.loading,
                        clickedTourId = clickedTourId,
                        onRefresh = {
                            staffSl?.let { sl ->
                                clientUrl?.let { url ->
                                    scope.launch {
                                        tourViewModel.fetchTourList(sl, url)
                                    }
                                }
                            }
                        },
                        onTourClick = { tour ->
                            // Set selected tour - this will trigger the LaunchedEffect
                            tourViewModel.selectTour(tour)
                        }
                    )
                } else {
                    // For edit mode, check if we have data
                    if (editMode && tourViewModel.currentTour == null && showLoadingOverlay) {
                        // Still loading, show centered progress
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator()
                        }
                    } else {
                        TourFormScreen(
                            editMode = editMode,
                            currentTour = tourViewModel.currentTour,
                            initialTripDetails = tourViewModel.tripDetails,
                            initialExpenseDetails = tourViewModel.expenseDetails,
                            locations = tourViewModel.locations,
                            expenses = tourViewModel.expenses,
                            onSaveTour = { tour, tripDetails, expenseDetails ->
                                scope.launch {
                                    try {
                                        clientUrl?.let { url ->
                                            val result = if (editMode) {
                                                tourViewModel.updateTour(tour, tripDetails, expenseDetails, url, context)
                                            } else {
                                                tourViewModel.createTour(tour, tripDetails, expenseDetails, url)
                                            }

                                            Toast.makeText(context, result.message, Toast.LENGTH_SHORT).show()

                                            if (result.isSuccess) {
                                                showTourList = true
                                                editMode = false
                                                showLoadingOverlay = false
                                                clickedTourId = null
                                                tourViewModel.clearSelectedTour()

                                                // Refresh the list
                                                staffSl?.let { sl ->
                                                    tourViewModel.fetchTourList(sl, url)
                                                }
                                            }
                                        }
                                    } catch (e: Exception) {
                                        e.printStackTrace()
                                        Toast.makeText(context, "Failed to save tour", Toast.LENGTH_SHORT).show()
                                    }
                                }
                            }
                        )
                    }
                }
            }

            // Loading Overlay - Show during API call
            if (showLoadingOverlay) {
                LoadingOverlay()
            }
        }
    }
}

// ---------------- SIMPLER LOADING OVERLAY ----------------
@Composable
fun LoadingOverlay(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.5f)),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            CircularProgressIndicator(
                modifier = Modifier.size(48.dp),
                color = Color.White,
                strokeWidth = 3.dp
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Loading Tour Details...",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.White
            ) // <-- Added closing parenthesis here
        }
    }
}

// ---------------- SIMPLIFIED TOUR LIST SCREEN ----------------
@Composable
fun TourListScreen(
    tourList: List<Tour>,
    loading: Boolean,
    clickedTourId: Int?,
    onRefresh: () -> Unit,
    onTourClick: (Tour) -> Unit,
    modifier: Modifier = Modifier
) {
    if (loading && tourList.isEmpty()) {
        Box(
            modifier = modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
        }
    } else if (tourList.isEmpty()) {
        EmptyTourState(modifier = modifier.fillMaxSize())
    } else {
        LazyColumn(
            modifier = modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(tourList) { tour ->
                TourListItem(
                    tour = tour,
                    isLoading = clickedTourId == tour.TourId,
                    onClick = { onTourClick(tour) },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}

// ---------------- SIMPLIFIED TOUR LIST ITEM ----------------
@Composable
fun TourListItem(
    tour: Tour,
    isLoading: Boolean = false,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        onClick = onClick,
        modifier = modifier.shadow(4.dp, shape = MaterialTheme.shapes.medium),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        enabled = !isLoading
    ) {
        Box {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = tour.TourTittle ?: "Untitled Tour",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.weight(1f)
                    )

                    if (!isLoading) {
                        FilterChip(
                            selected = false,
                            onClick = { },
                            label = {
                                Text(
                                    tour.StageName ?: "Draft",
                                    style = MaterialTheme.typography.labelSmall
                                )
                            },
                            colors = FilterChipDefaults.filterChipColors(
                                containerColor = when (tour.StageSl) {
                                    1 -> Color(0xFFE3F2FD)
                                    2 -> Color(0xFFFFF3E0)
                                    3 -> Color(0xFFE8F5E8)
                                    4 -> Color(0xFFFFEBEE)
                                    else -> MaterialTheme.colorScheme.surfaceVariant
                                },
                                labelColor = when (tour.StageSl) {
                                    1 -> Color(0xFF2196F3)
                                    2 -> Color(0xFFFF9800)
                                    3 -> Color(0xFF4CAF50)
                                    4 -> Color(0xFFF44336)
                                    else -> MaterialTheme.colorScheme.onSurface
                                }
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = tour.TourDescription ?: "No description",
                    style = MaterialTheme.typography.bodyMedium,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "From: ${tour.TourFrom ?: "N/A"}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "To: ${tour.TourTo ?: "N/A"}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                if (tour.RequestAdvance == 1) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Advance: ₹${tour.RequestedAdvanceAmount ?: 0}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }

            // Loading indicator overlay
            if (isLoading) {
                Box(
                    modifier = Modifier
                        .matchParentSize()
                        .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.7f)),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        strokeWidth = 2.dp,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }
    }
}

// ---------------- EMPTY TOUR STATE ----------------
@Composable
fun EmptyTourState(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Default.Tour,
            contentDescription = "No Tours",
            modifier = Modifier.size(100.dp), // Larger icon
            tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "No Tours Yet",
            style = typography.headlineMedium, // Larger text
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Tap the + button to create one",
            style = typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
        )
    }
}



// ---------------- IMPROVED EXPENSE ITEM ----------------
@Composable
fun ExpenseItem(
    expense: Expense,
    index: Int,
    onEdit: (Expense) -> Unit,
    onFileSelect: (Int) -> Unit, // Add this parameter
    onRemove: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Expense ${index + 1}",
                    style = typography.bodyMedium,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.primary
                )

                Text(
                    text = "₹${expense.Amount}",
                    style = typography.bodyMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Expense Details
            if (expense.ExpenseName.isNotEmpty()) {
                Text(
                    text = expense.ExpenseName,
                    style = typography.bodyMedium,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            if (expense.Particular.isNotEmpty()) {
                Text(
                    text = expense.Particular,
                    style = typography.bodySmall,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                if (expense.ExpenseDate.isNotEmpty()) {
                    Text(
                        text = "Date: ${expense.ExpenseDate}",
                        style = typography.bodySmall
                    )
                }

                if (expense.Location.isNotEmpty()) {
                    Text(
                        text = "Location: ${expense.Location}",
                        style = typography.bodySmall
                    )
                }
            }

            if (expense.ExpenseFrom.isNotEmpty() || expense.ExpenseTo.isNotEmpty()) {
                Text(
                    text = "${expense.ExpenseFrom} - ${expense.ExpenseTo}",
                    style = typography.bodySmall,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            // Add File Attachment Section
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Button(
                    onClick = { onFileSelect(index) },
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(Icons.Default.AttachFile, contentDescription = "Attach File", modifier = Modifier.size(16.dp))
                    Spacer(Modifier.width(4.dp))
                    Text("Attach Bill")
                }

                // Bill attachment indicator
                if (expense.fileUri != null || expense.BillFileBase64.isNotEmpty() || expense.BillPath.isNotEmpty()) {
                    Text(
                        text = "📎 Bill Attached",
                        style = typography.bodySmall,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                IconButton(
                    onClick = { onEdit(expense) },
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        Icons.Default.Edit,
                        contentDescription = "Edit",
                        modifier = Modifier.size(16.dp)
                    )
                }
                IconButton(
                    onClick = onRemove,
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        Icons.Default.Delete,
                        contentDescription = "Remove",
                        modifier = Modifier.size(16.dp),
                        tint = MaterialTheme.colorScheme.error
                    )
                }
            }
        }
    }
}

// ---------------- TOUR FORM SCREEN ----------------
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TourFormScreen(
    editMode: Boolean,
    currentTour: Tour?,
    initialTripDetails: List<TripDetail>,
    initialExpenseDetails: List<Expense>,
    locations: List<LocationItem>,
    expenses: List<ExpenseItem>,
    onSaveTour: (Tour, List<TripDetail>, List<Expense>) -> Unit,
    modifier: Modifier = Modifier,
    sessionViewModel: SessionViewModel = viewModel(),
    tourViewModel: TourViewModel = viewModel()
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val staffSl by sessionViewModel.staffSl.collectAsState()
    val locCd by sessionViewModel.locCd.collectAsState()


    val clientUrl by sessionViewModel.clientUrl.collectAsState() // Add clientUrl collection

    var tourTitle by remember { mutableStateOf(currentTour?.TourTittle ?: "") }
    var tourDescription by remember { mutableStateOf(currentTour?.TourDescription ?: "") }
    var fromDate by remember { mutableStateOf(parseApiDate(currentTour?.TourFrom ?: "")) }
    var toDate by remember { mutableStateOf(parseApiDate(currentTour?.TourTo ?: "")) }
    var fromTime by remember { mutableStateOf(currentTour?.TourFromTime ?: "") }
    var toTime by remember { mutableStateOf(currentTour?.TourToTime ?: "") }
    var requestAdvance by remember { mutableStateOf(currentTour?.RequestAdvance == 1) }
    var advanceAmount by remember { mutableStateOf(currentTour?.RequestedAdvanceAmount ?: "") }
    var tripDetails by remember { mutableStateOf(initialTripDetails) }
    var expenseDetails by remember { mutableStateOf(initialExpenseDetails) }
    var editingTripIndex by remember { mutableStateOf<Int?>(null) }
    var editingExpenseIndex by remember { mutableStateOf<Int?>(null) }
    var dateField by remember { mutableStateOf("") }
    var showDatePicker by remember { mutableStateOf(false) }
    var timeField by remember { mutableStateOf("") }
    var showTimePicker by remember { mutableStateOf(false) }
    var tourType by rememberSaveable { mutableStateOf(currentTour?.TourType ?: "Local") }
    var expanded by remember { mutableStateOf(false) }
    val tourTypeOptions = listOf("Local", "Outside")



    val filePickerLauncher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        editingExpenseIndex?.let { index ->
            val updatedList = expenseDetails.toMutableList()
            if (index in updatedList.indices) {
                updatedList[index] = updatedList[index].copy(fileUri = uri)
                expenseDetails = updatedList
            }
            editingExpenseIndex = null
        }
    }

    LaunchedEffect(currentTour) {
        tourTitle = currentTour?.TourTittle ?: ""
        tourDescription = currentTour?.TourDescription ?: ""
        fromDate = parseApiDate(currentTour?.TourFrom ?: "")
        toDate = parseApiDate(currentTour?.TourTo ?: "")
        fromTime = currentTour?.TourFromTime ?: ""
        toTime = currentTour?.TourToTime ?: ""
        requestAdvance = currentTour?.RequestAdvance == 1
        advanceAmount = currentTour?.RequestedAdvanceAmount ?: ""
        tourType = currentTour?.TourType ?: "Local" // ADD THIS LINE
    }

    LaunchedEffect(Unit) {
        staffSl?.let { sl ->
            locCd?.let { loc ->
                tourViewModel.fetchLocations(loc, clientUrl  ?: "")
                tourViewModel.fetchExpenses(sl, clientUrl ?: "")
            }
        }
    }

    LaunchedEffect(initialTripDetails) {
        Log.d("TourFormScreen", "Updating tripDetails with size: ${initialTripDetails.size}")
        tripDetails = initialTripDetails
    }

    LaunchedEffect(initialExpenseDetails) {
        Log.d("TourFormScreen", "Updating expenseDetails with size: ${initialExpenseDetails.size}")
        expenseDetails = initialExpenseDetails
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Tour Details Section
        Card(
            modifier = Modifier.fillMaxWidth(),
            elevation = CardDefaults.cardElevation(2.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(
                    text = "Basic Information",
                    style = typography.titleMedium,
                    fontWeight = FontWeight.SemiBold // Modern bold
                )
                OutlinedTextField(
                    value = tourTitle,
                    onValueChange = { tourTitle = it },
                    label = { Text("Tour Title") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = tourDescription,
                    onValueChange = { tourDescription = it },
                    label = { Text("Tour Description") },
                    modifier = Modifier.fillMaxWidth()
                )
                Box {
                    OutlinedTextField(
                        value = fromDate,
                        onValueChange = { },
                        label = { Text("From Date") },
                        modifier = Modifier
                            .fillMaxWidth(),
                        readOnly = true,
                        trailingIcon = {
                            Icon(
                                imageVector = Icons.Default.DateRange,
                                contentDescription = "Select From Date",
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                    )
                    // Overlay clickable layer
                    Box(
                        modifier = Modifier
                            .matchParentSize()
                            .clickable {
                                dateField = "fromDate"
                                showDatePicker = true
                            }
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Box {
                    OutlinedTextField(
                        value = toDate,
                        onValueChange = { },
                        label = { Text("To Date") },
                        modifier = Modifier.fillMaxWidth(),
                        readOnly = true,
                        trailingIcon = {
                            Icon(
                                imageVector = Icons.Default.DateRange,
                                contentDescription = "Select To Date",
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                    )
                    Box(
                        modifier = Modifier
                            .matchParentSize()
                            .clickable {
                                dateField = "toDate"
                                showDatePicker = true
                            }
                    )
                }

                Box {
                    OutlinedTextField(
                        value = fromTime,
                        onValueChange = {},
                        label = { Text("From Time") },
                        modifier = Modifier.fillMaxWidth(),
                        readOnly = true,
                        trailingIcon = {
                            Icon(
                                imageVector = Icons.Default.AccessTime,
                                contentDescription = "Select From Time",
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                    )
                    Box(
                        modifier = Modifier
                            .matchParentSize()
                            .clickable {
                                timeField = "fromTime"
                                showTimePicker = true
                            }
                    )
                }

                // ✅ To Time (clickable)
                Box {
                    OutlinedTextField(
                        value = toTime,
                        onValueChange = {},
                        label = { Text("To Time") },
                        modifier = Modifier.fillMaxWidth(),
                        readOnly = true,
                        trailingIcon = {
                            Icon(
                                imageVector = Icons.Default.AccessTime,
                                contentDescription = "Select To Time",
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                    )
                    Box(
                        modifier = Modifier
                            .matchParentSize()
                            .clickable {
                                timeField = "toTime"
                                showTimePicker = true
                            }
                    )
                }
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Checkbox(
                        checked = requestAdvance,
                        onCheckedChange = { requestAdvance = it }
                    )
                    Text("Request Advance")
                }
                if (requestAdvance) {
                    OutlinedTextField(
                        value = advanceAmount,
                        onValueChange = { advanceAmount = it },
                        label = { Text("Advance Amount") },
                        modifier = Modifier.fillMaxWidth(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                    )
                }



            }
        }

        // Trip Details Section
        Card(
            modifier = Modifier.fillMaxWidth(),
            elevation = CardDefaults.cardElevation(2.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Trip Details",
                        style = typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = "${tripDetails.size} added",
                        style = typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )
                }
                if (tripDetails.isEmpty()) {
                    Text(
                        text = "No trip details added",
                        style = typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center
                    )
                } else {
                    tripDetails.forEachIndexed { index, detail ->
                        TripDetailItem(
                            detail = detail,
                            index = index,
                            onEdit = { editingTripIndex = index },
                            onRemove = {
                                tripDetails = tripDetails.toMutableList().apply { removeAt(index) }
                            }
                        )
                    }
                }
                Button(
                    onClick = {
                        tripDetails = tripDetails + TripDetail(
                            TravelDate = fromDate.ifEmpty { getCurrentDate() },
                            TravelFrom = "",
                            TravelTo = "",
                            TravelMode = "",
                            km = "",
                            Particular = "",
                            NightHalt = "no",
                            TierSl = 1,
                            Location = locations.firstOrNull()?.LocationName ?: ""
                        )
                    },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = fromDate.isNotEmpty(),
                    shape = MaterialTheme.shapes.medium
                ) {
                    Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(Modifier.width(8.dp))
                    Text("Add Trip Detail")
                }
            }
        }

        // Expenses Section
        Card(
            modifier = Modifier.fillMaxWidth(),
            elevation = CardDefaults.cardElevation(2.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Expenses",
                        style = typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = "${expenseDetails.size} added",
                        style = typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )
                }
                if (expenseDetails.isEmpty()) {
                    Text(
                        text = "No expenses added",
                        style = typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center
                    )
                } else {
                    expenseDetails.forEachIndexed { index, expense ->
                        ExpenseItem(
                            expense = expense,
                            index = index,
                            onEdit = { editingExpenseIndex = index },
                            onFileSelect = {
                                editingExpenseIndex = index
                                filePickerLauncher.launch("application/pdf")
                            },
                            onRemove = {
                                expenseDetails = expenseDetails.toMutableList().apply { removeAt(index) }
                            }
                        )
                    }
                }
                Button(
                    onClick = {
                        expenseDetails = expenseDetails + Expense(
                            ExpenseDate = fromDate.ifEmpty { getCurrentDate() },
                            ExpenseSl = expenses.firstOrNull()?.ExpenseSl ?: 1,
                            Location = "",
                            ExpenseFrom = "",
                            ExpenseTo = "",
                            Amount = 0.0,
                            Particular = "",
                            BillFileBase64 = "",
                            FileName = "",
                            MimeType = "",
                            ExpenseName = expenses.firstOrNull()?.ExpenseName ?: "",
                            Sl = 0
                        )
                    },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = fromDate.isNotEmpty(),
                    shape = MaterialTheme.shapes.medium
                ) {
                    Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(Modifier.width(8.dp))
                    Text("Add Expense")
                }
            }
        }

        Button(
            onClick = {
                if (tourTitle.isNotEmpty() && fromDate.isNotEmpty() && toDate.isNotEmpty()) {

                    val validationError = tourViewModel.validateExpenses(expenseDetails, expenses)
                    if (validationError != null) {
                        Toast.makeText(context, validationError, Toast.LENGTH_LONG).show()
                        return@Button
                    }
                    val tour = Tour(
                        TourId = currentTour?.TourId,
                        TourTittle = tourTitle,
                        TourDescription = tourDescription,
                        TourFrom = fromDate,
                        TourTo = toDate,
                        TourFromTime = fromTime,
                        TourToTime = toTime,
                        RequestAdvance = if (requestAdvance) 1 else 0,
                        RequestedAdvanceAmount = advanceAmount,
                        StageSl = currentTour?.StageSl,
                        StageName = currentTour?.StageName,
                        staf_sl = staffSl ?: "1",
                        loc_cd = locCd ?: "1",
                        TourType = tourType // ADD THIS LINE
                    )
                    onSaveTour(tour, tripDetails, expenseDetails)
                } else {
                    Toast.makeText(context, "Please fill all required fields", Toast.LENGTH_SHORT).show()
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            enabled = tourTitle.isNotEmpty() && fromDate.isNotEmpty() && toDate.isNotEmpty(),
            shape = MaterialTheme.shapes.large // Rounded modern button
        ) {
            Text(if (editMode) "Update Tour" else "Create Tour", style = typography.labelLarge)
        }
    }

    if (showDatePicker) {
        val initDate = if (dateField == "fromDate") fromDate else toDate
        DatePickerDialog(
            initialDate = initDate,
            onDismissRequest = { showDatePicker = false },
            onDateSelected = { date ->
                when (dateField) {
                    "fromDate" -> fromDate = date
                    "toDate" -> toDate = date
                }
                showDatePicker = false
            }
        )
    }

    if (showTimePicker) {
        TimePickerDialog(
            onDismissRequest = { showTimePicker = false },
            onTimeSelected = { selectedTime ->
                when (timeField) {
                    "fromTime" -> fromTime = selectedTime
                    "toTime" -> toTime = selectedTime
                }
                showTimePicker = false
            }
        )
    }




    editingTripIndex?.let { index ->
        val tripDetail = tripDetails.getOrNull(index)
        if (tripDetail != null) {
            TripDetailEditDialog(
                tripDetail = tripDetail,
                currentTour = tourViewModel.currentTour,
                locations = locations,
                onSave = { updatedDetail ->
                    val updatedList = tripDetails.toMutableList()
                    if (index in updatedList.indices) {
                        updatedList[index] = updatedDetail
                        tripDetails = updatedList
                    } else {
                        Log.e("TourFormScreen", "Invalid trip index: $index")
                    }
                    editingTripIndex = null
                },
                onDismiss = { editingTripIndex = null }
            )
        } else {
            Log.e("TourFormScreen", "Trip detail not found at index: $index")
            editingTripIndex = null
        }
    }

    // In TourFormScreen, update the ExpenseEditDialog call:
    editingExpenseIndex?.let { index ->
        val expense = expenseDetails.getOrNull(index)
        if (expense != null) {
            ExpenseEditDialog(
                expense = expense,
                expenseTypes = expenses,
                locations = locations, // ADD THIS LINE
                onSave = { updatedExpense ->
                    val updatedList = expenseDetails.toMutableList()
                    if (index in updatedList.indices) {
                        updatedList[index] = updatedExpense
                        expenseDetails = updatedList
                    }
                    editingExpenseIndex = null
                },
                onDismiss = { editingExpenseIndex = null }
            )
        }
    }
}

fun ExpenseEditDialog(
    expense: Expense,
    expenseTypes: List<ExpenseItem>,
    onSave: (Expense) -> Unit,
    onDismiss: () -> Unit
) {}

// Date Picker Dialog
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DatePickerDialog(
    initialDate: String? = null,
    onDismissRequest: () -> Unit,
    onDateSelected: (String) -> Unit
) {
    val initialMillis = remember(initialDate) {
        initialDate?.let {
            try {
                SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).parse(it)?.time
            } catch (e: Exception) { null }
        }
    }

    val datePickerState = rememberDatePickerState(initialSelectedDateMillis = initialMillis)

    androidx.compose.material3.DatePickerDialog(
        onDismissRequest = onDismissRequest,
        confirmButton = {
            TextButton(onClick = {
                val selectedDate = datePickerState.selectedDateMillis?.let {
                    SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date(it))
                }
                selectedDate?.let { onDateSelected(it) }
                onDismissRequest()
            }) { Text("OK") }
        },
        dismissButton = {
            TextButton(onClick = onDismissRequest) { Text("Cancel") }
        }
    ) {
        androidx.compose.material3.DatePicker(state = datePickerState)
    }
}


// Time Picker Dialog
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TimePickerDialog(
    onDismissRequest: () -> Unit,
    onTimeSelected: (String) -> Unit
) {
    var initialHour by remember { mutableStateOf(0) }
    var initialMinute by remember { mutableStateOf(0) }

    // Parse current time if needed
    LaunchedEffect(Unit) {
        // You can set initial values here if needed
    }

    val state = rememberTimePickerState(
        initialHour = initialHour,
        initialMinute = initialMinute,
        is24Hour = false // Force 12-hour format for consistency
    )

    AlertDialog(
        onDismissRequest = onDismissRequest,
        title = { Text("Select Time") },
        text = {
            TimePicker(
                state = state,
                modifier = Modifier.height(300.dp)
            )
        },
        confirmButton = {
            TextButton(onClick = {
                val hour = state.hour
                val minute = state.minute
                val amPm = if (hour < 12) "AM" else "PM"
                val displayHour = when {
                    hour == 0 -> 12
                    hour > 12 -> hour - 12
                    else -> hour
                }
                val formatted = String.format("%d:%02d %s", displayHour, minute, amPm)
                onTimeSelected(formatted)
                onDismissRequest()
            }) { Text("OK") }
        },
        dismissButton = {
            TextButton(onClick = onDismissRequest) { Text("Cancel") }
        }
    )
}



// Trip Detail Edit Dialog - UPDATED VERSION
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TripDetailEditDialog(
    tripDetail: TripDetail,
    currentTour: Tour?,
    locations: List<LocationItem>,
    onSave: (TripDetail) -> Unit,
    onDismiss: () -> Unit
) {
    var travelDate by remember { mutableStateOf(tripDetail.TravelDate) }
    var travelFrom by remember { mutableStateOf(tripDetail.TravelFrom) }
    var travelTo by remember { mutableStateOf(tripDetail.TravelTo) }
    var travelMode by remember { mutableStateOf(tripDetail.TravelMode) }
    var km by remember { mutableStateOf(tripDetail.km) }
    var particular by remember { mutableStateOf(tripDetail.Particular) }
    var nightHalt by remember { mutableStateOf(tripDetail.NightHalt == "yes") }
    var selectedLocation by remember { mutableStateOf(tripDetail.Location) }
    var expanded by remember { mutableStateOf(false) }
    val tourTypeOptions = listOf("Local", "Outside")
    var tourType by rememberSaveable { mutableStateOf(currentTour?.TourType ?: "Local") }

    var showTimePicker by remember { mutableStateOf(false) }
    var timeField by remember { mutableStateOf("") }
    var showDatePicker by remember { mutableStateOf(false) }
    var dateField by remember { mutableStateOf("") }

    if (showTimePicker) {
        TimePickerDialog(
            onDismissRequest = { showTimePicker = false },
            onTimeSelected = { selectedTime ->
                when (timeField) {
                    "travelFrom" -> travelFrom = selectedTime
                    "travelTo" -> travelTo = selectedTime
                }
                showTimePicker = false
            }
        )
    }

    // ADD DATE PICKER DIALOG FOR TRAVEL DATE
    if (showDatePicker) {
        val initialDate = when (dateField) {
            "travelDate" -> travelDate
            else -> ""
        }

        DatePickerDialog(
            initialDate = initialDate,
            onDismissRequest = { showDatePicker = false },
            onDateSelected = { date ->
                when (dateField) {
                    "travelDate" -> travelDate = date
                }
                showDatePicker = false
            }
        )
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                "Edit Trip Detail",
                style = typography.headlineSmall
            )
        },
        text = {
            Column(
                modifier = Modifier.verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // FIXED: Travel Date Picker - PROVIDE EMPTY onValueChange
                Box {
                    OutlinedTextField(
                        value = travelDate,
                        onValueChange = { /* DO NOTHING - READ ONLY */ },
                        label = { Text("Travel Date *") },
                        modifier = Modifier.fillMaxWidth(),
                        readOnly = true,
                        trailingIcon = {
                            Icon(
                                imageVector = Icons.Default.DateRange,
                                contentDescription = "Select Travel Date",
                                tint = MaterialTheme.colorScheme.primary
                            )
                        },
                        placeholder = { Text("YYYY-MM-DD") }
                    )
                    Box(
                        modifier = Modifier
                            .matchParentSize()
                            .clickable {
                                dateField = "travelDate"
                                showDatePicker = true
                            }
                    )
                }

                // FIXED: Travel From Time Picker - PROVIDE EMPTY onValueChange
                Box {
                    OutlinedTextField(
                        value = travelFrom,
                        onValueChange = { /* DO NOTHING - READ ONLY */ },
                        label = { Text("Travel From Time *") },
                        modifier = Modifier.fillMaxWidth(),
                        readOnly = true,
                        trailingIcon = {
                            Icon(
                                imageVector = Icons.Default.AccessTime,
                                contentDescription = "Select Travel From Time",
                                tint = MaterialTheme.colorScheme.primary
                            )
                        },
                        placeholder = { Text("Select departure time") }
                    )
                    Box(
                        modifier = Modifier
                            .matchParentSize()
                            .clickable {
                                timeField = "travelFrom"
                                showTimePicker = true
                            }
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                // FIXED: Travel To Time Picker - PROVIDE EMPTY onValueChange
                Box {
                    OutlinedTextField(
                        value = travelTo,
                        onValueChange = { /* DO NOTHING - READ ONLY */ },
                        label = { Text("Travel To Time *") },
                        modifier = Modifier.fillMaxWidth(),
                        readOnly = true,
                        trailingIcon = {
                            Icon(
                                imageVector = Icons.Default.AccessTime,
                                contentDescription = "Select Travel To Time",
                                tint = MaterialTheme.colorScheme.primary
                            )
                        },
                        placeholder = { Text("Select arrival time") }
                    )
                    Box(
                        modifier = Modifier
                            .matchParentSize()
                            .clickable {
                                timeField = "travelTo"
                                showTimePicker = true
                            }
                    )
                }

                // THESE FIELDS SHOULD REMAIN EDITABLE (KEEP FUNCTIONAL onValueChange):
                OutlinedTextField(
                    value = travelMode,
                    onValueChange = { travelMode = it },
                    label = { Text("Travel Mode *") },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text("Car, Train, Flight, etc.") }
                )

                OutlinedTextField(
                    value = km,
                    onValueChange = { km = it },
                    label = { Text("Distance (km) *") },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    placeholder = { Text("Enter distance") }
                )

                OutlinedTextField(
                    value = particular,
                    onValueChange = { particular = it },
                    label = { Text("Particulars") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = false,
                    maxLines = 3,
                    placeholder = { Text("Additional details...") }
                )

                // FIXED: Location Dropdown - PROVIDE EMPTY onValueChange
                var locationExpanded by remember { mutableStateOf(false) }
                ExposedDropdownMenuBox(
                    expanded = locationExpanded,
                    onExpandedChange = { locationExpanded = !locationExpanded }
                ) {
                    OutlinedTextField(
                        value = selectedLocation,
                        onValueChange = { /* DO NOTHING - READ ONLY */ },
                        label = { Text("Location *") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor(),
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = locationExpanded) },
                        readOnly = true,
                        placeholder = { Text("Select location") }
                    )
                    ExposedDropdownMenu(
                        expanded = locationExpanded,
                        onDismissRequest = { locationExpanded = false }
                    ) {
                        if (locations.isEmpty()) {
                            DropdownMenuItem(
                                text = { Text("No locations available") },
                                onClick = { locationExpanded = false }
                            )
                        } else {
                            locations.forEach { location ->
                                DropdownMenuItem(
                                    text = { Text(location.LocationName) },
                                    onClick = {
                                        selectedLocation = location.LocationName
                                        locationExpanded = false
                                    }
                                )
                            }
                        }
                    }
                }

                ExposedDropdownMenuBox(
                    expanded = expanded,
                    onExpandedChange = { expanded = !expanded }
                ) {
                    OutlinedTextField(
                        value = tourType,
                        onValueChange = {},
                            readOnly = true,
                        label = { Text("Tour Type") },
                        trailingIcon = {
                            ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
                        },
                        modifier = Modifier
                            .menuAnchor()
                            .fillMaxWidth()
                    )

                    ExposedDropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        tourTypeOptions.forEach { type ->
                            DropdownMenuItem(
                                text = { Text(type) },
                                onClick = {
                                    tourType = type
                                    expanded = false
                                }
                            )
                        }
                    }
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Checkbox(
                        checked = nightHalt,
                        onCheckedChange = { nightHalt = it }
                    )
                    Text("Night Halt")
                }

                Text(
                    "* Required fields",
                    style = typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (travelDate.isNotEmpty() && travelFrom.isNotEmpty() &&
                        travelTo.isNotEmpty() && travelMode.isNotEmpty() &&
                        km.isNotEmpty() && selectedLocation.isNotEmpty()) {
                        onSave(
                            tripDetail.copy(
                                TravelDate = travelDate,
                                TravelFrom = travelFrom,
                                TravelTo = travelTo,
                                TravelMode = travelMode,
                                km = km,
                                Particular = particular,
                                NightHalt = if (nightHalt) "yes" else "no",
                                Location = selectedLocation
                            )
                        )
                    }
                },
                enabled = travelDate.isNotEmpty() && travelFrom.isNotEmpty() &&
                        travelTo.isNotEmpty() && travelMode.isNotEmpty() &&
                        km.isNotEmpty() && selectedLocation.isNotEmpty()
            ) {
                Text("Save")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
// Expense Edit Dialog
// Expense Edit Dialog - UPDATED VERSION
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExpenseEditDialog(
    expense: Expense,
    expenseTypes: List<ExpenseItem>,
    locations: List<LocationItem>,
    onSave: (Expense) -> Unit,
    onDismiss: () -> Unit
) {
    var expenseDate by remember { mutableStateOf(expense.ExpenseDate) }
    var selectedExpenseType by remember { mutableStateOf(expense.ExpenseName) }
    var selectedLocation by remember { mutableStateOf(expense.Location) }
    var expenseFrom by remember { mutableStateOf(expense.ExpenseFrom) }
    var expenseTo by remember { mutableStateOf(expense.ExpenseTo) }
    var amount by remember { mutableStateOf(expense.Amount.toString()) }
    var particular by remember { mutableStateOf(expense.Particular) }
    var showDatePicker by remember { mutableStateOf(false) }
    var dateField by remember { mutableStateOf("") } // ADD "expenseDate" TO THIS

    // ADD DATE PICKER DIALOG HERE (move it to the top)
    if (showDatePicker) {
        val initialDate = when (dateField) {
            "expenseDate" -> expenseDate  // ADD THIS CASE
            "expenseFrom" -> expenseFrom
            "expenseTo" -> expenseTo
            else -> ""
        }

        DatePickerDialog(
            initialDate = initialDate,
            onDismissRequest = { showDatePicker = false },
            onDateSelected = { date ->
                when (dateField) {
                    "expenseDate" -> expenseDate = date  // ADD THIS CASE
                    "expenseFrom" -> expenseFrom = date
                    "expenseTo" -> expenseTo = date
                }
                showDatePicker = false
            }
        )
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                "Edit Expense",
                style = typography.headlineSmall
            )
        },
        text = {
            Column(
                modifier = Modifier.verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // STEP 2: REPLACE THE OLD EXPENSE DATE FIELD WITH CALENDAR PICKER
                // Expense Date Picker
                Box {
                    OutlinedTextField(
                        value = expenseDate,
                        onValueChange = { }, // REMOVE DIRECT EDITING
                        label = { Text("Expense Date *") },
                        modifier = Modifier.fillMaxWidth(),
                        readOnly = true, // MAKE IT READ-ONLY
                        trailingIcon = {
                            Icon(
                                imageVector = Icons.Default.DateRange,
                                contentDescription = "Select Expense Date",
                                tint = MaterialTheme.colorScheme.primary
                            )
                        },
                        placeholder = { Text("YYYY-MM-DD") }
                    )
                    Box(
                        modifier = Modifier
                            .matchParentSize()
                            .clickable {
                                dateField = "expenseDate" // SET THE FIELD
                                showDatePicker = true // SHOW CALENDAR
                            }
                    )
                }

                // REST OF YOUR EXISTING CODE REMAINS THE SAME...
                // Expense Type Dropdown
                var expenseTypeExpanded by remember { mutableStateOf(false) }
                ExposedDropdownMenuBox(
                    expanded = expenseTypeExpanded,
                    onExpandedChange = { expenseTypeExpanded = !expenseTypeExpanded }
                ) {
                    OutlinedTextField(
                        value = selectedExpenseType,
                        onValueChange = { },
                        label = { Text("Expense Type *") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor(),
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expenseTypeExpanded) },
                        readOnly = true,
                        placeholder = { Text("Select expense type") }
                    )
                    ExposedDropdownMenu(
                        expanded = expenseTypeExpanded,
                        onDismissRequest = { expenseTypeExpanded = false }
                    ) {
                        expenseTypes.forEach { expenseType ->
                            DropdownMenuItem(
                                text = { Text(expenseType.ExpenseName) },
                                onClick = {
                                    selectedExpenseType = expenseType.ExpenseName
                                    expenseTypeExpanded = false
                                }
                            )
                        }
                    }
                }

                // REMOVE THE OLD DATE PICKER LOGIC FROM HERE (it's now at the top)

                // Location Dropdown
                var locationExpanded by remember { mutableStateOf(false) }
                ExposedDropdownMenuBox(
                    expanded = locationExpanded,
                    onExpandedChange = { locationExpanded = !locationExpanded }
                ) {
                    OutlinedTextField(
                        value = selectedLocation,
                        onValueChange = { },
                        label = { Text("Location *") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor(),
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = locationExpanded) },
                        readOnly = true,
                        placeholder = { Text("Select location") }
                    )
                    ExposedDropdownMenu(
                        expanded = locationExpanded,
                        onDismissRequest = { locationExpanded = false }
                    ) {
                        if (locations.isEmpty()) {
                            DropdownMenuItem(
                                text = { Text("No locations available") },
                                onClick = { locationExpanded = false }
                            )
                        } else {
                            locations.forEach { location ->
                                DropdownMenuItem(
                                    text = { Text(location.LocationName) },
                                    onClick = {
                                        selectedLocation = location.LocationName
                                        locationExpanded = false
                                    }
                                )
                            }
                        }
                    }
                }

                // Date Range Fields in Row (THESE ALREADY WORK CORRECTLY)
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Expense From Date Picker
                    Box(modifier = Modifier.weight(1f)) {
                        OutlinedTextField(
                            value = expenseFrom,
                            onValueChange = { },
                            label = { Text("From Date") },
                            modifier = Modifier.fillMaxWidth(),
                            readOnly = true,
                            trailingIcon = {
                                Icon(
                                    imageVector = Icons.Default.DateRange,
                                    contentDescription = "Select From Date",
                                    tint = MaterialTheme.colorScheme.primary
                                )
                            },
                            placeholder = { Text("YYYY-MM-DD") }
                        )
                        Box(
                            modifier = Modifier
                                .matchParentSize()
                                .clickable {
                                    dateField = "expenseFrom"
                                    showDatePicker = true
                                }
                        )
                    }

                    // Expense To Date Picker
                    Box(modifier = Modifier.weight(1f)) {
                        OutlinedTextField(
                            value = expenseTo,
                            onValueChange = { },
                            label = { Text("To Date") },
                            modifier = Modifier.fillMaxWidth(),
                            readOnly = true,
                            trailingIcon = {
                                Icon(
                                    imageVector = Icons.Default.DateRange,
                                    contentDescription = "Select To Date",
                                    tint = MaterialTheme.colorScheme.primary
                                )
                            },
                            placeholder = { Text("YYYY-MM-DD") }
                        )
                        Box(
                            modifier = Modifier
                                .matchParentSize()
                                .clickable {
                                    dateField = "expenseTo"
                                    showDatePicker = true
                                }
                        )
                    }
                }

                // Amount Field
                OutlinedTextField(
                    value = amount,
                    onValueChange = { amount = it },
                    label = { Text("Amount (₹) *") },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    prefix = { Text("₹") },
                    placeholder = { Text("0.00") }
                )

                // Particulars Field
                OutlinedTextField(
                    value = particular,
                    onValueChange = { particular = it },
                    label = { Text("Particulars") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = false,
                    maxLines = 3,
                    placeholder = { Text("Description of this expense...") }
                )

                // File attachment info
                if (expense.fileUri != null || expense.BillFileBase64.isNotEmpty() || expense.BillPath.isNotEmpty()) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
                                shape = MaterialTheme.shapes.small
                            )
                            .padding(8.dp)
                    ) {
                        Icon(
                            Icons.Default.Attachment,
                            contentDescription = "File attached",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Bill attached",
                            style = typography.bodySmall,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }

                // REQUIRED FIELDS NOTE
                Text(
                    "* Required fields",
                    style = typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (expenseDate.isNotEmpty() && selectedExpenseType.isNotEmpty() &&
                        selectedLocation.isNotEmpty() && amount.isNotEmpty()) {
                        onSave(
                            expense.copy(
                                ExpenseDate = expenseDate,
                                ExpenseName = selectedExpenseType,
                                Location = selectedLocation,
                                ExpenseFrom = expenseFrom,
                                ExpenseTo = expenseTo,
                                Amount = amount.toDoubleOrNull() ?: 0.0,
                                Particular = particular
                            )
                        )
                    }
                },
                enabled = expenseDate.isNotEmpty() && selectedExpenseType.isNotEmpty() &&
                        selectedLocation.isNotEmpty() && amount.isNotEmpty()
            ) {
                Text("Save")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

// Helper functions
private fun getCurrentDate(): String {
    val formatter = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault())
    return formatter.format(java.util.Date())
}

private fun getCurrentTime(): String {
    val formatter = java.text.SimpleDateFormat("hh:mm a", java.util.Locale.getDefault())
    return formatter.format(java.util.Date())
}

// ---------------- IMPROVED TRIP DETAIL ITEM ----------------
@Composable
fun TripDetailItem(
    detail: TripDetail,
    index: Int,
    onEdit: (TripDetail) -> Unit,
    onRemove: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Trip ${index + 1}",
                    style = typography.bodyMedium,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.primary
                )

                Row {
                    IconButton(
                        onClick = { onEdit(detail) },
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            Icons.Default.Edit,
                            contentDescription = "Edit",
                            modifier = Modifier.size(16.dp)
                        )
                    }
                    IconButton(
                        onClick = onRemove,
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            Icons.Default.Delete,
                            contentDescription = "Remove",
                            modifier = Modifier.size(16.dp),
                            tint = MaterialTheme.colorScheme.error
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Trip Details
            if (detail.Particular.isNotEmpty()) {
                Text(
                    text = detail.Particular,
                    style = typography.bodyMedium,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            if (detail.TravelFrom.isNotEmpty() || detail.TravelTo.isNotEmpty()) {
                Text(
                    text = "${detail.TravelFrom} → ${detail.TravelTo}",
                    style = typography.bodySmall,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                if (detail.TravelDate.isNotEmpty()) {
                    Text(
                        text = "Date: ${detail.TravelDate}",
                        style = typography.bodySmall
                    )
                }

                if (detail.TravelMode.isNotEmpty()) {
                    Text(
                        text = "Mode: ${detail.TravelMode}",
                        style = typography.bodySmall
                    )
                }
            }

            if (detail.km.isNotEmpty()) {
                Text(
                    text = "Distance: ${detail.km} km",
                    style = typography.bodySmall
                )
            }

            if (detail.Location.isNotEmpty()) {
                Text(
                    text = "Location: ${detail.Location}",
                    style = typography.bodySmall
                )
            }

            if (detail.NightHalt == "yes") {
                Text(
                    text = "🌙 Night Halt",
                    style = typography.bodySmall,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}

// ---------------- IMPROVED EXPENSE ITEM ----------------


fun requestIgnoreBatteryOptimizations(context: Context) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        val powerManager = context.getSystemService(Context.POWER_SERVICE) as PowerManager
        if (!powerManager.isIgnoringBatteryOptimizations(context.packageName)) {
            val intent = Intent(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS).apply {
                data = Uri.parse("package:${context.packageName}")
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }
            context.startActivity(intent)
        }
    }
}

suspend fun validateCompanyCode(
    code: String,
    vm: SessionViewModel,
    ctx: Context,
    onSuccess: () -> Unit,
    setLoading: (Boolean) -> Unit
) {
    if (code.isBlank()) {
        Toast.makeText(ctx, "Please enter a valid company code", Toast.LENGTH_SHORT).show()
        return
    }

    setLoading(true)

    try {
        val retrofit = Retrofit.Builder()
            .baseUrl("https://protimes.co.in/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)

        val response = retrofit.validateCompany(CompanyRequest(code))
        val clientUrl = response.data_value.firstOrNull()?.client_url

        if (response.status == "success" && !clientUrl.isNullOrBlank()) {
            vm.saveClientUrl(clientUrl)
            Log.d("validateCompanyCode", "✅ client_url saved: $clientUrl")
            Toast.makeText(ctx, "Company verified successfully", Toast.LENGTH_SHORT).show()
            onSuccess()
        } else {
            Toast.makeText(ctx, "Invalid company code", Toast.LENGTH_SHORT).show()
        }
    } catch (e: Exception) {
        Log.e("validateCompanyCode", "❌ Validation failed", e)
        Toast.makeText(ctx, "Network error. Try again.", Toast.LENGTH_SHORT).show()
    } finally {
        setLoading(false)
    }
}

// ---------------- SCREENS ----------------
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ServerSetupScreen(
    onSuccess: () -> Unit,
    modifier: Modifier = Modifier
) {
    val vm: SessionViewModel = viewModel()
    val ctx = LocalContext.current
    val scope = rememberCoroutineScope()
    val focusManager = LocalFocusManager.current
    var code by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }

    val backgroundPermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            Toast.makeText(ctx, "Background location permission granted", Toast.LENGTH_SHORT).show()
            requestIgnoreBatteryOptimizations(ctx)
        } else {
            Toast.makeText(ctx, "Background location permission denied", Toast.LENGTH_LONG).show()
        }
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val fineGranted = permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true
        val coarseGranted = permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true

        if (fineGranted || coarseGranted) {
            Toast.makeText(ctx, "Location permission granted", Toast.LENGTH_SHORT).show()
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                backgroundPermissionLauncher.launch(Manifest.permission.ACCESS_BACKGROUND_LOCATION)
            } else {
                requestIgnoreBatteryOptimizations(ctx)
            }
        } else {
            Toast.makeText(ctx, "Location permission denied", Toast.LENGTH_LONG).show()
        }
    }

    LaunchedEffect(Unit) {
        val fineGranted = ContextCompat.checkSelfPermission(
            ctx,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
        val coarseGranted = ContextCompat.checkSelfPermission(
            ctx,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        if (!fineGranted || !coarseGranted) {
            permissionLauncher.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q &&
                ContextCompat.checkSelfPermission(
                    ctx,
                    Manifest.permission.ACCESS_BACKGROUND_LOCATION
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                backgroundPermissionLauncher.launch(Manifest.permission.ACCESS_BACKGROUND_LOCATION)
            } else {
                requestIgnoreBatteryOptimizations(ctx)
            }
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.Center),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            AnimatedVisibility(
                visible = true,
                enter = fadeIn() + slideInVertically { -40 },
                exit = fadeOut() + slideOutVertically { -40 }
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Image(
                        painter = painterResource(id = R.drawable.logo1),
                        contentDescription = "Logo",
                        modifier = Modifier.size(120.dp)
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Image(
                        painter = painterResource(id = R.drawable.logo2),
                        contentDescription = "App Name",
                        modifier = Modifier
                            .height(40.dp)
                            .wrapContentWidth()
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = "Enter your company code to continue",
                style = typography.bodyMedium,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.8f)
            )

            Spacer(modifier = Modifier.height(32.dp))

            OutlinedTextField(
                value = code,
                onValueChange = { code = it.uppercase() },
                label = { Text("Company Code") },
                singleLine = true,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                keyboardActions = KeyboardActions(
                    onDone = {
                        focusManager.clearFocus()
                        scope.launch {
                            validateCompanyCode(code, vm, ctx, onSuccess) { isLoading = it }
                        }
                    }
                ),
                modifier = Modifier.fillMaxWidth(),
                shape = MaterialTheme.shapes.medium,
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.outline
                )
            )

            Spacer(modifier = Modifier.height(24.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .clip(MaterialTheme.shapes.medium)
                    .background(
                        brush = Brush.horizontalGradient(
                            colors = listOf(
                                Color(0xFFB4000A),
                                Color(0xFFFF6347)
                            )
                        )
                    )
                    .clickable(enabled = !isLoading) {
                        scope.launch {
                            validateCompanyCode(code, vm, ctx, onSuccess) { isLoading = it }
                        }
                    },
                contentAlignment = Alignment.Center
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = Color.White,
                        strokeWidth = 2.dp
                    )
                } else {
                    Text(
                        text = "Continue",
                        style = typography.labelLarge,
                        color = Color.White
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    onLogin: () -> Unit,
    onChangeServer: () -> Unit,
    modifier: Modifier = Modifier
) {
    val vm: SessionViewModel = viewModel()
    val clientUrl by vm.clientUrl.collectAsState()
    val ctx = LocalContext.current
    val scope = rememberCoroutineScope()
    var user by remember { mutableStateOf("") }
    var pass by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    val focusManager = LocalFocusManager.current



    // Get device identifier from MainActivity
    val deviceIdentifier = remember {
        (ctx as? MainActivity)?.getDeviceIdentifier() ?: ""
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.Center),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            AnimatedVisibility(
                visible = true,
                enter = fadeIn() + slideInVertically { -40 },
                exit = fadeOut() + slideOutVertically { -40 }
            ) {
                Image(
                    painter = painterResource(id = R.drawable.logo1),
                    contentDescription = "Logo",
                    modifier = Modifier.size(100.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Welcome Back",
                style = typography.headlineMedium,
                color = MaterialTheme.colorScheme.onBackground
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Sign in to continue",
                style = typography.bodyMedium,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.8f)
            )

            Spacer(modifier = Modifier.height(32.dp))

            OutlinedTextField(
                value = user,
                onValueChange = { user = it },
                label = { Text("User ID") },
                leadingIcon = { Icon(Icons.Default.Person, null) },
                singleLine = true,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                keyboardActions = KeyboardActions(
                    onNext = { focusManager.moveFocus(FocusDirection.Down) }
                ),
                modifier = Modifier.fillMaxWidth(),
                shape = MaterialTheme.shapes.medium,
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.outline
                )
            )

            Spacer(modifier = Modifier.height(16.dp))

            var passwordVisible by remember { mutableStateOf(false) }

            OutlinedTextField(
                value = pass,
                onValueChange = { pass = it },
                label = { Text("Password") },
                leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null) },
                trailingIcon = {
                    val icon = if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff
                    val desc = if (passwordVisible) "Hide password" else "Show password"
                    IconButton(onClick = { passwordVisible = !passwordVisible }) {
                        Icon(imageVector = icon, contentDescription = desc)
                    }
                },
                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                singleLine = true,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                keyboardActions = KeyboardActions(onDone = { focusManager.clearFocus() }),
                modifier = Modifier.fillMaxWidth(),
                shape = MaterialTheme.shapes.medium,
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.outline
                )
            )

            Spacer(modifier = Modifier.height(24.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .clip(MaterialTheme.shapes.medium)
                    .background(
                        brush = Brush.horizontalGradient(
                            colors = listOf(
                                Color(0xFFB4000A),
                                Color(0xFFFF6347)
                            )
                        )
                    )
                    .clickable(enabled = !isLoading) {
                        if (user.isBlank() || pass.isBlank()) {
                            showSnackbar(ctx, "Please enter both credentials")
                            return@clickable
                        }
                        scope.launch {
                            isLoading = true
                            try {
                                if (clientUrl.isNullOrEmpty()) {
                                    showSnackbar(ctx, "No client URL configured")
                                    return@launch
                                }

                                // Log the device identifier being sent
                                Log.d("LoginScreen", "Using device identifier: $deviceIdentifier")
                                val retrofit = Retrofit
                                    .Builder()
                                    .baseUrl(clientUrl!!)
                                    .addConverterFactory(GsonConverterFactory.create())
                                    .build()
                                    .create(ApiService::class.java)
                                val res = retrofit.login(LoginRequest(user, pass,deviceIdentifier))
                                Log.d("LoginScreen", "Sending login request: $user,$pass,$deviceIdentifier")
                                val name = res.data?.firstOrNull()?.staf_nm
                                val staffData = res.data?.firstOrNull()
                                if (res.status == "success" && name != null) {
                                    staffData?.let {
                                        vm.saveStaffDetails(it)
                                        vm.saveStaffInfo(
                                            StaffInfo(
                                                staf_sl = staffData.staf_sl,
                                                staf_nm = staffData.staf_nm.toString(),
                                                dept_nm = staffData.dept_nm.toString(),
                                                desg_nm = staffData.desg_nm.toString(),
                                                emp_code = staffData.emp_code,
                                                present_per = staffData.present_per
                                            )
                                        )
                                    }
                                    vm.setLoginState(true)
                                    vm.saveUsername(staffData?.staf_nm.toString())
                                    onLogin()
                                } else {
                                    showSnackbar(ctx, "Invalid credentials")
                                }
                            } catch (e: Exception) {
                                Log.e("Login", "Login failed", e)
                                showSnackbar(ctx, "Error: ${e.message ?: "Unknown error"}")
                            } finally {
                                isLoading = false
                            }
                        }
                    },
                contentAlignment = Alignment.Center
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = Color.White,
                        strokeWidth = 2.dp
                    )
                } else {
                    Text(
                        text = "Sign In",
                        color = Color.White,
                        style = typography.labelLarge
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            TextButton(
                onClick = onChangeServer,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "Change Company",
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}

@OptIn(ExperimentalPermissionsApi::class, ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    onLogout: () -> Unit,
    onNavigateToTour: () -> Unit, // Add this parameter
    modifier: Modifier = Modifier,
    sessionViewModel: SessionViewModel = viewModel()
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    val username by sessionViewModel.username.collectAsState()
    val stafSl by sessionViewModel.staffSl.collectAsState()
    val deptName by sessionViewModel.deptName.collectAsState()
    val desgName by sessionViewModel.designation.collectAsState()
    val trackingFromStore by sessionViewModel.isTracking.collectAsState()
    val startTimeMillis by sessionViewModel.trackingStartTime.collectAsState()

    var showLogoutDialog by remember { mutableStateOf(false) }
    var showPurposeDialog by remember { mutableStateOf(false) }
    var purposeText by remember { mutableStateOf("") }
    var isTrackingActive by rememberSaveable { mutableStateOf(trackingFromStore) }
    var trackingDuration by remember { mutableStateOf(0) }
    var lastLocation by remember { mutableStateOf<LocationData?>(null) }
    var locationHistory by remember { mutableStateOf<List<LocationData>>(emptyList()) }

    val permissionState = rememberMultiplePermissionsState(
        listOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION,
        )
    )

    // Timer for tracking duration
    LaunchedEffect(isTrackingActive, startTimeMillis) {
        while (isTrackingActive && startTimeMillis > 0) {
            val elapsed = (System.currentTimeMillis() - startTimeMillis) / 1000
            trackingDuration = elapsed.toInt()
            delay(1000)
        }
    }

    fun toggleTracking(context: Context, staffSl: String?, purpose: String) {
        val intent = Intent(context, MyForegroundService::class.java).apply {
            putExtra("staf_sl", staffSl)
            putExtra("purpose", purpose)
        }

        if (isTrackingActive) {
            context.stopService(intent)
            trackingDuration = 0
            isTrackingActive = false
            scope.launch {
                sessionViewModel.setTrackingActive(false)
                sessionViewModel.clearTrackingStartTime()
            }
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                val powerManager = context.getSystemService(Context.POWER_SERVICE) as PowerManager
                if (!powerManager.isIgnoringBatteryOptimizations(context.packageName)) {
                    val batteryIntent = Intent(Settings.ACTION_IGNORE_BATTERY_OPTIMIZATION_SETTINGS).apply {
                        data = Uri.parse("package:${context.packageName}")
                        flags = Intent.FLAG_ACTIVITY_NEW_TASK
                    }
                    context.startActivity(batteryIntent)
                }
            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                context.startForegroundService(intent)
            } else {
                context.startService(intent)
            }

            isTrackingActive = true
            scope.launch {
                sessionViewModel.setTrackingActive(true)
                sessionViewModel.setTrackingStartTime(System.currentTimeMillis())
            }
        }
    }

    Scaffold(
        modifier = modifier,
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Location Tracker") },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(),
                actions = {
                    IconButton(onClick = {
                        if (isTrackingActive) {
                            toggleTracking(context, stafSl, purposeText)
                        } else {
                            showPurposeDialog = true
                        }
                    }) {
                        Icon(
                            imageVector = if (isTrackingActive) Icons.Default.LocationOn else Icons.Default.LocationOff,
                            contentDescription = "Toggle Tracking",
                            tint = if (isTrackingActive) Color(0xFF4CAF50) else Color(0xFFF44336)
                        )
                    }
                    IconButton(onClick = { showLogoutDialog = true }) {
                        Icon(Icons.Default.Logout, contentDescription = "Logout")
                    }
                }
            )
        },
        floatingActionButton = {
            // REPLACED SOS WITH TOUR BUTTON
            FloatingActionButton(
                onClick = onNavigateToTour,
                containerColor = MaterialTheme.colorScheme.secondary,
                shape = MaterialTheme.shapes.extraLarge // More rounded
            ) {
                Icon(Icons.Default.Tour, contentDescription = "Tour")
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .background(MaterialTheme.colorScheme.background) // Ensure background
        ) {
            // Profile Card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .shadow(4.dp, shape = MaterialTheme.shapes.medium),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = Icons.Default.AccountCircle,
                        contentDescription = "User",
                        modifier = Modifier.size(80.dp)
                    )
                    Spacer(Modifier.height(16.dp))
                    Text("Welcome,", style = typography.bodyLarge)
                    Text(username ?: "User", style = typography.headlineMedium)
                    Spacer(Modifier.height(8.dp))
                    Text(deptName ?: "", style = typography.bodyMedium)
                    Text(desgName ?: "", style = typography.bodyMedium)
                    Spacer(Modifier.height(24.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Tracking Status:")
                        Text(
                            if (isTrackingActive) "ACTIVE" else "INACTIVE",
                            color = if (isTrackingActive) Color(0xFF4CAF50) else Color(0xFFF44336)
                        )
                    }

                    Spacer(Modifier.height(16.dp))

                    if (isTrackingActive) {
                        val min = trackingDuration / 60
                        val sec = trackingDuration % 60
                        Text("Duration: %02d:%02d".format(min, sec))
                    }

                    Spacer(Modifier.height(16.dp))

                    lastLocation?.let {
                        LocationDetailItem(it)
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Button(
                            onClick = {
                                if (isTrackingActive) {
                                    toggleTracking(context, stafSl, purposeText)
                                } else {
                                    showPurposeDialog = true
                                }
                            },
                            modifier = Modifier.weight(1f),
                            shape = MaterialTheme.shapes.medium
                        ) {
                            Text(if (isTrackingActive) "Stop Tracking" else "Start Tracking")
                        }
                        Spacer(Modifier.width(16.dp))

                        // ADDED TOUR BUTTON IN THE CARD AS WELL
                        Button(
                            onClick = onNavigateToTour,
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.secondary
                            ),
                            shape = MaterialTheme.shapes.medium
                        ) {
                            Icon(Icons.Default.Tour, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(Modifier.width(8.dp))
                            Text("Tour")
                        }
                    }
                }
            }

            if (locationHistory.isNotEmpty()) {
                Text(
                    text = "Recent Locations",
                    modifier = Modifier.padding(16.dp),
                    style = typography.titleMedium
                )
                LocationHistoryList(locations = locationHistory)
            }
        }
    }

    if (showLogoutDialog) {
        AlertDialog(
            onDismissRequest = { showLogoutDialog = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        if (isTrackingActive) {
                            context.stopService(Intent(context, MyForegroundService::class.java))
                        }
                        scope.launch {
                            sessionViewModel.clearSession()
                            onLogout()
                        }
                    }
                ) {
                    Text("Logout")
                }
            },
            dismissButton = {
                TextButton(onClick = { showLogoutDialog = false }) {
                    Text("Cancel")
                }
            },
            title = { Text("Confirm Logout") },
            text = {
                Text(
                    if (isTrackingActive)
                        "Tracking is active. Stop and logout?"
                    else
                        "Are you sure you want to logout?"
                )
            },
            icon = { Icon(Icons.Default.Logout, contentDescription = null) }
        )
    }

    if (showPurposeDialog) {
        AlertDialog(
            onDismissRequest = { showPurposeDialog = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        if (purposeText.isNotBlank()) {
                            toggleTracking(context, stafSl, purposeText)
                            showPurposeDialog = false
                            purposeText = ""
                        } else {
                            showSnackbar(context, "Please enter a purpose")
                        }
                    },
                    enabled = purposeText.isNotBlank()
                ) {
                    Text("Start Tracking")
                }
            },
            dismissButton = {
                TextButton(onClick = {
                    showPurposeDialog = false
                    purposeText = ""
                }) {
                    Text("Cancel")
                }
            },
            title = { Text("Enter Trip Details") },
            text = {
                Column {
                    Text("Please provide details for this tracking session:")
                    Spacer(Modifier.height(16.dp))
                    OutlinedTextField(
                        value = purposeText,
                        onValueChange = { purposeText = it },
                        label = { Text("Trip Purpose/Details *") },
                        placeholder = { Text("e.g., Visit to client, Field work, etc.") },
                        singleLine = false,
                        maxLines = 3,
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                        keyboardActions = KeyboardActions(
                            onDone = {
                                if (purposeText.isNotBlank()) {
                                    toggleTracking(context, stafSl, purposeText)
                                    showPurposeDialog = false
                                    purposeText = ""
                                } else {
                                    showSnackbar(context, "Please enter a purpose")
                                }
                            }
                        ),
                        modifier = Modifier.fillMaxWidth(),
                        shape = MaterialTheme.shapes.medium
                    )
                    Spacer(Modifier.height(8.dp))
                    Text(
                        text = "* This information will be sent with your location data",
                        style = typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )
                }
            },
            icon = { Icon(Icons.Default.Info, contentDescription = null) }
        )
    }
}

@Composable
private fun LocationDetailItem(location: LocationData) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
                shape = MaterialTheme.shapes.medium
            )
            .padding(12.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Place,
                contentDescription = "Location",
                modifier = Modifier.size(24.dp),
                tint = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = location.locationName,
                style = typography.bodyLarge,
                fontWeight = FontWeight.Medium
            )
        }
        Spacer(modifier = Modifier.height(4.dp))
        Row {
            Text(
                text = "Time: ",
                style = typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
            )
            Text(
                text = location.time,
                style = typography.bodySmall
            )
        }
        Row {
            Text(
                text = "Coordinates: ",
                style = typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
            )
            Text(
                text = "%.4f, %.4f".format(location.latitude, location.longitude),
                style = typography.bodySmall
            )
        }
    }
}

@Composable
private fun LocationHistoryList(
    locations: List<LocationData>,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = MaterialTheme.shapes.medium,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = MaterialTheme.colorScheme.onSurface
        )
    ) {
        Column {
            locations.forEachIndexed { index, location ->
                LocationHistoryItem(location)
                if (index < locations.size - 1) {
                    Divider(
                        modifier = Modifier.padding(horizontal = 16.dp),
                        thickness = 1.dp,
                        color = MaterialTheme.colorScheme.outline.copy(alpha = 0.1f)
                    )
                }
            }
        }
    }
}

@Composable
private fun LocationHistoryItem(location: LocationData) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = Icons.Default.History,
            contentDescription = "History",
            modifier = Modifier.size(24.dp),
            tint = MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.width(16.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = location.locationName,
                style = typography.bodyLarge
            )
            Text(
                text = location.time,
                style = typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
            )
        }
        Text(
            text = "%.2f, %.2f".format(location.latitude, location.longitude),
            style = typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
        )
    }
}

@Composable
private fun GradientLogoutButton(
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    val gradient = Brush.horizontalGradient(
        colors = listOf(
            Color(0xFFFF5252),
            Color(0xFFFF4081)
        )
    )

    Button(
        onClick = onClick,
        modifier = modifier,
        shape = MaterialTheme.shapes.medium,
        colors = ButtonDefaults.buttonColors(
            containerColor = Color.Transparent,
            contentColor = Color.White
        ),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.5f))
    ) {
        Box(
            modifier = Modifier
                .background(gradient, MaterialTheme.shapes.medium)
                .fillMaxSize()
                .padding(8.dp),
            contentAlignment = Alignment.Center
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Logout,
                    contentDescription = "Logout",
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("Logout", style = typography.labelLarge)
            }
        }
    }
}

data class LocationData(
    val time: String,
    val locationName: String,
    val latitude: Double,
    val longitude: Double
)



private fun getCityName(context: Context, latitude: Double, longitude: Double): String {
    return try {
        val geocoder = Geocoder(context, Locale.getDefault())
        val addresses = geocoder.getFromLocation(latitude, longitude, 1)
        addresses?.firstOrNull()?.let { address ->
            address.locality ?: address.subAdminArea ?: address.adminArea ?: "Unknown Location"
        } ?: "Unknown Location"
    } catch (e: Exception) {
        Log.e("Dashboard", "Geocoder failed", e)
        "Location Error"
    }
}

private fun showSnackbar(context: Context, message: String) {
    Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
}