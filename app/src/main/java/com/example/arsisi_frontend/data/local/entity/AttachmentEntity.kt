package com.example.arsisi_frontend.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "attachments")
data class AttachmentEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val documentId: String, // Foreign key ke akademik_documents
    val fileName: String,
    val fileSize: String,
    val fileUri: String,
    val description: String = "" // Nama/keterangan file lampiran
)




