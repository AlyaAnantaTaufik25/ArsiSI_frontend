package com.example.arsisi_frontend.data.model

import java.util.UUID

data class AkademikDocument(
    val id: String = UUID.randomUUID().toString(),
    val title: String,
    val category: String,
    val description: String,
    val date: String,
    val fileName: String = "File_Belum_Dipilih.pdf",
    val fileSize: String = "0 KB",
    val fileUri: String = "",
    val attachments: List<Triple<String, String, String>> = emptyList()
)