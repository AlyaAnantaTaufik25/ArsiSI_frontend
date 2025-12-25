package com.example.arsisi_frontend.ui.akademik

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
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
import androidx.compose.material.icons.outlined.*
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
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import com.example.arsisi_frontend.data.model.Attachment
import com.example.arsisi_frontend.ui.theme.SoftOrange
import java.io.File

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AkademikFormScreen(
    documentId: Int? = null,
    viewModel: AkademikViewModel,
    onBackClick: () -> Unit,
    onSuccess: () -> Unit
) {
    val context = LocalContext.current
    val isEditMode = documentId != null
    
    // State untuk error & loading (HARUS SEBELUM LaunchedEffect)
    var titleError by remember { mutableStateOf("") }
    var fileError by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    var shouldNavigate by remember { mutableStateOf(false) }
    
    // Load document jika edit mode
    LaunchedEffect(documentId) {
        if (isEditMode && documentId != null) {
            viewModel.loadDocumentById(documentId)
        }
    }
    
    // Observe error dan loading state dari ViewModel
    val vmError by viewModel.error
    val vmIsLoading by viewModel.isLoading
    
    // Auto navigate setelah berhasil save
    LaunchedEffect(vmIsLoading, vmError, shouldNavigate) {
        if (shouldNavigate && !vmIsLoading) {
            if (vmError == null) {
                // Berhasil save
                onSuccess()
            }
            // Reset flags
            isLoading = false
            shouldNavigate = false
        }
    }
    
    val existingDoc = if (isEditMode) {
        viewModel.currentDocument.value ?: viewModel.getDocumentById(documentId!!)
    } else null

    var title by remember { mutableStateOf(existingDoc?.judul ?: "") }
    var description by remember { mutableStateOf(existingDoc?.deskripsi ?: "") }
    var selectedFileName by remember { mutableStateOf(existingDoc?.file_name ?: "") }
    var selectedFileSize by remember { mutableStateOf(existingDoc?.file_size ?: "") }
    var selectedFileUri by remember { mutableStateOf(existingDoc?.file_path ?: "") }
    var hasFile by remember { mutableStateOf(isEditMode || selectedFileName.isNotEmpty()) }


    // === STATE LAMPIRAN ===
    val attachmentList = remember {
        mutableStateListOf<Attachment>().apply {
            if (existingDoc != null) addAll(existingDoc.attachments)
        }
    }

    var showDeleteDocDialog by remember { mutableStateOf(false) }
    var showSaveDialog by remember { mutableStateOf(false) }
    var showReplaceFileDialog by remember { mutableStateOf(false) }
    var showDeleteAttachmentDialog by remember { mutableStateOf(false) }
    var showFileSourceDialog by remember { mutableStateOf(false) } // Dialog pilih sumber file utama
    var showAttachmentSourceDialog by remember { mutableStateOf(false) } // Dialog pilih sumber lampiran
    var showAttachmentNameDialog by remember { mutableStateOf(false) } // Dialog input nama/keterangan lampiran
    var attachmentToDeleteIndex by remember { mutableIntStateOf(-1) }
    
    // State untuk menyimpan file yang baru dipilih sebelum input nama
    var pendingAttachmentFile by remember { mutableStateOf<Triple<String, String, String>?>(null) }
    var attachmentName by remember { mutableStateOf("") } // Nama/keterangan untuk lampiran baru
    var attachmentNameError by remember { mutableStateOf("") } // Error untuk validasi nama

    // File untuk kamera
    var photoUri by remember { mutableStateOf<Uri?>(null) }
    val photoFile = remember { 
        File(context.getExternalFilesDir("Pictures"), "IMG_${System.currentTimeMillis()}.jpg")
    }

    // === LAUNCHER UTAMA ===
    val mainFileLauncher = rememberLauncherForActivityResult(contract = ActivityResultContracts.OpenDocument()) { uri: Uri? ->
        uri?.let {
            try { context.contentResolver.takePersistableUriPermission(it, android.content.Intent.FLAG_GRANT_READ_URI_PERMISSION) } catch (e: Exception) {}
            val fileInfo = getFileNameAndSize(context, it)
            selectedFileName = fileInfo.first
            selectedFileSize = fileInfo.second
            selectedFileUri = it.toString()
            hasFile = true
            fileError = "" // Clear error saat file dipilih
        }
    }

    // Launcher kamera untuk file utama (harus didefinisikan sebelum cameraPermissionLauncher)
    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success ->
        if (success && photoUri != null) {
            photoUri?.let { uri ->
                val fileInfo = getFileNameAndSize(context, uri)
                selectedFileName = fileInfo.first
                selectedFileSize = fileInfo.second
                selectedFileUri = uri.toString()
                hasFile = true
                fileError = "" // Clear error saat file dipilih
            }
        }
    }

    // Permission launcher untuk kamera
    val cameraPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            launchCamera(context, photoFile) { uri ->
                photoUri = uri
                cameraLauncher.launch(uri)
            }
        }
    }

    // === LAUNCHER LAMPIRAN ===
    val attachmentLauncher = rememberLauncherForActivityResult(contract = ActivityResultContracts.OpenDocument()) { uri: Uri? ->
        uri?.let {
            try { context.contentResolver.takePersistableUriPermission(it, android.content.Intent.FLAG_GRANT_READ_URI_PERMISSION) } catch (e: Exception) {}
            val fileInfo = getFileNameAndSize(context, it)
            // Simpan sementara, lalu tampilkan dialog input nama
            pendingAttachmentFile = Triple(fileInfo.first, fileInfo.second, it.toString())
            // Set nama default dari nama file (tanpa ekstensi untuk lebih clean)
            attachmentName = fileInfo.first.substringBeforeLast(".")
            attachmentNameError = ""
            showAttachmentNameDialog = true
        }
    }

    // Launcher kamera untuk lampiran
    val attachmentCameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success ->
        if (success && photoUri != null) {
            photoUri?.let { uri ->
                val fileInfo = getFileNameAndSize(context, uri)
                // Simpan sementara, lalu tampilkan dialog input nama
                pendingAttachmentFile = Triple(fileInfo.first, fileInfo.second, uri.toString())
                // Set nama default dari nama file (tanpa ekstensi untuk lebih clean)
                attachmentName = fileInfo.first.substringBeforeLast(".")
                attachmentNameError = ""
                showAttachmentNameDialog = true
            }
        }
    }

    val categories = listOf("Administrasi", "Akademik", "Laporan", "Lainnya")
    var selectedCategory by remember {
        mutableStateOf(if (isEditMode && existingDoc != null) categories.indexOf(existingDoc.kategori).coerceAtLeast(0) else 0)
    }

    // Fungsi validasi
    fun validateForm(): Boolean {
        titleError = ""
        fileError = ""
        var isValid = true
        
        if (title.isEmpty()) {
            titleError = "Judul wajib diisi"
            isValid = false
        } else if (title.length < 3) {
            titleError = "Judul minimal 3 karakter"
            isValid = false
        }
        
        if (!hasFile) {
            fileError = "File utama wajib diisi"
            isValid = false
        }
        
        return isValid
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
            SectionLabel("Judul *")
            OutlinedTextField(
                value = title,
                onValueChange = { 
                    title = it
                    titleError = ""
                },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedBorderColor = if (titleError.isNotEmpty()) Color.Red else Color(0xFFE0E0E0),
                    focusedBorderColor = SoftOrange,
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White,
                    focusedTextColor = Color.Black,
                    unfocusedTextColor = Color.Black
                ),
                singleLine = true,
                isError = titleError.isNotEmpty(),
                supportingText = if (titleError.isNotEmpty()) {
                    { Text(titleError, color = Color.Red, fontSize = 12.sp) }
                } else null
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
                value = description, 
                onValueChange = { description = it },
                modifier = Modifier.fillMaxWidth().height(120.dp),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedBorderColor = Color(0xFFE0E0E0),
                    focusedBorderColor = SoftOrange,
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White,
                    focusedTextColor = Color.Black,
                    unfocusedTextColor = Color.Black
                ),
                maxLines = 5
            )
            Spacer(Modifier.height(20.dp))

            SectionLabel("File Utama *")
            if (hasFile) {
                FileCardItem(selectedFileName, selectedFileSize, { 
                    showReplaceFileDialog = true
                    fileError = "" // Clear error saat ganti file
                })
            } else {
                // Tombol upload (satu button) dengan error message
                Column {
                    UploadPlaceholder { 
                        showFileSourceDialog = true
                        fileError = "" // Clear error saat klik upload
                    }
                    if (fileError.isNotEmpty()) {
                        Spacer(Modifier.height(4.dp))
                        Text(
                            text = fileError,
                            color = Color.Red,
                            fontSize = 12.sp,
                            modifier = Modifier.padding(start = 4.dp)
                        )
                    }
                }
            }
            Spacer(Modifier.height(20.dp))

            SectionLabel("Lampiran Pendukung")
            // Render List Lampiran
            attachmentList.forEachIndexed { index, attachment ->
                AttachmentCard(
                    file_name = attachment.file_name,
                    file_size = attachment.file_size,
                    description = attachment.description,
                    icon = Icons.Outlined.Description
                ) {
                    attachmentToDeleteIndex = index
                    showDeleteAttachmentDialog = true
                }
                Spacer(Modifier.height(8.dp))
            }
            // Tombol tambah lampiran (satu button)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .dashedBorder(1.dp, SoftOrange, 10.dp)
                    .background(Color(0xFFFFF5F0))
                    .clickable { 
                        showAttachmentSourceDialog = true
                    },
                contentAlignment = Alignment.Center
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Default.AddCircle,
                        null,
                        tint = SoftOrange,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(
                        "Tambah Lampiran",
                        color = SoftOrange,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                }
            }
            Spacer(Modifier.height(40.dp))

            Button(
                onClick = {
                    if (!validateForm()) {
                        return@Button
                    }
                    isLoading = true
                    viewModel.clearError() // Clear error sebelum submit
                    if (isEditMode) {
                        showSaveDialog = true
                    } else {
                        viewModel.addDocument(
                            title,
                            categories[selectedCategory],
                            description,
                            selectedFileName,
                            selectedFileSize,
                            selectedFileUri,
                            attachmentList.toList()
                        )
                        shouldNavigate = true // Set flag untuk auto-navigate setelah loading selesai
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(containerColor = SoftOrange),
                enabled = !isLoading && !vmIsLoading
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        color = Color.White
                    )
                } else {
                    Text(
                        if (isEditMode) "Simpan Perubahan" else "Simpan Dokumen",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                }
            }
            Spacer(Modifier.height(16.dp))
            
            // Tampilkan error dari ViewModel
            if (vmError != null) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFFFEBEE)),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Default.Error,
                            contentDescription = null,
                            tint = Color.Red,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(Modifier.width(8.dp))
                        Text(
                            vmError!!,
                            color = Color.Red,
                            fontSize = 13.sp
                        )
                    }
                }
                Spacer(Modifier.height(16.dp))
            }
            
            if (isEditMode) {
                TextButton(onClick = { showDeleteDocDialog = true }, modifier = Modifier.fillMaxWidth()) { Text("Hapus Dokumen", color = Color(0xFFEF5350), fontWeight = FontWeight.SemiBold, fontSize = 14.sp) }
            }
            Spacer(Modifier.height(30.dp))
        }

        // DIALOGS
        if (showDeleteDocDialog) ConfirmationDialog("Hapus Dokumen?", "Data tidak dapat dikembalikan.", "HAPUS", true, { showDeleteDocDialog = false }) { viewModel.deleteDocument(documentId!!); showDeleteDocDialog = false; onSuccess() }
        if (showSaveDialog) ConfirmationDialog(
            "Simpan Perubahan?",
            "Yakin simpan perubahan?",
            "YAKIN",
            false,
            { 
                showSaveDialog = false
                isLoading = false
            }
        ) {
            viewModel.updateDocument(
                documentId!!,
                title,
                categories[selectedCategory],
                description,
                existingDoc?.tanggal ?: "",  // Preserve original date
                selectedFileName,
                selectedFileSize,
                selectedFileUri,
                attachmentList.toList()
            )
            showSaveDialog = false
            isLoading = false
            shouldNavigate = true  // Trigger auto-navigate after update completes
        }
        if (showReplaceFileDialog) {
            ConfirmationDialog(
                "Ganti File Utama?",
                "File lama akan diganti.",
                "GANTI",
                false,
                { showReplaceFileDialog = false }
            ) {
                showReplaceFileDialog = false
                showFileSourceDialog = true
            }
        }
        
        // Dialog pilih sumber file utama
        if (showFileSourceDialog) {
            AlertDialog(
                onDismissRequest = { showFileSourceDialog = false },
                title = { Text("Pilih Sumber File") },
                text = {
                    Column {
                        Text("Dari mana Anda ingin mengambil file?")
                    }
                },
                confirmButton = {
                    TextButton(onClick = {
                        showFileSourceDialog = false
                        mainFileLauncher.launch(arrayOf("application/pdf", "image/*", "application/msword"))
                    }) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.UploadFile, null, modifier = Modifier.size(18.dp))
                            Spacer(Modifier.width(8.dp))
                            Text("Pilih File")
                        }
                    }
                },
                dismissButton = {
                    TextButton(onClick = {
                        showFileSourceDialog = false
                        when {
                            ContextCompat.checkSelfPermission(
                                context,
                                Manifest.permission.CAMERA
                            ) == PackageManager.PERMISSION_GRANTED -> {
                                launchCamera(context, photoFile) { uri ->
                                    photoUri = uri
                                    cameraLauncher.launch(uri)
                                }
                            }
                            else -> {
                                cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
                            }
                        }
                    }) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.CameraAlt, null, modifier = Modifier.size(18.dp))
                            Spacer(Modifier.width(8.dp))
                            Text("Ambil Foto")
                        }
                    }
                }
            )
        }
        
        // Dialog pilih sumber lampiran
        if (showAttachmentSourceDialog) {
            AlertDialog(
                onDismissRequest = { showAttachmentSourceDialog = false },
                title = { Text("Pilih Sumber Lampiran") },
                text = {
                    Column {
                        Text("Dari mana Anda ingin mengambil lampiran?")
                    }
                },
                confirmButton = {
                    TextButton(onClick = {
                        showAttachmentSourceDialog = false
                        attachmentLauncher.launch(arrayOf("application/pdf", "image/*"))
                    }) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.UploadFile, null, modifier = Modifier.size(18.dp))
                            Spacer(Modifier.width(8.dp))
                            Text("Pilih File")
                        }
                    }
                },
                dismissButton = {
                    TextButton(onClick = {
                        showAttachmentSourceDialog = false
                        when {
                            ContextCompat.checkSelfPermission(
                                context,
                                Manifest.permission.CAMERA
                            ) == PackageManager.PERMISSION_GRANTED -> {
                                val attachmentPhotoFile = File(
                                    context.getExternalFilesDir("Pictures"),
                                    "ATTACH_${System.currentTimeMillis()}.jpg"
                                )
                                launchCamera(context, attachmentPhotoFile) { uri ->
                                    photoUri = uri
                                    attachmentCameraLauncher.launch(uri)
                                }
                            }
                            else -> {
                                cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
                            }
                        }
                    }) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.CameraAlt, null, modifier = Modifier.size(18.dp))
                            Spacer(Modifier.width(8.dp))
                            Text("Ambil Foto")
                        }
                    }
                }
            )
        }
        
        // Dialog input nama/keterangan lampiran
        if (showAttachmentNameDialog && pendingAttachmentFile != null) {
            AlertDialog(
                onDismissRequest = { 
                    showAttachmentNameDialog = false
                    pendingAttachmentFile = null
                    attachmentName = ""
                    attachmentNameError = ""
                },
                title = { Text("Nama/Keterangan File") },
                text = {
                    Column {
                        Text(
                            "Berikan nama atau keterangan untuk file ini",
                            fontSize = 14.sp,
                            color = Color.Gray
                        )
                        Spacer(Modifier.height(12.dp))
                        OutlinedTextField(
                            value = attachmentName,
                            onValueChange = { 
                                // Validasi maksimal 100 karakter
                                if (it.length <= 100) {
                                    attachmentName = it
                                    attachmentNameError = ""
                                } else {
                                    attachmentNameError = "Maksimal 100 karakter"
                                }
                            },
                            modifier = Modifier.fillMaxWidth(),
                            placeholder = { Text("Masukkan nama/keterangan file") },
                            singleLine = true,
                            isError = attachmentNameError.isNotEmpty(),
                            supportingText = {
                                if (attachmentNameError.isNotEmpty()) {
                                    Text(attachmentNameError, color = Color.Red, fontSize = 12.sp)
                                } else {
                                    Text("${attachmentName.length}/100 karakter", fontSize = 12.sp, color = Color.Gray)
                                }
                            },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = if (attachmentNameError.isNotEmpty()) Color.Red else SoftOrange,
                                unfocusedBorderColor = if (attachmentNameError.isNotEmpty()) Color.Red else Color(0xFFE0E0E0),
                                focusedContainerColor = Color.White,
                                unfocusedContainerColor = Color.White,
                                focusedTextColor = Color.Black,
                                unfocusedTextColor = Color.Black
                            )
                        )
                        Spacer(Modifier.height(8.dp))
                        Text(
                            "File asli: ${pendingAttachmentFile!!.first}",
                            fontSize = 12.sp,
                            color = Color.Gray,
                            fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
                        )
                    }
                },
                confirmButton = {
                    TextButton(
                        onClick = {
                            // Validasi sebelum simpan
                            if (attachmentName.trim().isEmpty()) {
                                attachmentNameError = "Nama tidak boleh kosong"
                                return@TextButton
                            }
                            if (attachmentName.length > 100) {
                                attachmentNameError = "Maksimal 100 karakter"
                                return@TextButton
                            }
                            
                            val (fileName, fileSize, fileUri) = pendingAttachmentFile!!
                            attachmentList.add(
                                Attachment(
                                    file_name = fileName,
                                    file_size = fileSize,
                                    file_path = fileUri,
                                    description = attachmentName.trim()
                                )
                            )
                            showAttachmentNameDialog = false
                            pendingAttachmentFile = null
                            attachmentName = ""
                            attachmentNameError = ""
                        },
                        enabled = attachmentName.trim().isNotEmpty() && attachmentName.length <= 100
                    ) {
                        Text("Simpan")
                    }
                },
                dismissButton = {
                    TextButton(onClick = {
                        showAttachmentNameDialog = false
                        pendingAttachmentFile = null
                        attachmentName = ""
                        attachmentNameError = ""
                    }) {
                        Text("Batal")
                    }
                }
            )
        }
        
        if (showDeleteAttachmentDialog) {
            ConfirmationDialog(
                "Hapus Lampiran?",
                "Lampiran akan dihapus.",
                "HAPUS",
                true,
                { showDeleteAttachmentDialog = false }
            ) {
                if (attachmentToDeleteIndex != -1) {
                    attachmentList.removeAt(attachmentToDeleteIndex)
                }
                showDeleteAttachmentDialog = false
            }
        }
    }
}

