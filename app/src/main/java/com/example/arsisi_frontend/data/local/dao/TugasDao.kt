package com.example.arsisi_frontend.data.local.dao

import androidx.room.*
import com.example.arsisi_frontend.data.local.entity.TugasEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TugasDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(tugas: List<TugasEntity>)

    @Query("SELECT * FROM tugas_cache")
    suspend fun getTugasSayaOnce(): List<TugasEntity>

    @Query("SELECT * FROM tugas_cache WHERE visibility = 'Publik'")
    suspend fun getTugasPublikOnce(): List<TugasEntity>

    @Query("SELECT * FROM tugas_cache WHERE tugasId = :id LIMIT 1")
    suspend fun getTugasByIdOnce(id: Int): TugasEntity?

    @Query("DELETE FROM tugas_cache WHERE tugasId = :id")
    suspend fun deleteTugasById(id: Int)
}
