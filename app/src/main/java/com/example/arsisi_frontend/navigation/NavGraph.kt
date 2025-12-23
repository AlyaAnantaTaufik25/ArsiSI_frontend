package com.example.arsisi_frontend.ui.navigation

import android.app.Application
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
import com.example.arsisi_frontend.data.local.AppDatabase
import com.example.arsisi_frontend.data.remote.ApiClient
import com.example.arsisi_frontend.data.remote.ApiService
import com.example.arsisi_frontend.data.remote.AuthInterceptor
import com.example.arsisi_frontend.data.repository.MataKuliahRepository
import com.example.arsisi_frontend.data.repository.TugasRepository
import com.example.arsisi_frontend.ui.tugas.TugasViewModel
import com.example.arsisi_frontend.ui.tugas.TugasListScreen
import com.example.arsisi_frontend.ui.tugas.TugasFormScreen
import com.example.arsisi_frontend.ui.tugas.TugasDetailScreen
import com.example.arsisi_frontend.ui.tugas.PublikTugasMatkulScreen
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

        // ✅ Ambil Database Instance
        val database = AppDatabase.getDatabase(context.applicationContext)

        // ✅ Ambil TugasDao dan MataKuliahDao
        val tugasDao = database.tugasDao()
        val mataKuliahDao = database.mataKuliahDao()  // ✅ TAMBAH INI

        // ✅ Repository dengan Room Database
        val tugasRepo = TugasRepository(apiService, tugasDao)
        val matkulRepo = MataKuliahRepository(apiService, mataKuliahDao)  // ✅ TAMBAH DAO

        object {
            val tugasViewModelFactory = TugasViewModel.provideFactory(
                application = context.applicationContext as Application,
                tugasRepository = tugasRepo,
                matakuliahRepository = matkulRepo
            )
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
                factory = appDependencies.tugasViewModelFactory
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
                onViewDetails = { tugasId: Int ->
                    navController.navigate("detail_tugas/$tugasId")
                },
                onEditTask = { tugasId: Int ->
                    navController.navigate("edit_tugas/$tugasId")
                },
                onOpenEksplorasiMatkul = { matkulId: Int ->
                    navController.navigate("eksplorasi_list_tugas/$matkulId")
                }
            )
        }

        // ===== FORM TUGAS (TAMBAH) =====
        composable(Route.FORM_TUGAS) {
            val tugasViewModel: TugasViewModel = viewModel(
                factory = appDependencies.tugasViewModelFactory
            )

            TugasFormScreen(
                tugasId = null,
                viewModel = tugasViewModel,
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

            val tugasViewModel: TugasViewModel = viewModel(
                factory = appDependencies.tugasViewModelFactory
            )

            LaunchedEffect(tugasId) {
                tugasViewModel.loadTugasDetail(tugasId)
            }

            TugasDetailScreen(
                tugasId = tugasId,
                onNavigateBack = { navController.popBackStack() },
                viewModel = tugasViewModel
            )
        }

        // ===== EDIT TUGAS =====
        composable(
            route = Route.EDIT_TUGAS,
            arguments = listOf(navArgument("tugasId") { type = NavType.IntType })
        ) { backStackEntry ->
            val tugasId = backStackEntry.arguments?.getInt("tugasId") ?: 0

            val tugasViewModel: TugasViewModel = viewModel(
                factory = appDependencies.tugasViewModelFactory
            )

            TugasFormScreen(
                tugasId = tugasId,
                viewModel = tugasViewModel,
                onNavigateBack = { navController.popBackStack() },
                onSuccessSubmit = {
                    val listEntry = navController.getBackStackEntry(Route.LIST_TUGAS)
                    listEntry.savedStateHandle[Route.KEY_SHOULD_REFRESH_LIST] = true
                    navController.popBackStack()
                }
            )
        }

        // ===== LIST TUGAS PUBLIK PER MATA KULIAH =====
        composable(
            route = Route.PUBLIK_TUGAS_MATKUL,
            arguments = listOf(navArgument("matkulId") { type = NavType.IntType })
        ) { backStackEntry ->
            val matkulId = backStackEntry.arguments?.getInt("matkulId") ?: -1

            val tugasViewModel: TugasViewModel = viewModel(
                factory = appDependencies.tugasViewModelFactory
            )

            PublikTugasMatkulScreen(
                matkulId = matkulId,
                onNavigateBack = { navController.popBackStack() },
                onViewDetails = { tugasId: Int ->
                    navController.navigate("detail_tugas/$tugasId")
                },
                viewModel = tugasViewModel
            )
        }
    }
}
