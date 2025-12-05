// Lokasi: com/example/arsisi_frontend/navigation/Screen.kt

package com.example.arsisi_frontend.navigation

/**
 * Mendefinisikan semua rute navigasi (route strings) dalam aplikasi.
 * File ini adalah sumber tunggal untuk menghindari error Redeclaration.
 */
sealed class Screen(val route: String) {
    object Splash : Screen("splash")
    object Login : Screen("login")
    object Register : Screen("register")
    object Dashboard : Screen("dashboard") // Rute utama Dashboard

    // Rute Profile dan Search (Dipanggil dari DashboardTopBar)
    object Profile : Screen("profile")
    object Search : Screen("search")

    // --- Mata Kuliah (Dipanggil dari Dashboard MenuCard) ---
    object MataKuliahList : Screen("mata_kuliah_list")
    object MataKuliahDetail : Screen("mata_kuliah_detail/{id}") {
        fun createRoute(id: Int) = "mata_kuliah_detail/$id"
    }
    object MataKuliahForm : Screen("mata_kuliah_form?id={id}") {
        fun createRoute(id: Int? = null) = if (id != null) "mata_kuliah_form?id=$id" else "mata_kuliah_form"
    }

    // --- Agenda (Dipanggil dari Dashboard MenuCard) ---
    object AgendaList : Screen("agenda_list")
    object AgendaDetail : Screen("agenda_detail/{id}") {
        fun createRoute(id: Int) = "agenda_detail/$id"
    }
    object AgendaForm : Screen("agenda_form?id={id}") {
        fun createRoute(id: Int? = null) = if (id != null) "agenda_form?id=$id" else "agenda_form"
    }

    // --- Prestasi (Dipanggil dari Dashboard MenuCard) ---
    object PrestasiList : Screen("prestasi_list")
    object PrestasiDetail : Screen("prestasi_detail/{id}") {
        fun createRoute(id: Int) = "prestasi_detail/$id"
    }
    object PrestasiForm : Screen("prestasi_form?id={id}") {
        fun createRoute(id: Int? = null) = if (id != null) "prestasi_form?id=$id" else "prestasi_form"
    }

    // --- Dokumen Akademik (Dipanggil dari Dashboard MenuCard) ---
    object DokumenAkademikList : Screen("dokumen_akademik_list")
    object DokumenAkademikDetail : Screen("dokumen_akademik_detail/{id}") {
        fun createRoute(id: Int) = "dokumen_akademik_detail/$id"
    }
    object DokumenAkademikForm : Screen("dokumen_akademik_form?id={id}") {
        fun createRoute(id: Int? = null) = if (id != null) "dokumen_akademik_form?id=$id" else "dokumen_akademik_form"
    }
}