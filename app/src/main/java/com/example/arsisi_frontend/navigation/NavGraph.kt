package com.example.arsisi_frontend.navigation

import android.app.Application
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import android.util.Log

import com.example.arsisi_frontend.ui.auth.AuthViewModel
import com.example.arsisi_frontend.ui.auth.AuthViewModelFactory
import com.example.arsisi_frontend.ui.auth.LoginScreen
import com.example.arsisi_frontend.ui.auth.RegisterScreen
import com.example.arsisi_frontend.ui.dashboard.DashboardScreen
import com.example.arsisi_frontend.ui.prestasi.PrestasiScreen
import com.example.arsisi_frontend.ui.prestasi.PrestasiFormScreen
import com.example.arsisi_frontend.ui.prestasi.PrestasiViewModel
import com.example.arsisi_frontend.ui.splash.NavigationRoute
import com.example.arsisi_frontend.ui.splash.SplashScreen
import com.example.arsisi_frontend.ui.splash.SplashViewModel

@Composable
fun NavGraph(
    navController: NavHostController,
) {
    val context = LocalContext.current
    val authViewModelFactory = remember {
        AuthViewModelFactory(context.applicationContext as Application)
    }

    val sharedAuthViewModel: AuthViewModel = viewModel(factory = authViewModelFactory)
    val splashViewModel: SplashViewModel = viewModel()
    val routeState by splashViewModel.nextRoute.collectAsState()
    val initialRoute = Screen.Splash.route

    NavHost(
        navController = navController,
        startDestination = initialRoute
    ) {
        // ============= SPLASH SCREEN =============
        composable(Screen.Splash.route) {
            val isFirstTime by splashViewModel.isFirstTimeState.collectAsState()
            val userName by splashViewModel.userNameState.collectAsState()

            SplashScreen(
                isFirstTime = isFirstTime,
                userName = userName,
                onNavigateToLogin = {
                    navController.navigate(Screen.Login.route) {
                        popUpTo(Screen.Splash.route) { inclusive = true }
                    }
                },
                onNavigateToRegister = {
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
                    Log.d("NavGraph", "🚀 NAV TO DASHBOARD FROM LOGIN")
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
                onNavigateToDashboard = {
                    navController.navigate(Screen.Dashboard.route) {
                        popUpTo(Screen.Register.route) { inclusive = true }
                    }
                },
                viewModel = sharedAuthViewModel
            )
        }

        // ============= DASHBOARD SCREEN =============
        composable(Screen.Dashboard.route) {
            val authState by sharedAuthViewModel.authState.collectAsState()
            val userName = authState.user?.nama ?: "Pengguna"

            DashboardScreen(
                userName = userName,
                authViewModel = sharedAuthViewModel,
                onNavigateToLogin = {
                    sharedAuthViewModel.logout()
                    navController.navigate(Screen.Login.route) {
                        popUpTo(Screen.Dashboard.route) { inclusive = true }
                    }
                },
                onNavigateToMataKuliah = { navController.navigate(Screen.MataKuliahList.route) },
                onNavigateToAgenda = { navController.navigate(Screen.AgendaList.route) },
                onNavigateToPrestasi = { navController.navigate(Screen.PrestasiList.route) },
                onNavigateToAkademik = { navController.navigate(Screen.DokumenAkademikList.route) },
                onNavigateToProfile = { navController.navigate(Screen.Profile.route) },
                onNavigateToSearch = { navController.navigate(Screen.Search.route) }
            )
        }

        // ============= PRESTASI LIST ============= ✅ FIXED
        composable(Screen.PrestasiList.route) {
            val prestasiViewModel: PrestasiViewModel = viewModel()

            PrestasiScreen(
                viewModel = prestasiViewModel,
                onAddClick = { navController.navigate("prestasi_form/-1") },
                onArsipClick = { id -> navController.navigate("prestasi_detail/$id") }
            )
        }

        // ============= PRESTASI FORM ============= ✅ FIXED
        composable(
            "prestasi_form/{prestasiId}",
            arguments = listOf(
                navArgument("prestasiId") {
                    type = NavType.IntType
                    defaultValue = -1
                }
            )
        ) { backStackEntry ->
            val prestasiViewModel: PrestasiViewModel = viewModel()
            val prestasiId = backStackEntry.arguments?.getInt("prestasiId")

            PrestasiFormScreen(
                viewModel = prestasiViewModel,
                prestasiId = if (prestasiId != -1) prestasiId else null,
                onNavigateBack = { navController.popBackStack() }
            )
        }

        // ============= PRESTASI DETAIL ============= ✅ READY
        composable(
            "prestasi_detail/{prestasiId}",
            arguments = listOf(
                navArgument("prestasiId") {
                    type = NavType.IntType
                }
            )
        ) { backStackEntry ->
            val prestasiViewModel: PrestasiViewModel = viewModel()
            val prestasiId = backStackEntry.arguments?.getInt("prestasiId") ?: 0

            // Uncomment kalau sudah ada PrestasiDetailScreen
            /*
            PrestasiDetailScreen(
                viewModel = prestasiViewModel,
                prestasiId = prestasiId,
                onNavigateBack = { navController.popBackStack() }
            )
            */
        }

        // ============= RUTE LAINNYA =============
        composable(Screen.MataKuliahList.route) { /* TODO: MataKuliahScreen */ }
        composable(Screen.AgendaList.route) { /* TODO: AgendaScreen */ }
        composable(Screen.DokumenAkademikList.route) { /* TODO: AkademikScreen */ }
        composable(Screen.Profile.route) { /* TODO: ProfileScreen */ }
        composable(Screen.Search.route) { /* TODO: SearchScreen */ }
    }
}
