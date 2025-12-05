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

class TugasFormViewModel(
    private val tugasRepository: TugasRepository,
    private val matakuliahRepository: MataKuliahRepository,
    private val tugasId: Int? = null
) : ViewModel() {

    private val _state = MutableStateFlow(TugasFormState(isLoading = (tugasId != null)))
    val state: StateFlow<TugasFormState> = _state

    init {
        if (tugasId != null) {
            fetchInitialData(tugasId)
        } else {
            fetchMataKuliah()
        }
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

    private fun fetchMataKuliah() {
        _state.update { it.copy(isLoading = true, errorMessage = null) }
        viewModelScope.launch {
            matakuliahRepository.getAllMataKuliah()
                .catch { e ->
                    _state.update { it.copy(isLoading = false, errorMessage = "Gagal memuat mata kuliah: ${e.localizedMessage}") }
                }
                .collect { matkulList ->
                    _state.update {
                        it.copy(
                            isLoading = false,
                            mataKuliahList = matkulList,
                            selectedMatkulId = matkulList.firstOrNull()?.matakuliahId
                        )
                    }
                }
        }
    }

    fun onJudulChange(value: String) { _state.update { it.copy(judul = value) } }
    fun onDeskripsiChange(value: String) { _state.update { it.copy(deskripsi = value) } }
    fun onLinkTugasChange(value: String) { _state.update { it.copy(linkTugas = value) } }
    fun onMatkulSelected(matkulId: Int) { _state.update { it.copy(selectedMatkulId = matkulId) } }
    fun onTypeSelected(tipeTugas: String) { _state.update { it.copy(tipe_tugas = tipeTugas) } }
    fun onVisibilityToggle(isPublic: Boolean) { _state.update { it.copy(visibility = if (isPublic) "Publik" else "Private") } }
    fun onDismissError() { _state.update { it.copy(errorMessage = null) } }
    fun onSuccessMessageShown() { _state.update { it.copy(successMessage = null) } }

    fun submitForm() {
        val currentState = _state.value

        if (currentState.selectedMatkulId == null) {
            _state.update { it.copy(errorMessage = "Pilih Mata Kuliah terlebih dahulu.") }
            return
        }

        if (currentState.linkTugas.isBlank()) {
            _state.update { it.copy(errorMessage = "Link tugas wajib diisi.") }
            return
        }

        if (!isValidUrl(currentState.linkTugas)) {
            _state.update { it.copy(errorMessage = "Masukkan link yang valid (diawali http atau https).") }
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
            val flow = if (tugasId == null) {
                tugasRepository.createTugas(requestBody)
            } else {
                tugasRepository.updateTugas(tugasId, requestBody)
            }

            flow
                .catch { e ->
                    _state.update { it.copy(isSaving = false, errorMessage = e.localizedMessage ?: "Gagal menyimpan/mengupdate tugas.") }
                }
                .collect { response ->
                    _state.update { it.copy(isSaving = false, successMessage = response.message) }
                }
        }
    }

    private fun isValidUrl(url: String): Boolean {
        val trimmed = url.trim()
        if (trimmed.isBlank()) return false
        return trimmed.startsWith("http://") || trimmed.startsWith("https://")
    }

}
