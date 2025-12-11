package com.example.hmifu_mobile.feature.events

import android.content.Intent
import android.provider.CalendarContract
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.hmifu_mobile.data.local.entity.EventCategory
import com.example.hmifu_mobile.data.local.entity.EventEntity
import com.example.hmifu_mobile.data.local.entity.EventStatus
import com.example.hmifu_mobile.data.repository.EventRepository
import com.example.hmifu_mobile.ui.components.HmifButton
import com.example.hmifu_mobile.ui.components.HmifCard
import com.example.hmifu_mobile.ui.components.HmifOutlinedButton
import kotlinx.coroutines.launch
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
    onRegister: (EventEntity) -> Unit = {}
) {
    var event by remember { mutableStateOf<EventEntity?>(null) }
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(eventId) {
        eventRepository.observeById(eventId).collect { event = it }
    }

    fun addToCalendar(evt: EventEntity) {
        try {
            val intent = Intent(Intent.ACTION_INSERT).apply {
                data = CalendarContract.Events.CONTENT_URI
                putExtra(CalendarContract.Events.TITLE, evt.title)
                putExtra(CalendarContract.Events.DESCRIPTION, evt.description)
                putExtra(CalendarContract.Events.EVENT_LOCATION, evt.location)
                putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, evt.startTime)
                putExtra(CalendarContract.EXTRA_EVENT_END_TIME, evt.endTime)
            }
            context.startActivity(intent)
        } catch (e: Exception) {
            scope.launch {
                snackbarHostState.showSnackbar("Could not open calendar app")
            }
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { Text("Event Details") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent,
                    scrolledContainerColor = MaterialTheme.colorScheme.surface
                )
            )
        }
    ) { padding ->
        event?.let { evt ->
            EventDetailContent(
                event = evt,
                onAddToCalendar = { addToCalendar(evt) },
                onRegister = { onRegister(evt) },
                modifier = Modifier.padding(padding)
            )
        } ?: run {
            // Loading state
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
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
    onRegister: () -> Unit,
    modifier: Modifier = Modifier
) {
    val status = EventStatus.fromEvent(event)
    val category = EventCategory.fromString(event.category)

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        // 1. Header Section (Image or Gradient Banner)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp) // Taller header
        ) {
            if (event.imageUrl != null) {
                coil.compose.AsyncImage(
                    model = coil.request.ImageRequest.Builder(androidx.compose.ui.platform.LocalContext.current)
                        .data(event.imageUrl)
                        .crossfade(true)
                        .build(),
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = androidx.compose.ui.layout.ContentScale.Crop
                )
                // Gradient overlay for text readability
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            brush = Brush.verticalGradient(
                                colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.7f))
                            )
                        )
                )
            } else {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            brush = Brush.verticalGradient(
                                colors = listOf(
                                    MaterialTheme.colorScheme.primary,
                                    MaterialTheme.colorScheme.primaryContainer
                                )
                            )
                        )
                )
            }

            // Event Title over Banner
            Column(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(16.dp)
            ) {
                 Text(
                    text = "${category.emoji} ${category.displayName}",
                    style = MaterialTheme.typography.labelMedium,
                    color = Color.White.copy(alpha = 0.9f) // Always white on banner
                )
                Text(
                    text = event.title,
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
        }

        // 2. Content Body
        Column(modifier = Modifier.padding(16.dp)) {
            
            // Status Chip
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                // Status Badge
                androidx.compose.material3.Surface(
                    color = when (status) {
                        EventStatus.UPCOMING -> MaterialTheme.colorScheme.primaryContainer
                        EventStatus.ONGOING -> MaterialTheme.colorScheme.tertiaryContainer
                        EventStatus.ENDED -> MaterialTheme.colorScheme.surfaceVariant
                    },
                    shape = MaterialTheme.shapes.small
                ) {
                    Text(
                        text = status.name,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(24.dp))

            // Info Card
            HmifCard(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    // Date
                    DetailRow(
                        icon = Icons.Default.CalendarMonth,
                        label = "Date",
                        value = formatFullDate(event.startTime)
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Time
                    DetailRow(
                        icon = Icons.Default.Schedule,
                        label = "Time",
                        value = formatTimeRange(event.startTime, event.endTime)
                    )

                    Spacer(modifier = Modifier.height(16.dp))

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
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    // Organizer
                    DetailRow(
                        icon = Icons.Default.Person,
                        label = "Organizer",
                        value = event.organizerName.ifBlank { "HMIF" }
                    )
                    
                     // Participants
                    event.maxParticipants?.let { max ->
                        Spacer(modifier = Modifier.height(16.dp))
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
                    HmifOutlinedButton(
                        onClick = onAddToCalendar,
                        text = "Add to Calendar",
                        leadingIcon = Icons.Default.CalendarMonth,
                        modifier = Modifier.weight(1f)
                    )

                    if (status == EventStatus.UPCOMING) {
                        HmifButton(
                            onClick = onRegister,
                            text = "Register",
                            modifier = Modifier.weight(1f),
                            enabled = event.maxParticipants?.let { event.currentParticipants < it }
                                ?: true
                        )
                    }
                }
            }
        }
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
            modifier = Modifier.size(24.dp), // Slightly larger icon
            tint = MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.width(12.dp))
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
    } catch (e: Exception) { "" }
}

private fun formatTimeRange(startTime: Long, endTime: Long): String {
    return try {
        val sdf = SimpleDateFormat("HH:mm", Locale.getDefault())
        "${sdf.format(Date(startTime))} - ${sdf.format(Date(endTime))}"
    } catch (e: Exception) { "" }
}
