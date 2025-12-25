package com.example.arsisi_frontend.data.repository

import android.content.Context
import com.example.arsisi_frontend.data.local.AppDatabase
import com.example.arsisi_frontend.data.local.entity.AkademikDocumentEntity
import com.example.arsisi_frontend.data.model.AkademikDocument
import com.example.arsisi_frontend.data.model.Attachment
import com.example.arsisi_frontend.data.remote.ApiConfig
import com.example.arsisi_frontend.data.remote.dto.toDomainModel
import com.example.arsisi_frontend.utils.FileUploadHelper
import com.example.arsisi_frontend.utils.PreferencesManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

class AkademikRepository(
    private val database: AppDatabase,
    private val preferencesManager: PreferencesManager,
    private val context: Context
) {
    
    private val dao = database.akademikDao()
    
    // ==================== READ OPERATIONS ====================
    
    fun getAllDocuments(): Flow<List<AkademikDocument>> {
        return dao.getAllDocuments().map { entities ->
            entities.map { it.toDomainModel() }
        }
    }
    
    suspend fun syncDocuments(): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val token = preferencesManager.getToken()
            if (token == null) {
                return@withContext Result.failure(Exception("Not logged in"))
            }
            
            val apiService = ApiConfig.getApiService(token)
            val response = apiService.getAllDokumen()
            
            if (response.success) {
                val entities = response.data.map { dto ->
                    dto.toDomainModel().toEntity()
                }
                entities.forEach { dao.insertDocument(it) }
                Result.success(Unit)
            } else {
                Result.failure(Exception("Failed to sync"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    fun getDocumentById(id: Int): Flow<AkademikDocument?> {
        return dao.getAllDocuments().map { documents ->
            documents.find { it.dokumen_id == id }?.toDomainModel()
        }
    }
    
    suspend fun getDocumentByIdSync(id: Int): AkademikDocument? {
        return dao.getDocumentById(id)?.toDomainModel()
    }
    
    fun getDocumentsByCategory(category: String): Flow<List<AkademikDocument>> {
        return dao.getDocumentsByCategory(category).map { entities ->
            entities.map { it.toDomainModel() }
        }
    }
    
    fun searchDocuments(query: String): Flow<List<AkademikDocument>> {
        return dao.searchDocuments(query).map { entities ->
            entities.map { it.toDomainModel() }
        }
    }
    
    // ==================== CREATE OPERATION ====================
    
    suspend fun createDocument(
        judul: String,
        kategori: String,
        deskripsi: String,
        tanggal: String,
        fileUri: String,
        attachments: List<Attachment> = emptyList()
    ): Result<AkademikDocument> = withContext(Dispatchers.IO) {
        try {
            val token = preferencesManager.getToken()
            if (token == null) {
                return@withContext Result.failure(Exception("Not logged in"))
            }
            
            val filePart = FileUploadHelper.uriToMultipartBodyPart(
                context,
                android.net.Uri.parse(fileUri),
                "file"
            ) ?: return@withContext Result.failure(Exception("Failed to process file"))
            
            val judulBody = FileUploadHelper.createPartFromString(judul)
            val kategoriBody = FileUploadHelper.createPartFromString(kategori)
            val deskripsiBody = FileUploadHelper.createPartFromString(deskripsi)
            val tanggalBody = FileUploadHelper.createPartFromString(tanggal)
            
            val apiService = ApiConfig.getApiService(token)
            val response = apiService.createDokumen(
                judulBody, kategoriBody, deskripsiBody, tanggalBody, filePart
            )
            
            if (response.success) {
                val document = response.data.toDomainModel()
                
                // Upload attachments if any
                if (attachments.isNotEmpty()) {
                    try {
                        val uploadedAttachments = uploadAttachments(document.dokumen_id, attachments, token)
                        
                        // Save attachments to local database with backend file_path
                        val documentEntity = document.toEntity()
                        val attachmentDataList = uploadedAttachments.map { attachment ->
                            com.example.arsisi_frontend.data.local.entity.AttachmentData(
                                file_name = attachment.file_name,
                                file_size = attachment.file_size,
                                file_path = attachment.file_path,  // Use backend path
                                description = attachment.description
                            )
                        }
                        val updatedEntity = documentEntity.copy(attachments = attachmentDataList)
                        dao.insertDocument(updatedEntity)
                    } catch (e: Exception) {
                        // Log error but don't fail the whole operation
                        android.util.Log.e("AkademikRepository", "Failed to upload attachments: ${e.message}")
                        // Still save document without attachments
                        dao.insertDocument(document.toEntity())
                    }
                } else {
                    dao.insertDocument(document.toEntity())
                }
                
                Result.success(document)
            } else {
                Result.failure(Exception("Failed to create document"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    private suspend fun uploadAttachments(
        dokumenId: Int,
        attachments: List<Attachment>,
        token: String
    ): List<Attachment> {
        val attachmentParts = mutableListOf<okhttp3.MultipartBody.Part>()
        val descriptions = mutableListOf<String>()
        
        attachments.forEach { attachment ->
            val part = FileUploadHelper.uriToMultipartBodyPart(
                context,
                android.net.Uri.parse(attachment.file_path),
                "attachments"
            )
            if (part != null) {
                attachmentParts.add(part)
                descriptions.add(attachment.description ?: "")
            }
        }
        
        if (attachmentParts.isNotEmpty()) {
            val descriptionsJson = com.google.gson.Gson().toJson(descriptions)
            val descriptionsBody = FileUploadHelper.createPartFromString(descriptionsJson)
            
            val apiService = ApiConfig.getApiService(token)
            val response = apiService.uploadAttachments(dokumenId, attachmentParts, descriptionsBody)
            
            // Return attachments from backend response
            if (response.success && response.data != null) {
                return response.data.map { it.toDomainModel() }
            }
        }
        
        return emptyList()
    }
    
    // ==================== UPDATE OPERATION ====================
    
    suspend fun updateDocument(
        id: Int,
        judul: String,
        kategori: String,
        deskripsi: String,
        tanggal: String,
        fileUri: String? = null
    ): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val token = preferencesManager.getToken()
            if (token == null) {
                return@withContext Result.failure(Exception("Not logged in"))
            }
            
            // Get existing document with attachments before update
            val existingDoc = dao.getDocumentById(id)
            android.util.Log.d("AkademikRepository", "UPDATE: existingDoc attachments count = ${existingDoc?.attachments?.size ?: 0}")
            
            val judulBody = FileUploadHelper.createPartFromString(judul)
            val kategoriBody = FileUploadHelper.createPartFromString(kategori)
            val deskripsiBody = FileUploadHelper.createPartFromString(deskripsi)
            val tanggalBody = FileUploadHelper.createPartFromString(tanggal)
            
            val filePart = fileUri?.let {
                FileUploadHelper.uriToMultipartBodyPart(context, android.net.Uri.parse(it), "file")
            }
            
            val apiService = ApiConfig.getApiService(token)
            val response = apiService.updateDokumen(
                id, judulBody, kategoriBody, deskripsiBody, tanggalBody, filePart
            )
            
            if (response.success) {
                // Update local database directly, preserving attachments
                if (existingDoc != null) {
                    android.util.Log.d("AkademikRepository", "UPDATE: Preserving ${existingDoc.attachments.size} attachments")
                    // Update existing document with new data but keep attachments and file info
                    val updatedEntity = existingDoc.copy(
                        judul = judul,
                        kategori = kategori,
                        deskripsi = deskripsi,
                        tanggal = tanggal,
                        updated_at = java.text.SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", java.util.Locale.getDefault())
                            .format(java.util.Date())
                        // Keep existing file_path, file_name, file_size (backend doesn't return new values)
                        // Keep existing attachments
                    )
                    android.util.Log.d("AkademikRepository", "UPDATE: Updated entity attachments count = ${updatedEntity.attachments.size}")
                    dao.insertDocument(updatedEntity)
                    android.util.Log.d("AkademikRepository", "UPDATE: Document saved to database")
                }
                
                Result.success(Unit)
            } else {
                Result.failure(Exception(response.message))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    // ==================== DELETE OPERATION ====================
    
    suspend fun deleteDocument(id: Int): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val token = preferencesManager.getToken()
            if (token == null) {
                return@withContext Result.failure(Exception("Not logged in"))
            }
            
            val apiService = ApiConfig.getApiService(token)
            val response = apiService.deleteDokumen(id)
            
            if (response.success) {
                dao.deleteDocumentById(id)
                Result.success(Unit)
            } else {
                Result.failure(Exception(response.message))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    // ==================== MAPPERS ====================
    
    private fun AkademikDocumentEntity.toDomainModel() = AkademikDocument(
        dokumen_id = this.dokumen_id,
        mahasiswa_id = this.mahasiswa_id,
        judul = this.judul,
        kategori = this.kategori,
        deskripsi = this.deskripsi,
        tanggal = this.tanggal,
        file_name = this.file_name,
        file_size = this.file_size,
        file_path = this.file_path,
        attachments = this.attachments.map { att ->
            Attachment(
                file_name = att.file_name,
                file_size = att.file_size,
                file_path = att.file_path,
                description = att.description
            )
        },
        created_at = this.created_at,
        updated_at = this.updated_at
    )
    
    private fun AkademikDocument.toEntity() = AkademikDocumentEntity(
        dokumen_id = this.dokumen_id,
        mahasiswa_id = this.mahasiswa_id,
        judul = this.judul,
        kategori = this.kategori,
        deskripsi = this.deskripsi,
        tanggal = this.tanggal,
        file_name = this.file_name,
        file_size = this.file_size,
        file_path = this.file_path,
        attachments = this.attachments.map { att ->
            com.example.arsisi_frontend.data.local.entity.AttachmentData(
                file_name = att.file_name,
                file_size = att.file_size,
                file_path = att.file_path,
                description = att.description
            )
        },
        created_at = this.created_at,
        updated_at = this.updated_at
    )
}

