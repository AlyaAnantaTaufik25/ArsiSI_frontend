package com.example.arsisi_frontend.ui.splash

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.arsisi_frontend.utils.PreferencesManager
import com.example.arsisi_frontend.navigation.Screen // Diperlukan untuk rute
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

// Sealed class untuk mengelola state navigasi
sealed class NavigationRoute {
    object Loading : NavigationRoute()
    data class Navigate(val route: String) : NavigationRoute()
}

class SplashViewModel(application: Application) : AndroidViewModel(application) {

    // Asumsi PreferencesManager menerima Application context
    private val prefsManager = PreferencesManager(application)

    // State untuk menentukan rute navigasi awal (Diperbarui setelah data I/O siap)
    private val _nextRoute = MutableStateFlow<NavigationRoute>(NavigationRoute.Loading)
    val nextRoute: StateFlow<NavigationRoute> = _nextRoute.asStateFlow()

    // State untuk menentukan apakah Onboarding perlu ditampilkan
    private val _isFirstTimeState = MutableStateFlow(true)
    val isFirstTimeState: StateFlow<Boolean> = _isFirstTimeState.asStateFlow()

    // State untuk menyimpan nama user
    private val _userNameState = MutableStateFlow<String?>("Pengguna")
    val userNameState: StateFlow<String?> = _userNameState.asStateFlow()

    init {
        checkUserSession()
    }

    /**
     * Memeriksa sesi pengguna (login status dan status onboarding).
     * Semua operasi I/O (membaca preferensi) dilakukan di Dispatchers.IO.
     */
    private fun checkUserSession() {
        viewModelScope.launch {

            // Definisikan tipe Pair untuk mengembalikan semua data dari background thread
            val (data, route) = withContext(Dispatchers.IO) {

                // 1. Operasi I/O
                val isLoggedIn = prefsManager.isLoggedIn()
                val isFirstTime = prefsManager.isFirstTime()
                val userName = prefsManager.getUserName()

                // 2. Jeda
                delay(1000)

                // 3. Penentuan Rute
                val finalRoute = when {
                    isLoggedIn -> Screen.Dashboard.route
                    isFirstTime -> Screen.Register.route
                    else -> Screen.Login.route
                }

                // Kembalikan Tiga data user dan Satu data rute sebagai Pair
                // Pair<Triple<Boolean, Boolean, String>, String>
                Triple(isLoggedIn, isFirstTime, userName) to finalRoute
            }

            // 4. Lakukan Destructuring TAHAP KEDUA di Main Thread
            // data adalah Triple yang berisi 3 nilai. route adalah String.
            val (isLoggedIn, isFirstTime, userName) = data

            // 5. Update StateFlows di Main Thread
            _isFirstTimeState.value = isFirstTime
            _userNameState.value = userName

            // 6. Update rute final untuk memicu navigasi di NavHost
            _nextRoute.value = NavigationRoute.Navigate(route)
        }
    }
}