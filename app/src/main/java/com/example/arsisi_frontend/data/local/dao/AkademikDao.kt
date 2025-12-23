package com.example.arsisi_frontend.data.local.dao

import androidx.room.*
import com.example.arsisi_frontend.data.local.entity.AkademikDocumentEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface AkademikDao {
    
    @Query("SELECT * FROM akademik_documents ORDER BY date DESC")
    fun getAllDocuments(): Flow<List<AkademikDocumentEntity>>
    
    @Query("SELECT * FROM akademik_documents WHERE id = :id")
    suspend fun getDocumentById(id: String): AkademikDocumentEntity?
    
    @Query("SELECT * FROM akademik_documents WHERE category = :category ORDER BY date DESC")
    fun getDocumentsByCategory(category: String): Flow<List<AkademikDocumentEntity>>
    
    @Query("SELECT * FROM akademik_documents WHERE title LIKE '%' || :query || '%' OR description LIKE '%' || :query || '%' ORDER BY date DESC")
    fun searchDocuments(query: String): Flow<List<AkademikDocumentEntity>>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDocument(document: AkademikDocumentEntity)
    
    @Update
    suspend fun updateDocument(document: AkademikDocumentEntity)
    
    @Delete
    suspend fun deleteDocument(document: AkademikDocumentEntity)
    
    @Query("DELETE FROM akademik_documents WHERE id = :id")
    suspend fun deleteDocumentById(id: String)
}
