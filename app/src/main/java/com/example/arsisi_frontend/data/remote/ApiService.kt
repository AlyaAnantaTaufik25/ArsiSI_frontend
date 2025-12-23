package com.example.arsisi_frontend.data.remote

import com.example.arsisi_frontend.data.model.MataKuliah
import com.example.arsisi_frontend.data.model.Tugas
import com.example.arsisi_frontend.data.model.CreateTugasRequest
import com.example.arsisi_frontend.data.model.SimpleResponse
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface ApiService {

    @GET("tugas/saya")  // ✅ Hapus "api/"
    suspend fun getTugasSaya(): List<Tugas>

    @GET("tugas/publik")  // ✅ Hapus "api/"
    suspend fun getTugasPublik(): List<Tugas>

    @GET("mata-kuliah")  // ✅ Hapus "api/"
    suspend fun getAllMataKuliah(): List<MataKuliah>

    @GET("tugas/{id}")  // ✅ Hapus "api/"
    suspend fun getTugasById(@Path("id") tugasId: Int): Tugas

    @POST("tugas")  // ✅ Hapus "api/"
    suspend fun createTugas(@Body request: CreateTugasRequest): SimpleResponse

    @PUT("tugas/{id}")  // ✅ Hapus "api/"
    suspend fun updateTugas(@Path("id") tugasId: Int, @Body request: CreateTugasRequest): SimpleResponse

    @DELETE("tugas/{id}")  // ✅ Hapus "api/"
    suspend fun deleteTugas(@Path("id") tugasId: Int): SimpleResponse
}
