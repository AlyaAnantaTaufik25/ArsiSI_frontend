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
import com.example.arsisi_frontend.data.model.Arsip
import com.example.arsisi_frontend.data.model.UiState
import com.example.arsisi_frontend.ui.theme.*
import com.example.arsisi_frontend.utils.Constants
import com.example.arsisi_frontend.utils.DateUtils

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PrestasiDetailScreen(
    arsipId: Int,
    onNavigateBack: () -> Unit,
    onNavigateToEdit: (Int) -> Unit,
    viewModel: PrestasiViewModel = viewModel()
) {
    val arsipDetailState by viewModel.prestasiDetailState.collectAsState()
    val operationState by viewModel.operationState.collectAsState()
    val context = LocalContext.current

    var showDeleteDialog by remember { mutableStateOf(false) }
    var showSuccessDialog by remember { mutableStateOf(false) }

    // Load detail saat screen dibuka
    LaunchedEffect(arsipId) {
        viewModel.loadPrestasiDetail(arsipId)
    }

    // Handle operation result
    LaunchedEffect(operationState) {
        when (operationState) {
            is UiState.Success -> {
                showSuccessDialog = true
                viewModel.resetOperationState()
            }
            is UiState.Error -> {
                // Show error snackbar or dialog
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
                        text = "Detail Arsip",
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
            when (arsipDetailState) {
                is UiState.Loading -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center),
                        color = Orange600
                    )
                }

                is UiState.Success -> {
                    val arsip = (arsipDetailState as UiState.Success<Arsip>).data
                    DetailContent(
                        arsip = arsip,
                        onDownload = {
                            // Download file
                            val intent = Intent(Intent.ACTION_VIEW).apply {
                                data = Uri.parse("${Constants.BASE_URL.replace("/api/", "")}/${arsip.filePath}")
                            }
                            context.startActivity(intent)
                        },
                        onEdit = { onNavigateToEdit(arsip.arsipId) },
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
                            text = (arsipDetailState as UiState.Error).message,
                            style = MaterialTheme.typography.bodyMedium,
                            color = TextSecondary
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(
                            onClick = { viewModel.loadPrestasiDetail(arsipId) },
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

    // Delete Confirmation Dialog
    if (showDeleteDialog) {
        DeleteConfirmationDialog(
            onConfirm = {
                viewModel.deletePrestasi(arsipId)
                showDeleteDialog = false
            },
            onDismiss = { showDeleteDialog = false }
        )
    }

    // Success Dialog
    if (showSuccessDialog) {
        SuccessDialog(
            message = "Arsip berhasil dihapus",
            onDismiss = {
                showSuccessDialog = false
                onNavigateBack()
            }
        )
    }
}

@Composable
fun DetailContent(
    arsip: Arsip,
    onDownload: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        // Header Card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = SurfaceWhite
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Icon Category
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .clip(CircleShape)
                        .background(
                            when (arsip.kategori) {
                                "PRESTASI" -> PrestasiColor.copy(alpha = 0.2f)
                                "SERTIFIKAT" -> SertifikatColor.copy(alpha = 0.2f)
                                "ORGANISASI" -> OrganisasiColor.copy(alpha = 0.2f)
                                else -> Grey200
                            }
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = when (arsip.kategori) {
                            "PRESTASI" -> Icons.Default.Star
                            "SERTIFIKAT" -> Icons.Default.Description
                            "ORGANISASI" -> Icons.Default.Groups
                            else -> Icons.Default.Description
                        },
                        contentDescription = arsip.kategori,
                        modifier = Modifier.size(40.dp),
                        tint = when (arsip.kategori) {
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
                    text = arsip.judul,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )

                Spacer(modifier = Modifier.height(8.dp))

                // File Info
                Text(
                    text = "PDF • 2.4 MB",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondary
                )
            }
        }

        // Detail Information
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
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
                // Kategori
                DetailRow(
                    label = "Kategori",
                    value = arsip.kategori,
                    valueColor = when (arsip.kategori) {
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

                // Tanggal Arsip
                DetailRow(
                    label = "Tanggal Arsip",
                    value = DateUtils.formatDateForDisplay(arsip.tanggal)
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
                        text = arsip.deskripsi,
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextSecondary
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Action Buttons
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Download Button
            Button(
                onClick = onDownload,
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Teal500
                ),
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

            // Edit Button
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

            // Delete Button
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
fun DetailRow(
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
                text = "Apakah Anda yakin ingin menghapus arsip ini?",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Text(
                text = "Arsip yang dihapus tidak dapat dikembalikan",
                style = MaterialTheme.typography.bodyMedium,
                color = TextSecondary
            )
        },
        confirmButton = {
            Button(
                onClick = onConfirm,
                colors = ButtonDefaults.buttonColors(
                    containerColor = ErrorRed
                )
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
            colors = CardDefaults.cardColors(
                containerColor = SurfaceWhite
            )
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
                    text = "Arsip Berhasil disimpan",
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
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Orange600
                    )
                ) {
                    Text("OK")
                }
            }
        }
    }
}