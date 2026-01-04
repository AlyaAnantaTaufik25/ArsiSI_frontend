package com.example.arsisi_frontend.utils
import android.content.Context
import android.content.SharedPreferences
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import com.example.arsisi_frontend.data.model.User
private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user_prefs")
class PreferencesManager(context: Context) {
    private val PREFS_NAME = "arsisi_prefs"
    private val KEY_AUTH_TOKEN = "auth_token"
    private val DEBUG_TOKEN_VALUE = "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJtYWhhc2lzd2EiOnsiaWQiOjEsImVtYWlsIjoibGFuaUBjb250b2guY29tIiwibmFtYSI6IkxlaWxsYW5pIE5hd3dpcmEifSwiaWF0IjoxNzY2NDcxMzE5LCJleHAiOjE3NjY1NTc3MTl9.gFAyiTsmrMhUD85aHGldyjd0fEcYHmsKvGQjcuFrCO8"
    private val dataStore = context.dataStore
    private val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    companion object {
        val USER_ID_KEY = intPreferencesKey("user_id")
        val AUTH_TOKEN_KEY = stringPreferencesKey("auth_token")
        val IS_FIRST_TIME_KEY = booleanPreferencesKey("is_first_time")
        val USER_NIM_KEY = stringPreferencesKey("user_nim")
        val USER_NAMA_KEY = stringPreferencesKey("user_nama")
        val USER_EMAIL_KEY = stringPreferencesKey("user_email")
        @Volatile
        private var INSTANCE: PreferencesManager? = null
        fun getInstance(context: Context): PreferencesManager {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: PreferencesManager(context.applicationContext).also { INSTANCE = it }
            }
        }
    }
    fun saveAuthToken(token: String) {
        prefs.edit().putString(KEY_AUTH_TOKEN, token).apply()
    }
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
    suspend fun setFirstTime(isFirstTime: Boolean) {
        dataStore.edit { preferences ->
            preferences[IS_FIRST_TIME_KEY] = isFirstTime
        }
    }
    fun getAuthToken(): String? {
        val storedToken = prefs.getString(KEY_AUTH_TOKEN, null)
        return storedToken ?: DEBUG_TOKEN_VALUE
    }
    suspend fun isLoggedIn(): Boolean {
        return false
    }
    suspend fun isFirstTime(): Boolean {
        return dataStore.data.map { preferences ->
            preferences[IS_FIRST_TIME_KEY] ?: true
        }.first()
    }
    suspend fun getUserId(): Int {
        return dataStore.data.map { preferences ->
            preferences[USER_ID_KEY] ?: -1
        }.first()
    }
    suspend fun getUserName(): String? {
        return dataStore.data.map { preferences ->
            preferences[USER_NAMA_KEY]
        }.first()
    }
    suspend fun getToken(): String? {
        return dataStore.data.map { preferences ->
            preferences[AUTH_TOKEN_KEY]
        }.first()
    }
    fun clearData() {
        prefs.edit().clear().apply()
    }
    suspend fun clearAll() {
        dataStore.edit { preferences ->
            preferences.clear()
        }
    }
}