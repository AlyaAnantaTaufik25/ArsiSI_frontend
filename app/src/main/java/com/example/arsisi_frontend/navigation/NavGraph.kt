package com.example.arsisi_frontend.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.arsisi_frontend.ui.akademik.AkademikDetailScreen
import com.example.arsisi_frontend.ui.akademik.AkademikFormScreen
import com.example.arsisi_frontend.ui.akademik.AkademikListScreen
import com.example.arsisi_frontend.ui.akademik.AkademikViewModel

@Composable
fun NavGraph(navController: NavHostController) {
    // Inisialisasi ViewModel di level NavGraph agar data persist antar layar
    val akademikViewModel = remember { AkademikViewModel() }

    NavHost(navController = navController, startDestination = "akademik_list") {

        // 1. HALAMAN LIST
        composable("akademik_list") {
            AkademikListScreen(
                viewModel = akademikViewModel,
                onAddClick = {
                    navController.navigate("akademik_form") // Mode Tambah (tanpa ID)
                },
                onItemClick = { docId ->
                    navController.navigate("akademik_detail/$docId") // Ke Detail bawa ID
                }
            )
        }

        // 2. HALAMAN FORM (Tambah & Edit)
        composable(
            route = "akademik_form?docId={docId}",
            arguments = listOf(navArgument("docId") {
                type = NavType.StringType
                nullable = true
            })
        ) { backStackEntry ->
            val docId = backStackEntry.arguments?.getString("docId")

            AkademikFormScreen(
                documentId = docId,
                viewModel = akademikViewModel,
                onBackClick = { navController.popBackStack() },
                onSuccess = {
                    navController.popBackStack() // Kembali setelah simpan
                }
            )
        }

        // 3. HALAMAN DETAIL
        composable(
            route = "akademik_detail/{docId}",
            arguments = listOf(navArgument("docId") { type = NavType.StringType })
        ) { backStackEntry ->
            val docId = backStackEntry.arguments?.getString("docId") ?: ""

            AkademikDetailScreen(
                documentId = docId,
                viewModel = akademikViewModel,
                onBackClick = { navController.popBackStack() },
                onEditClick = {
                    navController.navigate("akademik_form?docId=$docId") // Ke Form bawa ID
                },
                onDeleteSuccess = {
                    navController.popBackStack() // Kembali ke list setelah hapus
                }
            )
        }
    }
}