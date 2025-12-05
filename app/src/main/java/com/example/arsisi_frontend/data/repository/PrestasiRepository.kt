package com.example.arsisi_frontend.data.repository

import android.content.Context
import android.net.Uri
import com.example.arsisi_frontend.data.model.*
import com.example.arsisi_frontend.data.remote.ApiClient
import com.example.arsisi_frontend.utils.Constants
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

/**
 * Repository untuk mengelola data Prestasi.
 * Semua fungsi membungkus hasil dengan Result<T>.
 */
class PrestasiRepository(private val context: Context) {

    private val apiService = ApiClient.apiService
    // TODO: ganti dengan token asli dari sistem auth / PreferencesManager
    private val TOKEN_PLACEHOLDER = "Bearer "

    /**
     * Mengambil daftar prestasi berdasarkan ID pengguna.
     */
    suspend fun getPrestasiList(userId: Int): Result<List<Prestasi>> {
        return withContext(Dispatchers.IO) {
            try {
                val response = apiService.getPrestasi("Bearer $TOKEN_PLACEHOLDER", userId)

                if (response.isSuccessful) {
                    val body = response.body()
                    if (body?.success == true) {
                        Result.success(body.data ?: emptyList())
                    } else {
                        Result.failure(Exception(body?.message ?: "Server error"))
                    }
                } else {
                    Result.failure(Exception("Error ${response.code()}: ${response.message()}"))
                }
            } catch (e: IOException) {
                Result.failure(Exception("Network error"))
            } catch (e: Exception) {
                Result.failure(Exception(e.message ?: "Unknown error"))
            }
        }
    }

    /**
     * Mengambil satu prestasi berdasarkan ID.
     */
    suspend fun getPrestasiById(prestasiId: Int): Result<Prestasi> {
        return withContext(Dispatchers.IO) {
            try {
                val response = apiService.getPrestasiById(TOKEN_PLACEHOLDER, prestasiId)

                if (response.isSuccessful) {
                    val body = response.body()
                    if (body?.success == true) {
                        val list = body.data ?: emptyList()
                        if (list.isNotEmpty()) {
                            Result.success(list.first())
                        } else {
                            Result.failure(Exception("Data not found"))
                        }
                    } else {
                        Result.failure(Exception(body?.message ?: Constants.ERROR_SERVER))
                    }
                } else {
                    Result.failure(Exception("Error ${response.code()}: ${response.message()}"))
                }
            } catch (e: IOException) {
                Result.failure(Exception(Constants.ERROR_NETWORK))
            } catch (e: Exception) {
                Result.failure(Exception(e.message ?: Constants.ERROR_GENERIC))
            }
        }
    }

    /**
     * Membuat prestasi baru.
     */
    suspend fun createPrestasi(request: PrestasiRequest): Result<Prestasi> {
        return withContext(Dispatchers.IO) {
            try {
                val response = apiService.createPrestasi(TOKEN_PLACEHOLDER, request)

                if (response.isSuccessful) {
                    val body = response.body()
                    if (body?.success == true) {
                        val list = body.data ?: emptyList()
                        if (list.isNotEmpty()) {
                            Result.success(list.first())
                        } else {
                            Result.failure(Exception("Failed to create prestasi"))
                        }
                    } else {
                        Result.failure(Exception(body?.message ?: Constants.ERROR_SERVER))
                    }
                } else {
                    Result.failure(Exception("Error ${response.code()}: ${response.message()}"))
                }
            } catch (e: IOException) {
                Result.failure(Exception(Constants.ERROR_NETWORK))
            } catch (e: Exception) {
                Result.failure(Exception(e.message ?: Constants.ERROR_GENERIC))
            }
        }
    }

    /**
     * Mencari prestasi berdasarkan kata kunci dan kategori.
     */
    suspend fun searchPrestasi(
        userId: Int,
        query: String,
        kategori: String? = null
    ): Result<List<Prestasi>> {
        return withContext(Dispatchers.IO) {
            try {
                val response = apiService.searchPrestasi(
                    "Bearer $TOKEN_PLACEHOLDER",
                    userId,
                    query,
                    kategori
                )

                if (response.isSuccessful) {
                    val body = response.body()
                    if (body?.success == true) {
                        Result.success(body.data ?: emptyList())
                    } else {
                        Result.failure(Exception(body?.message ?: "Search server error"))
                    }
                } else {
                    Result.failure(Exception("Error ${response.code()}: ${response.message()}"))
                }
            } catch (e: IOException) {
                Result.failure(Exception("Network error during search"))
            } catch (e: Exception) {
                Result.failure(Exception(e.message ?: "Unknown search error"))
            }
        }
    }

