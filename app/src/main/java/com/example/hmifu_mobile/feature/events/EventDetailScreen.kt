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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.example.hmifu_mobile.data.local.entity.EventCategory
import com.example.hmifu_mobile.data.local.entity.EventEntity
import com.example.hmifu_mobile.data.local.entity.EventStatus
import com.example.hmifu_mobile.ui.components.EventBannerImage
import com.example.hmifu_mobile.ui.components.GlassmorphicCard
import com.example.hmifu_mobile.ui.components.GradientButton
import com.example.hmifu_mobile.ui.components.SecondaryButton
import com.example.hmifu_mobile.ui.components.SkeletonCard
import com.example.hmifu_mobile.ui.components.StaggeredAnimatedItem
import com.example.hmifu_mobile.ui.theme.Error
import com.example.hmifu_mobile.ui.theme.GradientEnd
import com.example.hmifu_mobile.ui.theme.GradientStart
import com.example.hmifu_mobile.ui.theme.HmifBlue
import com.example.hmifu_mobile.ui.theme.HmifOrange
import com.example.hmifu_mobile.ui.theme.HmifTheme
import com.example.hmifu_mobile.ui.theme.Success
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Event Detail Screen - Premium 2025 Design
 *
 * Features:
 * - Hero image with gradient overlay
 * - Glassmorphic details card
 * - Status badges with colors
 * - Gradient action buttons
 * - Staggered animations
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EventDetailScreen(
    viewModel: EventDetailViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit,
    onAddToCalendar: (EventEntity) -> Unit = {},
    onRegister: (EventEntity) -> Unit = {}
) {
    val event by viewModel.event.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Event Details") },
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
        event?.let { evt ->
            EventDetailContent(
                event = evt,
                onAddToCalendar = { onAddToCalendar(evt) },
                onRegister = { onRegister(evt) },
                modifier = Modifier.padding(padding)
            )
        } ?: run {
            LoadingState(modifier = Modifier.padding(padding))
        }
    }
}

// ════════════════════════════════════════════════════════════════
// EVENT DETAIL CONTENT
// ════════════════════════════════════════════════════════════════

@Composable
private fun EventDetailContent(
    event: EventEntity,
    onAddToCalendar: () -> Unit,
    onRegister: () -> Unit,
    modifier: Modifier = Modifier
) {
    val status = EventStatus.fromEvent(event)
    val category = EventCategory.fromString(event.category)

    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(bottom = HmifTheme.spacing.huge)
    ) {
        // Hero Image
        item {
            if (!event.imageUrl.isNullOrBlank()) {
                HeroImage(imageUrl = event.imageUrl, title = event.title)
            }
        }

        // Header with category and status
        item {
            StaggeredAnimatedItem(index = 0) {
                EventHeader(
                    title = event.title,
                    category = category,
                    status = status
                )
            }
        }

        // Details Card
        item {
            StaggeredAnimatedItem(index = 1) {
                DetailsCard(event = event)
            }
        }

        // Description
        item {
            StaggeredAnimatedItem(index = 2) {
                DescriptionSection(description = event.description)
            }
        }

        // Action Buttons
        item {
            StaggeredAnimatedItem(index = 3) {
                ActionButtons(
                    status = status,
                    event = event,
                    onAddToCalendar = onAddToCalendar,
                    onRegister = onRegister
                )
            }
        }
    }
}

// ════════════════════════════════════════════════════════════════
// HERO IMAGE
// ════════════════════════════════════════════════════════════════

@Composable
private fun HeroImage(imageUrl: String, title: String) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(220.dp)
    ) {
        EventBannerImage(
            path = imageUrl,
            modifier = Modifier.fillMaxSize()
        )

        // Gradient overlay
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color.Transparent,
                            Color.Black.copy(alpha = 0.7f)
                        ),
                        startY = 100f
                    )
                )
        )
    }
}

// ════════════════════════════════════════════════════════════════
// EVENT HEADER
// ════════════════════════════════════════════════════════════════

@Composable
private fun EventHeader(
    title: String,
    category: EventCategory,
    status: EventStatus
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(HmifTheme.spacing.lg)
    ) {
        // Category + Status row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            CategoryBadge(category = category)
            StatusBadge(status = status)
        }

        Spacer(modifier = Modifier.height(HmifTheme.spacing.md))

        // Title
        Text(
            text = title,
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
private fun CategoryBadge(category: EventCategory) {
    val color = when (category) {
        EventCategory.SEMINAR -> HmifBlue
        EventCategory.WORKSHOP -> HmifOrange
        EventCategory.COMPETITION -> HmifOrange
        EventCategory.SOCIAL -> HmifBlue
        EventCategory.MEETING -> GradientEnd
        EventCategory.OTHER -> GradientEnd
    }

    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(HmifTheme.cornerRadius.sm))
            .background(color.copy(alpha = 0.15f))
            .padding(horizontal = HmifTheme.spacing.md, vertical = HmifTheme.spacing.xs)
    ) {
        Text(
            text = "${category.emoji} ${category.displayName}",
            style = MaterialTheme.typography.labelMedium,
            color = color,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
private fun StatusBadge(status: EventStatus) {
    val (bgColor, text) = when (status) {
        EventStatus.UPCOMING -> HmifBlue to "Upcoming"
        EventStatus.ONGOING -> Success to "🔴 Live Now"
        EventStatus.ENDED -> MaterialTheme.colorScheme.surfaceContainerHighest to "Ended"
    }
    val textColor = if (status == EventStatus.ENDED)
        MaterialTheme.colorScheme.onSurfaceVariant else Color.White

    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(HmifTheme.cornerRadius.sm))
            .background(bgColor)
            .padding(horizontal = HmifTheme.spacing.md, vertical = HmifTheme.spacing.xs)
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelMedium,
            color = textColor,
            fontWeight = FontWeight.SemiBold
        )
    }
}

