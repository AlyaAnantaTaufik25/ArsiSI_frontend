package com.example.arsisi_frontend.ui.akademik

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.Visibility
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.arsisi_frontend.ui.theme.SoftOrange
import com.example.arsisi_frontend.ui.theme.WarmBrown
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.InputStream

// Warna Khusus UI
private val LightRedBg = Color(0xFFFFEBEE)
private val LightPinkBg = Color(0xFFFFE5E5)
private val LightGrayBg = Color(0xFFF9F9F9)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AkademikDetailScreen(
    documentId: String,
    viewModel: AkademikViewModel,
    onBackClick: () -> Unit,
    onEditClick: () -> Unit,
    onDeleteSuccess: () -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val document by viewModel.currentDocument
    var showDeleteDialog by remember { mutableStateOf(false) }
    var isDownloading by remember { mutableStateOf(false) }

    // Load document saat screen dibuka
    LaunchedEffect(documentId) {
        viewModel.loadDocumentById(documentId)
    }

    // Launcher untuk meminta izin notifikasi (Android 13+)
    val notificationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { /* Hasil izin tidak perlu di-handle khusus untuk kasus sederhana ini */ }

    // Cek Izin Notifikasi saat pertama kali dibuka
    LaunchedEffect(Unit) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }
    }

    // Simpan document ke local variable untuk menghindari smart cast error
    val currentDoc = document
    
    if (currentDoc == null) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("Dokumen tidak ditemukan")
        }
        return
    }

    // --- HELPER FUNCTIONS ---

    fun openFile(uriString: String) {
        if (uriString.isEmpty()) {
            Toast.makeText(context, "File tidak valid / rusak", Toast.LENGTH_SHORT).show()
            return
        }
        try {
            val intent = Intent(Intent.ACTION_VIEW).apply {
                data = Uri.parse(uriString)
                flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
            }
            context.startActivity(intent)
        } catch (e: Exception) {
            Toast.makeText(context, "Tidak ada aplikasi untuk membuka file ini", Toast.LENGTH_SHORT).show()
        }
    }

    fun downloadFile(uriString: String, fileName: String) {
        if (uriString.isEmpty()) {
            Toast.makeText(context, "File sumber tidak ditemukan", Toast.LENGTH_SHORT).show()
            return
        }

        isDownloading = true
        Toast.makeText(context, "Mengunduh...", Toast.LENGTH_SHORT).show()

        scope.launch {
            val success = withContext(Dispatchers.IO) {
                saveToDownloads(context, uriString, fileName)
            }

            isDownloading = false
            if (success) {
                // Tampilkan Notifikasi di Status Bar
                showDownloadNotification(context, fileName)
                Toast.makeText(context, "Berhasil disimpan di folder Download", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(context, "Gagal menyimpan file", Toast.LENGTH_SHORT).show()
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Detail Dokumen", color = Color.White, fontWeight = FontWeight.SemiBold) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) { Icon(Icons.Default.ArrowBack, "Kembali", tint = Color.White) }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = SoftOrange)
            )
        },
        bottomBar = {
            BottomActionButtons(onEdit = onEditClick, onDelete = { showDeleteDialog = true })
        },
        containerColor = Color.White
    ) { innerPadding ->
        Column(
            modifier = Modifier.padding(innerPadding).verticalScroll(rememberScrollState())
        ) {
            HeaderImageSection(category = currentDoc.category)

            Column(
                modifier = Modifier.padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                MainInfoSection(
                    title = currentDoc.title, 
                    date = "Ditambahkan pada ${currentDoc.date}",
                    updatedAt = currentDoc.updatedAt
                )
                DescriptionSection(description = currentDoc.description)

                Column {
                    SectionTitle(icon = Icons.Default.Description, title = "File Utama")
                    Spacer(Modifier.height(8.dp))
                    FileItem(
                        fileName = currentDoc.fileName,
                        fileSize = currentDoc.fileSize,
                        isMain = true,
                        onClick = { openFile(currentDoc.fileUri) },
                        onDownloadClick = { downloadFile(currentDoc.fileUri, currentDoc.fileName) }
                    )
                }

                DocumentActionButtons(
                    onViewClick = { openFile(currentDoc.fileUri) },
                    onDownloadClick = { downloadFile(currentDoc.fileUri, currentDoc.fileName) },
                    isDownloading = isDownloading
                )

                if (currentDoc.attachments.isNotEmpty()) {
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        SectionTitle(icon = Icons.Default.AttachFile, title = "File Pendukung")
                        currentDoc.attachments.forEach { attachment ->
                            FileItem(
                                fileName = if (attachment.description.isNotEmpty()) attachment.description else attachment.fileName,
                                fileSize = attachment.fileSize,
                                isMain = false,
                                onClick = { openFile(attachment.fileUri) },
                                onDownloadClick = { downloadFile(attachment.fileUri, attachment.fileName) },
                                subtitle = if (attachment.description.isNotEmpty()) attachment.fileName else null
                            )
                        }
                    }
                }
            }
        }
    }

    if (showDeleteDialog) {
        com.example.arsisi_frontend.ui.akademik.ConfirmationDialog(
            title = "Hapus Dokumen?",
            message = "Yakin hapus '${currentDoc.title}'?",
            confirmText = "HAPUS",
            isDeleteType = true,
            onDismiss = { showDeleteDialog = false },
            onConfirm = {
                viewModel.deleteDocument(documentId)
                showDeleteDialog = false
                onDeleteSuccess()
            }
        )
    }
}

