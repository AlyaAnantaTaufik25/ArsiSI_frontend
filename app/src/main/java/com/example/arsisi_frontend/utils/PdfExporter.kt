package com.example.arsisi_frontend.utils
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Typeface
import android.graphics.pdf.PdfDocument
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.content.FileProvider
import com.example.arsisi_frontend.R
import com.example.arsisi_frontend.data.model.Prestasi
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
class PdfExporter(private val context: Context) {
    private val TAG = "PdfExporter"
    private val CHANNEL_ID = "pdf_download_channel"
    private val CHANNEL_NAME = "PDF Downloads"
    private val NOTIFICATION_ID = 1001
    private val pageWidth = 595
    private val pageHeight = 842
    private val margin = 50f
    private val contentWidth = pageWidth - (2 * margin)
    init {
        createNotificationChannel()
    }
    fun exportPrestasiToPdf(
        prestasiList: List<Prestasi>,
        mahasiswaName: String,
        mahasiswaNim: String,
        onSuccess: (Uri) -> Unit,
        onError: (String) -> Unit
    ) {
        try {
            Log.d(TAG, "📄 Starting PDF export for ${prestasiList.size} prestasi")
            val pdfDocument = PdfDocument()
            var currentPage = 1
            var yPosition = margin
            val pageInfo = PdfDocument.PageInfo.Builder(pageWidth, pageHeight, currentPage).create()
            var page = pdfDocument.startPage(pageInfo)
            var canvas = page.canvas
            yPosition = drawHeader(canvas, mahasiswaName, mahasiswaNim, yPosition)
            yPosition += 40f
            prestasiList.forEachIndexed { index, prestasi ->
                val itemHeight = drawPrestasiItem(canvas, prestasi, index + 1, yPosition)
                yPosition += itemHeight
                if (yPosition > pageHeight - margin - 100f && index < prestasiList.size - 1) {
                    pdfDocument.finishPage(page)
                    currentPage++
                    val newPageInfo = PdfDocument.PageInfo.Builder(pageWidth, pageHeight, currentPage).create()
                    page = pdfDocument.startPage(newPageInfo)
                    canvas = page.canvas
                    yPosition = margin
                }
            }
            pdfDocument.finishPage(page)
            val fileName = "Prestasi_${mahasiswaNim}_${System.currentTimeMillis()}.pdf"
            val uri = savePdfToDownloads(pdfDocument, fileName)
            pdfDocument.close()
            if (uri != null) {
                Log.d(TAG, "✅ PDF exported successfully: $uri")
                showDownloadNotification(uri, fileName)
                onSuccess(uri)
            } else {
                Log.e(TAG, "❌ Failed to save PDF")
                onError("Failed to save PDF file")
            }
        } catch (e: Exception) {
            Log.e(TAG, "❌ PDF export error", e)
            onError(e.message ?: "Unknown error occurred")
        }
    }
    private fun drawHeader(
        canvas: Canvas,
        mahasiswaName: String,
        mahasiswaNim: String,
        startY: Float
    ): Float {
        var y = startY
        val paint = Paint()
        paint.color = Color.rgb(255, 111, 0)
        paint.textSize = 28f
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        canvas.drawText("Achievement Portfolio", margin, y, paint)
        y += 40f
        paint.color = Color.rgb(26, 26, 26)
        paint.textSize = 20f
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        canvas.drawText(mahasiswaName, margin, y, paint)
        y += 30f
        paint.textSize = 14f
        paint.typeface = Typeface.DEFAULT
        paint.color = Color.rgb(158, 158, 158)
        canvas.drawText("NIM: $mahasiswaNim", margin, y, paint)
        y += 25f
        val dateFormat = SimpleDateFormat("dd MMMM yyyy", Locale("id", "ID"))
        val currentDate = dateFormat.format(Date())
        canvas.drawText("Generated: $currentDate", margin, y, paint)
        y += 30f
        paint.color = Color.rgb(224, 224, 224)
        paint.strokeWidth = 2f
        canvas.drawLine(margin, y, pageWidth - margin, y, paint)
        y += 10f
        return y
    }
    private fun drawPrestasiItem(
        canvas: Canvas,
        prestasi: Prestasi,
        number: Int,
        startY: Float
    ): Float {
        var y = startY
        val paint = Paint()
        val itemPadding = 20f
        paint.color = Color.rgb(255, 111, 0)
        val badgeSize = 24f
        canvas.drawCircle(margin + badgeSize/2, y + badgeSize/2, badgeSize/2, paint)
        paint.color = Color.WHITE
        paint.textSize = 12f
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        paint.textAlign = Paint.Align.CENTER
        canvas.drawText(number.toString(), margin + badgeSize/2, y + badgeSize/2 + 4f, paint)
        paint.textAlign = Paint.Align.LEFT
        val nameX = margin + badgeSize + 15f
        paint.color = Color.rgb(26, 26, 26)
        paint.textSize = 16f
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        val wrappedName = wrapText(prestasi.nama, paint, contentWidth - badgeSize - 20f)
        wrappedName.forEach { line ->
            canvas.drawText(line, nameX, y + 18f, paint)
            y += 22f
        }
        y += 8f
        paint.textSize = 12f
        paint.typeface = Typeface.DEFAULT
        paint.color = Color.rgb(255, 111, 0)
        canvas.drawText("${prestasi.jenis} • ${prestasi.tingkat}", nameX, y, paint)
        y += 18f
        paint.color = Color.rgb(158, 158, 158)
        canvas.drawText("Tahun: ${prestasi.tahun}", nameX, y, paint)
        y += 18f
        if (!prestasi.deskripsi.isNullOrEmpty()) {
            paint.textSize = 11f
            paint.color = Color.rgb(97, 97, 97)
            val wrappedDesc = wrapText(prestasi.deskripsi, paint, contentWidth - badgeSize - 20f)
            wrappedDesc.take(2).forEach { line ->
                canvas.drawText(line, nameX, y, paint)
                y += 16f
            }
        }
        y += itemPadding
        paint.color = Color.rgb(240, 240, 240)
        paint.strokeWidth = 1f
        canvas.drawLine(nameX, y, pageWidth - margin, y, paint)
        y += itemPadding
        return y - startY
    }
    private fun wrapText(text: String, paint: Paint, maxWidth: Float): List<String> {
        val words = text.split(" ")
        val lines = mutableListOf<String>()
        var currentLine = ""
        words.forEach { word ->
            val testLine = if (currentLine.isEmpty()) word else "$currentLine $word"
            val width = paint.measureText(testLine)
            if (width > maxWidth && currentLine.isNotEmpty()) {
                lines.add(currentLine)
                currentLine = word
            } else {
                currentLine = testLine
            }
        }
        if (currentLine.isNotEmpty()) {
            lines.add(currentLine)
        }
        return lines
    }
    private fun savePdfToDownloads(pdfDocument: PdfDocument, fileName: String): Uri? {
        return try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                val contentValues = ContentValues().apply {
                    put(MediaStore.MediaColumns.DISPLAY_NAME, fileName)
                    put(MediaStore.MediaColumns.MIME_TYPE, "application/pdf")
                    put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS)
                }
                val uri = context.contentResolver.insert(
                    MediaStore.Downloads.EXTERNAL_CONTENT_URI,
                    contentValues
                )
                uri?.let {
                    context.contentResolver.openOutputStream(it)?.use { outputStream ->
                        pdfDocument.writeTo(outputStream)
                    }
                }
                uri
            } else {
                val downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
                if (!downloadsDir.exists()) {
                    downloadsDir.mkdirs()
                }
                val file = File(downloadsDir, fileName)
                file.outputStream().use { outputStream ->
                    pdfDocument.writeTo(outputStream)
                }
                FileProvider.getUriForFile(
                    context,
                    "${context.packageName}.fileprovider",
                    file
                )
            }
        } catch (e: Exception) {
            Log.e(TAG, "Failed to save PDF", e)
            null
        }
    }
    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "Notifications for PDF downloads"
                enableVibration(true)
            }
            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }
    private fun showDownloadNotification(uri: Uri, fileName: String) {
        try {
            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            val openIntent = Intent(Intent.ACTION_VIEW).apply {
                setDataAndType(uri, "application/pdf")
                flags = Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_ACTIVITY_NEW_TASK
            }
            val openPendingIntent = PendingIntent.getActivity(
                context,
                0,
                openIntent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                type = "application/pdf"
                putExtra(Intent.EXTRA_STREAM, uri)
                flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
            }
            val sharePendingIntent = PendingIntent.getActivity(
                context,
                1,
                Intent.createChooser(shareIntent, "Share PDF"),
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
            val notification = NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(android.R.drawable.stat_sys_download_done)
                .setContentTitle("PDF Downloaded")
                .setContentText(fileName)
                .setStyle(NotificationCompat.BigTextStyle()
                    .bigText("File tersimpan di folder Downloads\nKlik untuk membuka atau bagikan"))
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setAutoCancel(true)
                .setContentIntent(openPendingIntent)
                .addAction(
                    android.R.drawable.ic_menu_view,
                    "Open",
                    openPendingIntent
                )
                .addAction(
                    android.R.drawable.ic_menu_share,
                    "Share",
                    sharePendingIntent
                )
                .build()
            notificationManager.notify(NOTIFICATION_ID, notification)
            Log.d(TAG, "📬 Notification displayed for: $fileName")
        } catch (e: Exception) {
            Log.e(TAG, "Failed to show notification", e)
        }
    }
}