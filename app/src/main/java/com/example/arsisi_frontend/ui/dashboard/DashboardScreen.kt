package com.example.arsisi_frontend.ui.dashboard

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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun DashboardScreen(
    userName: String,
    onLogout: () -> Unit,
    onNavigateToAkademik: () -> Unit,
    onNavigateToPrestasi: () -> Unit,
    onNavigateToAgenda: () -> Unit,
    onNavigateToMataKuliah: () -> Unit,
    viewModel: DashboardViewModel
) {
    val stats by viewModel.stats.collectAsState()

    Scaffold(
        topBar = {
            DashboardTopBar(
                userName = userName,
                onLogout = onLogout
            )
        },
        containerColor = MaterialTheme.colorScheme.primary
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
                    .background(MaterialTheme.colorScheme.primary)
                    .padding(horizontal = 24.dp, vertical = 16.dp)
            ) {
                Text(
                    text = "Selamat Datang,",
                    style = MaterialTheme.typography.titleMedium,
                    color = Color.White
                )
                Text(
                    text = "$userName!",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
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
                        icon = Icons.Default.Book
                    )
                    StatCard(
                        modifier = Modifier.weight(1f),
                        value = stats.totalDokumen.toString(),
                        label = "Dokumen",
                        icon = Icons.Default.Description
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
                    .clip(RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp))
                    .background(Color.White)
                    .padding(24.dp)
            ) {
                Text(
                    text = "Menu Utama",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Menu Grid
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    MenuCard(
                        modifier = Modifier.weight(1f),
                        title = "Dokumen Akademik",
                        subtitle = "Kelola dokumen akademik",
                        icon = Icons.Default.Folder,
                        backgroundColor = Color(0xFFFFE5CC),
                        iconColor = Color(0xFFFF9800),
                        onClick = onNavigateToAkademik
                    )

                    MenuCard(
                        modifier = Modifier.weight(1f),
                        title = "Mata Kuliah",
                        subtitle = "Pengelolaan tugas & jadwal",
                        icon = Icons.Default.Book,
                        backgroundColor = Color(0xFFE3F2FD),
                        iconColor = Color(0xFF2196F3),
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
                        subtitle = "Pengingat jadwal penting",
                        icon = Icons.Default.Notifications,
                        backgroundColor = Color(0xFFFFF9C4),
                        iconColor = Color(0xFFFFC107),
                        onClick = onNavigateToAgenda
                    )

                    MenuCard(
                        modifier = Modifier.weight(1f),
                        title = "Arsip Prestasi",
                        subtitle = "Dokumentasi prestasi",
                        icon = Icons.Default.Star,
                        backgroundColor = Color(0xFFC8E6C9),
                        iconColor = Color(0xFF4CAF50),
                        onClick = onNavigateToPrestasi
                    )
                }
            }
        }
    }
}

@Composable
fun DashboardTopBar(
    userName: String,
    onLogout: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.primary)
            .padding(horizontal = 24.dp, vertical = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(
                text = "ArsiSI",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
            Text(
                text = "Manajemen Arsip Mahasiswa SI",
                style = MaterialTheme.typography.bodySmall,
                color = Color.White.copy(alpha = 0.8f)
            )
        }

        IconButton(
            onClick = onLogout,
            modifier = Modifier
                .size(40.dp)
                .background(Color.White.copy(alpha = 0.2f), CircleShape)
        ) {
            Icon(
                imageVector = Icons.Default.ExitToApp,
                contentDescription = "Logout",
                tint = Color.White
            )
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
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = value,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
            Text(
                text = label,
                style = MaterialTheme.typography.bodySmall,
                color = Color.Gray,
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
        colors = CardDefaults.cardColors(containerColor = Color.White),
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
                    color = Color.Black
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray,
                    lineHeight = 16.sp
                )
            }
        }
    }
}
