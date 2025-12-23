package com.example.arsisi_frontend.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "tugas_cache")
data class TugasEntity(
    @PrimaryKey
    val tugasId: Int,
    val judulTugas: String?,
    val deskripsi: String?,
    val deadline: String?,
    val linkTugas: String?,
    val visibility: String?,
    val tipe_tugas: String?,
    val matakuliah_id: Int?,
    val namaMatakuliah: String?,
    val kodeMatakuliah: String?,
    val user_id: Int?,
    val namaUser: String?,
    val created_at: String?,  // ✅ HARUS ADA
    val updated_at: String? = null,
    val lastUpdated: Long = System.currentTimeMillis()
)
