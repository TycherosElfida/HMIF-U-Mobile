package com.example.hmifu_mobile.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.hmifu_mobile.feature.auth.AuthViewModel
import com.example.hmifu_mobile.feature.auth.LoginScreen
import com.example.hmifu_mobile.feature.auth.ProfileSetupScreen
import com.example.hmifu_mobile.feature.auth.RegisterScreen

/**
 * Main navigation host with authentication flow.
 */
@Composable
fun HmifNavHost(modifier: Modifier = Modifier) {
    val navController = rememberNavController()
    val authViewModel: AuthViewModel = hiltViewModel()
    val authState by authViewModel.uiState.collectAsState()

    // Determine start destination based on auth state
    val startDestination = if (authState.isAuthenticated) {
        Screen.Home.route
    } else {
        Screen.Login.route
    }

    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier
    ) {
        // Auth flow (no bottom bar)
        composable(Screen.Login.route) {
            LoginScreen(
                onNavigateToRegister = {
                    navController.navigate(Screen.Register.route)
                },
                onLoginSuccess = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                },
                viewModel = authViewModel
            )
        }

        composable(Screen.Register.route) {
            RegisterScreen(
                onNavigateBack = {
                    navController.popBackStack()
                },
                onRegisterSuccess = {
                    navController.navigate(Screen.ProfileSetup.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                },
                viewModel = authViewModel
            )
        }

        composable(Screen.ProfileSetup.route) {
            ProfileSetupScreen(
                onProfileComplete = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.ProfileSetup.route) { inclusive = true }
                    }
                }
            )
        }

        // Main app flow (with bottom bar)
        composable(Screen.Home.route) {
            MainScaffold(
                currentRoute = Screen.Home.route,
                onNavigate = { route ->
                    navController.navigate(route) {
                        popUpTo(navController.graph.findStartDestination().id) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                },
                onLogout = {
                    authViewModel.logout()
                    navController.navigate(Screen.Login.route) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            ) {
                com.example.hmifu_mobile.feature.home.HomeScreen()
            }
        }

        composable(Screen.Events.route) {
            MainScaffold(
                currentRoute = Screen.Events.route,
                onNavigate = { route ->
                    navController.navigate(route) {
                        popUpTo(navController.graph.findStartDestination().id) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                },
                onLogout = {
                    authViewModel.logout()
                    navController.navigate(Screen.Login.route) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            ) {
                com.example.hmifu_mobile.feature.events.EventsScreen(
                    onEventClick = { eventId ->
                        navController.navigate("event_detail/$eventId")
                    }
                )
            }
        }

        composable(Screen.Profile.route) {
            MainScaffold(
                currentRoute = Screen.Profile.route,
                onNavigate = { route ->
                    navController.navigate(route) {
                        popUpTo(navController.graph.findStartDestination().id) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                },
                onLogout = {
                    authViewModel.logout()
                    navController.navigate(Screen.Login.route) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            ) {
                com.example.hmifu_mobile.feature.profile.ProfileScreen(
                    onEditProfile = { /* TODO: Navigate to edit profile */ },
                    onSettings = { navController.navigate("settings") },
                    onMemberCard = { navController.navigate("member_card") },
                    onLogout = {
                        authViewModel.logout()
                        navController.navigate(Screen.Login.route) {
                            popUpTo(0) { inclusive = true }
                        }
                    }
                )
            }
        }

        // Settings screen (no bottom bar)
        composable("settings") {
            com.example.hmifu_mobile.feature.settings.SettingsScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }

        // My Events screen (no bottom bar)
        composable("my_events") {
            com.example.hmifu_mobile.feature.events.MyEventsScreen(
                onNavigateBack = { navController.popBackStack() },
                onEventClick = { eventId ->
                    navController.navigate("event_detail/$eventId")
                }
            )
        }

        // Admin Panel routes
        composable("admin") {
            com.example.hmifu_mobile.feature.admin.AdminScreen(
                onNavigateBack = { navController.popBackStack() },
                onCreateAnnouncement = { navController.navigate("create_announcement") },
                onCreateEvent = { navController.navigate("create_event") },
                onViewRegistrants = { eventId ->
                    navController.navigate("event_registrants/$eventId")
                }
            )
        }

        composable("create_announcement") {
            com.example.hmifu_mobile.feature.admin.CreateAnnouncementScreen(
                onNavigateBack = { navController.popBackStack() },
                onSuccess = { navController.popBackStack() }
            )
        }

        composable("create_event") {
            com.example.hmifu_mobile.feature.admin.CreateEventScreen(
                onNavigateBack = { navController.popBackStack() },
                onSuccess = { navController.popBackStack() }
            )
        }

        composable("event_registrants/{eventId}") {
            com.example.hmifu_mobile.feature.admin.EventRegistrantsScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable("member_card") {
            com.example.hmifu_mobile.feature.qr.MemberCardScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable("qr_scanner/{eventId}") { backStackEntry ->
            com.example.hmifu_mobile.feature.qr.QrScannerScreen(
                eventId = backStackEntry.arguments?.getString("eventId") ?: "",
                onNavigateBack = { navController.popBackStack() }
            )
        }
    }
}

/**
 * Main scaffold with bottom navigation.
 */
@Composable
@Suppress("UNUSED_PARAMETER")
private fun MainScaffold(
    currentRoute: String,
    onNavigate: (String) -> Unit,
    onLogout: () -> Unit,  // Kept for future use (e.g., logout from app bar)
    content: @Composable () -> Unit
) {
    // Create list of bottom nav items with non-null assertion to avoid R8 issues
    val navItems = listOf(Screen.Home, Screen.Events, Screen.Profile)

    Scaffold(
        bottomBar = {
            NavigationBar {
                navItems.forEach { screen ->
                    NavigationBarItem(
                        icon = {
                            screen.icon?.let { Icon(it, contentDescription = screen.title) }
                        },
                        label = { Text(screen.title) },
                        selected = currentRoute == screen.route,
                        onClick = { onNavigate(screen.route) }
                    )
                }
            }
        }
    ) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding)) {
            content()
        }
    }
}

// Placeholder removed - all screens now use real implementations

