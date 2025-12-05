package com.example.arsisi_frontend.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

// --- SCHEME DARK ---
private val DarkColorScheme = darkColorScheme(
    // PRIMARY
    primary = Orange500,        // Warna utama (misal, tombol, header)
    onPrimary = Black,          // Teks di atas primary
    primaryContainer = Orange700, // Kunci wajib M3
    onPrimaryContainer = White, // Kunci wajib M3

    // SECONDARY
    secondary = Blue500,
    onSecondary = White,
    secondaryContainer = Blue600, // Kunci wajib M3
    onSecondaryContainer = White, // Kunci wajib M3

    // TERTIARY
    tertiary = Orange700,
    onTertiary = White,         // Kunci wajib M3
    tertiaryContainer = Orange600, // Kunci wajib M3
    onTertiaryContainer = White, // Kunci wajib M3

    // BACKGROUND & SURFACE
    background = Gray900,
    onBackground = White,
    surface = Gray800,
    onSurface = White,
    surfaceVariant = Gray700,   // Kunci wajib M3
    onSurfaceVariant = Gray300, // Kunci wajib M3

    // LAINNYA
    outline = Gray500,          // Kunci wajib M3
    error = Error,
    onError = White,
    errorContainer = Error,     // Kunci wajib M3
    onErrorContainer = White    // Kunci wajib M3
)

// --- SCHEME LIGHT ---
private val LightColorScheme = lightColorScheme(
    // PRIMARY
    primary = Orange700,
    onPrimary = White,
    primaryContainer = Orange600, // Kunci wajib M3
    onPrimaryContainer = White, // Kunci wajib M3

    // SECONDARY
    secondary = Blue500,
    onSecondary = White,
    secondaryContainer = Blue600, // Kunci wajib M3
    onSecondaryContainer = White, // Kunci wajib M3

    // TERTIARY
    tertiary = Orange600,
    onTertiary = White,         // Kunci wajib M3
    tertiaryContainer = Orange500, // Kunci wajib M3
    onTertiaryContainer = White, // Kunci wajib M3

    // BACKGROUND & SURFACE
    background = Background, // Gray50
    onBackground = TextPrimary,
    surface = Surface,       // White
    onSurface = TextPrimary,
    surfaceVariant = Gray100, // Kunci wajib M3 (Warna terang)
    onSurfaceVariant = TextSecondary, // Kunci wajib M3

    // LAINNYA
    outline = Gray400,          // Kunci wajib M3
    error = Error,
    onError = White,
    errorContainer = Error,     // Kunci wajib M3
    onErrorContainer = White    // Kunci wajib M3

    // TIDAK ADA PARAMETER LAIN DI SINI!
)

@Composable
fun ArsiSI_frontendTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false, // Set dynamicColor ke false untuk mencegah potensi konflik
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography, // Asumsikan Typography ada di Type.kt
        content = content
    )
}