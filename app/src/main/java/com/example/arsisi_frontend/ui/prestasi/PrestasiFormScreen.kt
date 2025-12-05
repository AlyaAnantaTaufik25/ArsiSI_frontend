package com.example.arsisi_frontend.ui.prestasi

import android.net.Uri
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
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.arsisi_frontend.data.model.*
import com.example.arsisi_frontend.ui.theme.*
import com.example.arsisi_frontend.utils.DateUtils
import java.util.Calendar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PrestasiFormScreen(
    prestasiId: Int? = null,  // ✅ Ganti arsipId → prestasiId
    onNavigateBack: () -> Unit,
    viewModel: PrestasiViewModel = viewModel()
) {
    val isEditMode = prestasiId != null
    val prestasiDetailState by viewModel.prestasiDetailState.collectAsState()  // ✅ Ganti nama
    val uploadState by viewModel.uploadState.collectAsState()
    val operationState by viewModel.operationState.collectAsState()

    var kategori by remember { mutableStateOf("Akademik") }  // ✅ Ubah default
    var judul by remember { mutableStateOf("") }
    var deskripsi by remember { mutableStateOf("") }
    var tanggal by remember { mutableStateOf(DateUtils.getCurrentDateApi()) }
    var filePath by remember { mutableStateOf<String?>(null) }
    var selectedFileUri by remember { mutableStateOf<Uri?>(null) }
    var selectedFileName by remember { mutableStateOf("") }

    var showKategoriDropdown by remember { mutableStateOf(false) }
    var showDatePicker by remember { mutableStateOf(false) }
    var showSuccessDialog by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    // File picker launcher
    val filePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            selectedFileUri = it
            selectedFileName = "File dipilih"
            viewModel.uploadFile(it, "prestasi")  // ✅ Ganti "arsip" → "prestasi"
        }
    }

    // Load data jika edit mode
    LaunchedEffect(prestasiId) {
        if (isEditMode && prestasiId != null) {
            viewModel.loadPrestasiDetail(prestasiId)
        }
    }

    // Set data dari detail jika edit mode - ✅ MAPPING Prestasi → UI
    LaunchedEffect(prestasiDetailState) {
        if (isEditMode && prestasiDetailState is UiState.Success<Prestasi>) {
            val prestasi = (prestasiDetailState as UiState.Success<Prestasi>).data
            kategori = prestasi.jenis  // jenis → kategori
            judul = prestasi.nama      // nama → judul
            deskripsi = prestasi.deskripsi
            tanggal = "${prestasi.tahun}-01-01"  // tahun → tanggal (sederhana)
            filePath = prestasi.filePath
            selectedFileName = prestasi.filePath?.substringAfterLast("/") ?: ""
        }
    }

    // Handle upload result
    LaunchedEffect(uploadState) {
        when (uploadState) {
            is UiState.Success -> {
                filePath = (uploadState as UiState.Success<String>).data
                viewModel.resetUploadState()
            }
            is UiState.Error -> {
                errorMessage = (uploadState as UiState.Error).message
                viewModel.resetUploadState()
            }
            else -> {}
        }
    }

    // Handle operation result
    LaunchedEffect(operationState) {
        when (operationState) {
            is UiState.Success -> {
                showSuccessDialog = true
                viewModel.resetOperationState()
            }
            is UiState.Error -> {
                errorMessage = (operationState as UiState.Error).message
                viewModel.resetOperationState()
            }
            else -> {}
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = if (isEditMode) "Edit Prestasi" else "Tambah Prestasi",  // ✅ Ganti teks
                        fontWeight = FontWeight.Bold,
                        color = TextWhite
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back",
                            tint = TextWhite
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Orange600
                )
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(BackgroundLight)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp)
            ) {
                // Header Card
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Orange600
                    )
                ) {
                    Text(
                        text = if (isEditMode) "Edit Prestasi" else "Tambah Prestasi Baru",  // ✅ Ganti teks
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = TextWhite,
                        modifier = Modifier.padding(20.dp)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Form Card - UI SAMA PERSIS
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = SurfaceWhite
                    )
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp)
                    ) {
                        // Kategori Dropdown - sekarang jadi Jenis Prestasi
                        Text(
                            text = "Jenis Prestasi",  // ✅ Ubah label
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.SemiBold,
                            color = TextPrimary
                        )
                        Spacer(modifier = Modifier.height(8.dp))

                        ExposedDropdownMenuBox(
                            expanded = showKategoriDropdown,
                            onExpandedChange = { showKategoriDropdown = it }
                        ) {
                            OutlinedTextField(
                                value = kategori,
                                onValueChange = {},
                                readOnly = true,
                                placeholder = { Text("Pilih Jenis") },
                                trailingIcon = {
                                    Icon(
                                        imageVector = if (showKategoriDropdown)
                                            Icons.Default.KeyboardArrowUp
                                        else
                                            Icons.Default.KeyboardArrowDown,
                                        contentDescription = "Dropdown"
                                    )
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .menuAnchor(),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = Orange600,
                                    unfocusedBorderColor = Grey300
                                )
                            )

                            ExposedDropdownMenu(
                                expanded = showKategoriDropdown,
                                onDismissRequest = { showKategoriDropdown = false }
                            ) {
                                listOf("Akademik", "Non-Akademik", "Organisasi").forEach { item ->  // ✅ Ubah opsi
                                    DropdownMenuItem(
                                        text = { Text(item) },
                                        onClick = {
                                            kategori = item
                                            showKategoriDropdown = false
                                        }
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Judul (mapping ke nama)
                        Text(
                            text = "Nama Prestasi",  // ✅ Ubah label
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.SemiBold,
                            color = TextPrimary
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        OutlinedTextField(
                            value = judul,
                            onValueChange = { judul = it },
                            placeholder = { Text("Masukkan nama prestasi") },
                            modifier = Modifier.fillMaxWidth(),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Orange600,
                                unfocusedBorderColor = Grey300
                            )
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        // Tanggal (mapping ke tahun)
                        Text(
                            text = "Tahun",  // ✅ Ubah label
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.SemiBold,
                            color = TextPrimary
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        OutlinedTextField(
                            value = tanggal.takeLast(4),  // ✅ Ambil tahun saja
                            onValueChange = { newTahun ->
                                val tahun = newTahun.toIntOrNull() ?: Calendar.getInstance().get(Calendar.YEAR)
                                tanggal = "${tahun}-01-01"
                            },
                            placeholder = { Text("2024") },
                            modifier = Modifier.fillMaxWidth(),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Orange600,
                                unfocusedBorderColor = Grey300
                            )
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        // Deskripsi
                        Text(
                            text = "Deskripsi",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.SemiBold,
                            color = TextPrimary
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        OutlinedTextField(
                            value = deskripsi,
                            onValueChange = { deskripsi = it },
                            placeholder = { Text("Masukkan deskripsi prestasi") },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(120.dp),
                            maxLines = 5,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Orange600,
                                unfocusedBorderColor = Grey300
                            )
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        // Upload File
                        Text(
                            text = "Upload File",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.SemiBold,
                            color = TextPrimary
                        )
                        Spacer(modifier = Modifier.height(8.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Button(
                                onClick = { filePickerLauncher.launch("application/pdf,image/*") },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Grey300,
                                    contentColor = TextPrimary
                                )
                            ) {
                                Text("Choose File")
                            }

                            Spacer(modifier = Modifier.width(12.dp))

                            Text(
                                text = if (selectedFileName.isNotEmpty())
                                    selectedFileName
                                else
                                    "No file chosen",
                                style = MaterialTheme.typography.bodyMedium,
                                color = TextSecondary
                            )
                        }

                        // Upload Progress
                        if (uploadState is UiState.Loading) {
                            Spacer(modifier = Modifier.height(8.dp))
                            LinearProgressIndicator(
                                modifier = Modifier.fillMaxWidth(),
                                color = Orange600
                            )
                            Text(
                                text = "Uploading...",
                                style = MaterialTheme.typography.bodySmall,
                                color = TextSecondary
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Action Buttons - ✅ MAPPING KE createPrestasi/updatePrestasi
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Batal Button
                    OutlinedButton(
                        onClick = onNavigateBack,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Batal")
                    }

                    // Simpan Button - ✅ PERBAIKAN LENGKAP
                    Button(
                        onClick = {
                            if (judul.isBlank() || deskripsi.isBlank()) {
                                errorMessage = "Harap isi nama dan deskripsi"
                                return@Button
                            }

                            val tahun = tanggal.takeLast(4).toIntOrNull() ?: 2024

                            if (isEditMode && prestasiId != null) {  // ✅ Ganti arsipId → prestasiId
                                viewModel.updatePrestasi(
                                    prestasiId = prestasiId,           // ✅ Benar
                                    nama = judul,                      // ✅ judul → nama
                                    jenis = kategori,                  // ✅ kategori → jenis
                                    tingkat = "Lokal",                 // ✅ Tambah wajib
                                    tahun = tahun,                     // ✅ tanggal → tahun
                                    penyelenggara = "Sistem ARSISI",   // ✅ Tambah wajib
                                    deskripsi = deskripsi,
                                    filePath = filePath
                                )
                            } else {
                                viewModel.createPrestasi(
                                    nama = judul,                      // ✅ judul → nama
                                    jenis = kategori,                  // ✅ kategori → jenis
                                    tingkat = "Lokal",                 // ✅ Tambah wajib
                                    tahun = tahun,                     // ✅ tanggal → tahun
                                    penyelenggara = "Sistem ARSISI",   // ✅ Tambah wajib
                                    deskripsi = deskripsi,
                                    filePath = filePath
                                )
                            }
                        },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Orange600
                        ),
                        enabled = operationState !is UiState.Loading
                    ) {
                        if (operationState is UiState.Loading) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(20.dp),
                                color = TextWhite,
                                strokeWidth = 2.dp
                            )
                        } else {
                            Icon(
                                imageVector = Icons.Default.Save,
                                contentDescription = "Save",
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Simpan")
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))
            }

            // Error Snackbar
            errorMessage?.let { error ->
                Snackbar(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(16.dp),
                    action = {
                        TextButton(onClick = { errorMessage = null }) {
                            Text("OK", color = TextWhite)
                        }
                    },
                    containerColor = ErrorRed
                ) {
                    Text(error)
                }
            }
        }
    }

    // Date Picker Dialog - Simplified untuk tahun saja
    if (showDatePicker) {
        DatePickerDialog(
            currentDate = tanggal,
            onDateSelected = { selectedDate ->
                tanggal = selectedDate
                showDatePicker = false
            },
            onDismiss = { showDatePicker = false }
        )
    }

    // Success Dialog
    if (showSuccessDialog) {
        AlertDialog(  // ✅ Ganti SuccessDialog → AlertDialog
            onDismissRequest = { showSuccessDialog = false },
            title = { Text("Berhasil!") },
            text = {
                Text(
                    text = if (isEditMode) "Prestasi berhasil diperbarui" else "Prestasi berhasil disimpan"
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        showSuccessDialog = false
                        onNavigateBack()
                    }
                ) {
                    Text("OK")
                }
            }
        )
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DatePickerDialog(
    currentDate: String,
    onDateSelected: (String) -> Unit,
    onDismiss: () -> Unit
) {
    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = DateUtils.parseApiDate(currentDate)?.time ?: System.currentTimeMillis()
    )

    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(
                onClick = {
                    datePickerState.selectedDateMillis?.let { millis ->
                        val selectedDate = DateUtils.timestampToApiFormat(millis)
                        onDateSelected(selectedDate)
                    }
                }
            ) {
                Text("OK", color = Orange600)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Batal", color = TextSecondary)
            }
        },
        text = {
            DatePicker(
                state = datePickerState,
                colors = DatePickerDefaults.colors(
                    selectedDayContainerColor = Orange600
                )
            )
        }
    )
}