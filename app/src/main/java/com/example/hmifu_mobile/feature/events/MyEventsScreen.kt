package com.example.hmifu_mobile.feature.events

import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Event
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.rounded.EventAvailable
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Tab
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.example.hmifu_mobile.data.local.entity.EventCategory
import com.example.hmifu_mobile.data.local.entity.EventEntity
import com.example.hmifu_mobile.ui.components.GlassmorphicCard
import com.example.hmifu_mobile.ui.components.SkeletonCard
import com.example.hmifu_mobile.ui.components.StaggeredAnimatedItem
import com.example.hmifu_mobile.ui.theme.GradientEnd
import com.example.hmifu_mobile.ui.theme.HmifBlue
import com.example.hmifu_mobile.ui.theme.HmifOrange
import com.example.hmifu_mobile.ui.theme.HmifPurple
import com.example.hmifu_mobile.ui.theme.HmifTheme
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * My Events Screen - Premium 2025 Design
 *
 * Features:
 * - Tab navigation (Upcoming/Past)
 * - Glassmorphic event cards
 * - Category badges with colors
 * - Staggered animations
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyEventsScreen(
    onNavigateBack: () -> Unit = {},
    onEventClick: (String) -> Unit = {},
    viewModel: MyEventsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    val tabs = remember {
        listOf(
            "Upcoming" to Icons.Default.Event,
            "Past" to Icons.Default.History
        )
    }

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
                            imageVector = Icons.Rounded.EventAvailable,
                            contentDescription = null,
                            tint = HmifBlue
                        )
                        Text(
                            text = "My Events",
                            fontWeight = FontWeight.Bold
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
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
            PrimaryTabRow(selectedTabIndex = uiState.selectedTab) {
                tabs.forEachIndexed { index, (title, icon) ->
                    Tab(
                        selected = uiState.selectedTab == index,
                        onClick = { viewModel.selectTab(index) },
                        text = {
                            Text(
                                text = title,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        },
                        icon = { Icon(imageVector = icon, contentDescription = null) }
                    )
                }
            }

            when {
                uiState.isLoading -> LoadingState()
                uiState.selectedTab == 0 -> {
                    if (uiState.upcomingEvents.isEmpty()) {
                        EmptyState(
                            emoji = "📅",
                            message = "No upcoming events",
                            submessage = "Register for events to see them here"
                        )
                    } else {
                        EventsList(events = uiState.upcomingEvents, onEventClick = onEventClick)
                    }
                }

                uiState.selectedTab == 1 -> {
                    if (uiState.pastEvents.isEmpty()) {
                        EmptyState(
                            emoji = "📚",
                            message = "No past events",
                            submessage = "Events you've attended will appear here"
                        )
                    } else {
                        EventsList(events = uiState.pastEvents, onEventClick = onEventClick)
                    }
                }
            }
        }
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
                MyEventCard(event = event, onClick = { onEventClick(event.id) })
            }
        }

        item {
            Spacer(modifier = Modifier.height(HmifTheme.spacing.huge))
        }
    }
}

// ════════════════════════════════════════════════════════════════
// EVENT CARD
// ════════════════════════════════════════════════════════════════

@Composable
private fun MyEventCard(
    event: EventEntity,
    onClick: () -> Unit
) {
    val category = EventCategory.fromString(event.category)
    val categoryColor = getCategoryColor(category)

    GlassmorphicCard(
        modifier = Modifier.fillMaxWidth(),
        onClick = onClick,
        cornerRadius = HmifTheme.cornerRadius.lg
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(HmifTheme.spacing.sm)) {
            // Category badge
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(HmifTheme.cornerRadius.sm))
                    .background(categoryColor.copy(alpha = 0.15f))
                    .padding(horizontal = HmifTheme.spacing.sm, vertical = HmifTheme.spacing.xs)
            ) {
                Text(
                    text = "${category.emoji} ${category.displayName}",
                    style = MaterialTheme.typography.labelSmall,
                    color = categoryColor,
                    fontWeight = FontWeight.Medium
                )
            }

            // Title
            Text(
                text = event.title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            // Date & Time row
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(HmifTheme.spacing.md)
            ) {
                InfoChip(
                    icon = Icons.Default.CalendarMonth,
                    text = formatEventDate(event.startTime),
                    color = HmifBlue
                )
                InfoChip(
                    icon = Icons.Default.Schedule,
                    text = formatEventTime(event.startTime, event.endTime),
                    color = HmifOrange
                )
            }

            // Location
            InfoChip(
                icon = Icons.Default.LocationOn,
                text = if (event.isOnline) "🌐 Online" else event.location,
                color = GradientEnd
            )
        }
    }
}

@Composable
private fun InfoChip(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    text: String,
    color: Color
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(HmifTheme.spacing.xs)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier.size(16.dp),
            tint = color
        )
        Text(
            text = text,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

private fun getCategoryColor(category: EventCategory): Color {
    return when (category) {
        EventCategory.SEMINAR -> HmifBlue
        EventCategory.WORKSHOP -> HmifOrange
        EventCategory.COMPETITION -> HmifOrange
        EventCategory.SOCIAL -> HmifBlue
        EventCategory.MEETING -> HmifPurple
        EventCategory.OTHER -> GradientEnd
    }
}

// ════════════════════════════════════════════════════════════════
// STATES
// ════════════════════════════════════════════════════════════════

@Composable
private fun EmptyState(emoji: String, message: String, submessage: String) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(HmifTheme.spacing.md)
        ) {
            Text(text = emoji, style = MaterialTheme.typography.displayLarge)
            Text(
                text = message,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = submessage,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.outline
            )
        }
    }
}

@Composable
private fun LoadingState() {
    Column(
        modifier = Modifier.padding(HmifTheme.spacing.lg),
        verticalArrangement = Arrangement.spacedBy(HmifTheme.spacing.md)
    ) {
        repeat(3) {
            SkeletonCard(modifier = Modifier.fillMaxWidth(), height = 140.dp)
        }
    }
}

// ════════════════════════════════════════════════════════════════
// UTILITIES
// ════════════════════════════════════════════════════════════════

private fun formatEventDate(timestamp: Long): String {
    return try {
        val sdf = SimpleDateFormat("EEE, dd MMM", Locale.getDefault())
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
