package com.smarttoolfactory.tutorial3_1navigation

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
