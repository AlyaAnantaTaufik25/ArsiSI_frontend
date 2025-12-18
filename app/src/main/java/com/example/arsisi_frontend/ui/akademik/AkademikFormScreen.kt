package com.example.arsisi_frontend.ui.akademik

import android.content.Context
import android.net.Uri
import android.provider.OpenableColumns
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Description
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.arsisi_frontend.ui.theme.SoftOrange

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AkademikFormScreen(
    documentId: String? = null,
    viewModel: AkademikViewModel,
    onBackClick: () -> Unit,
    onSuccess: () -> Unit
) {
    val context = LocalContext.current
    val isEditMode = documentId != null
    val existingDoc = if (isEditMode) viewModel.getDocumentById(documentId!!) else null

    var title by remember { mutableStateOf(existingDoc?.title ?: "") }
    var description by remember { mutableStateOf(existingDoc?.description ?: "") }
    var selectedFileName by remember { mutableStateOf(existingDoc?.fileName ?: "") }
    var selectedFileSize by remember { mutableStateOf(existingDoc?.fileSize ?: "") }
    var selectedFileUri by remember { mutableStateOf(existingDoc?.fileUri ?: "") }
    var hasFile by remember { mutableStateOf(isEditMode || selectedFileName.isNotEmpty()) }

    // === STATE LAMPIRAN (TRIPLE: Nama, Ukuran, URI) ===
    val attachmentList = remember {
        mutableStateListOf<Triple<String, String, String>>().apply {
            if (existingDoc != null) addAll(existingDoc.attachments)
        }
    }

    var showDeleteDocDialog by remember { mutableStateOf(false) }
    var showSaveDialog by remember { mutableStateOf(false) }
    var showReplaceFileDialog by remember { mutableStateOf(false) }
    var showDeleteAttachmentDialog by remember { mutableStateOf(false) }
    var attachmentToDeleteIndex by remember { mutableIntStateOf(-1) }

    // === LAUNCHER UTAMA ===
    val mainFileLauncher = rememberLauncherForActivityResult(contract = ActivityResultContracts.OpenDocument()) { uri: Uri? ->
        uri?.let {
            try { context.contentResolver.takePersistableUriPermission(it, android.content.Intent.FLAG_GRANT_READ_URI_PERMISSION) } catch (e: Exception) {}
            val fileInfo = getFileNameAndSize(context, it)
            selectedFileName = fileInfo.first
            selectedFileSize = fileInfo.second
            selectedFileUri = it.toString()
            hasFile = true
        }
    }

    // === LAUNCHER LAMPIRAN ===
    val attachmentLauncher = rememberLauncherForActivityResult(contract = ActivityResultContracts.OpenDocument()) { uri: Uri? ->
        uri?.let {
            try { context.contentResolver.takePersistableUriPermission(it, android.content.Intent.FLAG_GRANT_READ_URI_PERMISSION) } catch (e: Exception) {}
            val fileInfo = getFileNameAndSize(context, it)
            // Simpan Triple (Nama, Ukuran, URI)
            attachmentList.add(Triple(fileInfo.first, fileInfo.second, it.toString()))
        }
    }

    val categories = listOf("Administrasi", "Akademik", "Laporan", "Lainnya")
    var selectedCategory by remember {
        mutableStateOf(if (isEditMode && existingDoc != null) categories.indexOf(existingDoc.category).coerceAtLeast(0) else 0)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (isEditMode) "Edit Dokumen" else "Tambah Dokumen", color = Color.White, fontWeight = FontWeight.Bold) },
                navigationIcon = { IconButton(onClick = onBackClick) { Icon(Icons.Default.ArrowBack, "Kembali", tint = Color.White) } },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = SoftOrange)
            )
        },
        containerColor = Color(0xFFF9F9F9)
    ) { innerPadding ->
        Column(modifier = Modifier.padding(innerPadding).padding(horizontal = 20.dp).verticalScroll(rememberScrollState())) {
            Spacer(Modifier.height(24.dp))
            SectionLabel("Judul")
            OutlinedTextField(
                value = title, onValueChange = { title = it },
                modifier = Modifier.fillMaxWidth().background(Color.White, RoundedCornerShape(12.dp)),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(unfocusedBorderColor = Color(0xFFE0E0E0), focusedBorderColor = SoftOrange),
                singleLine = true
            )
            Spacer(Modifier.height(20.dp))
            SectionLabel("Kategori")
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                categories.forEachIndexed { index, label ->
                    val isSelected = index == selectedCategory
                    Box(modifier = Modifier.weight(1f).padding(horizontal = 4.dp).height(36.dp).clip(RoundedCornerShape(8.dp)).background(if (isSelected) SoftOrange else Color.Transparent).clickable { selectedCategory = index }, contentAlignment = Alignment.Center) {
                        Text(label, color = if (isSelected) Color.White else Color(0xFF9E9E9E), fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal, fontSize = 12.sp)
                    }
                }
            }
            Spacer(Modifier.height(20.dp))
            SectionLabel("Deskripsi")
            OutlinedTextField(
                value = description, onValueChange = { description = it },
                modifier = Modifier.fillMaxWidth().height(120.dp).background(Color.White, RoundedCornerShape(12.dp)),
                shape = RoundedCornerShape(12.dp), colors = OutlinedTextFieldDefaults.colors(unfocusedBorderColor = Color(0xFFE0E0E0), focusedBorderColor = SoftOrange), maxLines = 5
            )
            Spacer(Modifier.height(20.dp))

            SectionLabel("File Utama")
            if (hasFile) {
                FileCardItem(selectedFileName, selectedFileSize, { showReplaceFileDialog = true })
            } else {
                UploadPlaceholder { mainFileLauncher.launch(arrayOf("application/pdf", "image/*", "application/msword")) }
            }
            Spacer(Modifier.height(20.dp))

            SectionLabel("Lampiran Pendukung")
            // Render List Lampiran
            attachmentList.forEachIndexed { index, (name, size, _) ->
                AttachmentCard(name, size, Icons.Outlined.Description) {
                    attachmentToDeleteIndex = index
                    showDeleteAttachmentDialog = true
                }
                Spacer(Modifier.height(8.dp))
            }
            Box(
                modifier = Modifier.fillMaxWidth().height(48.dp).clip(RoundedCornerShape(10.dp)).dashedBorder(1.dp, SoftOrange, 10.dp).background(Color(0xFFFFF5F0))
                    .clickable { attachmentLauncher.launch(arrayOf("application/pdf", "image/*")) },
                contentAlignment = Alignment.Center
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.AddCircle, null, tint = SoftOrange, modifier = Modifier.size(16.dp))
                    Spacer(Modifier.width(8.dp))
                    Text("Tambah Lampiran", color = SoftOrange, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                }
            }
            Spacer(Modifier.height(40.dp))

            Button(
                onClick = {
                    if (title.isEmpty() || !hasFile) return@Button
                    if (isEditMode) showSaveDialog = true else {
                        viewModel.addDocument(title, categories[selectedCategory], description, selectedFileName, selectedFileSize, selectedFileUri, attachmentList.toList())
                        onSuccess()
                    }
                },
                modifier = Modifier.fillMaxWidth().height(50.dp), shape = RoundedCornerShape(8.dp), colors = ButtonDefaults.buttonColors(containerColor = SoftOrange)
            ) { Text(if (isEditMode) "Simpan Perubahan" else "Simpan Dokumen", fontWeight = FontWeight.Bold, fontSize = 16.sp) }
            Spacer(Modifier.height(16.dp))
            if (isEditMode) {
                TextButton(onClick = { showDeleteDocDialog = true }, modifier = Modifier.fillMaxWidth()) { Text("Hapus Dokumen", color = Color(0xFFEF5350), fontWeight = FontWeight.SemiBold, fontSize = 14.sp) }
            }
            Spacer(Modifier.height(30.dp))
        }

        // DIALOGS
        if (showDeleteDocDialog) ConfirmationDialog("Hapus Dokumen?", "Data tidak dapat dikembalikan.", "HAPUS", true, { showDeleteDocDialog = false }) { viewModel.deleteDocument(documentId!!); showDeleteDocDialog = false; onSuccess() }
        if (showSaveDialog) ConfirmationDialog("Simpan Perubahan?", "Yakin simpan perubahan?", "YAKIN", false, { showSaveDialog = false }) {
            viewModel.updateDocument(documentId!!, title, categories[selectedCategory], description, selectedFileName, selectedFileSize, selectedFileUri, attachmentList.toList())
            showSaveDialog = false
            onSuccess()
        }
        if (showReplaceFileDialog) ConfirmationDialog("Ganti File Utama?", "File lama akan diganti.", "GANTI", false, { showReplaceFileDialog = false }) { showReplaceFileDialog = false; mainFileLauncher.launch(arrayOf("application/pdf", "image/*")) }
        if (showDeleteAttachmentDialog) ConfirmationDialog("Hapus Lampiran?", "Lampiran akan dihapus.", "HAPUS", true, { showDeleteAttachmentDialog = false }) { if (attachmentToDeleteIndex != -1) attachmentList.removeAt(attachmentToDeleteIndex); showDeleteAttachmentDialog = false }
    }
}

