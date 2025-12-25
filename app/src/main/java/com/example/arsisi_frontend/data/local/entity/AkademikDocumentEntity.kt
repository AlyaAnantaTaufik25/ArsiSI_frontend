package com.example.arsisi_frontend.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "akademik_documents")
data class AkademikDocumentEntity(
    @PrimaryKey(autoGenerate = true)
    val dokumen_id: Int = 0,           // Sesuai backend
    val mahasiswa_id: Int,             // TAMBAH - dari modul login
    val judul: String,                 // Sesuai backend
    val kategori: String,              // Sesuai backend
    val deskripsi: String,             // Sesuai backend
    val tanggal: String,               // Sesuai backend
    val file_name: String,             // Sesuai backend
    val file_size: String,             // Sesuai backend
    val file_path: String,             // Sesuai backend
    val attachments: List<AttachmentData>, // List attachment
    val created_at: String,            // TAMBAH - timestamp create
    val updated_at: String? = null     // Sesuai backend
)
