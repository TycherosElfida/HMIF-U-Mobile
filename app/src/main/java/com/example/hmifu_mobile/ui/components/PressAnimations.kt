package com.example.hmifu_mobile.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.input.pointer.pointerInput

/**
 * Scale-on-press modifier for interactive feedback.
 */
@Composable
fun Modifier.pressAnimation(
    onClick: () -> Unit,
    pressScale: Float = 0.96f
): Modifier {
    var isPressed by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(
        targetValue = if (isPressed) pressScale else 1f,
        animationSpec = tween(durationMillis = 100),
        label = "press_scale"
    )

    return this
        .scale(scale)
        .pointerInput(Unit) {
            detectTapGestures(
                onPress = {
                    isPressed = true
                    tryAwaitRelease()
                    isPressed = false
                    onClick()
                }
            )
        }
}

/**
 * Bouncy press animation modifier.
 */
@Composable
fun Modifier.bouncyPress(
    onClick: () -> Unit
): Modifier {
    return pressAnimation(onClick = onClick, pressScale = 0.95f)
}
