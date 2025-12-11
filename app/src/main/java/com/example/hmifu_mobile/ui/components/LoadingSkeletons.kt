package com.example.hmifu_mobile.ui.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * Shimmer effect modifier for loading states.
 */
@Composable
fun shimmerBrush(showShimmer: Boolean = true): Brush {
    return if (showShimmer) {
        val shimmerColors = listOf(
            MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
            MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.2f),
            MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)
        )

        val transition = rememberInfiniteTransition(label = "shimmer")
        val translateAnim by transition.animateFloat(
            initialValue = 0f,
            targetValue = 1000f,
            animationSpec = infiniteRepeatable(
                animation = tween(durationMillis = 1200, easing = LinearEasing),
                repeatMode = RepeatMode.Restart
            ),
            label = "shimmer_translate"
        )

        Brush.linearGradient(
            colors = shimmerColors,
            start = Offset.Zero,
            end = Offset(x = translateAnim, y = translateAnim)
        )
    } else {
        Brush.linearGradient(
            colors = listOf(Color.Transparent, Color.Transparent),
            start = Offset.Zero,
            end = Offset.Zero
        )
    }
}

/**
 * Shimmer placeholder box.
 */
@Composable
fun ShimmerBox(
    modifier: Modifier = Modifier,
    height: Dp = 16.dp,
    shape: RoundedCornerShape = RoundedCornerShape(4.dp)
) {
    Box(
        modifier = modifier
            .height(height)
            .clip(shape)
            .background(shimmerBrush())
    )
}

/**
 * Skeleton loading card for announcements.
 */
@Composable
fun AnnouncementCardSkeleton(
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            // Header row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                ShimmerBox(modifier = Modifier.width(80.dp), height = 12.dp)
                ShimmerBox(modifier = Modifier.width(60.dp), height = 12.dp)
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Title
            ShimmerBox(modifier = Modifier.fillMaxWidth(0.9f), height = 20.dp)

            Spacer(modifier = Modifier.height(8.dp))

            // Body lines
            ShimmerBox(modifier = Modifier.fillMaxWidth(), height = 14.dp)
            Spacer(modifier = Modifier.height(4.dp))
            ShimmerBox(modifier = Modifier.fillMaxWidth(0.7f), height = 14.dp)

            Spacer(modifier = Modifier.height(12.dp))

            // Author
            ShimmerBox(modifier = Modifier.width(100.dp), height = 10.dp)
        }
    }
}

/**
 * Skeleton loading card for events.
 */
@Composable
fun EventCardSkeleton(
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            // Header row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                ShimmerBox(modifier = Modifier.width(100.dp), height = 12.dp)
                ShimmerBox(modifier = Modifier.width(70.dp), height = 12.dp)
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Title
            ShimmerBox(modifier = Modifier.fillMaxWidth(0.85f), height = 20.dp)

            Spacer(modifier = Modifier.height(12.dp))

            // Date/Time row
            Row(verticalAlignment = Alignment.CenterVertically) {
                ShimmerBox(
                    modifier = Modifier
                        .width(16.dp)
                        .height(16.dp),
                    shape = RoundedCornerShape(4.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                ShimmerBox(modifier = Modifier.width(120.dp), height = 12.dp)
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Location row
            Row(verticalAlignment = Alignment.CenterVertically) {
                ShimmerBox(
                    modifier = Modifier
                        .width(16.dp)
                        .height(16.dp),
                    shape = RoundedCornerShape(4.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                ShimmerBox(modifier = Modifier.width(100.dp), height = 12.dp)
            }
        }
    }
}

/**
 * Skeleton loading for profile.
 */
@Composable
fun ProfileSkeleton(
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Avatar
        Box(
            modifier = Modifier
                .size(100.dp)
                .clip(CircleShape)
                .background(shimmerBrush())
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Name
        ShimmerBox(modifier = Modifier.width(150.dp), height = 24.dp)

        Spacer(modifier = Modifier.height(8.dp))

        // Email
        ShimmerBox(modifier = Modifier.width(180.dp), height = 14.dp)

        Spacer(modifier = Modifier.height(24.dp))

        // Info card placeholder
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
            )
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                repeat(4) { index ->
                    if (index > 0) Spacer(modifier = Modifier.height(12.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        ShimmerBox(modifier = Modifier.width(80.dp), height = 14.dp)
                        ShimmerBox(modifier = Modifier.width(100.dp), height = 14.dp)
                    }
                }
            }
        }
    }
}

/**
 * Loading skeleton list with shimmer effect.
 */
@Composable
fun LoadingSkeletonList(
    itemCount: Int = 3,
    itemContent: @Composable () -> Unit
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(12.dp),
        modifier = Modifier.padding(16.dp)
    ) {
        repeat(itemCount) {
            itemContent()
        }
    }
}
