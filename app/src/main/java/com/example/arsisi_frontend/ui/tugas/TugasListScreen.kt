package com.example.arsisi_frontend.ui.tugas

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.arsisi_frontend.data.model.Tugas
import com.example.arsisi_frontend.ui.theme.PrimaryOrange
import com.example.arsisi_frontend.ui.theme.TextDark
import kotlinx.coroutines.delay
import java.text.SimpleDateFormat
import java.util.*

val LightGrayBackground = Color(0xFFF5F5F5)
val CardCorner = 12.dp

@Composable
fun TugasListScreen(
    viewModel: TugasViewModel,
    onNavigateToForm: () -> Unit,
    onViewDetails: (Int) -> Unit,
    onEditTask: (Int) -> Unit,
    onOpenEksplorasiMatkul: (Int) -> Unit
) {
    val listUiState by viewModel.listUiState.collectAsStateWithLifecycle()
    val selectedType by viewModel.selectedType.collectAsStateWithLifecycle()

    var searchText by remember { mutableStateOf("") }
    var showFilter by remember { mutableStateOf(false) }
    var selectedTipe by remember { mutableStateOf("Semua") }
    var selectedUrut by remember { mutableStateOf("Terbaru") }
    var deleteSuccessMessage by remember { mutableStateOf<String?>(null) }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = LightGrayBackground
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            Column(modifier = Modifier.fillMaxSize()) {
                TugasTopBar(
                    selectedType = selectedType,
                    onTypeSelected = viewModel::switchType
                )

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                ) {
                    SearchAndFilterBar(
                        value = searchText,
                        onValueChange = { searchText = it },
                        showFilterButton = selectedType == TugasViewModel.TugasType.SAYA,
                        onFilterClick = { showFilter = true }
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                when (val state = listUiState) {
                    is TugasViewModel.ListUiState.Loading -> LoadingState()
                    is TugasViewModel.ListUiState.Error -> ErrorState(state.message)
                    is TugasViewModel.ListUiState.Success -> {
                        if (selectedType == TugasViewModel.TugasType.SAYA) {
                            val filtered = state.tugasList.filter { tugas ->
                                searchText.isBlank() ||
                                        (tugas.judul ?:  "").contains(searchText, true) ||
                                        (tugas.namaMatakuliah?.contains(searchText, true) == true)
                            }

                            val byType = when (selectedTipe) {
                                "Individu" -> filtered.filter { it.tipe_tugas?.equals("Individu", ignoreCase = true) == true }
                                "Kelompok" -> filtered.filter { it.tipe_tugas?.equals("Kelompok", ignoreCase = true) == true }
                                else -> filtered
                            }

                            val sorted = when (selectedUrut) {
                                "Terbaru" -> byType.sortedByDescending { created_atToMillis(it.created_at) }
                                "Terlama" -> byType.sortedBy { created_atToMillis(it.created_at) }
                                else -> byType
                            }

                            TugasContent(
                                tugasList = sorted,
                                isPublic = false,
                                onViewDetails = onViewDetails,
                                onEditTask = onEditTask,
                                onDeleteTask = { id ->
                                    viewModel.deleteTugas(id)
                                    deleteSuccessMessage = "Tugas berhasil dihapus"
                                }
                            )
                        } else {
                            val filtered = state.tugasList.filter { tugas ->
                                searchText.isBlank() ||
                                        (tugas.namaMatakuliah?.contains(searchText, true) == true) ||
                                        (tugas.kodeMatakuliah?.contains(searchText, true) == true)
                            }

                            val eksplorasiList = viewModel.getEksplorasiPerMatkul(filtered)
                            EksplorasiMatkulContent(
                                list = eksplorasiList,
                                onItemClick = onOpenEksplorasiMatkul
                            )
                        }
                    }
                }
            }

            if (selectedType == TugasViewModel.TugasType.SAYA) {
                FloatingActionButton(
                    onClick = onNavigateToForm,
                    containerColor = PrimaryOrange,
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(end = 16.dp, bottom = 16.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Tambah tugas",
                        tint = Color.White
                    )
                }
            }

            if (showFilter && selectedType == TugasViewModel.TugasType.SAYA) {
                FilterBottomSheet(
                    currentTipe = selectedTipe,
                    currentUrut = selectedUrut,
                    onApply = { tipe, urut ->
                        selectedTipe = tipe
                        selectedUrut = urut
                    },
                    onDismiss = { showFilter = false }
                )
            }

            if (deleteSuccessMessage != null) {
                DeleteSuccessPopup(
                    message = deleteSuccessMessage!!,
                    onFinished = { deleteSuccessMessage = null }
                )
            }
        }
    }
}

