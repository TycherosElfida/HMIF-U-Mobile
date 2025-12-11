package com.example.hmifu_mobile.ui.theme

import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * HMIF U-Mobile Design System Spacing
 * Based on 8dp grid system for consistent layouts
 */
data class HmifSpacing(
    /** 4dp - Minimal spacing for tight elements */
    val xs: Dp = 4.dp,

    /** 8dp - Small spacing, default for related items */
    val sm: Dp = 8.dp,

    /** 12dp - Between small and medium */
    val md: Dp = 12.dp,

    /** 16dp - Standard padding for most components */
    val lg: Dp = 16.dp,

    /** 20dp - Comfortable spacing */
    val xl: Dp = 20.dp,

    /** 24dp - Section spacing */
    val xxl: Dp = 24.dp,

    /** 32dp - Large gaps between sections */
    val xxxl: Dp = 32.dp,

    /** 48dp - Screen edge padding, hero spacing */
    val huge: Dp = 48.dp,

    /** 64dp - Extra large spacing for emphasis */
    val massive: Dp = 64.dp
)

/**
 * HMIF U-Mobile Design System Corner Radius
 * Consistent rounded corners across the app
 */
data class HmifCornerRadius(
    /** 4dp - Subtle rounding */
    val xs: Dp = 4.dp,

    /** 8dp - Small components (chips, small buttons) */
    val sm: Dp = 8.dp,

    /** 12dp - Medium components (input fields) */
    val md: Dp = 12.dp,

    /** 16dp - Cards, containers */
    val lg: Dp = 16.dp,

    /** 20dp - Large cards */
    val xl: Dp = 20.dp,

    /** 24dp - Hero cards, modals, bottom sheets */
    val xxl: Dp = 24.dp,

    /** 28dp - Extra large elements */
    val xxxl: Dp = 28.dp,

    /** 50% - Circular/pill shapes */
    val full: Dp = 1000.dp
)

/**
 * HMIF U-Mobile Design System Elevation
 * Material 3 elevation levels for depth hierarchy
 */
data class HmifElevation(
    /** No elevation - flat surfaces */
    val none: Dp = 0.dp,

    /** 1dp - Subtle lift (cards at rest) */
    val xs: Dp = 1.dp,

    /** 2dp - Default card elevation */
    val sm: Dp = 2.dp,

    /** 4dp - Hovered/focused cards */
    val md: Dp = 4.dp,

    /** 6dp - Floating elements */
    val lg: Dp = 6.dp,

    /** 8dp - Modals, dialogs */
    val xl: Dp = 8.dp,

    /** 12dp - FAB at rest */
    val xxl: Dp = 12.dp,

    /** 16dp - FAB pressed */
    val xxxl: Dp = 16.dp
)

/**
 * HMIF U-Mobile Design System Sizes
 * Common component sizes for consistency
 */
data class HmifSizes(
    /** Minimum touch target as per Material Design guidelines */
    val minTouchTarget: Dp = 48.dp,

    /** Standard button height */
    val buttonHeight: Dp = 56.dp,

    /** Small button height */
    val buttonHeightSmall: Dp = 40.dp,

    /** Icon button size */
    val iconButton: Dp = 48.dp,

    /** Small icon size */
    val iconSmall: Dp = 16.dp,

    /** Medium icon size */
    val iconMedium: Dp = 24.dp,

    /** Large icon size */
    val iconLarge: Dp = 32.dp,

    /** Avatar small */
    val avatarSmall: Dp = 40.dp,

    /** Avatar medium */
    val avatarMedium: Dp = 56.dp,

    /** Avatar large */
    val avatarLarge: Dp = 80.dp,

    /** Avatar extra large (profile) */
    val avatarXLarge: Dp = 120.dp,

    /** Bottom navigation height */
    val bottomNavHeight: Dp = 80.dp,

    /** Top app bar height */
    val topAppBarHeight: Dp = 64.dp,

    /** Digital ID card height */
    val memberCardHeight: Dp = 220.dp,

    /** QR code size */
    val qrCodeSize: Dp = 140.dp,

    /** Event poster thumbnail */
    val eventThumbnail: Dp = 80.dp,

    /** Event hero image height */
    val eventHeroHeight: Dp = 200.dp
)

// ════════════════════════════════════════════════════════════════
// COMPOSITION LOCALS
// ════════════════════════════════════════════════════════════════

val LocalHmifSpacing = staticCompositionLocalOf { HmifSpacing() }
val LocalHmifCornerRadius = staticCompositionLocalOf { HmifCornerRadius() }
val LocalHmifElevation = staticCompositionLocalOf { HmifElevation() }
val LocalHmifSizes = staticCompositionLocalOf { HmifSizes() }

/**
 * Access spacing tokens from MaterialTheme
 */
object HmifTheme {
    val spacing: HmifSpacing
        @Composable
        @ReadOnlyComposable
        get() = LocalHmifSpacing.current

    val cornerRadius: HmifCornerRadius
        @Composable
        @ReadOnlyComposable
        get() = LocalHmifCornerRadius.current

    val elevation: HmifElevation
        @Composable
        @ReadOnlyComposable
        get() = LocalHmifElevation.current

    val sizes: HmifSizes
        @Composable
        @ReadOnlyComposable
        get() = LocalHmifSizes.current
}
