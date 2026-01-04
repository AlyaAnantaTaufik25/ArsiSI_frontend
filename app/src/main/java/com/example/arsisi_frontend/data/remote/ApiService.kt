package com.example.arsisi_frontend.data.remote
import com.example.arsisi_frontend.data.remote.dto.*
import com.example.arsisi_frontend.data.model.*
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.*

interface ApiService {
    // Auth endpoints
    @POST("auth/register")
    suspend fun register(@Body request: RegisterRequest): Response<AuthResponse>

    @POST("auth/login")
    suspend fun login(@Body request: LoginRequest): Response<AuthResponse>

    @GET("auth/profile")
    suspend fun getProfile(@Header("Authorization") token: String): Response<AuthResponse>

    // Agenda endpoints
    @GET("agenda")
    suspend fun getAllAgenda(@Header("Authorization") token: String): Response<List<Agenda>>

    @Multipart
    @POST("agenda")
    suspend fun createAgenda(
        @Header("Authorization") token: String,
        @Part("judul") judul: RequestBody,
        @Part("deskripsi") deskripsi: RequestBody?,
        @Part("tanggal") tanggal: RequestBody,
        @Part("waktu") waktu: RequestBody?,
        @Part("kategori") kategori: RequestBody?,
        @Part("prioritas") prioritas: RequestBody?,
        @Part("reminder_setting") reminderSetting: RequestBody?,
        @Part file: MultipartBody.Part?
    ): Response<ApiResponse>

    @Multipart
    @PUT("agenda/{id}")
    suspend fun updateAgenda(
        @Header("Authorization") token: String,
        @Path("id") id: Int,
        @Part("judul") judul: RequestBody,
        @Part("deskripsi") deskripsi: RequestBody?,
        @Part("tanggal") tanggal: RequestBody,
        @Part("waktu") waktu: RequestBody?,
        @Part("kategori") kategori: RequestBody?,
        @Part("prioritas") prioritas: RequestBody?,
        @Part("reminder_setting") reminderSetting: RequestBody?,
        @Part file: MultipartBody.Part?
    ): Response<ApiResponse>

    @DELETE("agenda/{id}")
    suspend fun deleteAgenda(@Header("Authorization") token: String, @Path("id") id: Int): Response<ApiResponse>

    // All other endpoints removed - only agenda features maintained
}