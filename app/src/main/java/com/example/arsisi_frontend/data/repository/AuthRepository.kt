package com.example.arsisi_frontend.data.repository

import com.example.arsisi_frontend.data.remote.ApiConfig
import com.example.arsisi_frontend.data.remote.dto.LoginRequest
import com.example.arsisi_frontend.utils.PreferencesManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class AuthRepository(
    private val preferencesManager: PreferencesManager
) {
    
    suspend fun login(email: String, password: String): Result<String> = withContext(Dispatchers.IO) {
        try {
            val apiService = ApiConfig.getApiService()
            val response = apiService.login(LoginRequest(email, password))
            
            // Save token
            preferencesManager.saveToken(response.token)
            
            // Save login state (keep mock user data for now since backend doesn't return user object)
            preferencesManager.saveLoginState(
                isLoggedIn = true,
                nim = email.substringBefore("@"), // Extract from email as fallback
                nama = "User", // Placeholder
                email = email,
                angkatan = "2021" // Placeholder
            )
            
            Result.success(response.token)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun register(
        nim: String,
        nama: String,
        email: String,
        password: String
    ): Result<String> = withContext(Dispatchers.IO) {
        try {
            val apiService = ApiConfig.getApiService()
            val response = apiService.register(
                com.example.arsisi_frontend.data.remote.dto.RegisterRequest(
                    nim = nim,
                    nama = nama,
                    email = email,
                    password = password
                )
            )
            
            if (response.success) {
                Result.success(response.message)
            } else {
                Result.failure(Exception(response.message))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun logout(): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            // Clear local data
            preferencesManager.clearToken()
            preferencesManager.logout()
            
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