// ... (Helper Functions getFileNameAndSize, SectionLabel, UploadPlaceholder, FileCardItem, AttachmentCard, ConfirmationDialog, dashedBorder SAMA SEPERTI SEBELUMNYA) ...
// Copy paste saja dari file sebelumnya, tidak ada perubahan di helper
fun getFileNameAndSize(context: Context, uri: Uri): Pair<String, String> {
    var name = "Unknown File"
    var size = "0 KB"
    val cursor = context.contentResolver.query(uri, null, null, null, null)
    cursor?.use {
        if (it.moveToFirst()) {
            val nameIndex = it.getColumnIndex(OpenableColumns.DISPLAY_NAME)
            if (nameIndex != -1) name = it.getString(nameIndex)
            val sizeIndex = it.getColumnIndex(OpenableColumns.SIZE)
            if (sizeIndex != -1) {
                val sizeBytes = it.getLong(sizeIndex)
                size = if (sizeBytes > 1024 * 1024) String.format("%.1f MB", sizeBytes / (1024.0 * 1024.0)) else String.format("%d KB", sizeBytes / 1024)
            }
        }
    }
    return Pair(name, size)
}

@Composable
fun SectionLabel(text: String) { Text(text, fontWeight = FontWeight.Bold, modifier = Modifier.padding(bottom = 8.dp)) }

