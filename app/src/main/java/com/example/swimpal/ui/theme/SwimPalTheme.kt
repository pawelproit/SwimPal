package com.example.swimpal.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color


private val LightPrimary = Color(0xFF00BCD4)
private val LightSecondary = Color(0xFF0288D1)
private val LightTertiary = Color(0xFF4DD0E1)
private val LightBackground = Color(0xFFF0F8FF)
private val LightSurface = Color(0xFFFFFFFF)
private val LightOnPrimary = Color(0xFFFFFFFF)
private val LightOnSecondary = Color(0xFFFFFFFF)
private val LightOnBackground = Color(0xFF0288D1)
private val LightOnSurface = Color(0xFF01579B)


private val DarkPrimary = Color(0xFF4DD0E1)
private val DarkSecondary = Color(0xFF00BCD4)
private val DarkTertiary = Color(0xFF80DEEA)
private val DarkBackground = Color(0xFF01579B)
private val DarkSurface = Color(0xFF0277BD)
private val DarkOnPrimary = Color(0xFF01579B)
private val DarkOnSecondary = Color(0xFF01579B)
private val DarkOnBackground = Color(0xFFE1F5FE)
private val DarkOnSurface = Color(0xFFFFFFFF)

private val LightColorScheme = lightColorScheme(
    primary = LightPrimary,
    onPrimary = LightOnPrimary,
    primaryContainer = LightTertiary,
    onPrimaryContainer = LightOnBackground,
    secondary = LightSecondary,
    onSecondary = LightOnSecondary,
    secondaryContainer = Color(0xFFB3E5FC),
    onSecondaryContainer = LightOnBackground,
    tertiary = LightTertiary,
    onTertiary = LightOnPrimary,
    background = LightBackground,
    onBackground = LightOnBackground,
    surface = LightSurface,
    onSurface = LightOnSurface,
    surfaceVariant = Color(0xFFE1F5FE),
    onSurfaceVariant = LightOnBackground,
    error = Color(0xFFE53935),
    onError = Color(0xFFFFFFFF)
)

private val DarkColorScheme = darkColorScheme(
    primary = DarkPrimary,
    onPrimary = DarkOnPrimary,
    primaryContainer = DarkTertiary,
    onPrimaryContainer = DarkOnSurface,
    secondary = DarkSecondary,
    onSecondary = DarkOnSecondary,
    secondaryContainer = Color(0xFF006064),
    onSecondaryContainer = DarkOnSurface,
    tertiary = DarkTertiary,
    onTertiary = DarkOnPrimary,
    background = DarkBackground,
    onBackground = DarkOnBackground,
    surface = DarkSurface,
    onSurface = DarkOnSurface,
    surfaceVariant = Color(0xFF004D61),
    onSurfaceVariant = DarkOnBackground,
    error = Color(0xFFEF5350),
    onError = Color(0xFF000000)
)

@Composable
fun SwimPalTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) {
        DarkColorScheme
    } else {
        LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
