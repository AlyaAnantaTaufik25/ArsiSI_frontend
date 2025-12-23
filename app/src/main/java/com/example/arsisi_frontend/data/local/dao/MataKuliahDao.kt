package com.example.arsisi_frontend.data.local.dao

import androidx.room.*
import com.example.arsisi_frontend.data.local.entity.MataKuliahEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface MataKuliahDao {

    // ===== Observe (UI) =====
    @Query("SELECT * FROM matakuliah_cache ORDER BY namaMatakuliah ASC")
    fun observeAll(): Flow<List<MataKuliahEntity>>

    // ===== One-shot (sync / fallback) =====
    @Query("SELECT * FROM matakuliah_cache ORDER BY namaMatakuliah ASC")
    suspend fun getAllOnce(): List<MataKuliahEntity>

    @Query("SELECT COUNT(*) FROM matakuliah_cache")
    suspend fun count(): Int

    // ===== Write =====
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(items: List<MataKuliahEntity>)

    @Query("DELETE FROM matakuliah_cache")
    suspend fun clearAll()
}


