package com.example.hmifu_mobile.ui.theme

import androidx.compose.ui.graphics.Color

/**
 * HMIF U-Mobile Design System Colors
 * Material 3 Expressive + Glassmorphism palette
 */

// ════════════════════════════════════════════════════════════════
// PRIMARY BRAND COLORS - HMIF Blue
// ════════════════════════════════════════════════════════════════
val HmifBlue = Color(0xFF1E88E5)
val HmifBlueLight = Color(0xFF6AB7FF)
val HmifBlueDark = Color(0xFF005CB2)

// ════════════════════════════════════════════════════════════════
// SECONDARY COLORS - Orange (CTAs, action buttons)
// ════════════════════════════════════════════════════════════════
val HmifOrange = Color(0xFFFF6F00)
val HmifOrangeLight = Color(0xFFFFA040)
val HmifOrangeDark = Color(0xFFC43E00)

// ════════════════════════════════════════════════════════════════
// TERTIARY COLORS - Purple (badges, gamification)
// ════════════════════════════════════════════════════════════════
val HmifPurple = Color(0xFF5E35B1)
val HmifPurpleLight = Color(0xFF9162E4)
val HmifPurpleDark = Color(0xFF280680)

// ════════════════════════════════════════════════════════════════
// ACCENT COLORS
// ════════════════════════════════════════════════════════════════
val AccentTeal = Color(0xFF00BCD4)
val AccentTealLight = Color(0xFF62EFFF)
val AccentTealDark = Color(0xFF008BA3)

// ════════════════════════════════════════════════════════════════
// GRADIENT COLORS (for hero elements)
// ════════════════════════════════════════════════════════════════
val GradientStart = HmifBlue           // #1E88E5
val GradientEnd = HmifPurple           // #5E35B1
val GradientOrangeStart = HmifOrange   // #FF6F00
val GradientOrangeEnd = HmifBlue       // #1E88E5

// ════════════════════════════════════════════════════════════════
// SURFACE COLORS - Light Mode
// ════════════════════════════════════════════════════════════════
val SurfaceLight = Color(0xFFFFFFFF)
val SurfaceLightVariant = Color(0xFFF5F5F5)
val SurfaceLightContainer = Color(0xFFEEEEEE)
val OnSurfaceLight = Color(0xFF1C1B1F)
val OnSurfaceLightVariant = Color(0xFF49454F)

// ════════════════════════════════════════════════════════════════
// SURFACE COLORS - Dark Mode (OLED optimized)
// ════════════════════════════════════════════════════════════════
val SurfaceDark = Color(0xFF121212)
val SurfaceDarkVariant = Color(0xFF1E1E1E)
val SurfaceDarkContainer = Color(0xFF2D2D2D)
val SurfaceDarkElevated = Color(0xFF383838)
val OnSurfaceDark = Color(0xFFE6E1E5)
val OnSurfaceDarkVariant = Color(0xFFCAC4D0)

// ════════════════════════════════════════════════════════════════
// GLASSMORPHISM COLORS
// ════════════════════════════════════════════════════════════════
val GlassLight = Color(0x1AFFFFFF)        // 10% white
val GlassLightBorder = Color(0x33FFFFFF)  // 20% white
val GlassLightShadow = Color(0x1A000000)  // 10% black

val GlassDark = Color(0x1AFFFFFF)         // 10% white on dark
val GlassDarkBorder = Color(0x26FFFFFF)   // 15% white
val GlassDarkShadow = Color(0x33000000)   // 20% black

// ════════════════════════════════════════════════════════════════
// SEMANTIC COLORS
// ════════════════════════════════════════════════════════════════
val Success = Color(0xFF4CAF50)
val SuccessLight = Color(0xFF81C784)
val SuccessDark = Color(0xFF388E3C)
val SuccessContainer = Color(0xFFE8F5E9)
val OnSuccessContainer = Color(0xFF1B5E20)

val Warning = Color(0xFFFFC107)
val WarningLight = Color(0xFFFFD54F)
val WarningDark = Color(0xFFFFA000)
val WarningContainer = Color(0xFFFFF8E1)
val OnWarningContainer = Color(0xFFFF6F00)

val Error = Color(0xFFEF5350)
val ErrorLight = Color(0xFFE57373)
val ErrorDark = Color(0xFFD32F2F)
val ErrorContainer = Color(0xFFFFEBEE)
val OnErrorContainer = Color(0xFFB71C1C)

val Info = Color(0xFF29B6F6)
val InfoLight = Color(0xFF4FC3F7)
val InfoDark = Color(0xFF0288D1)
val InfoContainer = Color(0xFFE1F5FE)
val OnInfoContainer = Color(0xFF01579B)

// ════════════════════════════════════════════════════════════════
// TEXT COLORS (for accessibility)
// ════════════════════════════════════════════════════════════════
val TextPrimaryLight = Color(0xFF1C1B1F)
val TextSecondaryLight = Color(0xFF49454F)
val TextDisabledLight = Color(0x61000000)  // 38% black

val TextPrimaryDark = Color(0xFFE6E1E5)
val TextSecondaryDark = Color(0xFFCAC4D0)
val TextDisabledDark = Color(0x61FFFFFF)   // 38% white

// ════════════════════════════════════════════════════════════════
// CATEGORY CHIP COLORS (for announcements, events)
// ════════════════════════════════════════════════════════════════
val CategoryEvent = HmifBlue
val CategoryAcademic = AccentTeal
val CategoryCompetition = HmifOrange
val CategoryCareer = HmifPurple
val CategoryInfo = Color(0xFF78909C)  // Blue Grey

// ════════════════════════════════════════════════════════════════
// STATUS BADGE COLORS
// ════════════════════════════════════════════════════════════════
val StatusActive = Success
val StatusInactive = Color(0xFF9E9E9E)
val StatusPending = Warning
val StatusRegistered = HmifBlue
val StatusAttended = Success
val StatusCancelled = Error