package com.example.arsisi_frontend.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "matakuliah_cache")
data class MataKuliahEntity(
    @PrimaryKey
    val matakuliahId: Int,
    val mahasiswaId: Int?,
    val namaMatakuliah: String?,
    val kodeMatakuliah: String?,
    val lastUpdated: Long = System.currentTimeMillis()
)
