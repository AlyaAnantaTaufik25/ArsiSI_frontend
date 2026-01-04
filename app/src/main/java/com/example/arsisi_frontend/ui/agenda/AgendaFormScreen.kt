package com.example.arsisi_frontend.ui.agenda
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.InsertDriveFile
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.FileProvider
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.navigation.NavController
import com.example.arsisi_frontend.R
import com.example.arsisi_frontend.navigation.Screen
import com.example.arsisi_frontend.ui.theme.*
import com.example.arsisi_frontend.utils.Constants
import androidx.compose.material3.TextFieldDefaults
import coil.compose.AsyncImage
import coil.compose.rememberAsyncImagePainter
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.text.input.KeyboardType
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AgendaFormScreen(
    navController: NavController,
    viewModel: AgendaViewModel,
    agendaId: String? = null
) {
    val formState by viewModel.formUiState.collectAsState()
    val showConfirmDialog by viewModel.showConfirmDialog.collectAsState()
    val showSuccessDialog by viewModel.showSuccessDialog.collectAsState()
    val showUpdateConfirmDialog by viewModel.showUpdateConfirmDialog.collectAsState()
    val showUpdateSuccessDialog by viewModel.showUpdateSuccessDialog.collectAsState()
    val isEditMode by viewModel.isEditMode.collectAsState()
    val editingAgendaId by viewModel.editingAgendaId.collectAsState()
    val existingFilePath by viewModel.existingFilePath.collectAsState()
    val selectedReminder by viewModel.selectedReminder.collectAsState()
    val reminderOptions = viewModel.reminderOptions
    LaunchedEffect(agendaId) {
        android.util.Log.d("AgendaFormScreen", "LaunchedEffect agendaId: '$agendaId', editingAgendaId: '$editingAgendaId'")
        if (agendaId != null && editingAgendaId != agendaId) {
            android.util.Log.d("AgendaFormScreen", "Loading agenda for edit: $agendaId")
            viewModel.loadAgendaForEdit(agendaId)
        } else if (agendaId == null && editingAgendaId != null) {
            android.util.Log.d("AgendaFormScreen", "Clearing edit mode")
            viewModel.clearEditMode()
        }
    }
    val kategoriOptions = viewModel.formKategoriOptions
    val prioritasOptions = viewModel.formPrioritasOptions
    var showDatePicker by remember { mutableStateOf(false) }
    var showTimePicker by remember { mutableStateOf(false) }
    var showDateInvalidAlert by remember { mutableStateOf(false) }
    var selectedDateMillis by remember { mutableStateOf<Long?>(null) }
    val initialDateMillis = remember(formState.date) {
        if (formState.date.isNotEmpty()) {
            try {
                val dateFormat = SimpleDateFormat("d MMM", Locale.ENGLISH)
                dateFormat.parse(formState.date)?.time ?: System.currentTimeMillis()
            } catch (e: Exception) {
                System.currentTimeMillis()
            }
        } else {
            System.currentTimeMillis()
        }
    }
    val todayMillis = remember {
        Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }.timeInMillis
    }
    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = initialDateMillis,
        yearRange = IntRange(
            Calendar.getInstance().get(Calendar.YEAR),
            Calendar.getInstance().get(Calendar.YEAR) + 10
        )
    )
    LaunchedEffect(datePickerState.selectedDateMillis) {
        datePickerState.selectedDateMillis?.let {
            selectedDateMillis = it
            android.util.Log.d("AgendaFormScreen", "DatePicker state updated: $it")
        }
    }
    var hourInput by remember { mutableStateOf("") }
    var minuteInput by remember { mutableStateOf("") }
    val context = LocalContext.current
    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let { viewModel.onFileSelected(it) }
    }
    val fileLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let { viewModel.onFileSelected(it) }
    }
    if (showSuccessDialog) {
        SuccessDialog()
    }
    if (showUpdateConfirmDialog) {
        UpdateConfirmDialog(
            onConfirm = { viewModel.confirmUpdateAgenda() },
            onCancel = { viewModel.cancelUpdateAgenda() }
        )
    }
    if (showUpdateSuccessDialog) {
        UpdateSuccessDialog(
            onDismiss = {
                viewModel.dismissUpdateSuccessDialog()
                navController.navigate(Screen.AgendaList.route) {
                    popUpTo(Screen.AgendaList.route) {
                        inclusive = false
                    }
                    launchSingleTop = true
                    restoreState = true
                }
            }
        )
    }
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        if (isEditMode) "Edit Agenda" else "Tambah Agenda Baru",
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = {
                        viewModel.clearEditMode()
                        navController.popBackStack()
                    }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Kembali")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Orange600,
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White
                )
            )
        },
        containerColor = White
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            item {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        FormTextField(
                            label = "Judul Agenda",
                            value = formState.title,
                            onValueChange = viewModel::onTitleChange,
                            placeholder = "masukkan judul agenda"
                        )
                        FormTextField(
                            label = "Deskripsi",
                            value = formState.description,
                            onValueChange = viewModel::onDescriptionChange,
                            placeholder = "Tambahkan deskripsi atau catatan....",
                            singleLine = false,
                            minLines = 3
                        )
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 16.dp),
                            horizontalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            Column(
                                modifier = Modifier
                                    .weight(1f)
                                    .fillMaxHeight()
                            ) {
                                Text(
                                    "Tanggal",
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(bottom = 8.dp)
                                )
                                OutlinedTextField(
                                    value = formState.date.ifEmpty { "" },
                                    onValueChange = {},
                                    readOnly = true,
                                    placeholder = {
                                        Text(
                                            "dd/mm/yy",
                                            color = Gray500,
                                            style = MaterialTheme.typography.bodySmall
                                        )
                                    },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .defaultMinSize(minHeight = 56.dp)
                                        .clickable { showDatePicker = true },
                                    shape = RoundedCornerShape(12.dp),
                                    singleLine = true,
                                    trailingIcon = {
                                        IconButton(onClick = { showDatePicker = true }) {
                                            Icon(
                                                Icons.Default.DateRange,
                                                contentDescription = "Pilih Tanggal",
                                                tint = ArsiBrown
                                            )
                                        }
                                    },
                                    colors = TextFieldDefaults.colors(
                                        focusedContainerColor = Color.White,
                                        unfocusedContainerColor = Color.White,
                                        focusedIndicatorColor = Orange700,
                                        unfocusedIndicatorColor = Gray500.copy(alpha = 0.6f),
                                        focusedTextColor = Color.Black,
                                        unfocusedTextColor = if (formState.date.isNotEmpty()) Color.Black else Gray500,
                                        focusedPlaceholderColor = Gray500,
                                        unfocusedPlaceholderColor = Gray500,
                                        focusedTrailingIconColor = Orange700,
                                        unfocusedTrailingIconColor = Orange700
                                    ),
                                    interactionSource = remember { MutableInteractionSource() }
                                )
                            }
                            Column(
                                modifier = Modifier
                                    .weight(1f)
                                    .fillMaxHeight()
                            ) {
                                Text(
                                    "Waktu",
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(bottom = 8.dp)
                                )
                                OutlinedTextField(
                                    value = formState.time.ifEmpty { "" },
                                    onValueChange = {},
                                    readOnly = true,
                                    placeholder = { Text("--:--", color = Gray500) },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .defaultMinSize(minHeight = 56.dp)
                                        .clickable { showTimePicker = true },
                                    shape = RoundedCornerShape(12.dp),
                                    singleLine = true,
                                    trailingIcon = {
                                        IconButton(onClick = { showTimePicker = true }) {
                                            Icon(
                                                Icons.Default.AccessTime,
                                                contentDescription = "Pilih Waktu",
                                                tint = ArsiBrown
                                            )
                                        }
                                    },
                                    colors = TextFieldDefaults.colors(
                                        focusedContainerColor = Color.White,
                                        unfocusedContainerColor = Color.White,
                                        focusedIndicatorColor = Orange700,
                                        unfocusedIndicatorColor = Gray500.copy(alpha = 0.6f),
                                        focusedTextColor = Color.Black,
                                        unfocusedTextColor = if (formState.time.isNotEmpty()) Color.Black else Gray500,
                                        focusedPlaceholderColor = Gray500,
                                        unfocusedPlaceholderColor = Gray500,
                                        focusedTrailingIconColor = Orange700,
                                        unfocusedTrailingIconColor = Orange700
                                    ),
                                    interactionSource = remember { MutableInteractionSource() }
                                )
                            }
                        }
                        FormDropdown(
                            label = "Kategori",
                            selectedOption = formState.category,
                            options = kategoriOptions,
                            onOptionSelected = { viewModel.onFormKategoriSelected(it) },
                            placeholder = "Pilih kategori..."
                        )
                        FormDropdown(
                            label = "Prioritas",
                            selectedOption = formState.priority,
                            options = prioritasOptions,
                            onOptionSelected = { viewModel.onFormPrioritasSelected(it) },
                            placeholder = "Pilih prioritas..."
                        )
                        Text(
                            "Reminder",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(top = 16.dp, bottom = 8.dp)
                        )
                        val reminderPairs = reminderOptions.chunked(2)
                        Column(Modifier.fillMaxWidth()) {
                            reminderPairs.forEach { pair ->
                                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Start) {
                                    pair.forEach { option ->
                                        Row(
                                            Modifier
                                                .weight(1f)
                                                .selectable(
                                                    selected = (selectedReminder == option),
                                                    onClick = { viewModel.onReminderSelected(option) },
                                                    role = Role.RadioButton
                                                )
                                                .padding(vertical = 4.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            RadioButton(
                                                selected = (selectedReminder == option),
                                                onClick = { viewModel.onReminderSelected(option) },
                                                colors = RadioButtonDefaults.colors(selectedColor = Orange700)
                                            )
                                            Text(
                                                text = option,
                                                style = MaterialTheme.typography.bodyMedium,
                                                modifier = Modifier.padding(start = 4.dp)
                                            )
                                        }
                                    }
                                    if (pair.size < 2) { Spacer(Modifier.weight(1f)) }
                                }
                            }
                        }
                        Text(
                            "Lampiran",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(top = 16.dp, bottom = 8.dp)
                        )
                        val fileToShowUri = formState.attachedFileUri ?: (existingFilePath?.takeIf { it.isNotBlank() }?.let { filePath ->
                            val normalizedPath = filePath.replace("\\", "/")
                            val urlString = if (normalizedPath.startsWith("http://") || normalizedPath.startsWith("https://")) {
                                normalizedPath
                            } else {
                                val cleanPath = if (normalizedPath.startsWith("/")) normalizedPath else "/$normalizedPath"
                                val finalUrl = "${Constants.BASE_URL}$cleanPath"
                                android.util.Log.d("AgendaFormScreen", "Creating URL for file: $finalUrl")
                                finalUrl
                            }
                            Uri.parse(urlString)
                        })
                        if (fileToShowUri != null && existingFilePath != null) {
                            android.util.Log.d("AgendaFormScreen", "File to show: ${fileToShowUri.toString()}")
                            android.util.Log.d("AgendaFormScreen", "Existing file path: $existingFilePath")
                        }
                        if (fileToShowUri != null) {
                            val imageUrl = if (fileToShowUri.scheme == "http" || fileToShowUri.scheme == "https") {
                                fileToShowUri.toString()
                            } else if (fileToShowUri.scheme == "content" || fileToShowUri.scheme == "file") {
                                fileToShowUri.toString()
                            } else {
                                fileToShowUri.toString()
                            }
                            val isImage = remember(fileToShowUri) {
                                if (fileToShowUri.scheme == "http" || fileToShowUri.scheme == "https") {
                                    val path = fileToShowUri.toString().lowercase()
                                    path.endsWith(".jpg") || path.endsWith(".jpeg") ||
                                    path.endsWith(".png") || path.endsWith(".gif") ||
                                    path.endsWith(".webp") || path.contains("image")
                                } else {
                                    isImageFile(context, fileToShowUri)
                                }
                            }
                            val fileName = if (fileToShowUri.scheme == "http" || fileToShowUri.scheme == "https") {
                                fileToShowUri.lastPathSegment ?: fileToShowUri.path?.substringAfterLast("/") ?: "File"
                            } else {
                                getFileName(context, fileToShowUri) ?: "File terpilih"
                            }
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(bottom = 8.dp),
                                colors = CardDefaults.cardColors(containerColor = Orange100.copy(alpha = 0.5f))
                            ) {
                                Column(
                                    modifier = Modifier.padding(12.dp)
                                ) {
                                    if (isImage) {
                                        Box(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .height(200.dp)
                                                .clip(RoundedCornerShape(8.dp))
                                                .background(Gray500.copy(alpha = 0.1f)),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            AsyncImage(
                                                model = imageUrl,
                                                contentDescription = "Preview gambar",
                                                modifier = Modifier
                                                    .fillMaxSize()
                                                    .clip(RoundedCornerShape(8.dp))
                                            )
                                        }
                                    }
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(
                                            if (isImage) Icons.Default.Image else Icons.Default.InsertDriveFile,
                                            contentDescription = "File",
                                            tint = Orange700,
                                            modifier = Modifier.size(24.dp)
                                        )
                                        Spacer(modifier = Modifier.width(12.dp))
                                        Text(
                                            text = fileName,
                                            style = MaterialTheme.typography.bodyMedium,
                                            modifier = Modifier.weight(1f),
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                        IconButton(
                                            onClick = {
                                                viewModel.onRemoveFile()
                                                viewModel.clearExistingFilePath()
                                            },
                                            modifier = Modifier.size(32.dp)
                                        ) {
                                            Icon(
                                                Icons.Default.Close,
                                                contentDescription = "Hapus/Ganti File",
                                                tint = Error,
                                                modifier = Modifier.size(20.dp)
                                            )
                                        }
                                    }
                                }
                            }
                        }
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            LampiranButton(
                                text = "Galeri",
                                icon = Icons.Default.Image,
                                onClick = { galleryLauncher.launch("image*") },
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(24.dp))
                Button(
                    onClick = { viewModel.onSubmitClicked() },
                    modifier = Modifier.fillMaxWidth().height(50.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Orange700),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        if (isEditMode) "Simpan Perubahan" else "Tambahkan",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
        val shouldNavigateBack by viewModel.shouldNavigateBack.collectAsState()
        LaunchedEffect(shouldNavigateBack) {
            if (shouldNavigateBack && !isEditMode) {
                viewModel.clearNavigateBackFlag()
                navController.navigate(Screen.AgendaList.route) {
                    popUpTo(Screen.AgendaList.route) {
                        inclusive = false
                    }
                    launchSingleTop = true
                }
            }
        }
        if (showDatePicker) {
            LaunchedEffect(showDatePicker) {
                if (showDatePicker) {
                    selectedDateMillis = datePickerState.selectedDateMillis
                }
            }
            DatePickerDialog(
                onDismissRequest = {
                    showDatePicker = false
                    selectedDateMillis = null
                },
                confirmButton = {
                    TextButton(
                        onClick = {
                            val millisToUse = selectedDateMillis ?: datePickerState.selectedDateMillis
                            if (millisToUse != null) {
                                val selectedCalendar = Calendar.getInstance().apply {
                                    timeInMillis = millisToUse
                                    set(Calendar.HOUR_OF_DAY, 0)
                                    set(Calendar.MINUTE, 0)
                                    set(Calendar.SECOND, 0)
                                    set(Calendar.MILLISECOND, 0)
                                }
                                val todayCalendar = Calendar.getInstance().apply {
                                    set(Calendar.HOUR_OF_DAY, 0)
                                    set(Calendar.MINUTE, 0)
                                    set(Calendar.SECOND, 0)
                                    set(Calendar.MILLISECOND, 0)
                                }
                                val isDateValid = when {
                                    selectedCalendar.get(Calendar.YEAR) > todayCalendar.get(Calendar.YEAR) -> true
                                    selectedCalendar.get(Calendar.YEAR) < todayCalendar.get(Calendar.YEAR) -> false
                                    selectedCalendar.get(Calendar.MONTH) > todayCalendar.get(Calendar.MONTH) -> true
                                    selectedCalendar.get(Calendar.MONTH) < todayCalendar.get(Calendar.MONTH) -> false
                                    else -> selectedCalendar.get(Calendar.DAY_OF_MONTH) >= todayCalendar.get(Calendar.DAY_OF_MONTH)
                                }
                                if (isDateValid) {
                                    val utcCalendar = Calendar.getInstance(TimeZone.getTimeZone("UTC")).apply {
                                        timeInMillis = millisToUse
                                    }
                                    val day = utcCalendar.get(Calendar.DAY_OF_MONTH)
                                    val month = utcCalendar.get(Calendar.MONTH)
                                    val year = utcCalendar.get(Calendar.YEAR)
                                    val localCalendar = Calendar.getInstance().apply {
                                        set(Calendar.YEAR, year)
                                        set(Calendar.MONTH, month)
                                        set(Calendar.DAY_OF_MONTH, day)
                                        set(Calendar.HOUR_OF_DAY, 12)
                                        set(Calendar.MINUTE, 0)
                                        set(Calendar.SECOND, 0)
                                        set(Calendar.MILLISECOND, 0)
                                    }
                                    val dateFormat = SimpleDateFormat("d MMM yyyy", Locale.ENGLISH)
                                    val dateString = dateFormat.format(localCalendar.time)
                                    android.util.Log.d("AgendaFormScreen", "Tanggal dipilih: $dateString")
                                    android.util.Log.d("AgendaFormScreen", "Millis: $millisToUse")
                                    android.util.Log.d("AgendaFormScreen", "Day: $day, Month: ${month + 1}, Year: $year")
                                    viewModel.onDateChange(dateString)
                                    showDatePicker = false
                                    selectedDateMillis = null
                                } else {
                                    android.util.Log.w("AgendaFormScreen", "Tanggal yang dipilih sebelum hari ini, tidak valid")
                                    showDatePicker = false
                                    selectedDateMillis = null
                                    showDateInvalidAlert = true
                                }
                            } else {
                                android.util.Log.e("AgendaFormScreen", "Error: selectedDateMillis is null")
                                showDatePicker = false
                                selectedDateMillis = null
                            }
                        }
                    ) {
                        Text("Pilih")
                    }
                },
                dismissButton = {
                    TextButton(onClick = {
                        showDatePicker = false
                        selectedDateMillis = null
                    }) {
                        Text("Batal")
                    }
                }
            ) {
                DatePicker(state = datePickerState)
            }
        }
        if (showDateInvalidAlert) {
            AlertDialog(
                onDismissRequest = { showDateInvalidAlert = false },
                title = {
                    Text(
                        text = "Tanggal Tidak Valid",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = Black
                    )
                },
                text = {
                    Text(
                        text = "Tanggal tidak bisa dipilih. Karena tanggal tersebut sudah kadarluwarsa.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Black
                    )
                },
                confirmButton = {
                    TextButton(
                        onClick = { showDateInvalidAlert = false },
                        colors = ButtonDefaults.textButtonColors(
                            contentColor = Orange600
                        )
                    ) {
                        Text("OK", fontWeight = FontWeight.Bold)
                    }
                },
                containerColor = Color.White,
                shape = RoundedCornerShape(16.dp)
            )
        }
        if (showTimePicker) {
            LaunchedEffect(key1 = showTimePicker) {
                if (showTimePicker) {
                    hourInput = ""
                    minuteInput = ""
                    if (formState.time.isNotEmpty()) {
                        try {
                            val parts = formState.time.split(":")
                            if (parts.size == 2) {
                                val h = parts[0].trim().toIntOrNull()?.coerceIn(0, 23) ?: 0
                                val m = parts[1].trim().toIntOrNull()?.coerceIn(0, 59) ?: 0
                                hourInput = String.format("%02d", h)
                                minuteInput = String.format("%02d", m)
                            }
                        } catch (e: Exception) {
                            android.util.Log.e("AgendaFormScreen", "Error parsing time: ${e.message}", e)
                        }
                    }
                }
            }
            Dialog(onDismissRequest = {
                showTimePicker = false
            }) {
                Card(
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(32.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Pilih Waktu",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(bottom = 24.dp)
                        )
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .width(80.dp)
                                    .height(100.dp)
                                    .clip(RoundedCornerShape(16.dp))
                                    .background(Gray500.copy(alpha = 0.1f))
                                    .padding(8.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                TextField(
                                    value = hourInput,
                                    onValueChange = { newValue ->
                                        when {
                                            newValue.isEmpty() -> hourInput = newValue
                                            newValue.all { it.isDigit() } -> {
                                                val hourValue = newValue.toIntOrNull()
                                                if (hourValue != null && hourValue in 0..23) {
                                            hourInput = newValue
                                                }
                                            }
                                        }
                                    },
                                    modifier = Modifier.fillMaxWidth(),
                                    textStyle = MaterialTheme.typography.headlineLarge.copy(
                                        fontWeight = FontWeight.Bold,
                                        textAlign = TextAlign.Center
                                    ),
                                    colors = TextFieldDefaults.colors(
                                        focusedContainerColor = Color.Transparent,
                                        unfocusedContainerColor = Color.Transparent,
                                        focusedIndicatorColor = Color.Transparent,
                                        unfocusedIndicatorColor = Color.Transparent,
                                        focusedTextColor = Color.Black,
                                        unfocusedTextColor = Color.Black
                                    ),
                                    singleLine = true,
                                    keyboardOptions = KeyboardOptions(
                                        keyboardType = KeyboardType.Number
                                    )
                                )
                            }
                            Text(
                                text = ":",
                                style = MaterialTheme.typography.headlineLarge,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 8.dp)
                            )
                            Box(
                                modifier = Modifier
                                    .width(80.dp)
                                    .height(100.dp)
                                    .clip(RoundedCornerShape(16.dp))
                                    .background(Orange700.copy(alpha = 0.1f))
                                    .padding(8.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                TextField(
                                    value = minuteInput,
                                    onValueChange = { newValue ->
                                        when {
                                            newValue.isEmpty() -> minuteInput = newValue
                                            newValue.all { it.isDigit() } -> {
                                                val minuteValue = newValue.toIntOrNull()
                                                if (minuteValue != null && minuteValue in 0..59) {
                                            minuteInput = newValue
                                                }
                                            }
                                        }
                                    },
                                    modifier = Modifier.fillMaxWidth(),
                                    textStyle = MaterialTheme.typography.headlineLarge.copy(
                                        fontWeight = FontWeight.Bold,
                                        textAlign = TextAlign.Center
                                    ),
                                    colors = TextFieldDefaults.colors(
                                        focusedContainerColor = Color.Transparent,
                                        unfocusedContainerColor = Color.Transparent,
                                        focusedIndicatorColor = Color.Transparent,
                                        unfocusedIndicatorColor = Color.Transparent,
                                        focusedTextColor = Orange700,
                                        unfocusedTextColor = Orange700
                                    ),
                                    singleLine = true,
                                    keyboardOptions = KeyboardOptions(
                                        keyboardType = KeyboardType.Number
                                    )
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(32.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.End
                        ) {
                            TextButton(
                                onClick = { showTimePicker = false },
                                colors = ButtonDefaults.textButtonColors(
                                    contentColor = Gray500
                                )
                            ) {
                                Text("Batal", style = MaterialTheme.typography.bodyLarge)
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Button(
                                onClick = {
                                    try {
                                        val h = hourInput.trim().toIntOrNull()?.coerceIn(0, 23) ?: 0
                                        val m = minuteInput.trim().toIntOrNull()?.coerceIn(0, 59) ?: 0
                                        val timeString = String.format("%02d:%02d", h, m)
                                        viewModel.onTimeChange(timeString)
                                        showTimePicker = false
                                    } catch (e: Exception) {
                                        android.util.Log.e("AgendaFormScreen", "Error saving time: ${e.message}", e)
                                    }
                                },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Orange700
                                ),
                                shape = RoundedCornerShape(12.dp),
                                enabled = hourInput.isNotEmpty() && minuteInput.isNotEmpty()
                            ) {
                                Text("Pilih", style = MaterialTheme.typography.bodyLarge)
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
fun FormDropdown(
    label: String,
    selectedOption: String,
    options: List<String>,
    onOptionSelected: (String) -> Unit,
    placeholder: String
) {
    var isExpanded by remember { mutableStateOf(false) }
    Column(modifier = Modifier.padding(bottom = 16.dp)) {
        Text(
            label,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        ExposedDropdownMenuBox(
            expanded = isExpanded,
            onExpandedChange = { isExpanded = !isExpanded }
        ) {
            OutlinedTextField(
                value = if (selectedOption.isEmpty()) placeholder else selectedOption,
                onValueChange = {},
                readOnly = true,
                placeholder = { Text(placeholder, color = Gray500) },
                trailingIcon = {
                    ExposedDropdownMenuDefaults.TrailingIcon(expanded = isExpanded)
                },
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .menuAnchor()
            )
            ExposedDropdownMenu(
                expanded = isExpanded,
                onDismissRequest = { isExpanded = false },
                modifier = Modifier.background(Color.White)
            ) {
                options.forEach { option ->
                    DropdownMenuItem(
                        text = { Text(option) },
                        onClick = {
                            onOptionSelected(option)
                            isExpanded = false
                        }
                    )
                }
            }
        }
    }
}
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FormTextField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    modifier: Modifier = Modifier,
    singleLine: Boolean = true,
    minLines: Int = 1
) {
    Column(modifier = modifier.padding(bottom = 16.dp)) {
        Text(
            label,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            placeholder = { Text(placeholder, color = ArsiGray) },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            singleLine = singleLine,
            minLines = minLines
        )
    }
}
@Composable
fun LampiranButton(
    text: String,
    icon: ImageVector,
    onClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .border(1.dp, Gray500.copy(alpha = 0.4f), RoundedCornerShape(12.dp))
            .clip(RoundedCornerShape(12.dp))
            .background(Orange100.copy(alpha = 0.5f))
            .clickable(onClick = onClick)
            .padding(horizontal = 24.dp, vertical = 16.dp)
    ) {
        Icon(icon, contentDescription = text, tint = Orange700)
        Spacer(modifier = Modifier.height(8.dp))
        Text(text, style = MaterialTheme.typography.bodyMedium)
    }
}
@Composable
fun getFileName(context: android.content.Context, uri: Uri?): String? {
    if (uri == null) return null
    var result: String? = null
    if (uri.scheme == "content") {
        val cursor = context.contentResolver.query(uri, null, null, null, null)
        cursor?.use {
            if (it.moveToFirst()) {
                val nameIndex = it.getColumnIndex(android.provider.OpenableColumns.DISPLAY_NAME)
                if (nameIndex >= 0) {
                    result = it.getString(nameIndex)
                }
            }
        }
    }
    if (result == null) {
        result = uri.path
        val cut = result?.lastIndexOf('/')
        if (cut != -1) {
            result = result?.substring(cut!! + 1)
        }
    }
    return result
}
fun isImageFile(context: android.content.Context, uri: Uri?): Boolean {
    if (uri == null) return false
    val mimeType = context.contentResolver.getType(uri)
    return mimeType?.startsWith("image/") == true
}
@Composable
fun ConfirmationDialog(
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
                    Icons.Default.Info,
                    contentDescription = null,
                    tint = Orange600,
                    modifier = Modifier.size(50.dp)
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    "Apakah Anda yakin ingin menambahkan agenda ini?",
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(24.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    Button(
                        onClick = onDismiss,
                        colors = ButtonDefaults.buttonColors(containerColor = Gray500.copy(alpha = 0.2f)),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("BATAL", color = Gray500)
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    Button(
                        onClick = onConfirm,
                        colors = ButtonDefaults.buttonColors(containerColor = Error),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("YAKIN", color = Color.White)
                    }
                }
            }
        }
    }
}
@Composable
fun SuccessDialog() {
    Dialog(onDismissRequest = {}) {
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            Column(
                modifier = Modifier.padding(32.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    Icons.Default.CheckCircle,
                    contentDescription = null,
                    tint = Color(0xFF4CAF50),
                    modifier = Modifier.size(60.dp)
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    "Agenda Berhasil ditambahkan",
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}
@Composable
fun UpdateConfirmDialog(
    onConfirm: () -> Unit,
    onCancel: () -> Unit
) {
    Dialog(onDismissRequest = onCancel) {
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .size(60.dp)
                        .background(Orange700, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        "?",
                        style = MaterialTheme.typography.headlineLarge,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    "Apakah Anda yakin ingin melakukan perubahan pada agenda ini?",
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.bodyMedium,
                    color = Black
                )
                Spacer(modifier = Modifier.height(24.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    Button(
                        onClick = onCancel,
                        colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                        modifier = Modifier
                            .weight(1f)
                            .border(1.dp, Gray500, RoundedCornerShape(8.dp))
                    ) {
                        Text("BATAL", color = Gray500)
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    Button(
                        onClick = onConfirm,
                        colors = ButtonDefaults.buttonColors(containerColor = Error.copy(alpha = 0.8f)),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("YAKIN", color = Color.White)
                    }
                }
            }
        }
    }
}
@Composable
fun UpdateSuccessDialog(
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            Column(
                modifier = Modifier.padding(32.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .size(60.dp)
                        .background(Orange600, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Default.CheckCircle,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(40.dp)
                    )
                }
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    "Agenda Berhasil di Edit",
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold,
                    color = Black
                )
            }
        }
    }
}