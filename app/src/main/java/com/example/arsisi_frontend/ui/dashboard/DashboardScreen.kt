package com.example.arsisi_frontend.ui.dashboard

import android.app.Application
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.arsisi_frontend.ui.auth.AuthViewModel
import com.example.arsisi_frontend.ui.auth.AuthViewModelFactory
import com.example.arsisi_frontend.ui.theme.*
import kotlinx.coroutines.launch
import androidx.compose.ui.text.style.TextAlign

@Composable
fun DashboardScreen(
    userName: String,
    authViewModel: AuthViewModel,
    onNavigateToLogin: () -> Unit,
    onNavigateToMataKuliah: () -> Unit,
    onNavigateToAgenda: () -> Unit,
    onNavigateToPrestasi: () -> Unit,
    onNavigateToAkademik: () -> Unit,
    onNavigateToProfile: () -> Unit,
    onNavigateToSearch: () -> Unit,
) {
    val authState by authViewModel.authState.collectAsState()

    val dashboardViewModel: DashboardViewModel = viewModel()
    val stats by dashboardViewModel.stats.collectAsState()
    val isLoading by dashboardViewModel.isLoading.collectAsState()
    val error by dashboardViewModel.error.collectAsState()



    // Logout navigation
    LaunchedEffect(authState.user) {
        if (authState.user == null && !authState.isLoading && !authState.isSuccess) {
            Log.d("DashboardScreen", "🚪 Auto logout")
            onNavigateToLogin()
        }
    }

    // ✅ LOADING SCREEN
    if (isLoading) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Orange500),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                CircularProgressIndicator(color = White, strokeWidth = 3.dp)
                Spacer(modifier = Modifier.height(24.dp))
                Text(
                    text = "Loading dashboard...",
                    style = MaterialTheme.typography.titleMedium,
                    color = White,
                    fontWeight = FontWeight.Medium
                )
            }
        }
        return
    }

    // ✅ ERROR SCREEN - FULLY FIXED
    if (error != null) {
        val errorMessage = error  // ✅ FIX #1: LOCAL COPY (smart cast)

        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Orange500),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = Icons.Default.Error,
                contentDescription = "Error",
                tint = White,
                modifier = Modifier.size(64.dp)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = error ?: "Terjadi kesalahan tidak diketahui",
                style = MaterialTheme.typography.titleMedium,
                color = White,
                textAlign = TextAlign.Center  // ✅ FIX #3: IMPORT SHORT
            )
            Spacer(modifier = Modifier.height(24.dp))
            Button(
                onClick = { dashboardViewModel.loadDashboardData() },
                colors = ButtonDefaults.buttonColors(containerColor = White),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Coba Lagi", color = Orange500, fontWeight = FontWeight.Bold)
            }
        }
        return
    }


    // ✅ MAIN UI - STABIL!
    Scaffold(
        topBar = {
            DashboardTopBar(
                userName = userName,
                onLogout = { authViewModel.logout() },
                isLogoutLoading = authState.isLoading,
                onNavigateToProfile = onNavigateToProfile,
                onNavigateToSearch = onNavigateToSearch
            )
        },
        containerColor = Orange500
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
        ) {
            // Header Section
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Orange500)
                    .padding(horizontal = 24.dp, vertical = 16.dp)
            ) {
                Text(
                    text = "Selamat Datang,",
                    style = MaterialTheme.typography.titleMedium,
                    color = White
                )
                Text(
                    text = "$userName!",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = White
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Statistics Cards
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    StatCard(
                        modifier = Modifier.weight(1f),
                        value = stats.totalAgenda.toString(),
                        label = "Agenda",
                        icon = Icons.Default.CalendarMonth
                    )
                    StatCard(
                        modifier = Modifier.weight(1f),
                        value = stats.totalMataKuliah.toString(),
                        label = "Mata Kuliah",
                        icon = Icons.Default.Description
                    )
                    StatCard(
                        modifier = Modifier.weight(1f),
                        value = stats.totalDokumen.toString(),
                        label = "Dokumen",
                        icon = Icons.Default.MenuBook
                    )
                    StatCard(
                        modifier = Modifier.weight(1f),
                        value = stats.totalPrestasi.toString(),
                        label = "Prestasi",
                        icon = Icons.Default.EmojiEvents
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Main Content Section
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .clip(RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp))
                    .background(White)
                    .padding(24.dp)
            ) {
                // Menu Grid
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    MenuCard(
                        modifier = Modifier.weight(1f),
                        title = "Dokumen Akademik",
                        subtitle = "Kelola dokumen akademik kamu dengan mudah",
                        icon = Icons.Default.Folder,
                        backgroundColor = Orange100,
                        iconColor = Orange500,
                        onClick = onNavigateToAkademik
                    )

                    MenuCard(
                        modifier = Modifier.weight(1f),
                        title = "Mata Kuliah",
                        subtitle = "Pengelolaan tugas & jadwal",
                        icon = Icons.Default.Book,
                        backgroundColor = Color.Blue.copy(alpha = 0.1f),
                        iconColor = Orange500,
                        onClick = onNavigateToMataKuliah
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    MenuCard(
                        modifier = Modifier.weight(1f),
                        title = "Agenda & Reminder",
                        subtitle = "Pengingat jadwal & acara penting",
                        icon = Icons.Default.Notifications,
                        backgroundColor = Warning.copy(alpha = 0.1f),
                        iconColor = Warning,
                        onClick = onNavigateToAgenda
                    )

                    MenuCard(
                        modifier = Modifier.weight(1f),
                        title = "Arsip Prestasi",
                        subtitle = "Pengelolaan dokumen prestasi",
                        icon = Icons.Default.Star,
                        backgroundColor = Success.copy(alpha = 0.1f),
                        iconColor = Success,
                        onClick = onNavigateToPrestasi
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}

// =========================================================================
// KOMPONEN PENDUKUNG - SAMA PERSIS
// =========================================================================

@Composable
fun DashboardTopBar(
    userName: String,
    onLogout: () -> Unit,
    isLogoutLoading: Boolean,
    onNavigateToProfile: () -> Unit,
    onNavigateToSearch: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Orange500)
            .padding(horizontal = 24.dp, vertical = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(
                text = "ArsiSI",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = White
            )
            Text(
                text = "Manajemen Arsip Mahasiswa SI",
                style = MaterialTheme.typography.bodySmall,
                color = White.copy(alpha = 0.8f)
            )
        }

        Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = onLogout,
                enabled = !isLogoutLoading,
                modifier = Modifier
                    .size(40.dp)
                    .background(White.copy(alpha = 0.2f), CircleShape)
            ) {
                if (isLogoutLoading) {
                    CircularProgressIndicator(color = White, modifier = Modifier.size(24.dp), strokeWidth = 2.dp)
                } else {
                    Icon(
                        imageVector = Icons.Default.ExitToApp,
                        contentDescription = "Logout",
                        tint = White
                    )
                }
            }

            IconButton(
                onClick = onNavigateToSearch,
                modifier = Modifier
                    .size(40.dp)
                    .background(White.copy(alpha = 0.2f), CircleShape)
            ) {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "Search",
                    tint = White
                )
            }

            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(White)
                    .clickable { onNavigateToProfile() },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = "Profile",
                    tint = Orange500
                )
            }
        }
    }
}

@Composable
fun StatCard(
    modifier: Modifier = Modifier,
    value: String,
    label: String,
    icon: ImageVector
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = White),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = Orange500,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = value,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )
            Text(
                text = label,
                style = MaterialTheme.typography.bodySmall,
                color = TextSecondary,
                fontSize = 10.sp
            )
        }
    }
}

@Composable
fun MenuCard(
    modifier: Modifier = Modifier,
    title: String,
    subtitle: String,
    icon: ImageVector,
    backgroundColor: Color,
    iconColor: Color,
    onClick: () -> Unit
) {
    Card(
        modifier = modifier
            .height(160.dp)
            .clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = White),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .background(backgroundColor, RoundedCornerShape(12.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = title,
                    tint = iconColor,
                    modifier = Modifier.size(24.dp)
                )
            }

            Column {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = TextPrimary
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondary,
                    lineHeight = 16.sp
                )
            }
        }
    }
}
