package com.example.hmifu_mobile.ui.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateIntAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.EmojiEvents
import androidx.compose.material.icons.rounded.Event
import androidx.compose.material.icons.rounded.Stars
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.hmifu_mobile.ui.theme.HmifBlue
import com.example.hmifu_mobile.ui.theme.HmifOrange
import com.example.hmifu_mobile.ui.theme.HmifPurple
import com.example.hmifu_mobile.ui.theme.HmifTheme
import com.example.hmifu_mobile.ui.theme.StatNumber

/**
 * Stat Card Component
 *
 * Displays a quick stat with:
 * - Icon with colored background
 * - Animated number value
 * - Label text
 *
 * @param icon The stat icon
 * @param value The numeric value to display
 * @param label The stat label
 * @param color The accent color for the icon background
 * @param modifier Modifier for the card
 */
@Composable
fun StatCard(
    icon: ImageVector,
    value: Int,
    label: String,
    color: Color = HmifBlue,
    modifier: Modifier = Modifier
) {
    var displayValue by remember { mutableIntStateOf(0) }

    // Animate the number counting up
    LaunchedEffect(value) {
        displayValue = value
    }

    val animatedValue by animateIntAsState(
        targetValue = displayValue,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "stat_value"
    )

    Column(
        modifier = modifier
            .clip(RoundedCornerShape(HmifTheme.cornerRadius.lg))
            .background(MaterialTheme.colorScheme.surfaceContainerHigh)
            .padding(HmifTheme.spacing.lg),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Icon with colored background
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(color.copy(alpha = 0.15f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = color,
                modifier = Modifier.size(24.dp)
            )
        }

        Spacer(modifier = Modifier.height(HmifTheme.spacing.sm))

        // Animated value
        AnimatedContent(
            targetState = animatedValue,
            transitionSpec = {
                slideInVertically { -it } togetherWith slideOutVertically { it }
            },
            label = "stat_number_animation"
        ) { currentValue ->
            Text(
                text = currentValue.toString(),
                style = StatNumber,
                color = MaterialTheme.colorScheme.onSurface
            )
        }

        // Label
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

/**
 * Quick Stats Row Component
 *
 * A row of three StatCards for Points, Events, and Badges.
 *
 * @param points User's points count
 * @param events Number of events attended
 * @param badges Number of badges earned
 * @param modifier Modifier for the row
 */
@Composable
fun QuickStatsRow(
    points: Int,
    events: Int,
    badges: Int,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(HmifTheme.spacing.sm)
    ) {
        StatCard(
            icon = Icons.Rounded.Stars,
            value = points,
            label = "Points",
            color = HmifOrange,
            modifier = Modifier.weight(1f)
        )

        StatCard(
            icon = Icons.Rounded.Event,
            value = events,
            label = "Events",
            color = HmifBlue,
            modifier = Modifier.weight(1f)
        )

        StatCard(
            icon = Icons.Rounded.EmojiEvents,
            value = badges,
            label = "Badges",
            color = HmifPurple,
            modifier = Modifier.weight(1f)
        )
    }
}

/**
 * Inline Stat Component
 *
 * A compact stat display for use within cards or lists.
 */
@Composable
fun InlineStat(
    value: String,
    label: String,
    color: Color = HmifBlue,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(HmifTheme.spacing.xs)
    ) {
        Box(
            modifier = Modifier
                .size(8.dp)
                .clip(CircleShape)
                .background(color)
        )

        Text(
            text = value,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )

        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}
