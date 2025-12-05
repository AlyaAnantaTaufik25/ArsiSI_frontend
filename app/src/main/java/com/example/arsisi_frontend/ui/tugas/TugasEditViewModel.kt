package com.example.arsisi_frontend.ui.tugas

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.arsisi_frontend.data.model.CreateTugasRequest
import com.example.arsisi_frontend.data.model.TugasFormState
import com.example.arsisi_frontend.data.repository.MataKuliahRepository
import com.example.arsisi_frontend.data.repository.TugasRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class TugasEditViewModel(
    private val tugasRepository: TugasRepository,
    private val matakuliahRepository: MataKuliahRepository,
    private val tugasId: Int
) : ViewModel() {

    private val _state = MutableStateFlow(TugasFormState(isLoading = true))
    val state: StateFlow<TugasFormState> = _state

    init {
        fetchInitialData(tugasId)
    }

    private fun fetchInitialData(id: Int) {
        viewModelScope.launch {
            matakuliahRepository.getAllMataKuliah().collect { list ->
                _state.update { it.copy(mataKuliahList = list) }
            }

            tugasRepository.getTugasById(id)
                .catch { e ->
                    _state.update { it.copy(isLoading = false, errorMessage = "Gagal memuat detail tugas: ${e.localizedMessage}") }
                }
                .collect { tugas ->
                    _state.update {
                        it.copy(
                            isLoading = false,
                            selectedMatkulId = tugas.matakuliah_id,
                            judul = tugas.judul,
                            deskripsi = tugas.deskripsi,
                            linkTugas = tugas.linkTugas ?: "",
                            visibility = tugas.visibility,
                            tipe_tugas = tugas.tipe_tugas ?: "Individu"
                        )
                    }
                }
        }
    }

    fun onJudulChange(value: String) { _state.update { it.copy(judul = value) } }
    fun onDeskripsiChange(value: String) { _state.update { it.copy(deskripsi = value) } }
    fun onLinkTugasChange(value: String) { _state.update { it.copy(linkTugas = value) } }
    fun onMatkulSelected(matkulId: Int) { _state.update { it.copy(selectedMatkulId = matkulId) } }
    fun onTypeSelected(tipeTugasString: String) { _state.update { it.copy(tipe_tugas = tipeTugasString) } }
    fun onVisibilityToggle(isPublic: Boolean) { _state.update { it.copy(visibility = if (isPublic) "Publik" else "Private") } }
    fun onDismissError() { _state.update { it.copy(errorMessage = null) } }

    fun submitUpdate(onSuccess: () -> Unit) {
        val currentState = _state.value
        if (currentState.selectedMatkulId == null) {
            _state.update { it.copy(errorMessage = "Pilih Mata Kuliah terlebih dahulu.") }
            return
        }

        _state.update { it.copy(isSaving = true, errorMessage = null, successMessage = null) }

        val requestBody = CreateTugasRequest(
            judul = currentState.judul,
            deskripsi = currentState.deskripsi,
            linkTugas = currentState.linkTugas.ifBlank { null },
            visibility = currentState.visibility,
            matakuliahId = currentState.selectedMatkulId!!,
            tipe_tugas = currentState.tipe_tugas
        )

        viewModelScope.launch {
            tugasRepository.updateTugas(tugasId, requestBody)
                .catch { e ->
                    _state.update { it.copy(isSaving = false, errorMessage = e.localizedMessage ?: "Gagal meng-update tugas.") }
                }
                .collect { response ->
                    _state.update { it.copy(isSaving = false, successMessage = response.message) }
                    onSuccess()
                }
        }
    }
}