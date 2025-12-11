package com.example.hmifu_mobile.feature.main

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Event
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.outlined.Event
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import com.example.hmifu_mobile.feature.events.EventsScreen
import com.example.hmifu_mobile.feature.home.HomeScreen
import com.example.hmifu_mobile.feature.profile.ProfileScreen
import com.example.hmifu_mobile.navigation.BottomNavItem
import com.example.hmifu_mobile.ui.theme.Primary
import com.example.hmifu_mobile.ui.theme.SurfaceDark
import com.example.hmifu_mobile.ui.theme.TextSecondary

/**
 * Main screen with bottom navigation.
 * Contains Home, Events, and Profile tabs.
 */
@Composable
fun MainScreen(
    onLogout: () -> Unit,
    onAddEventClick: () -> Unit = {},
    onEventClick: (String) -> Unit = {}
) {
    var selectedTabIndex by rememberSaveable { mutableIntStateOf(0) }

    val tabs = listOf(
        BottomNavTab(
            item = BottomNavItem.Home,
            selectedIcon = Icons.Filled.Home,
            unselectedIcon = Icons.Outlined.Home
        ),
        BottomNavTab(
            item = BottomNavItem.Events,
            selectedIcon = Icons.Filled.Event,
            unselectedIcon = Icons.Outlined.Event
        ),
        BottomNavTab(
            item = BottomNavItem.Profile,
            selectedIcon = Icons.Filled.Person,
            unselectedIcon = Icons.Outlined.Person
        )
    )

    Scaffold(
        bottomBar = {
            NavigationBar(
                containerColor = SurfaceDark,
                contentColor = Color.White
            ) {
                tabs.forEachIndexed { index, tab ->
                    NavigationBarItem(
                        selected = selectedTabIndex == index,
                        onClick = { selectedTabIndex = index },
                        icon = {
                            Icon(
                                imageVector = if (selectedTabIndex == index) tab.selectedIcon else tab.unselectedIcon,
                                contentDescription = tab.item.title
                            )
                        },
                        label = {
                            Text(
                                text = tab.item.title,
                                fontWeight = if (selectedTabIndex == index) FontWeight.SemiBold else FontWeight.Normal
                            )
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = Primary,
                            selectedTextColor = Primary,
                            unselectedIconColor = TextSecondary,
                            unselectedTextColor = TextSecondary,
                            indicatorColor = Primary.copy(alpha = 0.15f)
                        )
                    )
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(innerPadding)
        ) {
            when (selectedTabIndex) {
                0 -> HomeScreen(onEventClick = onEventClick)
                1 -> EventsScreen(onAddEventClick = onAddEventClick)
                2 -> ProfileScreen(onLogout = onLogout)
            }
        }
    }
}

/**
 * Data class for bottom navigation tab
 */
private data class BottomNavTab(
    val item: BottomNavItem,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector
)
