package com.example.arsisi_frontend.data.remote

import com.example.arsisi_frontend.data.remote.dto.*
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.*

interface ApiService {
    
    // ==================== AUTHENTICATION ====================
    @POST("api/auth/login")
    suspend fun login(@Body request: LoginRequest): LoginResponse
    
    @POST("api/auth/register")
    suspend fun register(@Body request: RegisterRequest): BaseResponse
    
    // ==================== DOKUMEN AKADEMIK ====================
    @GET("api/dokumen")
    suspend fun getAllDokumen(): DokumenListResponse
    
    @GET("api/dokumen/{id}")
    suspend fun getDokumenById(@Path("id") id: Int): DokumenDetailResponse
    
    @Multipart
    @POST("api/dokumen")
    suspend fun createDokumen(
        @Part("judul") judul: RequestBody,
        @Part("kategori") kategori: RequestBody,
        @Part("deskripsi") deskripsi: RequestBody,
        @Part("tanggal") tanggal: RequestBody,
        @Part file: MultipartBody.Part
    ): DokumenDetailResponse
    
    @Multipart
    @PUT("api/dokumen/{id}")
    suspend fun updateDokumen(
        @Path("id") id: Int,
        @Part("judul") judul: RequestBody,
        @Part("kategori") kategori: RequestBody,
        @Part("deskripsi") deskripsi: RequestBody,
        @Part("tanggal") tanggal: RequestBody,
        @Part file: MultipartBody.Part?
    ): BaseResponse
    
    @DELETE("api/dokumen/{id}")
    suspend fun deleteDokumen(@Path("id") id: Int): BaseResponse
    
    // ==================== ATTACHMENTS ====================
    @Multipart
    @POST("api/dokumen/{id}/attachments")
    suspend fun uploadAttachments(
        @Path("id") id: Int,
        @Part attachments: List<MultipartBody.Part>,
        @Part("descriptions") descriptions: RequestBody
    ): AttachmentListResponse
    
    @GET("api/dokumen/{id}/attachments")
    suspend fun getAttachments(@Path("id") id: Int): AttachmentListResponse
    
    @DELETE("api/dokumen/attachments/{attachmentId}")
    suspend fun deleteAttachment(@Path("attachmentId") id: Int): BaseResponse
}
