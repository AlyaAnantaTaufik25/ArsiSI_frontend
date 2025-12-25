package com.example.arsisi_frontend.data.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

// ==================== USER MODEL ====================
@Parcelize
data class User(
    val id: Int = 0,
    val nim: String,
    val nama: String,
    val email: String = "",
    val angkatan: String = ""
) : Parcelable

// ==================== DASHBOARD STATS ====================
data class DashboardStats(
    val totalAgenda: Int = 0,
    val totalMataKuliah: Int = 0,
    val totalDokumen: Int = 0,
    val totalPrestasi: Int = 0
)

// ==================== ATTACHMENT MODEL ====================
@Parcelize
data class Attachment(
    val file_name: String,      // Sesuai backend
    val file_size: String,       // Sesuai backend
    val file_path: String,       // Sesuai backend (dulu fileUri)
    val description: String = "" // Nama/keterangan file lampiran
) : Parcelable

// ==================== AKADEMIK DOCUMENT MODEL ====================
@Parcelize
data class AkademikDocument(
    val dokumen_id: Int = 0,           // Sesuai backend (dulu id: String)
    val mahasiswa_id: Int = 0,         // TAMBAH - dari modul login
    val judul: String,                 // Sesuai backend (dulu title)
    val kategori: String,              // Sesuai backend (dulu category)
    val deskripsi: String,             // Sesuai backend (dulu description)
    val tanggal: String,               // Sesuai backend (dulu date)
    val file_name: String = "File_Belum_Dipilih.pdf",  // Sesuai backend (dulu fileName)
    val file_size: String = "0 KB",    // Sesuai backend (dulu fileSize)
    val file_path: String = "",        // Sesuai backend (dulu fileUri)
    val attachments: List<Attachment> = emptyList(),
    val created_at: String = "",       // TAMBAH - timestamp create
    val updated_at: String? = null     // Sesuai backend (dulu updatedAt)
) : Parcelable