package com.example.arsisi_frontend.navigation

/**
 * Sealed class untuk define semua routes di aplikasi
 */
sealed class Screen(val route: String) {
    object Splash : Screen("splash")
    object Login : Screen("login")
    object Register : Screen("register")
    object Dashboard : Screen("dashboard")
    object AkademikList : Screen("akademik_list")
    object AkademikForm : Screen("akademik_form")
    object AkademikDetail : Screen("akademik_detail/{docId}")
    object PrestasiList : Screen("prestasi_list")
    object AgendaList : Screen("agenda_list")
    object MataKuliahList : Screen("matakuliah_list")
}
