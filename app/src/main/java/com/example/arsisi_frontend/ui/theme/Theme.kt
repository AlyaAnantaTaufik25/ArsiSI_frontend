package com.example.arsisi_frontend.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
// --- HAPUS SEMUA IMPOR WARNA SPESIFIK ---

// Import semua yang ada di package ui.theme (Color.kt)
import com.example.arsisi_frontend.ui.theme.* private val DarkColorScheme = darkColorScheme(
    // WARNA UTAMA ANDA: ORANGE
    primary = PrimaryOrange,
    secondary = PurpleGrey80,
    tertiary = Pink80,
    background = TextDark,
    surface = TextDark,
    onPrimary = Color.White
)

private val LightColorScheme = lightColorScheme(
    // WARNA UTAMA ANDA: ORANGE
    primary = PrimaryOrange,
    secondary = PurpleGrey40,
    tertiary = Pink40,

    background = Color(0xFFFFFFFF),
    surface = Color(0xFFFFFFFE),
    onPrimary = Color.White,
    onBackground = TextDark
)

@Composable
fun Arsisi_frontendTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}