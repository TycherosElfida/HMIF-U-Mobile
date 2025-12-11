package com.example.hmifu_mobile.feature.admin

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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Announcement
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Event
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.People
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.example.hmifu_mobile.data.local.entity.AnnouncementEntity
import com.example.hmifu_mobile.data.local.entity.EventEntity
import com.example.hmifu_mobile.ui.components.HmifCard
import com.example.hmifu_mobile.ui.components.LoadingSkeletonList
import com.example.hmifu_mobile.ui.components.ShimmerBox
import com.example.hmifu_mobile.feature.admin.treasurer.TreasurerScreen

/**
 * Admin dashboard screen.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminScreen(
    onNavigateBack: () -> Unit = {},
    onCreateAnnouncement: () -> Unit = {},
    onCreateEvent: () -> Unit = {},
    onViewRegistrants: (String) -> Unit = {},
    onNavigateToTreasurer: () -> Unit = {},
    onNavigateToUserManagement: () -> Unit = {},
    onNavigateToFinancials: () -> Unit = {},
    onNavigateToDocuments: () -> Unit = {},
    onNavigateToElection: () -> Unit = {},
    onNavigateToContent: () -> Unit = {},
    viewModel: AdminViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    
    // Check if user is not admin
    if (!uiState.isLoading && !uiState.isAdmin) {
        AccessDeniedScreen(onNavigateBack = onNavigateBack)
        return
    }

    // Role-based routing
    if (uiState.role == "treasurer") {
        com.example.hmifu_mobile.feature.admin.treasurer.TreasurerScreen(
            onNavigateBack = onNavigateBack
        )
        return
    }

    if (uiState.role == "president") {
        com.example.hmifu_mobile.feature.admin.president.PresidentScreen(
            onNavigateBack = onNavigateBack,
            onNavigateToUserManagement = onNavigateToUserManagement,
            onNavigateToFinancials = onNavigateToFinancials,
            onNavigateToDocuments = onNavigateToDocuments,
            onNavigateToElection = onNavigateToElection,
            onNavigateToContent = { onNavigateToContent() }
        )
        return
    }

    // Default Admin Dashboard for other roles (President, etc for now)
    AdminDashboardContent(
        uiState = uiState,
        onNavigateBack = onNavigateBack,
        onCreateAnnouncement = onCreateAnnouncement,
        onCreateEvent = onCreateEvent,
        onViewRegistrants = onViewRegistrants,
        viewModel = viewModel
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AdminDashboardContent(
    uiState: AdminUiState,
    onNavigateBack: () -> Unit,
    onCreateAnnouncement: () -> Unit,
    onCreateEvent: () -> Unit,
    onViewRegistrants: (String) -> Unit,
    viewModel: AdminViewModel
) {
    val snackbarHostState = remember { SnackbarHostState() }
    var showFabMenu by remember { mutableStateOf(false) }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Admin Panel",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                }
            )
        },
        floatingActionButton = {
            Column(horizontalAlignment = Alignment.End) {
                if (showFabMenu) {
                    // Create Event FAB
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(bottom = 12.dp)
                    ) {
                        Card(
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.surface
                            ),
                            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                        ) {
                            Text(
                                text = "Create Event",
                                modifier = Modifier.padding(8.dp),
                                style = MaterialTheme.typography.labelMedium
                            )
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        FloatingActionButton(
                            onClick = {
                                showFabMenu = false
                                onCreateEvent()
                            },
                            containerColor = MaterialTheme.colorScheme.secondaryContainer,
                            modifier = Modifier.size(48.dp)
                        ) {
                            Icon(Icons.Default.Event, contentDescription = "Create Event")
                        }
                    }

                    // Create Announcement FAB
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(bottom = 12.dp)
                    ) {
                        Card(
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.surface
                            ),
                            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                        ) {
                            Text(
                                text = "Create Announcement",
                                modifier = Modifier.padding(8.dp),
                                style = MaterialTheme.typography.labelMedium
                            )
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        FloatingActionButton(
                            onClick = {
                                showFabMenu = false
                                onCreateAnnouncement()
                            },
                            containerColor = MaterialTheme.colorScheme.tertiaryContainer,
                            modifier = Modifier.size(48.dp)
                        ) {
                            Icon(
                                Icons.AutoMirrored.Filled.Announcement,
                                contentDescription = "Create Announcement"
                            )
                        }
                    }
                }

                // Main FAB
                FloatingActionButton(
                    onClick = { showFabMenu = !showFabMenu },
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                ) {
                    Icon(
                        if(showFabMenu) androidx.compose.material.icons.Icons.Default.Add else Icons.Default.Add, // Rotate logic could be added here
                        contentDescription = if (showFabMenu) "Close" else "Create",
                        modifier = Modifier.size(24.dp) // Standard size
                    )
                }
            }
        }
    ) { padding ->
        if (uiState.isLoading) {
            LoadingSkeletonList(itemCount = 4) {
                ShimmerBox(modifier = Modifier
                    .fillMaxWidth()
                    .height(80.dp))
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                // Stats cards
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        StatCard(
                            title = "Announcements",
                            count = uiState.totalAnnouncements,
                            icon = Icons.AutoMirrored.Filled.Announcement,
                            modifier = Modifier.weight(1f)
                        )
                        StatCard(
                            title = "Events",
                            count = uiState.totalEvents,
                            icon = Icons.Default.Event,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }

                // Recent announcements
                item {
                    SectionHeader(title = "Recent Announcements")
                }

                if (uiState.recentAnnouncements.isEmpty()) {
                    item {
                        EmptyCard(message = "No announcements yet")
                    }
                } else {
                    item {
                        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                            uiState.recentAnnouncements.forEach { announcement ->
                                AnnouncementItem(announcement = announcement)
                            }
                        }
                    }
                }

                // Recent events
                item {
                    SectionHeader(title = "Recent Events")
                }

                if (uiState.recentEvents.isEmpty()) {
                    item {
                        EmptyCard(message = "No events yet")
                    }
                } else {
                    item {
                        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                            uiState.recentEvents.forEach { event ->
                                EventItem(
                                    event = event,
                                    onViewRegistrants = { onViewRegistrants(event.id) }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun AccessDeniedScreen(onNavigateBack: () -> Unit) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(
                Icons.Default.Lock,
                contentDescription = null,
                modifier = Modifier.size(64.dp),
                tint = MaterialTheme.colorScheme.error
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Access Denied",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "You don't have admin privileges",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(24.dp))
            androidx.compose.material3.TextButton(onClick = onNavigateBack) {
                Text("Go Back")
            }
        }
    }
}

@Composable
private fun StatCard(
    title: String,
    count: Int,
    icon: ImageVector,
    modifier: Modifier = Modifier
) {
    HmifCard(
        modifier = modifier
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                icon,
                contentDescription = null,
                modifier = Modifier.size(32.dp),
                tint = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = count.toString(),
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = title,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun SectionHeader(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.SemiBold,
        modifier = Modifier.padding(vertical = 4.dp)
    )
}

@Composable
private fun EmptyCard(message: String) {
    HmifCard(modifier = Modifier.fillMaxWidth()) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = message,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun AnnouncementItem(announcement: AnnouncementEntity) {
    HmifCard(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                Icons.AutoMirrored.Filled.Announcement,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = announcement.title,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = announcement.category,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun EventItem(
    event: EventEntity,
    onViewRegistrants: () -> Unit
) {
    HmifCard(
        modifier = Modifier.fillMaxWidth(),
        onClick = onViewRegistrants
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                Icons.Default.Event,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = event.title,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = "${event.currentParticipants}/${event.maxParticipants ?: "∞"} registered",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Icon(
                Icons.Default.People,
                contentDescription = "View registrants",
                tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.7f)
            )
        }
    }
}
