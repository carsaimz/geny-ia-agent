package com.genyassistant.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val DarkColorScheme = darkColorScheme(
    primary = NeonBlue,
    secondary = NeonBlueDark,
    tertiary = NeonBlueGlow,
    background = DarkBackground,
    onBackground = Color(0xFFEEEEEE),
    surface = SurfaceColor,
    onSurface = Color(0xFFEEEEEE),
    outline = Color(0xFF202020),
)

private val LightColorScheme = lightColorScheme(
    primary = NeonBlue,
    secondary = NeonBlueDark,
    tertiary = NeonBlueGlow,
    background = Color(0xFFF9F9F6),
    onBackground = Color(0xFF3B3B3B),
    surface = Color(0xFFF3F3F1),
    onSurface = Color(0xFF3B3B3B),
    outline = Color(0xFFDBDBD8),
)

@Composable
fun GenyAssistantTheme(
    darkTheme: Boolean = true, // Forçar tema escuro para o estilo néon
    dynamicColor: Boolean = false, // Desativar cor dinâmica para manter o estilo néon
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme
    
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.background.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
