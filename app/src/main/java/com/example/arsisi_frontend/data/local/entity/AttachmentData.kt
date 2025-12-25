package com.example.arsisi_frontend.data.local.entity

// Data class untuk TypeConverter (bukan Entity)
data class AttachmentData(
    val file_name: String,       // Sesuai backend
    val file_size: String,       // Sesuai backend
    val file_path: String,       // Sesuai backend
    val description: String = ""
)