// ================= LOGIC DOWNLOAD & NOTIFIKASI =================

private fun saveToDownloads(context: Context, sourceUriStr: String, fileName: String): Boolean {
    return try {
        val resolver = context.contentResolver
        val sourceUri = Uri.parse(sourceUriStr)

        val contentValues = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, fileName)
            put(MediaStore.MediaColumns.MIME_TYPE, resolver.getType(sourceUri) ?: "application/octet-stream")

            // PERBAIKAN 1: Cek versi Android sebelum pakai RELATIVE_PATH
            // RELATIVE_PATH hanya ada di Android 10 (API 29) ke atas
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS)
            }
        }

        // PERBAIKAN 2: Pilih lokasi penyimpanan berdasarkan versi Android
        val collection = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            // Untuk Android 10+ pakai folder Downloads khusus
            MediaStore.Downloads.EXTERNAL_CONTENT_URI
        } else {
            // Untuk Android 9 ke bawah, pakai penyimpanan eksternal umum
            MediaStore.Files.getContentUri("external")
        }

        val destinationUri = resolver.insert(collection, contentValues) ?: return false

        resolver.openInputStream(sourceUri)?.use { input ->
            resolver.openOutputStream(destinationUri)?.use { output ->
                input.copyTo(output)
            }
        }
        true
    } catch (e: Exception) {
        e.printStackTrace()
        false
    }
}

// FUNGSI UNTUK MEMUNCULKAN NOTIFIKASI
private fun showDownloadNotification(context: Context, fileName: String) {
    val channelId = "download_channel"
    val notificationId = System.currentTimeMillis().toInt()

    // 1. Buat Notification Channel (Wajib untuk Android 8.0+)
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        val name = "Unduhan Dokumen"
        val descriptionText = "Notifikasi saat dokumen berhasil diunduh"
        val importance = NotificationManager.IMPORTANCE_DEFAULT
        val channel = NotificationChannel(channelId, name, importance).apply {
            description = descriptionText
        }
        val notificationManager: NotificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }

    // 2. Build Notifikasi
    // Pastikan icon 'android.R.drawable.stat_sys_download_done' ada atau ganti dengan icon app
    val builder = NotificationCompat.Builder(context, channelId)
        .setSmallIcon(android.R.drawable.stat_sys_download_done)
        .setContentTitle("Unduhan Selesai")
        .setContentText("$fileName berhasil disimpan.")
        .setPriority(NotificationCompat.PRIORITY_DEFAULT)
        .setAutoCancel(true)

    // 3. Tampilkan (Cek Izin dulu untuk Android 13+)
    if (ActivityCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED) {
        NotificationManagerCompat.from(context).notify(notificationId, builder.build())
    } else {
        // Jika izin belum diberikan, fallback ke Toast saja (sudah ada di logic utama)
    }
}

// ================= KOMPONEN UI (Sama seperti sebelumnya) =================

