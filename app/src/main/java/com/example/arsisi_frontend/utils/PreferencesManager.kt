package com.example.arsisi_frontend.utils

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import android.content.SharedPreferences
import com.example.arsisi_frontend.data.model.User
import kotlin.reflect.KParameter

// Definisikan DataStore Singleton di luar kelas
private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user_prefs")

/**
 * Kelas untuk mengelola penyimpanan data kecil lokal (User ID, Token, dan Detail User)
 * menggunakan DataStore Preferences.
 */
class PreferencesManager(context: Context) {

    private val dataStore = context.dataStore

    companion object {
        // ==================== KUNCI OTENTIKASI ====================
        val USER_ID_KEY = intPreferencesKey("user_id")
        val AUTH_TOKEN_KEY = stringPreferencesKey("auth_token")

        // Kunci untuk penentuan rute awal (Onboarding/Splash Check)
        val IS_FIRST_TIME_KEY = booleanPreferencesKey("is_first_time")

        // ==================== KUNCI DETAIL USER ====================
        val USER_NIM_KEY = stringPreferencesKey("user_nim")
        val USER_NAMA_KEY = stringPreferencesKey("user_nama")
        val USER_EMAIL_KEY = stringPreferencesKey("user_email")
        // Note: Tidak perlu IS_LOGGED_IN_KEY, karena kita cek token.
    }

    // ----------------------------------------------------
    // FUNGSI SET (SUSPEND)
    // ----------------------------------------------------

    // Dipanggil di AuthViewModel setelah login/register sukses
    suspend fun saveUserId(userId: Int) {
        dataStore.edit { preferences -> preferences[USER_ID_KEY] = userId }
    }

    suspend fun saveToken(token: String) {
        dataStore.edit { preferences -> preferences[AUTH_TOKEN_KEY] = token }
    }

    suspend fun saveUserName(name: String) {
        dataStore.edit { preferences -> preferences[USER_NAMA_KEY] = name }
    }

    suspend fun saveUserNim(nim: String) {
        dataStore.edit { preferences -> preferences[USER_NIM_KEY] = nim }
    }

    suspend fun saveUserEmail(email: String) {
        dataStore.edit { preferences -> preferences[USER_EMAIL_KEY] = email }
    }

    // Dipanggil saat selesai Onboarding/Login pertama
    suspend fun setFirstTime(isFirstTime: Boolean) {
        dataStore.edit { preferences ->
            preferences[IS_FIRST_TIME_KEY] = isFirstTime
        }
    }

    // ----------------------------------------------------
    // FUNGSI GET (SUSPEND - Menggunakan .first() untuk sekali ambil)
    // ----------------------------------------------------

    /** Mengembalikan status token. Digunakan untuk Auth Check. */
    suspend fun isLoggedIn(): Boolean {
        return dataStore.data.map { preferences ->
            // Status login ditentukan dari keberadaan token
            !preferences[AUTH_TOKEN_KEY].isNullOrEmpty()
        }.first() // Mengambil nilai pertama (current value) dan mengakhiri flow.
    }

    /** Mengembalikan status Onboarding/Splash Check. */
    suspend fun isFirstTime(): Boolean {
        return dataStore.data.map { preferences ->
            // Default value: true (berarti belum pernah dibuka)
            preferences[IS_FIRST_TIME_KEY] ?: true
        }.first()
    }

    /** Mengambil user ID (Contoh: untuk API calls selanjutnya) */
    suspend fun getUserId(): Int {
        return dataStore.data.map { preferences ->
            preferences[USER_ID_KEY] ?: -1
        }.first()
    }

    /** Mengambil Nama User (Contoh: untuk Dashboard) */
    suspend fun getUserName(): String? {
        return dataStore.data.map { preferences ->
            preferences[USER_NAMA_KEY]
        }.first()
    }

    /** Mengambil Token untuk Header API */
    suspend fun getToken(): String? {
        return dataStore.data.map { preferences ->
            preferences[AUTH_TOKEN_KEY]
        }.first()
    }

        @Volatile
        private var INSTANCE: PreferencesManager? = null

        fun getInstance(context: Context): PreferencesManager {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: PreferencesManager(context.applicationContext).also { INSTANCE = it }
            }
        }


    // ----------------------------------------------------
    // UTILITY
    // ----------------------------------------------------

    /** Menghapus semua data (Logout/clearAll). */
    suspend fun clearAll() {
        dataStore.edit { preferences ->
            preferences.clear()
        }
    }
}