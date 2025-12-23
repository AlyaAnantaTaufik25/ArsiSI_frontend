package com.example.arsisi_frontend.data.repository

import android.content.Context
import android.net.Uri
import com.example.arsisi_frontend.data.model.*
import com.example.arsisi_frontend.data.remote.ApiClient
import com.example.arsisi_frontend.utils.Constants
import com.example.arsisi_frontend.utils.PreferencesManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream

class PrestasiRepository(
    private val context: Context,
    private val prefs: PreferencesManager
) {

    private val apiService = ApiClient.apiService

    private suspend fun getAuthHeader(): String {
        val token = prefs.getToken() ?: ""
        return "Bearer $token"
    }

    // =========================================================
    // LIST - Pakai ARSIP API + Filter Prestasi
    // =========================================================

    suspend fun getPrestasiList(): Result<List<Prestasi>> = withContext(Dispatchers.IO) {
        try {
            val authHeader = getAuthHeader()
            // ✅ FIX: Pakai getArsip, bukan getPrestasi
            val response = apiService.getArsip(authHeader)

            if (response.isSuccessful) {
                val body = response.body()
                if (body?.success == true) {
                    val arsipList = body.data ?: emptyList()

                    // ✅ Filter prestasi dari arsip
                    val prestasiList = arsipList
                        .filter {
                            it.kategori.lowercase().contains("prestasi") ||
                                    it.kategori.lowercase().contains("prest")
                        }
                        .map { arsip ->
                            Prestasi(
                                id = arsip.arsipId,
                                userId = arsip.mahasiswaId,
                                nama = arsip.judul,
                                jenis = arsip.kategori,
                                tingkat = "Lokal", // Default
                                tahun = arsip.tanggal?.substring(0, 4)?.toIntOrNull() ?: 2024,
                                penyelenggara = "Penyelenggara", // Default
                                deskripsi = arsip.deskripsi ?: "",
                                filePath = arsip.filePath
                            )
                        }

                    Result.success(prestasiList)
                } else {
                    Result.failure(Exception(body?.message ?: "Gagal memuat prestasi"))
                }
            } else {
                Result.failure(Exception("Error ${response.code()}: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(Exception(e.message ?: Constants.ERROR_GENERIC))
        }
    }

    // =========================================================
    // CREATE - Pakai ArsipRequest (TANPA userId di body)
    // =========================================================

    suspend fun createPrestasi(
        judul: String,
        deskripsi: String,
        tanggal: String,
        kategori: String = "PRESTASI"
    ): Result<String> = withContext(Dispatchers.IO) {
        try {
            val authHeader = getAuthHeader()
            // ✅ FIX: Backend ambil ID dari token, bukan body
            val request = ArsipRequest(
                kategori = kategori,
                judul = judul,
                deskripsi = deskripsi,
                tanggal = tanggal
            )

            val response = apiService.createArsip(authHeader, request) // ✅ Arsip API

            if (response.isSuccessful) {
                val body = response.body()
                Result.success(body?.message ?: "Prestasi berhasil dibuat")
            } else {
                Result.failure(Exception("Error ${response.code()}: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(Exception(e.message ?: Constants.ERROR_GENERIC))
        }
    }

    // =========================================================
    // UPDATE - Pakai ArsipRequest
    // =========================================================

    suspend fun updatePrestasi(
        prestasiId: Int,
        judul: String,
        deskripsi: String,
        tanggal: String,
        kategori: String = "PRESTASI"
    ): Result<String> = withContext(Dispatchers.IO) {
        try {
            val authHeader = getAuthHeader()
            val request = ArsipRequest(
                kategori = kategori,
                judul = judul,
                deskripsi = deskripsi,
                tanggal = tanggal
            )

            val response = apiService.updateArsip(authHeader, prestasiId, request)

            if (response.isSuccessful) {
                val body = response.body()
                Result.success(body?.message ?: "Prestasi berhasil diupdate")
            } else {
                Result.failure(Exception("Error ${response.code()}: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(Exception(e.message ?: Constants.ERROR_GENERIC))
        }
    }

    // =========================================================
    // DELETE - Sudah benar
    // =========================================================

    suspend fun deletePrestasi(prestasiId: Int): Result<String> = withContext(Dispatchers.IO) {
        try {
            val authHeader = getAuthHeader()
            val response = apiService.deleteArsip(authHeader, prestasiId)

            if (response.isSuccessful) {
                val body = response.body()
                Result.success(body?.message ?: "Prestasi berhasil dihapus")
            } else {
                Result.failure(Exception("Error ${response.code()}: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(Exception(e.message ?: Constants.ERROR_GENERIC))
        }
    }

    // =========================================================
    // DETAIL - Cari dari list (karena tidak ada endpoint detail arsip)
    // =========================================================

    suspend fun getPrestasiById(prestasiId: Int): Result<Prestasi> = withContext(Dispatchers.IO) {
        try {
            val prestasiList = getPrestasiList()
            if (prestasiList.isSuccess) {
                val prestasi = prestasiList.getOrNull()?.find { it.id == prestasiId }
                if (prestasi != null) {
                    Result.success(prestasi)
                } else {
                    Result.failure(Exception("Prestasi tidak ditemukan"))
                }
            } else {
                Result.failure(Exception("Gagal memuat data prestasi"))
            }
        } catch (e: Exception) {
            Result.failure(Exception(e.message ?: Constants.ERROR_GENERIC))
        }
    }

    // =========================================================
    // STATISTIK - Dari prestasi list
    // =========================================================

    suspend fun getStatistik(): Result<ArsipStatistik> = withContext(Dispatchers.IO) {
        try {
            val prestasiListResult = getPrestasiList()
            if (prestasiListResult.isSuccess) {
                val prestasiList = prestasiListResult.getOrNull() ?: emptyList()
                val stats = ArsipStatistik(
                    totalArsip = prestasiList.size,
                    totalPrestasi = prestasiList.size,
                    totalSertifikat = 0,
                    totalOrganisasi = 0
                )
                Result.success(stats)
            } else {
                Result.failure(Exception("Gagal memuat statistik"))
            }
        } catch (e: Exception) {
            Result.failure(Exception(e.message ?: Constants.ERROR_GENERIC))
        }
    }

    // =========================================================
    // UPLOAD FILE - Sementara disable (pakai multer backend)
    // =========================================================

    /*
    suspend fun uploadFile(uri: Uri): Result<String> = withContext(Dispatchers.IO) {
        // Disable dulu, pakai multer di backend
        Result.failure(Exception("Upload file belum diimplementasi"))
    }
    */
}
