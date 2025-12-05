package com.example.arsisi_frontend.ui.prestasi

import android.app.Application
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.arsisi_frontend.data.model.*
import com.example.arsisi_frontend.data.repository.PrestasiRepository
import com.example.arsisi_frontend.utils.PreferencesManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class PrestasiViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = PrestasiRepository(application.applicationContext)
    private val prefsManager = PreferencesManager(application.applicationContext)

    // State list & detail Prestasi
    private val _prestasiListState =
        MutableStateFlow<UiState<List<Prestasi>>>(UiState.Idle)
    val prestasiListState: StateFlow<UiState<List<Prestasi>>> =
        _prestasiListState.asStateFlow()

    private val _prestasiDetailState =
        MutableStateFlow<UiState<Prestasi>>(UiState.Idle)
    val prestasiDetailState: StateFlow<UiState<Prestasi>> =
        _prestasiDetailState.asStateFlow()

    private val _operationState =
        MutableStateFlow<UiState<String>>(UiState.Idle)
    val operationState: StateFlow<UiState<String>> =
        _operationState.asStateFlow()

    private val _uploadState =
        MutableStateFlow<UiState<String>>(UiState.Idle)
    val uploadState: StateFlow<UiState<String>> =
        _uploadState.asStateFlow()

    private val _statistikState =
        MutableStateFlow<UiState<ArsipStatistik>>(UiState.Idle)
    val statistikState: StateFlow<UiState<ArsipStatistik>> =
        _statistikState.asStateFlow()

    private val _selectedKategori =
        MutableStateFlow<KategoriArsip>(KategoriArsip.SEMUA)
    val selectedKategori: StateFlow<KategoriArsip> =
        _selectedKategori.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    init {
        loadPrestasiList()
        loadStatistik()
    }

    /**
     * Load daftar prestasi
     */
    fun loadPrestasiList() {
        viewModelScope.launch {
            _prestasiListState.value = UiState.Loading
            val userId = prefsManager.getUserId()

            if (userId == -1) {
                _prestasiListState.value = UiState.Error("User not logged in")
                return@launch
            }

            val result = repository.getPrestasiList(userId)

            _prestasiListState.value = if (result.isSuccess) {
                UiState.Success(result.getOrNull() ?: emptyList())
            } else {
                UiState.Error(result.exceptionOrNull()?.message ?: "Unknown error")
            }
        }
    }

    /**
     * Load detail satu prestasi
     */
    fun loadPrestasiDetail(prestasiId: Int) {
        viewModelScope.launch {
            _prestasiDetailState.value = UiState.Loading
            val result = repository.getPrestasiById(prestasiId)
            _prestasiDetailState.value = if (result.isSuccess) {
                UiState.Success(result.getOrNull()!!)
            } else {
                UiState.Error(result.exceptionOrNull()?.message ?: "Unknown error")
            }
        }
    }

    /**
     * Membuat prestasi baru
     */
    fun createPrestasi(
        nama: String,
        jenis: String,
        tingkat: String,
        tahun: Int,
        penyelenggara: String,
        deskripsi: String,
        filePath: String?
    ) {
        viewModelScope.launch {
            _operationState.value = UiState.Loading
            val userId = prefsManager.getUserId()

            if (userId == -1) {
                _operationState.value = UiState.Error("User not logged in")
                return@launch
            }

            val request = PrestasiRequest(
                userId = userId,
                nama = nama,
                jenis = jenis,
                tingkat = tingkat,
                tahun = tahun,
                penyelenggara = penyelenggara,
                deskripsi = deskripsi,
                filePath = filePath
            )

            val result = repository.createPrestasi(request)
            _operationState.value = if (result.isSuccess) {
                loadPrestasiList()
                UiState.Success("Prestasi berhasil ditambahkan")
            } else {
                UiState.Error(result.exceptionOrNull()?.message ?: "Failed to create prestasi")
            }
        }
    }

    /**
     * Memperbarui prestasi
     */
    fun updatePrestasi(
        prestasiId: Int,
        nama: String,
        jenis: String,
        tingkat: String,
        tahun: Int,
        penyelenggara: String,
        deskripsi: String,
        filePath: String?
    ) {
        viewModelScope.launch {
            _operationState.value = UiState.Loading
            val userId = prefsManager.getUserId()

            if (userId == -1) {
                _operationState.value = UiState.Error("User not logged in")
                return@launch
            }

            val request = PrestasiRequest(
                userId = userId,
                nama = nama,
                jenis = jenis,
                tingkat = tingkat,
                tahun = tahun,
                penyelenggara = penyelenggara,
                deskripsi = deskripsi,
                filePath = filePath
            )

            val result = repository.updatePrestasi(prestasiId, request)
            _operationState.value = if (result.isSuccess) {
                loadPrestasiList()
                UiState.Success("Prestasi berhasil diperbarui")
            } else {
                UiState.Error(result.exceptionOrNull()?.message ?: "Failed to update prestasi")
            }
        }
    }

    /**
     * Menghapus prestasi
     */
    fun deletePrestasi(prestasiId: Int) {
        viewModelScope.launch {
            _operationState.value = UiState.Loading
            val result = repository.deletePrestasi(prestasiId)
            _operationState.value = if (result.isSuccess) {
                loadPrestasiList()
                UiState.Success(result.getOrNull() ?: "Prestasi berhasil dihapus")
            } else {
                UiState.Error(result.exceptionOrNull()?.message ?: "Failed to delete prestasi")
            }
        }
    }

    /**
     * Upload file
     */
    fun uploadFile(uri: Uri, fileType: String = "prestasi") {
        viewModelScope.launch {
            _uploadState.value = UiState.Loading
            val result = repository.uploadFile(uri, fileType)
            _uploadState.value = if (result.isSuccess) {
                UiState.Success(result.getOrNull() ?: "")
            } else {
                UiState.Error(result.exceptionOrNull()?.message ?: "Failed to upload file")
            }
        }
    }

    /**
     * Pencarian prestasi
     */
    fun searchPrestasi(query: String) {
        viewModelScope.launch {
            _searchQuery.value = query

            if (query.isEmpty()) {
                loadPrestasiList()
                return@launch
            }

            _prestasiListState.value = UiState.Loading
            val mahasiswaId = prefsManager.getUserId()

            if (mahasiswaId == -1) {
                _prestasiListState.value = UiState.Error("User not logged in")
                return@launch
            }

            val kategori = if (_selectedKategori.value != KategoriArsip.SEMUA) {
                _selectedKategori.value.value
            } else null

            val result = repository.searchPrestasi(mahasiswaId, query, kategori)

            _prestasiListState.value = if (result.isSuccess) {
                UiState.Success(result.getOrNull() ?: emptyList())
            } else {
                UiState.Error(result.exceptionOrNull()?.message ?: "Search failed")
            }
        }
    }

    /**
     * Filter kategori (menggunakan search ulang)
     */
    fun filterByKategori(kategori: KategoriArsip) {
        _selectedKategori.value = kategori
        searchPrestasi(_searchQuery.value)
    }

    fun resetUploadState() {
        _uploadState.value = UiState.Idle
    }

    fun resetOperationState() {
        _operationState.value = UiState.Idle
    }

    /**
     * Load statistik (aktifkan kalau fungsi repository sudah ada)
     */
    fun loadStatistik() {
        viewModelScope.launch {
            _statistikState.value = UiState.Loading
            val mahasiswaId = prefsManager.getUserId()

            if (mahasiswaId == -1) {
                _statistikState.value = UiState.Error("User not logged in")
                return@launch
            }
        }
    }
}

// Pastikan ada fungsi ini di PrestasiRepository:
// suspend fun getStatistik(userId: Int
