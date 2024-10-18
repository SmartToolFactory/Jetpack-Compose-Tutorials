@file:OptIn(ExperimentalMaterial3Api::class)

package com.smarttoolfactory.tutorial3_1navigation

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavController
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController

@Preview
@Composable
fun Tutorial3Screen() {
    val navController = rememberNavController()

    NavHost(
        modifier = Modifier.fillMaxSize(),
        navController = navController,
        startDestination = BottomNavigationRoute.DashboardRoute,
    ) {

        composable<BottomNavigationRoute.DashboardRoute> {
            MainContainer { route: BottomNavigationRoute, navBackStackEntry: NavBackStackEntry ->
                // In next tutorial this is implemented to navigate to
                // detail screens from BottomNavigation
            }
        }
    }
}

@Composable
private fun MainContainer(
    onScreenClick: (
        route: BottomNavigationRoute,
        navBackStackEntry: NavBackStackEntry,
    ) -> Unit,
) {

    val items = remember {
        bottomRouteDataList()
    }

    val nestedNavController = rememberNavController()
    val navBackStackEntry: NavBackStackEntry? by nestedNavController.currentBackStackEntryAsState()

    var selectedIndex by remember {
        mutableIntStateOf(0)
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = {
                    Text("TopAppbar")
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.White
                )
            )
        },
        bottomBar = {

            NavigationBar(
                modifier = Modifier.height(56.dp),
                tonalElevation = 4.dp
            ) {
                items.forEachIndexed { index: Int, item: BottomRouteData ->

                    // Checks destination's route with type safety
                    val selected =
                        navBackStackEntry?.destination?.hasRoute(item.route::class) ?: false

                    NavigationBarItem(
                        selected = selected,
                        icon = {
                            Icon(
                                imageVector = item.icon,
                                contentDescription = null
                            )
                        },
                        onClick = {
                            selectedIndex = index

                            // This is for not opening same screen if current destination
                            // is equal to target destination
                            if (selected.not()) {
                                nestedNavController.navigate(
                                    route = item.route
                                ) {
                                    launchSingleTop = true
                                    restoreState = true

                                    // Pop up backstack to the first destination and save state.
                                    // This makes going back
                                    // to the start destination when pressing back in any other bottom tab.
                                    popUpTo(findStartDestination(nestedNavController.graph).id) {
                                        saveState = true
                                    }
                                }
                            }
                        }
                    )
                }
            }
        }
    ) { paddingValues: PaddingValues ->
        NavHost(
            modifier = Modifier.padding(paddingValues),
            navController = nestedNavController,
            startDestination = BottomNavigationRoute.HomeRoute
        ) {
            addHomeGraph(nestedNavController)
        }
    }
}

// 🔥 navController is passed to display back stack in each Composable for demonstration
// It's not recommended and only demonstration purposes
internal fun NavGraphBuilder.addHomeGraph(navController: NavController) {

    composable<BottomNavigationRoute.HomeRoute> { from: NavBackStackEntry ->

        Screen(
            text = "Home Screen",
            navController = navController
        )
    }

    composable<BottomNavigationRoute.SettingsRoute> { from: NavBackStackEntry ->
        Screen(
            text = "Settings Screen",
            navController = navController
        )
    }

    composable<BottomNavigationRoute.FavoritesRoute> { from: NavBackStackEntry ->
        Screen(
            text = "Favorites Screen",
            navController = navController
        )
    }

    composable<BottomNavigationRoute.NotificationRoute> { from: NavBackStackEntry ->
        Screen(
            text = "Notification Screen",
            navController = navController
        )
    }
}

@SuppressLint("RestrictedApi")
@Composable
private fun Screen(
    text: String,
    navController: NavController,
) {

    val packageName = LocalContext.current.packageName

    var counter by rememberSaveable {
        mutableIntStateOf(0)
    }

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = text,
            fontSize = 26.sp,
            fontWeight = FontWeight.Bold
        )

        Button(
            modifier = Modifier.fillMaxWidth(),
            onClick = {
                counter++
            }
        ) {
            Text("Counter: $counter")
        }

        val currentBackStack: List<NavBackStackEntry> by navController.currentBackStack.collectAsState()


        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {

            // Don't do looped operations in actual code, it's for demonstration
            items(items = currentBackStack.reversed()) {
                Text(
                    text = it.destination.route?.replace(
                        "$packageName.BottomNavigationRoute.",
                        ""
                    ) ?: it.destination.displayName,
                    modifier = Modifier
                        .shadow(4.dp, RoundedCornerShape(8.dp))
                        .background(Color.White)
                        .fillMaxWidth()
                        .padding(16.dp),
                    fontSize = 18.sp
                )
            }
        }
    }
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
