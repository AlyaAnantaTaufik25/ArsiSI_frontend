package com.example.arsisi_frontend.data.model

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize
import com.google.gson.annotations.SerializedName

// ==================== AUTH MODELS ====================



data class LoginRequest(
    val nim: String,
    val password: String
)

// Response utama dari API
data class LoginResponse(
    val success: Boolean,
    val message: String,
    val data: LoginData?
)

// Data yang berisi informasi pengguna (token, nama, dll.)
data class LoginData(
    val token: String,
    val nim: String,
    val nama: String
)

data class RegisterRequest(
    val nim: String,
    val nama: String,
    val email: String,
    val password: String,
    val angkatan: String,
    val jurusan: String
)

data class AuthResponse(
    val success: Boolean,
    val message: String,
    val data: UserData? = null,
    val token: String? = null
)

data class UserData(
    val id: Int,
    val nim: String,
    val nama: String,
    val email: String,
    val angkatan: String,
    val jurusan: String
)

// ==================== USER ENTITY ====================

@Entity(tableName = "user")
@Parcelize
data class User(
    @PrimaryKey
    val id: Int = 0,
    val nim: String,
    val nama: String,
    val email: String,
    val angkatan: String,
    val jurusan: String,
    val token: String? = null
) : Parcelable

// ==================== MATA KULIAH MODELS ====================

@Entity(tableName = "mata_kuliah")
@Parcelize
data class MataKuliah(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val userId: Int,
    val kode: String,
    val nama: String,
    val sks: Int,
    val semester: Int,
    val dosen: String,
    val ruangan: String? = null,
    val jadwal: String? = null,
    val createdAt: Long = System.currentTimeMillis()
) : Parcelable

data class MataKuliahRequest(
    val userId: Int,
    val kode: String,
    val nama: String,
    val sks: Int,
    val semester: Int,
    val dosen: String,
    val ruangan: String? = null,
    val jadwal: String? = null
)

data class MataKuliahResponse(
    val success: Boolean,
    val message: String,
    val data: MataKuliah? = null,
    val dataList: List<MataKuliah>? = null
)

// ==================== AGENDA MODELS ====================

@Entity(tableName = "agenda")
@Parcelize
data class Agenda(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val userId: Int,
    val judul: String,
    val deskripsi: String,
    val tanggal: String,
    val waktu: String,
    val kategori: String, // Tugas, Ujian, Meeting, Event
    val prioritas: String, // Rendah, Sedang, Tinggi
    val status: String = "Pending", // Pending, Selesai
    val reminder: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
) : Parcelable

data class AgendaRequest(
    val userId: Int,
    val judul: String,
    val deskripsi: String,
    val tanggal: String,
    val waktu: String,
    val kategori: String,
    val prioritas: String,
    val status: String = "Pending",
    val reminder: Boolean = false
)

data class AgendaResponse(
    val success: Boolean,
    val message: String,
    val data: Agenda? = null,
    val dataList: List<Agenda>? = null
)

// ==================== PRESTASI MODELS ====================

@Entity(tableName = "prestasi")
@Parcelize
data class Prestasi(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val userId: Int,
    val nama: String,
    val jenis: String, // Akademik, Non-Akademik, Organisasi
    val tingkat: String, // Lokal, Nasional, Internasional
    val tahun: Int,
    val penyelenggara: String,
    val deskripsi: String,
    val filePath: String? = null,
    val createdAt: Long = System.currentTimeMillis()
) : Parcelable

data class PrestasiRequest(
    val userId: Int,
    val nama: String,
    val jenis: String,
    val tingkat: String,
    val tahun: Int,
    val penyelenggara: String,
    val deskripsi: String,
    val filePath: String? = null
)

data class PrestasiResponse(
    val success: Boolean,
    val message: String,
    @SerializedName("data")
    val data: List<Prestasi>? = null

)

// ==================== DOKUMEN AKADEMIK MODELS ====================

@Entity(tableName = "dokumen_akademik")
@Parcelize
data class DokumenAkademik(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val userId: Int,
    val judul: String,
    val jenis: String, // KHS, Transkrip, Sertifikat, Surat
    val semester: Int? = null,
    val tahun: Int,
    val filePath: String,
    val ukuranFile: Long,
    val createdAt: Long = System.currentTimeMillis()
) : Parcelable

