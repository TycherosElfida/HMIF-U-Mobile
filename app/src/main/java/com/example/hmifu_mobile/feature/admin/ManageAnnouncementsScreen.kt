package com.example.hmifu_mobile.feature.admin

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.example.hmifu_mobile.data.local.entity.AnnouncementEntity
import com.example.hmifu_mobile.feature.home.HomeViewModel // Reusing HomeViewModel since it loads announcements or create new one
import com.example.hmifu_mobile.ui.components.GlassmorphicCard
import com.example.hmifu_mobile.ui.components.StaggeredAnimatedItem
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ManageAnnouncementsScreen(
    onNavigateBack: () -> Unit = {},
    onEditAnnouncement: (String) -> Unit = {},
    onCreateAnnouncement: () -> Unit = {},
    viewModel: AdminViewModel = hiltViewModel() 
) {
    // using AdminViewModel for data as it likely has recent but we ideally want all.
    // Or simpler: Reuse AdminViewModel but ensure it loads all?
    // Actually better to have dedicated ManageAnnouncementsViewModel or use HomeViewModel which has announcements.
    // Let's use AdminViewModel for now and rely on its list, or create a simple one.
    
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Manage Announcements", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onCreateAnnouncement,
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Icon(Icons.Default.Add, "Create Announcement")
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
            itemsIndexed(uiState.recentAnnouncements) { index, announcement ->
                StaggeredAnimatedItem(index = index) {
                    ManageAnnouncementItem(
                        announcement = announcement,
                        onEdit = { onEditAnnouncement(announcement.id) }
                    )
                }
            }
        }
    }
}

@Composable
fun ManageAnnouncementItem(
    announcement: AnnouncementEntity,
    onEdit: () -> Unit
) {
    GlassmorphicCard(
        modifier = Modifier.fillMaxWidth(),
        onClick = onEdit
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = announcement.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = announcement.body,
                    style = MaterialTheme.typography.bodySmall,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            
            IconButton(onClick = onEdit) {
                Icon(Icons.Default.Edit, "Edit", tint = MaterialTheme.colorScheme.primary)
            }
        }
    }
}
