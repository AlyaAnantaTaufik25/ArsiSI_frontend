package com.example.arsisi_frontend.ui.tugas

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.arsisi_frontend.ui.theme.PrimaryOrange
import com.example.arsisi_frontend.ui.theme.TextDark
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TugasFormScreen(
    tugasId: Int? = null,
    viewModel: TugasViewModel,
    onNavigateBack: () -> Unit,
    onSuccessSubmit: () -> Unit
) {
    val state by viewModel.formState.collectAsStateWithLifecycle()

    LaunchedEffect(tugasId) {
        if (tugasId != null) {
            viewModel.loadTugasForEdit(tugasId)
        } else {
            viewModel.resetFormState()
        }
    }

    Scaffold(
        topBar = {
            Surface(color = PrimaryOrange, shadowElevation = 4.dp) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(start = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(onClick = onNavigateBack) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Kembali",
                                tint = Color.White
                            )
                        }
                    }
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 72.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = if (tugasId == null) "Tambah Tugas Baru" else "Edit Tugas",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.titleMedium
                        )
                    }
                }
            }
        },
        containerColor = Color(0xFFF2F2F2)
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp, vertical = 12.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Card(
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        OutlinedTextField(
                            value = state.judul,
                            onValueChange = viewModel::onJudulChange,
                            label = { Text("Judul Tugas") },
                            placeholder = { Text("Contoh : Tugas Use Case Diagram") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true
                        )

                        Text(
                            text = "Mata Kuliah",
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.SemiBold,
                            color = TextDark
                        )

                        var matkulExpanded by remember { mutableStateOf(false) }

                        ExposedDropdownMenuBox(
                            expanded = matkulExpanded,
                            onExpandedChange = { matkulExpanded = it }
                        ) {
                            val matkulSelected =
                                state.mataKuliahList.firstOrNull { it.matakuliahId == state.selectedMatkulId }
                            OutlinedTextField(
                                value = matkulSelected?.namaMatakuliah ?: "Pilih mata kuliah",
                                onValueChange = {},
                                readOnly = true,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .menuAnchor(),
                                trailingIcon = {
                                    ExposedDropdownMenuDefaults.TrailingIcon(expanded = matkulExpanded)
                                },
                                colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors()
                            )

                            ExposedDropdownMenu(
                                expanded = matkulExpanded,
                                onDismissRequest = { matkulExpanded = false }
                            ) {
                                state.mataKuliahList.forEach { matkul ->
                                    DropdownMenuItem(
                                        text = { Text(matkul.namaMatakuliah) },
                                        onClick = {
                                            viewModel.onMatkulSelected(matkul.matakuliahId)
                                            matkulExpanded = false
                                        }
                                    )
                                }
                            }
                        }

                        Text(
                            text = "Tipe Tugas",
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.SemiBold,
                            color = TextDark
                        )

                        val tipeList = listOf("Individu", "Kelompok")
                        var tipeExpanded by remember { mutableStateOf(false) }

                        ExposedDropdownMenuBox(
                            expanded = tipeExpanded,
                            onExpandedChange = { tipeExpanded = it }
                        ) {
                            OutlinedTextField(
                                value = state.tipe_tugas ?: "Pilih Tipe Tugas",
                                onValueChange = {},
                                readOnly = true,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .menuAnchor(),
                                trailingIcon = {
                                    ExposedDropdownMenuDefaults.TrailingIcon(expanded = tipeExpanded)
                                },
                                colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors()
                            )

                            ExposedDropdownMenu(
                                expanded = tipeExpanded,
                                onDismissRequest = { tipeExpanded = false }
                            ) {
                                tipeList.forEach { tipe ->
                                    DropdownMenuItem(
                                        text = { Text(tipe) },
                                        onClick = {
                                            viewModel.onTypeSelected(tipe)
                                            tipeExpanded = false
                                        }
                                    )
                                }
                            }
                        }

                        OutlinedTextField(
                            value = state.linkTugas,
                            onValueChange = viewModel::onLinkTugasChange,
                            label = { Text("Link Tugas") },
                            placeholder = {
                                Text("https://drive.google.com/... atau https://github.com/...")
                            },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                            isError = state.errorMessage != null &&
                                    state.errorMessage!!.contains("link", ignoreCase = true),
                            supportingText = {
                                if (state.errorMessage != null &&
                                    state.errorMessage!!.contains("link", ignoreCase = true)
                                ) {
                                    Text(
                                        text = state.errorMessage ?: "",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.error
                                    )
                                }
                            }
                        )

                        OutlinedTextField(
                            value = state.deskripsi ?: "",
                            onValueChange = viewModel::onDeskripsiChange,
                            label = { Text("Deskripsi") },
                            placeholder = { Text("Jelaskan tentang tugas ini ...") },
                            modifier = Modifier
                                .fillMaxWidth()
                                .heightIn(min = 100.dp),
                            maxLines = 5
                        )
                    }
                }

                Card(
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "Visibility",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = TextDark
                        )

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            RadioButton(
                                selected = state.visibility == "Publik",
                                onClick = { viewModel.onVisibilityToggle(true) }
                            )
                            Column {
                                Text("Publik", fontWeight = FontWeight.Medium)
                                Text(
                                    "Semua mahasiswa bisa melihat dan mengakses link tugas anda",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = Color.Gray
                                )
                            }
                        }

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            RadioButton(
                                selected = state.visibility == "Private",
                                onClick = { viewModel.onVisibilityToggle(false) }
                            )
                            Column {
                                Text("Private", fontWeight = FontWeight.Medium)
                                Text(
                                    "Hanya anda yang dapat melihat tugas anda",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = Color.Gray
                                )
                            }
                        }
                    }
                }

                Button(
                    onClick = { viewModel.submitForm(tugasId) },
                    enabled = !state.isSaving &&
                            state.judul.isNotBlank() &&
                            state.linkTugas.isNotBlank(),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = PrimaryOrange,
                        disabledContainerColor = PrimaryOrange.copy(alpha = 0.4f)
                    )
                ) {
                    Text(if (tugasId == null) "Simpan" else "Simpan Perubahan")
                }
            }

            if (state.successMessage != null) {
                TugasSuccessPopup(
                    message = state.successMessage ?: "Tugas Tersimpan",
                    onFinished = {
                        viewModel.onSuccessMessageShown()
                        onSuccessSubmit()
                    }
                )
            }
        }
    }
}

@Composable
private fun TugasSuccessPopup(
    message: String,
    onFinished: () -> Unit
) {
    LaunchedEffect(Unit) {
        delay(1500)
        onFinished()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.3f)),
        contentAlignment = Alignment.Center
    ) {
        Card(
            shape = RoundedCornerShape(12.dp),
            elevation = CardDefaults.cardElevation(4.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            Column(
                modifier = Modifier.padding(horizontal = 24.dp, vertical = 16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    imageVector = Icons.Filled.Check,
                    contentDescription = null,
                    tint = PrimaryOrange,
                    modifier = Modifier.size(32.dp)
                )
                Text(
                    text = message,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}
