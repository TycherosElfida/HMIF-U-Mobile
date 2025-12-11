package com.example.hmifu_mobile.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.hmifu_mobile.ui.theme.HmifTheme
import kotlinx.coroutines.delay

// ════════════════════════════════════════════════════════════════
// SPRING ANIMATION SPECS (Material 3 Expressive)
// ════════════════════════════════════════════════════════════════

/**
 * Spring animations for Material 3 Expressive feel.
 */
object SpringSpecs {
    /** Standard spring for most UI interactions */
    val Default = spring<Float>(
        dampingRatio = Spring.DampingRatioMediumBouncy,
        stiffness = Spring.StiffnessLow
    )

    /** Quick spring for small UI feedback */
    val Quick = spring<Float>(
        dampingRatio = Spring.DampingRatioNoBouncy,
        stiffness = Spring.StiffnessMedium
    )

    /** Bouncy spring for playful elements */
    val Bouncy = spring<Float>(
        dampingRatio = Spring.DampingRatioLowBouncy,
        stiffness = Spring.StiffnessLow
    )

    /** Very bouncy for emphasis */
    val VeryBouncy = spring<Float>(
        dampingRatio = Spring.DampingRatioHighBouncy,
        stiffness = Spring.StiffnessVeryLow
    )
}

// ════════════════════════════════════════════════════════════════
// SLIDE & FADE ANIMATIONS
// ════════════════════════════════════════════════════════════════

/**
 * Animated content that fades and slides in from the bottom with spring physics.
 */
@Composable
fun AnimatedSlideIn(
    modifier: Modifier = Modifier,
    delay: Int = 0,
    content: @Composable () -> Unit
) {
    val visibleState = remember { MutableTransitionState(false).apply { targetState = true } }

    AnimatedVisibility(
        visibleState = visibleState,
        enter = fadeIn(
            animationSpec = tween(durationMillis = 300, delayMillis = delay)
        ) + slideInVertically(
            animationSpec = spring(
                dampingRatio = Spring.DampingRatioMediumBouncy,
                stiffness = Spring.StiffnessLow
            ),
            initialOffsetY = { it / 4 }
        ),
        exit = fadeOut(animationSpec = tween(200)),
        modifier = modifier
    ) {
        content()
    }
}

/**
 * Staggered animation for list items.
 */
@Composable
fun StaggeredAnimatedItem(
    index: Int,
    baseDelay: Int = 50,
    content: @Composable () -> Unit
) {
    AnimatedSlideIn(
        delay = index * baseDelay,
        content = content
    )
}

/**
 * Animated scale-in with spring physics.
 */
@Composable
fun AnimatedScaleIn(
    modifier: Modifier = Modifier,
    delay: Int = 0,
    content: @Composable () -> Unit
) {
    val visibleState = remember { MutableTransitionState(false).apply { targetState = true } }

    AnimatedVisibility(
        visibleState = visibleState,
        enter = fadeIn(
            animationSpec = tween(durationMillis = 200, delayMillis = delay)
        ) + scaleIn(
            animationSpec = spring(
                dampingRatio = Spring.DampingRatioMediumBouncy,
                stiffness = Spring.StiffnessMedium
            ),
            initialScale = 0.8f
        ),
        exit = fadeOut(),
        modifier = modifier
    ) {
        content()
    }
}

// ════════════════════════════════════════════════════════════════
// PULSE & SHIMMER ANIMATIONS
// ════════════════════════════════════════════════════════════════

/**
 * Pulsing animation for QR codes and attention-grabbing elements.
 */