@Composable
fun TugasTopBar(
    selectedType: TugasViewModel.TugasType,
    onTypeSelected: (TugasViewModel.TugasType) -> Unit
) {
    Surface(
        color = PrimaryOrange,
        shadowElevation = 4.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 18.dp)
        ) {
            Text(
                text = "Repository Project",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Daftar project dan tugas",
                style = MaterialTheme.typography.bodySmall,
                color = Color.White.copy(alpha = 0.85f)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                SegmentedButton(
                    isSelected = selectedType == TugasViewModel.TugasType.SAYA,
                    label = "Tugas Saya",
                    onClick = { onTypeSelected(TugasViewModel.TugasType.SAYA) },
                    modifier = Modifier.weight(1f)
                )
                SegmentedButton(
                    isSelected = selectedType == TugasViewModel.TugasType.PUBLIK,
                    label = "Eksplorasi",
                    onClick = { onTypeSelected(TugasViewModel.TugasType.PUBLIK) },
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
fun TugasContent(
    tugasList: List<Tugas>,
    isPublic: Boolean,
    onViewDetails: (Int) -> Unit,
    onEditTask: (Int) -> Unit,
    onDeleteTask: (Int) -> Unit
) {
    if (tugasList.isEmpty()) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("Tidak ada tugas di kategori ini.", color = Color.Gray)
        }
        return
    }

    LazyColumn(
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(tugasList, key = { it.tugasId ?: 0 }) { tugas ->
            TugasCard(
                tugas = tugas,
                isPublic = isPublic,
                onClick = { onViewDetails(tugas.tugasId ?: 0) },
                onEditClick = { onEditTask(tugas.tugasId ?: 0) },
                onDeleteClick = onDeleteTask
            )
        }
    }
}

@Composable
fun EksplorasiMatkulContent(
    list: List<TugasViewModel.MataKuliahEksplorasi>,
    onItemClick: (Int) -> Unit
) {
    if (list.isEmpty()) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("Belum ada tugas publik.", color = Color.Gray)
        }
        return
    }

    LazyColumn(
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(list) { item ->
            EksplorasiMatkulCard(
                item = item,
                onClick = { onItemClick(item.matakuliahId ?: 0) }
            )
        }
    }
}

@Composable
fun EksplorasiMatkulCard(
    item: TugasViewModel.MataKuliahEksplorasi,
    onClick: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(CardCorner),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(Modifier.weight(1f)) {
                Text(
                    text = item.namaMatakuliah ?: "Nama Mata Kuliah",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = TextDark
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = item.kodeMatakuliah ?: "",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray
                )
            }

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = item.jumlahTugas.toString(),
                    style = MaterialTheme.typography.titleMedium,
                    color = Color(0xFFE53935),
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Tugas",
                    style = MaterialTheme.typography.labelSmall,
                    color = Color.Gray
                )
            }
        }
    }
}

