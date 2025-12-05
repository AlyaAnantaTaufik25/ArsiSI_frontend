package com.example.arsisi_frontend.data.repository

import com.example.arsisi_frontend.data.remote.ApiService
import com.example.arsisi_frontend.data.model.CreateTugasRequest
import com.example.arsisi_frontend.data.model.SimpleResponse
import com.example.arsisi_frontend.data.model.Tugas // Diperlukan untuk getTugasById
import kotlinx.coroutines.flow.flow

class TugasRepository(
    private val apiService: ApiService
) {
    fun getTugasSaya() = flow {
        val response = apiService.getTugasSaya()
        emit(response)
    }

    fun getTugasPublik() = flow {
        val response = apiService.getTugasPublik()
        emit(response)
    }

    fun createTugas(request: CreateTugasRequest) = flow {
        val response = apiService.createTugas(request)
        emit(response)
    }

    fun getTugasById(tugasId: Int) = flow {
        // NOTE: Asumsi Anda menambahkan endpoint ini di ApiService dan Express backend
        val response = apiService.getTugasById(tugasId)
        emit(response)
    }

    fun updateTugas(tugasId: Int, request: CreateTugasRequest) = flow {
        // NOTE: Asumsi Anda menambahkan endpoint ini di ApiService dan Express backend
        val response = apiService.updateTugas(tugasId, request)
        emit(response)
    }

    fun deleteTugas(tugasId: Int) = flow {
        val response = apiService.deleteTugas(tugasId)
        emit(response)
    }
}