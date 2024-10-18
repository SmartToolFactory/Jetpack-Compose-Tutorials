@file:OptIn(ExperimentalMaterial3Api::class)

package com.smarttoolfactory.tutorial3_1navigation

import android.annotation.SuppressLint
import androidx.compose.animation.AnimatedContentTransitionScope.SlideDirection
import androidx.compose.animation.core.tween
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
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.navigation
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute

@Preview
@Composable
fun Tutorial3_2Screen() {
    val navController = rememberNavController()

    NavHost(
        modifier = Modifier.fillMaxSize(),
        navController = navController,
        startDestination = BottomNavigationRoute.DashboardRoute,
        enterTransition = {
            slideIntoContainer(
                towards = SlideDirection.Start,
                animationSpec = tween(700)
            )
        },
        exitTransition = {
            slideOutOfContainer(
                towards = SlideDirection.End,
                animationSpec = tween(700)
            )
        },
        popEnterTransition = {
            slideIntoContainer(
                towards = SlideDirection.Start,
                animationSpec = tween(700)
            )
        },
        popExitTransition = {
            slideOutOfContainer(
                towards = SlideDirection.End,
                animationSpec = tween(700)
            )
        }
    ) {

        composable<BottomNavigationRoute.DashboardRoute> {
            MainContainer { route: Any, navBackStackEntry: NavBackStackEntry ->
                // Navigate only when life cycle is resumed for current screen
                if (navBackStackEntry.lifecycleIsResumed()) {
                    navController.navigate(route = route)
                }
            }
        }

        composable<Profile> { navBackStackEntry: NavBackStackEntry ->
            val profile: Profile = navBackStackEntry.toRoute<Profile>()
            Screen(profile.toString(), navController)
        }
    }
}

@Composable
private fun MainContainer(
    onScreenClick: (
        route: Any,
        navBackStackEntry: NavBackStackEntry,
    ) -> Unit,
) {
    val items = remember {
        bottomRouteDataList()
    }

    val nestedNavController = rememberNavController()

    val navBackStackEntry: NavBackStackEntry? by nestedNavController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

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
                items.forEach { item: BottomRouteData ->

                    // Checks destination's route with type safety
                    val selected =
                        currentDestination?.hierarchy?.any { it.hasRoute(item.route::class) } == true

                    NavigationBarItem(
                        selected = selected,
                        icon = {
                            Icon(
                                imageVector = item.icon,
                                contentDescription = null
                            )
                        },
                        onClick = {

                            // This is for not opening same screen if current destination
                            // is equal to target destination
                            if (selected.not()) {

                                // Returns current destinations by parent-child relationship
                                currentDestination?.hierarchy?.forEach { destination: NavDestination ->
                                    println("HIERARCHY: destination: $destination")
                                }

                                nestedNavController.navigate(route = item.route) {
                                    launchSingleTop = true
                                    restoreState = true

                                    // Pop up backstack to the first destination and save state.
                                    // This makes going back
                                    // to the start destination when pressing back in any other bottom tab.
                                    popUpTo(findStartDestination(nestedNavController.graph).id) {
                                        saveState = true
                                    }

                                    val startDestinationRoute =
                                        nestedNavController.graph.startDestinationRoute
                                    val startDestinationRecursive =
                                        findStartDestination(nestedNavController.graph).route
                                    println(
                                        "🔥 startDestinationRoute: $startDestinationRoute, " +
                                                "startDestinationRecursive: $startDestinationRecursive\n" +
                                                "navigating target route: ${item.route}"
                                    )
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
            addBottomNavigationGraph(nestedNavController) { route, navBackStackEntry ->
                onScreenClick(route, navBackStackEntry)
            }
        }
    }
}

private fun NavGraphBuilder.addBottomNavigationGraph(
    nestedNavController: NavHostController,
    onScreenClick: (route: Any, navBackStackEntry: NavBackStackEntry) -> Unit,
) {
    navigation<BottomNavigationRoute.HomeRoute>(
        startDestination = BottomNavigationRoute.HomeRoute1
    ) {
        composable<BottomNavigationRoute.HomeRoute1> { from: NavBackStackEntry ->
            Screen(
                text = "Home Screen1",
                navController = nestedNavController,
                onClick = {
                    nestedNavController.navigate(BottomNavigationRoute.HomeRoute2)
                }
            )
        }

        composable<BottomNavigationRoute.HomeRoute2> { from: NavBackStackEntry ->
            Screen(
                text = "Home Screen2",
                navController = nestedNavController,
                onClick = {
                    nestedNavController.navigate(BottomNavigationRoute.HomeRoute3)
                }
            )
        }

        composable<BottomNavigationRoute.HomeRoute3> { from: NavBackStackEntry ->
            Screen(
                text = "Home Screen3",
                navController = nestedNavController
            )
        }
    }

    navigation<BottomNavigationRoute.SettingsRoute>(
        startDestination = BottomNavigationRoute.SettingsRoute1
    ) {
        composable<BottomNavigationRoute.SettingsRoute1> { from: NavBackStackEntry ->
            Screen(
                text = "Settings Screen",
                navController = nestedNavController,
                onClick = {
                    nestedNavController.navigate(BottomNavigationRoute.SettingsRoute2)
                }
            )
        }

        composable<BottomNavigationRoute.SettingsRoute2> { from: NavBackStackEntry ->
            Screen(
                text = "Settings Screen2",
                navController = nestedNavController,
                onClick = {
                    nestedNavController.navigate(BottomNavigationRoute.SettingsRoute3)
                }
            )
        }

        composable<BottomNavigationRoute.SettingsRoute3> { from: NavBackStackEntry ->
            Screen(
                text = "Settings Screen3",
                navController = nestedNavController
            )
        }
    }

    composable<BottomNavigationRoute.FavoritesRoute> { from: NavBackStackEntry ->
        Screen(
            text = "Favorites Screen",
            navController = nestedNavController,
            onClick = {
                onScreenClick(
                    Profile("Favorites"),
                    from
                )
            }
        )
    }

    composable<BottomNavigationRoute.NotificationRoute> { from: NavBackStackEntry ->
        Screen(
            text = "Notifications Screen",
            navController = nestedNavController,
            onClick = {
                onScreenClick(
                    Profile("Notifications"),
                    from
                )
            }
        )
    }
}

@SuppressLint("RestrictedApi")
@Composable
private fun Screen(
    text: String,
    navController: NavController,
    onClick: (() -> Unit)? = null,
) {

    val packageName = LocalContext.current.packageName

    var counter by rememberSaveable {
        mutableIntStateOf(0)
    }

    Column(
        modifier = Modifier
            .background(MaterialTheme.colorScheme.surface)
            .fillMaxSize()
            .padding(16.dp),
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

        onClick?.let {
            Button(
                modifier = Modifier.fillMaxWidth(),
                onClick = {
                    onClick()
                }
            ) {
                Text("Navigate next screen")
            }
        }

        val currentBackStack: List<NavBackStackEntry> by navController.currentBackStack.collectAsState()

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {

            // Don't do looped operations in actual code, it's for demonstration
            items(items = currentBackStack.reversed()) {
                Text(
                    text = it.destination.route
                        ?.replace("$packageName.", "")
                        ?.replace(
                            "BottomNavigationRoute.",
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