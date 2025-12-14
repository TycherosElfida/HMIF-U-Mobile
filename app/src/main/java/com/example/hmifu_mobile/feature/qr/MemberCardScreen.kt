package com.example.hmifu_mobile.feature.qr

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.rounded.Info
import androidx.compose.material.icons.rounded.Refresh
import androidx.compose.material3.CircularProgressIndicator
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
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.hmifu_mobile.R
import com.example.hmifu_mobile.ui.components.GlassmorphicCard
import com.example.hmifu_mobile.ui.components.SkeletonCard
import com.example.hmifu_mobile.ui.theme.CodeLarge
import com.example.hmifu_mobile.ui.theme.GradientEnd
import com.example.hmifu_mobile.ui.theme.GradientStart
import com.example.hmifu_mobile.ui.theme.HmifBlue
import com.example.hmifu_mobile.ui.theme.HmifOrange
import com.example.hmifu_mobile.ui.theme.HmifPurple
import com.example.hmifu_mobile.ui.theme.HmifTheme
import com.example.hmifu_mobile.ui.theme.Success

/**
 * Member Card Screen - Premium Digital ID Experience
 *
 * Features:
 * - Full-screen glassmorphic card design
 * - Animated pulsing QR code
 * - Live countdown timer with color transitions
 * - Holographic shimmer effect
 * - Quick info section
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MemberCardScreen(
    onNavigateBack: () -> Unit = {},
    viewModel: MemberCardViewModel = hiltViewModel()
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
            TopAppBar(
                title = {
                    Text(
                        text = "Digital ID",
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
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent
                )
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        // Gradient background
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            GradientStart.copy(alpha = 0.15f),
                            MaterialTheme.colorScheme.background,
                            GradientEnd.copy(alpha = 0.1f)
                        )
                    )
                )
                .padding(padding)
        ) {
            if (uiState.isLoading) {
                LoadingState()
            } else {
                MemberCardContent(uiState = uiState)
            }
        }
    }
}

@Composable
private fun LoadingState() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(HmifTheme.spacing.lg),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(HmifTheme.spacing.lg)
    ) {
        SkeletonCard(
            modifier = Modifier.fillMaxWidth(),
            height = 450.dp
        )
        SkeletonCard(
            modifier = Modifier.fillMaxWidth(),
            height = 120.dp
        )
    }
}

@Composable
private fun MemberCardContent(uiState: MemberCardUiState) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(HmifTheme.spacing.lg),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(HmifTheme.spacing.xl)
    ) {
        // Premium Member Card
        PremiumMemberCard(uiState = uiState)

        // Instructions Card
        InstructionsCard()

        // Bottom spacing
        Spacer(modifier = Modifier.height(HmifTheme.spacing.huge))
    }
}

// ════════════════════════════════════════════════════════════════
// PREMIUM MEMBER CARD
// ════════════════════════════════════════════════════════════════

@Composable
private fun PremiumMemberCard(uiState: MemberCardUiState) {
    // Holographic shimmer animation
    val infiniteTransition = rememberInfiniteTransition(label = "holographic")
    val shimmerOffset by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(3000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "shimmer_offset"
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(
                elevation = 16.dp,
                shape = RoundedCornerShape(HmifTheme.cornerRadius.xxl),
                ambientColor = GradientStart.copy(alpha = 0.3f),
                spotColor = GradientStart.copy(alpha = 0.3f)
            )
            .clip(RoundedCornerShape(HmifTheme.cornerRadius.xxl))
            .background(
                Brush.linearGradient(
                    colors = listOf(GradientStart, GradientEnd)
                )
            )
            .border(
                width = 1.dp,
                brush = Brush.linearGradient(
                    colors = listOf(
                        Color.White.copy(alpha = 0.3f),
                        Color.White.copy(alpha = 0.1f),
                        Color.White.copy(alpha = 0.3f)
                    )
                ),
                shape = RoundedCornerShape(HmifTheme.cornerRadius.xxl)
            )
    ) {
        // Holographic overlay
        Box(
            modifier = Modifier
                .matchParentSize()
                .background(
                    Brush.linearGradient(
                        colors = listOf(
                            Color.White.copy(alpha = 0f),
                            Color.White.copy(alpha = shimmerOffset * 0.2f),
                            Color.White.copy(alpha = 0f)
                        ),
                        start = androidx.compose.ui.geometry.Offset(
                            x = shimmerOffset * 1000 - 500,
                            y = 0f
                        ),
                        end = androidx.compose.ui.geometry.Offset(
                            x = shimmerOffset * 1000,
                            y = 600f
                        )
                    )
                )
        )

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(HmifTheme.spacing.xl),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "HMIF UKRIDA",
                        style = MaterialTheme.typography.labelSmall,
                        color = Color.White.copy(alpha = 0.8f),
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = "Digital Member ID",
                        style = MaterialTheme.typography.titleMedium,
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                }

                // Logo
                Image(
                    painter = painterResource(id = R.drawable.logo_hmif2),
                    contentDescription = "HMIF Logo",
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(Color.White)
                        .padding(4.dp),
                    contentScale = ContentScale.Fit
                )
            }

            Spacer(modifier = Modifier.height(HmifTheme.spacing.xxl))

            // User Info
            Text(
                text = uiState.userName,
                style = MaterialTheme.typography.headlineMedium,
                color = Color.White,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )

            Text(
                text = uiState.userNim,
                style = CodeLarge,
                color = Color.White.copy(alpha = 0.9f)
            )

            Spacer(modifier = Modifier.height(HmifTheme.spacing.sm))

            // Info badges
            Row(
                horizontalArrangement = Arrangement.spacedBy(HmifTheme.spacing.sm)
            ) {
                InfoBadge(text = "Angkatan ${uiState.userAngkatan}")
                if (uiState.userRole != "member") {
                    RoleBadge(role = uiState.userRole)
                }
            }

            Spacer(modifier = Modifier.height(HmifTheme.spacing.xxl))

            // QR Code Section
            AnimatedQrCode(
                bitmap = uiState.qrBitmap,
                timeRemaining = uiState.timeRemaining,
                code = uiState.currentCode
            )
        }
    }
}

@Composable
private fun InfoBadge(text: String) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(HmifTheme.cornerRadius.sm))
            .background(Color.White.copy(alpha = 0.2f))
            .padding(horizontal = HmifTheme.spacing.md, vertical = HmifTheme.spacing.xs)
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelSmall,
            color = Color.White,
            fontWeight = FontWeight.Medium
        )
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
// ANIMATED QR CODE
// ════════════════════════════════════════════════════════════════

@Composable
private fun AnimatedQrCode(
    bitmap: android.graphics.Bitmap?,
    timeRemaining: Int,
    code: String
) {
    val progress = timeRemaining / 30f
    val animatedProgress by animateFloatAsState(
        targetValue = progress,
        animationSpec = tween(durationMillis = 900),
        label = "qr_progress"
    )

    // Pulsing animation when time is low
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = if (timeRemaining <= 5) 1.02f else 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(500),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse_scale"
    )

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(HmifTheme.spacing.md)
    ) {
        // QR Code Container with glow
        Box(
            modifier = Modifier
                .scale(pulseScale)
                .shadow(
                    elevation = 12.dp,
                    shape = RoundedCornerShape(HmifTheme.cornerRadius.lg),
                    ambientColor = Color.White.copy(alpha = 0.3f),
                    spotColor = Color.White.copy(alpha = 0.3f)
                )
                .size(180.dp)
                .clip(RoundedCornerShape(HmifTheme.cornerRadius.lg))
                .background(Color.White)
                .padding(12.dp),
            contentAlignment = Alignment.Center
        ) {
            if (bitmap != null) {
                Image(
                    bitmap = bitmap.asImageBitmap(),
                    contentDescription = "QR Code for check-in",
                    modifier = Modifier.fillMaxSize()
                )
            } else {
                CircularProgressIndicator(color = GradientStart)
            }
        }

        // Timer Section
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(HmifTheme.spacing.sm)
        ) {
            // Progress bar
            LinearProgressIndicator(
                progress = { animatedProgress },
                modifier = Modifier
                    .fillMaxWidth(0.7f)
                    .height(6.dp)
                    .clip(RoundedCornerShape(3.dp)),
                color = when {
                    timeRemaining <= 5 -> MaterialTheme.colorScheme.error
                    timeRemaining <= 10 -> HmifOrange
                    else -> Success
                },
                trackColor = Color.White.copy(alpha = 0.2f)
            )

            // Timer info
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(HmifTheme.spacing.sm)
            ) {
                Icon(
                    imageVector = Icons.Rounded.Refresh,
                    contentDescription = null,
                    tint = Color.White.copy(alpha = 0.7f),
                    modifier = Modifier.size(14.dp)
                )
                Text(
                    text = "Refreshes in ${timeRemaining}s",
                    style = MaterialTheme.typography.labelSmall,
                    color = Color.White.copy(alpha = 0.8f)
                )
            }

            // Verification code
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(HmifTheme.cornerRadius.sm))
                    .background(Color.White.copy(alpha = 0.15f))
                    .padding(horizontal = HmifTheme.spacing.md, vertical = HmifTheme.spacing.xs)
            ) {
                Text(
                    text = "Code: $code",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
        }
    }
}

// ════════════════════════════════════════════════════════════════
// INSTRUCTIONS CARD
// ════════════════════════════════════════════════════════════════

@Composable
private fun InstructionsCard() {
    GlassmorphicCard(
        modifier = Modifier.fillMaxWidth(),
        cornerRadius = HmifTheme.cornerRadius.lg
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(HmifTheme.spacing.md)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(HmifTheme.spacing.sm)
            ) {
                Icon(
                    imageVector = Icons.Rounded.Info,
                    contentDescription = null,
                    tint = HmifBlue
                )
                Text(
                    text = "How to Check-in",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold
                )
            }

            InstructionItem(number = "1", text = "Show this QR code to the event admin")
            InstructionItem(number = "2", text = "QR refreshes every 30 seconds for security")
            InstructionItem(number = "3", text = "Keep screen visible during check-in")
        }
    }
}

@Composable
private fun InstructionItem(number: String, text: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(HmifTheme.spacing.sm)
    ) {
        Box(
            modifier = Modifier
                .size(24.dp)
                .clip(CircleShape)
                .background(HmifBlue.copy(alpha = 0.15f)),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = number,
                style = MaterialTheme.typography.labelSmall,
                color = HmifBlue,
                fontWeight = FontWeight.Bold
            )
        }
        Text(
            text = text,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}
