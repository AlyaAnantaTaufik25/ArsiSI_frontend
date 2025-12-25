package com.example.arsisi_frontend.ui.auth

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.arsisi_frontend.data.model.User
import com.example.arsisi_frontend.data.repository.AuthRepository
import com.example.arsisi_frontend.utils.PreferencesManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * AuthState untuk UI
 */
data class AuthState(
    val isLoading: Boolean = false,
    val isSuccess: Boolean = false,
    val error: String? = null,
    val user: User? = null
)

/**
 * AuthViewModel for authentication with real API (Phase 2)
 */
class AuthViewModel(application: Application) : AndroidViewModel(application) {

    private val prefsManager = PreferencesManager(application.applicationContext)
    private val authRepository = AuthRepository(prefsManager)

    private val _authState = MutableStateFlow(AuthState())
    val authState: StateFlow<AuthState> = _authState.asStateFlow()

    /**
     * Real Login - call backend API
     */
    fun login(email: String, password: String) {
        viewModelScope.launch {
            try {
                _authState.value = AuthState(isLoading = true)
                
                // Validation
                if (email.isBlank() || password.isBlank()) {
                    _authState.value = AuthState(
                        isLoading = false,
                        error = "Email dan Password tidak boleh kosong"
                    )
                    return@launch
                }
                
                // Call real API
                val result = authRepository.login(email, password)
                
                if (result.isSuccess) {
                    // Success - create user object
                    val mockUser = User(
                        id = 1,
                        nim = email.substringBefore("@"),
                        nama = "User",
                        email = email,
                        angkatan = "2021"
                    )
                    
                    _authState.value = AuthState(
                        isLoading = false,
                        isSuccess = true,
                        user = mockUser
                    )
                } else {
                    // API error
                    _authState.value = AuthState(
                        isLoading = false,
                        error = result.exceptionOrNull()?.message ?: "Login gagal"
                    )
                }
                
            } catch (e: Exception) {
                _authState.value = AuthState(
                    isLoading = false,
                    error = "Login gagal: ${e.message}"
                )
            }
        }
    }

    /**
     * Register - now connected to backend
     */
    fun register(
        nim: String,
        nama: String,
        email: String,
        angkatan: String,
        password: String
    ) {
        viewModelScope.launch {
            try {
                _authState.value = AuthState(isLoading = true)
                
                // Validation
                if (nim.isBlank() || nama.isBlank() || email.isBlank() || password.isBlank()) {
                    _authState.value = AuthState(
                        isLoading = false,
                        error = "Semua field harus diisi"
                    )
                    return@launch
                }
                
                // Call backend API
                val result = authRepository.register(nim, nama, email, password)
                
                if (result.isSuccess) {
                    _authState.value = AuthState(
                        isLoading = false,
                        isSuccess = true
                    )
                } else {
                    _authState.value = AuthState(
                        isLoading = false,
                        error = result.exceptionOrNull()?.message ?: "Registrasi gagal"
                    )
                }
                
            } catch (e: Exception) {
                _authState.value = AuthState(
                    isLoading = false,
                    error = "Registrasi gagal: ${e.message}"
                )
            }
        }
    }

    fun logout() {
        viewModelScope.launch {
            authRepository.logout()
            resetState()
        }
    }

    fun resetState() {
        _authState.value = AuthState()
    }
}
