package com.example.arsisi_frontend.data.local.entity
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.ColumnInfo
@Entity(tableName = "agendas")
data class AgendaEntity(
    @PrimaryKey
    @ColumnInfo(name = "agenda_id")
    val id: Int,
    @ColumnInfo(name = "judul")
    val judul: String,
    @ColumnInfo(name = "deskripsi")
    val deskripsi: String?,
    @ColumnInfo(name = "tanggal")
    val tanggal: String,
    @ColumnInfo(name = "waktu")
    val waktu: String?,
    @ColumnInfo(name = "kategori")
    val kategori: String?,
    @ColumnInfo(name = "prioritas")
    val prioritas: String?,
    @ColumnInfo(name = "reminder_setting")
    val reminderSetting: String?,
    @ColumnInfo(name = "file_path")
    val filePath: String?,
    @ColumnInfo(name = "mahasiswa_id")
    val mahasiswaId: Int,
    @ColumnInfo(name = "created_at")
    val createdAt: String?,
    @ColumnInfo(name = "updated_at")
    val updatedAt: String?
)