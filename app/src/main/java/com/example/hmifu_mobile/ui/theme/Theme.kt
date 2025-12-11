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
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

/**
 * HMIF U-Mobile Dark Color Scheme
 * Dark mode first design for developers
 */
private val DarkColorScheme = darkColorScheme(
    primary = HmifBlue,
    onPrimary = SurfaceLight,
    primaryContainer = HmifBlueDark,
    onPrimaryContainer = HmifBlueLight,
    secondary = HmifSecondaryBlue,
    onSecondary = SurfaceLight,
    secondaryContainer = HmifSecondaryBlueDark,
    onSecondaryContainer = HmifSecondaryBlueLight,
    tertiary = HmifPurple,
    onTertiary = SurfaceLight,
    tertiaryContainer = HmifPurpleDark,
    onTertiaryContainer = HmifPurpleLight,
    background = SurfaceDark,
    onBackground = OnSurfaceDark,
    surface = SurfaceDark,
    onSurface = OnSurfaceDark,
    error = Error,
    onError = SurfaceLight
)

/**
 * HMIF U-Mobile Light Color Scheme
 */
private val LightColorScheme = lightColorScheme(
    primary = HmifBlue,
    onPrimary = SurfaceLight,
    primaryContainer = HmifBlueLight,
    onPrimaryContainer = HmifBlueDark,
    secondary = HmifSecondaryBlue,
    onSecondary = SurfaceLight,
    secondaryContainer = HmifSecondaryBlueLight,
    onSecondaryContainer = HmifSecondaryBlueDark,
    tertiary = HmifPurple,
    onTertiary = SurfaceLight,
    tertiaryContainer = HmifPurpleLight,
    onTertiaryContainer = HmifPurpleDark,
    background = SurfaceLight,
    onBackground = OnSurfaceLight,
    surface = SurfaceLight,
    onSurface = OnSurfaceLight,
    error = Error,
    onError = SurfaceLight
)

/**
 * HMIF U-Mobile Theme
 *
 * @param darkTheme Whether to use dark theme. Defaults to system setting.
 * @param dynamicColor Whether to use dynamic colors on Android 12+. Defaults to true.
 * @param content The composable content to be themed.
 */
@Composable
fun HMIFUMobileTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is disabled to enforce HMIF Brand Identity
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        // Dynamic colors disabled by project plan
        // dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
        //     val context = LocalContext.current
        //     if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        // }

        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window

            // FIX: Only set statusBarColor for Android 14 (API 34) and below.
            // On Android 15+, the system enforces Edge-to-Edge, making this property deprecated/ignored.
            if (Build.VERSION.SDK_INT < 35) { // 35 is VANILLA_ICE_CREAM
                @Suppress("DEPRECATION")
                window.statusBarColor = colorScheme.primary.toArgb()
            }

            // This is still required to ensure status bar icons (time, battery) are visible
            // (e.g. white icons on dark theme, black icons on light theme)
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        content = content
    )
}