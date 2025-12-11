package com.example.hmifu_mobile

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.example.hmifu_mobile.navigation.NavGraph
import com.example.hmifu_mobile.navigation.Route
import com.example.hmifu_mobile.repository.AuthRepository
import com.example.hmifu_mobile.ui.theme.HMIFUMobileTheme
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

/**
 * Main Activity for HMIF U-Mobile.
 * Entry point for the application with Hilt dependency injection.
 */
@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var authRepository: AuthRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        
        setContent {
            HMIFUMobileTheme {
                HmifuMobileApp(
                    isAuthenticated = authRepository.isAuthenticated(),
                    onLogout = { authRepository.logout() }
                )
            }
        }
    }
}

/**
 * Main application composable with navigation setup.
 */
@Composable
fun HmifuMobileApp(
    isAuthenticated: Boolean,
    onLogout: suspend () -> Unit
) {
    val navController = rememberNavController()
    var shouldLogout by remember { mutableStateOf(false) }
    
    // Handle logout action
    LaunchedEffect(shouldLogout) {
        if (shouldLogout) {
            onLogout()
            shouldLogout = false
        }
    }
    
    Surface(
        modifier = Modifier.fillMaxSize()
    ) {
        NavGraph(
            navController = navController,
            startDestination = if (isAuthenticated) Route.Main else Route.Login,
            onLogout = { shouldLogout = true }
        )
    }
}