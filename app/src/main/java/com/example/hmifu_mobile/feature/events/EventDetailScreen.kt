package com.example.hmifu_mobile.feature.events

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.hmifu_mobile.data.local.entity.EventCategory
import com.example.hmifu_mobile.data.local.entity.EventEntity
import com.example.hmifu_mobile.data.local.entity.EventStatus
import com.example.hmifu_mobile.data.repository.EventRepository
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Event detail screen.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EventDetailScreen(
    eventId: String,
    eventRepository: EventRepository,
    onNavigateBack: () -> Unit,
    onAddToCalendar: (EventEntity) -> Unit = {}
) {
    var event by remember { mutableStateOf<EventEntity?>(null) }

    LaunchedEffect(eventId) {
        eventRepository.observeById(eventId).collect { event = it }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Event Details") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        event?.let { evt ->
            EventDetailContent(
                event = evt,
                onAddToCalendar = { onAddToCalendar(evt) },
                modifier = Modifier.padding(padding)
            )
        } ?: run {
            // Loading state
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text("Loading event...")
            }
        }
    }
}

@Composable
private fun EventDetailContent(
    event: EventEntity,
    onAddToCalendar: () -> Unit,
    modifier: Modifier = Modifier
) {
    val status = EventStatus.fromEvent(event)
    val category = EventCategory.fromString(event.category)

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        // Category + Status header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "${category.emoji} ${category.displayName}",
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.primary
            )
            Text(
                text = status.name,
                style = MaterialTheme.typography.labelLarge,
                color = when (status) {
                    EventStatus.UPCOMING -> MaterialTheme.colorScheme.primary
                    EventStatus.ONGOING -> MaterialTheme.colorScheme.tertiary
                    EventStatus.ENDED -> MaterialTheme.colorScheme.outline
                },
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Title
        Text(
            text = event.title,
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Details card
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant
            )
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                // Date
                DetailRow(
                    icon = Icons.Default.CalendarMonth,
                    label = "Date",
                    value = formatFullDate(event.startTime)
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Time
                DetailRow(
                    icon = Icons.Default.Schedule,
                    label = "Time",
                    value = formatTimeRange(event.startTime, event.endTime)
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Location
                DetailRow(
                    icon = if (event.isOnline) Icons.Default.Videocam else Icons.Default.LocationOn,
                    label = "Location",
                    value = if (event.isOnline) "Online Event" else event.location
                )

                if (event.isOnline && !event.meetingUrl.isNullOrBlank()) {
                    Text(
                        text = event.meetingUrl,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(start = 28.dp)
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Organizer
                DetailRow(
                    icon = Icons.Default.Person,
                    label = "Organizer",
                    value = event.organizerName.ifBlank { "HMIF" }
                )

                // Participants
                event.maxParticipants?.let { max ->
                    Spacer(modifier = Modifier.height(12.dp))
                    DetailRow(
                        icon = Icons.Default.People,
                        label = "Participants",
                        value = "${event.currentParticipants}/$max (${max - event.currentParticipants} spots left)"
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Description
        Text(
            text = "About this event",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = event.description,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(32.dp))

        // Action buttons
        if (status != EventStatus.ENDED) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedButton(
                    onClick = onAddToCalendar,
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(Icons.Default.CalendarMonth, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Add to Calendar")
                }

                if (status == EventStatus.UPCOMING) {
                    Button(
                        onClick = { /* TODO: Register */ },
                        modifier = Modifier.weight(1f),
                        enabled = event.maxParticipants?.let { event.currentParticipants < it } ?: true
                    ) {
                        Text("Register")
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}

@Composable
private fun DetailRow(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    value: String
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(
            icon,
            contentDescription = null,
            modifier = Modifier.size(20.dp),
            tint = MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.width(8.dp))
        Column {
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.outline
            )
            Text(
                text = value,
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

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
