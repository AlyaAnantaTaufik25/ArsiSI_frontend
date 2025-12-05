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

data class AuthState(
    val isLoading: Boolean = false,
    val isSuccess: Boolean = false,
    val error: String? = null,
    val user: User? = null
)

class AuthViewModel(application: Application) : AndroidViewModel(application) {

    /** ✅ PAKAI INI - ApiClient.apiService (bukan getApiService()) */
    private val apiService = ApiClient.apiService
    private val prefsManager = PreferencesManager(application.applicationContext)

    private val _authState = MutableStateFlow(AuthState())
    val authState: StateFlow<AuthState> = _authState.asStateFlow()

    fun login(nim: String, password: String) {
        viewModelScope.launch {
            try {
                _authState.value = AuthState(isLoading = true)

                val request = LoginRequest(nim, password)
                val response = apiService.login(request)  // Response<AuthResponse>

                if (response.isSuccessful) {
                    val authResponse = response.body()
                    if (authResponse?.success == true && authResponse.data != null) {
                        val userData = authResponse.data!!  // UserData dari AuthResponse

                        // Simpan ke preferences
                        prefsManager.saveUserId(userData.id)
                        prefsManager.saveUserName(userData.nama)
                        prefsManager.saveUserNim(userData.nim)
                        prefsManager.saveUserEmail(userData.email)

                        // Buat User object
                        val user = User(
                            id = userData.id,
                            nim = userData.nim,
                            nama = userData.nama,
                            email = userData.email,
                            angkatan = userData.angkatan,
                            jurusan = userData.jurusan,
                            token = authResponse.token ?: ""  // Token dari AuthResponse
                        )

                        _authState.value = AuthState(
                            isLoading = false,
                            isSuccess = true,
                            user = user
                        )
                    } else {
                        _authState.value = AuthState(
                            isLoading = false,
                            error = authResponse?.message ?: "Login gagal"
                        )
                    }
                } else {
                    _authState.value = AuthState(
                        isLoading = false,
                        error = "Login gagal: ${response.code()}"
                    )
                }
            } catch (e: HttpException) {
                _authState.value = AuthState(
                    isLoading = false,
                    error = "Server error: ${e.code()}"
                )
            } catch (e: Exception) {
                Log.e("AuthViewModel", "Login error", e)
                _authState.value = AuthState(
                    isLoading = false,
                    error = "Gagal terhubung: ${e.message}"
                )
            }
        }
    }

    fun register(
        nim: String, nama: String, email: String, angkatan: String,
        jurusan: String, password: String
    ) {
        viewModelScope.launch {
            try {
                _authState.value = AuthState(isLoading = true)

                val request = RegisterRequest(
                    nim = nim, nama = nama, email = email,
                    angkatan = angkatan, jurusan = jurusan, password = password
                )
                val response = apiService.register(request)

                if (response.isSuccessful) {
                    val authResponse = response.body()
                    if (authResponse?.success == true) {
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
            _authState.value = AuthState()
        }
    }

    fun resetState() {
        _authState.value = AuthState()
    }
}
