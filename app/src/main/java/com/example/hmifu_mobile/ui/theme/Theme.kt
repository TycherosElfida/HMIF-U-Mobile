package com.example.hmifu_mobile.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.material3.Typography

/**
 * Premium Dark Color Scheme for HMIF U-Mobile
 * Designed with tech aesthetic - deep blues, vibrant accents, and glassmorphism-ready surfaces
 */
private val DarkColorScheme = darkColorScheme(
    primary = Primary,
    onPrimary = Color.White,
    primaryContainer = PrimaryContainer,
    onPrimaryContainer = PrimaryLight,
    
    secondary = Secondary,
    onSecondary = Color.Black,
    secondaryContainer = SecondaryContainer,
    onSecondaryContainer = SecondaryLight,
    
    tertiary = Tertiary,
    onTertiary = Color.White,
    tertiaryContainer = TertiaryContainer,
    onTertiaryContainer = Tertiary,
    
    background = BackgroundDark,
    onBackground = OnBackgroundDark,
    
    surface = SurfaceDark,
    onSurface = OnSurfaceDark,
    surfaceVariant = Color(0xFF1E1E35),
    onSurfaceVariant = TextSecondary,
    
    error = Error,
    onError = Color.White,
    errorContainer = ErrorContainer,
    onErrorContainer = Color(0xFFFFB4AB),
    
    outline = GlassBorder,
    outlineVariant = Color(0xFF2A2A45)
)

private val LightColorScheme = lightColorScheme(
    primary = PrimaryDark,
    onPrimary = Color.White,
    primaryContainer = Color(0xFFE8E5FF),
    onPrimaryContainer = PrimaryDark,
    
    secondary = SecondaryDark,
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFD0F4FF),
    onSecondaryContainer = SecondaryDark,
    
    tertiary = Tertiary,
    onTertiary = Color.White,
    tertiaryContainer = Color(0xFFFFD9E4),
    onTertiaryContainer = TertiaryContainer,
    
    background = BackgroundLight,
    onBackground = OnBackgroundLight,
    
    surface = SurfaceLight,
    onSurface = OnSurfaceLight,
    surfaceVariant = Color(0xFFF0F0F5),
    onSurfaceVariant = Color(0xFF606070),
    
    error = Error,
    onError = Color.White,
    errorContainer = Color(0xFFFFDAD6),
    onErrorContainer = Color(0xFF410002),
    
    outline = Color(0xFFD0D0E0),
    outlineVariant = Color(0xFFE0E0F0)
)

@Composable
fun HMIFUMobileTheme(
    darkTheme: Boolean = true, // Default to dark theme for tech aesthetic
    dynamicColor: Boolean = false, // Disable dynamic color to maintain brand consistency
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

    // Edge-to-edge is now handled via enableEdgeToEdge() in MainActivity
    // Status bar and navigation bar colors are controlled there

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}