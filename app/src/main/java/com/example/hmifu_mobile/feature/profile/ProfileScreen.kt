package com.example.hmifu_mobile.feature.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.rounded.CardMembership
import androidx.compose.material.icons.rounded.ChevronRight
import androidx.compose.material.icons.rounded.Event
import androidx.compose.material.icons.rounded.Poll
import androidx.compose.material.icons.rounded.QrCode
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.graphics.asImageBitmap
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.example.hmifu_mobile.data.repository.UserProfile
import com.example.hmifu_mobile.ui.components.GlassmorphicCard
import com.example.hmifu_mobile.ui.components.ProfileAvatarImage
import com.example.hmifu_mobile.ui.components.ProfileSkeleton
import com.example.hmifu_mobile.ui.components.SecondaryButton
import com.example.hmifu_mobile.ui.components.StaggeredAnimatedItem
import com.example.hmifu_mobile.ui.theme.Error
import com.example.hmifu_mobile.ui.theme.GradientEnd
import com.example.hmifu_mobile.ui.theme.GradientStart
import com.example.hmifu_mobile.ui.theme.HmifBlue
import com.example.hmifu_mobile.ui.theme.HmifOrange
import com.example.hmifu_mobile.ui.theme.HmifPurple
import com.example.hmifu_mobile.ui.theme.HmifTheme

