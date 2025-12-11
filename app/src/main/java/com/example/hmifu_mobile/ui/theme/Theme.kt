package com.example.hmifu_mobile.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

/**
 * HMIF U-Mobile Dark Color Scheme
 * OLED-optimized dark mode first design for developers
 * Material 3 Expressive aesthetic
 */
private val DarkColorScheme = darkColorScheme(
    // Primary - HMIF Blue
    primary = HmifBlue,
    onPrimary = Color.White,
    primaryContainer = HmifBlueDark,
    onPrimaryContainer = HmifBlueLight,

    // Secondary - Orange (CTAs)
    secondary = HmifOrange,
    onSecondary = Color.White,
    secondaryContainer = HmifOrangeDark,
    onSecondaryContainer = HmifOrangeLight,

    // Tertiary - Purple (Gamification)
    tertiary = HmifPurple,
    onTertiary = Color.White,
    tertiaryContainer = HmifPurpleDark,
    onTertiaryContainer = HmifPurpleLight,

    // Background & Surface (OLED optimized)
    background = SurfaceDark,
    onBackground = OnSurfaceDark,
    surface = SurfaceDark,
    onSurface = OnSurfaceDark,
    surfaceVariant = SurfaceDarkVariant,
    onSurfaceVariant = OnSurfaceDarkVariant,
    surfaceContainerLowest = SurfaceDark,
    surfaceContainerLow = SurfaceDarkVariant,
    surfaceContainer = SurfaceDarkContainer,
    surfaceContainerHigh = SurfaceDarkElevated,
    surfaceContainerHighest = SurfaceDarkElevated,

    // Outline
    outline = OnSurfaceDarkVariant,
    outlineVariant = Color(0xFF444746),

    // Error
    error = Error,
    onError = Color.White,
    errorContainer = ErrorDark,
    onErrorContainer = ErrorLight,

    // Inverse
    inverseSurface = SurfaceLight,
    inverseOnSurface = OnSurfaceLight,
    inversePrimary = HmifBlueDark,

    // Scrim
    scrim = Color.Black
)

/**
 * HMIF U-Mobile Light Color Scheme
 * Clean and professional light theme
 */
private val LightColorScheme = lightColorScheme(
    // Primary - HMIF Blue
    primary = HmifBlue,
    onPrimary = Color.White,
    primaryContainer = HmifBlueLight,
    onPrimaryContainer = HmifBlueDark,

    // Secondary - Orange (CTAs)
    secondary = HmifOrange,
    onSecondary = Color.White,
    secondaryContainer = HmifOrangeLight,
    onSecondaryContainer = HmifOrangeDark,

    // Tertiary - Purple (Gamification)
    tertiary = HmifPurple,
    onTertiary = Color.White,
    tertiaryContainer = HmifPurpleLight,
    onTertiaryContainer = HmifPurpleDark,

    // Background & Surface
    background = SurfaceLight,
    onBackground = OnSurfaceLight,
    surface = SurfaceLight,
    onSurface = OnSurfaceLight,
    surfaceVariant = SurfaceLightVariant,
    onSurfaceVariant = OnSurfaceLightVariant,
    surfaceContainerLowest = Color.White,
    surfaceContainerLow = SurfaceLightVariant,
    surfaceContainer = SurfaceLightContainer,
    surfaceContainerHigh = SurfaceLightContainer,
    surfaceContainerHighest = Color(0xFFE0E0E0),

    // Outline
    outline = OnSurfaceLightVariant,
    outlineVariant = Color(0xFFCAC4D0),

    // Error
    error = Error,
    onError = Color.White,
    errorContainer = ErrorContainer,
    onErrorContainer = OnErrorContainer,

    // Inverse
    inverseSurface = SurfaceDark,
    inverseOnSurface = OnSurfaceDark,
    inversePrimary = HmifBlueLight,

    // Scrim
    scrim = Color.Black
)

/**
 * HMIF U-Mobile Theme
 *
 * Material 3 Expressive theme with:
 * - Custom HMIF brand colors
 * - OLED-optimized dark mode
 * - Design tokens for spacing, corner radius, elevation
 * - Custom typography with Poppins, Inter, JetBrains Mono
 *
 * @param darkTheme Whether to use dark theme. Defaults to system setting.
 * @param dynamicColor Whether to use dynamic colors on Android 12+.
 *                     Set to false to always use HMIF brand colors.
 * @param content The composable content to be themed.
 */
@Composable
fun HMIFUMobileTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Disabled dynamic color by default to maintain brand consistency
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

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window

            // Edge-to-edge with transparent status bar
            WindowCompat.setDecorFitsSystemWindows(window, false)

            // Set status bar color for Android 14 and below
            if (Build.VERSION.SDK_INT < 35) {
                @Suppress("DEPRECATION")
                window.statusBarColor = Color.Transparent.toArgb()
            }

            // Configure status bar icon colors
            WindowCompat.getInsetsController(window, view).apply {
                isAppearanceLightStatusBars = !darkTheme
                isAppearanceLightNavigationBars = !darkTheme
            }
        }
    }

    // Provide design tokens through CompositionLocalProvider
    CompositionLocalProvider(
        LocalHmifSpacing provides HmifSpacing(),
        LocalHmifCornerRadius provides HmifCornerRadius(),
        LocalHmifElevation provides HmifElevation(),
        LocalHmifSizes provides HmifSizes()
    ) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = Typography,
            content = content
        )
    }
}