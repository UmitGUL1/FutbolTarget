package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = LuxuryPrimary,
    onPrimary = LuxurySurfaceContainerLowest,
    primaryContainer = LuxuryPrimaryContainer,
    onPrimaryContainer = Color.White,
    secondary = LuxurySecondary,
    onSecondary = Color(0xFF241A00),
    secondaryContainer = Color(0xFF876900),
    onSecondaryContainer = Color(0xFFFFEFCD),
    tertiary = LuxuryTertiary,
    onTertiary = Color(0xFF003824),
    tertiaryContainer = Color(0xFF00A571),
    onTertiaryContainer = Color(0xFF00311F),
    background = LuxurySurface,
    onBackground = LuxuryOnSurface,
    surface = LuxurySurfaceContainer,
    onSurface = LuxuryOnSurface,
    surfaceVariant = LuxurySurfaceContainerHighest,
    onSurfaceVariant = LuxuryOutline,
    outline = LuxuryOutlineVariant,
    outlineVariant = Color(0x1FFFFFFF),
    error = LuxuryError,
    onError = Color(0xFF690005)
)

private val LightColorScheme = lightColorScheme(
    primary = LightPrimaryBlue,
    onPrimary = Color.White,
    primaryContainer = Color(0xFFDBEAFE),
    onPrimaryContainer = Color(0xFF1E40AF),
    secondary = GoldAccent,
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFFEF3C7),
    onSecondaryContainer = Color(0xFF92400E),
    tertiary = LightSecondaryBlue,
    background = LightMainBackground,
    onBackground = LightPrimaryText,
    surface = LightPrimarySurface,
    onSurface = LightPrimaryText,
    surfaceVariant = LightSecondarySurface,
    onSurfaceVariant = LightSecondaryText,
    outline = LightBorder,
    error = SemanticError,
    onError = Color.White
)

@Composable
fun FutbolTargetTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    FutbolTargetTheme(darkTheme = darkTheme, content = content)
}
