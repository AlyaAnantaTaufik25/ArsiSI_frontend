package com.example.arsisi_frontend.ui.prestasi

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.arsisi_frontend.data.model.Prestasi
import com.example.arsisi_frontend.data.model.UiState
import com.example.arsisi_frontend.ui.theme.*
import com.example.arsisi_frontend.utils.Constants
import com.example.arsisi_frontend.utils.DateUtils

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PrestasiDetailScreen(
    prestasiId: Int,
    onNavigateBack: () -> Unit,
    onNavigateToEdit: (Int) -> Unit,
    viewModel: PrestasiViewModel = viewModel()
) {
    val prestasiDetailState by viewModel.prestasiDetailState.collectAsState()
    val operationState by viewModel.operationState.collectAsState()
    val context = LocalContext.current

    var showDeleteDialog by remember { mutableStateOf(false) }
    var showSuccessDialog by remember { mutableStateOf(false) }

    // Load detail saat screen dibuka
    LaunchedEffect(prestasiId) {
        viewModel.loadPrestasiDetail(prestasiId)
    }

    // Handle hasil operasi delete
    LaunchedEffect(operationState) {
        when (operationState) {
            is UiState.Success -> {
                showSuccessDialog = true
                viewModel.resetOperationState()
            }
            is UiState.Error -> {
                // kalau mau, bisa tampilkan snackbar error di sini
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
                        text = "Detail Prestasi",
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
            when (prestasiDetailState) {
                is UiState.Loading -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center),
                        color = Orange600
                    )
                }

                is UiState.Success -> {
                    val prestasi = (prestasiDetailState as UiState.Success<Prestasi>).data
                    PrestasiDetailContent(
                        prestasi = prestasi,
                        onDownload = {
                            // buka file di browser / PDF viewer
                            prestasi.filePath?.let { path ->
                                val url = "${Constants.BASE_URL.replace("/api/", "")}/$path"
                                val intent = Intent(Intent.ACTION_VIEW).apply {
                                    data = Uri.parse(url)
                                }
                                context.startActivity(intent)
                            }
                        },
                        onEdit = { onNavigateToEdit(prestasi.id) },
                        onDelete = { showDeleteDialog = true }
                    )
                }

                is UiState.Error -> {
                    Column(
                        modifier = Modifier.align(Alignment.Center),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Default.Error,
                            contentDescription = "Error",
                            modifier = Modifier.size(64.dp),
                            tint = ErrorRed
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = (prestasiDetailState as UiState.Error).message,
                            style = MaterialTheme.typography.bodyMedium,
                            color = TextSecondary
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(
                            onClick = { viewModel.loadPrestasiDetail(prestasiId) },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Orange600
                            )
                        ) {
                            Text("Coba Lagi")
                        }
                    }
                }

                else -> {}
            }
        }
    }

    // Dialog konfirmasi hapus
    if (showDeleteDialog) {
        DeleteConfirmationDialog(
            onConfirm = {
                viewModel.deletePrestasi(prestasiId)
                showDeleteDialog = false
            },
            onDismiss = { showDeleteDialog = false }
        )
    }

    // Dialog sukses setelah hapus
    if (showSuccessDialog) {
        SuccessDialog(
            message = "Prestasi berhasil dihapus",
            onDismiss = {
                showSuccessDialog = false
                onNavigateBack()
            }
        )
    }
}

