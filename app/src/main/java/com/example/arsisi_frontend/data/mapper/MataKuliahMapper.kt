package com.example.arsisi_frontend.data.mapper
import com.example.arsisi_frontend.data.local.entity.MataKuliahEntity
import com.example.arsisi_frontend.data.model.MataKuliah
fun MataKuliah.toEntity(): MataKuliahEntity {
    return MataKuliahEntity(
        matakuliahId = this.matakuliahId,
        mahasiswaId = this.mahasiswaId,
        namaMatakuliah = this.namaMatakuliah,
        kodeMatakuliah = this.kodeMatakuliah,
        lastUpdated = System.currentTimeMillis()
    )
}
fun MataKuliahEntity.toModel(): MataKuliah {
    return MataKuliah(
        matakuliahId = this.matakuliahId,
        mahasiswaId = this.mahasiswaId,
        namaMatakuliah = this.namaMatakuliah,
        kodeMatakuliah = this.kodeMatakuliah
    )
}