package com.example.hmifu_mobile.navigation

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import com.example.hmifu_mobile.feature.auth.LoginScreen
import com.example.hmifu_mobile.feature.auth.RegisterScreen
import com.example.hmifu_mobile.feature.events.AddEventScreen
import com.example.hmifu_mobile.feature.events.EventDetailScreen
import com.example.hmifu_mobile.feature.main.MainScreen
import com.example.hmifu_mobile.model.Event
import com.example.hmifu_mobile.repository.EventRepository

/**
 * Main navigation graph for HMIF U-Mobile.
 * Defines all navigation destinations and transitions.
 */
@Composable
fun NavGraph(
    navController: NavHostController,
    startDestination: Route = Route.Login,
    onLogout: () -> Unit,
    eventRepository: EventRepository
) {
    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        // Login Screen
        composable<Route.Login>(
            enterTransition = {
                fadeIn(animationSpec = tween(300))
            },
            exitTransition = {
                fadeOut(animationSpec = tween(300))
            }
        ) {
            LoginScreen(
                onLoginSuccess = {
                    navController.navigate(Route.Main) {
                        popUpTo(Route.Login) { inclusive = true }
                    }
                },
                onNavigateToRegister = {
                    navController.navigate(Route.Register)
                }
            )
        }

        // Register Screen
        composable<Route.Register>(
            enterTransition = {
                slideIntoContainer(
                    towards = AnimatedContentTransitionScope.SlideDirection.Left,
                    animationSpec = tween(400)
                ) + fadeIn(animationSpec = tween(400))
            },
            exitTransition = {
                slideOutOfContainer(
                    towards = AnimatedContentTransitionScope.SlideDirection.Right,
                    animationSpec = tween(400)
                ) + fadeOut(animationSpec = tween(400))
            }
        ) {
            RegisterScreen(
                onRegisterSuccess = {
                    navController.navigate(Route.Main) {
                        popUpTo(Route.Login) { inclusive = true }
                    }
                },
                onNavigateToLogin = {
                    navController.popBackStack()
                }
            )
        }
        
        // Main Screen with Bottom Navigation
        composable<Route.Main>(
            enterTransition = {
                slideIntoContainer(
                    towards = AnimatedContentTransitionScope.SlideDirection.Left,
                    animationSpec = tween(400)
                ) + fadeIn(animationSpec = tween(400))
            },
            exitTransition = {
                slideOutOfContainer(
                    towards = AnimatedContentTransitionScope.SlideDirection.Right,
                    animationSpec = tween(400)
                ) + fadeOut(animationSpec = tween(400))
            }
        ) {
            MainScreen(
                onLogout = {
                    onLogout()
                    navController.navigate(Route.Login) {
                        popUpTo(Route.Main) { inclusive = true }
                    }
                },
                onAddEventClick = {
                    navController.navigate(Route.AddEvent)
                },
                onEventClick = { eventId ->
                    navController.navigate(Route.EventDetail(eventId))
                }
            )
        }

        // Add Event Screen (Admin only)
        composable<Route.AddEvent>(
            enterTransition = {
                slideIntoContainer(
                    towards = AnimatedContentTransitionScope.SlideDirection.Up,
                    animationSpec = tween(400)
                ) + fadeIn(animationSpec = tween(400))
            },
            exitTransition = {
                slideOutOfContainer(
                    towards = AnimatedContentTransitionScope.SlideDirection.Down,
                    animationSpec = tween(400)
                ) + fadeOut(animationSpec = tween(400))
            }
        ) {
            AddEventScreen(
                onNavigateBack = {
                    navController.popBackStack()
                },
                onEventAdded = {
                    navController.popBackStack()
                }
            )
        }

        // Event Detail Screen
        composable<Route.EventDetail>(
            enterTransition = {
                slideIntoContainer(
                    towards = AnimatedContentTransitionScope.SlideDirection.Left,
                    animationSpec = tween(400)
                ) + fadeIn(animationSpec = tween(400))
            },
            exitTransition = {
                slideOutOfContainer(
                    towards = AnimatedContentTransitionScope.SlideDirection.Right,
                    animationSpec = tween(400)
                ) + fadeOut(animationSpec = tween(400))
            }
        ) { backStackEntry ->
            val route = backStackEntry.toRoute<Route.EventDetail>()
            var event by remember { mutableStateOf<Event?>(null) }
            
            LaunchedEffect(route.eventId) {
                eventRepository.getEvents().collect { events ->
                    event = events.find { it.id == route.eventId }
                }
            }
            
            event?.let { eventData ->
                EventDetailScreen(
                    event = eventData,
                    onNavigateBack = {
                        navController.popBackStack()
                    }
                )
            }
        }
    }
}
