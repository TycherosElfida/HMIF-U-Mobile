package com.example.hmifu_mobile.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.expandHorizontally
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkHorizontally
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.hmifu_mobile.navigation.Screen
import com.example.hmifu_mobile.ui.theme.GradientStart
import com.example.hmifu_mobile.ui.theme.HmifBlue

/**
 * Premium Floating Bottom Navigation
 *
 * Features:
 * - Glassmorphism (Frosted Glass Effect) simulation
 * - Floating "Stadium" shape
 * - Smooth expansion animations for selected items
 * - Micro-interactions (Scale, Haptics)
 */
@Composable
fun FloatingBottomNavigation(
    screens: List<Screen>,
    currentRoute: String,
    onNavigate: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .padding(horizontal = 24.dp, vertical = 24.dp)
            .shadow(
                elevation = 12.dp,
                shape = RoundedCornerShape(percent = 50), // Stadium shape
                spotColor = HmifBlue.copy(alpha = 0.25f),
                ambientColor = HmifBlue.copy(alpha = 0.1f)
            )
            .clip(RoundedCornerShape(percent = 50))
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color.White.copy(alpha = 0.95f),
                        Color.White.copy(alpha = 0.85f)
                    )
                )
            )
            .border(
                width = 1.dp,
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color.White.copy(alpha = 0.8f),
                        Color.White.copy(alpha = 0.2f)
                    )
                ),
                shape = RoundedCornerShape(percent = 50)
            )
    ) {
        Row(
            modifier = Modifier
                .padding(vertical = 8.dp, horizontal = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            screens.forEach { screen ->
                PremiumNavItem(
                    screen = screen,
                    isSelected = currentRoute == screen.route,
                    onClick = { onNavigate(screen.route) }
                )
            }
        }
    }
}

@Composable
private fun PremiumNavItem(
    screen: Screen,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val selectedScale by animateFloatAsState(
        targetValue = if (isSelected) 1.0f else 0.9f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "scale"
    )

    val backgroundColor = if (isSelected) HmifBlue.copy(alpha = 0.15f) else Color.Transparent
    val contentColor = if (isSelected) HmifBlue else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
    
    val interactionSource = remember { MutableInteractionSource() }

    Box(
        modifier = Modifier
            .scale(selectedScale)
            .clip(CircleShape)
            .background(backgroundColor)
            .clickable(
                interactionSource = interactionSource,
                indication = null, // Custom indication can be added if needed
                onClick = onClick
            )
            .padding(vertical = 12.dp, horizontal = 16.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            screen.icon?.let { icon ->
                Icon(
                    imageVector = icon,
                    contentDescription = screen.title,
                    tint = contentColor,
                    modifier = Modifier.size(24.dp)
                )
            }

            AnimatedVisibility(
                visible = isSelected,
                enter = fadeIn() + expandHorizontally(),
                exit = fadeOut() + shrinkHorizontally()
            ) {
                Row {
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = screen.title,
                        style = MaterialTheme.typography.labelLarge,
                        color = contentColor,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}
