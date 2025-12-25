package com.example.arsisi_frontend.data.remote.dto

import com.example.arsisi_frontend.data.model.AkademikDocument
import com.example.arsisi_frontend.data.model.Attachment

// ==================== DTO TO DOMAIN MAPPERS ====================

fun AkademikDocumentDto.toDomainModel() = AkademikDocument(
    dokumen_id = this.dokumen_id,
    mahasiswa_id = this.mahasiswa_id,
    judul = this.judul,
    kategori = this.kategori,
    deskripsi = this.deskripsi,
    tanggal = this.tanggal,
    file_name = this.file_name,
    file_size = this.file_size,
    file_path = this.file_path,
    attachments = emptyList(), // Attachments loaded separately
    created_at = this.created_at,
    updated_at = this.updated_at
)

fun AttachmentDto.toDomainModel() = Attachment(
    file_name = this.file_name,
    file_size = this.file_size,
    file_path = this.file_path,
    description = this.description ?: ""
)

// Helper to get full URL from relative path
fun getFullFileUrl(relativePath: String, baseUrl: String = "http://10.0.2.2:5000/"): String {
    return if (relativePath.startsWith("http")) {
        relativePath
    } else {
        "$baseUrl$relativePath"
    }
}
