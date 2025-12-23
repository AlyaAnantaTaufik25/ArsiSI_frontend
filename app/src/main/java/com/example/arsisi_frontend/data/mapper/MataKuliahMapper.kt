package com.example.arsisi_frontend.data.mapper

import com.example.arsisi_frontend.data.local.entity.MataKuliahEntity
import com.example.arsisi_frontend.data.model.MataKuliah

fun MataKuliah.toEntity(): MataKuliahEntity {
    return MataKuliahEntity(
        matakuliahId = this.matakuliahId ?: 0,
        mahasiswaId = this.mahasiswaId,
        namaMatakuliah = this.namaMatakuliah,
        kodeMatakuliah = this.kodeMatakuliah,
        lastUpdated = System.currentTimeMillis()
    )
}

fun MataKuliahEntity.toModel(): MataKuliah {
    return MataKuliah(
        matakuliahId = this.matakuliahId ?: 0,  // ✅ Handle nullable
        mahasiswaId = this.mahasiswaId ?: 0,    // ✅ Handle nullable
        namaMatakuliah = this.namaMatakuliah ?: "",  // ✅ Handle nullable
        kodeMatakuliah = this.kodeMatakuliah ?: ""   // ✅ Handle nullable
    )
}
