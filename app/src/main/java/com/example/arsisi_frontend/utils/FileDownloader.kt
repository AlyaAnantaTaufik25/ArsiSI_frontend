package com.example.arsisi_frontend.utils
import android.app.DownloadManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.database.Cursor
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.webkit.MimeTypeMap
import androidx.core.app.NotificationCompat
import androidx.core.content.FileProvider
import com.example.arsisi_frontend.R
import java.io.File
class FileDownloader(private val context: Context) {
    private val TAG = "FileDownloader"
    private val CHANNEL_ID = "file_download_channel"
    private val CHANNEL_NAME = "File Downloads"
    private val NOTIFICATION_ID_BASE = 2000
    private var downloadId: Long = -1
    private var notificationId: Int = NOTIFICATION_ID_BASE
    init {
        createNotificationChannel()
    }
    fun downloadFile(
        fileUrl: String,
        fileName: String,
        onSuccess: (Uri) -> Unit = {},
        onError: (String) -> Unit = {}
    ) {
        try {
            Log.d(TAG, "📥 Starting download: $fileUrl")
            notificationId = NOTIFICATION_ID_BASE + System.currentTimeMillis().toInt() % 1000
            val extension = fileUrl.substringAfterLast(".", "pdf")
            val mimeType = getMimeType(extension)
            val isImage = isImageFile(extension)
            val cleanFileName = fileName.replace(Regex("[^a-zA-Z0-9._-]"), "_")
            val actualFileName = if (cleanFileName.contains(".")) {
                cleanFileName
            } else {
                "$cleanFileName.$extension"
            }
            Log.d(TAG, "📄 File: $actualFileName, Type: $mimeType, IsImage: $isImage")
            val downloadManager = context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
            val request = DownloadManager.Request(Uri.parse(fileUrl)).apply {
                setTitle("Mengunduh: $fileName")
                setDescription("Sedang mengunduh file...")
                setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE)
                setDestinationInExternalPublicDir(
                    Environment.DIRECTORY_DOWNLOADS,
                    "ArsiSI/$actualFileName"
                )
                setAllowedOverMetered(true)
                setAllowedOverRoaming(true)
                setMimeType(mimeType)
            }
            downloadId = downloadManager.enqueue(request)
            Log.d(TAG, "🚀 Download started with ID: $downloadId")
            val onComplete = object : BroadcastReceiver() {
                override fun onReceive(context: Context, intent: Intent) {
                    val id = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1)
                    if (id == downloadId) {
                        handleDownloadComplete(
                            downloadManager,
                            id,
                            fileName,
                            actualFileName,
                            isImage,
                            onSuccess,
                            onError
                        )
                        context.unregisterReceiver(this)
                    }
                }
            }
            context.registerReceiver(
                onComplete,
                IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE),
                Context.RECEIVER_NOT_EXPORTED
            )
        } catch (e: Exception) {
            Log.e(TAG, "❌ Download error", e)
            onError(e.message ?: "Unknown error")
        }
    }
    private fun handleDownloadComplete(
        downloadManager: DownloadManager,
        downloadId: Long,
        displayName: String,
        fileName: String,
        isImage: Boolean,
        onSuccess: (Uri) -> Unit,
        onError: (String) -> Unit
    ) {
        val query = DownloadManager.Query().setFilterById(downloadId)
        val cursor: Cursor? = downloadManager.query(query)
        if (cursor != null && cursor.moveToFirst()) {
            val statusIndex = cursor.getColumnIndex(DownloadManager.COLUMN_STATUS)
            val status = cursor.getInt(statusIndex)
            when (status) {
                DownloadManager.STATUS_SUCCESSFUL -> {
                    val uriIndex = cursor.getColumnIndex(DownloadManager.COLUMN_LOCAL_URI)
                    val localUri = cursor.getString(uriIndex)
                    val fileUri = Uri.parse(localUri)
                    Log.d(TAG, "✅ Download successful: $fileUri")
                    if (isImage) {
                        copyToGallery(fileUri, fileName)
                    }
                    showSuccessNotification(fileUri, displayName, fileName)
                    onSuccess(fileUri)
                }
                DownloadManager.STATUS_FAILED -> {
                    val reasonIndex = cursor.getColumnIndex(DownloadManager.COLUMN_REASON)
                    val reason = cursor.getInt(reasonIndex)
                    val errorMsg = "Download failed with reason: $reason"
                    Log.e(TAG, "❌ $errorMsg")
                    showErrorNotification(displayName)
                    onError(errorMsg)
                }
            }
        }
        cursor?.close()
    }
    private fun copyToGallery(fileUri: Uri, fileName: String) {
        try {
            Log.d(TAG, "🖼️ Copying image to gallery: $fileName")
            val inputStream = context.contentResolver.openInputStream(fileUri)
            if (inputStream != null) {
                val contentValues = ContentValues().apply {
                    put(MediaStore.Images.Media.DISPLAY_NAME, fileName)
                    put(MediaStore.Images.Media.MIME_TYPE, getMimeType(fileName.substringAfterLast(".")))
                    put(MediaStore.Images.Media.RELATIVE_PATH, "${Environment.DIRECTORY_PICTURES}/ArsiSI")
                    put(MediaStore.Images.Media.IS_PENDING, 1)
                }
                val imageUri = context.contentResolver.insert(
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                    contentValues
                )
                imageUri?.let { uri ->
                    context.contentResolver.openOutputStream(uri)?.use { outputStream ->
                        inputStream.copyTo(outputStream)
                    }
                    contentValues.clear()
                    contentValues.put(MediaStore.Images.Media.IS_PENDING, 0)
                    context.contentResolver.update(uri, contentValues, null, null)
                    Log.d(TAG, "✅ Image saved to gallery: $uri")
                }
                inputStream.close()
            }
        } catch (e: Exception) {
            Log.e(TAG, "❌ Failed to copy to gallery", e)
        }
    }
    private fun showSuccessNotification(fileUri: Uri, displayName: String, fileName: String) {
        try {
            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            val extension = fileName.substringAfterLast(".", "")
            val mimeType = getMimeType(extension)
            val file = File(fileUri.path ?: "")
            val contentUri = if (file.exists()) {
                FileProvider.getUriForFile(
                    context,
                    "${context.packageName}.fileprovider",
                    file
                )
            } else {
                fileUri
            }
            val openIntent = Intent(Intent.ACTION_VIEW).apply {
                setDataAndType(contentUri, mimeType)
                flags = Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_ACTIVITY_NEW_TASK
            }
            val openPendingIntent = PendingIntent.getActivity(
                context,
                notificationId,
                openIntent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                type = mimeType
                putExtra(Intent.EXTRA_STREAM, contentUri)
                flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
            }
            val sharePendingIntent = PendingIntent.getActivity(
                context,
                notificationId + 1,
                Intent.createChooser(shareIntent, "Bagikan File"),
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
            val notification = NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(android.R.drawable.stat_sys_download_done)
                .setContentTitle("Download Selesai")
                .setContentText(displayName)
                .setStyle(NotificationCompat.BigTextStyle()
                    .bigText("File tersimpan di folder Downloads/ArsiSI\nKlik untuk membuka"))
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true)
                .setContentIntent(openPendingIntent)
                .addAction(
                    android.R.drawable.ic_menu_view,
                    "Buka",
                    openPendingIntent
                )
                .addAction(
                    android.R.drawable.ic_menu_share,
                    "Bagikan",
                    sharePendingIntent
                )
                .build()
            notificationManager.notify(notificationId, notification)
            Log.d(TAG, "📬 Success notification displayed")
        } catch (e: Exception) {
            Log.e(TAG, "Failed to show success notification", e)
        }
    }
    private fun showErrorNotification(fileName: String) {
        try {
            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            val notification = NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(android.R.drawable.stat_notify_error)
                .setContentTitle("Download Gagal")
                .setContentText(fileName)
                .setStyle(NotificationCompat.BigTextStyle()
                    .bigText("Gagal mengunduh file. Periksa koneksi internet Anda."))
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true)
                .build()
            notificationManager.notify(notificationId, notification)
            Log.d(TAG, "📬 Error notification displayed")
        } catch (e: Exception) {
            Log.e(TAG, "Failed to show error notification", e)
        }
    }
    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Notifications for file downloads"
                enableVibration(true)
            }
            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }
    private fun getMimeType(extension: String): String {
        return when (extension.lowercase()) {
            "pdf" -> "application/pdf"
            "jpg", "jpeg" -> "image/jpeg"
            "png" -> "image/png"
            "gif" -> "image/gif"
            "webp" -> "image/webp"
            "bmp" -> "image/bmp"
            "doc" -> "application/msword"
            "docx" -> "application/vnd.openxmlformats-officedocument.wordprocessingml.document"
            "xls" -> "application/vnd.ms-excel"
            "xlsx" -> "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"
            "ppt" -> "application/vnd.ms-powerpoint"
            "pptx" -> "application/vnd.openxmlformats-officedocument.presentationml.presentation"
            "txt" -> "text/plain"
            "zip" -> "application/zip"
            else -> MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension)
                ?: "application/octet-stream"
        }
    }
    private fun isImageFile(extension: String): Boolean {
        return extension.lowercase() in listOf("jpg", "jpeg", "png", "gif", "webp", "bmp")
    }
}