// ... (Helper Functions getFileNameAndSize, SectionLabel, UploadPlaceholder, FileCardItem, AttachmentCard, ConfirmationDialog, dashedBorder SAMA SEPERTI SEBELUMNYA) ...
// Copy paste saja dari file sebelumnya, tidak ada perubahan di helper
fun launchCamera(context: Context, photoFile: File, onUriReady: (Uri) -> Unit) {
    val photoUri = FileProvider.getUriForFile(
        context,
        "${context.packageName}.fileprovider",
        photoFile
    )
    onUriReady(photoUri)
}

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
fun AttachmentCard(    file_name: String,    file_size: String, 
    description: String,
    icon: ImageVector, 
    onDelete: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(icon, null, tint = Color.Gray, modifier = Modifier.size(18.dp))
        Spacer(Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            // Tampilkan description jika ada, jika tidak tampilkan fileName
            Text(
                text = if (description.isNotEmpty()) description else file_name,
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium
            )
            if (description.isNotEmpty()) {
                Text(
                    text = file_name,
                    fontSize = 11.sp,
                    color = Color.Gray,
                    fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
                )
            }
            Text(
                text = file_size,
                fontSize = 10.sp,
                color = Color.Gray
            )
        }
        Icon(
            imageVector = Icons.Default.Delete,
            contentDescription = "Hapus",
            tint = Color(0xFF757575),
            modifier = Modifier
                .size(18.dp)
                .clickable { onDelete() }
        )
    }
}

