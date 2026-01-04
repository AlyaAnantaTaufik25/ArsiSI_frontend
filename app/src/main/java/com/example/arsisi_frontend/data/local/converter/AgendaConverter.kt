package com.example.arsisi_frontend.data.local.converter
import com.example.arsisi_frontend.data.model.Agenda
import com.example.arsisi_frontend.data.local.entity.AgendaEntity
object AgendaConverter {
    fun toEntity(agenda: Agenda): AgendaEntity {
        return AgendaEntity(
            id = agenda.id,
            judul = agenda.judul,
            deskripsi = agenda.deskripsi,
            tanggal = agenda.tanggal ?: "",
            waktu = agenda.waktu,
            kategori = agenda.kategori,
            prioritas = agenda.prioritas,
            reminderSetting = agenda.reminderSetting,
            filePath = agenda.filePath,
            mahasiswaId = agenda.mahasiswaId,
            createdAt = agenda.createdAt,
            updatedAt = agenda.updatedAt
        )
    }
    fun toModel(entity: AgendaEntity): Agenda {
        return Agenda(
            id = entity.id,
            judul = entity.judul,
            deskripsi = entity.deskripsi,
            tanggal = entity.tanggal,
            waktu = entity.waktu,
            kategori = entity.kategori,
            prioritas = entity.prioritas,
            reminderSetting = entity.reminderSetting,
            filePath = entity.filePath,
            mahasiswaId = entity.mahasiswaId,
            createdAt = entity.createdAt,
            updatedAt = entity.updatedAt
        )
    }
    fun toEntityList(agendas: List<Agenda>): List<AgendaEntity> {
        return agendas.map { toEntity(it) }
    }
    fun toModelList(entities: List<AgendaEntity>): List<Agenda> {
        return entities.map { toModel(it) }
    }
}