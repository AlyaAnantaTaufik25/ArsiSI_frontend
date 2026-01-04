package com.example.arsisi_frontend.receiver
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.example.arsisi_frontend.utils.NotificationHelper
class AlarmReceiver : BroadcastReceiver() {
    companion object {
        const val EXTRA_AGENDA_ID = "extra_agenda_id"
        const val EXTRA_AGENDA_TITLE = "extra_agenda_title"
        const val EXTRA_AGENDA_DESCRIPTION = "extra_agenda_description"
        const val EXTRA_AGENDA_TIME = "extra_agenda_time"
    }
    override fun onReceive(context: Context, intent: Intent) {
        android.util.Log.d("AlarmReceiver", "Alarm received!")
        val agendaId = intent.getIntExtra(EXTRA_AGENDA_ID, -1)
        val title = intent.getStringExtra(EXTRA_AGENDA_TITLE) ?: "Reminder Agenda"
        val description = intent.getStringExtra(EXTRA_AGENDA_DESCRIPTION) ?: ""
        val time = intent.getStringExtra(EXTRA_AGENDA_TIME) ?: ""
        android.util.Log.d("AlarmReceiver", "Agenda ID: $agendaId, Title: $title, Time: $time")
        if (agendaId != -1) {
            NotificationHelper.createNotificationChannel(context)
            NotificationHelper.showReminderNotification(
                context = context,
                agendaId = agendaId,
                title = title,
                description = description,
                time = time
            )
        } else {
            android.util.Log.e("AlarmReceiver", "Invalid agenda ID received")
        }
    }
}