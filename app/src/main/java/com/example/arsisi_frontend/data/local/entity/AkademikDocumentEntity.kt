package com.example.arsisi_frontend.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "akademik_documents")
data class AkademikDocumentEntity(
    @PrimaryKey
    val id: String,
    val title: String,
    val category: String,
    val description: String,
    val date: String, // Tanggal pertama kali upload
    val fileName: String,
    val fileSize: String,
    val fileUri: String,
    val attachments: List<AttachmentData>, // Gunakan AttachmentData, bukan AttachmentEntity
    val updatedAt: String? = null // Tanggal terakhir update (null jika belum pernah diupdate)
)