/**
 * Profile Screen - Premium 2025 Design
 *
 * Features:
 * - Gradient header with avatar
 * - Glassmorphic profile card
 * - Action menu with icons
 * - Profile info section
 * - Staggered animations
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    onEditProfile: () -> Unit = {},
    onSettings: () -> Unit = {},
    onMemberCard: () -> Unit = {},
    onMyEvents: () -> Unit = {},
    onCertificates: () -> Unit = {},
    onPolls: () -> Unit = {},
    onResources: () -> Unit = {},
    onLogout: () -> Unit,
    viewModel: ProfileViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    // Handle logout
    LaunchedEffect(uiState.isLoggedOut) {
        if (uiState.isLoggedOut) {
            onLogout()
        }
    }

    // Show messages
    LaunchedEffect(uiState.errorMessage, uiState.successMessage) {
        uiState.errorMessage?.let {
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
                    Text(
                        text = "Profile",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                },
                actions = {
                    IconButton(onClick = onSettings) {
                        Icon(
                            imageVector = Icons.Default.Settings,
                            contentDescription = "Settings"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent
                )
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        when {
            uiState.isLoading && uiState.profile == null -> {
                ProfileSkeleton(modifier = Modifier.padding(padding))
            }

            uiState.profile != null -> {
                ProfileContent(
                    profile = uiState.profile!!,
                    onEditProfile = onEditProfile,
                    onMemberCard = onMemberCard,
                    onMyEvents = onMyEvents,
                    onCertificates = onCertificates,
                    onPolls = onPolls,
                    onResources = onResources,
                    onLogout = viewModel::logout,
                    modifier = Modifier.padding(padding)
                )
            }

            else -> {
                EmptyState()
            }
        }
    }
}

// ════════════════════════════════════════════════════════════════
// PROFILE CONTENT
// ════════════════════════════════════════════════════════════════

@Composable
private fun ProfileContent(
    profile: UserProfile,
    onEditProfile: () -> Unit,
    onMemberCard: () -> Unit,
    onMyEvents: () -> Unit,
    onCertificates: () -> Unit,
    onPolls: () -> Unit,
    onResources: () -> Unit,
    onLogout: () -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(HmifTheme.spacing.lg),
        verticalArrangement = Arrangement.spacedBy(HmifTheme.spacing.lg)
    ) {
        // Profile Header Card
        item {
            StaggeredAnimatedItem(index = 0) {
                ProfileHeaderCard(profile = profile, onEditProfile = onEditProfile)
            }
        }

        // Quick Actions
        item {
            StaggeredAnimatedItem(index = 1) {
                QuickActionsSection(
                    onMemberCard = onMemberCard,
                    onMyEvents = onMyEvents,
                    onCertificates = onCertificates,
                    onPolls = onPolls,
                    onResources = onResources
                )
            }
        }

        // Profile Info Card
        item {
            StaggeredAnimatedItem(index = 2) {
                ProfileInfoCard(profile = profile)
            }
        }

        // Logout Section
        item {
            StaggeredAnimatedItem(index = 3) {
                LogoutSection(onLogout = onLogout)
            }
        }

        // Bottom spacing
        item {
            Spacer(modifier = Modifier.height(HmifTheme.spacing.huge))
        }
    }
}

// ════════════════════════════════════════════════════════════════
// PROFILE HEADER CARD
// ════════════════════════════════════════════════════════════════

@Composable
private fun ProfileHeaderCard(
    profile: UserProfile,
    onEditProfile: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(HmifTheme.cornerRadius.xxl))
            .background(
                Brush.linearGradient(
                    colors = listOf(GradientStart, GradientEnd)
                )
            )
            .padding(HmifTheme.spacing.xl)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Avatar
            Box(
                modifier = Modifier
                    .size(100.dp)
                    .clip(CircleShape)
                    .background(Color.White.copy(alpha = 0.2f))
                    .border(3.dp, Color.White.copy(alpha = 0.5f), CircleShape)
            ) {
                if (profile.photoBlob != null) {
                    val bitmap = remember(profile.photoBlob) {
                        com.example.hmifu_mobile.util.ImageUtils.bytesToBitmap(profile.photoBlob)
                    }
                    if (bitmap != null) {
                        androidx.compose.foundation.Image(
                            bitmap = bitmap.asImageBitmap(),
                            contentDescription = "Profile Picture",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = androidx.compose.ui.layout.ContentScale.Crop
                        )
                    } else {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Person,
                                contentDescription = null,
                                modifier = Modifier.size(48.dp),
                                tint = Color.White
                            )
                        }
                    }
                } else {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = null,
                            modifier = Modifier.size(48.dp),
                            tint = Color.White
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(HmifTheme.spacing.lg))

            // Name
            Text(
                text = profile.name.ifBlank { "No Name" },
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )

            // Email
            Text(
                text = profile.email,
                style = MaterialTheme.typography.bodyMedium,
                color = Color.White.copy(alpha = 0.8f)
            )

            // Role badge
            if (profile.role != "member") {
                Spacer(modifier = Modifier.height(HmifTheme.spacing.sm))
                RoleBadge(role = profile.role)
            }

            Spacer(modifier = Modifier.height(HmifTheme.spacing.lg))

            // Edit button
            SecondaryButton(
                text = "✏️ Edit Profile",
                onClick = onEditProfile
            )
        }
    }
}

@Composable
private fun RoleBadge(role: String) {
    val (bgColor, emoji) = when (role.lowercase()) {
        "admin" -> HmifOrange to "👑"
        "moderator" -> HmifPurple to "⚡"
        "staff" -> HmifBlue to "🎯"
        else -> Color.White.copy(alpha = 0.2f) to "👤"
    }

    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(HmifTheme.cornerRadius.sm))
            .background(bgColor.copy(alpha = 0.3f))
            .padding(horizontal = HmifTheme.spacing.md, vertical = HmifTheme.spacing.xs)
    ) {
        Text(
            text = "$emoji ${role.replaceFirstChar { it.uppercase() }}",
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )
    }
}

// ════════════════════════════════════════════════════════════════
// QUICK ACTIONS
// ════════════════════════════════════════════════════════════════

@Composable
private fun QuickActionsSection(
    onMemberCard: () -> Unit,
    onMyEvents: () -> Unit,
    onCertificates: () -> Unit,
    onPolls: () -> Unit,
    onResources: () -> Unit
) {
    GlassmorphicCard(
        modifier = Modifier.fillMaxWidth(),
        cornerRadius = HmifTheme.cornerRadius.lg
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(0.dp)) {
            ActionMenuItem(
                icon = Icons.Rounded.QrCode,
                title = "My Member Card",
                subtitle = "Show your digital ID",
                color = HmifBlue,
                onClick = onMemberCard
            )

            ActionMenuItem(
                icon = Icons.Rounded.Event,
                title = "My Events",
                subtitle = "Upcoming & Past Events",
                color = HmifPurple,
                onClick = onMyEvents
            )

            ActionMenuItem(
                icon = Icons.Rounded.CardMembership,
                title = "My Certificates",
                subtitle = "View earned certificates",
                color = HmifOrange,
                onClick = onCertificates
            )

            ActionMenuItem(
                icon = Icons.Rounded.Poll,
                title = "Polls & Voting",
                subtitle = "Participate in organization polls",
                color = HmifPurple,
                onClick = onPolls
            )

            ActionMenuItem(
                icon = Icons.Rounded.School,
                title = "Bank Soal",
                subtitle = "Academic resources",
                color = GradientEnd,
                onClick = onResources,
                showDivider = false
            )
        }
    }
}

@Composable
private fun ActionMenuItem(
    icon: ImageVector,
    title: String,
    subtitle: String,
    color: Color,
    onClick: () -> Unit,
    showDivider: Boolean = true
) {
    Column {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(HmifTheme.cornerRadius.md))
                .background(Color.Transparent)
                .padding(HmifTheme.spacing.md),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(HmifTheme.spacing.md),
                modifier = Modifier.weight(1f)
            ) {
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(RoundedCornerShape(HmifTheme.cornerRadius.md))
                        .background(color.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = color,
                        modifier = Modifier.size(24.dp)
                    )
                }

                Column {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Medium
                    )
                    Text(
                        text = subtitle,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            IconButton(onClick = onClick) {
                Icon(
                    imageVector = Icons.Rounded.ChevronRight,
                    contentDescription = "Go to $title",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        if (showDivider) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 68.dp)
                    .height(1.dp)
                    .background(MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
            )
        }
    }
}

// ════════════════════════════════════════════════════════════════
// PROFILE INFO CARD
// ════════════════════════════════════════════════════════════════

@Composable
private fun ProfileInfoCard(profile: UserProfile) {
    GlassmorphicCard(
        modifier = Modifier.fillMaxWidth(),
        cornerRadius = HmifTheme.cornerRadius.lg
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(HmifTheme.spacing.md)) {
            Text(
                text = "Profile Information",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold
            )

            ProfileInfoRow(label = "NIM", value = profile.nim.ifBlank { "-" })
            ProfileInfoRow(label = "Angkatan", value = profile.angkatan.ifBlank { "-" })
            ProfileInfoRow(label = "Concentration", value = profile.concentration.ifBlank { "-" })
            ProfileInfoRow(label = "Tech Stack", value = profile.techStack.ifBlank { "-" })
        }
    }
}

@Composable
private fun ProfileInfoRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Medium
        )
    }
}

// ════════════════════════════════════════════════════════════════
// LOGOUT SECTION
// ════════════════════════════════════════════════════════════════

@Composable
private fun LogoutSection(onLogout: () -> Unit) {
    GlassmorphicCard(
        modifier = Modifier.fillMaxWidth(),
        cornerRadius = HmifTheme.cornerRadius.lg,
        onClick = onLogout
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.Logout,
                contentDescription = null,
                tint = Error,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(HmifTheme.spacing.sm))
            Text(
                text = "Logout",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Medium,
                color = Error
            )
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
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "😕",
                style = MaterialTheme.typography.displayMedium
            )
            Spacer(modifier = Modifier.height(HmifTheme.spacing.md))
            Text(
                text = "Failed to load profile",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
