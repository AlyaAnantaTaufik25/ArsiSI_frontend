package com.example.arsisi_frontend.ui.prestasi

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.arsisi_frontend.data.model.ArsipStatistik
import com.example.arsisi_frontend.data.model.KategoriArsip
import com.example.arsisi_frontend.data.model.Prestasi
import com.example.arsisi_frontend.data.model.UiState
import com.example.arsisi_frontend.ui.theme.Orange600

@Composable
fun PrestasiScreen(
    viewModel: PrestasiViewModel,
    onAddClick: () -> Unit,
    onArsipClick: (Int) -> Unit
) {
    val prestasiListState by viewModel.prestasiListState.collectAsStateWithLifecycle()
    val statistikState by viewModel.statistikState.collectAsStateWithLifecycle()
    val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()
    val selectedKategori by viewModel.selectedKategori.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.loadPrestasiList()
        viewModel.loadStatistik()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFEFEFEF))
    ) {
        // HEADER ORANYE
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(220.dp)
                .background(Orange600)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 24.dp, vertical = 16.dp)
            ) {
                // Bar atas: back + title
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = { /* TODO: handle back di NavGraph kalau mau */ }) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Kembali",
                            tint = Color.White
                        )
                    }
                    Spacer(Modifier.width(8.dp))
                    Column {
                        Text(
                            text = "ARSIP KEGIATAN",
                            color = Color.White,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Pusat Arsip Seluruh Aktivitas Mahasiswa",
                            color = Color.White.copy(alpha = 0.9f),
                            fontSize = 12.sp
                        )
                    }
                }

                Spacer(Modifier.height(16.dp))

                // Search + tombol +
                Row(verticalAlignment = Alignment.CenterVertically) {
                    SearchBar(
                        query = searchQuery,
                        onQueryChange = { viewModel.setSearchQuery(it) },
                        onClear = { viewModel.clearSearch() },
                        modifier = Modifier.weight(1f)
                    )
                    Spacer(Modifier.width(12.dp))
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .background(Color.White, RoundedCornerShape(12.dp))
                            .clickable { onAddClick() },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Tambah",
                            tint = Orange600
                        )
                    }
                }
            }
        }

        // KONTEN PUTIH DI ATAS BACKGROUND ABU
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 200.dp)
        ) {
            Surface(
                modifier = Modifier.fillMaxSize(),
                color = Color(0xFFEFEFEF)
            ) {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {

                    // Telusuri Kategori
                    item {
                        Text(
                            text = "Telusuri Kategori",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color(0xFF444444),
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                        KategoriRow(
                            selected = selectedKategori,
                            onSelected = { viewModel.setKategori(it) }
                        )
                    }

                    // Statistik kartu besar
                    item {
                        StatistikBigCard(statistikState)
                    }

                    // Header list
                    item {
                        Text(
                            text = "Arsip Terbaru",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF1A1A1A)
                        )
                    }

                    // List prestasi
                    when (prestasiListState) {
                        is UiState.Loading -> {
                            item {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(64.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    CircularProgressIndicator(color = Orange600)
                                }
                            }
                        }

                        is UiState.Success -> {
                            val list = (prestasiListState as UiState.Success<List<Prestasi>>).data
                            if (list.isEmpty()) {
                                item { EmptyState() }
                            } else {
                                items(list) { prestasi ->
                                    ArsipItemCard(
                                        prestasi = prestasi,
                                        onClick = { onArsipClick(prestasi.id ?: 0) }
                                    )
                                }
                            }
                        }

                        is UiState.Error -> {
                            item {
                                ErrorState(
                                    message = (prestasiListState as UiState.Error).message
                                ) { viewModel.loadPrestasiList() }
                            }
                        }

                        else -> {}
                    }
                }
            }
        }
    }
}

/* ===================== COMPONENTS ===================== */

@Composable
private fun SearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    onClear: () -> Unit,
    modifier: Modifier = Modifier
) {
    OutlinedTextField(
        value = query,
        onValueChange = onQueryChange,
        modifier = modifier
            .height(44.dp),
        placeholder = {
            Text("Cari Arsip...", color = Color(0xFF9E9E9E))
        },
        leadingIcon = {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = null,
                tint = Color(0xFF666666)
            )
        },
        trailingIcon = if (query.isNotEmpty()) {
            {
                IconButton(onClick = onClear) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = null,
                        tint = Color(0xFF666666)
                    )
                }
            }
        } else null,
        shape = RoundedCornerShape(24.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = Color.Transparent,
            unfocusedBorderColor = Color.Transparent,
            cursorColor = Orange600,
            focusedContainerColor = Color.White,
            unfocusedContainerColor = Color.White,
            focusedTextColor = Color.Black,
            unfocusedTextColor = Color.Black,
            focusedLeadingIconColor = Color(0xFF666666),
            unfocusedLeadingIconColor = Color(0xFF666666)
        ),
        singleLine = true
    )
}

