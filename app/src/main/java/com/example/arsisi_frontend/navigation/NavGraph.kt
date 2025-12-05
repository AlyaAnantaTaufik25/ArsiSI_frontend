package com.example.arsisi_frontend.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.arsisi_frontend.data.remote.ApiClient
import com.example.arsisi_frontend.data.remote.ApiService
import com.example.arsisi_frontend.data.remote.AuthInterceptor
import com.example.arsisi_frontend.data.repository.MataKuliahRepository
import com.example.arsisi_frontend.data.repository.TugasRepository
import com.example.arsisi_frontend.ui.tugas.*
import com.example.arsisi_frontend.utils.PreferencesManager

object Route {
    const val LIST_TUGAS = "list_tugas"
    const val FORM_TUGAS = "form_tugas"
    const val DETAIL_TUGAS = "detail_tugas/{tugasId}"
    const val EDIT_TUGAS = "edit_tugas/{tugasId}"
    const val PUBLIK_TUGAS_MATKUL = "eksplorasi_list_tugas/{matkulId}"
    const val KEY_SHOULD_REFRESH_LIST = "should_refresh_tugas_list"
}

@Composable
fun AppNavGraph(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    val appDependencies = remember {
        val preferencesManager = PreferencesManager(context.applicationContext)
        val authInterceptor = AuthInterceptor(preferencesManager)
        val apiService = ApiClient.createService(authInterceptor, ApiService::class.java)

        val tugasRepo = TugasRepository(apiService)
        val matkulRepo = MataKuliahRepository(apiService)

        object {
            val tugasFactory = TugasViewModelFactory(tugasRepo)
            val formFactory = TugasFormViewModelFactory(tugasRepo, matkulRepo)
            val tugasRepoInstance = tugasRepo
            val matkulRepoInstance = matkulRepo
        }
    }

    NavHost(
        navController = navController,
        startDestination = Route.LIST_TUGAS,
        modifier = modifier
    ) {
        // ===== LIST TUGAS (TAB TUGAS SAYA & EKSPLORASI) =====
        composable(Route.LIST_TUGAS) { entry ->
            val tugasViewModel: TugasViewModel = viewModel(
                factory = appDependencies.tugasFactory
            )

            val shouldRefresh by entry.savedStateHandle
                .getStateFlow(Route.KEY_SHOULD_REFRESH_LIST, false)
                .collectAsStateWithLifecycle()

            LaunchedEffect(shouldRefresh) {
                if (shouldRefresh) {
                    tugasViewModel.refreshTugasList()
                    entry.savedStateHandle[Route.KEY_SHOULD_REFRESH_LIST] = false
                }
            }

            TugasListScreen(
                viewModel = tugasViewModel,
                onNavigateToForm = {
                    navController.navigate(Route.FORM_TUGAS)
                },
                onViewDetails = { tugasId ->
                    navController.navigate("detail_tugas/$tugasId")
                },
                onEditTask = { tugas ->
                    navController.navigate("edit_tugas/${tugas.tugasId}")
                },
                onOpenEksplorasiMatkul = { item ->
                    val matkulId = item.matakuliahId ?: -1
                    navController.navigate("eksplorasi_list_tugas/$matkulId")
                }
            )
        }

        // ===== FORM TUGAS (TAMBAH) =====
        composable(Route.FORM_TUGAS) {
            val formViewModel: TugasFormViewModel =
                viewModel(factory = appDependencies.formFactory)

            TugasFormScreen(
                viewModel = formViewModel,
                onNavigateBack = { navController.popBackStack() },
                onSuccessSubmit = {
                    val listEntry = navController.getBackStackEntry(Route.LIST_TUGAS)
                    listEntry.savedStateHandle[Route.KEY_SHOULD_REFRESH_LIST] = true
                    navController.popBackStack()
                }
            )
        }

        // ===== DETAIL TUGAS (PUNYA USER / PUBLIK) =====
        composable(
            route = Route.DETAIL_TUGAS,
            arguments = listOf(navArgument("tugasId") { type = NavType.IntType })
        ) { backStackEntry ->
            val tugasId = backStackEntry.arguments?.getInt("tugasId") ?: 0

            val detailFactory = remember(tugasId) {
                TugasDetailViewModelFactory(appDependencies.tugasRepoInstance, tugasId)
            }
            val detailViewModel: TugasDetailViewModel = viewModel(factory = detailFactory)

            DetailTugasScreen(
                tugasId = tugasId,
                onNavigateBack = { navController.popBackStack() },
                viewModel = detailViewModel
            )
        }

        // ===== EDIT TUGAS =====
        composable(
            route = Route.EDIT_TUGAS,
            arguments = listOf(navArgument("tugasId") { type = NavType.IntType })
        ) { backStackEntry ->
            val tugasId = backStackEntry.arguments?.getInt("tugasId") ?: 0

            val editFactory = remember(tugasId) {
                TugasFormViewModelFactory(
                    appDependencies.tugasRepoInstance,
                    appDependencies.matkulRepoInstance,
                    tugasId = tugasId
                )
            }
            val editViewModel: TugasFormViewModel = viewModel(factory = editFactory)

            EditTugasScreen(
                tugasId = tugasId,
                onNavigateBack = { navController.popBackStack() },
                onSuccessUpdate = {
                    val listEntry = navController.getBackStackEntry(Route.LIST_TUGAS)
                    listEntry.savedStateHandle[Route.KEY_SHOULD_REFRESH_LIST] = true
                    navController.popBackStack()
                },
                viewModel = editViewModel
            )
        }

        // ===== LIST TUGAS PUBLIK PER MATA KULIAH =====
        composable(
            route = Route.PUBLIK_TUGAS_MATKUL,
            arguments = listOf(navArgument("matkulId") { type = NavType.IntType })
        ) { backStackEntry ->
            val matkulId = backStackEntry.arguments?.getInt("matkulId") ?: -1

            val publikViewModel: TugasViewModel = viewModel(
                factory = appDependencies.tugasFactory
            )

            PublikTugasMatkulScreen(
                matkulId = matkulId,
                onNavigateBack = { navController.popBackStack() },
                onViewDetails = { tugasId ->
                    // buka halaman detail tugas seperti desain
                    navController.navigate("detail_tugas/$tugasId")
                },
                viewModel = publikViewModel
            )
        }
    }
}
