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

    // GET: Melihat daftar tugas
    @GET("tugas/saya")
    suspend fun getTugasSaya(): List<Tugas>

    @GET("tugas/publik")
    suspend fun getTugasPublik(): List<Tugas>

    // GET: Melihat daftar mata kuliah
    @GET("mata-kuliah")
    suspend fun getAllMataKuliah(): List<MataKuliah>

    // POST: Membuat tugas baru
    @POST("tugas")
    suspend fun createTugas(@Body request: CreateTugasRequest): SimpleResponse

    @GET("tugas/{id}")
    suspend fun getTugasById(@Path("id") tugasId: Int): Tugas

    // PUT: Mengupdate tugas berdasarkan ID
    @PUT("tugas/{id}")
    suspend fun updateTugas(@Path("id") tugasId: Int, @Body request: CreateTugasRequest): SimpleResponse

    // DELETE: Menghapus tugas berdasarkan ID
    @DELETE("tugas/{id}")
    suspend fun deleteTugas(@Path("id") tugasId: Int): SimpleResponse
}