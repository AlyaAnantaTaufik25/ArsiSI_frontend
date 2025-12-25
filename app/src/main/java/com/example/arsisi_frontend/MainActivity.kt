package com.example.arsisi_frontend

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.navigation.compose.rememberNavController
import com.example.arsisi_frontend.data.local.AppDatabase
import com.example.arsisi_frontend.data.repository.AkademikRepository
import com.example.arsisi_frontend.navigation.NavGraph
import com.example.arsisi_frontend.ui.theme.ArsiSI_frontendTheme
import com.example.arsisi_frontend.utils.PreferencesManager

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        try {
            // Inisialisasi PreferencesManager
            val preferencesManager = PreferencesManager(applicationContext)
            
            // Inisialisasi Database dan Repository
            val database = AppDatabase.getDatabase(applicationContext)
            val akademikRepository = AkademikRepository(database, preferencesManager, applicationContext)
            
            setContent {
                ArsiSI_frontendTheme {
                    val navController = rememberNavController()
                    
                    // Pass all dependencies to NavGraph
                    NavGraph(
                        navController = navController,
                        akademikRepository = akademikRepository,
                        application = application,
                        preferencesManager = preferencesManager
                    )
                }
            }
        } catch (e: Exception) {
            Log.e("MainActivity", "Error in onCreate: ${e.message}", e)
            e.printStackTrace()
        }
    }
}