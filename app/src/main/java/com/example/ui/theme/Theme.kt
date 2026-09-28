package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

val LocalHighContrastMode = compositionLocalOf { false }

private val DarkColorScheme = darkColorScheme(
    primary = SkyBlueDarkPrimary,
    onPrimary = SkyBlueDarkOnPrimary,
    primaryContainer = SkyBluePrimary,
    onPrimaryContainer = Color.White,
    secondary = PinkDarkSecondary,
    onSecondary = PinkDarkOnSecondary,
    secondaryContainer = Color(0xFF831843),
    onSecondaryContainer = Color(0xFFFCE7F3),
    tertiary = RoseTertiary,
    tertiaryContainer = Color(0xFF4C0519),
    onTertiary = Color.White,
    onTertiaryContainer = Color(0xFFFFE4E6),
    background = DarkBackground,
    surface = DarkSurface,
    surfaceVariant = DarkSurfaceVariant,
    onBackground = DarkOnBackground,
    onSurface = DarkOnSurface,
    onSurfaceVariant = DarkOnSurfaceVariant
)

private val LightColorScheme = lightColorScheme(
    primary = SkyBluePrimary,
    onPrimary = SkyBlueOnPrimary,
    primaryContainer = SkyBluePrimaryContainer,
    onPrimaryContainer = SkyBlueOnPrimaryContainer,
    secondary = PinkSecondary,
    onSecondary = PinkOnSecondary,
    secondaryContainer = PinkSecondaryContainer,
    onSecondaryContainer = PinkOnSecondaryContainer,
    tertiary = RoseTertiary,
    tertiaryContainer = RoseTertiaryContainer,
    onTertiaryContainer = RoseOnTertiaryContainer,
    background = SkyPinkBackground,
    surface = SkyPinkSurface,
    surfaceVariant = SkyPinkSurfaceVariant,
    onBackground = TextPrimary,
    onSurface = TextPrimary,
    onSurfaceVariant = TextSecondary
)

private val HighContrastLightColorScheme = lightColorScheme(
    primary = HighContrastPrimary,
    onPrimary = HighContrastOnPrimary,
    primaryContainer = HighContrastPrimaryContainer,
    onPrimaryContainer = HighContrastOnPrimaryContainer,
    secondary = HighContrastSecondary,
    onSecondary = HighContrastOnSecondary,
    secondaryContainer = HighContrastSecondaryContainer,
    onSecondaryContainer = HighContrastOnSecondaryContainer,
    tertiary = HighContrastTertiary,
    tertiaryContainer = HighContrastTertiaryContainer,
    onTertiaryContainer = HighContrastOnTertiaryContainer,
    background = HighContrastBackground,
    surface = HighContrastSurface,
    surfaceVariant = HighContrastSurfaceVariant,
    onBackground = HighContrastTextPrimary,
    onSurface = HighContrastTextPrimary,
    onSurfaceVariant = HighContrastTextSecondary,
    outline = HighContrastOutline,
    outlineVariant = HighContrastTextSecondary
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = false,
    highContrast: Boolean = false,
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit,
) {
    val colorScheme = when {
        highContrast -> HighContrastLightColorScheme
        darkTheme -> DarkColorScheme
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            dynamicLightColorScheme(context)
        }
        else -> LightColorScheme
    }

    CompositionLocalProvider(LocalHighContrastMode provides highContrast) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = Typography,
            content = content
        )
    }
}
