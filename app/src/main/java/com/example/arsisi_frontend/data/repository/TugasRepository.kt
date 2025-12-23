package com.example.arsisi_frontend.data.repository

import android.util.Log
import com.example.arsisi_frontend.data.local.dao.TugasDao
import com.example.arsisi_frontend.data.mapper.toEntity
import com.example.arsisi_frontend.data.mapper.toModel
import com.example.arsisi_frontend.data.model.CreateTugasRequest
import com.example.arsisi_frontend.data.model.SimpleResponse
import com.example.arsisi_frontend.data.model.Tugas
import com.example.arsisi_frontend.data.remote.ApiService
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class TugasRepository(
    private val apiService: ApiService,
    private val tugasDao: TugasDao
) {

    /* =======================
     * TUGAS SAYA (ONLINE FIRST)
     * ======================= */
    fun getTugasSaya(): Flow<List<Tugas>> = flow {
        try {
            Log.d("TugasRepository", "API: getTugasSaya")
            val response = apiService.getTugasSaya()

            // cache
            tugasDao.insertAll(response.map { it.toEntity() })

            // emit langsung dari API
            emit(response)

        } catch (e: Exception) {
            Log.e("TugasRepository", "API error, fallback cache", e)

            val cached = tugasDao.getTugasSayaOnce()
            emit(cached.map { it.toModel() })
        }
    }

    /* =======================
     * TUGAS PUBLIK
     * ======================= */
    fun getTugasPublik(): Flow<List<Tugas>> = flow {
        try {
            Log.d("TugasRepository", "API: getTugasPublik")
            val response = apiService.getTugasPublik()

            tugasDao.insertAll(response.map { it.toEntity() })
            emit(response)

        } catch (e: Exception) {
            Log.e("TugasRepository", "API error, fallback cache", e)

            val cached = tugasDao.getTugasPublikOnce()
            emit(cached.map { it.toModel() })
        }
    }

    /* =======================
     * DETAIL TUGAS
     * ======================= */
    fun getTugasById(tugasId: Int): Flow<Tugas> = flow {
        try {
            Log.d("TugasRepository", "API: getTugasById $tugasId")
            val response = apiService.getTugasById(tugasId)

            tugasDao.insertAll(listOf(response.toEntity()))
            emit(response)

        } catch (e: Exception) {
            Log.e("TugasRepository", "API error, fallback cache", e)

            val cached = tugasDao.getTugasByIdOnce(tugasId)
                ?: throw Exception("Data tidak ditemukan")

            emit(cached.toModel())
        }
    }

    /* =======================
     * CREATE
     * ======================= */
    fun createTugas(request: CreateTugasRequest): Flow<SimpleResponse> = flow {
        emit(apiService.createTugas(request))
    }

    /* =======================
     * UPDATE
     * ======================= */
    fun updateTugas(
        tugasId: Int,
        request: CreateTugasRequest
    ): Flow<SimpleResponse> = flow {
        emit(apiService.updateTugas(tugasId, request))
    }

    /* =======================
     * DELETE
     * ======================= */
    fun deleteTugas(tugasId: Int): Flow<SimpleResponse> = flow {
        val response = apiService.deleteTugas(tugasId)
        tugasDao.deleteTugasById(tugasId)
        emit(response)
    }
}
