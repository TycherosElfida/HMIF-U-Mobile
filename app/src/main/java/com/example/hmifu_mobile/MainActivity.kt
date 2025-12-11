package com.example.hmifu_mobile

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.unit.dp
import com.example.hmifu_mobile.navigation.HmifNavHost
import com.example.hmifu_mobile.ui.theme.HMIFUMobileTheme
import dagger.hilt.android.AndroidEntryPoint

/**
 * Main entry point for HMIF U-Mobile.
 */
@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @javax.inject.Inject
    lateinit var networkMonitor: com.example.hmifu_mobile.util.NetworkMonitor

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val isOnline by networkMonitor.isOnline.collectAsState(initial = true)

            HMIFUMobileTheme {
                androidx.compose.foundation.layout.Column {
                    if (!isOnline) {
                        androidx.compose.material3.Surface(
                            color = androidx.compose.material3.MaterialTheme.colorScheme.errorContainer,
                            modifier = androidx.compose.ui.Modifier.fillMaxWidth()
                        ) {
                            androidx.compose.material3.Text(
                                text = "No Internet Connection - Using Offline Mode",
                                modifier = androidx.compose.ui.Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp),
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                                style = androidx.compose.material3.MaterialTheme.typography.labelMedium,
                                color = androidx.compose.material3.MaterialTheme.colorScheme.onErrorContainer
                            )
                        }
                    }
                    HmifNavHost()
                }
            }
        }
    }
}