    /**
     * Memperbarui prestasi yang sudah ada.
     */
    suspend fun updatePrestasi(
        prestasiId: Int,
        request: PrestasiRequest
    ): Result<Prestasi> {
        return withContext(Dispatchers.IO) {
            try {
                val response = apiService.updatePrestasi(
                    TOKEN_PLACEHOLDER,
                    prestasiId,
                    request
                )

                if (response.isSuccessful) {
                    val body = response.body()
                    if (body?.success == true) {
                        val list = body.data ?: emptyList()
                        if (list.isNotEmpty()) {
                            Result.success(list.first())
                        } else {
                            Result.failure(Exception("Failed to update prestasi"))
                        }
                    } else {
                        Result.failure(Exception(body?.message ?: Constants.ERROR_SERVER))
                    }
                } else {
                    Result.failure(Exception("Error ${response.code()}: ${response.message()}"))
                }
            } catch (e: IOException) {
                Result.failure(Exception(Constants.ERROR_NETWORK))
            } catch (e: Exception) {
                Result.failure(Exception(e.message ?: Constants.ERROR_GENERIC))
            }
        }
    }

    /**
     * Menghapus prestasi.
     */
    suspend fun deletePrestasi(prestasiId: Int): Result<String> {
        return withContext(Dispatchers.IO) {
            try {
                val response = apiService.deletePrestasi(TOKEN_PLACEHOLDER, prestasiId)

                if (response.isSuccessful) {
                    val body = response.body()
                    if (body?.success == true) {
                        Result.success(body.message ?: Constants.SUCCESS_DELETE)
                    } else {
                        Result.failure(Exception(body?.message ?: Constants.ERROR_SERVER))
                    }
                } else {
                    Result.failure(Exception("Error ${response.code()}: ${response.message()}"))
                }
            } catch (e: IOException) {
                Result.failure(Exception(Constants.ERROR_NETWORK))
            } catch (e: Exception) {
                Result.failure(Exception(e.message ?: Constants.ERROR_GENERIC))
            }
        }
    }

    /**
     * Mengambil statistik prestasi per user.
     * Pastikan model dan endpoint API sesuai dengan project-mu.
     */
    suspend fun getStatistik(userId: Int): Result<ArsipStatistik> {
        return withContext(Dispatchers.IO) {
            try {
                val response = apiService.getPrestasiStatistik(
                    "Bearer $TOKEN_PLACEHOLDER",
                    userId
                )

                if (response.isSuccessful) {
                    val body = response.body()
                    if (body?.success == true && body.data != null) {
                        Result.success(body.data)
                    } else {
                        Result.failure(Exception(body?.message ?: "Failed to load statistik"))
                    }
                } else {
                    Result.failure(Exception("Error ${response.code()}: ${response.message()}"))
                }
            } catch (e: IOException) {
                Result.failure(Exception("Network error"))
            } catch (e: Exception) {
                Result.failure(Exception(e.message ?: "Unknown error"))
            }
        }
    }

    /**
     * Upload file (image atau PDF).
     */
    suspend fun uploadFile(uri: Uri, fileType: String): Result<String> {
        return withContext(Dispatchers.IO) {
            try {
                val inputStream = context.contentResolver.openInputStream(uri)
                    ?: return@withContext Result.failure(Exception("Cannot open file"))

                val fileName =
                    "upload_${System.currentTimeMillis()}.${getFileExtension(uri)}"
                val tempFile = File(context.cacheDir, fileName)

                FileOutputStream(tempFile).use { output ->
                    inputStream.copyTo(output)
                }
                inputStream.close()

                if (tempFile.length() > Constants.MAX_FILE_SIZE) {
                    tempFile.delete()
                    return@withContext Result.failure(Exception(Constants.ERROR_FILE_TOO_LARGE))
                }

                val requestFile =
                    tempFile.asRequestBody(getMimeType(uri).toMediaTypeOrNull())
                val body = MultipartBody.Part.createFormData(
                    "file",
                    tempFile.name,
                    requestFile
                )
                val typeBody =
                    fileType.toRequestBody("text/plain".toMediaTypeOrNull())

                val response = apiService.uploadFile(
                    TOKEN_PLACEHOLDER,
                    body,
                    typeBody
                )

                tempFile.delete()

                if (response.isSuccessful) {
                    val bodyResp = response.body()
                    if (bodyResp?.success == true && bodyResp.filePath != null) {
                        Result.success(bodyResp.filePath)
                    } else {
                        Result.failure(Exception(bodyResp?.message ?: Constants.ERROR_SERVER))
                    }
                } else {
                    Result.failure(Exception("Error ${response.code()}: ${response.message()}"))
                }
            } catch (e: IOException) {
                Result.failure(Exception(Constants.ERROR_NETWORK))
            } catch (e: Exception) {
                Result.failure(Exception(e.message ?: Constants.ERROR_GENERIC))
            }
        }
    }

    // --- Helper functions ---

    private fun getFileExtension(uri: Uri): String {
        return context.contentResolver.getType(uri)?.let { mimeType ->
            when {
                mimeType.contains("pdf") -> "pdf"
                mimeType.contains("jpeg") || mimeType.contains("jpg") -> "jpg"
                mimeType.contains("png") -> "png"
                else -> "jpg"
            }
        } ?: "jpg"
    }

    private fun getMimeType(uri: Uri): String {
        return context.contentResolver.getType(uri) ?: "application/octet-stream"
    }
}