@Composable
fun TugasCard(
    tugas: Tugas,
    isPublic: Boolean,
    onClick: () -> Unit,
    onEditClick: (Int) -> Unit,
    onDeleteClick: (Int) -> Unit
) {
    var showActionDialog by remember { mutableStateOf(false) }
    var showConfirmDelete by remember { mutableStateOf(false) }

    Card(
        shape = RoundedCornerShape(CardCorner),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.Top
        ) {
            Column(Modifier.weight(1f)) {
                Text(
                    text = tugas.judul ?: "",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = TextDark
                )
                Spacer(modifier = Modifier.height(6.dp))

                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = PrimaryOrange.copy(alpha = 0.12f)
                    ),
                    shape = RoundedCornerShape(6.dp)
                ) {
                    Text(
                        text = tugas.namaMatakuliah ?: "Mata Kuliah",
                        style = MaterialTheme.typography.labelMedium,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        color = TextDark
                    )
                }

                tugas.tipe_tugas?.let { tipe ->
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = tipe,
                        style = MaterialTheme.typography.labelSmall,
                        color = PrimaryOrange,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = if (tugas.visibility == "Publik")
                            Icons.Default.Public else Icons.Default.Lock,
                        contentDescription = null,
                        tint = Color.Gray,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(Modifier.width(6.dp))
                    Text(tugas.visibility ?: "Private", color = Color.Gray, style = MaterialTheme.typography.bodySmall)

                    Spacer(Modifier.width(16.dp))

                    Icon(
                        Icons.Default.Schedule,
                        contentDescription = null,
                        tint = Color.Gray,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(Modifier.width(6.dp))
                    Text(formatTanggal(tugas.created_at), color = Color.Gray, style = MaterialTheme.typography.bodySmall)
                }
            }

            if (!isPublic) {
                IconButton(onClick = { showActionDialog = true }) {
                    Icon(
                        Icons.Default.MoreVert,
                        contentDescription = "Menu",
                        tint = Color.Gray
                    )
                }
            } else {
                IconButton(onClick = onClick) {
                    Icon(
                        Icons.Default.ArrowForward,
                        contentDescription = null,
                        tint = Color.Gray
                    )
                }
            }
        }
    }

    if (showActionDialog && !isPublic) {
        Dialog(onDismissRequest = { showActionDialog = false }) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Card(
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(4.dp),
                    modifier = Modifier.widthIn(min = 220.dp, max = 260.dp)
                ) {
                    Box {
                        Column(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Button(
                                onClick = {
                                    showActionDialog = false
                                    onEditClick(tugas.tugasId ?: 0)
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(40.dp),
                                shape = RoundedCornerShape(8.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = PrimaryOrange,
                                    contentColor = Color.White
                                )
                            ) {
                                Text("Edit Tugas")
                            }

                            OutlinedButton(
                                onClick = {
                                    showActionDialog = false
                                    showConfirmDelete = true
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(40.dp),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text("Hapus Tugas", color = Color.Black)
                            }
                        }

                        IconButton(
                            onClick = { showActionDialog = false },
                            modifier = Modifier
                                .align(Alignment.TopEnd)
                                .padding(2.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Tutup"
                            )
                        }
                    }
                }
            }
        }
    }

    if (showConfirmDelete && !isPublic) {
        DeleteConfirmDialog(
            title = "Hapus Tugas?",
            message = "Anda yakin ingin menghapus tugas \"${tugas.judul ?:  "tugas ini"}\"?",
            onCancel = { showConfirmDelete = false },
            onConfirm = {
                showConfirmDelete = false
                onDeleteClick(tugas.tugasId ?: 0)
            }
        )
    }
}