@Composable
fun HeaderImageSection(category: String) {
    // Tentukan warna, icon utama, dan icon sekunder berdasarkan kategori
    val (backgroundColor, mainIcon, secondaryIcon, iconColor) = when (category.lowercase()) {
        "administrasi" -> Quadruple(
            Color(0xFFE3F2FD), // Light Blue background
            Icons.Default.Folder,
            Icons.Default.Description,
            Color(0xFF1976D2) // Blue icon
        )
        "akademik" -> Quadruple(
            Color(0xFFF3E5F5), // Light Purple background
            Icons.Default.School,
            Icons.Default.MenuBook,
            Color(0xFF7B1FA2) // Purple icon
        )
        "laporan" -> Quadruple(
            Color(0xFFE8F5E9), // Light Green background
            Icons.Default.Assessment,
            Icons.Default.BarChart,
            Color(0xFF388E3C) // Green icon
        )
        else -> Quadruple(
            Color(0xFFF5F5F5), // Light Gray background
            Icons.Default.Description,
            Icons.Default.FolderOpen,
            Color(0xFF616161) // Gray icon
        )
    }
    
    Box(modifier = Modifier.fillMaxWidth().height(220.dp)) {
        // Background dengan warna soft
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(backgroundColor)
        )
        
        // Logo besar di tengah dengan efek shadow
        Box(
            modifier = Modifier
                .align(Alignment.Center)
                .size(120.dp)
                .background(
                    color = iconColor.copy(alpha = 0.1f),
                    shape = androidx.compose.foundation.shape.CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            // Icon utama besar
            Icon(
                mainIcon,
                contentDescription = null,
                tint = iconColor,
                modifier = Modifier.size(60.dp)
            )
        }
        
        // Icon kecil di pojok kanan atas sebagai aksen
        Icon(
            secondaryIcon,
            contentDescription = null,
            tint = iconColor.copy(alpha = 0.3f),
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(20.dp)
                .size(40.dp)
        )
        
        // Badge kategori di pojok kiri bawah
        Surface(
            color = Color.White,
            shape = RoundedCornerShape(topEnd = 24.dp, bottomEnd = 0.dp),
            modifier = Modifier.align(Alignment.BottomStart),
            shadowElevation = 4.dp
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 20.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    mainIcon,
                    contentDescription = null,
                    tint = iconColor,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(Modifier.width(10.dp))
                Text(
                    category,
                    color = Color.Black,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
            }
        }
    }
}

// Helper data class untuk return multiple values
private data class Quadruple<A, B, C, D>(
    val first: A,
    val second: B,
    val third: C,
    val fourth: D
)

@Composable
fun MainInfoSection(title: String, date: String, updatedAt: String? = null) {
    Row(
        modifier = Modifier.fillMaxWidth(), 
        horizontalArrangement = Arrangement.SpaceBetween, 
        verticalAlignment = Alignment.Top
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                title, 
                style = MaterialTheme.typography.titleLarge, 
                fontWeight = FontWeight.Bold, 
                color = WarmBrown, 
                maxLines = 2, 
                overflow = TextOverflow.Ellipsis
            )
            Spacer(Modifier.height(6.dp))
            // Tanggal pertama kali upload
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    Icons.Default.CalendarToday, 
                    null, 
                    tint = Color.Gray, 
                    modifier = Modifier.size(14.dp)
                )
                Spacer(Modifier.width(6.dp))
                Text(
                    date, 
                    style = MaterialTheme.typography.bodyMedium, 
                    color = Color.Gray
                )
            }
            // Tampilkan tanggal update jika ada
            if (updatedAt != null) {
                Spacer(Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Default.Edit, 
                        null, 
                        tint = Color.Gray, 
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(Modifier.width(6.dp))
                    Text(
                        "Diupdate pada $updatedAt", 
                        style = MaterialTheme.typography.bodySmall, 
                        color = Color.Gray,
                        fontSize = 12.sp
                    )
                }
            }
        }
        Box(
            modifier = Modifier
                .size(56.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(LightRedBg), 
            contentAlignment = Alignment.Center
        ) {
            Icon(
                Icons.Default.Description, 
                "PDF", 
                tint = Color.Red, 
                modifier = Modifier.size(28.dp)
            )
        }
    }
}

@Composable
fun DescriptionSection(description: String) {
    Column {
        SectionTitle(icon = Icons.Default.Info, title = "Deskripsi")
        Spacer(Modifier.height(8.dp))
        Text(text = description, style = MaterialTheme.typography.bodyMedium, color = Color(0xFF4A4A4A), lineHeight = 22.sp)
    }
}

