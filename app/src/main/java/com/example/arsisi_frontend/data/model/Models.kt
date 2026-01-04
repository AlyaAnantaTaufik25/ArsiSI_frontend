package com.example.arsisi_frontend.data.model
import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize
import com.google.gson.annotations.SerializedName
@Parcelize
data class Attachment(
    val file_name: String,
    val file_size: String,
    val file_path: String,
    val description: String? = ""
) : Parcelable
@Parcelize
data class AkademikDocument(
    val dokumen_id: Int = 0,
    val mahasiswa_id: Int = 0,
    val judul: String,
    val kategori: String,
    val deskripsi: String,
    val tanggal: String,
    val file_name: String = "",
    val file_size: String = "0 KB",
    val file_path: String = "",
    val attachments: List<Attachment> = emptyList(),
    val created_at: String = "",
    val updated_at: String? = null
) : Parcelable
data class LoginRequest(
    val nim: String,
    val password: String
)
data class LoginResponse(
    val success: Boolean,
    val message: String,
    val data: LoginData?
)
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
    val angkatan: String
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
    val angkatan: String
)
@Entity(tableName = "user")
@Parcelize
data class User(
    @PrimaryKey
    val id: Int = 0,
    val nim: String,
    val nama: String,
    val email: String,
    val angkatan: String,
    val token: String? = null
) : Parcelable
data class MataKuliah(
    @SerializedName("matakuliah_id") val matakuliahId: Int,
    @SerializedName("nama_matakuliah") val namaMatakuliah: String?,
    @SerializedName("kode_matakuliah") val kodeMatakuliah: String?,
    @SerializedName("mahasiswa_id") val mahasiswaId: Int? = null
)
data class Tugas(
    @SerializedName("tugas_id")
    val tugasId: Int?,
    @SerializedName("judul")
    val judul: String?,
    @SerializedName("deskripsi")
    val deskripsi: String?,
    @SerializedName("deadline")
    val deadline: String?,
    @SerializedName("link_tugas")
    val linkTugas: String?,
    @SerializedName("visibility")
    val visibility: String?,
    @SerializedName("matakuliah_id")
    val matakuliah_id: Int?,
    @SerializedName("tipe_tugas")
    val tipe_tugas: String?,
    @SerializedName("nama_matakuliah")
    val namaMatakuliah: String?,
    @SerializedName("kode_matakuliah")
    val kodeMatakuliah: String?,
    @SerializedName("mahasiswa_id")
    val user_id: Int?,
    @SerializedName("nama")
    val namaUser: String?,
    @SerializedName("created_at")
    val created_at: String?,
    @SerializedName("updated_at")
    val updated_at: String?
)
data class CreateTugasRequest(
    @SerializedName("judul")
    val judul: String,
    @SerializedName("deskripsi")
    val deskripsi: String,
    @SerializedName("link_tugas")
    val linkTugas: String?,
    @SerializedName("visibility")
    val visibility: String,
    @SerializedName("matakuliah_id")
    val matakuliahId: Int,
    @SerializedName("tipe_tugas")
    val tipe_tugas: String
)
data class TugasFormState(
    val isLoading: Boolean = false,
    val isSaving: Boolean = false,
    val mataKuliahList: List<MataKuliah> = emptyList(),
    val selectedMatkulId: Int? = null,
    val judul: String = "",
    val deskripsi: String = "",
    val linkTugas: String = "",
    val visibility: String = "Private",
    val tipe_tugas: String = "Individu",
    val errorMessage: String? = null,
    val successMessage: String? = null
) {
    val isMatkulLoading: Boolean get() = isLoading && mataKuliahList.isEmpty()
}
data class SimpleResponse(
    val success: Boolean? = true,
    val message: String,
    @SerializedName("tugas_id") val tugasId: Int? = null,
    @SerializedName("id") val id: Int? = null,
    @SerializedName("judul") val judulTugas: String? = null,
    @SerializedName("deadline") val deadline: String? = null,
    @SerializedName("created_at") val created_at: String? = null
)
data class Agenda(
    @SerializedName("agenda_id") val id: Int,
    @SerializedName("judul") val judul: String,
    @SerializedName("deskripsi") val deskripsi: String?,
    @SerializedName("tanggal") val tanggal: String,
    @SerializedName("waktu") val waktu: String?,
    @SerializedName("kategori") val kategori: String?,
    @SerializedName("prioritas") val prioritas: String?,
    @SerializedName("reminder_setting") val reminderSetting: String?,
    @SerializedName("file_path") val filePath: String?,
    @SerializedName("mahasiswa_id") val mahasiswaId: Int,
    @SerializedName("created_at") val createdAt: String?,
    @SerializedName("updated_at") val updatedAt: String?
)
data class ApiResponse(
    @SerializedName("message") val message: String
)
@Entity(tableName = "prestasi")
@Parcelize
data class Prestasi(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val userId: Int,
    val nama: String,
    val jenis: String,
    val tingkat: String,
    val tahun: Int,
    val penyelenggara: String,
    val deskripsi: String,
    val filePath: String? = null,
    val tanggal: String? = null,
    val fileSize: String? = null,
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
    val tanggal: String,
    val filePath: String? = null
)
data class PrestasiResponse(
    val success: Boolean,
    val message: String,
    @SerializedName("data")
    val data: List<Prestasi>? = null
)
@Entity(tableName = "dokumen_akademik")
@Parcelize
data class DokumenAkademik(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val userId: Int,
    val judul: String,
    val jenis: String,
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
data class DashboardStats(
    val totalAgenda: Int = 0,
    val totalMataKuliah: Int = 0,
    val totalPrestasi: Int = 0,
    val totalDokumen: Int = 0,
    val agendaHariIni: Int = 0,
    val tugasPending: Int = 0,
    val totalTugas: Int = 0
)
@Entity(tableName = "arsip")
data class Arsip(
    @PrimaryKey(autoGenerate = true)
    @SerializedName("arsip_id")
    val arsipId: Int = 0,
    @SerializedName("mahasiswa_id")
    val mahasiswaId: Int,
    @SerializedName("kategori")
    val kategori: String,
    @SerializedName("judul")
    val judul: String?,
    @SerializedName("deskripsi")
    val deskripsi: String?,
    @SerializedName("tanggal")
    val tanggal: String?,
    @SerializedName("file_path")
    val filePath: String?,
    @SerializedName("created_at")
    val createdAt: String? = null
)
data class ArsipRequest(
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
data class ArsipListResponse(
    @SerializedName("success")
    val success: Boolean,
    @SerializedName("data")
    val data: List<Arsip>,
    @SerializedName("message")
    val message: String? = null
)
data class ArsipResponse(
    @SerializedName("success")
    val success: Boolean,
    @SerializedName("data")
    val data: Arsip?,
    @SerializedName("message")
    val message: String? = null
)
data class BaseResponse(
    @SerializedName("success")
    val success: Boolean,
    @SerializedName("message")
    val message: String
)
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
enum class KategoriArsip(val displayName: String, val value: String) {
    SEMUA("Semua", "SEMUA"),
    PRESTASI("Prestasi", "PRESTASI"),
    SERTIFIKAT("Sertifikat", "SERTIFIKAT"),
    ORGANISASI("Organisasi", "ORGANISASI");
    companion object {
        fun fromValue(value: String): KategoriArsip {
            return entries.find { it.value == value } ?: SEMUA
        }
    }
}
sealed class UiState<out T> {
    object Idle : UiState<Nothing>()
    object Loading : UiState<Nothing>()
    data class Success<T>(val data: T) : UiState<T>()
    data class Error(val message: String) : UiState<Nothing>()
}
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
data class MahasiswaProfileResponse(
    @SerializedName("mahasiswa_id")
    val mahasiswaId: Int? = null,
    @SerializedName("nim")
    val nim: String? = null,
    @SerializedName("nama")
    val nama: String? = null,
    @SerializedName("email")
    val email: String? = null,
    @SerializedName("angkatan")
    val angkatan: Any? = null,
    @SerializedName("foto_profil")
    val fotoProfil: String? = null
)