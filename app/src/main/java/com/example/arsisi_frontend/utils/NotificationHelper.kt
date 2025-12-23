package com.example.arsisi_frontend.utils

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.arsisi_frontend.R

class NotificationHelper(private val context: Context) {

    fun sendUploadNotification(judulTugas: String) {
        // Double check permission before notifying
        if (!PermissionHelper.checkNotificationPermission(context)) return

        val builder = NotificationCompat.Builder(context, "CHANNEL_TUGAS")
            .setSmallIcon(R.drawable.ic_launcher_foreground) // Use your app icon
            .setContentTitle("Upload Berhasil")
            .setContentText("Tugas '$judulTugas' berhasil diupload ke publik")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)

        with(NotificationManagerCompat.from(context)) {
            // Check permission again as required by newer Android versions
            try {
                notify(System.currentTimeMillis().toInt(), builder.build())
            } catch (e: SecurityException) {
                e.printStackTrace()
            }
        }
    }
}