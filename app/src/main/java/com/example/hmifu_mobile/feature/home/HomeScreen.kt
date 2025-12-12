package com.example.hmifu_mobile.feature.home

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PushPin
import androidx.compose.material.icons.rounded.ArrowForward
import androidx.compose.material.icons.rounded.Book
import androidx.compose.material.icons.rounded.EmojiEvents
import androidx.compose.material.icons.rounded.QrCodeScanner
import androidx.compose.material.icons.rounded.Work
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.hmifu_mobile.data.local.entity.AnnouncementCategory
import com.example.hmifu_mobile.data.local.entity.AnnouncementEntity
import com.example.hmifu_mobile.ui.components.CategoryChip
import com.example.hmifu_mobile.ui.components.FilterChip
import com.example.hmifu_mobile.ui.components.GlassmorphicCard
import com.example.hmifu_mobile.ui.components.HeroGlassmorphicCard
import com.example.hmifu_mobile.ui.components.PulsingElement
import com.example.hmifu_mobile.ui.components.QuickStatsRow
import com.example.hmifu_mobile.ui.components.SkeletonCard
import com.example.hmifu_mobile.ui.components.StaggeredAnimatedItem
import com.example.hmifu_mobile.ui.theme.GradientEnd
import com.example.hmifu_mobile.ui.theme.GradientStart
import com.example.hmifu_mobile.ui.theme.HmifBlue
import com.example.hmifu_mobile.ui.theme.HmifOrange
import com.example.hmifu_mobile.ui.theme.HmifPurple
import com.example.hmifu_mobile.ui.theme.HmifTheme
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

