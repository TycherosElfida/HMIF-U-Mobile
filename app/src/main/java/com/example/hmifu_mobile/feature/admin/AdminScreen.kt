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
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material.icons.rounded.AccountBalanceWallet
import androidx.compose.material.icons.rounded.School
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TabRow
import androidx.compose.material3.Tab
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.mutableIntStateOf
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
 * Features: 3 Tabs (Dashboard, Events, Announcements)
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminScreen(
    onNavigateBack: () -> Unit = {},
    onManageAnnouncements: () -> Unit = {},
    onEditAnnouncement: (String) -> Unit = {},
    onManageEvents: () -> Unit = {},
    onEditEvent: (String) -> Unit = {},
    onViewRegistrants: (String) -> Unit = {},
    onManageResources: () -> Unit = {},
    onFinance: () -> Unit = {},
    onSecretariat: () -> Unit = {},
    onManageUsers: () -> Unit = {},
    onScanTicket: () -> Unit = {},
    viewModel: AdminViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    
    // Tab State
    var selectedTabIndex by remember { mutableIntStateOf(0) }
    val tabs = listOf("Dashboard", "Events", "Announcements")

    // Event & Announcement ViewModels
    val eventViewModel: ManageEventsViewModel = hiltViewModel()
    val eventState by eventViewModel.uiState.collectAsState()
    
    val announcementViewModel: ManageAnnouncementsViewModel = hiltViewModel()
    val announcementState by announcementViewModel.uiState.collectAsState()

    // Delete Confirmation State
    var showEventDeleteDialog by remember { mutableStateOf<String?>(null) }
    var showAnnouncementDeleteDialog by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(uiState.errorMessage) {
        uiState.errorMessage?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearError()
        }
    }
    
    // Delete Dialogs
    if (showEventDeleteDialog != null) {
        AlertDialog(
            onDismissRequest = { showEventDeleteDialog = null },
            title = { Text("Delete Event") },
            text = { Text("Are you sure? This cannot be undone.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        eventViewModel.deleteEvent(showEventDeleteDialog!!)
                        showEventDeleteDialog = null
                    },
                    colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.error)
                ) { Text("Delete") }
            },
            dismissButton = {
                TextButton(onClick = { showEventDeleteDialog = null }) { Text("Cancel") }
            }
        )
    }

    if (showAnnouncementDeleteDialog != null) {
        AlertDialog(
            onDismissRequest = { showAnnouncementDeleteDialog = null },
            title = { Text("Delete Announcement") },
            text = { Text("Are you sure? This cannot be undone.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        announcementViewModel.deleteAnnouncement(showAnnouncementDeleteDialog!!)
                        showAnnouncementDeleteDialog = null
                    },
                    colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.error)
                ) { Text("Delete") }
            },
            dismissButton = {
                TextButton(onClick = { showAnnouncementDeleteDialog = null }) { Text("Cancel") }
            }
        )
    }

    if (!uiState.isLoading && !uiState.isAdmin) {
        AccessDeniedScreen(onNavigateBack = onNavigateBack)
        return
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            Column {
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
                
                TabRow(
                    selectedTabIndex = selectedTabIndex,
                    containerColor = Color.Transparent,
                    contentColor = MaterialTheme.colorScheme.primary,
                    divider = {}
                ) {
                    tabs.forEachIndexed { index, title ->
                        Tab(
                            selected = selectedTabIndex == index,
                            onClick = { selectedTabIndex = index },
                            text = { 
                                Text(
                                    text = title, 
                                    fontWeight = if (selectedTabIndex == index) FontWeight.Bold else FontWeight.Normal
                                ) 
                            }
                        )
                    }
                }
            }
        },
        floatingActionButton = {
            when (selectedTabIndex) {
                1 -> { // Events
                    FloatingActionButton(
                        onClick = { onManageEvents() },
                        containerColor = HmifBlue
                    ) {
                        Icon(Icons.Default.Add, "Create Event")
                    }
                }
                2 -> { // Announcements
                    FloatingActionButton(
                        onClick = { onManageAnnouncements() },
                        containerColor = HmifPurple
                    ) {
                        Icon(Icons.Default.Add, "Create Announcement")
                    }
                }
            }
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        Box(modifier = Modifier.padding(padding)) {
            when (selectedTabIndex) {
                0 -> DashboardContent(
                    uiState = uiState,
                    onFinance = onFinance,
                    onSecretariat = onSecretariat,
                    onManageResources = onManageResources,
                    onScanTicket = onScanTicket,
                    onManageUsers = onManageUsers
                )
                1 -> ManageEventsContent(
                    events = eventState.events,
                    onEditEvent = onEditEvent,
                    onDeleteEvent = { showEventDeleteDialog = it }
                )
                2 -> ManageAnnouncementsContent(
                    announcements = announcementState.announcements,
                    onEditAnnouncement = onEditAnnouncement,
                    onDeleteAnnouncement = { showAnnouncementDeleteDialog = it }
                )
            }
        }
    }
}