@Composable
fun PublikTugasMatkulScreen(
    matkulId: Int,
    onNavigateBack: () -> Unit,
    onViewDetails: (Int) -> Unit,
    viewModel: TugasViewModel
) {
    LaunchedEffect(matkulId) {
        viewModel.switchType(TugasViewModel.TugasType.PUBLIK)
    }

    val listUiState = viewModel.listUiState.collectAsStateWithLifecycle().value

    Scaffold(
        containerColor = Color(0xFFF2F2F2)
    ) { paddingValues ->
        when (val state = listUiState) {
            is TugasViewModel.ListUiState.Loading -> Box(
                modifier = Modifier
                    .padding(paddingValues)
                    .fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = PrimaryOrange)
            }

            is TugasViewModel.ListUiState.Error -> Box(
                modifier = Modifier
                    .padding(paddingValues)
                    .fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(state.message, color = Color.Red)
            }

            is TugasViewModel.ListUiState.Success -> {
                val listForMatkul = state.tugasList.filter { it.matakuliah_id == matkulId }

                val sortState = remember { mutableStateOf("Terbaru") }
                val sortOption = sortState.value

                val sortedList = when (sortOption) {
                    "Terbaru" -> listForMatkul.sortedByDescending { created_atToMillis(it.created_at) }
                    "Terlama" -> listForMatkul.sortedBy { created_atToMillis(it.created_at) }
                    else -> listForMatkul
                }

                val namaMatkul = sortedList.firstOrNull()?.namaMatakuliah ?: "Mata Kuliah"
                val jumlahTugas = sortedList.size

                Column(
                    modifier = Modifier
                        .padding(paddingValues)
                        .fillMaxSize()
                ) {
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
                            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 4.dp),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            items(sortedList) { tugas ->
                                PublikTugasItemCard(
                                    tugas = tugas,
                                    onClick = { onViewDetails(tugas.tugasId ?: 0) }
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
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            val inisial = tugas.namaUser
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
                    text = tugas.judul ?:  "",
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.SemiBold,
                    color = TextDark
                )
                Spacer(Modifier.height(2.dp))
                Text(
                    text = tugas.namaUser ?: "-",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray
                )
            }

            Spacer(Modifier.width(8.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Filled.Schedule,
                    contentDescription = null,
                    tint = Color.Gray,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(Modifier.width(4.dp))
                Text(
                    text = formatTanggal(tugas.created_at),
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray
                )
            }
        }
    }
}

@Composable
fun DeleteConfirmDialog(
    title: String,
    message: String,
    onCancel: () -> Unit,
    onConfirm: () -> Unit
) {
    Dialog(onDismissRequest = onCancel) {
        Card(
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(4.dp),
            modifier = Modifier.widthIn(min = 260.dp, max = 280.dp)
        ) {
            Column(
                modifier = Modifier.padding(horizontal = 20.dp, vertical = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = null,
                    tint = PrimaryOrange,
                    modifier = Modifier.size(32.dp)
                )

                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = TextDark
                )

                Text(
                    text = message,
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray
                )

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedButton(
                        onClick = onCancel,
                        modifier = Modifier
                            .weight(1f)
                            .height(40.dp),
                        shape = RoundedCornerShape(20.dp)
                    ) {
                        Text("Batal")
                    }

                    Button(
                        onClick = onConfirm,
                        modifier = Modifier
                            .weight(1f)
                            .height(40.dp),
                        shape = RoundedCornerShape(20.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = PrimaryOrange,
                            contentColor = Color.White
                        )
                    ) {
                        Text("Hapus")
                    }
                }
            }
        }
    }
}

@Composable
fun LoadingState() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        CircularProgressIndicator(color = PrimaryOrange)
    }
}

@Composable
fun ErrorState(message: String) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text("Error: $message", color = Color.Red)
    }
}

@Composable
fun SegmentedButton(
    isSelected: Boolean,
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = RoundedCornerShape(20.dp),
        color = if (isSelected) Color.White else Color(0xFFFFB300),
        shadowElevation = 0.dp,
        modifier = modifier
            .height(40.dp)
            .clickable(onClick = onClick)
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.fillMaxSize()
        ) {
            Text(
                text = label,
                color = if (isSelected) PrimaryOrange else Color.White,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun SearchAndFilterBar(
    value: String,
    onValueChange: (String) -> Unit,
    showFilterButton: Boolean,
    onFilterClick: () -> Unit
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        TextField(
            value = value,
            onValueChange = onValueChange,
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
            placeholder = { Text("Cari Project...") },
            shape = RoundedCornerShape(12.dp),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White,
                disabledContainerColor = Color.White,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent
            ),
            modifier = Modifier.weight(1f)
        )

        if (showFilterButton) {
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = Color.White,
                border = BorderStroke(1.dp, Color(0xFFE0E0E0)),
                modifier = Modifier
                    .height(56.dp)
                    .width(56.dp)
                    .clickable { onFilterClick() }
            ) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.fillMaxSize()
                ) {
                    Icon(
                        imageVector = Icons.Default.Tune,
                        contentDescription = "Filter",
                        tint = Color(0xFF555555)
                    )
                }
            }
        }
    }
}

