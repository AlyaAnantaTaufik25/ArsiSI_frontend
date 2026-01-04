package com.example.arsisi_frontend.utils
import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.example.arsisi_frontend.R
import com.example.arsisi_frontend.MainActivity
import android.util.Log
class NotificationHelper(private val context: Context) {
    private object StaticHelper {
        private const val CHANNEL_ID = "arsisi_prestasi_channel"
        private const val CHANNEL_NAME = "Notifikasi Prestasi"
        private const val CHANNEL_DESCRIPTION = "Notifikasi untuk aktivitas prestasi dan arsip"
        private const val TUGAS_CHANNEL_ID = "CHANNEL_TUGAS"
        private const val AGENDA_REMINDER_CHANNEL_ID = "CHANNEL_AGENDA_REMINDER"
        fun createNotificationChannel(context: Context) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val notificationManager =
                    context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                val importance = NotificationManager.IMPORTANCE_DEFAULT
                val channel = NotificationChannel(CHANNEL_ID, CHANNEL_NAME, importance).apply {
                    description = CHANNEL_DESCRIPTION
                    enableVibration(true)
                    vibrationPattern = longArrayOf(0, 250, 250, 250)
                    enableLights(true)
                }
                notificationManager.createNotificationChannel(channel)
                val tugasChannel = NotificationChannel(TUGAS_CHANNEL_ID, "Notifikasi Tugas", importance)
                notificationManager.createNotificationChannel(tugasChannel)
                val agendaChannel = NotificationChannel(
                    AGENDA_REMINDER_CHANNEL_ID,
                    "Reminder Agenda",
                    NotificationManager.IMPORTANCE_HIGH
                ).apply {
                    description = "Notifikasi pengingat agenda"
                    enableVibration(true)
                    vibrationPattern = longArrayOf(0, 500, 250, 500)
                    enableLights(true)
                    setShowBadge(true)
                }
                notificationManager.createNotificationChannel(agendaChannel)
                android.util.Log.d("NotificationHelper", "✅ All notification channels created")
            }
        }
        internal fun showNotificationInternal(
            context: Context,
            notificationId: Int,
            title: String,
            message: String,
            iconId: Int = android.R.drawable.ic_dialog_info,
            channelId: String = CHANNEL_ID
        ): NotificationCompat.Builder {
            createNotificationChannel(context)
            val intent = Intent(context, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            }
            val pendingIntent = PendingIntent.getActivity(
                context,
                0,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
            return NotificationCompat.Builder(context, channelId)
                .setSmallIcon(iconId)
                .setContentTitle(title)
                .setContentText(message)
                .setStyle(NotificationCompat.BigTextStyle().bigText(message))
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
                .setDefaults(NotificationCompat.DEFAULT_SOUND)
        }
        internal fun notifyWithPermissionCheck(
            context: Context,
            notificationId: Int,
            builder: NotificationCompat.Builder
        ) {
            val notificationManager = NotificationManagerCompat.from(context)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                if (ContextCompat.checkSelfPermission(
                        context,
                        Manifest.permission.POST_NOTIFICATIONS
                    ) == PackageManager.PERMISSION_GRANTED
                ) {
                    try {
                        notificationManager.notify(notificationId, builder.build())
                    } catch (e: SecurityException) {
                        Log.e("NotificationHelper", "Permission notifikasi tidak diberikan: ${e.message}")
                    }
                } else {
                    Log.w("NotificationHelper", "POST_NOTIFICATIONS permission not granted")
                }
            } else {
                try {
                    notificationManager.notify(notificationId, builder.build())
                } catch (e: SecurityException) {
                    Log.e("NotificationHelper", "Error showing notification: ${e.message}")
                }
            }
        }
        fun cancelNotification(context: Context, notificationId: Int) {
            val notificationManager = NotificationManagerCompat.from(context)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                if (ContextCompat.checkSelfPermission(
                        context,
                        Manifest.permission.POST_NOTIFICATIONS
                    ) == PackageManager.PERMISSION_GRANTED
                ) {
                    try {
                        notificationManager.cancel(notificationId)
                    } catch (e: SecurityException) {
                        Log.e("NotificationHelper", "Error canceling notification: ${e.message}")
                    }
                }
            } else {
                try {
                    notificationManager.cancel(notificationId)
                } catch (e: SecurityException) {
                    Log.e("NotificationHelper", "Error canceling notification: ${e.message}")
                }
            }
        }
        fun cancelAllNotifications(context: Context) {
            val notificationManager = NotificationManagerCompat.from(context)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                if (ContextCompat.checkSelfPermission(
                        context,
                        Manifest.permission.POST_NOTIFICATIONS
                    ) == PackageManager.PERMISSION_GRANTED
                ) {
                    try {
                        notificationManager.cancelAll()
                    } catch (e: SecurityException) {
                        Log.e("NotificationHelper", "Error canceling all notifications: ${e.message}")
                    }
                }
            } else {
                try {
                    notificationManager.cancelAll()
                } catch (e: SecurityException) {
                    Log.e("NotificationHelper", "Error canceling all notifications: ${e.message}")
                }
            }
        }
    }
    fun sendUploadNotification(judulTugas: String) {
        if (!PermissionHelper.checkNotificationPermission(context)) return
        val notificationId = System.currentTimeMillis().toInt()
        val builder = NotificationCompat.Builder(context, "CHANNEL_TUGAS")
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle("Upload Berhasil")
            .setContentText("Tugas '$judulTugas' berhasil diupload ke publik")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)
        StaticHelper.notifyWithPermissionCheck(context, notificationId, builder)
    }
    fun showNotification(
        notificationId: Int,
        title: String,
        message: String,
        iconId: Int = android.R.drawable.ic_dialog_info
    ) {
        val builder = StaticHelper.showNotificationInternal(context, notificationId, title, message, iconId)
        StaticHelper.notifyWithPermissionCheck(context, notificationId, builder)
    }
    fun showSuccessNotification(
        notificationId: Int,
        title: String,
        message: String
    ) {
        showNotification(notificationId, title, message, android.R.drawable.ic_dialog_info)
    }
    fun showInfoNotification(
        notificationId: Int,
        title: String,
        message: String
    ) {
        showNotification(notificationId, title, message, android.R.drawable.ic_dialog_info)
    }
    fun cancelNotification(notificationId: Int) {
        StaticHelper.cancelNotification(context, notificationId)
    }
    fun cancelAllNotifications() {
        StaticHelper.cancelAllNotifications(context)
    }
    companion object {
        fun createNotificationChannel(context: Context) {
            StaticHelper.createNotificationChannel(context)
        }
        fun showSuccessNotification(
            context: Context,
            notificationId: Int,
            title: String,
            message: String
        ) {
            val helper = NotificationHelper(context)
            helper.showSuccessNotification(notificationId, title, message)
        }
        fun showInfoNotification(
            context: Context,
            notificationId: Int,
            title: String,
            message: String
        ) {
            val helper = NotificationHelper(context)
            helper.showInfoNotification(notificationId, title, message)
        }
        fun showReminderNotification(
            context: Context,
            agendaId: Int,
            title: String,
            description: String,
            time: String
        ) {
            android.util.Log.d("NotificationHelper", "Showing reminder notification for agenda $agendaId")
            StaticHelper.createNotificationChannel(context)
            val message = "📅 $time - $title" + if (description.isNotEmpty()) "\n$description" else ""
            val intent = Intent(context, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            }
            val pendingIntent = PendingIntent.getActivity(
                context,
                agendaId,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
            val builder = NotificationCompat.Builder(context, "CHANNEL_AGENDA_REMINDER")
                .setSmallIcon(android.R.drawable.ic_lock_idle_alarm)
                .setContentTitle("🔔 Reminder Agenda")
                .setContentText(message)
                .setStyle(NotificationCompat.BigTextStyle().bigText(message))
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setCategory(NotificationCompat.CATEGORY_REMINDER)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
                .setDefaults(NotificationCompat.DEFAULT_ALL)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            StaticHelper.notifyWithPermissionCheck(context, agendaId, builder)
            android.util.Log.d("NotificationHelper", "✅ Reminder notification displayed for agenda $agendaId")
        }
    }
}