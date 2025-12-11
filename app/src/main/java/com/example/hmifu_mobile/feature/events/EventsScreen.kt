package com.example.hmifu_mobile.feature.events

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.rounded.Event
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.example.hmifu_mobile.data.local.entity.EventCategory
import com.example.hmifu_mobile.data.local.entity.EventEntity
import com.example.hmifu_mobile.data.local.entity.EventStatus
import com.example.hmifu_mobile.ui.components.FilterChip
import com.example.hmifu_mobile.ui.components.GlassmorphicCard
import com.example.hmifu_mobile.ui.components.ImageKitImageWithAspectRatio
import com.example.hmifu_mobile.ui.components.SkeletonCard
import com.example.hmifu_mobile.ui.components.StaggeredAnimatedItem
import com.example.hmifu_mobile.ui.theme.CategoryCompetition
import com.example.hmifu_mobile.ui.theme.CategoryEvent
import com.example.hmifu_mobile.ui.theme.Error
import com.example.hmifu_mobile.ui.theme.GradientEnd
import com.example.hmifu_mobile.ui.theme.HmifBlue
import com.example.hmifu_mobile.ui.theme.HmifOrange
import com.example.hmifu_mobile.ui.theme.HmifPurple
import com.example.hmifu_mobile.ui.theme.HmifTheme
import com.example.hmifu_mobile.ui.theme.Success
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Events Screen - Premium 2025 Design
 *
 * Features:
 * - Gradient header section
 * - Animated filter chips
 * - Glassmorphic event cards
 * - Status badges with color coding
 * - Smooth staggered animations
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EventsScreen(
    onEventClick: (String) -> Unit = {},
    viewModel: EventsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(uiState.errorMessage) {
        uiState.errorMessage?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearError()
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(HmifTheme.spacing.sm)
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.Event,
                            contentDescription = null,
                            tint = HmifBlue
                        )
                        Text(
                            text = "Events",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent
                )
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // Upcoming only toggle with enhanced styling
            UpcomingToggleRow(
                isChecked = uiState.showUpcomingOnly,
                onCheckedChange = viewModel::toggleUpcomingOnly
            )

            // Category filter chips
            CategoryFilterRow(
                selectedCategory = uiState.selectedCategory,
                onCategorySelected = viewModel::selectCategory
            )

            // Content
            when {
                uiState.isLoading && uiState.events.isEmpty() -> {
                    LoadingState()
                }

                uiState.events.isEmpty() -> {
                    EmptyState()
                }

                else -> {
                    EventsList(
                        events = uiState.events,
                        onEventClick = onEventClick
                    )
                }
            }
        }
    }
}

// ════════════════════════════════════════════════════════════════
// UPCOMING TOGGLE
// ════════════════════════════════════════════════════════════════

@Composable
private fun UpcomingToggleRow(
    isChecked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    GlassmorphicCard(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = HmifTheme.spacing.lg, vertical = HmifTheme.spacing.sm),
        cornerRadius = HmifTheme.cornerRadius.lg
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "Upcoming Events Only",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = if (isChecked) "Showing future events" else "Showing all events",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Switch(
                checked = isChecked,
                onCheckedChange = onCheckedChange,
                colors = SwitchDefaults.colors(
                    checkedThumbColor = Color.White,
                    checkedTrackColor = HmifBlue,
                    uncheckedThumbColor = MaterialTheme.colorScheme.outline,
                    uncheckedTrackColor = MaterialTheme.colorScheme.surfaceContainerHighest
                )
            )
        }
    }
}

// ════════════════════════════════════════════════════════════════
// CATEGORY FILTER
// ════════════════════════════════════════════════════════════════

