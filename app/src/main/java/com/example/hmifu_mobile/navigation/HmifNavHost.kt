package com.example.hmifu_mobile.navigation

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
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
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.hmifu_mobile.feature.auth.AuthViewModel
import com.example.hmifu_mobile.feature.auth.LoginScreen
import com.example.hmifu_mobile.feature.auth.ProfileSetupScreen
import com.example.hmifu_mobile.feature.auth.RegisterScreen

/**
 * Main navigation host with authentication flow and professional transitions.
 */
@Composable
fun HmifNavHost(modifier: Modifier = Modifier) {
    val navController = rememberNavController()
    val authViewModel: AuthViewModel = hiltViewModel()
    val authState by authViewModel.uiState.collectAsState()

    // Admin role check
    val adminRoles = listOf("admin", "moderator", "staff", "president", "vice_president", "secretary", "treasurer")
    val isAdmin = authState.role.lowercase() in adminRoles

    // Determine start destination based on auth state
    val startDestination = if (authState.isAuthenticated) {
        Screen.Home.route
    } else {
        Screen.Login.route
    }

    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier,
        enterTransition = {
            slideIntoContainer(AnimatedContentTransitionScope.SlideDirection.Left, tween(400))
        },
        exitTransition = {
            slideOutOfContainer(AnimatedContentTransitionScope.SlideDirection.Left, tween(400))
        },
        popEnterTransition = {
            slideIntoContainer(AnimatedContentTransitionScope.SlideDirection.Right, tween(400))
        },
        popExitTransition = {
            slideOutOfContainer(AnimatedContentTransitionScope.SlideDirection.Right, tween(400))
        }
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

        // Main app flow (Fade transitions between main tabs)
        composable(
            route = Screen.Home.route,
            enterTransition = { fadeIn(tween(400)) },
            exitTransition = { fadeOut(tween(400)) }
        ) {
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
                },
                isAdmin = isAdmin
            ) {
                com.example.hmifu_mobile.feature.home.HomeScreen()
            }
        }

        composable(
            route = Screen.Events.route,
            enterTransition = { fadeIn(tween(400)) },
            exitTransition = { fadeOut(tween(400)) }
        ) {
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
                },
                isAdmin = isAdmin
            ) {
                com.example.hmifu_mobile.feature.events.EventsScreen(
                    onEventClick = { eventId ->
                        navController.navigate("event_detail/$eventId")
                    }
                )
            }
        }

        composable(
            route = Screen.Profile.route,
            enterTransition = { fadeIn(tween(400)) },
            exitTransition = { fadeOut(tween(400)) }
        ) {
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
                },
                isAdmin = isAdmin
            ) {
                com.example.hmifu_mobile.feature.profile.ProfileScreen(
                    onEditProfile = { /* TODO: Navigate to edit profile */ },
                    onSettings = { navController.navigate("settings") },
                    onLogout = {
                        authViewModel.logout()
                        navController.navigate(Screen.Login.route) {
                            popUpTo(0) { inclusive = true }
                        }
                    }
                )
            }
        }
        
        // Admin Tab (Fade transition)
        composable(
            route = Screen.Admin.route,
            enterTransition = { fadeIn(tween(400)) },
            exitTransition = { fadeOut(tween(400)) }
        ) {
            MainScaffold(
                currentRoute = Screen.Admin.route,
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
                },
                isAdmin = isAdmin
            ) {
                com.example.hmifu_mobile.feature.admin.AdminScreen(
                    onNavigateBack = { navController.popBackStack() },
                    onCreateAnnouncement = { navController.navigate("create_announcement") },
                    onCreateEvent = { navController.navigate("create_event") },
                    onViewRegistrants = { eventId ->
                        navController.navigate("event_registrants/$eventId")
                    },
                    onNavigateToUserManagement = { navController.navigate("user_management") },
                    onNavigateToFinancials = { navController.navigate("financial_oversight") },
                    onNavigateToDocuments = { navController.navigate("document_inbox") },
                    onNavigateToElection = { navController.navigate("election_screen") },
                    onNavigateToContent = { navController.navigate("manage_content") }
                )
            }
        }

        // Settings screen (Slide)
        composable("settings") {
            com.example.hmifu_mobile.feature.settings.SettingsScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }

        // My Events screen (Slide)
        composable("my_events") {
            com.example.hmifu_mobile.feature.events.MyEventsScreen(
                onNavigateBack = { navController.popBackStack() },
                onEventClick = { eventId ->
                    navController.navigate("event_detail/$eventId")
                }
            )
        }

        // Admin sub-routes (Slide)
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
        
        composable("user_management") {
            com.example.hmifu_mobile.feature.admin.president.users.UserManagementScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable("financial_oversight") {
            com.example.hmifu_mobile.feature.admin.treasurer.TreasurerScreen(
                onNavigateBack = { navController.popBackStack() },
                isReadOnly = true
            )
        }
        
        composable("document_inbox") {
            com.example.hmifu_mobile.feature.admin.president.documents.DocumentInboxScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }
        
        composable("election_screen") {
            com.example.hmifu_mobile.feature.admin.president.election.ElectionScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }
        
        composable("manage_content") {
            com.example.hmifu_mobile.feature.admin.ManageContentScreen(
                onNavigateBack = { navController.popBackStack() },
                onCreateEvent = { navController.navigate("create_event") },
                onCreateAnnouncement = { navController.navigate("create_announcement") },
                onEditEvent = { id -> navController.navigate("edit_event/$id") },
                onEditAnnouncement = { id -> /* todo: implement edit announcement logic similar to event */ }
            )
        }
        
        composable("edit_event/{eventId}") { backStackEntry ->
            val eventId = backStackEntry.arguments?.getString("eventId")
            com.example.hmifu_mobile.feature.admin.CreateEventScreen(
                onNavigateBack = { navController.popBackStack() },
                onSuccess = { navController.popBackStack() },
                eventId = eventId
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
    onLogout: () -> Unit,
    isAdmin: Boolean = false,
    content: @Composable () -> Unit
) {
    // Create list of bottom nav items
    val navItems = mutableListOf(Screen.Home, Screen.Events, Screen.Profile)
    if (isAdmin) {
        navItems.add(Screen.Admin)
    }

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
