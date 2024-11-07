package com.smarttoolfactory.tutorial3_1navigation

import android.annotation.SuppressLint
import androidx.compose.animation.AnimatedContentTransitionScope.SlideDirection
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
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
import androidx.navigation.NavGraph
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

@Preview
@Composable
fun Tutorial3_1Screen() {

    /*
        In this example use saveState to save back stack state
        with a popUpTo destination and restore when that destination is navigated.

        If you navigate from C->D with popUpTo(B) back stack is saved with
        B key. Then if you go from D to B that state is restored
        if restoreState is true
     */
    val navController = rememberNavController()
    NavHost(
        modifier = Modifier.fillMaxSize(),
        navController = navController,
        startDestination = RouteA,
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
        composable<RouteA> {
            DestAScreen(navController)
        }

        composable<RouteB> {
            DestBScreen(navController)
        }

        composable<RouteC> {
            DestCScreen(navController)
        }

        composable<RouteD> {
            DestDScreen(navController)
        }
    }
}

@Composable
private fun DestAScreen(navController: NavController) {
    RouteScreen(
        modifier = Modifier.background(Color.White),
        title = "DestAScreen",
        navController = navController
    )
}

@Composable
internal fun DestBScreen(navController: NavController) {
    RouteScreen(
        modifier = Modifier.background(Color.Cyan),
        title = "DestBScreen",
        navController = navController
    )
}

@Composable
internal fun DestCScreen(navController: NavController) {
    RouteScreen(
        modifier = Modifier.background(Color.Yellow),
        title = "DestCScreen",
        navController = navController
    )
}

@Composable
internal fun DestDScreen(navController: NavController) {
    RouteScreen(
        modifier = Modifier.background(Color.Green),
        title = "DestDScreen",
        navController = navController
    )
}


@SuppressLint("RestrictedApi")
@Composable
private fun RouteScreen(
    modifier: Modifier = Modifier,
    title: String,
    navController: NavController,
) {

    var popUpToRoute by remember { mutableStateOf<Any?>(null) }

    var popUpToInclusive by rememberSaveable {
        mutableStateOf(false)
    }

    var saveState by rememberSaveable {
        mutableStateOf(false)
    }

    var restoreState by rememberSaveable {
        mutableStateOf(false)
    }

    var isSingleTop by rememberSaveable {
        mutableStateOf(false)
    }

    var counter by rememberSaveable {
        mutableIntStateOf(0)
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = title,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold
        )

        ExposedSelectionMenu(title = "PopUpTo",
            index = when (popUpToRoute) {
                null -> 0
                RouteA -> 1
                RouteB -> 2
                RouteC -> 3
                else -> 4
            },
            options = listOf("no popUpTo", "RouteA", "RouteB", "RouteC", "RouteD"),
            onSelected = {
                popUpToRoute = when (it) {
                    0 -> null
                    1 -> RouteA
                    2 -> RouteB
                    3 -> RouteC
                    else -> RouteD
                }
            }
        )

        Row {
            CheckBoxWithText(
                title = "popUpToInclusive",
                enabled = popUpToRoute != null,
                checked = popUpToInclusive
            ) {
                popUpToInclusive = it && popUpToRoute != null
            }

            CheckBoxWithText(
                title = "singleTop",
                checked = isSingleTop
            ) {
                isSingleTop = it
            }

        }

        Spacer(Modifier.width(8.dp))

        Row {

            CheckBoxWithText(
                title = "saveState",
                enabled = popUpToRoute != null,
                checked = saveState
            ) {
                saveState = it && popUpToRoute != null
            }

            Spacer(Modifier.width(8.dp))
            CheckBoxWithText(
                title = "restoreState",
                checked = restoreState
            ) {
                restoreState = it
            }
        }

        Button(
            modifier = Modifier.fillMaxWidth(),
            onClick = {
                counter++
            }
        ) {
            Text("Counter: $counter")
        }

        Row {
            NavigationButton(
                navController = navController,
                title = "RouteA",
                targetRoute = RouteA,
                popUpToRoute = popUpToRoute,
                popUpToInclusive = popUpToInclusive,
                singleTop = isSingleTop,
                saveState = saveState,
                restoreState = restoreState
            )

            Spacer(Modifier.width(8.dp))

            NavigationButton(
                navController = navController,
                title = "RouteB",
                targetRoute = RouteB,
                popUpToRoute = popUpToRoute,
                popUpToInclusive = popUpToInclusive,
                singleTop = isSingleTop,
                saveState = saveState,
                restoreState = restoreState
            )

        }

        Row {
            NavigationButton(
                navController = navController,
                title = "RouteC",
                targetRoute = RouteC,
                popUpToRoute = popUpToRoute,
                popUpToInclusive = popUpToInclusive,
                singleTop = isSingleTop,
                saveState = saveState,
                restoreState = restoreState
            )

            Spacer(Modifier.width(8.dp))

            NavigationButton(
                navController = navController,
                title = "RouteD",
                targetRoute = RouteD,
                popUpToRoute = popUpToRoute,
                popUpToInclusive = popUpToInclusive,
                singleTop = isSingleTop,
                saveState = saveState,
                restoreState = restoreState
            )
        }

        val currentBackStack: List<NavBackStackEntry> by navController.currentBackStack.collectAsState()
        val packageName = LocalContext.current.packageName

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {

            // Don't do looped operations in actual code, it's for demonstration
            items(items = currentBackStack.reversed()) {
                Row(
                    modifier = Modifier
                        .shadow(4.dp, RoundedCornerShape(8.dp))
                        .background(Color.White)
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Text(
                        modifier = Modifier.alignByBaseline(),
                        text = it.destination.route
                            ?.replace("$packageName.", "")
                            ?.replace(
                                "BottomNavigationRoute.",
                                ""
                            ) ?: it.destination.displayName,
                        fontSize = 16.sp
                    )

                    val text = if (it.destination is NavGraph) "NavGraph" else "NavDestination"

                    Spacer(Modifier.width(4.dp))
                    Text(
                        modifier = Modifier.alignByBaseline(),
                        text = "($text)",
                        fontSize = 10.sp
                    )
                }
            }
        }
    }
}

@Composable
private fun RowScope.NavigationButton(
    modifier: Modifier = Modifier,
    navController: NavController,
    title: String,
    targetRoute: Any,
    popUpToRoute: Any? = null,
    popUpToInclusive: Boolean,
    singleTop: Boolean,
    saveState: Boolean,
    restoreState: Boolean,
) {
    Button(
        modifier = modifier.weight(1f),
        onClick = {
            navController.navigate(
                route = targetRoute
            ) {
                this.restoreState = restoreState

                popUpToRoute?.let {
                    popUpTo(
                        route = it
                    ) {
                        inclusive = popUpToInclusive
                        this.saveState = saveState
                    }
                }

                launchSingleTop = singleTop
            }
        }
    ) {
        Text("Navigate to $title")
    }
}