@Composable
private fun CategoryFilterRow(
    selectedCategory: EventCategory?,
    onCategorySelected: (EventCategory?) -> Unit
) {
    LazyRow(
        contentPadding = PaddingValues(
            horizontal = HmifTheme.spacing.lg,
            vertical = HmifTheme.spacing.sm
        ),
        horizontalArrangement = Arrangement.spacedBy(HmifTheme.spacing.sm)
    ) {
        item {
            FilterChip(
                text = "All",
                selected = selectedCategory == null,
                onClick = { onCategorySelected(null) }
            )
        }
        items(EventCategory.entries) { category ->
            FilterChip(
                text = "${category.emoji} ${category.displayName}",
                selected = selectedCategory == category,
                onClick = { onCategorySelected(category) },
                color = getCategoryColor(category)
            )
        }
    }
}

private fun getCategoryColor(category: EventCategory): Color {
    return when (category) {
        EventCategory.SEMINAR -> HmifBlue
        EventCategory.WORKSHOP -> HmifOrange
        EventCategory.COMPETITION -> CategoryCompetition
        EventCategory.SOCIAL -> CategoryEvent
        EventCategory.MEETING -> HmifPurple
        EventCategory.OTHER -> GradientEnd
    }
}

// ════════════════════════════════════════════════════════════════
// EVENTS LIST
// ════════════════════════════════════════════════════════════════

@Composable
private fun EventsList(
    events: List<EventEntity>,
    onEventClick: (String) -> Unit
) {
    LazyColumn(
        contentPadding = PaddingValues(HmifTheme.spacing.lg),
        verticalArrangement = Arrangement.spacedBy(HmifTheme.spacing.md)
    ) {
        itemsIndexed(events, key = { _, it -> it.id }) { index, event ->
            StaggeredAnimatedItem(index = index) {
                EventCard(
                    event = event,
                    onClick = { onEventClick(event.id) }
                )
            }
        }

        // Bottom spacing
        item {
            Spacer(modifier = Modifier.height(HmifTheme.spacing.huge))
        }
    }
}

// ════════════════════════════════════════════════════════════════
// EVENT CARD
// ════════════════════════════════════════════════════════════════

@Composable
private fun EventCard(
    event: EventEntity,
    onClick: () -> Unit
) {
    val status = EventStatus.fromEvent(event)
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.98f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMedium
        ),
        label = "event_card_scale"
    )

    Box(
        modifier = Modifier
            .scale(scale)
            .fillMaxWidth()
            .clip(RoundedCornerShape(HmifTheme.cornerRadius.lg))
            .background(MaterialTheme.colorScheme.surfaceContainerLow)
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick
            )
    ) {
        Column {
            // Event Image (if available)
            if (!event.imageUrl.isNullOrBlank()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(140.dp)
                ) {
                    ImageKitImageWithAspectRatio(
                        path = event.imageUrl,
                        contentDescription = "Event image for ${event.title}",
                        modifier = Modifier.fillMaxSize(),
                        height = 140f,
                        aspectRatioWidth = 16,
                        aspectRatioHeight = 9
                    )

                    // Gradient overlay for text readability
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                Brush.verticalGradient(
                                    colors = listOf(
                                        Color.Transparent,
                                        Color.Black.copy(alpha = 0.6f)
                                    ),
                                    startY = 50f
                                )
                            )
                    )

                    // Status badge on image
                    StatusBadge(
                        status = status,
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(HmifTheme.spacing.sm)
                    )
                }
            }

            // Content
            Column(
                modifier = Modifier.padding(HmifTheme.spacing.lg),
                verticalArrangement = Arrangement.spacedBy(HmifTheme.spacing.sm)
            ) {
                // Category + Status (if no image)
                if (event.imageUrl.isNullOrBlank()) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        CategoryBadge(category = EventCategory.fromString(event.category))
                        StatusBadge(status = status)
                    }
                } else {
                    CategoryBadge(category = EventCategory.fromString(event.category))
                }

                // Title
                Text(
                    text = event.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                // Event Info
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    EventInfoRow(
                        icon = Icons.Default.CalendarMonth,
                        text = formatEventDate(event.startTime)
                    )

                    EventInfoRow(
                        icon = Icons.Default.Schedule,
                        text = formatEventTime(event.startTime, event.endTime)
                    )

                    EventInfoRow(
                        icon = Icons.Default.LocationOn,
                        text = if (event.isOnline) "🌐 Online" else event.location
                    )

                    // Participants (if limited)
                    event.maxParticipants?.let { max ->
                        val isFull = event.currentParticipants >= max
                        EventInfoRow(
                            icon = Icons.Default.People,
                            text = "${event.currentParticipants}/$max participants",
                            textColor = if (isFull) Error else MaterialTheme.colorScheme.onSurfaceVariant,
                            iconColor = if (isFull) Error else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun EventInfoRow(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    text: String,
    textColor: Color = MaterialTheme.colorScheme.onSurfaceVariant,
    iconColor: Color = MaterialTheme.colorScheme.onSurfaceVariant
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(HmifTheme.spacing.xs)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier.size(16.dp),
            tint = iconColor
        )
        Text(
            text = text,
            style = MaterialTheme.typography.bodySmall,
            color = textColor
        )
    }
}

// ════════════════════════════════════════════════════════════════
// BADGES
// ════════════════════════════════════════════════════════════════

@Composable
private fun StatusBadge(
    status: EventStatus,
    modifier: Modifier = Modifier
) {
    val (backgroundColor, textColor, text) = when (status) {
        EventStatus.UPCOMING -> Triple(HmifBlue, Color.White, "Upcoming")
        EventStatus.ONGOING -> Triple(Success, Color.White, "Live Now")
        EventStatus.ENDED -> Triple(
            MaterialTheme.colorScheme.surfaceContainerHighest,
            MaterialTheme.colorScheme.onSurfaceVariant,
            "Ended"
        )
    }

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(HmifTheme.cornerRadius.sm))
            .background(backgroundColor)
            .padding(horizontal = HmifTheme.spacing.sm, vertical = HmifTheme.spacing.xs)
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelSmall,
            color = textColor,
            fontWeight = FontWeight.SemiBold
        )
    }
}