@Composable
private fun DashboardContent(
    uiState: AdminUiState,
    onFinance: () -> Unit,
    onSecretariat: () -> Unit,
    onManageResources: () -> Unit,
    onScanTicket: () -> Unit,
    onManageUsers: () -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
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

        // Finance Section
        if (uiState.isTreasurer || uiState.isPresident || uiState.isVicePresident) {
            item {
                StaggeredAnimatedItem(index = 1) {
                    GlassmorphicCard(
                        modifier = Modifier.fillMaxWidth(),
                        onClick = onFinance,
                        cornerRadius = HmifTheme.cornerRadius.md
                    ) {
                        DashboardCardContent(
                            title = "Finance Dashboard",
                            subtitle = "Manage Income & Expenses",
                            icon = Icons.Rounded.AccountBalanceWallet,
                            color = Success
                        )
                    }
                }
            }
        }

        // Secretariat Section
        if (uiState.isSecretary || uiState.isPresident || uiState.isVicePresident) {
            item {
                StaggeredAnimatedItem(index = 2) {
                    GlassmorphicCard(
                        modifier = Modifier.fillMaxWidth(),
                        onClick = onSecretariat,
                        cornerRadius = HmifTheme.cornerRadius.md
                    ) {
                        DashboardCardContent(
                            title = "Secretariat Dashboard",
                            subtitle = "Proposals & LPJ Management",
                            icon = Icons.Default.Description,
                            color = HmifBlue
                        )
                    }
                }
            }
        }

        // Resources Management
        if (uiState.isAdmin) {
            item {
                StaggeredAnimatedItem(index = 3) {
                    GlassmorphicCard(
                        modifier = Modifier.fillMaxWidth(),
                        onClick = onManageResources,
                        cornerRadius = HmifTheme.cornerRadius.md
                    ) {
                        DashboardCardContent(
                            title = "Manage Resources",
                            subtitle = "Bank Soal & Academic Files",
                            icon = Icons.Rounded.School,
                            color = HmifOrange
                        )
                    }
                }
            }
        }

        // Scan Ticket Action
        item {
            StaggeredAnimatedItem(index = 4) {
                GlassmorphicCard(
                    modifier = Modifier.fillMaxWidth(),
                    onClick = onScanTicket,
                    cornerRadius = HmifTheme.cornerRadius.md
                ) {
                    DashboardCardContent(
                        title = "Scan Event Ticket",
                        subtitle = "Check-in participants",
                        icon = Icons.Default.QrCodeScanner,
                        color = Color.Gray
                    )
                }
            }
        }

        // User Management
        if (uiState.isPresident || uiState.isVicePresident) {
            item {
                StaggeredAnimatedItem(index = 5) {
                    GlassmorphicCard(
                        modifier = Modifier.fillMaxWidth(),
                        onClick = onManageUsers,
                        cornerRadius = HmifTheme.cornerRadius.md
                    ) {
                        DashboardCardContent(
                            title = "Manage Users",
                            subtitle = "Assign Roles",
                            icon = Icons.Default.SupervisorAccount,
                            color = HmifPurple
                        )
                    }
                }
            }
        }
        
        item { Spacer(modifier = Modifier.height(80.dp)) }
    }
}

@Composable
private fun DashboardCardContent(
    title: String,
    subtitle: String,
    icon: ImageVector,
    color: Color
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(HmifTheme.spacing.md)
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
                tint = color,
                modifier = Modifier.size(26.dp)
            )
        }
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        Icon(
            Icons.AutoMirrored.Filled.ArrowBack,
            contentDescription = "Go",
            modifier = Modifier.rotate(180f)
        )
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