// ════════════════════════════════════════════════════════════════
// DETAILS CARD
// ════════════════════════════════════════════════════════════════

@Composable
private fun DetailsCard(event: EventEntity) {
    GlassmorphicCard(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = HmifTheme.spacing.lg),
        cornerRadius = HmifTheme.cornerRadius.lg
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(HmifTheme.spacing.md)) {
            DetailRow(
                icon = Icons.Default.CalendarMonth,
                label = "Date",
                value = formatFullDate(event.startTime),
                color = HmifBlue
            )

            DetailRow(
                icon = Icons.Default.Schedule,
                label = "Time",
                value = formatTimeRange(event.startTime, event.endTime),
                color = HmifOrange
            )

            DetailRow(
                icon = if (event.isOnline) Icons.Default.Videocam else Icons.Default.LocationOn,
                label = "Location",
                value = if (event.isOnline) "🌐 Online Event" else event.location,
                color = GradientEnd
            )

            if (event.isOnline && !event.meetingUrl.isNullOrBlank()) {
                Text(
                    text = event.meetingUrl,
                    style = MaterialTheme.typography.bodySmall,
                    color = HmifBlue,
                    modifier = Modifier.padding(start = 36.dp)
                )
            }

            DetailRow(
                icon = Icons.Default.Person,
                label = "Organizer",
                value = event.organizerName.ifBlank { "HMIF" },
                color = GradientStart
            )

            event.maxParticipants?.let { max ->
                val spotsLeft = max - event.currentParticipants
                val isFull = spotsLeft <= 0
                DetailRow(
                    icon = Icons.Default.People,
                    label = "Participants",
                    value = "${event.currentParticipants}/$max" +
                            if (isFull) " (Full)" else " ($spotsLeft spots left)",
                    color = if (isFull) Error else Success
                )
            }
        }
    }
}

@Composable
private fun DetailRow(
    icon: ImageVector,
    label: String,
    value: String,
    color: Color
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(HmifTheme.spacing.md)
    ) {
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(RoundedCornerShape(HmifTheme.cornerRadius.sm))
                .background(color.copy(alpha = 0.15f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(20.dp),
                tint = color
            )
        }

        Column {
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = value,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

// ════════════════════════════════════════════════════════════════
// DESCRIPTION SECTION
// ════════════════════════════════════════════════════════════════

@Composable
private fun DescriptionSection(description: String) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(HmifTheme.spacing.lg)
    ) {
        Text(
            text = "About this event",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(HmifTheme.spacing.sm))

        Text(
            text = description,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

// ════════════════════════════════════════════════════════════════
// ACTION BUTTONS
// ════════════════════════════════════════════════════════════════

@Composable
private fun ActionButtons(
    status: EventStatus,
    event: EventEntity,
    onAddToCalendar: () -> Unit,
    onRegister: () -> Unit
) {
    if (status == EventStatus.ENDED) return

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(HmifTheme.spacing.lg),
        verticalArrangement = Arrangement.spacedBy(HmifTheme.spacing.md)
    ) {
        if (status == EventStatus.UPCOMING) {
            val canRegister = event.maxParticipants?.let { event.currentParticipants < it } ?: true

            GradientButton(
                text = if (canRegister) "Register Now" else "Event Full",
                onClick = onRegister,
                enabled = canRegister
            )
        }

        SecondaryButton(
            text = "📅 Add to Calendar",
            onClick = onAddToCalendar
        )
    }
}

// ════════════════════════════════════════════════════════════════
// LOADING STATE
// ════════════════════════════════════════════════════════════════

@Composable
private fun LoadingState(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(HmifTheme.spacing.lg),
        verticalArrangement = Arrangement.spacedBy(HmifTheme.spacing.lg)
    ) {
        SkeletonCard(modifier = Modifier.fillMaxWidth(), height = 200.dp)
        SkeletonCard(modifier = Modifier.fillMaxWidth(), height = 80.dp)
        SkeletonCard(modifier = Modifier.fillMaxWidth(), height = 200.dp)
    }
}

// ════════════════════════════════════════════════════════════════
// UTILITIES
// ════════════════════════════════════════════════════════════════

private fun formatFullDate(timestamp: Long): String {
    return try {
        val sdf = SimpleDateFormat("EEEE, dd MMMM yyyy", Locale.getDefault())
        sdf.format(Date(timestamp))
    } catch (e: Exception) {
        ""
    }
}

private fun formatTimeRange(startTime: Long, endTime: Long): String {
    return try {
        val sdf = SimpleDateFormat("HH:mm", Locale.getDefault())
        "${sdf.format(Date(startTime))} - ${sdf.format(Date(endTime))}"
    } catch (e: Exception) {
        ""
    }
}
