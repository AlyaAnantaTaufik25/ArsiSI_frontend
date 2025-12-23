package com.example.arsisi_frontend

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.lifecycleScope
import androidx.navigation.compose.rememberNavController
import com.example.arsisi_frontend.data.local.AppDatabase
import com.example.arsisi_frontend.ui.navigation.AppNavGraph
import com.example.arsisi_frontend.ui.theme.ArsiSI_frontendTheme
import com.example.arsisi_frontend.utils.PermissionHelper
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        createNotificationChannel()

        if (!PermissionHelper.checkNotificationPermission(this)) {
            PermissionHelper.requestNotificationPermission(this, 101)
        }

        lifecycleScope.launch {
            try {
                AppDatabase.getDatabase(applicationContext)
                Log.d("MainActivity", "Database siap")
            } catch (e: Exception) {
                Log.e("MainActivity", "Database error: ${e.message}")
            }
        }

        setContent {
            ArsiSI_frontendTheme {
                AplikasiArsisi() // Sekarang ini sudah tidak merah lagi
            }
        }
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "Notifikasi Tugas"
            val channel = NotificationChannel(
                "CHANNEL_TUGAS",
                name,
                NotificationManager.IMPORTANCE_DEFAULT
            )
            val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            manager.createNotificationChannel(channel)
        }
    }
}

// FUNGSI INI HARUS ADA DI SINI AGAR BISA DIPANGGIL DI SETCONTENT
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AplikasiArsisi() {
    val navController = rememberNavController()
    Scaffold { innerPadding ->
        AppNavGraph(
            navController = navController,
            modifier = Modifier.padding(innerPadding)
        )
    }
}