@Composable
fun PulsingElement(
    modifier: Modifier = Modifier,
    pulseDuration: Int = 2000,
    minScale: Float = 0.97f,
    maxScale: Float = 1.03f,
    content: @Composable () -> Unit
) {
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")

    val scale by infiniteTransition.animateFloat(
        initialValue = minScale,
        targetValue = maxScale,
        animationSpec = infiniteRepeatable(
            animation = tween(pulseDuration / 2, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse_scale"
    )

    Box(modifier = modifier.scale(scale)) {
        content()
    }
}

/**
 * Shimmer loading effect for skeleton screens.
 */
@Composable
fun ShimmerBox(
    modifier: Modifier = Modifier,
    width: Dp = 100.dp,
    height: Dp = 20.dp,
    cornerRadius: Dp = HmifTheme.cornerRadius.sm
) {
    val infiniteTransition = rememberInfiniteTransition(label = "shimmer")

    val translateAnim by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1000f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1200, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "shimmer_translate"
    )

    val shimmerColors = listOf(
        MaterialTheme.colorScheme.surfaceContainerHigh,
        MaterialTheme.colorScheme.surfaceContainerHighest,
        MaterialTheme.colorScheme.surfaceContainerHigh
    )

    val brush = Brush.linearGradient(
        colors = shimmerColors,
        start = Offset(translateAnim - 500f, 0f),
        end = Offset(translateAnim, 0f)
    )

    Box(
        modifier = modifier
            .size(width, height)
            .clip(RoundedCornerShape(cornerRadius))
            .background(brush)
    )
}

/**
 * Skeleton card for loading states.
 */
@Composable
fun SkeletonCard(
    modifier: Modifier = Modifier,
    height: Dp = 100.dp
) {
    Box(
        modifier = modifier
            .size(width = Dp.Unspecified, height = height)
            .clip(RoundedCornerShape(HmifTheme.cornerRadius.lg))
            .background(MaterialTheme.colorScheme.surfaceContainerHigh)
    ) {
        val infiniteTransition = rememberInfiniteTransition(label = "skeleton_shimmer")

        val translateAnim by infiniteTransition.animateFloat(
            initialValue = 0f,
            targetValue = 1000f,
            animationSpec = infiniteRepeatable(
                animation = tween(durationMillis = 1500, easing = LinearEasing),
                repeatMode = RepeatMode.Restart
            ),
            label = "skeleton_translate"
        )

        val shimmerColors = listOf(
            Color.White.copy(alpha = 0f),
            Color.White.copy(alpha = 0.1f),
            Color.White.copy(alpha = 0f)
        )

        Box(
            modifier = Modifier
                .matchParentSize()
                .background(
                    Brush.linearGradient(
                        colors = shimmerColors,
                        start = Offset(translateAnim - 300f, 0f),
                        end = Offset(translateAnim, 0f)
                    )
                )
        )
    }
}

// ════════════════════════════════════════════════════════════════
// BOUNCE & SCALE ANIMATIONS
// ════════════════════════════════════════════════════════════════

/**
 * Bounce-in animation for elements that need emphasis.
 */
@Composable
fun BounceIn(
    modifier: Modifier = Modifier,
    delay: Long = 0,
    content: @Composable () -> Unit
) {
    var visible by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        delay(delay)
        visible = true
    }

    val scale by animateFloatAsState(
        targetValue = if (visible) 1f else 0f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioLowBouncy,
            stiffness = Spring.StiffnessMedium
        ),
        label = "bounce_in_scale"
    )

    val alpha by animateFloatAsState(
        targetValue = if (visible) 1f else 0f,
        animationSpec = tween(200),
        label = "bounce_in_alpha"
    )

    Box(
        modifier = modifier
            .scale(scale)
            .alpha(alpha)
    ) {
        content()
    }
}

/**
 * Counting animation for numbers (e.g., statistics).
 */
@Composable
fun AnimatedCounter(
    targetValue: Int,
    durationMillis: Int = 1000,
    content: @Composable (Int) -> Unit
) {
    var currentValue by remember { mutableIntStateOf(0) }

    LaunchedEffect(targetValue) {
        val startValue = currentValue
        val steps = 60 // 60fps
        val stepDuration = durationMillis / steps
        val increment = (targetValue - startValue).toFloat() / steps

        repeat(steps) { step ->
            currentValue = (startValue + increment * (step + 1)).toInt()
            delay(stepDuration.toLong())
        }
        currentValue = targetValue
    }

    content(currentValue)
}

// ════════════════════════════════════════════════════════════════
// UTILITY MODIFIERS
// ════════════════════════════════════════════════════════════════

/**
 * Modifier for fade-in animation on first composition.
 */
@Composable
fun Modifier.fadeInOnMount(
    delay: Long = 0,
    durationMillis: Int = 300
): Modifier {
    var visible by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        delay(delay)
        visible = true
    }

    val alpha by animateFloatAsState(
        targetValue = if (visible) 1f else 0f,
        animationSpec = tween(durationMillis),
        label = "fade_in_mount"
    )

    return this.alpha(alpha)
}
