package com.example.arsisi_frontend.ui.splash

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.arsisi_frontend.R
import com.example.arsisi_frontend.data.model.OnboardingData
import com.example.arsisi_frontend.ui.theme.Orange500
import com.example.arsisi_frontend.ui.theme.White
import com.example.arsisi_frontend.ui.theme.Gray100
import com.example.arsisi_frontend.ui.theme.Gray300
import com.example.arsisi_frontend.ui.theme.Gray500
import com.example.arsisi_frontend.ui.theme.Gray600
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.rememberPagerState
import kotlinx.coroutines.launch
import com.example.arsisi_frontend.navigation.Screen
import com.example.arsisi_frontend.navigation.NavigationRoute // Pastikan ini di-import jika berada di package lain
import kotlinx.coroutines.delay

/**
 * Composable utama untuk Splash dan Onboarding.
 */
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun SplashScreen(
    onNavigateToLogin: () -> Unit,
    onNavigateToRegister: () -> Unit,
    onNavigateToDashboard: () -> Unit,
    isFirstTime: Boolean,
    userName: String? // Parameter tidak digunakan di UI tapi dijaga untuk konsistensi
) {
    val viewModel: SplashViewModel = viewModel()
    val routeState by viewModel.nextRoute.collectAsState()

    // Amankan callback navigasi agar stabil (BEST PRACTICE)
    val currentOnNavigateToLogin by rememberUpdatedState(onNavigateToLogin)
    val currentOnNavigateToRegister by rememberUpdatedState(onNavigateToRegister)
    val currentOnNavigateToDashboard by rememberUpdatedState(onNavigateToDashboard)

    // 1. Logika Navigasi Akhir: Dipicu HANYA jika rute sudah ditentukan oleh ViewModel
    LaunchedEffect(routeState) {
        if (routeState is NavigationRoute.Navigate) {
            val route = (routeState as NavigationRoute.Navigate).route
            when (route) {
                Screen.Login.route -> currentOnNavigateToLogin()
                Screen.Dashboard.route -> currentOnNavigateToDashboard()
                // Jika rute adalah Screen.Splash.route, biarkan OnboardingContent yang tampil
            }
        }
    }

    // 2. Penentuan UI: Tampilkan UI berdasarkan state isFirstTime
    if (isFirstTime) {
        OnboardingContent(
            onNavigateToRegister = {
                // Aksi tombol 'Mulai Sekarang' (akhir Onboarding)
                currentOnNavigateToRegister()
            }
        )
    } else {
        // Tampilkan Splash loading atau Splash Content
        SplashContent()
    }
}

// ====================================================================
// Composable SplashContent
// ====================================================================

@Composable
fun SplashContent() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(White),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Logo placeholder
            Box(
                modifier = Modifier
                    .size(150.dp)
                    .background(Orange500, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "ArsiSI",
                    style = MaterialTheme.typography.headlineMedium,
                    color = White,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Sistem Informasi Arsip Mahasiswa",
                style = MaterialTheme.typography.bodyLarge,
                color = Gray600,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(32.dp))

            CircularProgressIndicator(
                color = Orange500,
                modifier = Modifier.size(40.dp)
            )
        }
    }
}

// ====================================================================
// Composable OnboardingContent
// ====================================================================

@OptIn(ExperimentalPagerApi::class)
@Composable
fun OnboardingContent(
    onNavigateToRegister: () -> Unit
) {
    val pages = OnboardingData.getPages()
    val pagerState = rememberPagerState()
    val scope = rememberCoroutineScope() // Coroutine scope diperlukan untuk delay!

    // Pastikan dependensi yang diperlukan di-import:
    // import kotlinx.coroutines.launch
    // import kotlinx.coroutines.delay

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(White)
    ) {
        // Pager
        HorizontalPager(
            count = pages.size,
            state = pagerState,
            modifier = Modifier.weight(1f)
        ) { page ->
            OnboardingPage(pages[page])
        }

        // Indicator & Button
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 32.dp, vertical = 40.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // ... (Kode Indicator tidak berubah)
            Row(
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(bottom = 32.dp)
            ) {
                repeat(pages.size) { index ->
                    val color = if (pagerState.currentPage == index) Orange500 else Gray300
                    Box(
                        modifier = Modifier
                            .padding(horizontal = 4.dp)
                            .size(if (pagerState.currentPage == index) 24.dp else 8.dp, 8.dp)
                            .clip(RoundedCornerShape(4.dp))
                            .background(color)
                    )
                }
            }

            // Button
            if (pagerState.currentPage == pages.size - 1) {
                // Last page - Show "Mulai Sekarang" button
                Button(
                    // V V V V V PERBAIKAN PENTING V V V V V
                    onClick = {
                        scope.launch {
                            // Menambahkan penundaan singkat (misalnya 200ms)
                            // Ini memberikan waktu bagi HorizontalPager/LayoutNode untuk menyelesaikan
                            // proses disposal-nya sebelum NavHost sepenuhnya menghapus Composable.
                            delay(200)
                            onNavigateToRegister()
                        }
                    },
                    // ^ ^ ^ ^ ^ ^ ^ ^ ^ ^ ^ ^ ^ ^ ^ ^ ^ ^ ^
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Orange500
                    ),
                    shape = RoundedCornerShape(28.dp)
                ) {
                    Text(
                        text = "Mulai Sekarang",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            } else {
                // ... (Kode Navigation button tidak berubah)
                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .clip(CircleShape)
                        .background(Orange500),
                    contentAlignment = Alignment.Center
                ) {
                    IconButton(
                        onClick = {
                            val nextPage = pagerState.currentPage + 1
                            if (nextPage < pages.size) {
                                scope.launch {
                                    pagerState.animateScrollToPage(nextPage)
                                }
                            }
                        }
                    ) {
                        Icon(
                            painter = painterResource(android.R.drawable.ic_media_play),
                            contentDescription = "Next",
                            tint = White,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }
            }
        }
    }
}

// ... (OnboardingPage Composable Anda yang tidak berubah)
@Composable
fun OnboardingPage(page: com.example.arsisi_frontend.data.model.OnboardingPage) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Image placeholder
        Box(
            modifier = Modifier
                .size(300.dp)
                .background(Gray100, RoundedCornerShape(16.dp)),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = page.imageDescription,
                style = MaterialTheme.typography.bodyMedium,
                color = Gray500,
                textAlign = TextAlign.Center
            )
        }

        Spacer(modifier = Modifier.height(48.dp))

        Text(
            text = page.title,
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            color = Orange500,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = page.description,
            style = MaterialTheme.typography.bodyMedium,
            color = Gray600,
            textAlign = TextAlign.Center,
            lineHeight = 24.sp
        )
    }
}