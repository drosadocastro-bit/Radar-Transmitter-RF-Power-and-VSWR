package com.example.ui.theme

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
    primary = RfPrimaryDark,
    onPrimary = RfOnPrimaryDark,
    primaryContainer = RfPrimaryContainerDark,
    onPrimaryContainer = RfOnPrimaryContainerDark,
    secondary = RfSecondaryDark,
    onSecondary = RfOnSecondaryDark,
    secondaryContainer = RfSecondaryContainerDark,
    onSecondaryContainer = RfOnSecondaryContainerDark,
    tertiary = RfTertiaryDark,
    onTertiary = RfOnTertiaryDark,
    tertiaryContainer = RfTertiaryContainerDark,
    onTertiaryContainer = RfOnTertiaryContainerDark,
    background = RfBackgroundDark,
    onBackground = RfOnBackgroundDark,
    surface = RfSurfaceDark,
    onSurface = RfOnSurfaceDark,
    surfaceVariant = RfSurfaceVariantDark,
    onSurfaceVariant = RfOnSurfaceVariantDark,
    outline = RfOutlineDark
)

private val LightColorScheme = lightColorScheme(
    primary = RfPrimaryLight,
    onPrimary = RfOnPrimaryLight,
    primaryContainer = RfPrimaryContainerLight,
    onPrimaryContainer = RfOnPrimaryContainerLight,
    secondary = RfSecondaryLight,
    onSecondary = RfOnSecondaryLight,
    secondaryContainer = RfSecondaryContainerLight,
    onSecondaryContainer = RfOnSecondaryContainerLight,
    tertiary = RfTertiaryLight,
    onTertiary = RfOnTertiaryLight,
    tertiaryContainer = RfTertiaryContainerLight,
    onTertiaryContainer = RfOnTertiaryContainerLight,
    background = RfBackgroundLight,
    onBackground = RfOnBackgroundLight,
    surface = RfSurfaceLight,
    onSurface = RfOnSurfaceLight,
    surfaceVariant = RfSurfaceVariantLight,
    onSurfaceVariant = RfOnSurfaceVariantLight,
    outline = RfOutlineLight
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false, // Set false to prioritize our polished RF instrumentation palette
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