@Composable
fun FilterBottomSheet(
    currentTipe: String,
    currentUrut: String,
    onApply: (String, String) -> Unit,
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = Color.White,
            tonalElevation = 4.dp,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
        ) {
            Column(
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Filter & Urutkan",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    IconButton(onClick = onDismiss) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Tutup"
                        )
                    }
                }

                Spacer(Modifier.height(8.dp))
                HorizontalDivider()
                Spacer(Modifier.height(12.dp))

                var tipe by remember(currentTipe) { mutableStateOf(currentTipe) }
                var urut by remember(currentUrut) { mutableStateOf(currentUrut) }

                Text("Tipe Tugas", fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(8.dp))

                FilterRadioRow("Semua", tipe) { tipe = it }
                FilterRadioRow("Individu", tipe) { tipe = it }
                FilterRadioRow("Kelompok", tipe) { tipe = it }

                Spacer(Modifier.height(16.dp))
                Text("Urutkan Berdasarkan", fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(8.dp))

                FilterRadioRow("Terbaru", urut) { urut = it }
                FilterRadioRow("Terlama", urut) { urut = it }

                Spacer(Modifier.height(20.dp))

                Surface(
                    color = Color(0xFFF5F5F5),
                    tonalElevation = 0.dp,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 12.dp)
                    ) {
                        OutlinedButton(
                            onClick = {
                                tipe = "Semua"
                                urut = "Terbaru"
                                onApply("Semua", "Terbaru")
                                onDismiss()
                            },
                            modifier = Modifier
                                .weight(1f)
                                .height(44.dp),
                            shape = RoundedCornerShape(22.dp),
                            border = BorderStroke(1.dp, PrimaryOrange),
                            colors = ButtonDefaults.outlinedButtonColors(
                                containerColor = Color.White,
                                contentColor = PrimaryOrange
                            )
                        ) {
                            Text("Reset", maxLines = 1)
                        }
                        Button(
                            onClick = {
                                onApply(tipe, urut)
                                onDismiss()
                            },
                            modifier = Modifier
                                .weight(2f)
                                .height(44.dp),
                            shape = RoundedCornerShape(22.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = PrimaryOrange,
                                contentColor = Color.White
                            )
                        ) {
                            Text("Terapkan Filter")
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun FilterRadioRow(
    label: String,
    selectedValue: String,
    onSelected: (String) -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onSelected(label) }
            .padding(vertical = 4.dp)
    ) {
        RadioButton(
            selected = selectedValue == label,
            onClick = { onSelected(label) }
        )
        Spacer(Modifier.width(8.dp))
        Text(label)
    }
}

@Composable
fun DeleteSuccessPopup(
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
                    imageVector = Icons.Default.Check,
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

fun formatTanggal(created_at: String?): String {
    if (created_at.isNullOrBlank()) return "—"

    return try {
        val parser = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US)
        parser.timeZone = TimeZone.getTimeZone("UTC")
        val date = parser.parse(created_at) ?: return "—"
        val formatter = SimpleDateFormat("dd MMM yyyy", Locale("id", "ID"))
        formatter.format(date)
    } catch (e: Exception) {
        "—"
    }
}

private fun created_atToMillis(created_at: String?): Long {
    if (created_at.isNullOrBlank()) return 0L
    return try {
        val parser = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US)
        parser.timeZone = TimeZone.getTimeZone("UTC")
        parser.parse(created_at)?.time ?: 0L
    } catch (e: Exception) {
        0L
    }
}
