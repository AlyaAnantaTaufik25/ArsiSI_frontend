package com.example.arsisi_frontend.data.model

import java.util.UUID

data class Attachment(
    val fileName: String,
    val fileSize: String,
    val fileUri: String,
    val description: String = "" // Nama/keterangan file lampiran
)

data class AkademikDocument(
    val id: String = UUID.randomUUID().toString(),
    val title: String,
    val category: String,
    val description: String,
    val date: String, // Tanggal pertama kali upload
    val fileName: String = "File_Belum_Dipilih.pdf",
    val fileSize: String = "0 KB",
    val fileUri: String = "",
    val attachments: List<Attachment> = emptyList(),
    val updatedAt: String? = null // Tanggal terakhir update (null jika belum pernah diupdate)
)