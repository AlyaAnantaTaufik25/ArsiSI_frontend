package com.example.arsisi_frontend

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge // Pertahankan jika Anda ingin EdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface // Gunakan Surface, bukan Scaffold, sebagai kontainer utama
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController // Impor NavController
import com.example.arsisi_frontend.navigation.NavGraph // Impor NavGraph Anda
import com.example.arsisi_frontend.ui.theme.ArsiSI_frontendTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge() // Pertahankan jika Anda ingin efek EdgeToEdge
        setContent {
            ArsiSI_frontendTheme {
                // Gunakan Surface sebagai kontainer utama untuk seluruh aplikasi
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    // color = MaterialTheme.colorScheme.background // Opsional: atur warna background
                ) {
                    // 1. Buat NavController
                    val navController = rememberNavController()

                    // 2. Panggil NavGraph Anda sebagai Composable utama
                    NavGraph(navController = navController)
                }
            }
        }
    }
}

// Hapus atau abaikan fungsi Greeting dan GreetingPreview
// Karena sudah tidak dipakai oleh aplikasi utama.
// @Composable
// fun Greeting(name: String, modifier: Modifier = Modifier) { ... }
// @Preview(showBackground = true)
// @Composable
// fun GreetingPreview() { ... }