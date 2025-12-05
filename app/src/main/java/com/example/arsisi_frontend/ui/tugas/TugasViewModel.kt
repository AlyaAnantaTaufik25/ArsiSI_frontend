package com.example.arsisi_frontend.ui.tugas

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.arsisi_frontend.data.model.Tugas
import com.example.arsisi_frontend.data.repository.TugasRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

sealed class TugasUiState {
    data object Loading : TugasUiState()
    data class Success(val tugasList: List<Tugas>, val isPublic: Boolean) : TugasUiState()
    data class Error(val message: String) : TugasUiState()
}

enum class TugasType { SAYA, PUBLIK }

/**
 * Model ringkasan untuk tab Eksplorasi:
 * satu item = satu mata kuliah + jumlah tugas publik di matkul itu.
 */
data class MataKuliahEksplorasi(
    val matakuliahId: Int?,
    val namaMatakuliah: String?,
    val kodeMatakuliah: String?,
    val jumlahTugas: Int
)

class TugasViewModel(
    private val repository: TugasRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<TugasUiState>(TugasUiState.Loading)
    val uiState: StateFlow<TugasUiState> = _uiState

    private val _selectedType = MutableStateFlow(TugasType.SAYA)
    val selectedType: StateFlow<TugasType> = _selectedType

    init {
        fetchTugas(TugasType.SAYA)
    }

    fun switchType(type: TugasType) {
        _selectedType.value = type
        fetchTugas(type)
    }

    fun fetchTugas(type: TugasType) {
        _uiState.value = TugasUiState.Loading
        viewModelScope.launch {
            val flow = when (type) {
                TugasType.SAYA -> repository.getTugasSaya()
                TugasType.PUBLIK -> repository.getTugasPublik()
            }

            flow
                .catch { e ->
                    _uiState.value = TugasUiState.Error(
                        e.localizedMessage
                            ?: "Gagal memuat. Periksa token JWT dan koneksi server."
                    )
                }
                .collect { tugasList ->
                    _uiState.value = TugasUiState.Success(
                        tugasList = tugasList,
                        isPublic = type == TugasType.PUBLIK
                    )
                }
        }
    }

    fun refreshTugasList() {
        fetchTugas(_selectedType.value)
    }

    /**
     * Hapus tugas lalu refresh daftar di tab aktif.
     * Pastikan TugasRepository punya fungsi suspend deleteTugas(tugasId: Int).
     */
    fun deleteTugas(tugasId: Int) {
        viewModelScope.launch {
            try {
                repository.deleteTugas(tugasId)

                // Cara 1: langsung buang dari list saat ini (lebih responsif)
                _uiState.update { current ->
                    if (current is TugasUiState.Success) {
                        current.copy(
                            tugasList = current.tugasList.filter { it.tugasId != tugasId }
                        )
                    } else {
                        current
                    }
                }

                // Opsional: kalau mau benar‑benar sync dengan server, panggil lagi
                // fetchTugas(_selectedType.value)
            } catch (e: Exception) {
                _uiState.value = TugasUiState.Error(
                    e.localizedMessage ?: "Gagal menghapus tugas."
                )
            }
        }
    }

    fun getEksplorasiPerMatkul(tugasList: List<Tugas>): List<MataKuliahEksplorasi> {
        return tugasList
            .filter { it.visibility.equals("Publik", ignoreCase = true) }
            .groupBy { it.matakuliah_id }
            .map { (_, tugasPerMatkul) ->
                val first = tugasPerMatkul.first()
                MataKuliahEksplorasi(
                    matakuliahId = first.matakuliah_id,
                    namaMatakuliah = first.namaMatakuliah,
                    kodeMatakuliah = first.kodeMatakuliah,
                    jumlahTugas = tugasPerMatkul.size
                )
            }
            .sortedBy { it.namaMatakuliah ?: "" }
    }
}