/**
 * Home Screen - Complete 2025 UI Overhaul
 *
 * Features:
 * - Personalized greeting section
 * - Digital membership card hero
 * - Quick stats row with animations
 * - Quick actions grid
 * - Announcements feed with categories
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onNotificationsClick: () -> Unit = {},
    onMemberCardClick: () -> Unit = {},
    onScanQrClick: () -> Unit = {},
    onResourcesClick: () -> Unit = {},
    onCompetitionsClick: () -> Unit = {},
    onCareersClick: () -> Unit = {},
    viewModel: HomeViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(uiState.errorMessage) {
        uiState.errorMessage?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearError()
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            HomeTopBar(
                userName = uiState.userName.ifBlank { "Member" },
                onNotificationsClick = onNotificationsClick,
                hasUnreadNotifications = uiState.hasUnreadNotifications
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = PaddingValues(
                horizontal = HmifTheme.spacing.lg,
                vertical = HmifTheme.spacing.md
            ),
            verticalArrangement = Arrangement.spacedBy(HmifTheme.spacing.xl)
        ) {
            // Digital Membership Card (Hero)
            item {
                StaggeredAnimatedItem(index = 0) {
                    DigitalMembershipCard(
                        memberName = uiState.userName.ifBlank { "Member Name" },
                        nim = uiState.userNim.ifBlank { "412022XXX" },
                        angkatan = uiState.userAngkatan.ifBlank { "2022" },
                        points = uiState.userPoints,
                        photoUrl = uiState.userPhotoUrl,
                        onClick = onMemberCardClick
                    )
                }
            }

            // Quick Stats Row
            item {
                StaggeredAnimatedItem(index = 1) {
                    QuickStatsRow(
                        points = uiState.userPoints,
                        events = 12, // TODO: Get from events repository
                        badges = 5 // TODO: Get from badges/achievements
                    )
                }
            }

            // Quick Actions Grid
            item {
                StaggeredAnimatedItem(index = 2) {
                    QuickActionsSection(
                        onScanQrClick = onScanQrClick,
                        onResourcesClick = onResourcesClick,
                        onCompetitionsClick = onCompetitionsClick,
                        onCareersClick = onCareersClick
                    )
                }
            }

            // Announcements Section Header
            item {
                StaggeredAnimatedItem(index = 3) {
                    SectionHeader(
                        title = "📢 Announcements",
                        subtitle = "Stay updated with HMIF"
                    )
                }
            }

            // Category Filter
            item {
                CategoryFilterRow(
                    selectedCategory = uiState.selectedCategory,
                    onCategorySelected = viewModel::selectCategory
                )
            }

            // Announcements Content
            when {
                uiState.isLoading && uiState.announcements.isEmpty() -> {
                    items(4) { index ->
                        StaggeredAnimatedItem(index = 4 + index) {
                            SkeletonCard(
                                modifier = Modifier.fillMaxWidth(),
                                height = 120.dp
                            )
                        }
                    }
                }

                uiState.announcements.isEmpty() -> {
                    item {
                        EmptyAnnouncementsState()
                    }
                }

                else -> {
                    itemsIndexed(
                        items = uiState.announcements,
                        key = { _, announcement -> announcement.id }
                    ) { index, announcement ->
                        StaggeredAnimatedItem(index = 4 + index) {
                            AnnouncementCard(announcement = announcement)
                        }
                    }
                }
            }

            // Bottom spacing
            item {
                Spacer(modifier = Modifier.height(HmifTheme.spacing.huge))
            }
        }
    }
}

// ════════════════════════════════════════════════════════════════
// TOP APP BAR
// ════════════════════════════════════════════════════════════════

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun HomeTopBar(
    userName: String,
    onNotificationsClick: () -> Unit,
    hasUnreadNotifications: Boolean
) {
    TopAppBar(
        title = {
            Column {
                Text(
                    text = getGreeting() + ", " + userName + " 👋",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Welcome back to HMIF",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        },
        actions = {
            IconButton(onClick = onNotificationsClick) {
                BadgedBox(
                    badge = {
                        if (hasUnreadNotifications) {
                            Badge { }
                        }
                    }
                ) {
                    Icon(
                        imageVector = Icons.Default.Notifications,
                        contentDescription = "Notifications"
                    )
                }
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = Color.Transparent
        )
    )
}

private fun getGreeting(): String {
    val hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
    return when {
        hour < 12 -> "Good morning"
        hour < 17 -> "Good afternoon"
        else -> "Good evening"
    }
}

// ════════════════════════════════════════════════════════════════
// DIGITAL MEMBERSHIP CARD
// ════════════════════════════════════════════════════════════════

@Composable
private fun DigitalMembershipCard(
    memberName: String,
    nim: String,
    angkatan: String,
    points: Int,
    photoUrl: String?,
    onClick: () -> Unit
) {
    HeroGlassmorphicCard(
        modifier = Modifier
            .fillMaxWidth()
            .height(HmifTheme.sizes.memberCardHeight),
        onClick = onClick
    ) {
        Row(
            modifier = Modifier.fillMaxSize(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Left side - Member info
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = "HMIF MEMBER",
                    style = MaterialTheme.typography.labelSmall,
                    color = Color.White.copy(alpha = 0.8f),
                    fontWeight = FontWeight.SemiBold
                )

                Spacer(modifier = Modifier.height(HmifTheme.spacing.sm))

                Text(
                    text = memberName,
                    style = MaterialTheme.typography.headlineSmall,
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Text(
                    text = nim,
                    style = MaterialTheme.typography.titleMedium,
                    color = Color.White.copy(alpha = 0.9f),
                    fontWeight = FontWeight.Medium
                )

                Spacer(modifier = Modifier.height(HmifTheme.spacing.lg))

                Row(
                    horizontalArrangement = Arrangement.spacedBy(HmifTheme.spacing.sm)
                ) {
                    MemberCardBadge(text = angkatan)
                    MemberCardBadge(text = "$points pts")
                }
            }

            // Right side - Profile Picture (replacing QR Code)
            Box(
                modifier = Modifier
                    .size(HmifTheme.sizes.qrCodeSize)
                    .clip(RoundedCornerShape(HmifTheme.cornerRadius.md))
                    .background(Color.White.copy(alpha = 0.2f))
                    .border(2.dp, Color.White.copy(alpha = 0.5f), RoundedCornerShape(HmifTheme.cornerRadius.md)),
                contentAlignment = Alignment.Center
            ) {
                if (!photoUrl.isNullOrBlank()) {
                    com.example.hmifu_mobile.ui.components.ImageKitImageWithAspectRatio(
                        path = photoUrl,
                        contentDescription = "Profile Picture",
                        modifier = Modifier.fillMaxSize(),
                        height = 100f,
                        aspectRatioWidth = 1,
                        aspectRatioHeight = 1
                    )
                } else {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = null,
                        modifier = Modifier.size(48.dp),
                        tint = Color.White
                    )
                }
            }
        }
    }
}

@Composable
private fun MemberCardBadge(text: String) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(HmifTheme.cornerRadius.sm))
            .background(Color.White.copy(alpha = 0.2f))
            .padding(horizontal = HmifTheme.spacing.sm, vertical = HmifTheme.spacing.xs)
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelSmall,
            color = Color.White,
            fontWeight = FontWeight.Medium
        )
    }
}

// ════════════════════════════════════════════════════════════════
// QUICK ACTIONS SECTION
// ════════════════════════════════════════════════════════════════

@Composable
private fun QuickActionsSection(
    onScanQrClick: () -> Unit,
    onResourcesClick: () -> Unit,
    onCompetitionsClick: () -> Unit,
    onCareersClick: () -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(HmifTheme.spacing.sm)) {
        Text(
            text = "Quick Actions",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(HmifTheme.spacing.sm)
        ) {
            QuickActionCard(
                icon = Icons.Rounded.QrCodeScanner,
                label = "Scan QR",
                color = HmifBlue,
                onClick = onScanQrClick,
                modifier = Modifier.weight(1f)
            )
            QuickActionCard(
                icon = Icons.Rounded.Book,
                label = "Resources",
                color = HmifPurple,
                onClick = onResourcesClick,
                modifier = Modifier.weight(1f)
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(HmifTheme.spacing.sm)
        ) {
            QuickActionCard(
                icon = Icons.Rounded.EmojiEvents,
                label = "Compete",
                color = HmifOrange,
                onClick = onCompetitionsClick,
                modifier = Modifier.weight(1f)
            )
            QuickActionCard(
                icon = Icons.Rounded.Work,
                label = "Careers",
                color = GradientEnd,
                onClick = onCareersClick,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun QuickActionCard(
    icon: ImageVector,
    label: String,
    color: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    GlassmorphicCard(
        modifier = modifier,
        onClick = onClick,
        cornerRadius = HmifTheme.cornerRadius.lg
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(HmifTheme.spacing.sm),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(color.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = label,
                    tint = color,
                    modifier = Modifier.size(24.dp)
                )
            }

            Text(
                text = label,
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

// ════════════════════════════════════════════════════════════════
// SECTION HEADER
// ════════════════════════════════════════════════════════════════

@Composable
private fun SectionHeader(
    title: String,
    subtitle: String? = null,
    onSeeAllClick: (() -> Unit)? = null
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )
            if (subtitle != null) {
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        if (onSeeAllClick != null) {
            Row(
                modifier = Modifier.clickable(onClick = onSeeAllClick),
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "See all",
                    style = MaterialTheme.typography.labelMedium,
                    color = HmifBlue
                )
                Icon(
                    imageVector = Icons.Rounded.ArrowForward,
                    contentDescription = null,
                    tint = HmifBlue,
                    modifier = Modifier.size(16.dp)
                )
            }
        }
    }
}

// ════════════════════════════════════════════════════════════════
// CATEGORY FILTER
// ════════════════════════════════════════════════════════════════

@Composable
private fun CategoryFilterRow(
    selectedCategory: AnnouncementCategory?,
    onCategorySelected: (AnnouncementCategory?) -> Unit
) {
    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(HmifTheme.spacing.sm)
    ) {
        item {
            FilterChip(
                text = "All",
                selected = selectedCategory == null,
                onClick = { onCategorySelected(null) }
            )
        }
        items(AnnouncementCategory.entries) { category ->
            FilterChip(
                text = category.displayName,
                selected = selectedCategory == category,
                onClick = { onCategorySelected(category) }
            )
        }
    }
}

// ════════════════════════════════════════════════════════════════
// ANNOUNCEMENT CARD
// ════════════════════════════════════════════════════════════════

@Composable
private fun AnnouncementCard(announcement: AnnouncementEntity) {
    GlassmorphicCard(
        modifier = Modifier.fillMaxWidth(),
        cornerRadius = HmifTheme.cornerRadius.lg
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(HmifTheme.spacing.sm)) {
            // Header row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                CategoryChip(category = announcement.category)

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    if (announcement.isPinned) {
                        Icon(
                            imageVector = Icons.Default.PushPin,
                            contentDescription = "Pinned",
                            modifier = Modifier.size(14.dp),
                            tint = HmifOrange
                        )
                    }
                    Text(
                        text = formatTimeAgo(announcement.createdAt),
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            // Title
            Text(
                text = announcement.title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            // Body preview
            Text(
                text = announcement.body,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            if (announcement.authorName.isNotBlank()) {
                Text(
                    text = "By ${announcement.authorName}",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.outline
                )
            }
        }
    }
}

// ════════════════════════════════════════════════════════════════
// EMPTY STATE
// ════════════════════════════════════════════════════════════════

@Composable
private fun EmptyAnnouncementsState() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = HmifTheme.spacing.xxxl),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "📭",
                style = MaterialTheme.typography.displayMedium
            )
            Spacer(modifier = Modifier.height(HmifTheme.spacing.md))
            Text(
                text = "No announcements yet",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = "Check back later for updates",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.outline
            )
        }
    }
}

// ════════════════════════════════════════════════════════════════
// UTILITIES
// ════════════════════════════════════════════════════════════════

private fun formatTimeAgo(timestamp: Long): String {
    val now = System.currentTimeMillis()
    val diff = now - timestamp

    val seconds = diff / 1000
    val minutes = seconds / 60
    val hours = minutes / 60
    val days = hours / 24

    return when {
        days > 7 -> {
            val sdf = SimpleDateFormat("dd MMM", Locale.getDefault())
            sdf.format(Date(timestamp))
        }

        days > 0 -> "${days}d ago"
        hours > 0 -> "${hours}h ago"
        minutes > 0 -> "${minutes}m ago"
        else -> "Just now"
    }
}
