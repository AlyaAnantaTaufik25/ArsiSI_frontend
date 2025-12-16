package com.example.arsisi_frontend.ui.dashboard

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.arsisi_frontend.data.model.*
import com.example.arsisi_frontend.data.remote.ApiClient
import com.example.arsisi_frontend.utils.PreferencesManager
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class DashboardStats(
    val totalAgenda: Int = 0,
    val totalMataKuliah: Int = 0,
    val totalDokumen: Int = 0,
    val totalPrestasi: Int = 0,
    val agendaHariIni: Int = 0,
    val tugasPending: Int = 0
)

class DashboardViewModel(application: Application) : AndroidViewModel(application) {

    private val apiService = ApiClient.apiService
    private val prefsManager = PreferencesManager(application)

    // ✅ INITIAL MOCK DATA - INSTANT UI
    private val _stats = MutableStateFlow(
        DashboardStats(
            totalAgenda = 5,
            totalMataKuliah = 6,
            totalDokumen = 12,
            totalPrestasi = 3,
            agendaHariIni = 2,
            tugasPending = 3
        )
    )
    val stats: StateFlow<DashboardStats> = _stats.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    // ✅ 2 FLAGS ANTI INFINITE LOOP
    private var isLoadingData = false
    private var isDataLoaded = false

    /**
     * Load dashboard data ONCE ONLY
     * Mock fallback + parallel API calls
     */
    fun loadDashboardData() {
        // ✅ PREVENT MULTIPLE CALLS
        if (isDataLoaded || isLoadingData) {
            Log.d("DashboardVM", "⏳ Skip - already loaded/loading")
            return
        }

        isLoadingData = true
        isDataLoaded = true  // Mark loaded immediately

        viewModelScope.launch {
            try {
                Log.d("DashboardVM", "🔄 Loading dashboard data...")
                _isLoading.value = true
                _error.value = null

                val userId = prefsManager.getUserId()
                val token = prefsManager.getToken()

                if (userId == -1 || token == null) {
                    Log.w("DashboardVM", "⚠️ No auth - using mock data")
                    setMockData()
                    return@launch
                }

                val authHeader = "Bearer $token"

                // ✅ PARALLEL API CALLS - 4x CEPAT!
                val agendaDeferred = async { apiService.getAgenda(authHeader, userId) }
                val mataKuliahDeferred = async { apiService.getMataKuliah(authHeader, userId) }
                val prestasiDeferred = async { apiService.getPrestasi(authHeader, userId) }
                val dokumenDeferred = async { apiService.getDokumenAkademik(authHeader, userId) }

                // Await SEMUA bersamaan
                val agendaResponse = agendaDeferred.await()
                val mataKuliahResponse = mataKuliahDeferred.await()
                val prestasiResponse = prestasiDeferred.await()
                val dokumenResponse = dokumenDeferred.await()

                // Safe parsing
                val agendaList: List<Agenda> = agendaResponse.body()?.dataList ?: emptyList()
                val mataKuliahList: List<MataKuliah> = mataKuliahResponse.body()?.dataList ?: emptyList()
                val prestasiList: List<Prestasi> = prestasiResponse.body()?.data ?: emptyList()
                val dokumenList: List<DokumenAkademik> = dokumenResponse.body()?.dataList ?: emptyList()

                // Calculate stats
                val agendaHariIni = agendaList.count { /* TODO: date filter */ true }
                val tugasPending = agendaList.count { it.status == "Pending" }

                _stats.value = DashboardStats(
                    totalAgenda = agendaList.size,
                    totalMataKuliah = mataKuliahList.size,
                    totalDokumen = dokumenList.size,
                    totalPrestasi = prestasiList.size,
                    agendaHariIni = agendaHariIni,
                    tugasPending = tugasPending
                )

                Log.d("DashboardVM", "✅ SUCCESS: A=${agendaList.size}, MK=${mataKuliahList.size}, D=${dokumenList.size}, P=${prestasiList.size}")

            } catch (e: Exception) {
                Log.e("DashboardVM", "💥 API ERROR: ${e.message}", e)
                _error.value = "Gagal memuat data: ${e.message}"
                setMockData()  // Fallback mock data
            } finally {
                _isLoading.value = false
                isLoadingData = false
            }
        }
    }

    /** ✅ MOCK DATA - STABILITY GUARANTEE */
    private fun setMockData() {
        _stats.value = DashboardStats(
            totalAgenda = 5,
            totalMataKuliah = 6,
            totalDokumen = 12,
            totalPrestasi = 3,
            agendaHariIni = 2,
            tugasPending = 3
        )
        Log.d("DashboardVM", "📊 Using mock data")
    }

    /** Manual refresh - bypass loaded flag */
    fun refresh() {
        isDataLoaded = false  // Allow reload
        loadDashboardData()
    }

    fun clearError() {
        _error.value = null
    }
}
    