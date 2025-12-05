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
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable

import com.example.arsisi_frontend.ui.auth.AuthViewModel
import com.example.arsisi_frontend.ui.auth.AuthViewModelFactory
import com.example.arsisi_frontend.ui.auth.LoginScreen
import com.example.arsisi_frontend.ui.auth.RegisterScreen
import com.example.arsisi_frontend.ui.dashboard.DashboardScreen
import com.example.arsisi_frontend.ui.splash.NavigationRoute // Import NavigationRoute yang benar
import com.example.arsisi_frontend.ui.splash.SplashScreen
import com.example.arsisi_frontend.ui.splash.SplashViewModel

@Composable
fun NavGraph(
    navController: NavHostController,
) {
    val context = LocalContext.current

    // Inisialisasi Factory (pastikan context dapat di-cast ke Application)
    val authViewModelFactory = remember {
        AuthViewModelFactory(context.applicationContext as Application)
    }

    // Inisialisasi SplashViewModel
    val splashViewModel: SplashViewModel = viewModel()
    val routeState by splashViewModel.nextRoute.collectAsState()

    // Penentuan rute awal NavHost
    val initialRoute = when (val state = routeState) {
        is NavigationRoute.Loading -> Screen.Splash.route
        is NavigationRoute.Navigate -> state.route
    }

    // Tampilkan Loading UI/Box kosong saat state masih Loading
    if (routeState is NavigationRoute.Loading) {
        // Tampilkan Box kosong dengan warna latar belakang tema agar transisi lebih mulus
        Box(modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background))
        return
    }

    NavHost(
        navController = navController,
        startDestination = initialRoute
        // CATATAN PENTING: enterTransition dan exitTransition dihilangkan SEMENTARA
        // untuk mengatasi crash LayoutNode saat navigasi dari Pager (SplashScreen).
    ) {
        // ==================== 1. SPLASH SCREEN ====================
        composable(Screen.Splash.route) {
            val isFirstTime by splashViewModel.isFirstTimeState.collectAsState()
            val userName by splashViewModel.userNameState.collectAsState()

            SplashScreen(
                isFirstTime = isFirstTime,
                userName = userName,
                // Navigasi setelah Onboarding selesai
                onNavigateToLogin = {
                    navController.navigate(Screen.Login.route) {
                        popUpTo(Screen.Splash.route) { inclusive = true }
                    }
                },
                onNavigateToRegister = {
                    // Navigasi yang diinginkan: Splash -> Register
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

        // ==================== 2. LOGIN SCREEN ====================
        composable(Screen.Login.route) {
            val authViewModel: AuthViewModel = viewModel(factory = authViewModelFactory)

            LoginScreen(
                onNavigateToRegister = { navController.navigate(Screen.Register.route) },
                onNavigateToDashboard = {
                    navController.navigate(Screen.Dashboard.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                },
                viewModel = authViewModel
            )
        }

        // ==================== 3. REGISTER SCREEN ====================
        composable(Screen.Register.route) {
            val authViewModel: AuthViewModel = viewModel(factory = authViewModelFactory)

            RegisterScreen(
                onNavigateToLogin = { navController.popBackStack() },
                onNavigateToDashboard = {
                    navController.navigate(Screen.Dashboard.route) {
                        popUpTo(Screen.Register.route) { inclusive = true }
                    }
                },
                viewModel = authViewModel
            )
        }

        // ==================== 4. DASHBOARD SCREEN ====================
        composable(Screen.Dashboard.route) {
            val authViewModel: AuthViewModel = viewModel(factory = authViewModelFactory)
            val userName = authViewModel.authState.collectAsState().value.user?.nama ?: "Pengguna"

            DashboardScreen(
                userName = userName,
                onNavigateToLogin = {
                    navController.navigate(Screen.Login.route) {
                        popUpTo(Screen.Dashboard.route) { inclusive = true }
                    }
                },
                // Navigasi ke sub-fitur
                onNavigateToMataKuliah = { navController.navigate(Screen.MataKuliahList.route) },
                onNavigateToAgenda = { navController.navigate(Screen.AgendaList.route) },
                onNavigateToPrestasi = { navController.navigate(Screen.PrestasiList.route) },
                onNavigateToAkademik = { navController.navigate(Screen.DokumenAkademikList.route) },
                onNavigateToProfile = { navController.navigate(Screen.Profile.route) },
                onNavigateToSearch = { navController.navigate(Screen.Search.route) },
            )
        }

        // ==================== 5. DEFINISI SEMUA RUTE DETAIL ====================
        composable(Screen.MataKuliahList.route) { /* MataKuliahListScreen() */ }
        composable(Screen.AgendaList.route) { /* AgendaListScreen() */ }
        composable(Screen.PrestasiList.route) { /* PrestasiListScreen() */ }
        composable(Screen.DokumenAkademikList.route) { /* DokumenAkademikListScreen() */ }
        composable(Screen.Profile.route) { /* ProfileScreen() */ }
        composable(Screen.Search.route) { /* SearchScreen() */ }
    }
}