@Composable
private fun KategoriRow(
    selected: KategoriArsip,
    onSelected: (KategoriArsip) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        KategoriChip(
            label = "Semua",
            icon = Icons.Default.Layers,
            selected = selected == KategoriArsip.SEMUA
        ) { onSelected(KategoriArsip.SEMUA) }

        KategoriChip(
            label = "Prestasi",
            icon = Icons.Default.EmojiEvents,
            selected = selected == KategoriArsip.PRESTASI
        ) { onSelected(KategoriArsip.PRESTASI) }

        KategoriChip(
            label = "Sertifikat",
            icon = Icons.Default.Badge,
            selected = selected == KategoriArsip.SERTIFIKAT
        ) { onSelected(KategoriArsip.SERTIFIKAT) }

        KategoriChip(
            label = "Organisasi",
            icon = Icons.Default.Groups,
            selected = selected == KategoriArsip.ORGANISASI
        ) { onSelected(KategoriArsip.ORGANISASI) }
    }
}

@Composable
private fun KategoriChip(
    label: String,
    icon: ImageVector,
    selected: Boolean,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.clickable { onClick() }
    ) {
        Box(
            modifier = Modifier
                .size(56.dp)
                .background(
                    color = if (selected) Color(0xFFFFF3E0) else Color.White,
                    shape = RoundedCornerShape(18.dp)
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = if (selected) Orange600 else Color(0xFFBDBDBD),
                modifier = Modifier.size(26.dp)
            )
        }
        Spacer(Modifier.height(6.dp))
        Text(
            text = label,
            fontSize = 12.sp,
            color = if (selected) Orange600 else Color(0xFF777777)
        )
    }
}

@Composable
private fun StatistikBigCard(statistikState: UiState<*>) {
    val stats = if (statistikState is UiState.Success<*>) {
        statistikState.data as? ArsipStatistik
    } else null

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF3E0))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            StatistikColumn(
                value = stats?.totalArsip ?: 0,
                label = "Total Arsip",
                highlight = true
            )
            StatistikColumn(
                value = stats?.totalPrestasi ?: 0,
                label = "Prestasi"
            )
            StatistikColumn(
                value = stats?.totalSertifikat ?: 0,
                label = "Sertifikat"
            )
            StatistikColumn(
                value = stats?.totalOrganisasi ?: 0,
                label = "Organisasi"
            )
        }
    }
}

@Composable
private fun StatistikColumn(
    value: Int,
    label: String,
    highlight: Boolean = false
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.widthIn(min = 60.dp)
    ) {
        Text(
            text = value.toString(),
            fontSize = if (highlight) 24.sp else 18.sp,
            fontWeight = FontWeight.Bold,
            color = if (highlight) Orange600 else Color(0xFF555555)
        )
        Text(
            text = label,
            fontSize = 11.sp,
            color = if (highlight) Orange600 else Color(0xFF777777)
        )
    }
}

@Composable
private fun ArsipItemCard(
    prestasi: Prestasi,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp, horizontal = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // strip oranye di kiri
            Box(
                modifier = Modifier
                    .width(4.dp)
                    .height(48.dp)
                    .background(Orange600, RoundedCornerShape(50))
            )

            Spacer(Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = prestasi.nama ?: "",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 2
                )
                Spacer(Modifier.height(4.dp))

                Box(
                    modifier = Modifier
                        .background(
                            color = Orange600.copy(alpha = 0.12f),
                            shape = RoundedCornerShape(8.dp)
                        )
                        .padding(horizontal = 8.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = prestasi.jenis ?: "",
                        fontSize = 11.sp,
                        color = Orange600
                    )
                }

                Spacer(Modifier.height(4.dp))

                Text(
                    text = "${prestasi.tahun ?: ""} • ${prestasi.penyelenggara.orEmpty()}",
                    fontSize = 11.sp,
                    color = Color(0xFF9E9E9E)
                )
            }

            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = null,
                tint = Color(0xFFBDBDBD)
            )
        }
    }
}

@Composable
private fun EmptyState() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(64.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = Icons.Default.StarBorder,
            contentDescription = null,
            tint = Color(0xFFBDBDBD),
            modifier = Modifier.size(64.dp)
        )
        Spacer(Modifier.height(16.dp))
        Text(
            text = "Belum ada arsip",
            fontSize = 18.sp,
            fontWeight = FontWeight.Medium,
            color = Color(0xFF424242)
        )
        Text(
            text = "Tambahkan arsip pertama Anda",
            fontSize = 14.sp,
            color = Color(0xFF757575)
        )
    }
}

@Composable
private fun ErrorState(
    message: String,
    onRetry: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(64.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = Icons.Default.Error,
            contentDescription = null,
            tint = Color.Red,
            modifier = Modifier.size(64.dp)
        )
        Spacer(Modifier.height(16.dp))
        Text(message, fontSize = 16.sp, color = Color(0xFF424242))
        Spacer(Modifier.height(24.dp))
        Button(
            onClick = onRetry,
            colors = ButtonDefaults.buttonColors(containerColor = Orange600)
        ) {
            Text("Coba Lagi", color = Color.White)
        }
    }
}
