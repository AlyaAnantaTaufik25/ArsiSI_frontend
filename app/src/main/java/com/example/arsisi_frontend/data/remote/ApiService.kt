package com.example.arsisi_frontend.data.remote

import com.example.arsisi_frontend.data.model.*
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.*

/**
 * Interface yang mendefinisikan semua endpoint API yang digunakan.
 * Semua fungsi harus suspend dan mengembalikan Response<ModelSpesifik>.
 */
interface ApiService {

    // ==================== AUTH ====================

    @POST("auth/register")
    suspend fun register(
        @Body request: RegisterRequest
    ): Response<AuthResponse>

    @POST("auth/login")
    suspend fun login(
        @Body request: LoginRequest
    ): Response<AuthResponse>

    @GET("auth/profile")
    suspend fun getProfile(
        @Header("Authorization") token: String
    ): Response<AuthResponse>

    // ==================== MATA KULIAH ====================

    @GET("mata-kuliah")
    suspend fun getMataKuliah(
        @Header("Authorization") token: String,
        @Query("user_id") userId: Int
    ): Response<MataKuliahResponse>

    @GET("mata-kuliah/{id}")
    suspend fun getMataKuliahById(
        @Header("Authorization") token: String,
        @Path("id") id: Int
    ): Response<MataKuliahResponse>

    @POST("mata-kuliah")
    suspend fun createMataKuliah(
        @Header("Authorization") token: String,
        @Body request: MataKuliahRequest
    ): Response<MataKuliahResponse>

    @PUT("mata-kuliah/{id}")
    suspend fun updateMataKuliah(
        @Header("Authorization") token: String,
        @Path("id") id: Int,
        @Body request: MataKuliahRequest
    ): Response<MataKuliahResponse>

    @DELETE("mata-kuliah/{id}")
    suspend fun deleteMataKuliah(
        @Header("Authorization") token: String,
        @Path("id") id: Int
    ): Response<MataKuliahResponse>

    // ==================== AGENDA ====================

    @GET("agenda")
    suspend fun getAgenda(
        @Header("Authorization") token: String,
        @Query("user_id") userId: Int
    ): Response<AgendaResponse>

    @GET("agenda/{id}")
    suspend fun getAgendaById(
        @Header("Authorization") token: String,
        @Path("id") id: Int
    ): Response<AgendaResponse>

    @POST("agenda")
    suspend fun createAgenda(
        @Header("Authorization") token: String,
        @Body request: AgendaRequest
    ): Response<AgendaResponse>

    @PUT("agenda/{id}")
    suspend fun updateAgenda(
        @Header("Authorization") token: String,
        @Path("id") id: Int,
        @Body request: AgendaRequest
    ): Response<AgendaResponse>

    @DELETE("agenda/{id}")
    suspend fun deleteAgenda(
        @Header("Authorization") token: String,
        @Path("id") id: Int
    ): Response<AgendaResponse>

    // ==================== PRESTASI ====================

    @GET("prestasi")
    suspend fun getPrestasi(
        @Header("Authorization") token: String,
        @Query("user_id") userId: Int
    ): Response<PrestasiResponse>

    @GET("prestasi/{id}")
    suspend fun getPrestasiById(
        @Header("Authorization") token: String,
        @Path("id") id: Int
    ): Response<PrestasiResponse>

    @POST("prestasi")
    suspend fun createPrestasi(
        @Header("Authorization") token: String,
        @Body request: PrestasiRequest
    ): Response<PrestasiResponse>

    @PUT("prestasi/{id}")
    suspend fun updatePrestasi(
        @Header("Authorization") token: String,
        @Path("id") id: Int,
        @Body request: PrestasiRequest      // ← perbaikan: tadinya ArsipRequest
    ): Response<PrestasiResponse>

    @DELETE("prestasi/{id}")
    suspend fun deletePrestasi(
        @Header("Authorization") token: String,
        @Path("id") id: Int
    ): Response<PrestasiResponse>

    @GET("prestasi/search")
    suspend fun searchPrestasi(
        @Header("Authorization") token: String,
        @Query("mahasiswa_id") userId: Int,
        @Query("query") query: String,
        @Query("kategori") kategori: String? = null
    ): Response<PrestasiResponse>

    // Statistik prestasi per user
    @GET("prestasi/statistik")
    suspend fun getPrestasiStatistik(
        @Header("Authorization") token: String,
        @Query("mahasiswa_id") userId: Int
    ): Response<PrestasiStatistikResponse>
    // ==================== FILE UPLOAD ENDPOINT ====================

    @Multipart
    @POST("file/upload")
    suspend fun uploadFile(
        @Header("Authorization") token: String,
        @Part file: MultipartBody.Part,
        @Part("type") type: RequestBody
    ): Response<FileUploadResponse>

    // ==================== DOKUMEN AKADEMIK ====================

    @GET("dokumen-akademik")
    suspend fun getDokumenAkademik(
        @Header("Authorization") token: String,
        @Query("user_id") userId: Int
    ): Response<DokumenAkademikResponse>

    @GET("dokumen-akademik/{id}")
    suspend fun getDokumenAkademikById(
        @Header("Authorization") token: String,
        @Path("id") id: Int
    ): Response<DokumenAkademikResponse>

    @POST("dokumen-akademik")
    suspend fun createDokumenAkademik(
        @Header("Authorization") token: String,
        @Body request: DokumenAkademikRequest
    ): Response<DokumenAkademikResponse>

    @PUT("dokumen-akademik/{id}")
    suspend fun updateDokumenAkademik(
        @Header("Authorization") token: String,
        @Path("id") id: Int,
        @Body request: DokumenAkademikRequest
    ): Response<DokumenAkademikResponse>

    @DELETE("dokumen-akademik/{id}")
    suspend fun deleteDokumenAkademik(
        @Header("Authorization") token: String,
        @Path("id") id: Int
    ): Response<DokumenAkademikResponse>
}
