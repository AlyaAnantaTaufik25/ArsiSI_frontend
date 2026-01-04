package com.example.arsisi_frontend.utils
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import com.example.arsisi_frontend.receiver.AlarmReceiver
import java.text.SimpleDateFormat
import java.util.*
class ReminderScheduler(private val context: Context) {
    private val alarmManager: AlarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
    companion object {
        private const val TAG = "ReminderScheduler"
    }
    fun scheduleReminder(
        agendaId: Int,
        title: String,
        description: String,
        date: String,
        time: String,
        reminderSetting: String?
    ) {
        if (reminderSetting.isNullOrBlank()) {
            android.util.Log.d(TAG, "No reminder setting for agenda $agendaId, skipping schedule")
            return
        }
        val agendaTimeMillis = parseAgendaDateTime(date, time)
        if (agendaTimeMillis == null) {
            android.util.Log.e(TAG, "Failed to parse date/time for agenda $agendaId: $date $time")
            return
        }
        val offsetMillis = parseReminderSettingToMillis(reminderSetting)
        if (offsetMillis == 0L) {
            android.util.Log.e(TAG, "Invalid reminder setting: $reminderSetting")
            return
        }
        val alarmTimeMillis = agendaTimeMillis - offsetMillis
        val currentTimeMillis = System.currentTimeMillis()
        if (alarmTimeMillis <= currentTimeMillis) {
            android.util.Log.w(TAG, "Alarm time already passed for agenda $agendaId, skipping schedule")
            return
        }
        val intent = Intent(context, AlarmReceiver::class.java).apply {
            putExtra(AlarmReceiver.EXTRA_AGENDA_ID, agendaId)
            putExtra(AlarmReceiver.EXTRA_AGENDA_TITLE, title)
            putExtra(AlarmReceiver.EXTRA_AGENDA_DESCRIPTION, description)
            putExtra(AlarmReceiver.EXTRA_AGENDA_TIME, time)
        }
        val pendingIntentFlags = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        } else {
            PendingIntent.FLAG_UPDATE_CURRENT
        }
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            agendaId,
            intent,
            pendingIntentFlags
        )
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                if (alarmManager.canScheduleExactAlarms()) {
                    alarmManager.setExactAndAllowWhileIdle(
                        AlarmManager.RTC_WAKEUP,
                        alarmTimeMillis,
                        pendingIntent
                    )
                } else {
                    alarmManager.set(
                        AlarmManager.RTC_WAKEUP,
                        alarmTimeMillis,
                        pendingIntent
                    )
                    android.util.Log.w(TAG, "Cannot schedule exact alarm, using inexact alarm for agenda $agendaId")
                }
            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                alarmManager.setExactAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP,
                    alarmTimeMillis,
                    pendingIntent
                )
            } else {
                alarmManager.setExact(
                    AlarmManager.RTC_WAKEUP,
                    alarmTimeMillis,
                    pendingIntent
                )
            }
            val alarmCalendar = Calendar.getInstance().apply { timeInMillis = alarmTimeMillis }
            android.util.Log.d(TAG, "Reminder scheduled for agenda $agendaId at ${SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).format(alarmCalendar.time)}")
        } catch (e: SecurityException) {
            android.util.Log.e(TAG, "SecurityException scheduling alarm: ${e.message}", e)
        } catch (e: Exception) {
            android.util.Log.e(TAG, "Error scheduling alarm: ${e.message}", e)
        }
    }
    fun cancelReminder(agendaId: Int) {
        val intent = Intent(context, AlarmReceiver::class.java)
        val pendingIntentFlags = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        } else {
            PendingIntent.FLAG_UPDATE_CURRENT
        }
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            agendaId,
            intent,
            pendingIntentFlags
        )
        alarmManager.cancel(pendingIntent)
        pendingIntent.cancel()
        android.util.Log.d(TAG, "Reminder cancelled for agenda $agendaId")
    }
    private fun parseAgendaDateTime(date: String, time: String): Long? {
        return try {
            val calendar = Calendar.getInstance()
            val dateParsed = parseDate(date)
            if (dateParsed == null) {
                android.util.Log.e(TAG, "Failed to parse date: $date")
                return null
            }
            val dateCalendar = Calendar.getInstance().apply { this.time = dateParsed }
            calendar.set(Calendar.YEAR, dateCalendar.get(Calendar.YEAR))
            calendar.set(Calendar.MONTH, dateCalendar.get(Calendar.MONTH))
            calendar.set(Calendar.DAY_OF_MONTH, dateCalendar.get(Calendar.DAY_OF_MONTH))
            if (time.isNotBlank() && time.contains(":")) {
                val timeParts = time.split(":")
                if (timeParts.size >= 2) {
                    calendar.set(Calendar.HOUR_OF_DAY, timeParts[0].toIntOrNull() ?: 0)
                    calendar.set(Calendar.MINUTE, timeParts[1].toIntOrNull() ?: 0)
                }
            } else {
                calendar.set(Calendar.HOUR_OF_DAY, 9)
                calendar.set(Calendar.MINUTE, 0)
            }
            calendar.set(Calendar.SECOND, 0)
            calendar.set(Calendar.MILLISECOND, 0)
            calendar.timeInMillis
        } catch (e: Exception) {
            android.util.Log.e(TAG, "Error parsing date/time: ${e.message}", e)
            null
        }
    }
    private fun parseDate(date: String): Date? {
        val formats = listOf(
            SimpleDateFormat("d MMM yyyy", Locale.ENGLISH),
            SimpleDateFormat("dd MMM yyyy", Locale.ENGLISH),
            SimpleDateFormat("d MMM", Locale.ENGLISH),
            SimpleDateFormat("dd MMM", Locale.ENGLISH),
            SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()),
            SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()),
            SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
        )
        for (format in formats) {
            try {
                val parsed = format.parse(date.trim())
                if (parsed != null) {
                    if (!format.toPattern().contains("yyyy")) {
                        val calendar = Calendar.getInstance().apply { time = parsed }
                        if (calendar.get(Calendar.YEAR) == 1970) {
                            calendar.set(Calendar.YEAR, Calendar.getInstance().get(Calendar.YEAR))
                        }
                        return calendar.time
                    }
                    return parsed
                }
            } catch (e: Exception) {
                continue
            }
        }
        return null
    }
    private fun parseReminderSettingToMillis(reminderSetting: String): Long {
        return when {
            reminderSetting.contains("5 menit", ignoreCase = true) -> 5 * 60 * 1000L
            reminderSetting.contains("30 menit", ignoreCase = true) -> 30 * 60 * 1000L
            reminderSetting.contains("1 jam", ignoreCase = true) -> 60 * 60 * 1000L
            reminderSetting.contains("1 hari", ignoreCase = true) -> 24 * 60 * 60 * 1000L
            else -> 0L
        }
    }
}