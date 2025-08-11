package com.example.tron

import android.app.Application
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import android.os.Build
import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Geocoder
import android.net.Uri
import android.os.Bundle
import android.os.PowerManager
import android.provider.Settings
import android.util.Log
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
import androidx.compose.ui.unit.*
import androidx.core.content.ContextCompat
import androidx.core.view.WindowCompat
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.*
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.*
import com.google.accompanist.permissions.MultiplePermissionsState
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.POST
import java.util.Locale

// ---------------- THEME ----------------
private val DarkColorScheme = darkColorScheme(
    primary = Color(0xFFA5C9FF),
    secondary = Color(0xFFB5C4D9),
    tertiary = Color(0xFFE8DEF8),
    background = Color(0xFF1C1B1F),
    surface = Color(0xFF1C1B1F),
    onPrimary = Color(0xFF002E5D),
    onSecondary = Color(0xFF202C3B),
    onTertiary = Color(0xFF3A2A56),
    onBackground = Color(0xFFE5E1E6),
    onSurface = Color(0xFFE5E1E6),
)

private val LightColorScheme = lightColorScheme(
    primary = Color(0xFF0062A7),
    secondary = Color(0xFF4F6074),
    tertiary = Color(0xFF6750A4),
    background = Color(0xFFFBF8FE),
    surface = Color(0xFFFBF8FE),
    onPrimary = Color(0xFFFFFFFF),
    onSecondary = Color(0xFFFFFFFF),
    onTertiary = Color(0xFFFFFFFF),
    onBackground = Color(0xFF1C1B1F),
    onSurface = Color(0xFF1C1B1F),
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
        shapes = Shapes,
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
    val emp_code: String?,        // must be String
    val device_code: String?,
    val trackon: String?,         // nullable
    val r_usr_sl: String?,        // nullable
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

    // 🔹 Optional in-memory cache for quick access
    private val _staffInfo = mutableStateOf<StaffInfo?>(null)
    val staffInfo: StaffInfo? get() = _staffInfo.value

    fun saveStaffInfo(data: StaffInfo?) {
        _staffInfo.value = data
    }

    // 🔹 Save functions
    suspend fun saveStaffDetails(data: StaffData) = ds.edit {
        it[USERNAME] = data.staf_nm.toString()
        it[STAFF_SL] = data.staf_sl.toString()
        it[DEPT_NAME] = data.dept_nm.toString()
        it[DESIGNATION] = data.desg_nm.toString()
        it[EMP_CODE] = data.emp_code.toString()
        it[PRESENT_PER] = data.present_per?.toString() ?: "0.0"
        Log.d("SessionViewModel", "Staff details saved. SL: ${data.staf_sl}")
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

// ---------------- API MODELS ----------------
data class CompanyRequest(val compcode: String)
data class CompanyResponse(val status: String, val data_value: List<ClientData>)
data class ClientData(val client_url: String)

data class LoginRequest(val userid: String, val password: String)
data class LoginResponse(
    val status: String,
    val Code: String,
    val msg: String,
    val data: List<StaffData>
)

data class UserData(val staf_nm: String, val emp_code: String)

// ---------------- API SERVICE ----------------
interface ApiService {
    @POST("ptadmin/api/validcode")
    suspend fun validateCompany(@Body req: CompanyRequest): CompanyResponse

    @POST("api/login")
    suspend fun login(@Body req: LoginRequest): LoginResponse
}

// ---------------- MAIN ACTIVITY ----------------
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, true)
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
                modifier = Modifier.fillMaxSize()
            )
        }
    }
}

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
            .baseUrl("https://protimes.co.in/") // default base URL to call validateCompany
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)

        val response = retrofit.validateCompany(CompanyRequest(code))
        val clientUrl = response.data_value.firstOrNull()?.client_url

        if (response.status == "success" && !clientUrl.isNullOrBlank()) {
            // ✅ Save to DataStore via ViewModel
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

    // 🔹 Launcher for background location (Android Q+)
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

    // 🔹 Launcher for fine + coarse location
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

    // 🔹 Launch permissions on first load
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

    // 🔹 UI Layout
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
                        painter = painterResource(id = R.drawable.vichaar),
                        contentDescription = "Logo",
                        modifier = Modifier.size(120.dp)
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Image(
                        painter = painterResource(id = R.drawable.vichaar),
                        contentDescription = "App Name",
                        modifier = Modifier.height(40.dp).wrapContentWidth()
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = "Enter your company code to continue",
                style = MaterialTheme.typography.bodyMedium,
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
                        style = MaterialTheme.typography.labelLarge,
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
            // Animated logo/icon
            AnimatedVisibility(
                visible = true,
                enter = fadeIn() + slideInVertically { -40 },
                exit = fadeOut() + slideOutVertically { -40 }
            ) {
                Image(
                    painter = painterResource(id = R.drawable.vichaar), // Logo
                    contentDescription = "Logo",
                    modifier = Modifier
                        .size(100.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Welcome Back",
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.onBackground
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Sign in to continue",
                style = MaterialTheme.typography.bodyMedium,
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
                                Color(0xFFB4000A), // #b4000a
                                Color(0xFFFF6347)  // #ff6347
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

                                val retrofit = Retrofit.Builder()
                                    .baseUrl(clientUrl!!)
                                    .addConverterFactory(GsonConverterFactory.create())
                                    .build()
                                    .create(ApiService::class.java)


                                val res = retrofit.login(LoginRequest(user, pass))
                                Log.d("here i am 2", res.toString())
                                val name = res.data.firstOrNull()?.staf_nm
                                val staffData = res.data.firstOrNull()
                                if (res.status == "success" && name != null) {
                                    val staffData = res.data.firstOrNull()
                                    staffData?.let {
                                        vm.saveStaffDetails(it)  // Save all data to DataStore
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
                                    onLogin()
                                }else {
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
                        style = MaterialTheme.typography.labelLarge
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

    fun toggleTracking(context: Context, staffSl: String?) {
        val intent = Intent(context, MyForegroundService::class.java).apply {
            putExtra("staf_sl", staffSl)
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
                    IconButton(onClick = { toggleTracking(context, stafSl) }) {
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
            ExtendedFloatingActionButton(
                onClick = { /* SOS action */ },
                icon = { Icon(Icons.Default.Emergency, contentDescription = null) },
                text = { Text("SOS") }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
        ) {
            // Profile Card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
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
                    Text("Welcome,", style = MaterialTheme.typography.bodyLarge)
                    Text(username ?: "User", style = MaterialTheme.typography.headlineMedium)
                    Spacer(Modifier.height(8.dp))
                    Text(deptName ?: "", style = MaterialTheme.typography.bodyMedium)
                    Text(desgName ?: "", style = MaterialTheme.typography.bodyMedium)
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
                            onClick = { toggleTracking(context, stafSl) },
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(if (isTrackingActive) "Stop" else "Start")
                        }
                        Spacer(Modifier.width(16.dp))
                        Button(
                            onClick = { showLogoutDialog = true },
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Logout")
                        }
                    }
                }
            }

            if (locationHistory.isNotEmpty()) {
                Text(
                    text = "Recent Locations",
                    modifier = Modifier.padding(16.dp),
                    style = MaterialTheme.typography.titleMedium
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
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Medium
            )
        }
        Spacer(modifier = Modifier.height(4.dp))
        Row {
            Text(
                text = "Time: ",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
            )
            Text(
                text = location.time,
                style = MaterialTheme.typography.bodySmall
            )
        }
        Row {
            Text(
                text = "Coordinates: ",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
            )
            Text(
                text = "%.4f, %.4f".format(location.latitude, location.longitude),
                style = MaterialTheme.typography.bodySmall
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
                style = MaterialTheme.typography.bodyLarge
            )
            Text(
                text = location.time,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
            )
        }
        Text(
            text = "%.2f, %.2f".format(location.latitude, location.longitude),
            style = MaterialTheme.typography.labelSmall,
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
                Text("Logout", style = MaterialTheme.typography.labelLarge)
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
