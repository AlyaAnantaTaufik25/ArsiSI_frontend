package com.example.arsisi_frontend.ui.dashboard

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.example.arsisi_frontend.data.model.DashboardStats
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * DashboardViewModel dengan mock data (Phase 1)
 * Tidak ada API call, langsung return mock statistics
 */
class DashboardViewModel(application: Application) : AndroidViewModel(application) {

    // Mock data statistics
    private val _stats = MutableStateFlow(
        DashboardStats(
            totalAgenda = 5,
            totalMataKuliah = 6,
            totalDokumen = 12,
            totalPrestasi = 3
        )
    )
    val stats: StateFlow<DashboardStats> = _stats.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()
}
