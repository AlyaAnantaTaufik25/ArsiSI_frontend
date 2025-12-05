package com.example.arsisi_frontend.data.model

import com.google.gson.annotations.SerializedName

data class MataKuliah(
    @SerializedName("matakuliah_id") val matakuliahId: Int,
    @SerializedName("nama_matakuliah") val namaMatakuliah: String,
    @SerializedName("kode_matakuliah") val kodeMatakuliah: String,
    @SerializedName("mahasiswa_id") val mahasiswaId: Int
)

data class Tugas(
    @SerializedName("tugas_id") val tugasId: Int,
    @SerializedName("judul") val judul: String,
    @SerializedName("deskripsi") val deskripsi: String,
    @SerializedName("link_tugas") val linkTugas: String?,
    @SerializedName("visibility") val visibility: String,
    @SerializedName("matakuliah_id") val matakuliah_id: Int?,
    @SerializedName("tipe_tugas") val tipe_tugas: String?,
    @SerializedName("nama_matakuliah") val namaMatakuliah: String?,
    @SerializedName("kode_matakuliah") val kodeMatakuliah: String?,
    @SerializedName("nama_mahasiswa") val namaMahasiswa: String?,
    @SerializedName("created_at") val createdAt: String?
)


data class CreateTugasRequest(
    val judul: String,
    val deskripsi: String,
    @SerializedName("link_tugas") val linkTugas: String?,
    val visibility: String,
    @SerializedName("matakuliah_id") val matakuliahId: Int,
    @SerializedName("tipe_tugas") val tipe_tugas: String // Menggunakan tipe_tugas (String)
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
    val tipe_tugas: String = "Individu", // REVISI: Mengganti tipeTugasString menjadi tipe_tugas
    val errorMessage: String? = null,
    val successMessage: String? = null
)

data class SimpleResponse(
    val message: String
)