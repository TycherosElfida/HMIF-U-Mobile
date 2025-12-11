package com.example.hmifu_mobile.feature.qr

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel

/**
 * QR Scanner Screen for admin check-in.
 *
 * Note: Actual camera-based QR scanning requires CameraX integration.
 * This screen provides the UI framework and manual input fallback.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QrScannerScreen(
    eventId: String = "",
    onNavigateBack: () -> Unit = {},
    viewModel: QrScannerViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(eventId) {
        if (eventId.isNotBlank()) {
            viewModel.setEventContext(eventId)
        }
    }

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
                        text = "QR Check-in Scanner",
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
                }
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Stats Bar
                StatsBar(checkedInCount = uiState.totalCheckedIn)

                // Scanner Area
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    if (uiState.isProcessing) {
                        ProcessingIndicator()
                    } else if (uiState.isScanning) {
                        ScannerPlaceholder()
                    }
                }

                // Manual Input Option
                ManualInputSection(
                    onSubmit = { qrData ->
                        viewModel.processQrCode(qrData)
                    }
                )
            }

            // Result Overlay
            AnimatedVisibility(
                visible = uiState.lastResult != null,
                enter = fadeIn() + scaleIn(),
                exit = fadeOut() + scaleOut()
            ) {
                uiState.lastResult?.let { result ->
                    ResultOverlay(
                        result = result,
                        onDismiss = { viewModel.dismissResult() },
                        onContinue = { viewModel.resumeScanning() }
                    )
                }
            }
        }
    }
}

@Composable
private fun StatsBar(checkedInCount: Int) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = checkedInCount.toString(),
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
                Text(
                    text = "Checked In",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
        }
    }
}

@Composable
private fun ScannerPlaceholder() {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Box(
            modifier = Modifier
                .size(250.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(MaterialTheme.colorScheme.surfaceVariant),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    Icons.Default.QrCodeScanner,
                    contentDescription = null,
                    modifier = Modifier.size(64.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = "Point camera at QR code",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        Text(
            text = "Camera integration requires CameraX setup.\nUse manual input below for testing.",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.outline,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun ProcessingIndicator() {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        CircularProgressIndicator(
            modifier = Modifier.size(64.dp),
            strokeWidth = 6.dp
        )
        Text(
            text = "Validating...",
            style = MaterialTheme.typography.bodyLarge
        )
    }
}

@Composable
private fun ManualInputSection(onSubmit: (String) -> Unit) {
    val inputText = remember { androidx.compose.runtime.mutableStateOf("") }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "Manual Input (for testing)",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Medium
            )

            androidx.compose.material3.OutlinedTextField(
                value = inputText.value,
                onValueChange = { inputText.value = it },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("Paste QR data: HMIF:userId:eventId:code:timestamp") },
                singleLine = true
            )

            Button(
                onClick = {
                    if (inputText.value.isNotBlank()) {
                        onSubmit(inputText.value)
                        inputText.value = ""
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = inputText.value.isNotBlank()
            ) {
                Text("Process Check-in")
            }
        }
    }
}

@Composable
private fun ResultOverlay(
    result: CheckInResult,
    onDismiss: () -> Unit,
    onContinue: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.7f)),
        contentAlignment = Alignment.Center
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth(0.85f)
                .padding(16.dp),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            )
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Result Icon
                val (icon, iconColor, title) = getResultDisplay(result)

                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .clip(CircleShape)
                        .background(iconColor.copy(alpha = 0.2f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        icon,
                        contentDescription = null,
                        modifier = Modifier.size(48.dp),
                        tint = iconColor
                    )
                }

                Text(
                    text = title,
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )

                Text(
                    text = getResultMessage(result),
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(8.dp))

                Button(
                    onClick = if (result is CheckInResult.Success) onContinue else onDismiss,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(if (result is CheckInResult.Success) "Scan Next" else "Try Again")
                }
            }
        }
    }
}

@Composable
private fun getResultDisplay(result: CheckInResult): Triple<ImageVector, Color, String> {
    return when (result) {
        is CheckInResult.Success -> Triple(
            Icons.Default.Check,
            Color(0xFF4CAF50),
            "Check-in Successful!"
        )

        is CheckInResult.AlreadyCheckedIn -> Triple(
            Icons.Default.Info,
            MaterialTheme.colorScheme.tertiary,
            "Already Checked In"
        )

        is CheckInResult.NotRegistered -> Triple(
            Icons.Default.Warning,
            MaterialTheme.colorScheme.error,
            "Not Registered"
        )

        is CheckInResult.InvalidCode -> Triple(
            Icons.Default.Close,
            MaterialTheme.colorScheme.error,
            "Invalid Code"
        )

        is CheckInResult.Error -> Triple(
            Icons.Default.Close,
            MaterialTheme.colorScheme.error,
            "Error"
        )
    }
}

private fun getResultMessage(result: CheckInResult): String {
    return when (result) {
        is CheckInResult.Success -> "${result.userName} has been checked in successfully."
        is CheckInResult.AlreadyCheckedIn -> "${result.userName} has already checked in for this event."
        is CheckInResult.NotRegistered -> "User is not registered for this event."
        is CheckInResult.InvalidCode -> "QR code is expired or invalid. ${result.reason}"
        is CheckInResult.Error -> result.message
    }
}
