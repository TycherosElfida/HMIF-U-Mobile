package com.example.hmifu_mobile.feature.events

import android.graphics.Bitmap
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.hmifu_mobile.ui.components.GlassmorphicCard
import com.example.hmifu_mobile.ui.theme.HmifBlue
import com.example.hmifu_mobile.util.QrCodeGenerator
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TicketScreen(
    eventId: String,
    onNavigateBack: () -> Unit,
    viewModel: TicketViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val qrCodeGenerator = remember { QrCodeGenerator() }
    var qrBitmap by remember { mutableStateOf<Bitmap?>(null) }
    
    // Generate QR when IDs are available
    LaunchedEffect(uiState.userId, eventId) {
        if (uiState.userId.isNotBlank()) {
            val content = "${eventId}_${uiState.userId}"
            // Run generation on IO thread to avoid blocking UI
            launch(Dispatchers.IO) {
                val bitmap = qrCodeGenerator.generateQrCode(content, 600)
                qrBitmap = bitmap
            }
        }
    }
    
    LaunchedEffect(eventId) {
        viewModel.loadEvent(eventId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("My Ticket", fontWeight = FontWeight.Bold) },
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
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            if (uiState.isLoading) {
                CircularProgressIndicator()
            } else if (uiState.event != null) {
                val event = uiState.event!!
                
                GlassmorphicCard(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                    cornerRadius = 24.dp
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        // Event Title
                        Text(
                            text = event.title,
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center,
                            color = MaterialTheme.colorScheme.primary
                        )
                        
                        Text(
                            text = SimpleDateFormat("EEEE, dd MMM yyyy", Locale.getDefault()).format(Date(event.startTime)),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        
                        Divider(modifier = Modifier.padding(vertical = 8.dp))

                        // QR Code
                        Box(
                            modifier = Modifier
                                .size(250.dp)
                                .clip(RoundedCornerShape(16.dp))
                                .background(Color.White)
                                .padding(16.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            if (qrBitmap != null) {
                                Image(
                                    bitmap = qrBitmap!!.asImageBitmap(),
                                    contentDescription = "Event Ticket QR Code",
                                    modifier = Modifier.fillMaxSize()
                                )
                            } else {
                                CircularProgressIndicator()
                            }
                        }

                        Text(
                            text = "Scan this code at the event entrance",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = TextAlign.Center
                        )
                        
                        Divider(modifier = Modifier.padding(vertical = 8.dp))
                        
                        // User Info
                        Text(
                            text = "Attendee",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.outline
                        )
                        Text(
                            text = uiState.userName, // Should come from VM
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            } else {
                 Text("Event not found or failed to load.")
            }
        }
    }
}
