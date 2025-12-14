package com.example.hmifu_mobile.feature.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.runtime.remember
import androidx.hilt.navigation.compose.hiltViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import android.graphics.BitmapFactory
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.height
import com.example.hmifu_mobile.data.local.entity.AnnouncementEntity
import com.example.hmifu_mobile.data.local.entity.EventEntity
import com.example.hmifu_mobile.ui.components.EventCard
import com.example.hmifu_mobile.ui.components.GlassmorphicCard
import com.example.hmifu_mobile.ui.theme.HmifTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoryFeedScreen(
    onNavigateBack: () -> Unit,
    onEventClick: (String) -> Unit,
    onAnnouncementClick: (String) -> Unit = {},
    viewModel: CategoryFeedViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Text(
                        text = uiState.categoryName,
                        fontWeight = FontWeight.Bold
                    ) 
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
        if (uiState.announcements.isEmpty() && uiState.events.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize().padding(padding),
                contentAlignment = Alignment.Center
            ) {
                Text("No items found in this category")
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentPadding = PaddingValues(HmifTheme.spacing.lg),
                verticalArrangement = Arrangement.spacedBy(HmifTheme.spacing.lg)
            ) {
                if (uiState.events.isNotEmpty()) {
                    item {
                         Text(
                            text = "Upcoming Events",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    items(uiState.events) { event ->
                        // Using a simple event card adaptation
                        // Reusing generic EventCard logic if possible or simplified
                         EventCategoryCard(event, onClick = { onEventClick(event.id) })
                    }
                }

                if (uiState.announcements.isNotEmpty()) {
                    item {
                        Text(
                            text = "Announcements",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    items(uiState.announcements) { announcement ->
                        AnnouncementCategoryCard(announcement)
                    }
                }
            }
        }
    }
}

@Composable
fun EventCategoryCard(event: EventEntity, onClick: () -> Unit) {
    val dateString = remember(event.startTime) {
        val sdf = SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.getDefault())
        sdf.format(Date(event.startTime))
    }
    
    val imageBitmap = remember(event.imageBlob) {
        event.imageBlob?.let {
            BitmapFactory.decodeByteArray(it, 0, it.size)?.asImageBitmap()
        }
    }

    com.example.hmifu_mobile.ui.components.GlassmorphicCard(
        modifier = Modifier.fillMaxWidth(),
        onClick = onClick
    ) {
        Column {
            if (imageBitmap != null) {
                Image(
                    bitmap = imageBitmap,
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(150.dp),
                    contentScale = androidx.compose.ui.layout.ContentScale.Crop
                )
            }
            
            Column(modifier = Modifier.padding(8.dp)) {
                Text(event.title, style = MaterialTheme.typography.titleMedium)
                Text(dateString, style = MaterialTheme.typography.bodySmall)
            }
        }
    }
}

@Composable
fun AnnouncementCategoryCard(announcement: AnnouncementEntity) {
    val imageBitmap = remember(announcement.imageBlob) {
        announcement.imageBlob?.let {
            BitmapFactory.decodeByteArray(it, 0, it.size)?.asImageBitmap()
        }
    }

     com.example.hmifu_mobile.ui.components.GlassmorphicCard(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column {
            if (imageBitmap != null) {
                Image(
                    bitmap = imageBitmap,
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(150.dp),
                    contentScale = androidx.compose.ui.layout.ContentScale.Crop
                )
            }
            
            Column(modifier = Modifier.padding(8.dp)) {
                 Text(announcement.title, style = MaterialTheme.typography.titleMedium)
                 Text(announcement.body, style = MaterialTheme.typography.bodySmall, maxLines = 2)
            }
        }
    }
}
