package com.example.arsisi_frontend.ui.tugas

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlinx.coroutines.delay

val LightGrayBackground = Color(0xFFF5F5F5)
val CardCorner = 12.dp

// Format "2025-12-04T15:34:16.000Z" -> "04 Des 2025"
fun formatTanggal(createdAt: String?): String {
    if (createdAt.isNullOrBlank()) return "—"

    return try {
        val parser = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US)
        parser.timeZone = java.util.TimeZone.getTimeZone("UTC")
        val date: Date = parser.parse(createdAt)

        val formatter = SimpleDateFormat("dd MMM yyyy", Locale("id", "ID"))
        formatter.format(date)
    } catch (e: Exception) {
        "—"
    }
}

// Untuk sort berdasarkan createdAt
private fun createdAtToMillis(createdAt: String?): Long {
    if (createdAt.isNullOrBlank()) return 0L
    return try {
        val parser = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US)
        parser.timeZone = java.util.TimeZone.getTimeZone("UTC")
        parser.parse(createdAt)?.time ?: 0L
    } catch (e: Exception) {
        0L
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TugasListScreen(
    viewModel: TugasViewModel,
    onNavigateToForm: () -> Unit,
    onViewDetails: (Int) -> Unit,
    onEditTask: (Tugas) -> Unit,
    onOpenEksplorasiMatkul: (MataKuliahEksplorasi) -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val selectedType by viewModel.selectedType.collectAsStateWithLifecycle()

    var searchText by remember { mutableStateOf("") }
    var showFilter by remember { mutableStateOf(false) }

    // state filter (hanya dipakai di tab Tugas Saya)
    var selectedTipe by remember { mutableStateOf("Semua") }
    var selectedUrut by remember { mutableStateOf("Terbaru") }

    // pesan sukses hapus tugas
    var deleteSuccessMessage by remember { mutableStateOf<String?>(null) }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = LightGrayBackground
    ) {
        Box(modifier = Modifier.fillMaxSize()) {

            // KONTEN UTAMA
            Column(modifier = Modifier.fillMaxSize()) {

                RepositoryTopBar(
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
                        showFilterButton = selectedType == TugasType.SAYA,
                        onFilterClick = { showFilter = true }
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                when (val state = uiState) {
                    TugasUiState.Loading -> LoadingState()
                    is TugasUiState.Error -> ErrorState(state.message)
                    is TugasUiState.Success -> {

                        if (selectedType == TugasType.SAYA) {
                            // ===== TAB TUGAS SAYA =====
                            val base = state.tugasList.filter { tugas ->
                                searchText.isBlank() ||
                                        tugas.judul.contains(searchText, true) ||
                                        (tugas.namaMatakuliah?.contains(searchText, true) == true)
                            }

                            val byType = when (selectedTipe) {
                                "Individu" -> base.filter { t ->
                                    t.tipe_tugas?.equals("Individu", ignoreCase = true) == true
                                }
                                "Kelompok" -> base.filter { t ->
                                    t.tipe_tugas?.equals("Kelompok", ignoreCase = true) == true
                                }
                                else -> base
                            }

                            val sortedTasks = when (selectedUrut) {
                                "Terbaru" -> byType.sortedByDescending { createdAtToMillis(it.createdAt) }
                                "Terlama" -> byType.sortedBy { createdAtToMillis(it.createdAt) }
                                else -> byType
                            }

                            TugasContent(
                                tugasList = sortedTasks,
                                isPublic = false,
                                onViewDetails = onViewDetails,
                                onEditTask = onEditTask,
                                onDeleteTask = { id ->
                                    viewModel.deleteTugas(id)              // langsung ke ViewModel
                                    deleteSuccessMessage = "Tugas berhasil dihapus"
                                }
                            )
                        } else {
                            // ===== TAB EKSPLORASI =====
                            val base = state.tugasList.filter { tugas ->
                                searchText.isBlank() ||
                                        (tugas.namaMatakuliah?.contains(searchText, true) == true) ||
                                        (tugas.kodeMatakuliah?.contains(searchText, true) == true)
                            }

                            val eksplorasiList = viewModel.getEksplorasiPerMatkul(base)
                            EksplorasiMatkulContent(
                                list = eksplorasiList,
                                onItemClick = onOpenEksplorasiMatkul
                            )
                        }
                    }
                }
            }

            // FAB pojok kanan bawah
            if (selectedType == TugasType.SAYA) {
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

            if (showFilter && selectedType == TugasType.SAYA) {
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

            // Popup pesan sukses hapus
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
fun RepositoryTopBar(
    selectedType: TugasType,
    onTypeSelected: (TugasType) -> Unit
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
                    isSelected = selectedType == TugasType.SAYA,
                    label = "Tugas Saya",
                    onClick = { onTypeSelected(TugasType.SAYA) },
                    modifier = Modifier.weight(1f)
                )
                SegmentedButton(
                    isSelected = selectedType == TugasType.PUBLIK,
                    label = "Eksplorasi",
                    onClick = { onTypeSelected(TugasType.PUBLIK) },
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
    onEditTask: (Tugas) -> Unit,
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
        items(tugasList) { tugas ->
            TugasCard(
                tugas = tugas,
                isPublic = isPublic,
                onClick = { onViewDetails(tugas.tugasId) },
                onEditClick = onEditTask,
                onDeleteClick = onDeleteTask
            )
        }
    }
}

/** LIST EKSPLORASI PER MATA KULIAH **/
@Composable
fun EksplorasiMatkulContent(
    list: List<MataKuliahEksplorasi>,
    onItemClick: (MataKuliahEksplorasi) -> Unit
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
                onClick = { onItemClick(item) }
            )
        }
    }
}

@Composable
fun EksplorasiMatkulCard(
    item: MataKuliahEksplorasi,
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
            modifier = Modifier
                .padding(horizontal = 16.dp, vertical = 12.dp),
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

            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
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
    onEditClick: (Tugas) -> Unit,
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
                    text = tugas.judul,
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
                    Text(tugas.visibility, color = Color.Gray)

                    Spacer(Modifier.width(16.dp))

                    Icon(
                        Icons.Default.Schedule,
                        contentDescription = null,
                        tint = Color.Gray,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(Modifier.width(6.dp))
                    Text(formatTanggal(tugas.createdAt), color = Color.Gray)
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

    // Dialog pertama: Edit / Hapus
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
                    modifier = Modifier
                        .widthIn(min = 220.dp, max = 260.dp)
                ) {
                    Box {
                        Column(
                            modifier = Modifier
                                .padding(horizontal = 12.dp, vertical = 10.dp),
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Button(
                                onClick = {
                                    showActionDialog = false
                                    onEditClick(tugas)
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
                                    // tutup menu, buka dialog verifikasi hapus
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

    // Dialog kedua: verifikasi hapus
    if (showConfirmDelete && !isPublic) {
        DeleteConfirmDialog(
            title = "Hapus Tugas ?",
            message = "Anda yakin ingin menghapus tugas \"${tugas.judul}\"?",
            onCancel = { showConfirmDelete = false },
            onConfirm = {
                showConfirmDelete = false
                onDeleteClick(tugas.tugasId)
            }
        )
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
            modifier = Modifier
                .widthIn(min = 260.dp, max = 280.dp)
        ) {
            Column(
                modifier = Modifier
                    .padding(horizontal = 20.dp, vertical = 16.dp),
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
                modifier = Modifier
                    .padding(horizontal = 16.dp, vertical = 12.dp)
            ) {

                // Header: judul + tombol X
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
                Divider()
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

                // Area tombol bawah
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
        delay(1500)   // tampil 1.5 detik lalu hilang
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
