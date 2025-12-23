package com.example.arsisi_frontend.utils

import java.text.SimpleDateFormat
import java.util.*

/**
 * Modern DateUtils - Thread-safe + Android best practices
 */
object DateUtils {

    // ✅ FIX: ThreadLocal untuk SimpleDateFormat (thread-safe)
    private val dateFormat by lazy {
        ThreadLocal.withInitial { SimpleDateFormat("dd MMMM yyyy", Locale("id", "ID")) }
    }

    private val shortDateFormat by lazy {
        ThreadLocal.withInitial { SimpleDateFormat("dd MMM yyyy", Locale("id", "ID")) }
    }

    private val dateTimeFormat by lazy {
        ThreadLocal.withInitial { SimpleDateFormat("dd MMM yyyy HH:mm", Locale("id", "ID")) }
    }

    /**
     * Format lengkap: "17 Desember 2025"
     */
    fun formatDate(timestamp: Long): String {
        return dateFormat.get().format(Date(timestamp))
    }

    /**
     * Format pendek: "17 Des 2025"
     */
    fun formatShortDate(timestamp: Long): String {
        return shortDateFormat.get().format(Date(timestamp))
    }

    /**
     * Format date + time: "17 Des 2025 14:30"
     */
    fun formatDateTime(timestamp: Long): String {
        return dateTimeFormat.get().format(Date(timestamp))
    }

    /**
     * Parse string ke timestamp
     */
    fun parseDate(dateString: String): Long {
        return try {
            dateFormat.get().parse(dateString)?.time ?: System.currentTimeMillis()
        } catch (e: Exception) {
            System.currentTimeMillis()
        }
    }

    /**
     * Get current timestamp
     */
    fun getCurrentTimestamp(): Long = System.currentTimeMillis()

    /**
     * Format untuk tahun saja (Prestasi)
     */
    fun formatYearOnly(year: Int): String = year.toString()

    /**
     * Convert tahun ke timestamp awal tahun
     */
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

    // ✅ FIX: Konstanta langsung di object (NO companion)
    val CURRENT_YEAR: Int = Calendar.getInstance().get(Calendar.YEAR)
}
