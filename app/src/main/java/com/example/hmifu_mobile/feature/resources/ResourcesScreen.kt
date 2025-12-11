package com.example.hmifu_mobile.feature.resources

import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.rounded.Description
import androidx.compose.material.icons.rounded.School
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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.example.hmifu_mobile.data.local.entity.ResourceEntity
import com.example.hmifu_mobile.ui.components.FilterChip
import com.example.hmifu_mobile.ui.components.GlassmorphicCard
import com.example.hmifu_mobile.ui.components.StaggeredAnimatedItem
import com.example.hmifu_mobile.ui.theme.GradientEnd
import com.example.hmifu_mobile.ui.theme.HmifBlue
import com.example.hmifu_mobile.ui.theme.HmifOrange
import com.example.hmifu_mobile.ui.theme.HmifPurple
import com.example.hmifu_mobile.ui.theme.HmifTheme

/**
 * Resources Screen (Bank Soal) - Premium 2025 Design
 *
 * Features:
 * - Glassmorphic resource cards
 * - Animated filter chips
 * - File type icons
 * - Download action
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ResourcesScreen(
    onNavigateBack: () -> Unit,
    viewModel: ResourceViewModel = hiltViewModel()
) {
    val resources by viewModel.resources.collectAsState()
    val subjects by viewModel.subjects.collectAsState()
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val context = LocalContext.current

    LaunchedEffect(uiState.error) {
        uiState.error?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearError()
        }
    }

    val filteredResources = remember(resources, uiState.selectedSemester, uiState.selectedSubject) {
        resources.filter { resource ->
            val semesterMatch = uiState.selectedSemester?.let { resource.semester == it } ?: true
            val subjectMatch = uiState.selectedSubject?.let { resource.subject == it } ?: true
            semesterMatch && subjectMatch
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
                            imageVector = Icons.Rounded.School,
                            contentDescription = null,
                            tint = HmifBlue
                        )
                        Text(
                            text = "Bank Soal",
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
            // Semester filter chips
            LazyRow(
                contentPadding = PaddingValues(horizontal = HmifTheme.spacing.lg),
                horizontalArrangement = Arrangement.spacedBy(HmifTheme.spacing.sm)
            ) {
                item {
                    FilterChip(
                        text = "All",
                        selected = uiState.selectedSemester == null,
                        onClick = { viewModel.selectSemester(null) }
                    )
                }
                items(8) { index ->
                    val semester = index + 1
                    FilterChip(
                        text = "Sem $semester",
                        selected = uiState.selectedSemester == semester,
                        onClick = { viewModel.selectSemester(semester) },
                        color = getSemesterColor(semester)
                    )
                }
            }

            // Subject filter
            if (subjects.isNotEmpty()) {
                Spacer(modifier = Modifier.height(HmifTheme.spacing.sm))
                LazyRow(
                    contentPadding = PaddingValues(horizontal = HmifTheme.spacing.lg),
                    horizontalArrangement = Arrangement.spacedBy(HmifTheme.spacing.sm)
                ) {
                    item {
                        FilterChip(
                            text = "All Subjects",
                            selected = uiState.selectedSubject == null,
                            onClick = { viewModel.selectSubject(null) }
                        )
                    }
                    items(subjects) { subject ->
                        FilterChip(
                            text = subject,
                            selected = uiState.selectedSubject == subject,
                            onClick = { viewModel.selectSubject(subject) }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(HmifTheme.spacing.md))

            if (filteredResources.isEmpty()) {
                EmptyState()
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(HmifTheme.spacing.lg),
                    verticalArrangement = Arrangement.spacedBy(HmifTheme.spacing.md)
                ) {
                    itemsIndexed(filteredResources, key = { _, it -> it.id }) { index, resource ->
                        StaggeredAnimatedItem(index = index) {
                            ResourceCard(
                                resource = resource,
                                onDownload = {
                                    if (resource.fileUrl.isNotBlank()) {
                                        val intent =
                                            Intent(Intent.ACTION_VIEW, resource.fileUrl.toUri())
                                        context.startActivity(intent)
                                    }
                                }
                            )
                        }
                    }

                    item {
                        Spacer(modifier = Modifier.height(HmifTheme.spacing.huge))
                    }
                }
            }
        }
    }
}

private fun getSemesterColor(semester: Int): Color {
    return when (semester % 4) {
        1 -> HmifBlue
        2 -> HmifOrange
        3 -> HmifPurple
        else -> GradientEnd
    }
}

// ════════════════════════════════════════════════════════════════
// RESOURCE CARD
// ════════════════════════════════════════════════════════════════

@Composable
private fun ResourceCard(
    resource: ResourceEntity,
    onDownload: () -> Unit
) {
    val fileTypeColor = when (resource.type.lowercase()) {
        "pdf" -> HmifOrange
        "doc", "docx" -> HmifBlue
        "ppt", "pptx" -> HmifPurple
        else -> GradientEnd
    }

    GlassmorphicCard(
        modifier = Modifier.fillMaxWidth(),
        onClick = onDownload,
        cornerRadius = HmifTheme.cornerRadius.lg
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(HmifTheme.spacing.md)
        ) {
            // File icon
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(RoundedCornerShape(HmifTheme.cornerRadius.md))
                    .background(fileTypeColor.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Rounded.Description,
                    contentDescription = null,
                    modifier = Modifier.size(26.dp),
                    tint = fileTypeColor
                )
            }

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = resource.title,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "${resource.subject} • Semester ${resource.semester}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Row(
                    horizontalArrangement = Arrangement.spacedBy(HmifTheme.spacing.sm)
                ) {
                    Text(
                        text = resource.type.uppercase(),
                        style = MaterialTheme.typography.labelSmall,
                        color = fileTypeColor,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = "• ${formatFileSize(resource.fileSize)} • ${resource.year}",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.outline
                    )
                }
            }

            // Download button
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(HmifTheme.cornerRadius.sm))
                    .background(HmifBlue.copy(alpha = 0.15f))
                    .clickable(onClick = onDownload),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Download,
                    contentDescription = "Download",
                    tint = HmifBlue,
                    modifier = Modifier.size(22.dp)
                )
            }
        }
    }
}

// ════════════════════════════════════════════════════════════════
// EMPTY STATE
// ════════════════════════════════════════════════════════════════

@Composable
private fun EmptyState() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(HmifTheme.spacing.md)
        ) {
            Text(
                text = "📚",
                style = MaterialTheme.typography.displayLarge
            )
            Text(
                text = "No resources found",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = "Try adjusting your filters",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.outline
            )
        }
    }
}

// ════════════════════════════════════════════════════════════════
// UTILITIES
// ════════════════════════════════════════════════════════════════

private fun formatFileSize(bytes: Long): String {
    return when {
        bytes < 1024 -> "$bytes B"
        bytes < 1024 * 1024 -> "${bytes / 1024} KB"
        else -> "${bytes / (1024 * 1024)} MB"
    }
}
