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
private val DarkColorScheme = darkColorScheme(
    primary = Orange500,
    onPrimary = Black,
    primaryContainer = Orange700,
    onPrimaryContainer = White,
    secondary = Blue500,
    onSecondary = White,
    secondaryContainer = Blue600,
    onSecondaryContainer = White,
    tertiary = Orange700,
    onTertiary = White,
    tertiaryContainer = Orange600,
    onTertiaryContainer = White,
    background = Gray900,
    onBackground = White,
    surface = Gray800,
    onSurface = White,
    surfaceVariant = Gray700,
    onSurfaceVariant = Gray300,
    outline = Gray500,
    error = Error,
    onError = White,
    errorContainer = Error,
    onErrorContainer = White
)
private val LightColorScheme = lightColorScheme(
    primary = Orange700,
    onPrimary = White,
    primaryContainer = Orange600,
    onPrimaryContainer = White,
    secondary = Blue500,
    onSecondary = White,
    secondaryContainer = Blue600,
    onSecondaryContainer = White,
    tertiary = Orange600,
    onTertiary = White,
    tertiaryContainer = Orange500,
    onTertiaryContainer = White,
    background = Background,
    onBackground = TextPrimary,
    surface = Surface,
    onSurface = TextPrimary,
    surfaceVariant = Gray100,
    onSurfaceVariant = TextSecondary,
    outline = Gray400,
    error = Error,
    onError = White,
    errorContainer = Error,
    onErrorContainer = White
)
@Composable
fun ArsiSI_frontendTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
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
        typography = Typography,
        content = content
    )
}