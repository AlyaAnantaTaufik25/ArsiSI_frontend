package com.example.arsisi_frontend.ui.navigation
import android.app.Application
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
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
import com.example.arsisi_frontend.ui.agenda.AgendaListScreen
import com.example.arsisi_frontend.ui.agenda.AgendaFormScreen
import com.example.arsisi_frontend.ui.agenda.AgendaViewModel
object Route {
    // Tugas routes removed - only agenda features maintained
}

sealed class Screen(val route: String) {
    object Splash : Screen("splash")
    object Login : Screen("login")
    object Register : Screen("register")
    object Dashboard : Screen("dashboard")
    object AgendaList : Screen("agenda_list")
    object AgendaForm : Screen("agenda_form")
}
@Composable
fun AppNavGraph(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    val authViewModelFactory = remember {
        AuthViewModelFactory(context.applicationContext as Application)
    }

    val agendaRepository = remember {
        com.example.arsisi_frontend.data.repository.RemoteAgendaRepositoryImpl(context.applicationContext)
    }

    val agendaViewModelFactory = remember {
        com.example.arsisi_frontend.ui.agenda.AgendaViewModelFactory(agendaRepository, context.applicationContext)
    }

    val sharedAgendaViewModel: AgendaViewModel = viewModel(factory = agendaViewModelFactory)
    val sharedAuthViewModel: AuthViewModel = viewModel(factory = authViewModelFactory)
    val splashViewModel: SplashViewModel = viewModel()

    val dashboardViewModel: DashboardViewModel = viewModel(
        factory = object : androidx.lifecycle.ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
                return DashboardViewModel(context.applicationContext as Application, agendaRepository, null) as T
            }
        }
    )

    val routeState by splashViewModel.nextRoute.collectAsState()
    val initialRoute = Screen.Splash.route
    NavHost(
        navController = navController,
        startDestination = initialRoute,
        modifier = modifier
    ) {
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
        composable(Screen.Dashboard.route) {
            val authState by sharedAuthViewModel.authState.collectAsState()
            val userName = authState.user?.nama ?: "Pengguna"
            DashboardScreen(
                userName = userName,
                isLogoutLoading = authState.isLoading,
                onLogout = {
                    sharedAuthViewModel.logout()
                    navController.navigate(Screen.Login.route) {
                        popUpTo(Screen.Dashboard.route) { inclusive = true }
                    }
                },
                onNavigateToAkademik = { /* Akademik removed */ },
                onNavigateToPrestasi = { /* Prestasi removed */ },
                onNavigateToAgenda = { navController.navigate(Screen.AgendaList.route) },
                onNavigateToMataKuliah = { /* Tugas removed */ },
                onNavigateToProfile = { /* Profile removed */ },
                onNavigateToSearch = { /* Search removed */ },
                viewModel = dashboardViewModel
            )
        }

        composable(Screen.AgendaList.route) {
            LaunchedEffect(Unit) {
                sharedAgendaViewModel.loadAgendas()
            }
            AgendaListScreen(
                navController = navController,
                viewModel = sharedAgendaViewModel
            )
        }

        composable(
            route = "agenda_form?agendaId={agendaId}",
            arguments = listOf(
                navArgument("agendaId") {
                    type = NavType.StringType
                    defaultValue = null
                    nullable = true
                }
            )
        ) { backStackEntry ->
            val agendaId = backStackEntry.arguments?.getString("agendaId")
            AgendaFormScreen(
                navController = navController,
                viewModel = sharedAgendaViewModel,
                agendaId = agendaId
            )
        }
    }
}