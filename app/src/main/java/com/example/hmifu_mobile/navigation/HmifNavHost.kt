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
import androidx.navigation.NavType
import androidx.compose.runtime.remember
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.hmifu_mobile.feature.auth.AuthViewModel
import com.example.hmifu_mobile.feature.auth.LoginScreen
import com.example.hmifu_mobile.feature.auth.ProfileSetupScreen
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Alignment
import com.example.hmifu_mobile.feature.auth.RegisterScreen

import androidx.compose.material3.ExperimentalMaterial3Api

/**
 * Main navigation host with authentication flow.
 */
@OptIn(ExperimentalMaterial3Api::class)
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
                userRole = authState.userRole,
                currentRoute = Screen.Home.route,
                onNavigate = { route ->
                    navController.navigate(route) {
                        popUpTo(navController.graph.findStartDestination().id) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            ) {
                com.example.hmifu_mobile.feature.home.HomeScreen(
                    onNotificationsClick = { /* TODO */ },
                    onMemberCardClick = { navController.navigate("member_card") },
                    onScanQrClick = { navController.navigate("member_card") },
                    onResourcesClick = { navController.navigate("resources") },
                    onCompetitionsClick = { navController.navigate("category_feed/COMPETITION") },
                    onCareersClick = { navController.navigate("category_feed/CAREER") }
                )
            }
        }

        composable(Screen.Events.route) {
            MainScaffold(
                userRole = authState.userRole,
                currentRoute = Screen.Events.route,
                onNavigate = { route ->
                    navController.navigate(route) {
                        popUpTo(navController.graph.findStartDestination().id) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
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

        composable(
            route = "event_detail/{eventId}",
            arguments = listOf(navArgument("eventId") { type = NavType.StringType })
        ) { _ ->
            com.example.hmifu_mobile.feature.events.EventDetailScreen(
                onNavigateBack = { navController.popBackStack() },
                onEditEvent = { eventId ->
                    navController.navigate("create_event?eventId=$eventId")
                }
            )
        }

        composable("certificates") {
            com.example.hmifu_mobile.feature.certificates.CertificatesScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable("polls") {
            com.example.hmifu_mobile.feature.polls.PollsScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable("resources") {
            com.example.hmifu_mobile.feature.resources.ResourcesScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }
        composable(Screen.Profile.route) {
            MainScaffold(
                userRole = authState.userRole,
                currentRoute = Screen.Profile.route,
                onNavigate = { route ->
                    navController.navigate(route) {
                        popUpTo(navController.graph.findStartDestination().id) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            ) {
                com.example.hmifu_mobile.feature.profile.ProfileScreen(
                    onEditProfile = { navController.navigate("edit_profile") },
                    onSettings = { navController.navigate("settings") },
                    onMemberCard = { navController.navigate("member_card") },
                    onMyEvents = { navController.navigate("my_events") },
                    onCertificates = { navController.navigate("certificates") },
                    onPolls = { navController.navigate("polls") },
                    onResources = { navController.navigate("resources") },
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

        // Edit Profile screen
        composable("edit_profile") {
            com.example.hmifu_mobile.feature.profile.EditProfileScreen(
                onNavigateBack = { navController.popBackStack() },
                onSuccess = { navController.popBackStack() }
            )
        }

        // My Events screen (no bottom bar)
        composable("my_events") {
            com.example.hmifu_mobile.feature.events.MyEventsScreen(
                onNavigateBack = { navController.popBackStack() },
                onEventClick = { eventId ->
                    navController.navigate("event_detail/$eventId")
                },
                onViewTicket = { eventId ->
                    navController.navigate("ticket_screen/$eventId")
                }
            )
        }

        composable(
            route = "ticket_screen/{eventId}",
            arguments = listOf(navArgument("eventId") { type = NavType.StringType })
        ) { backStackEntry ->
            val eventId = backStackEntry.arguments?.getString("eventId") ?: ""
            com.example.hmifu_mobile.feature.events.TicketScreen(
                eventId = eventId,
                onNavigateBack = { navController.popBackStack() }
            )
        }

        // Admin Panel routes
        composable("admin") {
            com.example.hmifu_mobile.feature.admin.AdminScreen(
                onNavigateBack = { navController.popBackStack() },
                onManageAnnouncements = { navController.navigate("manage_announcements") },
                onEditAnnouncement = { id -> navController.navigate("create_announcement?announcementId=$id") },
                onManageEvents = { navController.navigate("manage_events") },
                onSecretariat = { navController.navigate("secretariat") },
                onViewRegistrants = { eventId ->
                    navController.navigate("event_registrants/$eventId")
                },
                onFinance = { navController.navigate("finance") },
                onManageUsers = { navController.navigate("user_management") },
                onManageResources = { navController.navigate("manage_resources") },
                onScanTicket = { navController.navigate("scan_ticket") }
            )
        }

        composable("finance") {
            com.example.hmifu_mobile.feature.finance.FinanceScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable("user_management") {
            com.example.hmifu_mobile.feature.admin.users.UserManagementScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable("manage_events") {
            com.example.hmifu_mobile.feature.admin.ManageEventsScreen(
                onNavigateBack = { navController.popBackStack() },
                onCreateEvent = { navController.navigate("create_event") },
                onEditEvent = { id -> navController.navigate("create_event?eventId=$id") }
            )
        }

        composable("manage_announcements") {
            com.example.hmifu_mobile.feature.admin.ManageAnnouncementsScreen(
                onNavigateBack = { navController.popBackStack() },
                onCreateAnnouncement = { navController.navigate("create_announcement") },
                onEditAnnouncement = { id -> navController.navigate("create_announcement?announcementId=$id") }
            )
        }

        composable("secretariat") {
            com.example.hmifu_mobile.feature.secretariat.SecretariatScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(
            route = "create_announcement?announcementId={announcementId}",
            arguments = listOf(
                navArgument("announcementId") {
                    nullable = true
                    defaultValue = null
                    type = NavType.StringType
                }
            )
        ) {
            com.example.hmifu_mobile.feature.admin.CreateAnnouncementScreen(
                onNavigateBack = { navController.popBackStack() },
                onSuccess = { navController.popBackStack() }
            )
        }

        composable(
            route = "create_event?eventId={eventId}",
            arguments = listOf(
                navArgument("eventId") {
                    nullable = true
                    defaultValue = null
                    type = NavType.StringType
                }
            )
        ) {
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

        composable("manage_resources") {
            com.example.hmifu_mobile.feature.admin.resources.ManageResourcesScreen(
                onNavigateBack = { navController.popBackStack() },
                onNavigateToCreate = { navController.navigate("create_resource") }
            )
        }

        composable("create_resource") {
            com.example.hmifu_mobile.feature.admin.resources.CreateResourceScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable("scan_ticket") {
            com.example.hmifu_mobile.feature.admin.scan.ScanTicketScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(
            route = "category_feed/{categoryType}",
            arguments = listOf(navArgument("categoryType") { type = NavType.StringType })
        ) {
            com.example.hmifu_mobile.feature.home.CategoryFeedScreen(
                onNavigateBack = { navController.popBackStack() },
                onEventClick = { eventId ->
                    navController.navigate("event_detail/$eventId")
                }
            )
        }
    }
}

/**
 * Main scaffold with bottom navigation.
 */
@Composable
private fun MainScaffold(
    currentRoute: String,
    userRole: String,
    onNavigate: (String) -> Unit,
    content: @Composable () -> Unit
) {
    // Create list of bottom nav items
    val navItems = remember(userRole) {
        val items = mutableListOf(Screen.Home, Screen.Events, Screen.Profile)
        val adminRoles = listOf("admin", "moderator", "staff", "treasurer", "secretary", "president", "vice_president")
        if (userRole.lowercase() in adminRoles) {
            items.add(Screen.Admin)
        }
        items
    }

    // Use Box to overlay Floating Bottom Navigation
    Scaffold(
        // We don't use the standard bottomBar parameter to avoid the reserved space
        // confusing the floating effect. We want content to be able to go behind it if needed,
        // or just have it float over.
    ) { innerPadding ->
        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            // Main Content
            // Add extra padding at bottom so content isn't hidden behind floating nav
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                content()
            }

            // Premium Floating Navbar
            com.example.hmifu_mobile.ui.components.FloatingBottomNavigation(
                screens = navItems,
                currentRoute = currentRoute,
                onNavigate = onNavigate,
                modifier = Modifier.align(Alignment.BottomCenter)
            )
        }
    }
}

// Placeholder removed - all screens now use real implementations

