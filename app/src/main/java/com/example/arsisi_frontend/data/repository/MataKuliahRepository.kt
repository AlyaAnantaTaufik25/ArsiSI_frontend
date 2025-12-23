package com.example.arsisi_frontend.data.repository

import android.util.Log
import com.example.arsisi_frontend.data.local.dao.MataKuliahDao
import com.example.arsisi_frontend.data.mapper.toEntity
import com.example.arsisi_frontend.data.mapper.toModel
import com.example.arsisi_frontend.data.model.MataKuliah
import com.example.arsisi_frontend.data.remote.ApiService
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class MataKuliahRepository(
    private val apiService: ApiService,
    private val mataKuliahDao: MataKuliahDao
) {

    fun getAllMataKuliah(): Flow<List<MataKuliah>> = flow {
        try {
            Log.d("MataKuliahRepo", "Mulai fetching API...")
            val apiData = apiService.getAllMataKuliah()

            Log.d("MataKuliahRepo", "API berhasil, jumlah data: ${apiData.size}")

            // Proses simpan ke cache
            mataKuliahDao.clearAll()
            val entities = apiData.map { it.toEntity() }
            mataKuliahDao.insertAll(entities)

            Log.d("MataKuliahRepo", "Berhasil simpan ke Room database")
            emit(apiData)

        } catch (e: Exception) {

            // TAMBAHKAN INI:
            Log.e("MataKuliahRepo", "DETAIL ERROR: ${e.message}")
            e.printStackTrace()


            val cached = mataKuliahDao.getAllOnce()
            if (cached.isNotEmpty()) {
                Log.d("MataKuliahRepo", "Menggunakan data dari Cache")
                emit(cached.map { it.toModel() })
            } else {
                Log.e("MataKuliahRepo", "Database kosong dan API error!")
                emit(emptyList())
            }
        }
    }
}