@Composable
private fun PrestasiDetailContent(
    prestasi: Prestasi,
    onDownload: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        // Header Card (ikon + judul + info file)
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = SurfaceWhite),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Icon kategori (jenis)
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .clip(CircleShape)
                        .background(
                            when (prestasi.jenis.uppercase()) {
                                "PRESTASI" -> PrestasiColor.copy(alpha = 0.2f)
                                "SERTIFIKAT" -> SertifikatColor.copy(alpha = 0.2f)
                                "ORGANISASI" -> OrganisasiColor.copy(alpha = 0.2f)
                                else -> Grey200
                            }
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = when (prestasi.jenis.uppercase()) {
                            "PRESTASI" -> Icons.Default.EmojiEvents
                            "SERTIFIKAT" -> Icons.Default.Description
                            "ORGANISASI" -> Icons.Default.Groups
                            else -> Icons.Default.Description
                        },
                        contentDescription = prestasi.jenis,
                        modifier = Modifier.size(40.dp),
                        tint = when (prestasi.jenis.uppercase()) {
                            "PRESTASI" -> PrestasiColor
                            "SERTIFIKAT" -> SertifikatColor
                            "ORGANISASI" -> OrganisasiColor
                            else -> Grey500
                        }
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Judul
                Text(
                    text = prestasi.nama,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Info file sederhana (kalau backend belum pakai size/type, bisa hardcode sementara)
                Text(
                    text = "Dokumen Arsip",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondary
                )
            }
        }

        // Card detail informasi
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = SurfaceWhite)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
            ) {
                // Jenis / kategori
                DetailRow(
                    label = "Kategori",
                    value = prestasi.jenis,
                    valueColor = when (prestasi.jenis.uppercase()) {
                        "PRESTASI" -> PrestasiColor
                        "SERTIFIKAT" -> SertifikatColor
                        "ORGANISASI" -> OrganisasiColor
                        else -> TextPrimary
                    },
                    showBadge = true
                )

                Divider(
                    modifier = Modifier.padding(vertical = 16.dp),
                    color = Grey200
                )

                // Tanggal / Tahun
                DetailRow(
                    label = "Tahun",
                    value = prestasi.tahun.toString()
                )

                Divider(
                    modifier = Modifier.padding(vertical = 16.dp),
                    color = Grey200
                )

                // Deskripsi
                Column {
                    Text(
                        text = "Deskripsi",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.SemiBold,
                        color = TextPrimary
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = prestasi.deskripsi,
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextSecondary
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Tombol aksi: Unduh, Edit, Hapus
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Button(
                onClick = onDownload,
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.buttonColors(containerColor = Teal500),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Download,
                    contentDescription = "Download",
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("Unduh")
            }

            OutlinedButton(
                onClick = onEdit,
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = Orange600
                )
            ) {
                Icon(
                    imageVector = Icons.Default.Edit,
                    contentDescription = "Edit",
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("Edit")
            }

            OutlinedButton(
                onClick = onDelete,
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = ErrorRed
                )
            ) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Delete",
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("Hapus")
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}

@Composable
private fun DetailRow(
    label: String,
    value: String,
    valueColor: androidx.compose.ui.graphics.Color = TextPrimary,
    showBadge: Boolean = false
) {
    Column {
        Text(
            text = label,
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.SemiBold,
            color = TextPrimary
        )
        Spacer(modifier = Modifier.height(8.dp))
        if (showBadge) {
            Surface(
                color = valueColor,
                shape = RoundedCornerShape(6.dp)
            ) {
                Text(
                    text = value,
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextWhite,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                )
            }
        } else {
            Text(
                text = value,
                style = MaterialTheme.typography.bodyMedium,
                color = valueColor,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@Composable
fun DeleteConfirmationDialog(
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        icon = {
            Icon(
                imageVector = Icons.Default.Delete,
                contentDescription = "Delete",
                tint = ErrorRed,
                modifier = Modifier.size(48.dp)
            )
        },
        title = {
            Text(
                text = "Apakah Anda yakin ingin menghapus prestasi ini?",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Text(
                text = "Data yang dihapus tidak dapat dikembalikan.",
                style = MaterialTheme.typography.bodyMedium,
                color = TextSecondary
            )
        },
        confirmButton = {
            Button(
                onClick = onConfirm,
                colors = ButtonDefaults.buttonColors(containerColor = ErrorRed)
            ) {
                Text("YAKIN")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("BATAL", color = TextSecondary)
            }
        }
    )
}

@Composable
fun SuccessDialog(
    message: String,
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = SurfaceWhite)
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = "Success",
                    tint = SuccessGreen,
                    modifier = Modifier.size(64.dp)
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Berhasil",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = message,
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextSecondary
                )
                Spacer(modifier = Modifier.height(24.dp))
                Button(
                    onClick = onDismiss,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = Orange600)
                ) {
                    Text("OK")
                }
            }
        }
    }
}
