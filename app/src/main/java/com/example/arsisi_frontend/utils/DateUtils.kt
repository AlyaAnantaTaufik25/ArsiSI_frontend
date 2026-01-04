package com.example.arsisi_frontend.utils
import java.text.SimpleDateFormat
import java.util.*
object DateUtils {
    private val dateFormat by lazy {
        ThreadLocal.withInitial { SimpleDateFormat("dd MMMM yyyy", Locale("id", "ID")) }
    }
    private val shortDateFormat by lazy {
        ThreadLocal.withInitial { SimpleDateFormat("dd MMM yyyy", Locale("id", "ID")) }
    }
    private val dateTimeFormat by lazy {
        ThreadLocal.withInitial { SimpleDateFormat("dd MMM yyyy HH:mm", Locale("id", "ID")) }
    }
    fun formatDate(timestamp: Long): String {
        return dateFormat.get().format(Date(timestamp))
    }
    fun formatShortDate(timestamp: Long): String {
        return shortDateFormat.get().format(Date(timestamp))
    }
    fun formatDateTime(timestamp: Long): String {
        return dateTimeFormat.get().format(Date(timestamp))
    }
    fun parseDate(dateString: String): Long {
        return try {
            dateFormat.get().parse(dateString)?.time ?: System.currentTimeMillis()
        } catch (e: Exception) {
            System.currentTimeMillis()
        }
    }
    fun getCurrentTimestamp(): Long = System.currentTimeMillis()
    fun formatYearOnly(year: Int): String = year.toString()
    fun yearToTimestamp(year: Int): Long {
        val calendar = Calendar.getInstance(Locale("id", "ID")).apply {
            set(Calendar.YEAR, year)
            set(Calendar.MONTH, 0)
            set(Calendar.DAY_OF_MONTH, 1)
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        return calendar.timeInMillis
    }
    val CURRENT_YEAR: Int = Calendar.getInstance().get(Calendar.YEAR)
    fun formatDateIndonesian(dateString: String?): String {
        if (dateString.isNullOrEmpty()) return "Tidak ada tanggal"
        return try {
            val inputFormat = SimpleDateFormat("yyyy-MM-dd", Locale("id", "ID"))
            val date = inputFormat.parse(dateString)
            if (date != null) {
                dateFormat.get().format(date)
            } else {
                dateString
            }
        } catch (e: Exception) {
            dateString
        }
    }
    fun formatDateIndonesianShort(dateString: String?): String {
        if (dateString.isNullOrEmpty()) return ""
        return try {
            val inputFormat = SimpleDateFormat("yyyy-MM-dd", Locale("id", "ID"))
            val date = inputFormat.parse(dateString)
            if (date != null) {
                shortDateFormat.get().format(date)
            } else {
                dateString
            }
        } catch (e: Exception) {
            dateString
        }
    }
    fun formatDateTimeIndonesian(dateTimeString: String?): String {
        if (dateTimeString.isNullOrEmpty()) return ""
        return try {
            val inputFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale("id", "ID"))
            val date = inputFormat.parse(dateTimeString)
            if (date != null) {
                dateTimeFormat.get().format(date)
            } else {
                try {
                    val inputFormat2 = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale("id", "ID"))
                    val date2 = inputFormat2.parse(dateTimeString)
                    if (date2 != null) {
                        dateTimeFormat.get().format(date2)
                    } else {
                        dateTimeString
                    }
                } catch (e2: Exception) {
                    dateTimeString
                }
            }
        } catch (e: Exception) {
            dateTimeString
        }
    }
    fun getCurrentDateTimeIndonesian(): String {
        return dateTimeFormat.get().format(Date())
    }
    fun getCurrentDateIndonesian(): String {
        return dateFormat.get().format(Date())
    }
}