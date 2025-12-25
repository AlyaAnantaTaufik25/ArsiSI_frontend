package com.example.arsisi_frontend.utils

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

// Extension untuk DataStore
private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user_prefs")

/**
 * PreferencesManager untuk menyimpan state login dan user data (mock authentication)
 * Phase 1: Tidak ada token, hanya simpan login state
 */
class PreferencesManager(private val context: Context) {

    private val dataStore = context.dataStore

    companion object {
        // Keys untuk preferences
        private val IS_LOGGED_IN_KEY = booleanPreferencesKey("is_logged_in")
        private val IS_FIRST_TIME_KEY = booleanPreferencesKey("is_first_time")
        private val USER_NIM_KEY = stringPreferencesKey("user_nim")
        private val USER_NAMA_KEY = stringPreferencesKey("user_nama")
        private val USER_EMAIL_KEY = stringPreferencesKey("user_email")
        private val USER_ANGKATAN_KEY = stringPreferencesKey("user_angkatan")
        private val AUTH_TOKEN_KEY = stringPreferencesKey("auth_token") // Phase 2: JWT token
    }

    // ==================== SAVE FUNCTIONS ====================

    suspend fun saveLoginState(
        isLoggedIn: Boolean,
        nim: String = "",
        nama: String = "",
        email: String = "",
        angkatan: String = ""
    ) {
        dataStore.edit { preferences ->
            preferences[IS_LOGGED_IN_KEY] = isLoggedIn
            if (isLoggedIn) {
                preferences[USER_NIM_KEY] = nim
                preferences[USER_NAMA_KEY] = nama
                preferences[USER_EMAIL_KEY] = email
                preferences[USER_ANGKATAN_KEY] = angkatan
            }
        }
    }

    suspend fun setFirstTime(isFirstTime: Boolean) {
        dataStore.edit { preferences ->
            preferences[IS_FIRST_TIME_KEY] = isFirstTime
        }
    }

    // ==================== GET FUNCTIONS ====================

    suspend fun isLoggedIn(): Boolean {
        return dataStore.data.map { preferences ->
            preferences[IS_LOGGED_IN_KEY] ?: false
        }.first()
    }

    suspend fun isFirstTime(): Boolean {
        return dataStore.data.map { preferences ->
            preferences[IS_FIRST_TIME_KEY] ?: true
        }.first()
    }

    suspend fun getUserNim(): String {
        return dataStore.data.map { preferences ->
            preferences[USER_NIM_KEY] ?: ""
        }.first()
    }

    suspend fun getUserNama(): String {
        return dataStore.data.map { preferences ->
            preferences[USER_NAMA_KEY] ?: "Pengguna"
        }.first()
    }

    suspend fun getUserEmail(): String {
        return dataStore.data.map { preferences ->
            preferences[USER_EMAIL_KEY] ?: ""
        }.first()
    }

    suspend fun getUserAngkatan(): String {
        return dataStore.data.map { preferences ->
            preferences[USER_ANGKATAN_KEY] ?: ""
        }.first()
    }

    // ==================== UTILITY ====================

    suspend fun clearAll() {
        dataStore.edit { preferences ->
            preferences.clear()
        }
    }

    suspend fun logout() {
        dataStore.edit { preferences ->
            preferences[IS_LOGGED_IN_KEY] = false
            // Keep first time flag, clear user data
            preferences.remove(USER_NIM_KEY)
            preferences.remove(USER_NAMA_KEY)
            preferences.remove(USER_EMAIL_KEY)
            preferences.remove(USER_ANGKATAN_KEY)
        }
    }

    // ==================== TOKEN MANAGEMENT (Phase 2) ====================
    
    suspend fun saveToken(token: String) {
        dataStore.edit { preferences ->
            preferences[AUTH_TOKEN_KEY] = token
        }
    }
    
    suspend fun getToken(): String? {
        return dataStore.data.map { preferences ->
            preferences[AUTH_TOKEN_KEY]
        }.first()
    }
    
    suspend fun clearToken() {
        dataStore.edit { preferences ->
            preferences.remove(AUTH_TOKEN_KEY)
        }
    }
}