@Composable
private fun CategoryBadge(category: EventCategory) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(HmifTheme.cornerRadius.sm))
            .background(getCategoryColor(category).copy(alpha = 0.15f))
            .padding(horizontal = HmifTheme.spacing.sm, vertical = HmifTheme.spacing.xs)
    ) {
        Text(
            text = "${category.emoji} ${category.displayName}",
            style = MaterialTheme.typography.labelSmall,
            color = getCategoryColor(category),
            fontWeight = FontWeight.Medium
        )
    }
}

// ════════════════════════════════════════════════════════════════
// STATES
// ════════════════════════════════════════════════════════════════

@Composable
private fun LoadingState() {
    LazyColumn(
        contentPadding = PaddingValues(HmifTheme.spacing.lg),
        verticalArrangement = Arrangement.spacedBy(HmifTheme.spacing.md)
    ) {
        items(4) { index ->
            StaggeredAnimatedItem(index = index) {
                SkeletonCard(
                    modifier = Modifier.fillMaxWidth(),
                    height = 200.dp
                )
            }
        }
    }
}

@Composable
private fun EmptyState() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(HmifTheme.spacing.xl),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(HmifTheme.spacing.md)
        ) {
            Text(
                text = "📅",
                style = MaterialTheme.typography.displayMedium
            )
            Text(
                text = "No events yet",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = "Check back later for upcoming events!",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.outline
            )
        }
    }
}

// ════════════════════════════════════════════════════════════════
// UTILITIES
// ════════════════════════════════════════════════════════════════

private fun formatEventDate(timestamp: Long): String {
    return try {
        val sdf = SimpleDateFormat("EEE, dd MMM yyyy", Locale.getDefault())
        sdf.format(Date(timestamp))
    } catch (e: Exception) {
        ""
    }
}

private fun formatEventTime(startTime: Long, endTime: Long): String {
    return try {
        val sdf = SimpleDateFormat("HH:mm", Locale.getDefault())
        "${sdf.format(Date(startTime))} - ${sdf.format(Date(endTime))}"
    } catch (e: Exception) {
        ""
    }
}
