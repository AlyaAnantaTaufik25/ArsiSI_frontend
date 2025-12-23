package com.example.arsisi_frontend.ui.prestasi

import android.app.DatePickerDialog
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.arsisi_frontend.data.model.UiState
import com.example.arsisi_frontend.ui.theme.Orange600
import java.util.Calendar
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.material3.TextFieldDefaults


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PrestasiFormScreen(
    viewModel: PrestasiViewModel,
    prestasiId: Int? = null,
    onNavigateBack: () -> Unit
) {
    val context = LocalContext.current
    val calendar = remember { Calendar.getInstance() }

    // ===== STATE FORM =====
    var kategori by remember { mutableStateOf("Prestasi") }
    var judul by remember { mutableStateOf("") }
    var tanggal by remember { mutableStateOf("") }
    var deskripsi by remember { mutableStateOf("") }
    var selectedFileUri by remember { mutableStateOf<Uri?>(null) }

    var showKategoriMenu by remember { mutableStateOf(false) }
    var showSuccessDialog by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    val operationState by viewModel.operationState.collectAsStateWithLifecycle()
    val isEditMode = prestasiId != null

    // File picker
    val filePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        selectedFileUri = uri
    }

    // ===== DATE PICKER =====
    fun openDatePicker() {
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        DatePickerDialog(
            context,
            { _, y, m, d ->
                val monthDisplay = (m + 1).toString().padStart(2, '0')
                val dayDisplay = d.toString().padStart(2, '0')
                tanggal = "$y-$monthDisplay-$dayDisplay"
            },
            year, month, day
        ).show()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = if (isEditMode) "Edit Arsip" else "Tambah Arsip",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Orange600),
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, "Kembali", tint = Color.White)
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(Color(0xFFF5F5F5))
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // ===== FORM CARD =====
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // KATEGORI DROPDOWN
                    Column {
                        Text(
                            text = "Kategori",
                            fontSize = 13.sp,
                            color = Color(0xFF666666),
                            fontWeight = FontWeight.SemiBold
                        )
                        Spacer(Modifier.height(6.dp))
                        ExposedDropdownMenuBox(
                            expanded = showKategoriMenu,
                            onExpandedChange = { showKategoriMenu = !showKategoriMenu }
                        ) {
                            OutlinedTextField(
                                value = kategori,
                                onValueChange = {},
                                readOnly = true,
                                modifier = Modifier.menuAnchor().fillMaxWidth(),
                                trailingIcon = {
                                    ExposedDropdownMenuDefaults.TrailingIcon(expanded = showKategoriMenu)
                                },
                                colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = Orange600),
                                shape = RoundedCornerShape(12.dp)
                            )
                            ExposedDropdownMenu(
                                expanded = showKategoriMenu,
                                onDismissRequest = { showKategoriMenu = false }
                            ) {
                                listOf("Prestasi", "Sertifikat", "Organisasi").forEach { item ->
                                    DropdownMenuItem(
                                        text = { Text(item) },
                                        onClick = {
                                            kategori = item
                                            showKategoriMenu = false
                                        }
                                    )
                                }
                            }
                        }
                    }

                    // JUDUL
                    OutlinedTextField(
                        value = judul,
                        onValueChange = { judul = it },
                        label = { Text("Judul") },
                        modifier = Modifier.fillMaxWidth(),
                        isError = judul.isBlank() && errorMessage != null,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Orange600,
                            errorBorderColor = Color.Red
                        ),
                        shape = RoundedCornerShape(12.dp)
                    )

                    // TANGGAL ✅ FIXED
                    // TANGGAL - VERSI FIX ✅
                    TextField(
                        value = tanggal.ifEmpty { "Pilih tanggal..." },
                        onValueChange = { },  // Kosongkan
                        label = { Text("Tanggal") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = null,  // Hilangkan ripple effect
                                onClick = { openDatePicker() }
                            ),
                        trailingIcon = {
                            IconButton(
                                onClick = { openDatePicker() }  // ✅ DOUBLE TRIGGER
                            ) {
                                Icon(
                                    Icons.Default.CalendarMonth,
                                    contentDescription = "Pilih tanggal",
                                    tint = Color(0xFF757575)
                                )
                            }
                        },
                        readOnly = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Orange600,
                            unfocusedBorderColor = if (tanggal.isBlank()) Color(0xFFFFCDD2) else Orange600,
                            errorBorderColor = Color.Red
                        ),
                        shape = RoundedCornerShape(12.dp),
                        singleLine = true
                    )


                    // DESKRIPSI
                    OutlinedTextField(
                        value = deskripsi,
                        onValueChange = { deskripsi = it },
                        label = { Text("Deskripsi") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(120.dp),
                        maxLines = 5,
                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = Orange600),
                        shape = RoundedCornerShape(12.dp)
                    )

                    // UPLOAD FILE
                    OutlinedButton(
                        onClick = { filePickerLauncher.launch("*/*") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(Icons.Default.AttachFile, contentDescription = null)
                            Text(
                                if (selectedFileUri != null) "File dipilih (${selectedFileUri?.lastPathSegment})"
                                else "Upload File (Opsional)"
                            )
                        }
                    }
                }
            }

            // ===== BUTTONS =====
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedButton(
                    onClick = onNavigateBack,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Batal")
                }

                Button(
                    onClick = {
                        // VALIDASI LENGKAP ✅
                        if (judul.isBlank()) {
                            errorMessage = "Judul tidak boleh kosong"
                            return@Button
                        }
                        if (tanggal.isBlank()) {
                            errorMessage = "Silakan pilih tanggal terlebih dahulu"
                            return@Button
                        }
                        if (deskripsi.isBlank()) {
                            errorMessage = "Deskripsi tidak boleh kosong"
                            return@Button
                        }

                        isLoading = true
                        errorMessage = null

                        // MAPPING KE VIEWMODEL ✅
                        val jenisParam = kategori.lowercase()
                        val tingkatParam = "Umum"
                        val tahunParam = tanggal.takeLast(4).toIntOrNull() ?: 2025

                        if (isEditMode && prestasiId != null) {
                            viewModel.updatePrestasi(
                                prestasiId = prestasiId,
                                nama = judul,
                                jenis = jenisParam,
                                tingkat = tingkatParam,
                                tahun = tahunParam,
                                penyelenggara = null,
                                deskripsi = deskripsi,
                                tanggal = tanggal,  // ✅ TAMBAH TANGGAL
                                fileUri = selectedFileUri
                            )
                        } else {
                            viewModel.createPrestasi(
                                nama = judul,
                                jenis = jenisParam,
                                tingkat = tingkatParam,
                                tahun = tahunParam,
                                penyelenggara = null,
                                deskripsi = deskripsi,
                                tanggal = tanggal,  // ✅ TAMBAH TANGGAL
                                fileUri = selectedFileUri
                            )
                        }
                    },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Orange600),
                    enabled = !isLoading
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(20.dp),
                            color = Color.White,
                            strokeWidth = 2.dp
                        )
                    } else {
                        Text("Simpan")
                    }
                }
            }
        }
    }

    // HANDLE STATE
    LaunchedEffect(operationState) {
        when (val state = operationState) {
            is UiState.Success<*> -> {
                showSuccessDialog = true
                isLoading = false
            }
            is UiState.Error -> {
                errorMessage = state.message
                isLoading = false
            }
            is UiState.Loading -> {
                isLoading = true
            }
            else -> {}
        }
    }

    // SUCCESS DIALOG
    if (showSuccessDialog) {
        AlertDialog(
            onDismissRequest = { showSuccessDialog = false },
            icon = { Icon(Icons.Default.CheckCircle, contentDescription = null, tint = Orange600, modifier = Modifier.size(48.dp)) },
            title = { Text(if (isEditMode) "Berhasil Diupdate!" else "Arsip Ditambahkan!") },
            text = { Text("Data arsip telah ${if (isEditMode) "diperbarui" else "disimpan"}.") },
            confirmButton = {
                TextButton(onClick = {
                    showSuccessDialog = false
                    onNavigateBack()
                }) { Text("OK") }
            }
        )
    }

    // ERROR TOAST
    errorMessage?.let {
        LaunchedEffect(it) {
            kotlinx.coroutines.delay(3000)
            errorMessage = null
        }
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.BottomCenter) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFFFEBEE)),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text(
                    text = it,
                    modifier = Modifier.padding(12.dp),
                    color = Color.Red
                )
            }
        }
    }
}
