package com.example.hmifu_mobile.feature.polls

import androidx.compose.animation.animateContentSize
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
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.rounded.HowToVote
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.example.hmifu_mobile.data.local.entity.PollEntity
import com.example.hmifu_mobile.ui.components.GlassmorphicCard
import com.example.hmifu_mobile.ui.components.StaggeredAnimatedItem
import com.example.hmifu_mobile.ui.theme.GradientEnd
import com.example.hmifu_mobile.ui.theme.HmifBlue
import com.example.hmifu_mobile.ui.theme.HmifOrange
import com.example.hmifu_mobile.ui.theme.HmifPurple
import com.example.hmifu_mobile.ui.theme.HmifTheme
import com.example.hmifu_mobile.ui.theme.Success
import org.json.JSONArray

/**
 * Polls Screen - Premium 2025 Design
 *
 * Features:
 * - Glassmorphic poll cards
 * - Animated vote progress bars
 * - Color-coded selections
 * - Staggered animations
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PollsScreen(
    onNavigateBack: () -> Unit,
    viewModel: PollViewModel = hiltViewModel()
) {
    val polls by viewModel.polls.collectAsState()
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(uiState.error, uiState.successMessage) {
        uiState.error?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearMessages()
        }
        uiState.successMessage?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearMessages()
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
                            imageVector = Icons.Rounded.HowToVote,
                            contentDescription = null,
                            tint = HmifPurple
                        )
                        Text(
                            text = "Polls & Voting",
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
        if (polls.isEmpty()) {
            EmptyState(modifier = Modifier.padding(padding))
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentPadding = PaddingValues(HmifTheme.spacing.lg),
                verticalArrangement = Arrangement.spacedBy(HmifTheme.spacing.lg)
            ) {
                itemsIndexed(polls, key = { _, poll -> poll.id }) { index, poll ->
                    StaggeredAnimatedItem(index = index) {
                        PollCard(
                            poll = poll,
                            isVoting = uiState.isVoting,
                            onVote = { optionId ->
                                viewModel.vote(poll.id, optionId)
                            }
                        )
                    }
                }

                // Bottom spacing
                item {
                    Spacer(modifier = Modifier.height(HmifTheme.spacing.huge))
                }
            }
        }
    }
}

// ════════════════════════════════════════════════════════════════
// POLL CARD
// ════════════════════════════════════════════════════════════════

@Composable
private fun PollCard(
    poll: PollEntity,
    isVoting: Boolean,
    onVote: (String) -> Unit
) {
    val options = remember(poll.options) {
        try {
            val jsonArray = JSONArray(poll.options)
            (0 until jsonArray.length()).map { i ->
                val obj = jsonArray.getJSONObject(i)
                PollOption(
                    id = obj.getString("id"),
                    text = obj.getString("text"),
                    votes = obj.getInt("votes")
                )
            }
        } catch (_: Exception) {
            emptyList()
        }
    }

    GlassmorphicCard(
        modifier = Modifier.fillMaxWidth(),
        cornerRadius = HmifTheme.cornerRadius.lg
    ) {
        Column(
            modifier = Modifier.animateContentSize(),
            verticalArrangement = Arrangement.spacedBy(HmifTheme.spacing.sm)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = poll.title,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    if (poll.description.isNotBlank()) {
                        Text(
                            text = poll.description,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                // Status badge
                val isActive = poll.userVotedOptionId == null
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(HmifTheme.cornerRadius.sm))
                        .background(
                            if (isActive) HmifBlue.copy(alpha = 0.15f)
                            else Success.copy(alpha = 0.15f)
                        )
                        .padding(horizontal = HmifTheme.spacing.sm, vertical = HmifTheme.spacing.xs)
                ) {
                    Text(
                        text = if (isActive) "Vote Now" else "Voted ✓",
                        style = MaterialTheme.typography.labelSmall,
                        color = if (isActive) HmifBlue else Success,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            Spacer(modifier = Modifier.height(HmifTheme.spacing.xs))

            // Options
            options.forEachIndexed { index, option ->
                val percentage = if (poll.totalVotes > 0) {
                    option.votes.toFloat() / poll.totalVotes
                } else 0f

                val isSelected = poll.userVotedOptionId == option.id
                val optionColor = getOptionColor(index)

                PollOptionItem(
                    option = option,
                    percentage = percentage,
                    isSelected = isSelected,
                    hasVoted = poll.userVotedOptionId != null,
                    isVoting = isVoting,
                    color = optionColor,
                    onClick = { if (!isVoting) onVote(option.id) }
                )
            }

            // Vote count
            Text(
                text = "📊 ${poll.totalVotes} vote${if (poll.totalVotes != 1) "s" else ""}",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

private fun getOptionColor(index: Int): Color {
    return when (index % 4) {
        0 -> HmifBlue
        1 -> HmifOrange
        2 -> HmifPurple
        else -> GradientEnd
    }
}

// ════════════════════════════════════════════════════════════════
// POLL OPTION ITEM
// ════════════════════════════════════════════════════════════════

@Composable
private fun PollOptionItem(
    option: PollOption,
    percentage: Float,
    isSelected: Boolean,
    hasVoted: Boolean,
    isVoting: Boolean,
    color: Color,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(HmifTheme.cornerRadius.md))
            .background(MaterialTheme.colorScheme.surfaceContainerLow)
            .clickable(enabled = !hasVoted && !isVoting, onClick = onClick)
    ) {
        // Progress background
        if (hasVoted) {
            LinearProgressIndicator(
                progress = { percentage },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                color = if (isSelected) color.copy(alpha = 0.25f) else color.copy(alpha = 0.1f),
                trackColor = Color.Transparent
            )
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp)
                .padding(horizontal = HmifTheme.spacing.md),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(HmifTheme.spacing.sm)
            ) {
                if (isSelected) {
                    Icon(
                        Icons.Default.CheckCircle,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp),
                        tint = color
                    )
                }
                Text(
                    text = option.text,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                    color = if (isSelected) color else MaterialTheme.colorScheme.onSurface
                )
            }

            if (hasVoted) {
                Text(
                    text = "${(percentage * 100).toInt()}%",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = if (isSelected) color else MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

// ════════════════════════════════════════════════════════════════
// EMPTY STATE
// ════════════════════════════════════════════════════════════════

@Composable
private fun EmptyState(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(HmifTheme.spacing.md)
        ) {
            Text(
                text = "🗳️",
                style = MaterialTheme.typography.displayLarge
            )
            Text(
                text = "No active polls",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = "Check back later for new polls!",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.outline
            )
        }
    }
}

// ════════════════════════════════════════════════════════════════
// DATA CLASS
// ════════════════════════════════════════════════════════════════

private data class PollOption(
    val id: String,
    val text: String,
    val votes: Int
)
