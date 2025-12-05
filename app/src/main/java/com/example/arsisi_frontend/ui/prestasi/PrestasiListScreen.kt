package com.example.arsisi_frontend.ui.prestasi


import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.arsisi_frontend.data.model.*
import com.example.arsisi_frontend.ui.theme.*
import com.example.arsisi_frontend.utils.DateUtils

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PrestasiListScreen(
    onNavigateToDetail: (Int) -> Unit,
    onNavigateToForm: () -> Unit,
    onNavigateBack: () -> Unit,
    viewModel: PrestasiViewModel = viewModel()
) {
    val arsipListState by viewModel.prestasiListState.collectAsState()
    val statistikState by viewModel.statistikState.collectAsState()
    val selectedKategori by viewModel.selectedKategori.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()

    var showSearchBar by remember { mutableStateOf(false) }
    var searchText by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            if (showSearchBar) {
                SearchTopBar(
                    query = searchText,
                    onQueryChange = {
                        searchText = it
                        viewModel.searchPrestasi(it)
                    },
                    onClose = {
                        showSearchBar = false
                        searchText = ""
                        viewModel.searchPrestasi("")
                    }
                )
            } else {
                TopAppBar(
                    title = {
                        Column {
                            Text(
                                text = "ARSIP KEGIATAN",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                color = TextWhite
                            )
                            Text(
                                text = "Pusat Arsip Seluruh Aktivitas Mahasiswa",
                                style = MaterialTheme.typography.bodySmall,
                                color = TextWhite.copy(alpha = 0.9f)
                            )
                        }
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
                    actions = {
                        IconButton(onClick = { showSearchBar = true }) {
                            Icon(
                                imageVector = Icons.Default.Search,
                                contentDescription = "Search",
                                tint = TextWhite
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = Orange600
                    )
                )
            }
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onNavigateToForm,
                containerColor = Orange600,
                contentColor = TextWhite
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Add Arsip"
                )
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(BackgroundLight)
        ) {
            // Statistik Card
            StatistikCard(
                statistikState = statistikState,
                modifier = Modifier.padding(16.dp)
            )

            // Category Filter
            CategoryFilter(
                selectedKategori = selectedKategori,
                onKategoriSelected = { viewModel.filterByKategori(it) },
                modifier = Modifier.padding(horizontal = 16.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Arsip List
            when (arsipListState) {
                is UiState.Loading -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = Orange600)
                    }
                }
                is UiState.Success -> {
                    val arsipList = (arsipListState as UiState.Success<List<Arsip>>).data
                    if (arsipList.isEmpty()) {
                        EmptyStateView()
                    } else {
                        ArsipList(
                            arsipList = arsipList,
                            onArsipClick = onNavigateToDetail
                        )
                    }
                }
                is UiState.Error -> {
                    ErrorStateView(
                        message = (arsipListState as UiState.Error).message,
                        onRetry = { viewModel.loadPrestasiList() }
                    )
                }
                else -> {}
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchTopBar(
    query: String,
    onQueryChange: (String) -> Unit,
    onClose: () -> Unit
) {
    TopAppBar(
        title = {
            TextField(
                value = query,
                onValueChange = onQueryChange,
                placeholder = { Text("Cari Arsip...", color = TextSecondary) },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    focusedTextColor = TextWhite,
                    unfocusedTextColor = TextWhite
                )
            )
        },
        navigationIcon = {
            IconButton(onClick = onClose) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Close Search",
                    tint = TextWhite
                )
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = Orange600
        )
    )
}

@Composable
fun StatistikCard(
    statistikState: UiState<ArsipStatistik>,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Orange600
        )
    ) {
        when (statistikState) {
            is UiState.Success -> {
                val stats = statistikState.data
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    StatistikItem(
                        value = stats.totalArsip.toString(),
                        label = "Total Arsip"
                    )
                    StatistikItem(
                        value = stats.totalPrestasi.toString(),
                        label = "Prestasi"
                    )
                    StatistikItem(
                        value = stats.totalSertifikat.toString(),
                        label = "Sertifikat"
                    )
                    StatistikItem(
                        value = stats.totalOrganisasi.toString(),
                        label = "Organisasi"
                    )
                }
            }
            else -> {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    repeat(4) {
                        StatistikItem(value = "0", label = "Loading...")
                    }
                }
            }
        }
    }
}

@Composable
fun StatistikItem(
    value: String,
    label: String
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = value,
            style = MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.Bold,
            color = TextWhite
        )
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = TextWhite.copy(alpha = 0.9f)
        )
    }
}

