package com.smarttoolfactory.tutorial3_1navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Settings
import androidx.compose.ui.graphics.vector.ImageVector
import kotlinx.serialization.Serializable


@Serializable
object Splash

@Serializable
object RegisterGraph

// Define a home route that doesn't take any arguments
@Serializable
object Home

@Serializable
object HomeGraph

@Serializable
object ProfileGraph

// Define a profile route that takes an ID
@Serializable
data class Profile(val id: String)

// Parameters with default values are query params
// parameter with no default values are uri params
@Serializable
data class Product(
    val id: String,
    val count: Int = 0,
    val type: String = "default",
)


const val uri = "test://www.example.com"

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
    data object HomeGraph : BottomNavigationRoute()

    @Serializable
    data object HomeRoute1 : BottomNavigationRoute()

    @Serializable
    data object HomeRoute2 : BottomNavigationRoute()

    @Serializable
    data object HomeRoute3 : BottomNavigationRoute()

    @Serializable
    data object SettingsGraph : BottomNavigationRoute()

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
        route = BottomNavigationRoute.HomeRoute1
    ),
    BottomRouteData(
        title = "Settings",
        icon = Icons.Default.Settings,
        route = BottomNavigationRoute.SettingsRoute1
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

internal fun bottomNestedRouteDataList() = listOf(
    BottomRouteData(
        title = "Home",
        icon = Icons.Default.Home,
        route = BottomNavigationRoute.HomeGraph
    ),
    BottomRouteData(
        title = "Settings",
        icon = Icons.Default.Settings,
        route = BottomNavigationRoute.SettingsGraph
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

internal fun bottomNestedRouteDataList2() = listOf(
    BottomRouteData(
        title = "Home",
        icon = Icons.Default.Home,
        route = BottomNavigationRoute.HomeGraph
    ),
    BottomRouteData(
        title = "Settings",
        icon = Icons.Default.Settings,
        route = BottomNavigationRoute.SettingsGraph
    ),
    BottomRouteData(
        title = "Accounts",
        icon = Icons.Default.AccountCircle,
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
