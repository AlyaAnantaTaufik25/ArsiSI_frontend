package com.example.arsisi_frontend.ui.agenda
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.History
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.arsisi_frontend.data.repository.Agenda
import com.example.arsisi_frontend.navigation.Screen
import com.example.arsisi_frontend.ui.theme.*
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.foundation.clickable
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.window.Dialog
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.min
import coil.compose.AsyncImage
import androidx.compose.ui.layout.ContentScale
import com.example.arsisi_frontend.utils.Constants
fun getDaysUntilDeadline(agenda: Agenda): Int {
    return try {
        val dateFormatWithYear = SimpleDateFormat("d MMM yyyy", Locale.ENGLISH)
        val dateFormatWithoutYear = SimpleDateFormat("d MMM", Locale.ENGLISH)
        var agendaDateParsed = try {
            dateFormatWithYear.parse(agenda.date.trim())
        } catch (e: Exception) {
            null
        }
        if (agendaDateParsed == null) {
            try {
                val idLocale = Locale("id", "ID")
                val dateFormatWithYearId = SimpleDateFormat("d MMM yyyy", idLocale)
                agendaDateParsed = dateFormatWithYearId.parse(agenda.date.trim())
            } catch (e: Exception) {
            }
        }
        if (agendaDateParsed == null) {
            try {
                agendaDateParsed = dateFormatWithoutYear.parse(agenda.date.trim())
            } catch (e: Exception) {
            }
        }
        if (agendaDateParsed == null) {
            try {
                val idLocale = Locale("id", "ID")
                val dateFormatWithoutYearId = SimpleDateFormat("d MMM", idLocale)
                agendaDateParsed = dateFormatWithoutYearId.parse(agenda.date.trim())
            } catch (e: Exception) {
            }
        }
        if (agendaDateParsed == null) {
            android.util.Log.e("getDaysUntilDeadline", "Failed to parse date: ${agenda.date}")
            return Int.MAX_VALUE
        }
        val today = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        val agendaCalendar = Calendar.getInstance().apply {
            time = agendaDateParsed
            if (!agenda.date.trim().matches(Regex(".*\\d{4}.*"))) {
                set(Calendar.YEAR, today.get(Calendar.YEAR))
            }
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        val diffInMillis = agendaCalendar.timeInMillis - today.timeInMillis
        val diffInDays = (diffInMillis / (1000 * 60 * 60 * 24)).toInt()
        if (diffInDays < 0) {
            return 0
        }
        diffInDays
    } catch (e: Exception) {
        android.util.Log.e("getDaysUntilDeadline", "Error calculating days until deadline: ${e.message}", e)
        Int.MAX_VALUE
    }
}
fun isAgendaToday(agenda: Agenda): Boolean {
    return try {
        val dateFormatWithYear = SimpleDateFormat("d MMM yyyy", Locale.ENGLISH)
        val dateFormatWithoutYear = SimpleDateFormat("d MMM", Locale.ENGLISH)
        var agendaDateParsed = try {
            dateFormatWithYear.parse(agenda.date.trim())
        } catch (e: Exception) {
            null
        }
        if (agendaDateParsed == null) {
            try {
                val idLocale = Locale("id", "ID")
                val dateFormatWithYearId = SimpleDateFormat("d MMM yyyy", idLocale)
                agendaDateParsed = dateFormatWithYearId.parse(agenda.date.trim())
            } catch (e: Exception) {
            }
        }
        if (agendaDateParsed == null) {
            try {
                agendaDateParsed = dateFormatWithoutYear.parse(agenda.date.trim())
            } catch (e: Exception) {
            }
        }
        if (agendaDateParsed == null) {
            try {
                val idLocale = Locale("id", "ID")
                val dateFormatWithoutYearId = SimpleDateFormat("d MMM", idLocale)
                agendaDateParsed = dateFormatWithoutYearId.parse(agenda.date.trim())
            } catch (e: Exception) {
            }
        }
        if (agendaDateParsed == null) {
            android.util.Log.e("isAgendaToday", "Failed to parse date: ${agenda.date}")
            return false
        }
        val today = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        val agendaCalendar = Calendar.getInstance().apply {
            time = agendaDateParsed
            if (!agenda.date.trim().matches(Regex(".*\\d{4}.*"))) {
                set(Calendar.YEAR, today.get(Calendar.YEAR))
            }
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        val isSameDay = agendaCalendar.get(Calendar.YEAR) == today.get(Calendar.YEAR) &&
                        agendaCalendar.get(Calendar.MONTH) == today.get(Calendar.MONTH) &&
                        agendaCalendar.get(Calendar.DAY_OF_MONTH) == today.get(Calendar.DAY_OF_MONTH)
        android.util.Log.d("isAgendaToday",
            "Agenda: ${agenda.title}, " +
            "Date: ${agenda.date}, " +
            "AgendaDate: ${agendaCalendar.get(Calendar.DAY_OF_MONTH)}/${agendaCalendar.get(Calendar.MONTH) + 1}/${agendaCalendar.get(Calendar.YEAR)}, " +
            "Today: ${today.get(Calendar.DAY_OF_MONTH)}/${today.get(Calendar.MONTH) + 1}/${today.get(Calendar.YEAR)}, " +
            "IsToday: $isSameDay")
        isSameDay
    } catch (e: Exception) {
        android.util.Log.e("isAgendaToday", "Error checking today agenda: ${e.message}", e)
        false
    }
}
fun isAgendaCompleted(agenda: Agenda): Boolean {
    return try {
        val dateFormatWithYear = SimpleDateFormat("d MMM yyyy", Locale.ENGLISH)
        val dateFormatWithoutYear = SimpleDateFormat("d MMM", Locale.ENGLISH)
        var agendaDateParsed = try {
            dateFormatWithYear.parse(agenda.date.trim())
        } catch (e: Exception) {
            null
        }
        if (agendaDateParsed == null) {
            try {
                val idLocale = Locale("id", "ID")
                val dateFormatWithYearId = SimpleDateFormat("d MMM yyyy", idLocale)
                agendaDateParsed = dateFormatWithYearId.parse(agenda.date.trim())
            } catch (e: Exception) {
            }
        }
        if (agendaDateParsed == null) {
            try {
                agendaDateParsed = dateFormatWithoutYear.parse(agenda.date.trim())
            } catch (e: Exception) {
            }
        }
        if (agendaDateParsed == null) {
            try {
                val idLocale = Locale("id", "ID")
                val dateFormatWithoutYearId = SimpleDateFormat("d MMM", idLocale)
                agendaDateParsed = dateFormatWithoutYearId.parse(agenda.date.trim())
            } catch (e: Exception) {
            }
        }
        if (agendaDateParsed == null) {
            android.util.Log.e("isAgendaCompleted", "Failed to parse date: ${agenda.date}")
            return false
        }
        val now = Calendar.getInstance()
        val agendaCalendar = Calendar.getInstance().apply {
            time = agendaDateParsed
            if (!agenda.date.trim().matches(Regex(".*\\d{4}.*"))) {
                set(Calendar.YEAR, now.get(Calendar.YEAR))
            }
            if (!agenda.time.isNullOrBlank() && agenda.time.contains(":")) {
                try {
                    val timeParts = agenda.time.trim().split(":")
                    if (timeParts.isNotEmpty()) {
                        val hour = timeParts[0].toIntOrNull() ?: 23
                        val minute = if (timeParts.size > 1) timeParts[1].toIntOrNull() ?: 59 else 59
                        val second = if (timeParts.size > 2) timeParts[2].toIntOrNull() ?: 0 else 0
                        set(Calendar.HOUR_OF_DAY, hour)
                        set(Calendar.MINUTE, minute)
                        set(Calendar.SECOND, second)
                        set(Calendar.MILLISECOND, 0)
                    } else {
                        set(Calendar.HOUR_OF_DAY, 23)
                        set(Calendar.MINUTE, 59)
                        set(Calendar.SECOND, 59)
                    }
                } catch (e: Exception) {
                     set(Calendar.HOUR_OF_DAY, 23)
                     set(Calendar.MINUTE, 59)
                     set(Calendar.SECOND, 59)
                }
            } else {
                set(Calendar.HOUR_OF_DAY, 23)
                set(Calendar.MINUTE, 59)
                set(Calendar.SECOND, 59)
            }
            set(Calendar.MILLISECOND, 0)
        }
        val comparison = agendaCalendar.compareTo(now)
        val isCompleted = comparison < 0
        android.util.Log.d("isAgendaCompleted",
            "Agenda: ${agenda.title}, " +
            "DateTime: ${agenda.date} ${agenda.time}, " +
            "AgendaCal: ${agendaCalendar.time}, " +
            "Now: ${now.time}, " +
            "IsCompleted: $isCompleted")
        isCompleted
    } catch (e: Exception) {
        android.util.Log.e("isAgendaCompleted", "Error checking completed agenda: ${e.message}", e)
        false
    }
}
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AgendaListScreen(
    navController: NavController,
    viewModel: AgendaViewModel
) {
    val agendas by viewModel.agendas.collectAsState()
    val allAgendas by viewModel.allAgendas.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val totalAgenda = allAgendas.size
    val activeAgendaCount = allAgendas.count { !isAgendaCompleted(it) }
    val activeAgendas = allAgendas.filter { !isAgendaCompleted(it) }
    val todayAgendaCount = activeAgendas.count { isAgendaToday(it) }
    var selectedTabIndex by remember { mutableStateOf(0) }
    val tabs = listOf("Daftar", "Kalender", "Ringkasan")
    val showFilterSheet by viewModel.showFilterSheet.collectAsState()
    val selectedKategori by viewModel.selectedKategori.collectAsState()
    val selectedUrutkan by viewModel.selectedUrutkan.collectAsState()
    if (showFilterSheet) {
        FilterSortBottomSheet(
            viewModel = viewModel,
            selectedKategori = selectedKategori,
            kategoriOptions = viewModel.kategoriOptions,
            selectedUrutkan = selectedUrutkan,
            urutkanOptions = viewModel.urutkanOptions,
            onDismiss = { viewModel.onDismissFilterSheet() }
        )
    }
    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = { navController.navigate(Screen.AgendaForm.route) },
                containerColor = Orange500,
                contentColor = Color.White,
                shape = CircleShape
            ) {
                Icon(Icons.Default.Add, contentDescription = "Tambah")
            }
        },
        containerColor = Color(0xFFFAFAFA)
    ) { paddingValues ->
        val scrollState = rememberScrollState()
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(bottom = paddingValues.calculateBottomPadding())
        ) {
            HeaderSection(activeAgendaCount, todayAgendaCount)
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            ) {
                SearchBar(
                    query = searchQuery,
                    onQueryChange = { viewModel.onSearchQueryChanged(it) },
                    onFilterClicked = { viewModel.onFilterIconClicked() }
                )
                TabRow(
                    selectedTabIndex = selectedTabIndex,
                    containerColor = Color(0xFFFAFAFA),
                    contentColor = Orange700,
                    divider = {},
                    indicator = { }
                ) {
                    tabs.forEachIndexed { index, title ->
                        Tab(
                            selected = selectedTabIndex == index,
                            onClick = { selectedTabIndex = index },
                            text = {
                                Text(
                                    title,
                                    fontWeight = if (selectedTabIndex == index) FontWeight.Bold else FontWeight.Normal
                                )
                            },
                            selectedContentColor = Color.White,
                            unselectedContentColor = ArsiTextSecondary,
                            modifier = if (selectedTabIndex == index) {
                                Modifier
                                    .padding(4.dp)
                                    .background(Orange500, shape = RoundedCornerShape(50))
                                    .clip(RoundedCornerShape(50))
                            } else { Modifier.padding(4.dp) }
                        )
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
            }
                when (selectedTabIndex) {
                0 -> AgendaDaftarTabContent(agendas, viewModel, navController)
                1 -> AgendaKalenderTabContent(agendas)
                2 -> AgendaStatistikTabContent(agendas = allAgendas, navController = navController, viewModel = viewModel)
            }
            Spacer(modifier = Modifier.height(80.dp))
        }
    }
}
@Composable
fun HeaderSection(agendaAktif: Int, agendaHariIni: Int) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Orange600, shape = RoundedCornerShape(bottomStart = 24.dp, bottomEnd = 24.dp))
            .statusBarsPadding()
            .padding(start = 16.dp, end = 16.dp, bottom = 24.dp, top = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Agenda & Reminder", style = MaterialTheme.typography.headlineMedium, color = Color.White, fontWeight = FontWeight.Bold)
        Text("Kelola jadwal & deadline anda", style = MaterialTheme.typography.bodyMedium, color = Color.White.copy(alpha = 0.9f))
        Spacer(modifier = Modifier.height(24.dp))
        Row(horizontalArrangement = Arrangement.SpaceEvenly, modifier = Modifier.fillMaxWidth()) {
            SummaryCard("$agendaAktif", "Agenda Aktif", Modifier.weight(1f))
            Spacer(modifier = Modifier.width(16.dp))
            SummaryCard("$agendaHariIni", "Agenda Hari Ini", Modifier.weight(1f))
        }
    }
}
@Composable
fun SummaryCard(value: String, label: String, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Orange500.copy(alpha = 0.8f)),
        elevation = CardDefaults.cardElevation(0.dp)
    ) {
        Column(
            modifier = Modifier.padding(vertical = 16.dp, horizontal = 12.dp).fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(value, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.ExtraBold, color = Color.White)
            Text(label, style = MaterialTheme.typography.labelMedium, color = Color.White.copy(alpha = 0.9f))
        }
    }
}
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchBar(query: String, onQueryChange: (String) -> Unit, onFilterClicked: () -> Unit) {
    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(vertical = 16.dp)) {
        OutlinedTextField(
            value = query,
            onValueChange = onQueryChange,
            placeholder = { Text("Cari agenda....", color = Gray500) },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Cari", tint = Gray500) },
            modifier = Modifier.weight(1f).height(50.dp),
            shape = RoundedCornerShape(24.dp),
            colors = TextFieldDefaults.colors(
                unfocusedContainerColor = Color.White, focusedContainerColor = Color.White,
                unfocusedIndicatorColor = Color.Transparent, focusedIndicatorColor = Orange600,
                focusedTextColor = Black, unfocusedTextColor = Black, cursorColor = Black
            )
        )
        Spacer(modifier = Modifier.width(8.dp))
        IconButton(
            onClick = onFilterClicked,
            modifier = Modifier.size(50.dp).background(Color.White, RoundedCornerShape(12.dp)).border(1.dp, ArsiGray, RoundedCornerShape(12.dp))
        ) {
            Icon(Icons.Default.FilterList, contentDescription = "Filter", tint = Gray500)
        }
    }
}
@Composable
fun AgendaDaftarTab(agendas: List<Agenda>) {
    LazyColumn(
        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, bottom = 80.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(agendas) { agenda -> AgendaItemCard(agenda) }
    }
}
@Composable
fun AgendaDaftarTabContent(agendas: List<Agenda>, viewModel: AgendaViewModel, navController: NavController) {
    var agendaToDelete by remember { mutableStateOf<Agenda?>(null) }
    var showDeleteDialog by remember { mutableStateOf(false) }
    val showDetailDialog by viewModel.showDetailDialog.collectAsState()
    val selectedAgenda by viewModel.selectedAgendaForDetail.collectAsState()
    val activeAgendas = agendas.filter { !isAgendaCompleted(it) }
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        if (activeAgendas.isEmpty()) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(2.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        Icons.Default.CheckCircle,
                        contentDescription = null,
                        tint = Success,
                        modifier = Modifier.size(48.dp)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        "Tidak ada agenda aktif",
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Medium,
                        color = TextPrimary
                    )
                    Text(
                        "Tekan tombol + untuk menambah agenda baru",
                        style = MaterialTheme.typography.bodySmall,
                        color = Gray600
                    )
                    Text(
                        "Lihat riwayat di tab Ringkasan",
                        style = MaterialTheme.typography.bodySmall,
                        color = Gray400
                    )
                }
            }
        } else {
            activeAgendas.forEach { agenda ->
                AgendaItemCard(
                    agenda = agenda,
                    onEditClick = {
                        viewModel.showAgendaDetail(agenda)
                    },
                    onDeleteClick = {
                        agendaToDelete = agenda
                        showDeleteDialog = true
                    },
                    onClick = {
                        viewModel.showAgendaDetail(agenda)
                    }
                )
            }
        }
    }
    if (showDeleteDialog && agendaToDelete != null) {
        DeleteConfirmDialog(
            agendaTitle = agendaToDelete!!.title,
            onDismiss = {
                showDeleteDialog = false
                agendaToDelete = null
            },
            onConfirm = {
                viewModel.deleteAgenda(agendaToDelete!!.id)
                showDeleteDialog = false
                agendaToDelete = null
            }
        )
    }
    if (showDetailDialog && selectedAgenda != null) {
        AgendaDetailDialog(
            agenda = selectedAgenda!!,
            navController = navController,
            onDismiss = { viewModel.dismissDetailDialog() },
            onEditClick = {
                viewModel.dismissDetailDialog()
                navController.navigate("${Screen.AgendaForm.route}?agendaId=${selectedAgenda!!.id}")
            }
        )
    }
}
@Composable
fun HistoryAgendaItem(
    agenda: Agenda,
    onClick: () -> Unit = {}
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Surface(
            shape = CircleShape,
            color = Success.copy(alpha = 0.15f),
            modifier = Modifier.size(36.dp)
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.fillMaxSize()
            ) {
                Icon(
                    Icons.Default.CheckCircle,
                    contentDescription = null,
                    tint = ArsiGreen,
                    modifier = Modifier.size(24.dp)
                )
            }
        }
        Spacer(modifier = Modifier.width(12.dp))
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                agenda.title,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold,
                color = Black,
                maxLines = 1,
                overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
            )
            Spacer(modifier = Modifier.height(2.dp))
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    Icons.Default.DateRange,
                    contentDescription = null,
                    tint = Gray500,
                    modifier = Modifier.size(12.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    "${agenda.date} • ${agenda.time}",
                    style = MaterialTheme.typography.bodySmall,
                    color = Gray600
                )
            }
        }
        Surface(
            shape = RoundedCornerShape(6.dp),
            color = when(agenda.priority) {
                "Tinggi" -> Error.copy(alpha = 0.1f)
                "Sedang" -> Orange600.copy(alpha = 0.1f)
                else -> Blue600.copy(alpha = 0.1f)
            }
        ) {
            Text(
                agenda.priority,
                color = when(agenda.priority) {
                    "Tinggi" -> Error
                    "Sedang" -> Orange600
                    else -> Blue600
                },
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
            )
        }
    }
}
@Composable
fun AgendaItemCard(
    agenda: Agenda,
    onEditClick: () -> Unit = {},
    onDeleteClick: () -> Unit = {},
    onClick: () -> Unit = {}
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Column(modifier = Modifier.weight(1f)) {
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = when(agenda.priority) {
                        "Tinggi" -> Error.copy(alpha = 0.1f)
                        "Sedang" -> Orange600.copy(alpha = 0.1f)
                        else -> Blue600.copy(alpha = 0.1f)
                    }
                ) {
                    Text(
                        "Prioritas ${agenda.priority}",
                        color = when(agenda.priority) {
                            "Tinggi" -> Error
                            "Sedang" -> Orange600
                            else -> Blue600
                        },
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text(agenda.title, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Bold, color = Black)
                Spacer(modifier = Modifier.height(6.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.DateRange, contentDescription = null, tint = Gray500, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("${agenda.date} • ${agenda.time}", style = MaterialTheme.typography.bodySmall, color = Gray500)
                }
            }
            Row {
                IconButton(onClick = onEditClick, modifier = Modifier.size(32.dp)) {
                    Icon(Icons.Default.Edit, null, tint = Blue600, modifier = Modifier.size(20.dp))
                }
                Spacer(modifier = Modifier.width(4.dp))
                IconButton(onClick = onDeleteClick, modifier = Modifier.size(32.dp)) {
                    Icon(Icons.Default.Delete, null, tint = Error, modifier = Modifier.size(20.dp))
                }
            }
        }
    }
}
@Composable
fun AgendaKalenderTab(agendas: List<Agenda>) {
    var selectedDate by remember { mutableStateOf<Date?>(null) }
    var showAgendaDialog by remember { mutableStateOf(false) }
    var selectedDateAgendas by remember { mutableStateOf<List<Agenda>>(emptyList()) }
    val initialCalendar = Calendar.getInstance()
    var currentMonth by remember { mutableStateOf(initialCalendar.get(Calendar.MONTH)) }
    var currentYear by remember { mutableStateOf(initialCalendar.get(Calendar.YEAR)) }
    val agendaMap = remember(agendas, currentMonth, currentYear) {
        try {
            agendas.filter { it.date.isNotBlank() }.groupBy { agenda ->
                try {
                    val dateFormat = SimpleDateFormat("d MMM", Locale.ENGLISH)
                    val date = dateFormat.parse(agenda.date.trim())
                    date?.let {
                        val cal = Calendar.getInstance()
                        cal.time = it
                        Triple(cal.get(Calendar.DAY_OF_MONTH), cal.get(Calendar.MONTH), currentYear)
                    } ?: null
                } catch (e: Exception) {
                    null
                }
            }.filterKeys { it != null }.mapKeys { it.key!! }
        } catch (e: Exception) {
            emptyMap()
        }
    }
    val scrollState = rememberScrollState()
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(horizontal = 16.dp)
    ) {
        CalendarWidget(
            currentMonth = currentMonth,
            currentYear = currentYear,
            onMonthChange = { month, year ->
                currentMonth = month
                currentYear = year
            },
            agendaMap = agendaMap,
            onDateClick = { day, month, year ->
                try {
                    val clickedDate = Calendar.getInstance().apply {
                        set(Calendar.YEAR, year)
                        set(Calendar.MONTH, month)
                        set(Calendar.DAY_OF_MONTH, day)
                    }.time
                    val dateKey = Triple(day, month, year)
                    val agendasForDate = agendaMap[dateKey] ?: emptyList()
                    selectedDate = clickedDate
                    selectedDateAgendas = agendasForDate
                    showAgendaDialog = true
                } catch (e: Exception) {
                }
            }
        )
        Spacer(modifier = Modifier.height(80.dp))
    }
    if (showAgendaDialog) {
        AgendaDateDialog(
            date = selectedDate,
            agendas = selectedDateAgendas,
            onDismiss = { showAgendaDialog = false }
        )
    }
}
@Composable
fun AgendaKalenderTabContent(agendas: List<Agenda>) {
    var selectedDate by remember { mutableStateOf<Date?>(null) }
    var showAgendaDialog by remember { mutableStateOf(false) }
    var selectedDateAgendas by remember { mutableStateOf<List<Agenda>>(emptyList()) }
    val initialCalendar = Calendar.getInstance()
    var currentMonth by remember { mutableStateOf(initialCalendar.get(Calendar.MONTH)) }
    var currentYear by remember { mutableStateOf(initialCalendar.get(Calendar.YEAR)) }
    val agendaMap = remember(agendas, currentMonth, currentYear) {
        try {
            agendas.filter { it.date.isNotBlank() }.groupBy { agenda ->
                try {
                    val dateFormat = SimpleDateFormat("d MMM", Locale.ENGLISH)
                    val date = dateFormat.parse(agenda.date.trim())
                    date?.let {
                        val cal = Calendar.getInstance()
                        cal.time = it
                        Triple(cal.get(Calendar.DAY_OF_MONTH), cal.get(Calendar.MONTH), currentYear)
                    } ?: null
                } catch (e: Exception) {
                    null
                }
            }.filterKeys { it != null }.mapKeys { it.key!! }
        } catch (e: Exception) {
            emptyMap()
        }
    }
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
    ) {
        CalendarWidget(
            currentMonth = currentMonth,
            currentYear = currentYear,
            onMonthChange = { month, year ->
                currentMonth = month
                currentYear = year
            },
            agendaMap = agendaMap,
            onDateClick = { day, month, year ->
                try {
                    val clickedDate = Calendar.getInstance().apply {
                        set(Calendar.YEAR, year)
                        set(Calendar.MONTH, month)
                        set(Calendar.DAY_OF_MONTH, day)
                    }.time
                    val dateKey = Triple(day, month, year)
                    val agendasForDate = agendaMap[dateKey] ?: emptyList()
                    selectedDate = clickedDate
                    selectedDateAgendas = agendasForDate
                    showAgendaDialog = true
                } catch (e: Exception) {
                }
            }
        )
    }
    if (showAgendaDialog) {
        AgendaDateDialog(
            date = selectedDate,
            agendas = selectedDateAgendas,
            onDismiss = { showAgendaDialog = false }
        )
    }
}
@Composable
fun CalendarWidget(
    currentMonth: Int,
    currentYear: Int,
    onMonthChange: (Int, Int) -> Unit,
    agendaMap: Map<Triple<Int, Int, Int>, List<Agenda>>,
    onDateClick: (Int, Int, Int) -> Unit
) {
    val monthNames = arrayOf(
        "Januari", "Februari", "Maret", "April", "Mei", "Juni",
        "Juli", "Agustus", "September", "Oktober", "November", "Desember"
    )
    val dayNames = arrayOf("Min", "Sen", "Sel", "Rab", "Kam", "Jum", "Sab")
    val calendarInfo = remember(currentMonth, currentYear) {
        try {
            val cal = Calendar.getInstance()
            cal.set(Calendar.YEAR, currentYear)
            cal.set(Calendar.MONTH, currentMonth)
            cal.set(Calendar.DAY_OF_MONTH, 1)
            val firstDay = cal.get(Calendar.DAY_OF_WEEK)
            val daysInMonth = cal.getActualMaximum(Calendar.DAY_OF_MONTH)
            Pair(firstDay, daysInMonth)
        } catch (e: Exception) {
            Pair(Calendar.SUNDAY, 31)
        }
    }
    val firstDayOfWeek = calendarInfo.first
    val daysInMonth = calendarInfo.second
    val startOffset = (firstDayOfWeek - Calendar.SUNDAY) % 7
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = {
                        val newMonth = if (currentMonth == 0) 11 else currentMonth - 1
                        val newYear = if (currentMonth == 0) currentYear - 1 else currentYear
                        onMonthChange(newMonth, newYear)
                    }
                ) {
                    Text("◀", fontSize = 18.sp, color = Orange700)
                }
                Text(
                    "${monthNames[currentMonth]} $currentYear",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = Black
                )
                IconButton(
                    onClick = {
                        val newMonth = if (currentMonth == 11) 0 else currentMonth + 1
                        val newYear = if (currentMonth == 11) currentYear + 1 else currentYear
                        onMonthChange(newMonth, newYear)
                    }
                ) {
                    Text("▶", fontSize = 18.sp, color = Orange700)
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                dayNames.forEach { day ->
                    Text(
                        day,
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.Bold,
                        color = Gray500,
                        modifier = Modifier.weight(1f),
                        textAlign = TextAlign.Center
                    )
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            val totalCells = 42
            val weeks = (startOffset + daysInMonth + 6) / 7
            Column {
                repeat(weeks) { week ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        repeat(7) { dayOfWeek ->
                            val dayIndex = week * 7 + dayOfWeek
                            val day = if (dayIndex >= startOffset && dayIndex < startOffset + daysInMonth) {
                                dayIndex - startOffset + 1
                            } else {
                                null
                            }
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .height(40.dp)
                                    .padding(2.dp)
                            ) {
                                if (day != null) {
                                    val dateKey = Triple(day, currentMonth, currentYear)
                                    val hasAgenda = agendaMap.containsKey(dateKey)
                                    Box(
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .clickable { onDateClick(day, currentMonth, currentYear) }
                                            .background(
                                                if (hasAgenda) Orange500.copy(alpha = 0.3f) else Color.Transparent,
                                                shape = RoundedCornerShape(8.dp)
                                            )
                                            .padding(2.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Column(
                                            horizontalAlignment = Alignment.CenterHorizontally
                                        ) {
                                            Text(
                                                "$day",
                                                style = MaterialTheme.typography.bodySmall,
                                                color = Black,
                                                fontWeight = if (hasAgenda) FontWeight.Bold else FontWeight.Normal
                                            )
                                            if (hasAgenda) {
                                                Spacer(modifier = Modifier.height(2.dp))
                                                Box(
                                                    modifier = Modifier
                                                        .size(4.dp)
                                                        .background(Error, CircleShape)
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AgendaDateDialog(
    date: Date?,
    agendas: List<Agenda>,
    onDismiss: () -> Unit
) {
    val dateString = remember(date) {
        date?.let {
            try {
                val dateFormat = SimpleDateFormat("EEEE, d MMMM yyyy", Locale.getDefault())
                dateFormat.format(it)
            } catch (e: Exception) {
                val simpleFormat = SimpleDateFormat("d MMMM yyyy", Locale.getDefault())
                simpleFormat.format(it)
            }
        } ?: ""
    }
    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(24.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        dateString,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Tutup", tint = Gray500)
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
                if (agendas.isEmpty()) {
                    Text(
                        "Tidak ada agenda pada tanggal ini",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Gray500,
                        modifier = Modifier.padding(vertical = 16.dp)
                    )
                } else {
                    agendas.forEach { agenda ->
                        AgendaDetailItem(agenda)
                        if (agenda != agendas.last()) {
                            Spacer(modifier = Modifier.height(12.dp))
                        }
                    }
                }
            }
        }
    }
}
@Composable
fun AgendaDetailItem(agenda: Agenda) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Orange100.copy(alpha = 0.5f)),
        elevation = CardDefaults.cardElevation(0.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                agenda.title,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Bold,
                color = ArsiBlack
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                agenda.description,
                style = MaterialTheme.typography.bodyMedium,
                color = Gray600
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    Icons.Default.DateRange,
                    contentDescription = null,
                    tint = Gray500,
                    modifier = Modifier.size(14.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    "${agenda.date} • ${agenda.time}",
                    style = MaterialTheme.typography.bodySmall,
                    color = Gray600
                )
                Spacer(modifier = Modifier.width(16.dp))
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = when(agenda.priority) {
                        "Tinggi" -> Error.copy(alpha = 0.1f)
                        "Sedang" -> Orange600.copy(alpha = 0.1f)
                        else -> Blue600.copy(alpha = 0.1f)
                    }
                ) {
                    Text(
                        agenda.priority,
                        color = when(agenda.priority) {
                            "Tinggi" -> Error
                            "Sedang" -> Orange600
                            else -> Blue600
                        },
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }
        }
    }
}
@Composable
fun AgendaStatistikTab(agendas: List<Agenda>) {
    val scrollState = rememberScrollState()
    val totalCount = agendas.size
    val completedCount = agendas.count { isAgendaCompleted(it) }
    val completedProgress = if (totalCount > 0) completedCount.toFloat() / totalCount.toFloat() else 0f
    val priorityHighCount = agendas.count { it.priority.trim().equals("Tinggi", ignoreCase = true) }
    val priorityMediumCount = agendas.count { it.priority.trim().equals("Sedang", ignoreCase = true) }
    val priorityLowCount = agendas.count { it.priority.trim().equals("Rendah", ignoreCase = true) }
    val priorityHighProgress = if (totalCount > 0) priorityHighCount.toFloat() / totalCount.toFloat() else 0f
    val priorityMediumProgress = if (totalCount > 0) priorityMediumCount.toFloat() / totalCount.toFloat() else 0f
    val priorityLowProgress = if (totalCount > 0) priorityLowCount.toFloat() / totalCount.toFloat() else 0f
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(2.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(bottom = 16.dp)
                ) {
                    Icon(
                        Icons.Default.DateRange,
                        contentDescription = null,
                        tint = Orange600,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        "Capaian Agenda",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        "Agenda Selesai",
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextPrimary
                    )
                    Text(
                        "$completedCount/$totalCount",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                LinearProgressIndicator(
                    progress = completedProgress,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                        .clip(RoundedCornerShape(4.dp)),
                    color = ArsiGreen,
                    trackColor = ArsiGray.copy(alpha = 0.3f)
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    "Rincian Prioritas",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold,
                    color = Black,
                    modifier = Modifier.padding(bottom = 12.dp)
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            shape = RoundedCornerShape(4.dp),
                            color = ArsiRed.copy(alpha = 0.1f),
                            modifier = Modifier.size(16.dp)
                        ) {}
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            "Prioritas Tinggi",
                            style = MaterialTheme.typography.bodyMedium,
                            color = TextPrimary
                        )
                    }
                    Text(
                        "$priorityHighCount",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold,
                        color = ArsiRed
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                LinearProgressIndicator(
                    progress = priorityHighProgress,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                        .clip(RoundedCornerShape(4.dp)),
                    color = ArsiRed,
                    trackColor = ArsiGray.copy(alpha = 0.3f)
                )
                Spacer(modifier = Modifier.height(12.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            shape = RoundedCornerShape(4.dp),
                            color = ArsiOrange.copy(alpha = 0.1f),
                            modifier = Modifier.size(16.dp)
                        ) {}
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            "Prioritas Sedang",
                            style = MaterialTheme.typography.bodyMedium,
                            color = TextPrimary
                        )
                    }
                    Text(
                        "$priorityMediumCount",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold,
                        color = ArsiOrange
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                LinearProgressIndicator(
                    progress = priorityMediumProgress,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                        .clip(RoundedCornerShape(4.dp)),
                    color = ArsiOrange,
                    trackColor = ArsiGray.copy(alpha = 0.3f)
                )
                Spacer(modifier = Modifier.height(12.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            shape = RoundedCornerShape(4.dp),
                            color = ArsiBlue.copy(alpha = 0.1f),
                            modifier = Modifier.size(16.dp)
                        ) {}
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            "Prioritas Rendah",
                            style = MaterialTheme.typography.bodyMedium,
                            color = TextPrimary
                        )
                    }
                    Text(
                        "$priorityLowCount",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold,
                        color = ArsiBlue
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                LinearProgressIndicator(
                    progress = priorityLowProgress,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                        .clip(RoundedCornerShape(4.dp)),
                    color = ArsiBlue,
                    trackColor = ArsiGray.copy(alpha = 0.3f)
                )
            }
        }
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(2.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(bottom = 16.dp)
                ) {
                    Icon(
                        Icons.Default.DateRange,
                        contentDescription = null,
                        tint = ArsiRed,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        "Deadline Mendatang",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                }
                val upcomingAgendas = agendas
                    .filter { !isAgendaCompleted(it) }
                    .sortedBy { getDaysUntilDeadline(it) }
                    .take(5)
                if (upcomingAgendas.isEmpty()) {
                    Text(
                        "Tidak ada deadline mendatang",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Gray500,
                        modifier = Modifier.padding(vertical = 16.dp)
                    )
                } else {
                    upcomingAgendas.forEachIndexed { index, agenda ->
                        if (index > 0) {
                            Spacer(modifier = Modifier.height(12.dp))
                        }
                        DeadlineItem(
                            title = agenda.title,
                            date = agenda.date,
                            daysLeft = getDaysUntilDeadline(agenda)
                        )
                    }
                }
            }
        }
    }
}
@Composable
fun AgendaStatistikTabContent(agendas: List<Agenda>, navController: NavController, viewModel: AgendaViewModel) {
    var selectedAgendaForDetail by remember { mutableStateOf<Agenda?>(null) }
    var selectedRiwayatAgenda by remember { mutableStateOf<Agenda?>(null) }
    var showDeleteConfirmDialog by remember { mutableStateOf(false) }
    var agendaToDelete by remember { mutableStateOf<Agenda?>(null) }
    val totalCount = agendas.size
    val completedCount = agendas.count { isAgendaCompleted(it) }
    val completedProgress = if (totalCount > 0) completedCount.toFloat() / totalCount.toFloat() else 0f
    val priorityHighCount = agendas.count { it.priority.trim().equals("Tinggi", ignoreCase = true) }
    val priorityMediumCount = agendas.count { it.priority.trim().equals("Sedang", ignoreCase = true) }
    val priorityLowCount = agendas.count { it.priority.trim().equals("Rendah", ignoreCase = true) }
    val priorityHighProgress = if (totalCount > 0) priorityHighCount.toFloat() / totalCount.toFloat() else 0f
    val priorityMediumProgress = if (totalCount > 0) priorityMediumCount.toFloat() / totalCount.toFloat() else 0f
    val priorityLowProgress = if (totalCount > 0) priorityLowCount.toFloat() / totalCount.toFloat() else 0f
    selectedAgendaForDetail?.let { agenda ->
        AgendaDetailDialog(
            agenda = agenda,
            navController = navController,
            onDismiss = { selectedAgendaForDetail = null },
            onEditClick = {
                selectedAgendaForDetail = null
                navController.navigate("${Screen.AgendaForm.route}?agendaId=${agenda.id}")
            }
        )
    }
    selectedRiwayatAgenda?.let { agenda ->
        RiwayatAgendaDetailDialog(
            agenda = agenda,
            onDismiss = { selectedRiwayatAgenda = null },
            onDeleteClick = {
                agendaToDelete = agenda
                selectedRiwayatAgenda = null
                showDeleteConfirmDialog = true
            }
        )
    }
    if (showDeleteConfirmDialog && agendaToDelete != null) {
        DeleteConfirmDialog(
            agendaTitle = agendaToDelete!!.title,
            onDismiss = {
                showDeleteConfirmDialog = false
                agendaToDelete = null
            },
            onConfirm = {
                viewModel.deleteAgenda(agendaToDelete!!.id)
                showDeleteConfirmDialog = false
                agendaToDelete = null
            }
        )
    }
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(2.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(bottom = 16.dp)
                ) {
                    Icon(
                        Icons.Default.DateRange,
                        contentDescription = null,
                        tint = Orange600,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        "Capaian Agenda",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        "Agenda Selesai",
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextPrimary
                    )
                    Text(
                        "$completedCount/$totalCount",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                LinearProgressIndicator(
                    progress = completedProgress,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                        .clip(RoundedCornerShape(4.dp)),
                    color = ArsiGreen,
                    trackColor = ArsiGray.copy(alpha = 0.3f)
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    "Rincian Prioritas",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold,
                    color = Black,
                    modifier = Modifier.padding(bottom = 12.dp)
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            shape = RoundedCornerShape(4.dp),
                            color = ArsiRed.copy(alpha = 0.1f),
                            modifier = Modifier.size(16.dp)
                        ) {}
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            "Prioritas Tinggi",
                            style = MaterialTheme.typography.bodyMedium,
                            color = TextPrimary
                        )
                    }
                    Text(
                        "$priorityHighCount",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold,
                        color = ArsiRed
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                LinearProgressIndicator(
                    progress = priorityHighProgress,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                        .clip(RoundedCornerShape(4.dp)),
                    color = ArsiRed,
                    trackColor = ArsiGray.copy(alpha = 0.3f)
                )
                Spacer(modifier = Modifier.height(12.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            shape = RoundedCornerShape(4.dp),
                            color = ArsiOrange.copy(alpha = 0.1f),
                            modifier = Modifier.size(16.dp)
                        ) {}
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            "Prioritas Sedang",
                            style = MaterialTheme.typography.bodyMedium,
                            color = TextPrimary
                        )
                    }
                    Text(
                        "$priorityMediumCount",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold,
                        color = ArsiOrange
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                LinearProgressIndicator(
                    progress = priorityMediumProgress,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                        .clip(RoundedCornerShape(4.dp)),
                    color = ArsiOrange,
                    trackColor = ArsiGray.copy(alpha = 0.3f)
                )
                Spacer(modifier = Modifier.height(12.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            shape = RoundedCornerShape(4.dp),
                            color = ArsiBlue.copy(alpha = 0.1f),
                            modifier = Modifier.size(16.dp)
                        ) {}
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            "Prioritas Rendah",
                            style = MaterialTheme.typography.bodyMedium,
                            color = TextPrimary
                        )
                    }
                    Text(
                        "$priorityLowCount",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold,
                        color = ArsiBlue
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                LinearProgressIndicator(
                    progress = priorityLowProgress,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                        .clip(RoundedCornerShape(4.dp)),
                    color = ArsiBlue,
                    trackColor = ArsiGray.copy(alpha = 0.3f)
                )
            }
        }
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(2.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(bottom = 16.dp)
                ) {
                    Icon(
                        Icons.Default.DateRange,
                        contentDescription = null,
                        tint = ArsiRed,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        "Deadline Mendatang",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                }
                val upcomingAgendas = agendas
                    .filter { !isAgendaCompleted(it) }
                    .sortedBy { getDaysUntilDeadline(it) }
                    .take(5)
                if (upcomingAgendas.isEmpty()) {
                    Text(
                        "Tidak ada deadline mendatang",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Gray500,
                        modifier = Modifier.padding(vertical = 16.dp)
                    )
                } else {
                    upcomingAgendas.forEachIndexed { index, agenda ->
                        if (index > 0) {
                            Spacer(modifier = Modifier.height(12.dp))
                        }
                        DeadlineItem(
                            title = agenda.title,
                            date = agenda.date,
                            daysLeft = getDaysUntilDeadline(agenda)
                        )
                    }
                }
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(2.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(bottom = 16.dp)
                ) {
                    Icon(
                        Icons.Default.AccessTime,
                        contentDescription = null,
                        tint = Gray500,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        "Riwayat Agenda",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                }
                val pastAgendas = agendas
                    .filter { isAgendaCompleted(it) }
                    .sortedByDescending {
                        try {
                            val dateFormat = java.text.SimpleDateFormat("d MMM yyyy", java.util.Locale.ENGLISH)
                            dateFormat.parse(it.date.trim())?.time ?: 0L
                        } catch (e: Exception) {
                            0L
                        }
                    }
                    .take(10)
                if (pastAgendas.isEmpty()) {
                    Text(
                        "Belum ada riwayat agenda",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Gray500,
                        modifier = Modifier.padding(vertical = 16.dp)
                    )
                } else {
                    pastAgendas.forEachIndexed { index, agenda ->
                        if (index > 0) {
                            Spacer(modifier = Modifier.height(12.dp))
                            HorizontalDivider(
                                color = ArsiGray.copy(alpha = 0.2f),
                                thickness = 1.dp
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                        }
                        RiwayatAgendaItem(
                            title = agenda.title,
                            date = agenda.date,
                            time = agenda.time,
                            category = agenda.category,
                            priority = agenda.priority,
                            onClick = { selectedRiwayatAgenda = agenda }
                        )
                    }
                }
            }
        }
    }
}
@Composable
fun RiwayatAgendaItem(
    title: String,
    date: String,
    time: String,
    category: String,
    priority: String,
    onClick: () -> Unit = {}
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Surface(
            shape = CircleShape,
            color = ArsiGray.copy(alpha = 0.2f),
            modifier = Modifier.size(40.dp)
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.fillMaxSize()
            ) {
                Icon(
                    Icons.Default.CheckCircle,
                    contentDescription = null,
                    tint = ArsiGreen,
                    modifier = Modifier.size(24.dp)
                )
            }
        }
        Spacer(modifier = Modifier.width(12.dp))
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                title,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold,
                color = Black,
                maxLines = 1,
                overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
            )
            Spacer(modifier = Modifier.height(4.dp))
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    Icons.Default.DateRange,
                    contentDescription = null,
                    tint = Gray500,
                    modifier = Modifier.size(12.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    "$date • $time",
                    style = MaterialTheme.typography.bodySmall,
                    color = Gray600
                )
            }
        }
        Surface(
            shape = RoundedCornerShape(6.dp),
            color = when(priority) {
                "Tinggi" -> Error.copy(alpha = 0.1f)
                "Sedang" -> Orange600.copy(alpha = 0.1f)
                else -> Blue600.copy(alpha = 0.1f)
            }
        ) {
            Text(
                priority,
                color = when(priority) {
                    "Tinggi" -> Error
                    "Sedang" -> Orange600
                    else -> Blue600
                },
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
            )
        }
    }
}
@Composable
fun DeleteConfirmDialog(
    agendaTitle: String,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    Icons.Default.Delete,
                    contentDescription = null,
                    tint = ArsiRed,
                    modifier = Modifier.size(50.dp)
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    "Hapus Agenda?",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = Black
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    "Apakah Anda yakin ingin menghapus agenda \"$agendaTitle\"?",
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.bodyMedium,
                    color = Gray600
                )
                Spacer(modifier = Modifier.height(24.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    Button(
                        onClick = onDismiss,
                        colors = ButtonDefaults.buttonColors(containerColor = ArsiGray.copy(alpha = 0.2f)),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("BATAL", color = ArsiGray)
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    Button(
                        onClick = onConfirm,
                        colors = ButtonDefaults.buttonColors(containerColor = ArsiRed),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("HAPUS", color = Color.White)
                    }
                }
            }
        }
    }
}
@Composable
fun AgendaDetailDialog(
    agenda: Agenda,
    navController: NavController,
    onDismiss: () -> Unit,
    onEditClick: () -> Unit
) {
    val isPastDeadline = isAgendaCompleted(agenda)
    android.util.Log.d("AgendaDetailDialog", "Agenda: ${agenda.title}, filePath: '${agenda.filePath}'")
    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(12.dp)
        ) {
            Column {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            brush = androidx.compose.ui.graphics.Brush.horizontalGradient(
                                colors = listOf(ArsiOrange, ArsiOrange.copy(alpha = 0.85f))
                            )
                        )
                        .padding(20.dp)
                ) {
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                "Detail Agenda",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Medium,
                                color = Color.White.copy(alpha = 0.9f)
                            )
                            Surface(
                                onClick = onDismiss,
                                shape = CircleShape,
                                color = Color.White.copy(alpha = 0.2f),
                                modifier = Modifier.size(32.dp)
                            ) {
                                Box(
                                    contentAlignment = Alignment.Center,
                                    modifier = Modifier.fillMaxSize()
                                ) {
                                    Icon(
                                        Icons.Default.Close,
                                        contentDescription = "Tutup",
                                        tint = Color.White,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            agenda.title,
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            maxLines = 2,
                            overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
                        )
                    }
                }
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState())
                        .padding(20.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Surface(
                            shape = RoundedCornerShape(20.dp),
                            color = when(agenda.priority) {
                                "Tinggi" -> ArsiRed.copy(alpha = 0.12f)
                                "Sedang" -> ArsiOrange.copy(alpha = 0.12f)
                                else -> ArsiBlue.copy(alpha = 0.12f)
                            }
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    Icons.Default.Flag,
                                    contentDescription = null,
                                    tint = when(agenda.priority) {
                                        "Tinggi" -> ArsiRed
                                        "Sedang" -> ArsiOrange
                                        else -> ArsiBlue
                                    },
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    agenda.priority,
                                    color = when(agenda.priority) {
                                        "Tinggi" -> ArsiRed
                                        "Sedang" -> ArsiOrange
                                        else -> ArsiBlue
                                    },
                                    style = MaterialTheme.typography.labelMedium,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }
                        Surface(
                            shape = RoundedCornerShape(20.dp),
                            color = if (isPastDeadline) ArsiGray.copy(alpha = 0.12f) else ArsiGreen.copy(alpha = 0.12f)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    if (isPastDeadline) Icons.Default.CheckCircle else Icons.Default.AccessTime,
                                    contentDescription = null,
                                    tint = if (isPastDeadline) ArsiGray else ArsiGreen,
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    if (isPastDeadline) "Selesai" else "Aktif",
                                    color = if (isPastDeadline) ArsiGray else ArsiGreen,
                                    style = MaterialTheme.typography.labelMedium,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }
                        if (agenda.category.isNotEmpty()) {
                            Surface(
                                shape = RoundedCornerShape(20.dp),
                                color = ArsiOrangeLight.copy(alpha = 0.4f)
                            ) {
                                Text(
                                    agenda.category,
                                    color = ArsiOrange,
                                    style = MaterialTheme.typography.labelMedium,
                                    fontWeight = FontWeight.SemiBold,
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                                )
                            }
                        }
                    }
                    if (!agenda.filePath.isNullOrEmpty()) {
                        Spacer(modifier = Modifier.height(16.dp))
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(bottom = 8.dp)
                        ) {
                            Icon(
                                Icons.Default.DateRange,
                                contentDescription = null,
                                tint = Orange600,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                "Lampiran",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                        }
                        val imageUrl = if (agenda.filePath!!.startsWith("http")) {
                            agenda.filePath
                        } else {
                            val baseUrl = "http://10.0.2.2:5000"
                            val cleanPath = agenda.filePath!!.replace("\\", "/").removePrefix("/")
                            "$baseUrl/$cleanPath"
                        }
                        android.util.Log.d("AgendaDetailDialog", "Image URL: $imageUrl")
                        Surface(
                            shape = RoundedCornerShape(16.dp),
                            color = ArsiGray.copy(alpha = 0.08f),
                            modifier = Modifier
                                .fillMaxWidth()
                                .heightIn(max = 200.dp)
                        ) {
                            AsyncImage(
                                model = imageUrl,
                                contentDescription = "Lampiran Agenda",
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .heightIn(max = 200.dp)
                                    .clip(RoundedCornerShape(16.dp)),
                                contentScale = ContentScale.Fit
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(20.dp))
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = Color(0xFFFFF8F0),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 20.dp, horizontal = 16.dp),
                            horizontalArrangement = Arrangement.SpaceEvenly,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier.weight(1f)
                            ) {
                                Surface(
                                    shape = CircleShape,
                                    color = ArsiOrange.copy(alpha = 0.15f),
                                    modifier = Modifier.size(48.dp)
                                ) {
                                    Box(
                                        contentAlignment = Alignment.Center,
                                        modifier = Modifier.fillMaxSize()
                                    ) {
                                        Icon(
                                            Icons.Default.CalendarToday,
                                            contentDescription = null,
                                            tint = Orange600,
                                            modifier = Modifier.size(24.dp)
                                        )
                                    }
                                }
                                Spacer(modifier = Modifier.height(10.dp))
                                Text(
                                    "Tanggal",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = Gray500,
                                    fontWeight = FontWeight.Medium
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    agenda.date,
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = Black,
                                    textAlign = TextAlign.Center
                                )
                            }
                            Box(
                                modifier = Modifier
                                    .width(1.dp)
                                    .height(70.dp)
                                    .background(ArsiGray.copy(alpha = 0.3f))
                            )
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier.weight(1f)
                            ) {
                                Surface(
                                    shape = CircleShape,
                                    color = ArsiOrange.copy(alpha = 0.15f),
                                    modifier = Modifier.size(48.dp)
                                ) {
                                    Box(
                                        contentAlignment = Alignment.Center,
                                        modifier = Modifier.fillMaxSize()
                                    ) {
                                        Icon(
                                            Icons.Default.AccessTime,
                                            contentDescription = null,
                                            tint = Orange600,
                                            modifier = Modifier.size(24.dp)
                                        )
                                    }
                                }
                                Spacer(modifier = Modifier.height(10.dp))
                                Text(
                                    "Waktu",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = Gray500,
                                    fontWeight = FontWeight.Medium
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    agenda.time.ifEmpty { "-" },
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = TextPrimary
                                )
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(20.dp))
                    if (agenda.description.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(20.dp))
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(bottom = 10.dp)
                        ) {
                            Surface(
                                shape = CircleShape,
                                color = ArsiBlue.copy(alpha = 0.1f),
                                modifier = Modifier.size(28.dp)
                            ) {
                                Box(
                                    contentAlignment = Alignment.Center,
                                    modifier = Modifier.fillMaxSize()
                                ) {
                                    Icon(
                                        Icons.Default.Description,
                                        contentDescription = null,
                                        tint = ArsiBlue,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                "Deskripsi",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                        }
                        Surface(
                            shape = RoundedCornerShape(14.dp),
                            color = ArsiGray.copy(alpha = 0.06f),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                agenda.description,
                                style = MaterialTheme.typography.bodyMedium,
                                color = Gray500,
                                lineHeight = 22.sp,
                                modifier = Modifier.padding(16.dp)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(24.dp))
                    Button(
                        onClick = onEditClick,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(54.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = ArsiOrange),
                        shape = RoundedCornerShape(14.dp),
                        elevation = ButtonDefaults.buttonElevation(
                            defaultElevation = 6.dp,
                            pressedElevation = 10.dp
                        )
                    ) {
                        Icon(
                            Icons.Default.Edit,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(22.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            "Edit Agenda",
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }
            }
        }
    }
}
@Composable
fun RiwayatAgendaDetailDialog(
    agenda: Agenda,
    onDismiss: () -> Unit,
    onDeleteClick: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(12.dp)
        ) {
            Column {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            brush = androidx.compose.ui.graphics.Brush.horizontalGradient(
                                colors = listOf(ArsiOrange, ArsiOrange.copy(alpha = 0.85f))
                            )
                        )
                        .padding(20.dp)
                ) {
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                "Detail Riwayat Agenda",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Medium,
                                color = Color.White.copy(alpha = 0.9f)
                            )
                            Surface(
                                onClick = onDismiss,
                                shape = CircleShape,
                                color = Color.White.copy(alpha = 0.2f),
                                modifier = Modifier.size(32.dp)
                            ) {
                                Box(
                                    contentAlignment = Alignment.Center,
                                    modifier = Modifier.fillMaxSize()
                                ) {
                                    Icon(
                                        Icons.Default.Close,
                                        contentDescription = "Tutup",
                                        tint = Color.White,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            agenda.title,
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            maxLines = 2,
                            overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
                        )
                    }
                }
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState())
                        .padding(20.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Surface(
                            shape = RoundedCornerShape(20.dp),
                            color = when(agenda.priority) {
                                "Tinggi" -> ArsiRed.copy(alpha = 0.12f)
                                "Sedang" -> ArsiOrange.copy(alpha = 0.12f)
                                else -> ArsiBlue.copy(alpha = 0.12f)
                            }
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    Icons.Default.Flag,
                                    contentDescription = null,
                                    tint = when(agenda.priority) {
                                        "Tinggi" -> ArsiRed
                                        "Sedang" -> ArsiOrange
                                        else -> ArsiBlue
                                    },
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    agenda.priority,
                                    color = when(agenda.priority) {
                                        "Tinggi" -> ArsiRed
                                        "Sedang" -> ArsiOrange
                                        else -> ArsiBlue
                                    },
                                    style = MaterialTheme.typography.labelMedium,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }
                        Surface(
                            shape = RoundedCornerShape(20.dp),
                            color = ArsiGreen.copy(alpha = 0.12f)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    Icons.Default.CheckCircle,
                                    contentDescription = null,
                                    tint = Success,
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    "Selesai",
                                    color = ArsiGreen,
                                    style = MaterialTheme.typography.labelMedium,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }
                        if (agenda.category.isNotEmpty()) {
                            Surface(
                                shape = RoundedCornerShape(20.dp),
                                color = ArsiOrangeLight.copy(alpha = 0.4f)
                            ) {
                                Text(
                                    agenda.category,
                                    color = ArsiOrange,
                                    style = MaterialTheme.typography.labelMedium,
                                    fontWeight = FontWeight.SemiBold,
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                                )
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(20.dp))
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = Color(0xFFFFF8F0),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 20.dp, horizontal = 16.dp),
                            horizontalArrangement = Arrangement.SpaceEvenly,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier.weight(1f)
                            ) {
                                Surface(
                                    shape = CircleShape,
                                    color = ArsiOrange.copy(alpha = 0.15f),
                                    modifier = Modifier.size(48.dp)
                                ) {
                                    Box(
                                        contentAlignment = Alignment.Center,
                                        modifier = Modifier.fillMaxSize()
                                    ) {
                                        Icon(
                                            Icons.Default.CalendarToday,
                                            contentDescription = null,
                                            tint = Orange600,
                                            modifier = Modifier.size(24.dp)
                                        )
                                    }
                                }
                                Spacer(modifier = Modifier.height(10.dp))
                                Text(
                                    "Tanggal",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = Gray500,
                                    fontWeight = FontWeight.Medium
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    agenda.date,
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = Black,
                                    textAlign = TextAlign.Center
                                )
                            }
                            Box(
                                modifier = Modifier
                                    .width(1.dp)
                                    .height(70.dp)
                                    .background(ArsiGray.copy(alpha = 0.3f))
                            )
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier.weight(1f)
                            ) {
                                Surface(
                                    shape = CircleShape,
                                    color = ArsiOrange.copy(alpha = 0.15f),
                                    modifier = Modifier.size(48.dp)
                                ) {
                                    Box(
                                        contentAlignment = Alignment.Center,
                                        modifier = Modifier.fillMaxSize()
                                    ) {
                                        Icon(
                                            Icons.Default.AccessTime,
                                            contentDescription = null,
                                            tint = Orange600,
                                            modifier = Modifier.size(24.dp)
                                        )
                                    }
                                }
                                Spacer(modifier = Modifier.height(10.dp))
                                Text(
                                    "Waktu",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = Gray500,
                                    fontWeight = FontWeight.Medium
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    agenda.time.ifEmpty { "-" },
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = TextPrimary
                                )
                            }
                        }
                    }
                    if (agenda.description.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(20.dp))
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(bottom = 10.dp)
                        ) {
                            Surface(
                                shape = CircleShape,
                                color = ArsiBlue.copy(alpha = 0.1f),
                                modifier = Modifier.size(28.dp)
                            ) {
                                Box(
                                    contentAlignment = Alignment.Center,
                                    modifier = Modifier.fillMaxSize()
                                ) {
                                    Icon(
                                        Icons.Default.Description,
                                        contentDescription = null,
                                        tint = ArsiBlue,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                "Deskripsi",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                        }
                        Surface(
                            shape = RoundedCornerShape(14.dp),
                            color = ArsiGray.copy(alpha = 0.06f),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                agenda.description,
                                style = MaterialTheme.typography.bodyMedium,
                                color = Gray500,
                                lineHeight = 22.sp,
                                modifier = Modifier.padding(16.dp)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(24.dp))
                    Button(
                        onClick = onDeleteClick,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(54.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = ArsiRed),
                        shape = RoundedCornerShape(14.dp),
                        elevation = ButtonDefaults.buttonElevation(
                            defaultElevation = 6.dp,
                            pressedElevation = 10.dp
                        )
                    ) {
                        Icon(
                            Icons.Default.Delete,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(22.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            "Hapus Agenda",
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }
            }
        }
    }
}
@Composable
fun DeadlineItem(
    title: String,
    date: String,
    daysLeft: Int
) {
    val badgeColor = when {
        daysLeft <= 3 -> ArsiRed
        daysLeft <= 6 -> ArsiOrange
        else -> Color(0xFFFDD835)
    }
    val badgeBackground = when {
        daysLeft <= 3 -> ArsiRed.copy(alpha = 0.1f)
        daysLeft <= 6 -> ArsiOrange.copy(alpha = 0.1f)
        else -> Color(0xFFFDD835).copy(alpha = 0.1f)
    }
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Top
    ) {
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                title,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold,
                color = ArsiBlack
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                date,
                style = MaterialTheme.typography.bodySmall,
                color = Gray600
            )
        }
        Surface(
            shape = RoundedCornerShape(8.dp),
            color = badgeBackground
        ) {
            Text(
                "$daysLeft hari",
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold,
                color = badgeColor,
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
            )
        }
    }
}
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FilterSortBottomSheet(
    viewModel: AgendaViewModel,
    selectedKategori: String,
    kategoriOptions: List<String>,
    selectedUrutkan: String,
    urutkanOptions: List<String>,
    onDismiss: () -> Unit
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = Color.White,
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
    ) {
        Column(modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp, vertical = 16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Filter", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = ArsiBlack)
                IconButton(onClick = onDismiss) {
                    Icon(Icons.Default.Close, contentDescription = "Tutup", tint = ArsiBlack)
                }
            }
            Spacer(modifier = Modifier.height(24.dp))
            FilterDropdown("Kategori", selectedKategori, kategoriOptions) { viewModel.onKategoriSelected(it) }
            Spacer(modifier = Modifier.height(16.dp))
            FilterDropdown("Prioritas", selectedUrutkan, urutkanOptions) { viewModel.onUrutkanSelected(it) }
            Spacer(modifier = Modifier.height(32.dp))
            Button(
                onClick = { viewModel.onTerapkanFilterClicked() },
                modifier = Modifier.fillMaxWidth().height(50.dp),
                colors = ButtonDefaults.buttonColors(containerColor = ArsiOrange),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Terapkan Filter", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.White)
            }
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FilterDropdown(label: String, selectedOption: String, options: List<String>, onOptionSelected: (String) -> Unit) {
    var isExpanded by remember { mutableStateOf(false) }
    Column {
        Text(label, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold, color = ArsiBlack, modifier = Modifier.padding(bottom = 8.dp))
        ExposedDropdownMenuBox(expanded = isExpanded, onExpandedChange = { isExpanded = !isExpanded }) {
            OutlinedTextField(
                value = selectedOption, onValueChange = {}, readOnly = true,
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = isExpanded) },
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth().menuAnchor(),
                colors = TextFieldDefaults.colors(
                    unfocusedContainerColor = Color.White, focusedContainerColor = Color.White,
                    unfocusedIndicatorColor = ArsiGray, focusedIndicatorColor = ArsiOrange,
                    focusedTextColor = ArsiBlack, unfocusedTextColor = ArsiBlack
                )
            )
            ExposedDropdownMenu(expanded = isExpanded, onDismissRequest = { isExpanded = false }, modifier = Modifier.background(Color.White)) {
                options.forEach { option ->
                    DropdownMenuItem(text = { Text(option, color = ArsiBlack) }, onClick = { onOptionSelected(option); isExpanded = false })
                }
            }
        }
    }
}