@Composable
fun DocumentActionButtons(
    onViewClick: () -> Unit, 
    onDownloadClick: () -> Unit,
    isDownloading: Boolean = false
) {
    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
        OutlinedButton(
            onClick = onViewClick, 
            modifier = Modifier.weight(1f).height(48.dp), 
            shape = RoundedCornerShape(10.dp), 
            colors = ButtonDefaults.outlinedButtonColors(contentColor = SoftOrange), 
            border = androidx.compose.foundation.BorderStroke(1.5.dp, SoftOrange)
        ) {
            Icon(Icons.Outlined.Visibility, null, modifier = Modifier.size(18.dp))
            Spacer(Modifier.width(8.dp))
            Text("Lihat", fontWeight = FontWeight.SemiBold)
        }
        Button(
            onClick = onDownloadClick, 
            modifier = Modifier.weight(1f).height(48.dp), 
            shape = RoundedCornerShape(10.dp), 
            colors = ButtonDefaults.buttonColors(containerColor = SoftOrange),
            enabled = !isDownloading
        ) {
            if (isDownloading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(18.dp),
                    color = Color.White
                )
            } else {
                Icon(Icons.Default.Download, null, modifier = Modifier.size(18.dp))
                Spacer(Modifier.width(8.dp))
                Text("Unduh", fontWeight = FontWeight.SemiBold)
            }
        }
    }
}

@Composable
fun FileItem(
    fileName: String, 
    fileSize: String, 
    isMain: Boolean, 
    onClick: () -> Unit, 
    onDownloadClick: () -> Unit,
    subtitle: String? = null
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(if (isMain) Color(0xFFFFF8F3) else LightGrayBg)
            .clickable { onClick() }
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            Icons.Default.Description, 
            null, 
            tint = if(isMain) SoftOrange else Color.Gray, 
            modifier = Modifier.size(32.dp)
        )
        Spacer(Modifier.width(16.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                fileName, 
                fontWeight = FontWeight.Medium, 
                fontSize = 14.sp, 
                maxLines = 1, 
                overflow = TextOverflow.Ellipsis
            )
            if (subtitle != null) {
                Text(
                    subtitle,
                    fontSize = 11.sp,
                    color = Color.Gray,
                    fontStyle = androidx.compose.ui.text.font.FontStyle.Italic,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
            Text(
                "$fileSize • ${if(isMain) "File Utama" else "Lampiran"}", 
                color = Color.Gray, 
                fontSize = 12.sp
            )
        }
        if (!isMain) {
            IconButton(onClick = onDownloadClick) { 
                Icon(Icons.Default.DownloadForOffline, "Download", tint = Color.Gray) 
            }
        }
    }
}

@Composable
fun BottomActionButtons(onEdit: () -> Unit, onDelete: () -> Unit) {
    Surface(shadowElevation = 20.dp, color = Color.White, shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp)) {
        Row(modifier = Modifier.padding(20.dp).fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            OutlinedButton(onClick = onEdit, modifier = Modifier.weight(1f).height(50.dp), shape = RoundedCornerShape(12.dp), colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.Black), border = androidx.compose.foundation.BorderStroke(1.dp, Color.LightGray)) {
                Icon(Icons.Outlined.Edit, null, modifier = Modifier.size(18.dp)); Spacer(Modifier.width(8.dp)); Text("Edit")
            }
            Button(onClick = onDelete, modifier = Modifier.weight(1f).height(50.dp), shape = RoundedCornerShape(12.dp), colors = ButtonDefaults.buttonColors(containerColor = LightPinkBg, contentColor = Color.Red), elevation = ButtonDefaults.buttonElevation(0.dp)) {
                Icon(Icons.Outlined.Delete, null, modifier = Modifier.size(18.dp)); Spacer(Modifier.width(8.dp)); Text("Hapus")
            }
        }
    }
}

@Composable
fun SectionTitle(icon: ImageVector, title: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(icon, null, tint = SoftOrange, modifier = Modifier.size(20.dp)); Spacer(Modifier.width(8.dp)); Text(title, fontWeight = FontWeight.Bold, color = Color.Black, fontSize = 16.sp)
    }
}