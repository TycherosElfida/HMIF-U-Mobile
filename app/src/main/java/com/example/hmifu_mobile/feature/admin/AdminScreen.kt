package com.example.hmifu_mobile.feature.admin

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
import androidx.compose.material.icons.automirrored.filled.Announcement
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Event
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.SupervisorAccount
import androidx.compose.material.icons.rounded.AdminPanelSettings
import androidx.compose.material.icons.rounded.AccountBalanceWallet
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
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.example.hmifu_mobile.data.local.entity.AnnouncementEntity
import com.example.hmifu_mobile.data.local.entity.EventEntity
import com.example.hmifu_mobile.ui.components.GlassmorphicCard
import com.example.hmifu_mobile.ui.components.SkeletonCard
import com.example.hmifu_mobile.ui.components.StaggeredAnimatedItem
import com.example.hmifu_mobile.ui.theme.GradientEnd
import com.example.hmifu_mobile.ui.theme.HmifBlue
import com.example.hmifu_mobile.ui.theme.HmifOrange
import com.example.hmifu_mobile.ui.theme.HmifPurple
import com.example.hmifu_mobile.ui.theme.HmifTheme
import com.example.hmifu_mobile.ui.theme.Success

/**
 * Admin Screen - Premium 2025 Design
 *
 * Features:
 * - Glassmorphic stat cards
 * - FAB menu for creation
 * - Announcement and event lists
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminScreen(
    onNavigateBack: () -> Unit = {},
    onManageAnnouncements: () -> Unit = {},
    onEditAnnouncement: (String) -> Unit = {},
    onManageEvents: () -> Unit = {},
    onViewRegistrants: (String) -> Unit = {},
    onFinance: () -> Unit = {},
    onSecretariat: () -> Unit = {},
    onManageUsers: () -> Unit = {},
    viewModel: AdminViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    var showFabMenu by remember { mutableStateOf(false) }

    LaunchedEffect(uiState.errorMessage) {
        uiState.errorMessage?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearError()
        }
    }

    if (!uiState.isLoading && !uiState.isAdmin) {
        AccessDeniedScreen(onNavigateBack = onNavigateBack)
        return
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
                            imageVector = Icons.Rounded.AdminPanelSettings,
                            contentDescription = null,
                            tint = HmifOrange
                        )
                        Text(
                            text = "Admin Panel",
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
        floatingActionButton = {
            FabMenu(
                showMenu = showFabMenu,
                onToggle = { showFabMenu = !showFabMenu },
                onManageAnnouncements = {
                    showFabMenu = false
                    onManageAnnouncements()
                },
                onManageEvents = {
                    showFabMenu = false
                    onManageEvents()
                }
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        if (uiState.isLoading) {
            LoadingState(modifier = Modifier.padding(padding))
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentPadding = PaddingValues(HmifTheme.spacing.lg),
                verticalArrangement = Arrangement.spacedBy(HmifTheme.spacing.lg)
            ) {
                // Stats row
                item {
                    StaggeredAnimatedItem(index = 0) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(HmifTheme.spacing.md)
                        ) {
                            StatCard(
                                title = "Announcements",
                                count = uiState.totalAnnouncements,
                                icon = Icons.AutoMirrored.Filled.Announcement,
                                color = HmifBlue,
                                modifier = Modifier.weight(1f)
                            )
                            StatCard(
                                title = "Events",
                                count = uiState.totalEvents,
                                icon = Icons.Default.Event,
                                color = HmifOrange,
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                    }

                // Finance Section (Treasurer & President)
                if (uiState.isTreasurer || uiState.isPresident) {
                    item {
                        StaggeredAnimatedItem(index = 1) {
                            GlassmorphicCard(
                                modifier = Modifier.fillMaxWidth(),
                                onClick = onFinance,
                                cornerRadius = HmifTheme.cornerRadius.md
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(HmifTheme.spacing.md)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(48.dp)
                                            .clip(RoundedCornerShape(HmifTheme.cornerRadius.md))
                                            .background(Success.copy(alpha = 0.15f)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = Icons.Rounded.AccountBalanceWallet,
                                            contentDescription = null,
                                            tint = Success,
                                            modifier = Modifier.size(26.dp)
                                        )
                                    }
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = "Finance Dashboard",
                                            style = MaterialTheme.typography.titleMedium,
                                            fontWeight = FontWeight.Bold
                                        )
                                        Text(
                                            text = "Manage Income & Expenses",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                    Icon(
                                        Icons.AutoMirrored.Filled.ArrowBack, // Changed to ArrowForward in full impl or just reused icon
                                        contentDescription = "Go",
                                        modifier = Modifier.rotate(180f)
                                    )
                                }
                            }
                        }
                    }
                }

                // Secretariat Section (Secretary & President)
                if (uiState.isSecretary || uiState.isPresident) {
                    item {
                        StaggeredAnimatedItem(index = 2) {
                            GlassmorphicCard(
                                modifier = Modifier.fillMaxWidth(),
                                onClick = onSecretariat,
                                cornerRadius = HmifTheme.cornerRadius.md
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(HmifTheme.spacing.md)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(48.dp)
                                            .clip(RoundedCornerShape(HmifTheme.cornerRadius.md))
                                            .background(HmifBlue.copy(alpha = 0.15f)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Description, // Requires import
                                            contentDescription = null,
                                            tint = HmifBlue,
                                            modifier = Modifier.size(26.dp)
                                        )
                                    }
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = "Secretariat Dashboard",
                                            style = MaterialTheme.typography.titleMedium,
                                            fontWeight = FontWeight.Bold
                                        )
                                        Text(
                                            text = "Proposals & LPJ Management",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                    Icon(
                                        Icons.AutoMirrored.Filled.ArrowBack, // Changed to ArrowForward in full impl or just reused icon
                                        contentDescription = "Go",
                                        modifier = Modifier.rotate(180f)
                                    )
                                }
                            }
                        }
                    }
                }

                // User Management (President Only)
                if (uiState.isPresident) {
                    item {
                        StaggeredAnimatedItem(index = 3) {
                            GlassmorphicCard(
                                modifier = Modifier.fillMaxWidth(),
                                onClick = onManageUsers,
                                cornerRadius = HmifTheme.cornerRadius.md
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(HmifTheme.spacing.md)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(48.dp)
                                            .clip(RoundedCornerShape(HmifTheme.cornerRadius.md))
                                            .background(HmifPurple.copy(alpha = 0.15f)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.SupervisorAccount,
                                            contentDescription = null,
                                            tint = HmifPurple,
                                            modifier = Modifier.size(26.dp)
                                        )
                                    }
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = "Manage Users",
                                            style = MaterialTheme.typography.titleMedium,
                                            fontWeight = FontWeight.Bold
                                        )
                                        Text(
                                            text = "Assign Roles (President, Treasurer, etc.)",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                    Icon(
                                        Icons.AutoMirrored.Filled.ArrowBack, // Changed to ArrowForward in full impl or just reused icon
                                        contentDescription = "Go",
                                        modifier = Modifier.rotate(180f)
                                    )
                                }
                            }
                        }
                    }
                }

                // Recent announcements
                item {
                    StaggeredAnimatedItem(index = 3) {
                        SectionHeader(title = "📢 Recent Announcements")
                    }
                }

                if (uiState.recentAnnouncements.isEmpty()) {
                    item { EmptyCard(message = "No announcements yet") }
                } else {
                    itemsIndexed(uiState.recentAnnouncements) { index, announcement ->
                        StaggeredAnimatedItem(index = index + 2) {
                            AnnouncementItem(
                                announcement = announcement,
                                onClick = { onEditAnnouncement(announcement.id) }
                            )
                        }
                    }
                }

                // Recent events
                item {
                    StaggeredAnimatedItem(index = uiState.recentAnnouncements.size + 2) {
                        SectionHeader(title = "📅 Recent Events")
                    }
                }

                if (uiState.recentEvents.isEmpty()) {
                    item { EmptyCard(message = "No events yet") }
                } else {
                    itemsIndexed(uiState.recentEvents) { index, event ->
                        StaggeredAnimatedItem(index = index + uiState.recentAnnouncements.size + 3) {
                            EventItem(
                                event = event,
                                onViewRegistrants = { onViewRegistrants(event.id) }
                            )
                        }
                    }
                }

                item { Spacer(modifier = Modifier.height(80.dp)) }
            }
        }
    }
}

// ════════════════════════════════════════════════════════════════
// FAB MENU
// ════════════════════════════════════════════════════════════════

@Composable
private fun FabMenu(
    showMenu: Boolean,
    onToggle: () -> Unit,
    onManageAnnouncements: () -> Unit,
    onManageEvents: () -> Unit
) {
    Column(horizontalAlignment = Alignment.End) {
        if (showMenu) {
            FabMenuItem(
                label = "Manage Events",
                icon = Icons.Default.Event,
                color = HmifBlue,
                onClick = onManageEvents
            )
            Spacer(modifier = Modifier.height(HmifTheme.spacing.sm))
            FabMenuItem(
                label = "Manage Announcements",
                icon = Icons.AutoMirrored.Filled.Announcement,
                color = HmifPurple,
                onClick = onManageAnnouncements
            )
            Spacer(modifier = Modifier.height(HmifTheme.spacing.sm))
        }

        FloatingActionButton(
            onClick = onToggle,
            containerColor = HmifOrange
        ) {
            Icon(
                Icons.Default.Add,
                contentDescription = if (showMenu) "Close" else "Create",
                tint = Color.White
            )
        }
    }
}

@Composable
private fun FabMenuItem(
    label: String,
    icon: ImageVector,
    color: Color,
    onClick: () -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(HmifTheme.spacing.sm)
    ) {
        Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)) {
            Text(
                text = label,
                modifier = Modifier.padding(HmifTheme.spacing.sm),
                style = MaterialTheme.typography.bodySmall
            )
        }
        FloatingActionButton(
            onClick = onClick,
            containerColor = color.copy(alpha = 0.15f),
            contentColor = color
        ) {
            Icon(icon, contentDescription = label)
        }
    }
}

// ════════════════════════════════════════════════════════════════
// STAT CARD
// ════════════════════════════════════════════════════════════════

@Composable
private fun StatCard(
    title: String,
    count: Int,
    icon: ImageVector,
    color: Color,
    modifier: Modifier = Modifier
) {
    GlassmorphicCard(
        modifier = modifier,
        cornerRadius = HmifTheme.cornerRadius.lg
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(HmifTheme.spacing.sm)
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(RoundedCornerShape(HmifTheme.cornerRadius.md))
                    .background(color.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    modifier = Modifier.size(26.dp),
                    tint = color
                )
            }
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

// ════════════════════════════════════════════════════════════════
// SECTION HEADER
// ════════════════════════════════════════════════════════════════

@Composable
private fun SectionHeader(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.SemiBold,
        modifier = Modifier.padding(vertical = HmifTheme.spacing.sm)
    )
}

@Composable
private fun EmptyCard(message: String) {
    GlassmorphicCard(
        modifier = Modifier.fillMaxWidth(),
        cornerRadius = HmifTheme.cornerRadius.md
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(HmifTheme.spacing.xl),
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

// ════════════════════════════════════════════════════════════════
// LIST ITEMS
// ════════════════════════════════════════════════════════════════

@Composable
private fun AnnouncementItem(
    announcement: AnnouncementEntity,
    onClick: () -> Unit
) {
    GlassmorphicCard(
        modifier = Modifier.fillMaxWidth(),
        onClick = onClick,
        cornerRadius = HmifTheme.cornerRadius.md
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(HmifTheme.spacing.md)
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(HmifTheme.cornerRadius.sm))
                    .background(HmifBlue.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.AutoMirrored.Filled.Announcement,
                    contentDescription = null,
                    tint = HmifBlue,
                    modifier = Modifier.size(22.dp)
                )
            }
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = announcement.title,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = announcement.category,
                    style = MaterialTheme.typography.bodySmall,
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
    GlassmorphicCard(
        modifier = Modifier.fillMaxWidth(),
        onClick = onViewRegistrants,
        cornerRadius = HmifTheme.cornerRadius.md
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(HmifTheme.spacing.md)
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(HmifTheme.cornerRadius.sm))
                    .background(HmifOrange.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Default.Event,
                    contentDescription = null,
                    tint = HmifOrange,
                    modifier = Modifier.size(22.dp)
                )
            }
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = event.title,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = "${event.currentParticipants}/${event.maxParticipants ?: "∞"} registered",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Icon(
                Icons.Default.People,
                contentDescription = "View registrants",
                tint = GradientEnd
            )
        }
    }
}

// ════════════════════════════════════════════════════════════════
// STATES
// ════════════════════════════════════════════════════════════════

@Composable
private fun AccessDeniedScreen(onNavigateBack: () -> Unit) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(HmifTheme.spacing.md)
        ) {
            Text(text = "🔒", style = MaterialTheme.typography.displayLarge)
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
            TextButton(onClick = onNavigateBack) {
                Text("Go Back")
            }
        }
    }
}

@Composable
private fun LoadingState(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.padding(HmifTheme.spacing.lg),
        verticalArrangement = Arrangement.spacedBy(HmifTheme.spacing.md)
    ) {
        Row(horizontalArrangement = Arrangement.spacedBy(HmifTheme.spacing.md)) {
            SkeletonCard(modifier = Modifier.weight(1f), height = 120.dp)
            SkeletonCard(modifier = Modifier.weight(1f), height = 120.dp)
        }
        SkeletonCard(modifier = Modifier.fillMaxWidth(), height = 60.dp)
        SkeletonCard(modifier = Modifier.fillMaxWidth(), height = 60.dp)
        SkeletonCard(modifier = Modifier.fillMaxWidth(), height = 60.dp)
    }
}
