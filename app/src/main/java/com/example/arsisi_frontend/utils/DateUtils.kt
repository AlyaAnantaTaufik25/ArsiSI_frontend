package com.example.arsisi_frontend.utils


import java.text.SimpleDateFormat
import java.util.*

object DateUtils {
    private const val API_FORMAT = "yyyy-MM-dd"
    private const val DISPLAY_FORMAT = "dd-MM-yyyy"

    fun getCurrentDateApi(): String {
        return SimpleDateFormat(API_FORMAT, Locale.getDefault()).format(Date())
    }

    fun formatDateForDisplay(apiDate: String): String {
        return try {
            val date = SimpleDateFormat(API_FORMAT, Locale.getDefault()).parse(apiDate)
            date?.let { SimpleDateFormat(DISPLAY_FORMAT, Locale.getDefault()).format(it) } ?: ""
        } catch (e: Exception) {
            apiDate // Kembalikan format asli jika gagal parsing
        }
    }

    fun parseApiDate(apiDate: String): Date? {
        return try {
            SimpleDateFormat(API_FORMAT, Locale.getDefault()).parse(apiDate)
        } catch (e: Exception) {
            null
        }
    }

    fun timestampToApiFormat(timestamp: Long): String {
        return SimpleDateFormat(API_FORMAT, Locale.getDefault()).format(Date(timestamp))
    }
}