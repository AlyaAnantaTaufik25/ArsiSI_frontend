package com.example.arsisi_frontend.ui.tugas

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.arsisi_frontend.data.model.Tugas
import com.example.arsisi_frontend.ui.theme.PrimaryOrange
import com.example.arsisi_frontend.ui.theme.TextDark
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.TimeZone

@Composable
fun PublikTugasMatkulScreen(
    matkulId: Int,
    onNavigateBack: () -> Unit,
    onViewDetails: (Int) -> Unit,
    viewModel: TugasViewModel
) {
    // pastikan pakai mode publik setiap kali screen ini dibuka
    LaunchedEffect(matkulId) {
        viewModel.switchType(TugasType.PUBLIK)
    }

    val uiState = viewModel.uiState.collectAsStateWithLifecycle().value

    Scaffold(
        containerColor = Color(0xFFF2F2F2)
    ) { paddingValues ->
        when (val state = uiState) {
            TugasUiState.Loading -> Box(
                modifier = Modifier
                    .padding(paddingValues)
                    .fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = PrimaryOrange)
            }

            is TugasUiState.Error -> Box(
                modifier = Modifier
                    .padding(paddingValues)
                    .fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(state.message, color = Color.Red)
            }

            is TugasUiState.Success -> {
                val listForMatkul = state.tugasList.filter { it.matakuliah_id == matkulId }

                val sortState = remember { mutableStateOf("Terbaru") }
                val sortOption = sortState.value

                val sortedList = when (sortOption) {
                    "Terbaru" -> listForMatkul.sortedByDescending { createdAtToMillisSimple(it.createdAt) }
                    "Terlama" -> listForMatkul.sortedBy { createdAtToMillisSimple(it.createdAt) }
                    else -> listForMatkul
                }

                val namaMatkul = sortedList.firstOrNull()?.namaMatakuliah ?: "Mata Kuliah"
                val jumlahTugas = sortedList.size

                Column(
                    modifier = Modifier
                        .padding(paddingValues)
                        .fillMaxSize()
                ) {

                    // HEADER ORANYE
                    Surface(
                        color = PrimaryOrange,
                        modifier = Modifier.fillMaxWidth(),
                        shadowElevation = 2.dp
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            IconButton(onClick = onNavigateBack) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                    contentDescription = "Kembali",
                                    tint = Color.White
                                )
                            }

                            Spacer(Modifier.width(8.dp))

                            Column {
                                Text(
                                    text = namaMatkul,
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                                Spacer(Modifier.height(2.dp))
                                Text(
                                    text = "$jumlahTugas Tugas Tersedia",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = Color.White.copy(alpha = 0.9f)
                                )
                            }
                        }
                    }

                    if (sortedList.isEmpty()) {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                "Belum ada tugas publik di mata kuliah ini.",
                                color = Color.Gray
                            )
                        }
                    } else {
                        // BAR URUTKAN
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Daftar Tugas",
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.SemiBold,
                                color = TextDark
                            )

                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "Urutkan:",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = Color.Gray
                                )
                                Spacer(Modifier.width(8.dp))

                                FilterChip(
                                    selected = sortOption == "Terbaru",
                                    onClick = { sortState.value = "Terbaru" },
                                    label = { Text("Terbaru") },
                                    shape = RoundedCornerShape(50),
                                    colors = FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = PrimaryOrange,
                                        selectedLabelColor = Color.White
                                    )
                                )
                                Spacer(Modifier.width(6.dp))
                                FilterChip(
                                    selected = sortOption == "Terlama",
                                    onClick = { sortState.value = "Terlama" },
                                    label = { Text("Terlama") },
                                    shape = RoundedCornerShape(50),
                                    colors = FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = PrimaryOrange,
                                        selectedLabelColor = Color.White
                                    )
                                )
                            }
                        }

                        LazyColumn(
                            contentPadding = PaddingValues(
                                horizontal = 16.dp,
                                vertical = 4.dp
                            ),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            items(sortedList) { tugas ->
                                PublikTugasItemCard(
                                    tugas = tugas,
                                    onClick = { onViewDetails(tugas.tugasId) }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun PublikTugasItemCard(
    tugas: Tugas,
    onClick: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
    ) {
        Row(
            modifier = Modifier
                .padding(horizontal = 12.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // avatar inisial mahasiswa
            val inisial = tugas.namaMahasiswa
                ?.trim()
                ?.split(" ")
                ?.take(2)
                ?.joinToString("") { it.first().uppercase() }
                ?: "AA"

            Surface(
                shape = RoundedCornerShape(99.dp),
                color = PrimaryOrange,
                modifier = Modifier.size(36.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(
                        text = inisial,
                        style = MaterialTheme.typography.labelMedium,
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(Modifier.width(12.dp))

            Column(Modifier.weight(1f)) {
                Text(
                    text = tugas.judul,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.SemiBold,
                    color = TextDark
                )
                Spacer(Modifier.height(2.dp))
                Text(
                    text = tugas.namaMahasiswa ?: "-",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray
                )
            }

            Spacer(Modifier.width(8.dp))

            // ikon waktu + tanggal upload
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Filled.Schedule,
                    contentDescription = null,
                    tint = Color.Gray,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(Modifier.width(4.dp))
                Text(
                    text = formatTanggal(tugas.createdAt),
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray
                )
            }
        }
    }
}

private fun createdAtToMillisSimple(createdAt: String?): Long {
    if (createdAt.isNullOrBlank()) return 0L
    return try {
        val parser = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US)
        parser.timeZone = TimeZone.getTimeZone("UTC")
        parser.parse(createdAt)?.time ?: 0L
    } catch (_: Exception) {
        0L
    }
}
