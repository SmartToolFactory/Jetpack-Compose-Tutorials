@file:OptIn(ExperimentalMaterial3Api::class)

package com.smarttoolfactory.tutorial3_1navigation

import android.annotation.SuppressLint
import androidx.activity.compose.BackHandler
import androidx.collection.forEach
import androidx.compose.animation.AnimatedContentTransitionScope.SlideDirection
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
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
import androidx.compose.ui.Alignment
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
import androidx.navigation.NavGraph
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.navigation
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute

@SuppressLint("RestrictedApi")
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

@SuppressLint("RestrictedApi")
@Composable
private fun MainContainer(
    onGoToProfileScreen: (
        route: Any,
        navBackStackEntry: NavBackStackEntry,
    ) -> Unit,
) {
    val items = remember {
        bottomNestedRouteDataList()
    }

    val nestedNavController = rememberNavController()
    val navBackStackEntry: NavBackStackEntry? by nestedNavController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    // Other way to set selected bottom navigation item
//    var selectedIndex by remember {
//        mutableIntStateOf(0)
//    }
//
//    currentDestination?.let {
//        selectedIndex =
//            if (currentDestination.hasRoute(BottomNavigationRoute.NotificationRoute::class)) {
//                3
//            } else if (currentDestination.hasRoute(BottomNavigationRoute.FavoritesRoute::class)) {
//                2
//            } else if (currentDestination.hierarchy.any { it.hasRoute(BottomNavigationRoute.SettingsGraph::class) }) {
//                1
//            } else {
//                0
//            }
//    }

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
                items.forEachIndexed { index, item: BottomRouteData ->

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
                            nestedNavController.navigate(route = item.route) {
                                launchSingleTop = true

                                // 🔥 If restoreState = true and saveState = true are commented
                                // routes other than Home1 are not saved
                                restoreState = true

                                // Pop up backstack to the first destination and save state.

                                // Then restore any previous back stack state associated with
                                // the item.route destination.
                                // Finally navigate to the item.route destination.
                                popUpTo(findStartDestination(nestedNavController.graph).id) {
                                    saveState = true
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
            startDestination = BottomNavigationRoute.HomeGraph
        ) {
            addBottomNavigationGraph(
                nestedNavController = nestedNavController,
                onGoToProfileScreen = { route, navBackStackEntry ->
                    onGoToProfileScreen(route, navBackStackEntry)
                },
                onBottomScreenClick = { route, navBackStackEntry ->
                    nestedNavController.navigate(route)
                }
            )
        }
    }
}

@Composable
private fun BackHome(
    navController: NavController,
) {
    BackHandler {
        navController.navigate(BottomNavigationRoute.HomeGraph) {
            launchSingleTop = true
            restoreState = true
            popUpTo(navController.graph.findStartDestination().id) { saveState = true }
        }
    }
}

/**
 * @param onGoToProfileScreen lambda for navigating Profile screen from current screen with top NavHostController
 * @param onBottomScreenClick lambda for navigating with [nestedNavController] in BottomNavigation
 */
private fun NavGraphBuilder.addBottomNavigationGraph(
    nestedNavController: NavHostController,
    onGoToProfileScreen: (route: Any, navBackStackEntry: NavBackStackEntry) -> Unit,
    onBottomScreenClick: (route: Any, navBackStackEntry: NavBackStackEntry) -> Unit,
) {
    navigation<BottomNavigationRoute.HomeGraph>(
        startDestination = BottomNavigationRoute.HomeRoute1
    ) {
        composable<BottomNavigationRoute.HomeRoute1> { from: NavBackStackEntry ->
            Screen(
                text = "Home Screen1",
                navController = nestedNavController,
                onClick = {
                    onBottomScreenClick(BottomNavigationRoute.HomeRoute2, from)
                }
            )
        }

        composable<BottomNavigationRoute.HomeRoute2> { from: NavBackStackEntry ->
            Screen(
                text = "Home Screen2",
                navController = nestedNavController,
                onClick = {
                    onBottomScreenClick(BottomNavigationRoute.HomeRoute3, from)
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

    navigation<BottomNavigationRoute.SettingsGraph>(
        startDestination = BottomNavigationRoute.SettingsRoute1
    ) {
        composable<BottomNavigationRoute.SettingsRoute1> { from: NavBackStackEntry ->
            BackHome(navController = nestedNavController)
            Screen(
                text = "Settings Screen",
                navController = nestedNavController,
                onClick = {
                    onBottomScreenClick(BottomNavigationRoute.SettingsRoute2, from)
                }
            )
        }

        composable<BottomNavigationRoute.SettingsRoute2> { from: NavBackStackEntry ->
            Screen(
                text = "Settings Screen2",
                navController = nestedNavController,
                onClick = {
                    onBottomScreenClick(BottomNavigationRoute.SettingsRoute3, from)
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
        BackHome(navController = nestedNavController)
        Screen(
            text = "Favorites Screen",
            navController = nestedNavController,
            onClick = {
                onGoToProfileScreen(
                    Profile("Favorites"),
                    from
                )
            }
        )
    }

    composable<BottomNavigationRoute.NotificationRoute> { from: NavBackStackEntry ->
        BackHome(navController = nestedNavController)
        Screen(
            text = "Notifications Screen",
            navController = nestedNavController,
            onClick = {
                onGoToProfileScreen(
                    Profile("Notifications"),
                    from
                )
            }
        )
    }
}

@SuppressLint("RestrictedApi")
@Composable
fun Screen(
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

        val pagerState = rememberPagerState {
            2
        }
        HorizontalPager(state = pagerState) { page ->

            val headerText = if (page == 0) "Current Back stack(reversed)" else "Current hierarchy"
            Column {
                Text(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp),
                    text = headerText,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )

                val destinations = if (page == 0) {
                    currentBackStack.reversed().map { it.destination }
                } else {
                    navController.currentDestination?.hierarchy?.toList() ?: listOf()
                }

                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {

                    items(items = destinations) { destination: NavDestination ->

                        if (destination is NavGraph) {
                            MainText(destination, packageName)
                            destination.nodes.forEach { _, value ->
                                SubItemText(value, packageName)
                            }

                        } else {
                            MainText(destination, packageName)
                        }
                    }
                }

            }
        }
    }
}

@Composable
internal fun SubItemText(destination: NavDestination, packageName: String?) {
    Row(
        modifier = Modifier
            .padding(start = 8.dp, bottom = 2.dp)
            .shadow(2.dp, RoundedCornerShape(8.dp))
            .background(Color.White)
            .fillMaxWidth()
            .padding(8.dp),
        verticalAlignment = Alignment.Bottom
    ) {

        Text(
            text = getDestinationFormattedText(
                destination,
                packageName
            ),
            fontSize = 12.sp
        )

//        destination.parent?.let {
//            Text(
//                text = ", parent: " + getGraphFormattedText(it, packageName),
//                fontSize = 10.sp
//            )
//        }
    }
}

@Composable
internal fun MainText(destination: NavDestination, packageName: String?) {


    Row(
        modifier = Modifier
            .shadow(4.dp, RoundedCornerShape(8.dp))
            .background(Color.White)
            .fillMaxWidth()
            .padding(16.dp),
        verticalAlignment = Alignment.Bottom
    ) {
        Text(
            text = getDestinationFormattedText(
                destination,
                packageName
            ),
            fontSize = 18.sp
        )
//        destination.parent?.let {
//            Text(
//                text = ", parent: " + getGraphFormattedText(it, packageName),
//                fontSize = 14.sp
//            )
//        }
    }
}

@SuppressLint("RestrictedApi")
private fun getDestinationFormattedText(
    destination: NavDestination,
    packageName: String?,
) = (destination.route
    ?.replace("$packageName.", "")
    ?.replace("BottomNavigationRoute.", "")
    ?: (destination.displayName))

@SuppressLint("RestrictedApi")
private fun getGraphFormattedText(
    destination: NavGraph,
    packageName: String?,
) = (destination.route
    ?.replace("$packageName.", "")
    ?.replace("BottomNavigationRoute.", "")
    ?: (destination.displayName))
