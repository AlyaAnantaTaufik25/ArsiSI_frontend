package com.example.arsisi_frontend.data.local.dao
import androidx.room.*
import com.example.arsisi_frontend.data.local.entity.AgendaEntity
import kotlinx.coroutines.flow.Flow
@Dao
interface AgendaDao {
    @Query("SELECT * FROM agendas ORDER BY tanggal DESC, waktu DESC")
    fun getAllAgendas(): Flow<List<AgendaEntity>>
    @Query("SELECT * FROM agendas WHERE agenda_id = :id")
    suspend fun getAgendaById(id: Int): AgendaEntity?
    @Query("SELECT * FROM agendas WHERE tanggal = :date ORDER BY waktu ASC")
    fun getAgendasByDate(date: String): Flow<List<AgendaEntity>>
    @Query("SELECT * FROM agendas WHERE kategori = :category ORDER BY tanggal DESC")
    fun getAgendasByCategory(category: String): Flow<List<AgendaEntity>>
    @Query("SELECT * FROM agendas WHERE prioritas = :priority ORDER BY tanggal DESC")
    fun getAgendasByPriority(priority: String): Flow<List<AgendaEntity>>
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAgenda(agenda: AgendaEntity)
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllAgendas(agendas: List<AgendaEntity>)
    @Update
    suspend fun updateAgenda(agenda: AgendaEntity)
    @Delete
    suspend fun deleteAgenda(agenda: AgendaEntity)
    @Query("DELETE FROM agendas WHERE agenda_id = :id")
    suspend fun deleteAgendaById(id: Int)
    @Query("DELETE FROM agendas")
    suspend fun deleteAllAgendas()
    @Query("SELECT COUNT(*) FROM agendas")
    suspend fun getAgendaCount(): Int
}