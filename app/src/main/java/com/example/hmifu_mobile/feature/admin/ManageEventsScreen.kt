package com.example.hmifu_mobile.feature.admin

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.example.hmifu_mobile.data.local.entity.EventEntity
import com.example.hmifu_mobile.feature.events.EventsViewModel
import com.example.hmifu_mobile.ui.components.GlassmorphicCard
import com.example.hmifu_mobile.ui.components.StaggeredAnimatedItem
import com.example.hmifu_mobile.ui.theme.HmifTheme
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ManageEventsScreen(
    onNavigateBack: () -> Unit = {},
    onEditEvent: (String) -> Unit = {},
    onCreateEvent: () -> Unit = {},
    viewModel: EventsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    // Use shared EventsViewModel but we might need a dedicated one if we implement delete here
    // For now assuming we can add delete to EventsViewModel or just view/edit.
    
    // Note: EventsViewModel currently doesn't have delete. We might need to add it or create ManageEventsViewModel.
    // Given the scope, let's stick to using EventsViewModel and maybe adding delete later if needed,
    // or just let user edit. But "Manage" usually implies Delete.
    
    // Let's create a dedicated ManageEventsViewModel later if needed, but for now reuse or simpler:
    // Actually, AdminViewModel has delete logic or CreateEventViewModel?
    // Let's create a simple screen first.

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Manage Events", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onCreateEvent,
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Icon(Icons.Default.Add, "Create Event")
            }
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            itemsIndexed(uiState.events) { index, event ->
                StaggeredAnimatedItem(index = index) {
                    ManageEventItem(
                        event = event,
                        onEdit = { onEditEvent(event.id) },
                        onDelete = { /* TODO */ }
                    )
                }
            }
        }
    }
}

@Composable
fun ManageEventItem(
    event: EventEntity,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    GlassmorphicCard(
        modifier = Modifier.fillMaxWidth(),
        onClick = onEdit // Default click to edit
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = event.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(Date(event.startTime)),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            
            // Actions
            Row {
                IconButton(onClick = onEdit) {
                    Icon(Icons.Default.Edit, "Edit", tint = MaterialTheme.colorScheme.primary)
                }
                // Delete button could be here
            }
        }
    }
}
