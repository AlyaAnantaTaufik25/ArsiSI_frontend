package com.example.arsisi_frontend.data.repository

import com.example.arsisi_frontend.data.local.AppDatabase
import com.example.arsisi_frontend.data.local.entity.AkademikDocumentEntity
import com.example.arsisi_frontend.data.model.AkademikDocument
import com.example.arsisi_frontend.data.model.Attachment
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class AkademikRepository(private val database: AppDatabase) {
    
    private val dao = database.akademikDao()
    
    fun getAllDocuments(): Flow<List<AkademikDocument>> {
        return dao.getAllDocuments().map { entities ->
            entities.map { it.toDomainModel() }
        }
    }
    
    fun getDocumentById(id: String): Flow<AkademikDocument?> {
        return dao.getAllDocuments().map { documents ->
            documents.find { it.id == id }?.toDomainModel()
        }
    }
    
    suspend fun getDocumentByIdSync(id: String): AkademikDocument? {
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
    
    suspend fun insertDocument(document: AkademikDocument) {
        dao.insertDocument(document.toEntity())
    }
    
    suspend fun updateDocument(document: AkademikDocument) {
        dao.updateDocument(document.toEntity())
    }
    
    suspend fun deleteDocument(document: AkademikDocument) {
        dao.deleteDocument(document.toEntity())
    }
    
    suspend fun deleteDocumentById(id: String) {
        dao.deleteDocumentById(id)
    }
    
    // Extension functions untuk konversi Entity <-> Domain Model
    private fun AkademikDocumentEntity.toDomainModel(): AkademikDocument {
        return AkademikDocument(
            id = this.id,
            title = this.title,
            category = this.category,
            description = this.description,
            date = this.date,
            fileName = this.fileName,
            fileSize = this.fileSize,
            fileUri = this.fileUri,
            attachments = this.attachments.map { 
                Attachment(
                    fileName = it.fileName,
                    fileSize = it.fileSize,
                    fileUri = it.fileUri,
                    description = it.description
                )
            },
            updatedAt = this.updatedAt
        )
    }
    
    private fun AkademikDocument.toEntity(): AkademikDocumentEntity {
        return AkademikDocumentEntity(
            id = this.id,
            title = this.title,
            category = this.category,
            description = this.description,
            date = this.date,
            fileName = this.fileName,
            fileSize = this.fileSize,
            fileUri = this.fileUri,
            attachments = this.attachments.map {
                com.example.arsisi_frontend.data.local.entity.AttachmentData(
                    fileName = it.fileName,
                    fileSize = it.fileSize,
                    fileUri = it.fileUri,
                    description = it.description
                )
            },
            updatedAt = this.updatedAt
        )
    }
}
