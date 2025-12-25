package com.example.arsisi_frontend.data.local.dao

import androidx.room.*
import com.example.arsisi_frontend.data.local.entity.AkademikDocumentEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface AkademikDao {
    
    @Query("SELECT * FROM akademik_documents ORDER BY tanggal DESC")
    fun getAllDocuments(): Flow<List<AkademikDocumentEntity>>
    
    @Query("SELECT * FROM akademik_documents WHERE dokumen_id = :id")
    suspend fun getDocumentById(id: Int): AkademikDocumentEntity?
    
    @Query("SELECT * FROM akademik_documents WHERE kategori = :category ORDER BY tanggal DESC")
    fun getDocumentsByCategory(category: String): Flow<List<AkademikDocumentEntity>>
    
    @Query("SELECT * FROM akademik_documents WHERE judul LIKE '%' || :query || '%' OR deskripsi LIKE '%' || :query || '%' ORDER BY tanggal DESC")
    fun searchDocuments(query: String): Flow<List<AkademikDocumentEntity>>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDocument(document: AkademikDocumentEntity)
    
    @Update
    suspend fun updateDocument(document: AkademikDocumentEntity)
    
    @Delete
    suspend fun deleteDocument(document: AkademikDocumentEntity)
    
    @Query("DELETE FROM akademik_documents WHERE dokumen_id = :id")
    suspend fun deleteDocumentById(id: Int)
}
