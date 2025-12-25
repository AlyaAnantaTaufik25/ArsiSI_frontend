package com.example.arsisi_frontend.ui.splash

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.arsisi_frontend.utils.PreferencesManager
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * NavigationRoute untuk menentukan kemana user harus diarahkan
 */
sealed class NavigationRoute {
    object Idle : NavigationRoute()
    data class Navigate(val route: String) : NavigationRoute()
}

/**
 * SplashViewModel untuk check login status dan first time
 */
class SplashViewModel(application: Application) : AndroidViewModel(application) {

    private val prefsManager = PreferencesManager(application.applicationContext)

    private val _isFirstTimeState = MutableStateFlow(true)
    val isFirstTimeState: StateFlow<Boolean> = _isFirstTimeState.asStateFlow()

    private val _userNameState = MutableStateFlow<String?>(null)
    val userNameState: StateFlow<String?> = _userNameState.asStateFlow()

    private val _nextRoute = MutableStateFlow<NavigationRoute>(NavigationRoute.Idle)
    val nextRoute: StateFlow<NavigationRoute> = _nextRoute.asStateFlow()

    init {
        checkInitialRoute()
    }

    /**
     * Check apakah first time dan login status
     */
    private fun checkInitialRoute() {
        viewModelScope.launch {
            delay(1500) // Splash delay

            val isFirstTime = prefsManager.isFirstTime()
            val isLoggedIn = prefsManager.isLoggedIn()

            _isFirstTimeState.value = isFirstTime

            if (isFirstTime) {
                // First time -> stay di splash, akan show onboarding
                _nextRoute.value = NavigationRoute.Navigate("splash")
            } else if (isLoggedIn) {
                // Sudah pernah login -> Dashboard
                val userName = prefsManager.getUserNama()
                _userNameState.value = userName
                _nextRoute.value = NavigationRoute.Navigate("dashboard")
            } else {
                // Bukan first time, tapi belum login -> Login
                _nextRoute.value = NavigationRoute.Navigate("login")
            }
        }
    }
}
