package com.example.arsisi_frontend.ui.auth

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.arsisi_frontend.data.model.*
import com.example.arsisi_frontend.data.remote.ApiClient
import com.example.arsisi_frontend.utils.PreferencesManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import retrofit2.HttpException
import kotlinx.coroutines.withTimeoutOrNull
import kotlinx.coroutines.TimeoutCancellationException


data class AuthState(
    val isLoading: Boolean = false,
    val isSuccess: Boolean = false,
    val error: String? = null,
    val user: User? = null
)

class AuthViewModel(application: Application) : AndroidViewModel(application) {

    private val apiService = ApiClient.apiService
    private val prefsManager = PreferencesManager(application.applicationContext)

    private val _authState = MutableStateFlow(AuthState())
    val authState: StateFlow<AuthState> = _authState.asStateFlow()

    fun login(nim: String, password: String) {
        Log.d("AuthViewModel", "🚀 LOGIN START: nim=$nim")
        viewModelScope.launch {
            try {
                _authState.value = AuthState(isLoading = true)
                Log.d("AuthViewModel", "📡 API CALL")

                val request = LoginRequest(nim, password)

                // ✅ TIMEOUT 15 detik + SUPERVISION
                val response = withTimeoutOrNull(15000) {
                    apiService.login(request)
                } ?: throw Exception("Timeout: Server tidak merespon")

                Log.d("AuthViewModel", "📥 RESPONSE: ${response.code()}, body=${response.body()}")

                if (response.isSuccessful) {
                    val authResponse = response.body()
                    if (authResponse?.success == true && authResponse.data != null) {
                        val userData = authResponse.data!!

                        // ✅ SAVE SEKALIGUS di 1 coroutine, bukan nested
                        prefsManager.saveUserId(userData.id)
                        prefsManager.saveToken(authResponse.token ?: "")
                        prefsManager.saveUserName(userData.nama)
                        prefsManager.saveUserNim(userData.nim)
                        prefsManager.saveUserEmail(userData.email)

                        val user = User(
                            id = userData.id, nim = userData.nim, nama = userData.nama,
                            email = userData.email, angkatan = userData.angkatan,
                            token = authResponse.token ?: ""
                        )

                        _authState.value = AuthState(isLoading = false, isSuccess = true, user = user)
                    } else {
                        _authState.value = AuthState(isLoading = false, error = authResponse?.message ?: "Login gagal")
                    }
                } else {
                    _authState.value = AuthState(isLoading = false, error = "HTTP ${response.code()}: ${response.message()}")
                }
            } catch (e: Exception) {
                Log.e("AuthViewModel", "💥 LOGIN FAILED", e)
                _authState.value = AuthState(isLoading = false, error = "Gagal login: ${e.message}")
            }
        }
    }


    fun register(
        nim: String, nama: String, email: String, angkatan: String,
        password: String
    ) {
        viewModelScope.launch {
            try {
                _authState.value = AuthState(isLoading = true)
                Log.d("AuthViewModel", "📝 REGISTER: $nim")

                val request = RegisterRequest(
                    nim = nim, nama = nama, email = email,
                    angkatan = angkatan, password = password
                )
                val response = apiService.register(request)

                if (response.isSuccessful) {
                    val authResponse = response.body()
                    if (authResponse?.success == true) {
                        Log.d("AuthViewModel", "🎉 REGISTER SUCCESS")
                        _authState.value = AuthState(
                            isLoading = false,
                            isSuccess = true
                        )
                    } else {
                        _authState.value = AuthState(
                            isLoading = false,
                            error = authResponse?.message ?: "Registrasi gagal"
                        )
                    }
                } else {
                    _authState.value = AuthState(
                        isLoading = false,
                        error = "Registrasi gagal: ${response.code()}"
                    )
                }
            } catch (e: HttpException) {
                _authState.value = AuthState(
                    isLoading = false,
                    error = "Server error: ${e.code()}"
                )
            } catch (e: Exception) {
                Log.e("AuthViewModel", "Register error", e)
                _authState.value = AuthState(
                    isLoading = false,
                    error = "Gagal terhubung: ${e.message}"
                )
            }
        }
    }

    fun logout() {
        viewModelScope.launch {
            prefsManager.clearAll()
            Log.d("AuthViewModel", "🔓 LOGOUT - All data cleared")
            _authState.value = AuthState()
        }
    }

    fun resetState() {
        _authState.value = AuthState()
    }

    // ✅ BONUS: Check login status (untuk Navigation)
    suspend fun isUserLoggedIn(): Boolean {
        val userId = prefsManager.getUserId()
        val token = prefsManager.getToken()
        Log.d("AuthViewModel", "🔍 Login check: userId=$userId, hasToken=${token != null}")
        return userId != -1 && token != null
    }
}
