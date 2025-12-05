package com.example.arsisi_frontend.utils

import android.content.Context

class PreferencesManager(context: Context) {

    private val PREFS_NAME = "arsisi_prefs"
    private val KEY_AUTH_TOKEN = "auth_token"
    private val DEBUG_TOKEN_VALUE = "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJtYWhhc2lzd2EiOnsiaWQiOjEsImVtYWlsIjoibGFuaUBjb250b2guY29tIiwibmFtYSI6IkxlaWxsYW5pIE5hd3dpcmEifSwiaWF0IjoxNzY0OTI2MzQ5LCJleHAiOjE3NjUwMTI3NDl9.b6KM1VPnPhPp3kZjWdnyf9lB1Y17bY79HUD2NHI6yUY"

    private val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    fun saveAuthToken(token: String) {
        prefs.edit().putString(KEY_AUTH_TOKEN, token).apply()
    }

    fun getAuthToken(): String? {
        val storedToken = prefs.getString(KEY_AUTH_TOKEN, null)

        // --- INJEKSI TOKEN SEMENTARA ---
        return storedToken ?: DEBUG_TOKEN_VALUE
    }

    fun clearData() {
        prefs.edit().clear().apply()
    }
}