@Composable
fun CategoryFilter(
    selectedKategori: KategoriArsip,
    onKategoriSelected: (KategoriArsip) -> Unit,
    modifier: Modifier = Modifier
) {
    Text(
        text = "Telusuri Kategori",
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.SemiBold,
        modifier = modifier
    )

    Spacer(modifier = Modifier.height(12.dp))

    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        KategoriArsip.values().forEach { kategori ->
            CategoryButton(
                kategori = kategori,
                isSelected = selectedKategori == kategori,
                onClick = { onKategoriSelected(kategori) }
            )
        }
    }
}

@Composable
fun CategoryButton(
    kategori: KategoriArsip,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.clickable(onClick = onClick)
    ) {
        Box(
            modifier = Modifier
                .size(64.dp)
                .clip(CircleShape)
                .background(
                    if (isSelected) Orange600 else Grey200
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = when (kategori) {
                    KategoriArsip.SEMUA -> Icons.Default.List
                    KategoriArsip.PRESTASI -> Icons.Default.Star
                    KategoriArsip.SERTIFIKAT -> Icons.Default.Description
                    KategoriArsip.ORGANISASI -> Icons.Default.Groups
                },
                contentDescription = kategori.displayName,
                tint = if (isSelected) TextWhite else TextSecondary,
                modifier = Modifier.size(32.dp)
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = kategori.displayName,
            style = MaterialTheme.typography.bodySmall,
            color = if (isSelected) Orange600 else TextSecondary
        )
    }
}

@Composable
fun ArsipList(
    arsipList: List<Arsip>,
    onArsipClick: (Int) -> Unit
) {
    Text(
        text = "Arsip Terbaru",
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.SemiBold,
        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
    )

    LazyColumn(
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(arsipList, key = { it.arsipId }) { arsip ->
            ArsipItem(arsip = arsip, onClick = { onArsipClick(arsip.arsipId) })
        }
    }
}

@Composable
fun ArsipItem(
    arsip: Arsip,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = SurfaceWhite
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Icon berdasarkan kategori
            Box(
                modifier = Modifier
                    .size(48.dp)
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
                    tint = when (arsip.kategori) {
                        "PRESTASI" -> PrestasiColor
                        "SERTIFIKAT" -> SertifikatColor
                        "ORGANISASI" -> OrganisasiColor
                        else -> Grey500
                    }
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            // Content
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = arsip.judul,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold,
                    color = TextPrimary
                )

                Spacer(modifier = Modifier.height(4.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        color = when (arsip.kategori) {
                            "PRESTASI" -> PrestasiColor
                            "SERTIFIKAT" -> SertifikatColor
                            "ORGANISASI" -> OrganisasiColor
                            else -> Grey400
                        },
                        shape = RoundedCornerShape(4.dp)
                    ) {
                        Text(
                            text = arsip.kategori,
                            style = MaterialTheme.typography.labelSmall,
                            color = TextWhite,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    Icon(
                        imageVector = Icons.Default.DateRange,
                        contentDescription = "Date",
                        modifier = Modifier.size(14.dp),
                        tint = TextSecondary
                    )

                    Spacer(modifier = Modifier.width(4.dp))

                    Text(
                        text = DateUtils.formatDateForDisplay(arsip.tanggal),
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondary
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    Icon(
                        imageVector = Icons.Default.AttachFile,
                        contentDescription = "File",
                        modifier = Modifier.size(14.dp),
                        tint = TextSecondary
                    )

                    Spacer(modifier = Modifier.width(4.dp))

                    Text(
                        text = "2.4 MB",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondary
                    )
                }
            }

            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = "Go to detail",
                tint = TextSecondary
            )
        }
    }
}

@Composable
fun EmptyStateView() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(
                imageVector = Icons.Default.Folder,
                contentDescription = "Empty",
                modifier = Modifier.size(80.dp),
                tint = Grey400
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Belum ada arsip",
                style = MaterialTheme.typography.titleMedium,
                color = TextSecondary
            )
            Text(
                text = "Tambahkan arsip pertama Anda",
                style = MaterialTheme.typography.bodySmall,
                color = TextSecondary
            )
        }
    }
}

@Composable
fun ErrorStateView(
    message: String,
    onRetry: () -> Unit
) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(
                imageVector = Icons.Default.Error,
                contentDescription = "Error",
                modifier = Modifier.size(80.dp),
                tint = ErrorRed
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Terjadi Kesalahan",
                style = MaterialTheme.typography.titleMedium,
                color = TextPrimary
            )
            Text(
                text = message,
                style = MaterialTheme.typography.bodySmall,
                color = TextSecondary
            )
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = onRetry,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Orange600
                )
            ) {
                Text("Coba Lagi")
            }
        }
    }
}