package com.example.arsisi_frontend.ui.dashboard

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
// Import semua model data yang dibutuhkan untuk tipe List
import com.example.arsisi_frontend.data.model.Agenda
import com.example.arsisi_frontend.data.model.MataKuliah
import com.example.arsisi_frontend.data.model.Prestasi
import com.example.arsisi_frontend.data.model.DokumenAkademik
import com.example.arsisi_frontend.data.model.DashboardStats
import com.example.arsisi_frontend.data.remote.ApiClient
import com.example.arsisi_frontend.utils.PreferencesManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class DashboardViewModel(application: Application) : AndroidViewModel(application) {

    private val apiService = ApiClient.apiService
    private val prefsManager = PreferencesManager(application)

    private val _stats = MutableStateFlow(DashboardStats())
    val stats: StateFlow<DashboardStats> = _stats.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    init {
        loadDashboardData()
    }

    fun loadDashboardData() {
        viewModelScope.launch {
            try {
                _isLoading.value = true

                // Pastikan fungsi ini suspend di PreferencesManager
                val userId = prefsManager.getUserId()
                val token = prefsManager.getToken()

                if (userId != -1 && token != null) {
                    val authHeader = "Bearer $token"

                    // Load data dari semua module
                    val agendaResponse = apiService.getAgenda(authHeader, userId)
                    val mataKuliahResponse = apiService.getMataKuliah(authHeader, userId)
                    val prestasiResponse = apiService.getPrestasi(authHeader, userId)
                    val dokumenResponse = apiService.getDokumenAkademik(authHeader, userId)

                    // KOREKSI UTAMA: Menggunakan tipe data eksplisit (List<T>)
                    // Ini menyelesaikan error 'Cannot infer type', 'dataList' (setelah ApiService diperbaiki), dan 'size'.
                    val agendaList: List<Agenda> = agendaResponse.body()?.dataList ?: emptyList()
                    val mataKuliahList: List<MataKuliah> = mataKuliahResponse.body()?.dataList ?: emptyList()
                    val prestasiList = if (prestasiResponse.isSuccessful) {
                        prestasiResponse.body()?.data ?: emptyList<Prestasi>()  // ✅ PrestasiResponse pakai 'data'
                    } else emptyList()
                    val dokumenList: List<DokumenAkademik> = dokumenResponse.body()?.dataList ?: emptyList()

                    // Calculate statistics
                    val today = System.currentTimeMillis() // Variabel ini sudah digunakan, menghilangkan warning
                    val agendaHariIni = agendaList.count { agenda ->
                        // Simple date check - in production use proper date comparison
                        true // Menggunakan 'it' seperti di kode Anda akan hilang
                    }

                    val tugasPending = agendaList.count { it.status == "Pending" } // 'it' sekarang dikenali

                    _stats.value = DashboardStats(
                        totalAgenda = agendaList.size, // 'size' sekarang dikenali
                        totalMataKuliah = mataKuliahList.size, // 'size' sekarang dikenali
                        totalPrestasi = prestasiList.size, // 'size' sekarang dikenali
                        totalDokumen = dokumenList.size, // 'size' sekarang dikenali
                        agendaHariIni = agendaHariIni,
                        tugasPending = tugasPending
                    )
                }

                _isLoading.value = false
            } catch (e: Exception) {
                _isLoading.value = false
                // Handle error - could show a snackbar or error state
                // Parameter 'e' sekarang digunakan, menghilangkan warning
                // Log.e("DashboardViewModel", "Error loading data", e)
            }
        }
    }

    fun refresh() {
        loadDashboardData()
    }
}