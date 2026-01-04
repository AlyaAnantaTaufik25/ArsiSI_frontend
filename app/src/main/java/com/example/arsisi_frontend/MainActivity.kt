package com.example.arsisi_frontend
import android.Manifest
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.arsisi_frontend.ui.navigation.Screen
import com.example.arsisi_frontend.ui.auth.AuthViewModel
import com.example.arsisi_frontend.ui.auth.LoginScreen
import com.example.arsisi_frontend.ui.auth.RegisterScreen
import com.example.arsisi_frontend.ui.dashboard.DashboardScreen
import com.example.arsisi_frontend.ui.dashboard.DashboardViewModel
import com.example.arsisi_frontend.ui.splash.SplashScreen
import com.example.arsisi_frontend.ui.theme.ArsiSI_frontendTheme
import com.example.arsisi_frontend.ui.navigation.AppNavGraph
import com.example.arsisi_frontend.utils.NotificationHelper
import com.example.arsisi_frontend.data.remote.ApiClient
class MainActivity : ComponentActivity() {
    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            android.util.Log.d("MainActivity", "✅ Notification permission granted")
        } else {
            android.util.Log.w("MainActivity", "⚠️ Notification permission denied")
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        NotificationHelper.createNotificationChannel(this)
        ApiClient.getApiService(this)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
        }
        enableEdgeToEdge()
        setContent {
            ArsiSI_frontendTheme {
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
private fun AppNavigation() {
    val navController = rememberNavController()
    AppNavGraph(navController = navController)
}