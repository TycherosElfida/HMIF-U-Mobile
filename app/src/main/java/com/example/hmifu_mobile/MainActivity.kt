package com.example.hmifu_mobile

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.hmifu_mobile.navigation.HmifNavHost
import com.example.hmifu_mobile.ui.theme.HMIFUMobileTheme
import dagger.hilt.android.AndroidEntryPoint

/**
 * Main entry point for HMIF U-Mobile.
 */
@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            HMIFUMobileTheme {
                HmifNavHost()
            }
        }
    }
}