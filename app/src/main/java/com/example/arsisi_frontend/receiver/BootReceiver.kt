package com.example.arsisi_frontend.receiver
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.example.arsisi_frontend.data.local.AppDatabase
import com.example.arsisi_frontend.utils.NotificationHelper
import com.example.arsisi_frontend.utils.ReminderScheduler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
class BootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED ||
            intent.action == "android.intent.action.QUICKBOOT_POWERON") {
            android.util.Log.d("BootReceiver", "Device boot completed, rescheduling reminders...")
            NotificationHelper.createNotificationChannel(context)
            rescheduleAllReminders(context)
        }
    }
    private fun rescheduleAllReminders(context: Context) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val database = AppDatabase.getDatabase(context)
                val agendaDao = database.agendaDao()
                val reminderScheduler = ReminderScheduler(context)
                val agendas = agendaDao.getAllAgendas().first()
                var rescheduledCount = 0
                for (agenda in agendas) {
                    if (!agenda.reminderSetting.isNullOrBlank()) {
                        reminderScheduler.scheduleReminder(
                            agendaId = agenda.id,
                            title = agenda.judul,
                            description = agenda.deskripsi ?: "",
                            date = agenda.tanggal,
                            time = agenda.waktu ?: "",
                            reminderSetting = agenda.reminderSetting
                        )
                        rescheduledCount++
                    }
                }
                android.util.Log.d("BootReceiver", "Rescheduled $rescheduledCount reminders after boot")
            } catch (e: Exception) {
                android.util.Log.e("BootReceiver", "Error rescheduling reminders: ${e.message}", e)
            }
        }
    }
}