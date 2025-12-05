package com.example.arsisi_frontend.data.repository

import com.example.arsisi_frontend.data.remote.ApiService
import kotlinx.coroutines.flow.flow

class MataKuliahRepository(
    private val apiService: ApiService
) {
    fun getAllMataKuliah() = flow {
        val response = apiService.getAllMataKuliah()
        emit(response)
    }
}