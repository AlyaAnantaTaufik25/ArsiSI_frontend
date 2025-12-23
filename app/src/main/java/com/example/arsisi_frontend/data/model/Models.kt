package com.example.arsisi_frontend.data.model

import com.google.gson.annotations.SerializedName

data class MataKuliah(
    @SerializedName("matakuliah_id") val matakuliahId: Int,
    @SerializedName("nama_matakuliah") val namaMatakuliah: String,
    @SerializedName("kode_matakuliah") val kodeMatakuliah: String,
    @SerializedName("mahasiswa_id") val mahasiswaId: Int
)

data class Tugas(
    @SerializedName("tugas_id")
    val tugasId: Int?,  // ✅ Nullable karena bisa null dari API

    @SerializedName("judul")
    val judul: String?,  // ✅ Alias untuk backward compatibility

    @SerializedName("deskripsi")
    val deskripsi: String?,

    @SerializedName("deadline")
    val deadline: String?,  // ✅ TAMBAH INI (deadline tugas)

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
    val user_id: Int?,  // ✅ TAMBAH INI

    @SerializedName("nama")
    val namaUser: String?,  // ✅ Konsisten dengan Entity

    // ✅ FIX: Ganti jadi created_at (snake_case sesuai database)
    @SerializedName("created_at")
    val created_at: String?,

    @SerializedName("updated_at")
    val updated_at: String?
)

data class CreateTugasRequest(
    val judul: String,
    val deskripsi: String,
    @SerializedName("link_tugas") val linkTugas: String?,
    val visibility: String,
    @SerializedName("matakuliah_id") val matakuliahId: Int,
    @SerializedName("tipe_tugas") val tipe_tugas: String
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
)

data class SimpleResponse(
    val message: String,
    @SerializedName("tugas_id") val tugasId: Int?,  // ✅ TAMBAH INI (ID tugas yang baru dibuat)
    @SerializedName("judul") val judulTugas: String?,
    @SerializedName("deadline") val deadline: String?,
    @SerializedName("created_at") val created_at: String?
)
