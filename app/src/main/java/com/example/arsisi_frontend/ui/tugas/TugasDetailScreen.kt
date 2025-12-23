package com.example.arsisi_frontend.ui.tugas

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Link
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.arsisi_frontend.data.model.Tugas
import com.example.arsisi_frontend.ui.theme.PrimaryOrange
import com.example.arsisi_frontend.ui.theme.TextDark
import com.example.arsisi_frontend.ui.theme.LightGray
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.TimeZone

@Composable
fun TugasDetailScreen(
    tugasId: Int,
    onNavigateBack: () -> Unit,
    viewModel: TugasViewModel
) {
    val uiState by viewModel.detailUiState.collectAsStateWithLifecycle()

    when (val state = uiState) {
        is TugasViewModel.DetailUiState.Loading -> Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator(color = PrimaryOrange)
        }

        is TugasViewModel.DetailUiState.Error -> Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(state.message, color = Color.Red)
        }

        is TugasViewModel.DetailUiState.Success -> {
            val tugas: Tugas = state.tugas

            TugasDetailContent(
                judul = tugas.judul ?:  "",  // ✅ FIX INI
                namaMatkul = tugas.namaMatakuliah ?: "-",
                deskripsi = tugas.deskripsi ?: "",
                linkTugas = tugas.linkTugas ?: "",
                tipeTugas = tugas.tipe_tugas ?: "-",
                visibility = tugas.visibility ?: "-",
                waktuUploadText = formatWaktuUpload(tugas.created_at),
                onNavigateBack = onNavigateBack
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TugasDetailContent(
    judul: String,
    namaMatkul: String,
    deskripsi: String,
    linkTugas: String,
    tipeTugas: String,
    visibility: String,
    waktuUploadText: String,
    onNavigateBack: () -> Unit,
) {
    val uriHandler = LocalUriHandler.current
    val trimmedLink = linkTugas.trim()
    val hasLink = trimmedLink.isNotBlank()

    Scaffold(
        topBar = {
            Surface(
                color = PrimaryOrange,
                shadowElevation = 4.dp
            ) {
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
                            text = "Detail Tugas",
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
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
                .padding(horizontal = 16.dp, vertical = 10.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 14.dp, vertical = 12.dp)
                ) {
                    Text(
                        text = judul,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = TextDark
                    )
                    Spacer(Modifier.height(6.dp))
                    Surface(
                        color = PrimaryOrange.copy(alpha = 0.15f),
                        shape = RoundedCornerShape(20.dp)
                    ) {
                        Text(
                            text = namaMatkul,
                            style = MaterialTheme.typography.labelSmall,
                            color = PrimaryOrange,
                            modifier = Modifier
                                .padding(horizontal = 10.dp, vertical = 4.dp)
                        )
                    }
                }
            }

            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 14.dp, vertical = 12.dp)
                ) {
                    Text(
                        text = "Deskripsi",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = TextDark
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(
                        text = if (deskripsi.isBlank()) "-" else deskripsi,
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Gray
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
                        .padding(horizontal = 14.dp, vertical = 12.dp)
                ) {
                    Text(
                        text = "Link Tugas",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = TextDark
                    )
                    Spacer(Modifier.height(6.dp))
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = LightGray
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable(enabled = hasLink) {
                                    uriHandler.openUri(trimmedLink)
                                }
                                .padding(horizontal = 10.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Link,
                                contentDescription = null,
                                tint = if (hasLink) Color.Gray else Color.LightGray,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(Modifier.width(8.dp))
                            Text(
                                text = if (hasLink) trimmedLink else "-",
                                style = MaterialTheme.typography.bodySmall,
                                color = if (hasLink) Color(0xFF1E88E5) else Color.Gray,
                                maxLines = 1
                            )
                        }
                    }
                }
            }

            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 14.dp, vertical = 12.dp)
                ) {
                    Text(
                        text = "Informasi Tambahan",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = TextDark
                    )
                    Spacer(Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Text("Tipe Tugas", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                            Text("Visibility Tugas", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                            Text("Waktu Upload", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                        }
                        Column(
                            horizontalAlignment = Alignment.End,
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Text(tipeTugas, style = MaterialTheme.typography.bodySmall, color = TextDark)
                            Text(visibility, style = MaterialTheme.typography.bodySmall, color = TextDark)
                            Text(waktuUploadText, style = MaterialTheme.typography.bodySmall, color = TextDark)
                        }
                    }
                }
            }
        }
    }
}

private fun formatWaktuUpload(createdAt: String?): String {
    if (createdAt.isNullOrBlank()) return "-"

    return try {
        val parser = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US)
        parser.timeZone = TimeZone.getTimeZone("UTC")
        val date = parser.parse(createdAt) ?: return "-"
        val formatter = SimpleDateFormat("dd MMM yyyy", Locale("id", "ID"))
        formatter.format(date)
    } catch (_: Exception) {
        "-"
    }
}
