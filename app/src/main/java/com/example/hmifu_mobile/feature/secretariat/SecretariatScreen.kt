package com.example.hmifu_mobile.feature.secretariat

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.example.hmifu_mobile.data.local.entity.DocumentEntity
import com.example.hmifu_mobile.data.local.entity.DocumentStatus
import com.example.hmifu_mobile.data.local.entity.DocumentType
import com.example.hmifu_mobile.ui.components.GlassmorphicCard
import com.example.hmifu_mobile.ui.theme.*
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SecretariatScreen(
    onNavigateBack: () -> Unit = {},
    viewModel: SecretariatViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val uriHandler = LocalUriHandler.current

    LaunchedEffect(uiState.errorMessage) {
        uiState.errorMessage?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearMessages()
        }
    }
    LaunchedEffect(uiState.successMessage) {
        uiState.successMessage?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearMessages()
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { Text("Secretariat", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            if (uiState.isLoading) {
                LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
            }

            if (uiState.isSecretary) {
                SecretaryView(
                    uiState = uiState,
                    onTitleChange = viewModel::updateTitle,
                    onUrlChange = viewModel::updateUrl,
                    onTypeChange = viewModel::updateType,
                    onSubmit = viewModel::submitDocument
                )
            } else if (uiState.isPresident || uiState.isVicePresident) {
                PresidentView(
                    documents = uiState.documents,
                    onOpenUrl = { url -> 
                        try {
                            uriHandler.openUri(url)
                        } catch (e: Exception) {
                            // Handle invalid URL
                        }
                    },
                    onApprove = if (uiState.isPresident) { id -> viewModel.updateDocumentStatus(id, DocumentStatus.APPROVED) } else null,
                    onReject = if (uiState.isPresident) { id -> viewModel.updateDocumentStatus(id, DocumentStatus.REJECTED) } else null,
                    onRequestRevision = if (uiState.isPresident) { id -> viewModel.updateDocumentStatus(id, DocumentStatus.REVISION) } else null
                )
            } else {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("Access Denied: You are not Secretary, President, or Vice President.")
                }
            }
        }
    }
}

@Composable
fun SecretaryView(
    uiState: SecretariatUiState,
    onTitleChange: (String) -> Unit,
    onUrlChange: (String) -> Unit,
    onTypeChange: (DocumentType) -> Unit,
    onSubmit: () -> Unit
) {
    LazyColumn(
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Submission Form
        item {
            GlassmorphicCard(modifier = Modifier.fillMaxWidth()) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text("Submit Document", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    
                    OutlinedTextField(
                        value = uiState.title,
                        onValueChange = onTitleChange,
                        label = { Text("Document Title") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = uiState.url,
                        onValueChange = onUrlChange,
                        label = { Text("Document URL (Google Docs/Drive)") },
                        placeholder = { Text("https://docs.google.com/...") },
                        modifier = Modifier.fillMaxWidth(),
                        leadingIcon = { Icon(Icons.Default.Link, null) }
                    )

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        DocumentType.entries.forEach { type ->
                            FilterChip(
                                selected = uiState.type == type,
                                onClick = { onTypeChange(type) },
                                label = { 
                                    Text(when (type) {
                                        DocumentType.PROPOSAL -> "Proposal"
                                        DocumentType.LPJ -> "Laporan Pertanggungjawaban"
                                    }) 
                                }
                            )
                        }
                    }

                    Button(
                        onClick = onSubmit,
                        modifier = Modifier.fillMaxWidth(),
                        enabled = !uiState.isLoading && uiState.title.isNotBlank() && uiState.url.isNotBlank()
                    ) {
                        Text("Submit for Review")
                    }
                }
            }
        }

        item {
            Text("My Submissions", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, modifier = Modifier.padding(vertical = 8.dp))
        }

        if (uiState.myDocuments.isEmpty()) {
            item { Text("No documents submitted yet.", color = MaterialTheme.colorScheme.onSurfaceVariant) }
        } else {
            items(uiState.myDocuments) { doc ->
                DocumentItem(doc, null, null, null, null)
            }
        }
    }
}

@Composable
fun PresidentView(
    documents: List<DocumentEntity>,
    onOpenUrl: (String) -> Unit,
    onApprove: ((String) -> Unit)?,
    onReject: ((String) -> Unit)?,
    onRequestRevision: ((String) -> Unit)?
) {
    LazyColumn(
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Text("Pending Review", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        }

        if (documents.isEmpty()) {
            item { Text("No pending documents.", color = MaterialTheme.colorScheme.onSurfaceVariant) }
        } else {
            items(documents) { doc ->
                DocumentItem(
                    document = doc,
                    onOpenUrl = { onOpenUrl(doc.url) },
                    onApprove = onApprove?.let { { it(doc.id) } },
                    onReject = onReject?.let { { it(doc.id) } },
                    onRequestRevision = onRequestRevision?.let { { it(doc.id) } }
                )
            }
        }
    }
}

@Composable
fun DocumentItem(
    document: DocumentEntity,
    onOpenUrl: (() -> Unit)?,
    onApprove: (() -> Unit)?,
    onReject: (() -> Unit)?,
    onRequestRevision: (() -> Unit)?
) {
    GlassmorphicCard(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = when (document.type) {
                            DocumentType.PROPOSAL -> "PROPOSAL"
                            DocumentType.LPJ -> "LPJ"
                        },
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = document.title,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "By ${document.uploaderName} • ${SimpleDateFormat("dd MMM", Locale.getDefault()).format(Date(document.timestamp))}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                StatusBadge(document.status)
            }

            if (onOpenUrl != null) {
                Spacer(modifier = Modifier.height(12.dp))
                Button(
                    onClick = onOpenUrl,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondaryContainer, contentColor = MaterialTheme.colorScheme.onSecondaryContainer)
                ) {
                    Icon(Icons.Default.OpenInNew, null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Open Document")
                }
            }

            // Action Buttons for President
            if (onApprove != null && onReject != null && onRequestRevision != null) {
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedButton(
                        onClick = onReject,
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.error)
                    ) {
                        Text("Reject")
                    }
                    OutlinedButton(
                        onClick = onRequestRevision,
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = HmifOrange)
                    ) {
                        Text("Revise")
                    }
                    Button(
                        onClick = onApprove,
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = Success)
                    ) {
                        Text("Approve")
                    }
                }
            }
        }
    }
}

@Composable
fun StatusBadge(status: DocumentStatus) {
    val (color, text) = when (status) {
        DocumentStatus.PENDING -> MaterialTheme.colorScheme.outline to "Pending"
        DocumentStatus.APPROVED -> Success to "Approved"
        DocumentStatus.REVISION -> HmifOrange to "Revision"
        DocumentStatus.REJECTED -> MaterialTheme.colorScheme.error to "Rejected"
    }

    Surface(
        color = color.copy(alpha = 0.1f),
        shape = RoundedCornerShape(4.dp)
    ) {
        Text(
            text = text,
            color = color,
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
        )
    }
}
