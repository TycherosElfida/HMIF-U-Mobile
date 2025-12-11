package com.example.hmifu_mobile.feature.certificates

import android.content.Intent
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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.rounded.CardMembership
import androidx.compose.material.icons.rounded.Verified
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.example.hmifu_mobile.data.local.entity.CertificateEntity
import com.example.hmifu_mobile.ui.components.GlassmorphicCard
import com.example.hmifu_mobile.ui.components.StaggeredAnimatedItem
import com.example.hmifu_mobile.ui.theme.HmifBlue
import com.example.hmifu_mobile.ui.theme.HmifOrange
import com.example.hmifu_mobile.ui.theme.HmifTheme
import com.example.hmifu_mobile.ui.theme.Success
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Certificates Screen - Premium 2025 Design
 *
 * Features:
 * - Glassmorphic certificate cards
 * - Verification badge
 * - Download action
 * - Empty state with illustration
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CertificatesScreen(
    onNavigateBack: () -> Unit,
    viewModel: CertificateViewModel = hiltViewModel()
) {
    val certificates by viewModel.certificates.collectAsState()
    val context = LocalContext.current

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(HmifTheme.spacing.sm)
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.CardMembership,
                            contentDescription = null,
                            tint = HmifOrange
                        )
                        Text(
                            text = "My Certificates",
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
        if (certificates.isEmpty()) {
            EmptyState(modifier = Modifier.padding(padding))
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentPadding = PaddingValues(HmifTheme.spacing.lg),
                verticalArrangement = Arrangement.spacedBy(HmifTheme.spacing.md)
            ) {
                // Stats header
                item {
                    StaggeredAnimatedItem(index = 0) {
                        StatsHeader(count = certificates.size)
                    }
                }

                // Certificate cards
                itemsIndexed(certificates, key = { _, cert -> cert.id }) { index, cert ->
                    StaggeredAnimatedItem(index = index + 1) {
                        CertificateCard(
                            certificate = cert,
                            onOpen = {
                                if (cert.fileUrl.isNotBlank()) {
                                    val intent = Intent(Intent.ACTION_VIEW, cert.fileUrl.toUri())
                                    context.startActivity(intent)
                                }
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
// STATS HEADER
// ════════════════════════════════════════════════════════════════

@Composable
private fun StatsHeader(count: Int) {
    GlassmorphicCard(
        modifier = Modifier.fillMaxWidth(),
        cornerRadius = HmifTheme.cornerRadius.lg
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "🎓 Total Earned",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "$count Certificate${if (count != 1) "s" else ""}",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
            }

            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(RoundedCornerShape(HmifTheme.cornerRadius.md))
                    .background(HmifOrange.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Rounded.Verified,
                    contentDescription = null,
                    tint = HmifOrange,
                    modifier = Modifier.size(28.dp)
                )
            }
        }
    }
}

// ════════════════════════════════════════════════════════════════
// CERTIFICATE CARD
// ════════════════════════════════════════════════════════════════

@Composable
private fun CertificateCard(
    certificate: CertificateEntity,
    onOpen: () -> Unit
) {
    GlassmorphicCard(
        modifier = Modifier.fillMaxWidth(),
        onClick = onOpen,
        cornerRadius = HmifTheme.cornerRadius.lg
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(HmifTheme.spacing.sm)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = certificate.eventTitle,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Issued on ${formatDate(certificate.issueDate)}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(RoundedCornerShape(HmifTheme.cornerRadius.sm))
                        .background(HmifBlue.copy(alpha = 0.15f)),
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

            // Verification badge
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(HmifTheme.spacing.xs)
            ) {
                Icon(
                    imageVector = Icons.Rounded.Verified,
                    contentDescription = null,
                    tint = Success,
                    modifier = Modifier.size(16.dp)
                )
                Text(
                    text = "Verified • ${certificate.recipientName}",
                    style = MaterialTheme.typography.labelMedium,
                    color = Success
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
                text = "🏆",
                style = MaterialTheme.typography.displayLarge
            )
            Text(
                text = "No certificates yet",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = "Participate in events to earn certificates!",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.outline
            )
        }
    }
}

// ════════════════════════════════════════════════════════════════
// UTILITIES
// ════════════════════════════════════════════════════════════════

private fun formatDate(timestamp: Long): String {
    val sdf = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
    return sdf.format(Date(timestamp))
}
