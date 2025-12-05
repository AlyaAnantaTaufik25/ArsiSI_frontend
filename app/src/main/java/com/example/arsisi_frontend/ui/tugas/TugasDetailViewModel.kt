package com.example.arsisi_frontend.ui.tugas

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.arsisi_frontend.data.model.Tugas
import com.example.arsisi_frontend.data.repository.TugasRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

sealed class DetailUiState {
    data object Loading : DetailUiState()
    data class Success(val tugas: Tugas) : DetailUiState()
    data class Error(val message: String) : DetailUiState()
}

class TugasDetailViewModel(
    private val repository: TugasRepository,
    tugasId: Int
) : ViewModel() {

    private val _uiState = MutableStateFlow<DetailUiState>(DetailUiState.Loading)
    val uiState: StateFlow<DetailUiState> = _uiState

    init {
        fetchTugasDetail(tugasId)
    }

    private fun fetchTugasDetail(tugasId: Int) {
        viewModelScope.launch {
            repository.getTugasById(tugasId)
                .catch { e ->
                    _uiState.value = DetailUiState.Error(e.localizedMessage ?: "Gagal memuat detail.")
                }
                .collect { tugas ->
                    _uiState.value = DetailUiState.Success(tugas)
                }
        }
    }
}