@Composable
fun ConfirmationDialog(title: String, message: String, confirmText: String, isDeleteType: Boolean, onDismiss: () -> Unit, onConfirm: () -> Unit) {
    Dialog(onDismissRequest = onDismiss) { Surface(shape = RoundedCornerShape(24.dp), color = Color.White, modifier = Modifier.fillMaxWidth()) { Column(Modifier.padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally) { if (isDeleteType) Icon(Icons.Outlined.Delete, null, tint = Color.Red, modifier = Modifier.size(48.dp)); Text(title, fontWeight = FontWeight.Bold, fontSize = 18.sp, textAlign = TextAlign.Center); Text(message, fontSize = 12.sp, textAlign = TextAlign.Center, color = Color.Gray); Spacer(Modifier.height(24.dp)); Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) { OutlinedButton(onClick = onDismiss, modifier = Modifier.weight(1f)) { Text("BATAL", color = Color.Black) }; Button(onClick = onConfirm, modifier = Modifier.weight(1f), colors = ButtonDefaults.buttonColors(containerColor = if(isDeleteType) Color(0xFFFFE5E5) else SoftOrange)) { Text(confirmText, color = if(isDeleteType) Color.Red else Color.White) } } } } }
}

fun Modifier.dashedBorder(width: Dp, color: Color, cornerRadius: Dp) = drawBehind { drawRoundRect(color = color, style = Stroke(width = width.toPx(), pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 10f), 0f)), cornerRadius = androidx.compose.ui.geometry.CornerRadius(cornerRadius.toPx())) }

