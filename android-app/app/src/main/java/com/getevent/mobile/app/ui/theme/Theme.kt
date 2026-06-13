package com.getevent.mobile.app.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColorScheme = lightColorScheme(
    primary = IndigoDeep,
    onPrimary = Color.White,
    primaryContainer = IndigoLight.copy(alpha = 0.2f),
    onPrimaryContainer = IndigoDeep,
    secondary = TealAccent,
    onSecondary = Color.White,
    secondaryContainer = TealLight.copy(alpha = 0.35f),
    onSecondaryContainer = TealAccent,
    tertiary = CoralAccent,
    onTertiary = Color.White,
    tertiaryContainer = CoralLight.copy(alpha = 0.4f),
    onTertiaryContainer = Color(0xFFBF360C),
    background = SurfaceLight,
    onBackground = Color(0xFF1C1B1F),
    surface = Color.White,
    onSurface = Color(0xFF1C1B1F),
    onSurfaceVariant = OnSurfaceMuted,
    surfaceVariant = Color(0xFFE8EAF6),
    outline = Color(0xFF9AA5B1)
)

private val DarkColorScheme = darkColorScheme(
    primary = IndigoLight,
    onPrimary = Color(0xFF0D1238),
    primaryContainer = IndigoDeep,
    onPrimaryContainer = Color(0xFFDDE1FF),
    secondary = TealLight,
    onSecondary = Color(0xFF003731),
    secondaryContainer = TealAccent,
    onSecondaryContainer = Color(0xFFB2DFDB),
    tertiary = CoralLight,
    onTertiary = Color(0xFF4A1510),
    tertiaryContainer = CoralAccent,
    onTertiaryContainer = Color(0xFFFFDAD4),
    background = Color(0xFF121318),
    onBackground = Color(0xFFE6E1E5),
    surface = Color(0xFF1C1B1F),
    onSurface = Color(0xFFE6E1E5),
    onSurfaceVariant = Color(0xFFCAC4D0),
    surfaceVariant = Color(0xFF2D2F42),
    outline = Color(0xFF938F99)
)

@Composable
fun GetEventTheme(
    darkTheme: Boolean = false,
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme,
        typography = GetEventTypography,
        shapes = GetEventShapes,
        content = content
    )
}
