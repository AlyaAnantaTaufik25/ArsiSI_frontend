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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.arsisi_frontend.data.model.ArsipStatistik
import com.example.arsisi_frontend.data.model.KategoriArsip
import com.example.arsisi_frontend.data.model.Prestasi
import com.example.arsisi_frontend.data.model.UiState
import com.example.arsisi_frontend.ui.theme.*


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PrestasiListScreen(
    onNavigateToDetail: (Int) -> Unit,
    onNavigateToForm: () -> Unit,
    onNavigateBack: () -> Unit,
    viewModel: PrestasiViewModel = viewModel()
) {
    val prestasiListState by viewModel.prestasiListState.collectAsState()
    val statistikState by viewModel.statistikState.collectAsState()
    val selectedKategori by viewModel.selectedKategori.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()

    // SAFE FILTER - LOCAL VAR
    val currentPrestasiList = if (prestasiListState is UiState.Success<*>) {
        (prestasiListState as UiState.Success<List<Prestasi>>).data ?: emptyList()
    } else emptyList()

    val filteredList = currentPrestasiList.filter { item ->
        val matchKategori = when (selectedKategori) {
            KategoriArsip.SEMUA -> true
            KategoriArsip.PRESTASI -> item.jenis.equals("PRESTASI", true)
            KategoriArsip.SERTIFIKAT -> item.jenis.equals("SERTIFIKAT", true)
            KategoriArsip.ORGANISASI -> item.jenis.equals("ORGANISASI", true)
        }
        val matchQuery = item.nama.contains(searchQuery, ignoreCase = true)
        matchKategori && matchQuery
    }

    // SAFE STATISTIK - LOCAL VAR
    val statistikData = if (statistikState is UiState.Success<*>) {
        (statistikState as UiState.Success<ArsipStatistik>).data
    } else null

    LaunchedEffect(Unit) {
        viewModel.loadPrestasiList()
        viewModel.loadStatistik()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Arsip Prestasi", color = Color.White, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, "Kembali", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Orange600),
                actions = {
                    IconButton(onClick = onNavigateToForm) {
                        Icon(Icons.Default.Add, "Tambah", tint = Color.White)
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onNavigateToForm,
                containerColor = Orange600,
                contentColor = Color.White
            ) {
                Icon(Icons.Default.Add, "Tambah Prestasi")
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(BackgroundLight)
        ) {
            // SEARCH BAR
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { viewModel.setSearchQuery(it) },
                placeholder = { Text("Cari prestasi...") },
                leadingIcon = { Icon(Icons.Default.Search, null) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                shape = RoundedCornerShape(24.dp),
                singleLine = true
            )

            // KATEGORI BUTTONS - VERTICAL (NO horizontalScroll)
            Column(
                modifier = Modifier.padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                KategoriButton("Semua", Icons.Default.Menu, selectedKategori == KategoriArsip.SEMUA) {
                    viewModel.setKategori(KategoriArsip.SEMUA)
                }
                KategoriButton("Prestasi", Icons.Default.EmojiEvents, selectedKategori == KategoriArsip.PRESTASI) {
                    viewModel.setKategori(KategoriArsip.PRESTASI)
                }
                KategoriButton("Sertifikat", Icons.Default.Badge, selectedKategori == KategoriArsip.SERTIFIKAT) {
                    viewModel.setKategori(KategoriArsip.SERTIFIKAT)
                }
                KategoriButton("Organisasi", Icons.Default.Groups, selectedKategori == KategoriArsip.ORGANISASI) {
                    viewModel.setKategori(KategoriArsip.ORGANISASI)
                }
            }

            Spacer(Modifier.height(16.dp))

            // STATISTIK - SAFE ACCESS
            statistikData?.let { data ->
                Column(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    StatistikCard("Total Arsip", data.totalArsip.toString())
                    StatistikCard("Prestasi", data.totalPrestasi.toString())
                    StatistikCard("Sertifikat", data.totalSertifikat.toString())
                    StatistikCard("Organisasi", data.totalOrganisasi.toString())
                }
            }

            Spacer(Modifier.height(16.dp))

            // MAIN CONTENT
            when (prestasiListState) {
                is UiState.Loading -> LoadingState()
                is UiState.Error -> ErrorState(
                    message = (prestasiListState as UiState.Error).message
                ) { viewModel.loadPrestasiList() }
                is UiState.Success -> {
                    if (filteredList.isEmpty()) {
                        EmptyState()
                    } else {
                        PrestasiListContent(filteredList, onNavigateToDetail)
                    }
                }
                else -> {}
            }
        }
    }
}

@Composable
private fun KategoriButton(
    label: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(vertical = 2.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) Orange600 else SurfaceWhite
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = if (isSelected) 4.dp else 0.dp
        )
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = if (isSelected) Color.White else Orange600,
                modifier = Modifier.size(20.dp)
            )
            Text(
                text = label,
                style = MaterialTheme.typography.bodyMedium,
                color = if (isSelected) Color.White else TextPrimary
            )
        }
    }
}

@Composable
private fun StatistikCard(title: String, value: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Orange600)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = value,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
            Text(
                text = title,
                style = MaterialTheme.typography.labelMedium,
                color = Color.White
            )
        }
    }
}

@Composable
private fun PrestasiListContent(
    prestasiList: List<Prestasi>,
    onPrestasiClick: (Int) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Text(
                text = "Daftar Prestasi (${prestasiList.size})",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.padding(vertical = 8.dp),
                color = TextPrimary
            )
        }
        items(prestasiList, key = { it.id }) { prestasi ->
            PrestasiItem(prestasi) { onPrestasiClick(prestasi.id) }
        }
    }
}

@Composable
private fun PrestasiItem(
    prestasi: Prestasi,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceWhite),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .background(
                        color = Orange600.copy(alpha = 0.1f),
                        shape = RoundedCornerShape(16.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.EmojiEvents,
                    contentDescription = null,
                    tint = Orange600,
                    modifier = Modifier.size(28.dp)
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column {
                Text(
                    text = prestasi.nama,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    maxLines = 2
                )
                Spacer(modifier = Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        color = Orange600.copy(alpha = 0.1f),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            text = prestasi.jenis,
                            style = MaterialTheme.typography.labelSmall,
                            color = Orange600,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "${prestasi.tahun}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextSecondary
                    )
                }
            }

            Icon(
                imageVector = Icons.Default.KeyboardArrowRight,
                contentDescription = null,
                tint = TextSecondary,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

@Composable
private fun LoadingState() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator(color = Orange600)
    }
}

@Composable
private fun EmptyState() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Star,
                contentDescription = null,
                tint = Grey400,
                modifier = Modifier.size(80.dp)
            )
            Text(
                text = "Belum ada prestasi",
                style = MaterialTheme.typography.titleLarge,
                color = TextPrimary
            )
            Text(
                text = "Tambahkan prestasi pertama Anda",
                style = MaterialTheme.typography.bodyMedium,
                color = TextSecondary
            )
        }
    }
}

@Composable
private fun ErrorState(
    message: String,
    onRetry: () -> Unit
) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Error,
                contentDescription = null,
                tint = ErrorRed,
                modifier = Modifier.size(80.dp)
            )
            Text(
                text = "Oops!",
                style = MaterialTheme.typography.headlineSmall,
                color = TextPrimary
            )
            Text(
                text = message,
                style = MaterialTheme.typography.bodyLarge,
                color = TextSecondary
            )
            Button(
                onClick = onRetry,
                colors = ButtonDefaults.buttonColors(containerColor = Orange600)
            ) {
                Text("Coba Lagi")
            }
        }
    }
}
