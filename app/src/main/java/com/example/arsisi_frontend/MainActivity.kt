package com.example.arsisi_frontend

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.navigation.compose.rememberNavController
import com.example.arsisi_frontend.navigation.NavGraph // Pastikan import NavGraph kamu benar
import com.example.arsisi_frontend.ui.theme.ArsiSI_frontendTheme // Sesuaikan dengan nama tema projectmu

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            // Gunakan tema aplikasi kamu
            ArsiSI_frontendTheme {
                // Inisialisasi NavController
                val navController = rememberNavController()

                // Panggil NavGraph utama yang sudah kita buat
                NavGraph(navController = navController)
            }
        }
    }
}