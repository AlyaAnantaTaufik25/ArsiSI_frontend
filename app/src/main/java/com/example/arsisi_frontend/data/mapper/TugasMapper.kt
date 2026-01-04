package com.example.arsisi_frontend.data.mapper
import com.example.arsisi_frontend.data.local.entity.TugasEntity
import com.example.arsisi_frontend.data.model.Tugas
fun Tugas.toEntity() = TugasEntity(
    tugasId = this.tugasId!!,
    judulTugas = this.judul ,
    deskripsi = this.deskripsi,
    deadline = this.deadline,
    linkTugas = this.linkTugas,
    visibility = this.visibility ?: "Private",
    tipe_tugas = this.tipe_tugas ?: "Individu",
    matakuliah_id = this.matakuliah_id,
    namaMatakuliah = this.namaMatakuliah,
    kodeMatakuliah = this.kodeMatakuliah,
    user_id = this.user_id,
    namaUser = this.namaUser,
    created_at = this.created_at,
    updated_at = this.updated_at ?: this.created_at,
    lastUpdated = System.currentTimeMillis()
)
fun TugasEntity.toModel() = Tugas(
    tugasId = this.tugasId,
    judul = this.judulTugas,
    deskripsi = this.deskripsi,
    deadline = this.deadline,
    linkTugas = this.linkTugas,
    visibility = this.visibility,
    tipe_tugas = this.tipe_tugas,
    matakuliah_id = this.matakuliah_id,
    namaMatakuliah = this.namaMatakuliah,
    kodeMatakuliah = this.kodeMatakuliah,
    user_id = this.user_id,
    namaUser = this.namaUser,
    created_at = this.created_at,
    updated_at = this.updated_at
)