package com.smarttoolfactory.tutorial3_1navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Settings
import androidx.compose.ui.graphics.vector.ImageVector
import kotlinx.serialization.Serializable


// Define a home route that doesn't take any arguments
@Serializable
object Home

// Define a profile route that takes an ID
@Serializable
data class Profile(val id: String)

@Serializable
object RouteA

@Serializable
object RouteB

@Serializable
object RouteC

@Serializable
object RouteD

@Serializable
sealed class BottomNavigationRoute {

    @Serializable
    data object DashboardRoute : BottomNavigationRoute()

    @Serializable
    data object HomeRoute : BottomNavigationRoute()

    @Serializable
    data object HomeRoute1 : BottomNavigationRoute()

    @Serializable
    data object HomeRoute2 : BottomNavigationRoute()

    @Serializable
    data object HomeRoute3 : BottomNavigationRoute()

    @Serializable
    data object SettingsRoute : BottomNavigationRoute()

    @Serializable
    data object SettingsRoute1 : BottomNavigationRoute()

    @Serializable
    data object SettingsRoute2 : BottomNavigationRoute()

    @Serializable
    data object SettingsRoute3 : BottomNavigationRoute()

    @Serializable
    data object FavoritesRoute : BottomNavigationRoute()

    @Serializable
    data object NotificationRoute : BottomNavigationRoute()
}

internal fun bottomRouteDataList() = listOf(
    BottomRouteData(
        title = "Home",
        icon = Icons.Default.Home,
        route = BottomNavigationRoute.HomeRoute
    ),
    BottomRouteData(
        title = "Settings",
        icon = Icons.Default.Settings,
        route = BottomNavigationRoute.SettingsRoute
    ),
    BottomRouteData(
        title = "Favorites",
        icon = Icons.Default.Favorite,
        route = BottomNavigationRoute.FavoritesRoute
    ),
    BottomRouteData(
        title = "Notifications",
        icon = Icons.Default.Notifications,
        route = BottomNavigationRoute.NotificationRoute
    )
)

data class BottomRouteData(
    val title: String,
    val icon: ImageVector,
    val route: BottomNavigationRoute,
)
