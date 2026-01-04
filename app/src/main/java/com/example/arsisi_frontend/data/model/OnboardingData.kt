package com.example.arsisi_frontend.data.model
import androidx.annotation.DrawableRes
import com.example.arsisi_frontend.R
data class OnboardingPage(
    val title: String,
    val subtitle: String = "",
    val description: String,
    @DrawableRes val image: Int,
    val imageDescription: String = ""
)
object OnboardingData {
    fun getPages(): List<OnboardingPage> {
        return listOf(
            OnboardingPage(
                title = "Tertata dan Efisien",
                subtitle = "Arsip Mahasiswa Sistem Informasi",
                description = "Simpan dan kelola seluruh data akademikmu — mulai dari mata kuliah hingga prestasi — dalam satu aplikasi yang mudah digunakan dan terpercaya.",
                image = R.drawable.arsisi1,
                imageDescription = "Ilustrasi efisiensi"
            ),
            OnboardingPage(
                title = "Akurat dan Praktis",
                subtitle = "Semua Arsip Dalam Genggaman",
                description = "Akses cepat ke data akademik, prestasi, dan kalender kegiatan kapan pun dibutuhkan. Mulai kelola perjalanan akademikmu secara digital dan efisien.",
                image = R.drawable.arsisi2,
                imageDescription = "Ilustrasi praktis"
            )
        )
    }
}