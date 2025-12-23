package com.example.arsisi_frontend.ui.prestasi

import android.app.Application
import android.content.Context
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.arsisi_frontend.data.model.*
import com.example.arsisi_frontend.data.remote.ApiService
import com.example.arsisi_frontend.utils.PreferencesManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeoutOrNull
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import android.util.Log
import okhttp3.RequestBody.Companion.toRequestBody


class PrestasiViewModel(
    application: Application
) : AndroidViewModel(application) {

    private val prefsManager = PreferencesManager(application.applicationContext)
    private val context: Context = application.applicationContext
    private val apiService: ApiService = com.example.arsisi_frontend.data.remote.ApiClient.apiService

    // UI STATES
    private val _prestasiListState = MutableStateFlow<UiState<List<Prestasi>>>(UiState.Idle)
    val prestasiListState: StateFlow<UiState<List<Prestasi>>> = _prestasiListState.asStateFlow()

    private val _prestasiDetailState = MutableStateFlow<UiState<Prestasi>>(UiState.Idle)
    val prestasiDetailState: StateFlow<UiState<Prestasi>> = _prestasiDetailState.asStateFlow()

    private val _operationState = MutableStateFlow<UiState<String>>(UiState.Idle)
    val operationState: StateFlow<UiState<String>> = _operationState.asStateFlow()

    private val _statistikState = MutableStateFlow<UiState<ArsipStatistik>>(UiState.Idle)
    val statistikState: StateFlow<UiState<ArsipStatistik>> = _statistikState.asStateFlow()

    // FILTER STATES
    private val _selectedJenis = MutableStateFlow("SEMUA")
    val selectedJenis: StateFlow<String> = _selectedJenis.asStateFlow()

    private val _selectedKategori = MutableStateFlow(KategoriArsip.SEMUA)
    val selectedKategori: StateFlow<KategoriArsip> = _selectedKategori.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private var originalPrestasiList: List<Prestasi> = emptyList()

    init {
        loadInitialData()
    }

    private fun loadInitialData() {
        loadPrestasiList()
    }

    fun loadPrestasiList() {
        viewModelScope.launch {
            _prestasiListState.value = UiState.Loading
            Log.d("PrestasiVM", "🚀 Loading prestasi...")

            val token = try { prefsManager.getToken() } catch (e: Exception) { null }

            if (token == null) {
                Log.e("PrestasiVM", "❌ No auth")
                _prestasiListState.value = UiState.Error("User belum login")
                return@launch
            }

            try {
                // ✅ FIXED - HAPUS userId, hanya 1 parameter!
                val response = withTimeoutOrNull(15000) {
                    apiService.getArsip("Bearer $token")
                }

                if (response == null) {
                    Log.e("PrestasiVM", "⏰ TIMEOUT")
                    _prestasiListState.value = UiState.Error("Server timeout")
                    return@launch
                }

                Log.d("PrestasiVM", "📥 Response: ${response.code()}")

                if (response.isSuccessful) {
                    val body = response.body()
                    val arsipList = body?.data ?: emptyList()

                    // ✅ MAP Arsip → Prestasi (SESUAI MODEL)
                    val prestasiList = arsipList
                        .filter { it.kategori.lowercase().contains("prestasi") }
                        .map { arsip ->
                            Prestasi(
                                id = arsip.arsipId,
                                userId = arsip.mahasiswaId,
                                nama = arsip.judul,
                                jenis = arsip.kategori,
                                tingkat = "Lokal", // Default
                                tahun = 2024,
                                penyelenggara = "Penyelenggara",
                                deskripsi = arsip.deskripsi,
                                filePath = arsip.filePath
                            )
                        }

                    originalPrestasiList = prestasiList
                    updateStatistik(prestasiList)
                    _prestasiListState.value = UiState.Success(prestasiList)
                    Log.d("PrestasiVM", "✅ Loaded ${prestasiList.size} prestasi")
                } else {
                    Log.e("PrestasiVM", "❌ HTTP ${response.code()}")
                    _prestasiListState.value = UiState.Error("Server: ${response.code()}")
                }
            } catch (e: Exception) {
                Log.e("PrestasiVM", "💥 Error: ${e.message}", e)
                _prestasiListState.value = UiState.Error("Network error")
            }
        }
    }


    private fun updateStatistik(prestasiList: List<Prestasi>) {
        val stats = ArsipStatistik(
            totalArsip = prestasiList.size,
            totalPrestasi = prestasiList.size,
            totalSertifikat = 0,
            totalOrganisasi = 0
        )
        _statistikState.value = UiState.Success(stats)
    }

    fun loadStatistik() {
        loadPrestasiList()
    }

    fun loadPrestasiDetail(prestasiId: Int) {
        viewModelScope.launch {
            _prestasiDetailState.value = UiState.Loading
            val prestasi = originalPrestasiList.find { it.id == prestasiId }
            if (prestasi != null) {
                _prestasiDetailState.value = UiState.Success(prestasi)
            } else {
                _prestasiDetailState.value = UiState.Error("Prestasi tidak ditemukan")
            }
        }
    }

    private suspend fun uploadFile(fileUri: Uri?): String? {
        val token = try { prefsManager.getToken() } catch (e: Exception) { return null }
        if (token == null || fileUri == null) return null

        return try {
            val fileName = "prestasi_${System.currentTimeMillis()}.jpg"
            val file = File(context.cacheDir, fileName)

            context.contentResolver.openInputStream(fileUri)?.use { input ->
                file.outputStream().use { output ->
                    input.copyTo(output)
                }
            }

            val requestFile = file.asRequestBody("image/*".toMediaTypeOrNull())
            val filePart = MultipartBody.Part.createFormData("file", file.name, requestFile)
            val typePart = "prestasi".toRequestBody("text/plain".toMediaTypeOrNull())

            val response = apiService.uploadFile("Bearer $token", filePart, typePart)
            response.body()?.filePath
        } catch (e: Exception) {
            Log.e("PrestasiVM", "Upload error: ${e.message}")
            null
        }
    }

    fun createPrestasi(
        nama: String, jenis: String, tingkat: String, tahun: Int,
        penyelenggara: String?, deskripsi: String, tanggal: String, fileUri: Uri? = null
    ) {
        viewModelScope.launch {
            _operationState.value = UiState.Loading
            try {
                val token = prefsManager.getToken() ?: run {
                    _operationState.value = UiState.Error("Token tidak ada")
                    return@launch
                }

                val request = ArsipRequest(
                    kategori = "PRESTASI",   // atau dari dropdown
                    judul = nama,
                    deskripsi = deskripsi,
                    tanggal = tanggal
                )


                val response = apiService.createArsip("Bearer $token", request)
                if (response.isSuccessful) {
                    _operationState.value = UiState.Success("✅ Prestasi ditambahkan!")
                    loadPrestasiList()
                } else {
                    _operationState.value = UiState.Error("Gagal: ${response.code()}")
                }
            } catch (e: Exception) {
                _operationState.value = UiState.Error("Error: ${e.message}")
            }
        }
    }

    fun updatePrestasi(
        prestasiId: Int, nama: String, jenis: String, tingkat: String,
        tahun: Int, penyelenggara: String?, deskripsi: String, tanggal: String, fileUri: Uri? = null
    ) {
        viewModelScope.launch {
            _operationState.value = UiState.Loading
            try {
                val token = prefsManager.getToken() ?: run {
                    _operationState.value = UiState.Error("Token tidak ada")
                    return@launch
                }

                val request = ArsipRequest(
                    kategori = "PRESTASI",   // atau dari dropdown
                    judul = nama,
                    deskripsi = deskripsi,
                    tanggal = tanggal
                )


                val response = apiService.updateArsip("Bearer $token", prestasiId, request)
                if (response.isSuccessful) {
                    _operationState.value = UiState.Success("✅ Prestasi diupdate!")
                    loadPrestasiList()
                } else {
                    _operationState.value = UiState.Error("Gagal: ${response.code()}")
                }
            } catch (e: Exception) {
                _operationState.value = UiState.Error("Error: ${e.message}")
            }
        }
    }

    fun deletePrestasi(prestasiId: Int) {
        viewModelScope.launch {
            _operationState.value = UiState.Loading
            val token = try { prefsManager.getToken() } catch (e: Exception) { null }

            if (token == null) {
                _operationState.value = UiState.Error("Token tidak ada")
                return@launch
            }

            try {
                val response = apiService.deleteArsip("Bearer $token", prestasiId)
                if (response.isSuccessful) {
                    _operationState.value = UiState.Success("✅ Dihapus!")
                    loadPrestasiList()
                } else {
                    _operationState.value = UiState.Error("Gagal: ${response.code()}")
                }
            } catch (e: Exception) {
                _operationState.value = UiState.Error("Error: ${e.message}")
            }
        }
    }

    // FILTER FUNCTIONS
    fun setKategori(kategori: KategoriArsip) {
        _selectedKategori.value = kategori
        filterPrestasiList()
    }

    fun setJenisFilter(jenis: String) {
        _selectedJenis.value = jenis
        filterPrestasiList()
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
        filterPrestasiList()
    }

    fun clearSearch() {
        setSearchQuery("")
    }

    private fun filterPrestasiList() {
        val filtered = originalPrestasiList.filter { prestasi ->
            val matchKategori = when (_selectedKategori.value) {
                KategoriArsip.SEMUA -> true
                KategoriArsip.PRESTASI -> true
                else -> false
            }
            val matchQuery = _searchQuery.value.isEmpty() ||
                    prestasi.nama.contains(_searchQuery.value, ignoreCase = true)
            matchKategori && matchQuery
        }
        _prestasiListState.value = UiState.Success(filtered)
    }

    fun resetOperationState() {
        _operationState.value = UiState.Idle
    }
}