data class DokumenAkademikRequest(
    val userId: Int,
    val judul: String,
    val jenis: String,
    val semester: Int? = null,
    val tahun: Int,
    val filePath: String,
    val ukuranFile: Long
)

data class DokumenAkademikResponse(
    val success: Boolean,
    val message: String,
    val data: DokumenAkademik? = null,
    val dataList: List<DokumenAkademik>? = null
)

// ==================== DASHBOARD STATISTICS ====================

data class DashboardStats(
    val totalAgenda: Int = 0,
    val totalMataKuliah: Int = 0,
    val totalPrestasi: Int = 0,
    val totalDokumen: Int = 0,
    val agendaHariIni: Int = 0,
    val tugasPending: Int = 0
)





// ============ ARSIP MODELS ============

@Entity(tableName = "arsip")
data class Arsip(
    @PrimaryKey(autoGenerate = true)
    @SerializedName("arsip_id")
    val arsipId: Int = 0,

    @SerializedName("mahasiswa_id")
    val mahasiswaId: Int,

    @SerializedName("kategori")
    val kategori: String, // "PRESTASI", "SERTIFIKAT", "ORGANISASI"

    @SerializedName("judul")
    val judul: String,

    @SerializedName("deskripsi")
    val deskripsi: String,

    @SerializedName("tanggal")
    val tanggal: String, // Format: YYYY-MM-DD

    @SerializedName("file_path")
    val filePath: String,

    @SerializedName("created_at")
    val createdAt: String = ""
)

// Request untuk create/update arsip
data class ArsipRequest(
    @SerializedName("mahasiswa_id")
    val mahasiswaId: Int,

    @SerializedName("kategori")
    val kategori: String,

    @SerializedName("judul")
    val judul: String,

    @SerializedName("deskripsi")
    val deskripsi: String,

    @SerializedName("tanggal")
    val tanggal: String,

    @SerializedName("file_path")
    val filePath: String? = null
)

// Response untuk list arsip
data class ArsipListResponse(
    @SerializedName("success")
    val success: Boolean,

    @SerializedName("data")
    val data: List<Arsip>,

    @SerializedName("message")
    val message: String? = null
)

// Response untuk single arsip
data class ArsipResponse(
    @SerializedName("success")
    val success: Boolean,

    @SerializedName("data")
    val data: Arsip?,

    @SerializedName("message")
    val message: String? = null
)

// Response untuk delete/update
data class BaseResponse(
    @SerializedName("success")
    val success: Boolean,

    @SerializedName("message")
    val message: String
)

// ============ AUTH MODELS ============





// ============ STATISTIK MODELS ============

data class ArsipStatistik(
    val totalArsip: Int = 0,
    val totalPrestasi: Int = 0,
    val totalSertifikat: Int = 0,
    val totalOrganisasi: Int = 0
)

data class PrestasiStatistikResponse(
    @SerializedName("success")
    val success: Boolean,

    @SerializedName("data")
    val data: ArsipStatistik?,

    @SerializedName("message")
    val message: String? = null
)

// ============ KATEGORI ENUM ============

enum class KategoriArsip(val displayName: String, val value: String) {
    SEMUA("Semua", "SEMUA"),
    PRESTASI("Prestasi", "PRESTASI"),
    SERTIFIKAT("Sertifikat", "SERTIFIKAT"),
    ORGANISASI("Organisasi", "ORGANISASI");

    companion object {
        fun fromValue(value: String): KategoriArsip {
            return values().find { it.value == value } ?: SEMUA
        }
    }
}

// ============ UI STATE MODELS ============

sealed class UiState<out T> {
    object Idle : UiState<Nothing>()
    object Loading : UiState<Nothing>()
    data class Success<T>(val data: T) : UiState<T>()
    data class Error(val message: String) : UiState<Nothing>()
}

// ============ FILE UPLOAD MODELS ============

data class FileUploadRequest(
    val file: ByteArray,
    val fileName: String,
    val mimeType: String
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as FileUploadRequest

        if (!file.contentEquals(other.file)) return false
        if (fileName != other.fileName) return false
        if (mimeType != other.mimeType) return false

        return true
    }

    override fun hashCode(): Int {
        var result = file.contentHashCode()
        result = 31 * result + fileName.hashCode()
        result = 31 * result + mimeType.hashCode()
        return result
    }
}

data class FileUploadResponse(
    @SerializedName("success")
    val success: Boolean,

    @SerializedName("file_path")
    val filePath: String?,

    @SerializedName("message")
    val message: String
)
