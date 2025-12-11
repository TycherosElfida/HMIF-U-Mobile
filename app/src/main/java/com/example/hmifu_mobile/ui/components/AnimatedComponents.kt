package com.example.hmifu_mobile.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.slideInVertically
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier

/**
 * Animated content that fades and slides in from the bottom.
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
            animationSpec = tween(durationMillis = 300, delayMillis = delay),
            initialOffsetY = { it / 4 }
        ),
        exit = fadeOut(animationSpec = tween(200)),
        modifier = modifier
    ) {
        content()
    }
}

/**
 * Animated content that appears with a scale effect.
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
                stiffness = Spring.StiffnessLow
            ),
            initialScale = 0.8f
        ),
        exit = fadeOut(animationSpec = tween(150)),
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
