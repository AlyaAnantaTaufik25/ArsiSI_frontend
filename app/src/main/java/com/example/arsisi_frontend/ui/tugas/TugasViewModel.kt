package com.example.arsisi_frontend.ui.tugas

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.arsisi_frontend.data.model.CreateTugasRequest
import com.example.arsisi_frontend.data.model.Tugas
import com.example.arsisi_frontend.data.model.TugasFormState
import com.example.arsisi_frontend.data.repository.MataKuliahRepository
import com.example.arsisi_frontend.data.repository.TugasRepository
import com.example.arsisi_frontend.utils.NotificationHelper
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class TugasViewModel(
    application: Application,
    private val tugasRepository: TugasRepository,
    private val matakuliahRepository: MataKuliahRepository
) : AndroidViewModel(application) {

    private val notificationHelper = NotificationHelper(application.applicationContext)

    // ==================== UI STATES ====================
    sealed class ListUiState {
        data object Loading : ListUiState()
        data class Success(val tugasList: List<Tugas>, val isPublic: Boolean) : ListUiState()
        data class Error(val message: String) : ListUiState()
    }

    sealed class DetailUiState {
        data object Loading : DetailUiState()
        data class Success(val tugas: Tugas) : DetailUiState()
        data class Error(val message: String) : DetailUiState()
    }

    enum class TugasType { SAYA, PUBLIK }

    data class MataKuliahEksplorasi(
        val matakuliahId: Int?,
        val namaMatakuliah: String?,
        val kodeMatakuliah: String?,
        val jumlahTugas: Int
    )

    // ==================== STATE FLOWS ====================
    private val _listUiState = MutableStateFlow<ListUiState>(ListUiState.Loading)
    val listUiState: StateFlow<ListUiState> = _listUiState

    private val _detailUiState = MutableStateFlow<DetailUiState>(DetailUiState.Loading)
    val detailUiState: StateFlow<DetailUiState> = _detailUiState

    private val _formState = MutableStateFlow(TugasFormState())
    val formState: StateFlow<TugasFormState> = _formState

    private val _selectedType = MutableStateFlow(TugasType.SAYA)
    val selectedType: StateFlow<TugasType> = _selectedType

    init {
        fetchTugasList(TugasType.SAYA)
        fetchMataKuliah()
    }

    // ==================== LIST OPERATIONS ====================
    fun switchType(type: TugasType) {
        _selectedType.value = type
        fetchTugasList(type)
    }

    private fun fetchTugasList(type: TugasType) {
        _listUiState.value = ListUiState.Loading
        viewModelScope.launch {
            val flow = when (type) {
                TugasType.SAYA -> tugasRepository.getTugasSaya()
                TugasType.PUBLIK -> tugasRepository.getTugasPublik()
            }

            flow.catch { e ->
                _listUiState.value = ListUiState.Error(e.localizedMessage ?: "Gagal memuat tugas.")
            }.collect { tugasList ->
                _listUiState.value = ListUiState.Success(tugasList, type == TugasType.PUBLIK)
            }
        }
    }

    fun refreshTugasList() {
        fetchTugasList(_selectedType.value)
    }

    // ✅ FIX: Fungsi deleteTugas (Menghilangkan error Gambar 23)
    fun deleteTugas(tugasId: Int) {
        viewModelScope.launch {
            tugasRepository.deleteTugas(tugasId)
                .catch { e ->
                    Log.e("TugasViewModel", "Error delete: ${e.message}")
                }
                .collect {
                    refreshTugasList()
                }
        }
    }

    // ✅ FIX: Fungsi getEksplorasiPerMatkul (Menghilangkan error Gambar 22)
    fun getEksplorasiPerMatkul(tugasList: List<Tugas>): List<MataKuliahEksplorasi> {
        return tugasList
            .filter { it.visibility?.equals("Publik", ignoreCase = true) == true }
            .groupBy { it.matakuliah_id }
            .map { (_, list) ->
                val first = list.first()
                MataKuliahEksplorasi(
                    matakuliahId = first.matakuliah_id,
                    namaMatakuliah = first.namaMatakuliah,
                    kodeMatakuliah = first.kodeMatakuliah,
                    jumlahTugas = list.size
                )
            }
            .sortedBy { it.namaMatakuliah ?: "" }
    }

    // ==================== DETAIL & EDIT OPERATIONS ====================

    // ✅ FIX: Fungsi loadTugasDetail (Menghilangkan error Gambar 21)
    fun loadTugasDetail(tugasId: Int) {
        _detailUiState.value = DetailUiState.Loading
        viewModelScope.launch {
            tugasRepository.getTugasById(tugasId)
                .catch { e ->
                    _detailUiState.value = DetailUiState.Error(e.localizedMessage ?: "Gagal memuat detail.")
                }
                .collect { tugas ->
                    _detailUiState.value = DetailUiState.Success(tugas)
                }
        }
    }

    fun loadTugasForEdit(tugasId: Int) {
        _formState.update { it.copy(isLoading = true) }
        viewModelScope.launch {
            tugasRepository.getTugasById(tugasId)
                .catch { e ->
                    _formState.update { it.copy(isLoading = false, errorMessage = e.localizedMessage) }
                }
                .collect { tugas ->
                    _formState.update {
                        it.copy(
                            isLoading = false,
                            selectedMatkulId = tugas.matakuliah_id,
                            judul = tugas.judul ?: "",
                            deskripsi = tugas.deskripsi ?: "",
                            linkTugas = tugas.linkTugas ?: "",
                            visibility = tugas.visibility ?: "Private",
                            tipe_tugas = tugas.tipe_tugas ?: "Individu"
                        )
                    }
                }
        }
    }

    // ==================== FORM SUBMISSION ====================
    fun submitForm(tugasId: Int? = null) {
        val currentState = _formState.value
        if (currentState.selectedMatkulId == null || currentState.judul.isBlank()) {
            _formState.update { it.copy(errorMessage = "Judul dan Mata Kuliah wajib diisi.") }
            return
        }

        _formState.update { it.copy(isSaving = true, errorMessage = null) }

        val requestBody = CreateTugasRequest(
            judul = currentState.judul,
            deskripsi = currentState.deskripsi,
            linkTugas = currentState.linkTugas.ifBlank { null },
            visibility = currentState.visibility,
            matakuliahId = currentState.selectedMatkulId!!,
            tipe_tugas = currentState.tipe_tugas
        )

        viewModelScope.launch {
            val flow = if (tugasId == null) tugasRepository.createTugas(requestBody)
            else tugasRepository.updateTugas(tugasId, requestBody)

            flow.catch { e ->
                _formState.update { it.copy(isSaving = false, errorMessage = e.localizedMessage) }
            }.collect {
                _formState.update { it.copy(isSaving = false, successMessage = "Tugas berhasil disimpan") }

                // Kirim notifikasi jika publik
                if (currentState.visibility.equals("Publik", ignoreCase = true)) {
                    notificationHelper.sendUploadNotification(currentState.judul)
                }
                refreshTugasList()
            }
        }
    }

    // ==================== EVENT HANDLERS ====================
    fun onJudulChange(v: String) = _formState.update { it.copy(judul = v) }
    fun onDeskripsiChange(v: String) = _formState.update { it.copy(deskripsi = v) }
    fun onLinkTugasChange(v: String) = _formState.update { it.copy(linkTugas = v) }
    fun onMatkulSelected(id: Int) = _formState.update { it.copy(selectedMatkulId = id) }
    fun onTypeSelected(t: String) = _formState.update { it.copy(tipe_tugas = t) }
    fun onVisibilityToggle(isPub: Boolean) = _formState.update {
        it.copy(visibility = if (isPub) "Publik" else "Private")
    }
    fun resetFormState() = _formState.update { TugasFormState(mataKuliahList = it.mataKuliahList) }
    fun onSuccessMessageShown() = _formState.update { it.copy(successMessage = null) }

    private fun fetchMataKuliah() {
        viewModelScope.launch {
            matakuliahRepository.getAllMataKuliah()
                .collect { list -> _formState.update { it.copy(mataKuliahList = list) } }
        }
    }

    companion object {
        fun provideFactory(
            application: Application,
            tugasRepository: TugasRepository,
            matakuliahRepository: MataKuliahRepository
        ): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return TugasViewModel(application, tugasRepository, matakuliahRepository) as T
            }
        }
    }
}