package com.example.hmifu_mobile.ui.components

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.hmifu_mobile.ui.theme.GlassDark
import com.example.hmifu_mobile.ui.theme.GlassDarkBorder
import com.example.hmifu_mobile.ui.theme.GlassLight
import com.example.hmifu_mobile.ui.theme.GlassLightBorder
import com.example.hmifu_mobile.ui.theme.GradientEnd
import com.example.hmifu_mobile.ui.theme.GradientStart
import com.example.hmifu_mobile.ui.theme.HmifTheme

/**
 * Glassmorphic Card Component
 *
 * A frosted-glass effect card with:
 * - Semi-transparent background with blur effect
 * - Subtle border stroke for depth
 * - Optional gradient overlay
 * - Press animation with spring physics
 *
 * @param modifier Modifier for the card
 * @param onClick Optional click handler. If null, card is not clickable.
 * @param cornerRadius Corner radius of the card
 * @param useGradient Whether to apply the HMIF gradient overlay
 * @param content The content to display inside the card
 */
@Composable
fun GlassmorphicCard(
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
    cornerRadius: Dp = HmifTheme.cornerRadius.xxl,
    useGradient: Boolean = false,
    content: @Composable ColumnScope.() -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.98f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "glassmorphic_scale"
    )

    val isDarkTheme =
        MaterialTheme.colorScheme.background == com.example.hmifu_mobile.ui.theme.SurfaceDark
    val glassColor = if (isDarkTheme) GlassDark else GlassLight
    val borderColor = if (isDarkTheme) GlassDarkBorder else GlassLightBorder

    val cardModifier = modifier
        .scale(scale)
        .shadow(
            elevation = HmifTheme.elevation.md,
            shape = RoundedCornerShape(cornerRadius),
            ambientColor = Color.Black.copy(alpha = 0.1f),
            spotColor = Color.Black.copy(alpha = 0.1f)
        )
        .clip(RoundedCornerShape(cornerRadius))
        .then(
            if (useGradient) {
                Modifier.background(
                    brush = Brush.linearGradient(
                        colors = listOf(
                            GradientStart.copy(alpha = 0.3f),
                            GradientEnd.copy(alpha = 0.3f)
                        )
                    )
                )
            } else {
                Modifier.background(glassColor)
            }
        )
        .border(
            width = 1.dp,
            color = borderColor,
            shape = RoundedCornerShape(cornerRadius)
        )
        .then(
            if (onClick != null) {
                Modifier.clickable(
                    interactionSource = interactionSource,
                    indication = null, // Custom press animation replaces ripple
                    onClick = onClick
                )
            } else {
                Modifier
            }
        )

    Column(
        modifier = cardModifier.padding(HmifTheme.spacing.lg),
        content = content
    )
}

/**
 * Glassmorphic Card with Box layout
 *
 * Same as GlassmorphicCard but uses Box instead of Column for more flexible layouts.
 */
@Composable
fun GlassmorphicBox(
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
    cornerRadius: Dp = HmifTheme.cornerRadius.xxl,
    useGradient: Boolean = false,
    contentAlignment: Alignment = Alignment.TopStart,
    content: @Composable BoxScope.() -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.98f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "glassmorphic_box_scale"
    )

    val isDarkTheme =
        MaterialTheme.colorScheme.background == com.example.hmifu_mobile.ui.theme.SurfaceDark
    val glassColor = if (isDarkTheme) GlassDark else GlassLight
    val borderColor = if (isDarkTheme) GlassDarkBorder else GlassLightBorder

    val cardModifier = modifier
        .scale(scale)
        .shadow(
            elevation = HmifTheme.elevation.md,
            shape = RoundedCornerShape(cornerRadius),
            ambientColor = Color.Black.copy(alpha = 0.1f),
            spotColor = Color.Black.copy(alpha = 0.1f)
        )
        .clip(RoundedCornerShape(cornerRadius))
        .then(
            if (useGradient) {
                Modifier.background(
                    brush = Brush.linearGradient(
                        colors = listOf(
                            GradientStart.copy(alpha = 0.3f),
                            GradientEnd.copy(alpha = 0.3f)
                        )
                    )
                )
            } else {
                Modifier.background(glassColor)
            }
        )
        .border(
            width = 1.dp,
            color = borderColor,
            shape = RoundedCornerShape(cornerRadius)
        )
        .then(
            if (onClick != null) {
                Modifier.clickable(
                    interactionSource = interactionSource,
                    indication = null,
                    onClick = onClick
                )
            } else {
                Modifier
            }
        )

    Box(
        modifier = cardModifier.padding(HmifTheme.spacing.lg),
        contentAlignment = contentAlignment,
        content = content
    )
}

/**
 * Hero Glassmorphic Card
 *
 * A larger glassmorphic card designed for hero sections like the digital membership card.
 * Includes gradient background and enhanced shadow.
 */
@Composable
fun HeroGlassmorphicCard(
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
    content: @Composable BoxScope.() -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.97f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "hero_card_scale"
    )

    Box(
        modifier = modifier
            .scale(scale)
            .shadow(
                elevation = HmifTheme.elevation.lg,
                shape = RoundedCornerShape(HmifTheme.cornerRadius.xxl),
                ambientColor = GradientStart.copy(alpha = 0.2f),
                spotColor = GradientStart.copy(alpha = 0.2f)
            )
            .clip(RoundedCornerShape(HmifTheme.cornerRadius.xxl))
            .background(
                brush = Brush.linearGradient(
                    colors = listOf(
                        GradientStart,
                        GradientEnd
                    )
                )
            )
            .then(
                if (onClick != null) {
                    Modifier.clickable(
                        interactionSource = interactionSource,
                        indication = null,
                        onClick = onClick
                    )
                } else {
                    Modifier
                }
            )
            .padding(HmifTheme.spacing.xl)
    ) {
        // Inner glass layer for depth
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    color = Color.White.copy(alpha = 0.1f),
                    shape = RoundedCornerShape(HmifTheme.cornerRadius.lg)
                )
        )
        content()
    }
}
