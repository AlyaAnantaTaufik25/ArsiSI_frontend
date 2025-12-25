package com.example.arsisi_frontend.data.model

data class OnboardingPage(
    val title: String,
    val description: String,
    val imageDescription: String
)

object OnboardingData {
    fun getPages(): List<OnboardingPage> {
        return listOf(
            OnboardingPage(
                title = "Kelola Dokumen Akademik",
                description = "Simpan dan kelola semua dokumen akademik kamu dengan mudah dan aman dalam satu aplikasi",
                imageDescription = "📚 Dokumen Akademik"
            ),
            OnboardingPage(
                title = "Arsip Prestasi",
                description = "Catat dan dokumentasikan semua prestasi yang telah kamu raih selama kuliah",
                imageDescription = "🏆 Prestasi"
            ),
            OnboardingPage(
                title = "Agenda & Reminder",
                description = "Atur jadwal dan dapatkan pengingat untuk tugas, ujian, dan acara penting lainnya",
                imageDescription = "📅 Agenda"
            )
        )
    }
}
