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

import androidx.compose.runtime.LaunchedEffect
import android.util.Log


import com.example.arsisi_frontend.ui.auth.AuthViewModel
import com.example.arsisi_frontend.ui.auth.AuthViewModelFactory
import com.example.arsisi_frontend.ui.auth.LoginScreen
import com.example.arsisi_frontend.ui.auth.RegisterScreen
import com.example.arsisi_frontend.ui.dashboard.DashboardScreen
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

    // ✅ FIX #1: SHARED AUTH VIEWMODEL - SATU INSTANCE UNTUK SEMUA SCREEN
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
// ============= LOGIN SCREEN =============
        composable(Screen.Login.route) {
            // 🔥 FIX: AUTO-REDIRECT BACKUP di NavGraph level
            val authState by sharedAuthViewModel.authState.collectAsState()
            LaunchedEffect(authState.isSuccess) {
                if (authState.isSuccess && authState.user != null) {
                    Log.d("NavGraph", "💥 AUTO DASHBOARD FROM NAVGRAPH!")
                    navController.navigate(Screen.Dashboard.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                }
            }

            LoginScreen(
                onNavigateToRegister = { navController.navigate(Screen.Register.route) },
                onNavigateToDashboard = {
                    navController.navigate(Screen.Dashboard.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                },
                viewModel = sharedAuthViewModel
            )
        }


        // ============= REGISTER SCREEN =============
        composable(Screen.Register.route) {
            // ❌ HAPUS: val authViewModel: AuthViewModel = viewModel(factory = authViewModelFactory)

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
                viewModel = sharedAuthViewModel  // ✅ SHARED INSTANCE
            )
        }

        // ============= DASHBOARD SCREEN =============
        composable(Screen.Dashboard.route) {
            // ❌ HAPUS: val authViewModel: AuthViewModel = viewModel(factory = authViewModelFactory)
            val authState by sharedAuthViewModel.authState.collectAsState()  // ✅ SHARED STATE
            val userName = authState.user?.nama ?: "Pengguna"

            DashboardScreen(
                userName = userName,
                authViewModel = sharedAuthViewModel,  // ✅ SHARED INSTANCE
                onNavigateToLogin = {
                    sharedAuthViewModel.logout()  // ✅ SHARED LOGOUT
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

        // RUTE LAINNYA (SAMA)
        composable(Screen.MataKuliahList.route) { /* TODO */ }
        composable(Screen.AgendaList.route) { /* TODO */ }
        composable(Screen.PrestasiList.route) { /* TODO */ }
        composable(Screen.DokumenAkademikList.route) { /* TODO */ }
        composable(Screen.Profile.route) { /* TODO */ }
        composable(Screen.Search.route) { /* TODO */ }
    }
}
