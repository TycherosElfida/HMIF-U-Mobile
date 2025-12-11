package com.example.hmifu_mobile.feature.admin.president

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.HowToVote
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.People
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.hmifu_mobile.ui.components.HmifCard

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PresidentScreen(
    onNavigateBack: () -> Unit,
    onNavigateToUserManagement: () -> Unit = {},
    onNavigateToFinancials: () -> Unit = {},
    onNavigateToContent: () -> Unit = {},
    onNavigateToDocuments: () -> Unit = {},
    onNavigateToElection: () -> Unit = {}
) {
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("President Dashboard") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = androidx.compose.foundation.layout.Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "Welcome, President",
                style = androidx.compose.material3.MaterialTheme.typography.headlineSmall,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            // User Management Card
            FeatureCard(
                title = "Manage BPH Roles",
                description = "Assign Vice President, Secretary, Treasurer",
                icon = Icons.Default.People,
                onClick = onNavigateToUserManagement
            )

            // Financials Card
            FeatureCard(
                title = "Financial Overview",
                description = "View Treasurer's reports (Read-Only)",
                icon = Icons.Default.AttachMoney,
                onClick = onNavigateToFinancials
            )

            // Content Management Card
            FeatureCard(
                title = "Events & Announcements",
                description = "Manage or Edit Events & Announcements",
                icon = Icons.Default.Image,
                onClick = onNavigateToContent
            )

            // Documents
            FeatureCard(
                title = "Document Inbox",
                description = "Review Proposals & LPJ (Coming Soon)",
                icon = Icons.Default.Description,
                onClick = onNavigateToDocuments
            )

            // Election
            FeatureCard(
                title = "Election System",
                description = "Manage Succession (Coming Soon)",
                icon = Icons.Default.HowToVote,
                onClick = onNavigateToElection
            )
        }
    }
}

@Composable
private fun FeatureCard(
    title: String,
    description: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    onClick: () -> Unit
) {
    HmifCard(
        modifier = Modifier.fillMaxWidth(),
        onClick = onClick
    ) {
        androidx.compose.foundation.layout.Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(40.dp),
                tint = androidx.compose.material3.MaterialTheme.colorScheme.primary
            )
            androidx.compose.foundation.layout.Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(
                    text = title,
                    style = androidx.compose.material3.MaterialTheme.typography.titleMedium,
                    fontWeight = androidx.compose.ui.text.font.FontWeight.Bold
                )
                Text(
                    text = description,
                    style = androidx.compose.material3.MaterialTheme.typography.bodyMedium,
                    color = androidx.compose.material3.MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}
