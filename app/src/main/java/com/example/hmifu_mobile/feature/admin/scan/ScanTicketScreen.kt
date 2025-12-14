package com.example.hmifu_mobile.feature.admin.scan

import android.Manifest
import android.util.Size
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.OptIn
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.hmifu_mobile.ui.components.GlassmorphicCard
import com.example.hmifu_mobile.ui.theme.Error
import com.example.hmifu_mobile.ui.theme.Success
import com.google.zxing.BinaryBitmap
import com.google.zxing.MultiFormatReader
import com.google.zxing.PlanarYUVLuminanceSource
import com.google.zxing.common.HybridBinarizer
import com.journeyapps.barcodescanner.CompoundBarcodeView
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

/**
 * Screen used to scan user QR tickets.
 * Implementation uses ZXing Embedded CompoundBarcodeView for simplicity.
 */
@androidx.compose.material3.ExperimentalMaterial3Api
@Composable
fun ScanTicketScreen(
    onNavigateBack: () -> Unit,
    viewModel: ScanTicketViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val scanState by viewModel.scanState.collectAsState()
    
    // Permission handling
    var hasCameraPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.CAMERA
            ) == android.content.pm.PackageManager.PERMISSION_GRANTED
        )
    }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { granted ->
            hasCameraPermission = granted
        }
    )

    LaunchedEffect(Unit) {
        if (!hasCameraPermission) {
            launcher.launch(Manifest.permission.CAMERA)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Scan Ticket", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent,
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White
                )
            )
        },
        containerColor = Color.Black
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize().padding(padding)) {
            if (hasCameraPermission) {
                if (scanState !is ScanState.Success && scanState !is ScanState.Error) {
                    AndroidView(
                        factory = { ctx ->
                            CompoundBarcodeView(ctx).apply {
                                val exposure = 0.5 // example config if needed
                                decodeContinuous { result ->
                                    result.text?.let {
                                        viewModel.onScanResult(it)
                                    }
                                }
                                resume()
                            }
                        },
                        modifier = Modifier.fillMaxSize()
                    )
                }

                // Overlay
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    // Reticle
                    if (scanState is ScanState.Idle || scanState is ScanState.Loading) {
                        Box(
                            modifier = Modifier
                                .size(300.dp)
                                .background(Color.White.copy(alpha = 0.1f), RoundedCornerShape(16.dp))
                                .padding(2.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.QrCodeScanner,
                                contentDescription = null,
                                modifier = Modifier.fillMaxSize(),
                                tint = Color.White.copy(alpha = 0.5f)
                            )
                        }
                    }

                    // Result Dialog / Overlay
                    when (val state = scanState) {
                        is ScanState.Success -> {
                            ScanResultCard(
                                isSuccess = true,
                                title = "Check-in Successful",
                                message = "User Checked In!",
                                onReset = viewModel::resetState
                            )
                        }
                        is ScanState.Error -> {
                            ScanResultCard(
                                isSuccess = false,
                                title = "Check-in Failed",
                                message = state.message,
                                onReset = viewModel::resetState
                            )
                        }
                        ScanState.Loading -> {
                            CircularProgressIndicator()
                        }
                        else -> {}
                    }
                }
            } else {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("Camera permission required", color = Color.White)
                }
            }
        }
    }
}

@Composable
fun ScanResultCard(
    isSuccess: Boolean,
    title: String,
    message: String,
    onReset: () -> Unit
) {
    GlassmorphicCard(
        modifier = Modifier
            .padding(32.dp)
            .wrapContentHeight()
    ) {
        Column(
            modifier = Modifier.padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Icon(
                imageVector = if (isSuccess) Icons.Default.CheckCircle else Icons.Default.Error,
                contentDescription = null,
                tint = if (isSuccess) Success else Error,
                modifier = Modifier.size(64.dp)
            )
            
            Text(
                text = title,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = if (isSuccess) Success else Error
            )
            
            Text(
                text = message,
                style = MaterialTheme.typography.bodyMedium,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )
            
            Button(
                onClick = onReset,
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isSuccess) Success else Error
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Scan Next")
            }
        }
    }
}
