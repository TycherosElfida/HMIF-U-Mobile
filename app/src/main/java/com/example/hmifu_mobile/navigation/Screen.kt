package com.example.hmifu_mobile.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Event
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.ui.graphics.vector.ImageVector

/**
 * Sealed class representing bottom navigation destinations.
 */
sealed class Screen(
    val route: String,
    val title: String,
    val icon: ImageVector
) {
    data object Home : Screen(
        route = "home",
        title = "Home",
        icon = Icons.Default.Home
    )

    data object Events : Screen(
        route = "events",
        title = "Events",
        icon = Icons.Default.Event
    )

    data object Profile : Screen(
        route = "profile",
        title = "Profile",
        icon = Icons.Default.Person
    )

    data object Admin : Screen(
        route = "admin",
        title = "Admin",
        icon = Icons.Default.Settings
    )

    companion object {
        val bottomNavItems = listOf(Home, Events, Profile)
    }
}
