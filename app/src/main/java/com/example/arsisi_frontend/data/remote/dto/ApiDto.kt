package com.example.arsisi_frontend.data.remote.dto

import com.google.gson.annotations.SerializedName

// ==================== BASE RESPONSE ====================
data class BaseResponse(
    @SerializedName("success")
    val success: Boolean,
    @SerializedName("message")
    val message: String
)

// ==================== AUTH DTOs ====================
data class LoginRequest(
    @SerializedName("email")
    val email: String,
    @SerializedName("password")
    val password: String
)

data class LoginResponse(
    @SerializedName("message")
    val message: String,
    @SerializedName("token")
    val token: String
)

data class RegisterRequest(
    @SerializedName("nim")
    val nim: String,
    @SerializedName("nama")
    val nama: String,
    @SerializedName("email")
    val email: String,
    @SerializedName("password")
    val password: String
)

// ==================== DOKUMEN AKADEMIK DTOs ====================
data class DokumenListResponse(
    @SerializedName("success")
    val success: Boolean,
    @SerializedName("data")
    val data: List<AkademikDocumentDto>
)

data class DokumenDetailResponse(
    @SerializedName("success")
    val success: Boolean,
    @SerializedName("data")
    val data: AkademikDocumentDto
)

data class AkademikDocumentDto(
    @SerializedName("dokumen_id")
    val dokumen_id: Int,
    @SerializedName("mahasiswa_id")
    val mahasiswa_id: Int,
    @SerializedName("judul")
    val judul: String,
    @SerializedName("kategori")
    val kategori: String,
    @SerializedName("deskripsi")
    val deskripsi: String,
    @SerializedName("tanggal")
    val tanggal: String,
    @SerializedName("file_name")
    val file_name: String,
    @SerializedName("file_size")
    val file_size: String,
    @SerializedName("file_path")
    val file_path: String,
    @SerializedName("created_at")
    val created_at: String,
    @SerializedName("updated_at")
    val updated_at: String?
)

// ==================== ATTACHMENT DTOs ====================
data class AttachmentListResponse(
    @SerializedName("success")
    val success: Boolean,
    @SerializedName("data")
    val data: List<AttachmentDto>
)

data class AttachmentDto(
    @SerializedName("attachment_id")
    val attachment_id: Int,
    @SerializedName("dokumen_id")
    val dokumen_id: Int,
    @SerializedName("file_name")
    val file_name: String,
    @SerializedName("file_size")
    val file_size: String,
    @SerializedName("file_path")
    val file_path: String,
    @SerializedName("description")
    val description: String?,
    @SerializedName("created_at")
    val created_at: String
)
