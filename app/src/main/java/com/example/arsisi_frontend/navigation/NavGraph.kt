package com.example.arsisi_frontend.navigation

import android.app.Application
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.arsisi_frontend.data.repository.AkademikRepository
import com.example.arsisi_frontend.ui.akademik.AkademikDetailScreen
import com.example.arsisi_frontend.ui.akademik.AkademikFormScreen
import com.example.arsisi_frontend.ui.akademik.AkademikListScreen
import com.example.arsisi_frontend.ui.akademik.AkademikViewModel
import com.example.arsisi_frontend.ui.akademik.AkademikViewModelFactory
import com.example.arsisi_frontend.ui.auth.AuthViewModel
import com.example.arsisi_frontend.ui.auth.AuthViewModelFactory
import com.example.arsisi_frontend.ui.auth.LoginScreen
import com.example.arsisi_frontend.ui.auth.RegisterScreen
import com.example.arsisi_frontend.ui.dashboard.DashboardScreen
import com.example.arsisi_frontend.ui.dashboard.DashboardViewModel
import com.example.arsisi_frontend.ui.splash.NavigationRoute
import com.example.arsisi_frontend.ui.splash.SplashScreen
import com.example.arsisi_frontend.ui.splash.SplashViewModel
import com.example.arsisi_frontend.utils.PreferencesManager
import kotlinx.coroutines.runBlocking

@Composable
fun NavGraph(
    navController: NavHostController,
    akademikRepository: AkademikRepository,
    application: Application,
    preferencesManager: PreferencesManager
) {
    // Shared ViewModels
    val authViewModelFactory = remember { AuthViewModelFactory(application) }
    val sharedAuthViewModel: AuthViewModel = viewModel(factory = authViewModelFactory)
    
    val splashViewModel: SplashViewModel = viewModel(
        factory = object : androidx.lifecycle.ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
                return SplashViewModel(application) as T
            }
        }
    )
    
    val dashboardViewModel: DashboardViewModel = viewModel(
        factory = object : androidx.lifecycle.ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
                return DashboardViewModel(application) as T
            }
        }
    )

    val akademikViewModel: AkademikViewModel = viewModel(
        factory = AkademikViewModelFactory(akademikRepository)
    )

    NavHost(
        navController = navController,
        startDestination = Screen.Splash.route
    ) {
        // ============= SPLASH SCREEN =============
        composable(Screen.Splash.route) {
            val isFirstTime by splashViewModel.isFirstTimeState.collectAsState()
            val userName by splashViewModel.userNameState.collectAsState()
            val routeState by splashViewModel.nextRoute.collectAsState()

            SplashScreen(
                isFirstTime = isFirstTime,
                userName = userName,
                onNavigateToLogin = {
                    navController.navigate(Screen.Login.route) {
                        popUpTo(Screen.Splash.route) { inclusive = true }
                    }
                },
                onNavigateToRegister = {
                    // Set first time to false
                    runBlocking {
                        preferencesManager.setFirstTime(false)
                    }
                    navController.navigate(Screen.Register.route) {
                        popUpTo(Screen.Splash.route) { inclusive = true }
                    }
                },
                onNavigateToDashboard = {
                    navController.navigate(Screen.Dashboard.route) {
                        popUpTo(Screen.Splash.route) { inclusive = true }
                    }
                }
            )

            // Auto navigation based on route state
            if (routeState is NavigationRoute.Navigate) {
                val route = (routeState as NavigationRoute.Navigate).route
                when (route) {
                    "login" -> {
                        navController.navigate(Screen.Login.route) {
                            popUpTo(Screen.Splash.route) { inclusive = true }
                        }
                    }
                    "dashboard" -> {
                        navController.navigate(Screen.Dashboard.route) {
                            popUpTo(Screen.Splash.route) { inclusive = true }
                        }
                    }
                }
            }
        }

        // ============= LOGIN SCREEN =============
        composable(Screen.Login.route) {
            LoginScreen(
                onNavigateToRegister = {
                    navController.navigate(Screen.Register.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                },
                onNavigateToDashboard = {
                    navController.navigate(Screen.Dashboard.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                        launchSingleTop = true
                    }
                },
                viewModel = sharedAuthViewModel
            )
        }

        // ============= REGISTER SCREEN =============
        composable(Screen.Register.route) {
            RegisterScreen(
                onNavigateToLogin = {
                    navController.navigate(Screen.Login.route) {
                        popUpTo(Screen.Register.route) { inclusive = true }
                    }
                },
                viewModel = sharedAuthViewModel
            )
        }

        // ============= DASHBOARD SCREEN =============
        composable(Screen.Dashboard.route) {
            val userName = runBlocking { preferencesManager.getUserNama() }

            DashboardScreen(
                userName = userName,
                onLogout = {
                    sharedAuthViewModel.logout()
                    navController.navigate(Screen.Login.route) {
                        popUpTo(Screen.Dashboard.route) { inclusive = true }
                    }
                },
                onNavigateToAkademik = {
                    navController.navigate(Screen.AkademikList.route)
                },
                onNavigateToPrestasi = {
                    navController.navigate(Screen.PrestasiList.route)
                },
                onNavigateToAgenda = {
                    navController.navigate(Screen.AgendaList.route)
                },
                onNavigateToMataKuliah = {
                    navController.navigate(Screen.MataKuliahList.route)
                },
                viewModel = dashboardViewModel
            )
        }

        // ============= AKADEMIK LIST =============
        composable(Screen.AkademikList.route) {
            AkademikListScreen(
                viewModel = akademikViewModel,
                onAddClick = {
                    navController.navigate("akademik_form")
                },
                onItemClick = { docId ->
                    navController.navigate("akademik_detail/$docId")
                }
            )
        }

        // ============= AKADEMIK FORM =============
        composable(
            route = "akademik_form?docId={docId}",
            arguments = listOf(navArgument("docId") {
                type = NavType.IntType
                defaultValue = 0
            })
        ) { backStackEntry ->
            val docId = backStackEntry.arguments?.getInt("docId") ?: 0

            AkademikFormScreen(
                documentId = if (docId == 0) null else docId,
                viewModel = akademikViewModel,
                onBackClick = { navController.popBackStack() },
                onSuccess = {
                    navController.popBackStack()
                }
            )
        }

        // ============= AKADEMIK DETAIL =============
        composable(
            route = "akademik_detail/{docId}",
            arguments = listOf(navArgument("docId") { type = NavType.IntType })
        ) { backStackEntry ->
            val docId = backStackEntry.arguments?.getInt("docId") ?: 0

            AkademikDetailScreen(
                documentId = docId,
                viewModel = akademikViewModel,
                onBackClick = { navController.popBackStack() },
                onEditClick = {
                    navController.navigate("akademik_form?docId=$docId")
                },
                onDeleteSuccess = {
                    navController.popBackStack()
                }
            )
        }

        // ============= PLACEHOLDER ROUTES =============
        composable(Screen.PrestasiList.route) {
            // TODO: Implement PrestasiListScreen
        }
        composable(Screen.AgendaList.route) {
            // TODO: Implement AgendaListScreen
        }
        composable(Screen.MataKuliahList.route) {
            // TODO: Implement MataKuliahListScreen
        }
    }
}