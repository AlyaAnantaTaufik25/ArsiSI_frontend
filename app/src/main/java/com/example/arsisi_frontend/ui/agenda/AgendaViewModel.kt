
package com.example.arsisi_frontend.ui.agenda
import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.arsisi_frontend.data.repository.Agenda
import com.example.arsisi_frontend.data.repository.AgendaRepository
import com.example.arsisi_frontend.utils.ReminderScheduler
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
data class AgendaFormUiState(
    val title: String = "",
    val description: String = "",
    val date: String = "",
    val time: String = "",
    val category: String = "",
    val priority: String = "",
    val attachedFileUri: Uri? = null
)
class AgendaViewModel(
    private val repository: AgendaRepository,
    private val reminderScheduler: ReminderScheduler
) : ViewModel() {
    private var _masterAgendaList = listOf<Agenda>()
    private val _allAgendas = MutableStateFlow<List<Agenda>>(emptyList())
    val allAgendas: StateFlow<List<Agenda>> = _allAgendas.asStateFlow()
    private val _agendas = MutableStateFlow<List<Agenda>>(emptyList())
    val agendas: StateFlow<List<Agenda>> = _agendas.asStateFlow()
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()
    private val _showFilterSheet = MutableStateFlow(false)
    val showFilterSheet: StateFlow<Boolean> = _showFilterSheet.asStateFlow()
    val kategoriOptions = listOf("Semua Kategori", "Tugas", "Presentasi", "Meeting", "Ujian")
    val urutkanOptions = listOf("Semua Prioritas", "Prioritas Tinggi", "Prioritas Sedang", "Prioritas Rendah")
    private val _selectedKategori = MutableStateFlow(kategoriOptions[0])
    val selectedKategori: StateFlow<String> = _selectedKategori.asStateFlow()
    private val _selectedUrutkan = MutableStateFlow(urutkanOptions[0])
    val selectedUrutkan: StateFlow<String> = _selectedUrutkan.asStateFlow()
    private val _formUiState = MutableStateFlow(AgendaFormUiState())
    val formUiState: StateFlow<AgendaFormUiState> = _formUiState.asStateFlow()
    private val _showConfirmDialog = MutableStateFlow(false)
    val showConfirmDialog: StateFlow<Boolean> = _showConfirmDialog.asStateFlow()
    private val _showSuccessDialog = MutableStateFlow(false)
    val showSuccessDialog: StateFlow<Boolean> = _showSuccessDialog.asStateFlow()
    private val _showDetailDialog = MutableStateFlow(false)
    val showDetailDialog: StateFlow<Boolean> = _showDetailDialog.asStateFlow()
    private val _selectedAgendaForDetail = MutableStateFlow<Agenda?>(null)
    val selectedAgendaForDetail: StateFlow<Agenda?> = _selectedAgendaForDetail.asStateFlow()
    private val _editingAgendaId = MutableStateFlow<String?>(null)
    val editingAgendaId: StateFlow<String?> = _editingAgendaId.asStateFlow()
    private val _existingFilePath = MutableStateFlow<String?>(null)
    val existingFilePath: StateFlow<String?> = _existingFilePath.asStateFlow()
    private val _showUpdateConfirmDialog = MutableStateFlow(false)
    val showUpdateConfirmDialog: StateFlow<Boolean> = _showUpdateConfirmDialog.asStateFlow()
    private val _showUpdateSuccessDialog = MutableStateFlow(false)
    val showUpdateSuccessDialog: StateFlow<Boolean> = _showUpdateSuccessDialog.asStateFlow()
    val formKategoriOptions = listOf("Tugas", "Presentasi", "Meeting", "Ujian")
    val formPrioritasOptions = listOf("Tinggi", "Sedang", "Rendah")
    val reminderOptions = listOf("5 menit sebelum", "30 menit sebelum", "1 hari sebelum")
    private val _selectedReminder = MutableStateFlow<String?>(null)
    val selectedReminder: StateFlow<String?> = _selectedReminder.asStateFlow()
    init {
        loadAgendas()
    }
    fun loadAgendas() {
        viewModelScope.launch {
            repository.getAgendas().collect { agendaList ->
                _masterAgendaList = agendaList
                _allAgendas.value = agendaList
                applyFiltersAndSort()
            }
        }
    }
    fun onSearchQueryChanged(query: String) {
        _searchQuery.value = query
        applyFiltersAndSort()
    }
    private fun applyFiltersAndSort() {
        var filteredList = _masterAgendaList
        val query = _searchQuery.value.trim()
        if (query.isNotEmpty()) {
            filteredList = filteredList.filter {
                it.title.contains(query, ignoreCase = true) ||
                        it.description.contains(query, ignoreCase = true)
            }
        }
        val kategori = _selectedKategori.value
        if (kategori != "Semua Kategori") {
            filteredList = filteredList.filter {
                it.category.trim().equals(kategori, ignoreCase = true)
            }
        }
        filteredList = when (_selectedUrutkan.value) {
            "Semua Prioritas" -> {
                filteredList
            }
            "Prioritas Tinggi" -> {
                filteredList.filter { it.priority.trim().equals("Tinggi", ignoreCase = true) }
            }
            "Prioritas Sedang" -> {
                filteredList.filter { it.priority.trim().equals("Sedang", ignoreCase = true) }
            }
            "Prioritas Rendah" -> {
                filteredList.filter { it.priority.trim().equals("Rendah", ignoreCase = true) }
            }
            else -> filteredList
        }
        _agendas.value = filteredList
    }
    private fun getDateScore(dateStr: String): Long {
        try {
            val parts = dateStr.trim().split(" ")
            if (parts.size >= 2) {
                val day = parts[0].toIntOrNull() ?: 31
                val monthStr = parts[1]
                val monthScore = getMonthNumber(monthStr)
                return (monthScore * 100L) + day
            }
            return Long.MAX_VALUE
        } catch (e: Exception) {
            return Long.MAX_VALUE
        }
    }
    private fun getMonthNumber(month: String): Int {
        return when (month.lowercase().take(3)) {
            "jan" -> 1
            "feb" -> 2
            "mar" -> 3
            "apr" -> 4
            "mei", "may" -> 5
            "jun" -> 6
            "jul" -> 7
            "agu", "agt", "aug" -> 8
            "sep" -> 9
            "okt", "oct" -> 10
            "nov" -> 11
            "des", "dec" -> 12
            else -> 13
        }
    }
    fun onTitleChange(v: String) { _formUiState.update { it.copy(title = v) } }
    fun onDescriptionChange(v: String) { _formUiState.update { it.copy(description = v) } }
    fun onDateChange(v: String) { _formUiState.update { it.copy(date = v) } }
    fun onTimeChange(v: String) { _formUiState.update { it.copy(time = v) } }
    fun onFormKategoriSelected(v: String) { _formUiState.update { it.copy(category = v) } }
    fun onFormPrioritasSelected(v: String) {
        android.util.Log.d("AgendaViewModel", "Prioritas dipilih: '$v'")
        _formUiState.update { it.copy(priority = v) }
        android.util.Log.d("AgendaViewModel", "Prioritas tersimpan di formState: '${_formUiState.value.priority}'")
    }
    fun onReminderSelected(v: String) { _selectedReminder.value = if(_selectedReminder.value == v) null else v }
    fun onFileSelected(uri: Uri?) {
        _formUiState.update { it.copy(attachedFileUri = uri) }
    }
    fun onRemoveFile() {
        _formUiState.update { it.copy(attachedFileUri = null) }
    }
    fun onSubmitClicked() {
        val editingId = _editingAgendaId.value
        if (editingId != null) {
            _showUpdateConfirmDialog.value = true
        } else {
            viewModelScope.launch {
                try {
                    val s = _formUiState.value
                    android.util.Log.d("AgendaViewModel", "Submitting agenda:")
                    android.util.Log.d("AgendaViewModel", "  - Title: ${s.title}")
                    android.util.Log.d("AgendaViewModel", "  - Category: ${s.category}")
                    android.util.Log.d("AgendaViewModel", "  - Priority: ${s.priority}")
                    android.util.Log.d("AgendaViewModel", "  - Date: ${s.date}")
                    android.util.Log.d("AgendaViewModel", "  - Time: ${s.time}")
                    android.util.Log.d("AgendaViewModel", "  - Reminder: ${_selectedReminder.value}")
                    repository.addAgenda(
                        title = s.title,
                        description = s.description,
                        date = s.date,
                        time = s.time,
                        category = s.category,
                        priority = s.priority,
                        reminderSetting = _selectedReminder.value,
                        fileUri = s.attachedFileUri
                    )
                    loadAgendas()
                    val reminderSetting = _selectedReminder.value
                    if (reminderSetting != null) {
                        delay(500)
                        val newAgenda = _masterAgendaList.find {
                            it.title == s.title && it.date == s.date
                        }
                        if (newAgenda != null) {
                            val agendaIdInt = newAgenda.id.toIntOrNull()
                            if (agendaIdInt != null) {
                                reminderScheduler.scheduleReminder(
                                    agendaId = agendaIdInt,
                                    title = newAgenda.title,
                                    description = newAgenda.description,
                                    date = newAgenda.date,
                                    time = newAgenda.time,
                                    reminderSetting = reminderSetting
                                )
                                android.util.Log.d("AgendaViewModel", "Reminder scheduled for new agenda: ${newAgenda.id}")
                            }
                        }
                    }
                    _formUiState.value = AgendaFormUiState()
                    _selectedReminder.value = null
                    _shouldNavigateBack.value = true
                } catch (e: Exception) {
                    android.util.Log.e("AgendaViewModel", "Error adding agenda: ${e.message}", e)
                }
            }
        }
    }
    fun confirmUpdateAgenda() {
        viewModelScope.launch {
            try {
                val editingId = _editingAgendaId.value
                if (editingId == null) {
                    android.util.Log.e("AgendaViewModel", "No agenda ID for update")
                    return@launch
                }
                val s = _formUiState.value
                android.util.Log.d("AgendaViewModel", "Updating agenda:")
                android.util.Log.d("AgendaViewModel", "  - ID: $editingId")
                android.util.Log.d("AgendaViewModel", "  - Title: ${s.title}")
                android.util.Log.d("AgendaViewModel", "  - Category: ${s.category}")
                android.util.Log.d("AgendaViewModel", "  - Priority: ${s.priority}")
                android.util.Log.d("AgendaViewModel", "  - Date: ${s.date}")
                android.util.Log.d("AgendaViewModel", "  - Time: ${s.time}")
                android.util.Log.d("AgendaViewModel", "  - Reminder: ${_selectedReminder.value}")
                repository.updateAgenda(
                    agendaId = editingId,
                    title = s.title,
                    description = s.description,
                    date = s.date,
                    time = s.time,
                    category = s.category,
                    priority = s.priority,
                    reminderSetting = _selectedReminder.value,
                    fileUri = s.attachedFileUri
                )
                val agendaIdInt = editingId.toIntOrNull()
                if (agendaIdInt != null) {
                    reminderScheduler.cancelReminder(agendaIdInt)
                    val reminderSetting = _selectedReminder.value
                    if (reminderSetting != null) {
                        reminderScheduler.scheduleReminder(
                            agendaId = agendaIdInt,
                            title = s.title,
                            description = s.description,
                            date = s.date,
                            time = s.time,
                            reminderSetting = reminderSetting
                        )
                        android.util.Log.d("AgendaViewModel", "Reminder rescheduled for agenda: $editingId")
                    }
                }
                loadAgendas()
                _showUpdateConfirmDialog.value = false
                _showUpdateSuccessDialog.value = true
                _formUiState.value = AgendaFormUiState()
                _selectedReminder.value = null
                _editingAgendaId.value = null
            } catch (e: Exception) {
                android.util.Log.e("AgendaViewModel", "Error updating agenda: ${e.message}", e)
                _showUpdateConfirmDialog.value = false
            }
        }
    }
    fun cancelUpdateAgenda() {
        _showUpdateConfirmDialog.value = false
    }
    fun dismissUpdateSuccessDialog() {
        _showUpdateSuccessDialog.value = false
        _shouldNavigateBack.value = true
    }
    fun loadAgendaForEdit(agendaId: String) {
        viewModelScope.launch {
            _editingAgendaId.value = agendaId
            if (_masterAgendaList.isEmpty()) {
                kotlinx.coroutines.delay(100)
            }
            val agenda = _masterAgendaList.find { it.id == agendaId }
            if (agenda != null) {
                _existingFilePath.value = agenda.filePath
                android.util.Log.d("AgendaViewModel", "Loading agenda for edit:")
                android.util.Log.d("AgendaViewModel", "  - Title: ${agenda.title}")
                android.util.Log.d("AgendaViewModel", "  - FilePath: ${agenda.filePath}")
                android.util.Log.d("AgendaViewModel", "  - FilePath isBlank: ${agenda.filePath.isNullOrBlank()}")
                _formUiState.value = AgendaFormUiState(
                    title = agenda.title,
                    description = agenda.description,
                    date = agenda.date,
                    time = agenda.time,
                    category = agenda.category,
                    priority = agenda.priority,
                    attachedFileUri = null
                )
                _selectedReminder.value = null
                android.util.Log.d("AgendaViewModel", "Agenda loaded for edit: ${agenda.title}, filePath: ${agenda.filePath}")
            } else {
                android.util.Log.e("AgendaViewModel", "Agenda not found for edit: $agendaId")
                android.util.Log.d("AgendaViewModel", "Available agenda IDs: ${_masterAgendaList.map { it.id }}")
            }
        }
    }
    fun clearEditMode() {
        _editingAgendaId.value = null
        _existingFilePath.value = null
        _formUiState.value = AgendaFormUiState()
        _selectedReminder.value = null
    }
    fun clearExistingFilePath() {
        _existingFilePath.value = null
    }
    private val _shouldNavigateBack = MutableStateFlow(false)
    val shouldNavigateBack: StateFlow<Boolean> = _shouldNavigateBack.asStateFlow()
    fun clearNavigateBackFlag() {
        _shouldNavigateBack.value = false
    }
    fun onDismissDialog() {
        _showConfirmDialog.value = false
        _showSuccessDialog.value = false
    }
    fun onFilterIconClicked() { _showFilterSheet.value = true }
    fun onDismissFilterSheet() { _showFilterSheet.value = false }
    fun onKategoriSelected(v: String) { _selectedKategori.value = v }
    fun onUrutkanSelected(v: String) { _selectedUrutkan.value = v }
    fun onTerapkanFilterClicked() {
        onDismissFilterSheet()
        applyFiltersAndSort()
    }
    fun deleteAgenda(agendaId: String) {
        viewModelScope.launch {
            try {
                val agendaIdInt = agendaId.toIntOrNull()
                if (agendaIdInt != null) {
                    reminderScheduler.cancelReminder(agendaIdInt)
                    android.util.Log.d("AgendaViewModel", "Reminder cancelled for deleted agenda: $agendaId")
                }
                repository.deleteAgenda(agendaId)
                android.util.Log.d("AgendaViewModel", "Agenda berhasil dihapus: $agendaId")
                loadAgendas()
            } catch (e: Exception) {
                android.util.Log.e("AgendaViewModel", "Error deleting agenda: ${e.message}", e)
            }
        }
    }
    fun showAgendaDetail(agenda: Agenda) {
        _selectedAgendaForDetail.value = agenda
        _showDetailDialog.value = true
    }
    fun dismissDetailDialog() {
        _showDetailDialog.value = false
        _selectedAgendaForDetail.value = null
    }
    val isEditMode: StateFlow<Boolean> = _editingAgendaId.map { it != null }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        false
    )
}
class AgendaViewModelFactory(
    private val repository: AgendaRepository,
    private val context: Context
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AgendaViewModel::class.java)) {
            val reminderScheduler = ReminderScheduler(context)
            @Suppress("UNCHECKED_CAST")
            return AgendaViewModel(repository, reminderScheduler) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}