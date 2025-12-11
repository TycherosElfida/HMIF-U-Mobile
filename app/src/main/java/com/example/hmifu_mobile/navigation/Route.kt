package com.example.hmifu_mobile.navigation

import kotlinx.serialization.Serializable

/**
 * Navigation routes for the application.
 * Using Kotlin Serialization for type-safe navigation with Navigation Compose.
 */
sealed interface Route {
    /**
     * Login screen route
     */
    @Serializable
    data object Login : Route

    /**
     * Register screen route
     */
    @Serializable
    data object Register : Route
    
    /**
     * Main screen route with bottom navigation (after successful login)
     */
    @Serializable
    data object Main : Route

    /**
     * Add Event screen route (admin only)
     */
    @Serializable
    data object AddEvent : Route

    /**
     * Event Detail screen route
     */
    @Serializable
    data class EventDetail(val eventId: String) : Route
}

/**
 * Bottom navigation tab items
 */
enum class BottomNavItem(
    val title: String,
    val iconName: String
) {
    Home("Home", "home"),
    Events("Events", "event"),
    Profile("Profile", "person")
}