@Composable
fun UploadPlaceholder(onClick: () -> Unit) {
    Box(modifier = Modifier.fillMaxWidth().height(120.dp).clip(RoundedCornerShape(12.dp)).background(Color(0xFFFFF2E9)).clickable { onClick() }, contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) { Icon(Icons.Default.UploadFile, null, tint = SoftOrange, modifier = Modifier.size(32.dp)); Spacer(Modifier.height(8.dp)); Text("Ketuk untuk unggah dokumen", color = SoftOrange, fontSize = 12.sp, fontWeight = FontWeight.Medium); Text("PDF, DOCX, Image (maks 10MB)", color = Color.Gray, fontSize = 10.sp) }
    }
}

@Composable
fun FileCardItem(fileName: String, fileSize: String, onActionClick: () -> Unit) {
    Box(modifier = Modifier.fillMaxWidth().background(Color.White, RoundedCornerShape(12.dp)).padding(vertical = 12.dp, horizontal = 16.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) { Box(modifier = Modifier.size(42.dp).background(Color(0xFFFFE0E0), RoundedCornerShape(8.dp)), contentAlignment = Alignment.Center) { Icon(Icons.Outlined.Description, null, tint = SoftOrange) }; Spacer(Modifier.width(14.dp)); Column(modifier = Modifier.weight(1f)) { Text(fileName, fontWeight = FontWeight.Medium, fontSize = 14.sp); Text(fileSize, color = Color.Gray, fontSize = 11.sp) }; Text("Ganti", color = SoftOrange, fontSize = 12.sp, fontWeight = FontWeight.Bold, modifier = Modifier.clickable { onActionClick() }) }
    }
}

@Composable
fun AttachmentCard(name: String, size: String, icon: ImageVector, onDelete: () -> Unit) {
    Row(modifier = Modifier.fillMaxWidth().background(Color.White).padding(12.dp), verticalAlignment = Alignment.CenterVertically) { Icon(icon, null, tint = Color.Gray, modifier = Modifier.size(18.dp)); Spacer(Modifier.width(12.dp)); Column(modifier = Modifier.weight(1f)) { Text(name, fontSize = 13.sp); Text(size, fontSize = 10.sp, color = Color.Gray) }; Icon(imageVector = Icons.Default.Delete, contentDescription = "Hapus", tint = Color(0xFF757575), modifier = Modifier.size(18.dp).clickable { onDelete() }) }
}

@Composable
fun ConfirmationDialog(title: String, message: String, confirmText: String, isDeleteType: Boolean, onDismiss: () -> Unit, onConfirm: () -> Unit) {
    Dialog(onDismissRequest = onDismiss) { Surface(shape = RoundedCornerShape(24.dp), color = Color.White, modifier = Modifier.fillMaxWidth()) { Column(Modifier.padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally) { if (isDeleteType) Icon(Icons.Outlined.Delete, null, tint = Color.Red, modifier = Modifier.size(48.dp)); Text(title, fontWeight = FontWeight.Bold, fontSize = 18.sp, textAlign = TextAlign.Center); Text(message, fontSize = 12.sp, textAlign = TextAlign.Center, color = Color.Gray); Spacer(Modifier.height(24.dp)); Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) { OutlinedButton(onClick = onDismiss, modifier = Modifier.weight(1f)) { Text("BATAL", color = Color.Black) }; Button(onClick = onConfirm, modifier = Modifier.weight(1f), colors = ButtonDefaults.buttonColors(containerColor = if(isDeleteType) Color(0xFFFFE5E5) else SoftOrange)) { Text(confirmText, color = if(isDeleteType) Color.Red else Color.White) } } } } }
}

fun Modifier.dashedBorder(width: Dp, color: Color, cornerRadius: Dp) = drawBehind { drawRoundRect(color = color, style = Stroke(width = width.toPx(), pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 10f), 0f)), cornerRadius = androidx.compose.ui.geometry.CornerRadius(cornerRadius.toPx())) }