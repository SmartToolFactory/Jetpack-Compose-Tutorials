package com.smarttoolfactory.tutorial3_1navigation

import android.annotation.SuppressLint
import androidx.compose.animation.AnimatedContentTransitionScope.SlideDirection
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

@Preview
@Composable
fun Tutorial2Screen() {
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
            RouteAScreen(navController)
        }

        composable<RouteB> {
            RouteBScreen(navController)
        }

        composable<RouteC> {
            RouteCScreen(navController)
        }

        composable<RouteD> {
            RouteDScreen(navController)
        }
    }
}

@Composable
private fun RouteAScreen(navController: NavController) {
    RouteScreen(
        modifier = Modifier.background(Color.White),
        title = "RouteAScreen",
        navController = navController
    )
}

@Composable
private fun RouteBScreen(navController: NavController) {
    RouteScreen(
        modifier = Modifier.background(Color.Cyan),
        title = "RouteBScreen",
        navController = navController
    )
}

@Composable
private fun RouteCScreen(navController: NavController) {
    RouteScreen(
        modifier = Modifier.background(Color.Yellow),
        title = "RouteCScreen",
        navController = navController
    )
}

@Composable
private fun RouteDScreen(navController: NavController) {
    RouteScreen(
        modifier = Modifier.background(Color.Green),
        title = "RouteDScreen",
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

    var isSingleTop by rememberSaveable {
        mutableStateOf(false)
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
                popUpToInclusive = it
            }

            Spacer(Modifier.width(8.dp))

            CheckBoxWithText(
                title = "singleTop",
                checked = isSingleTop
            ) {
                isSingleTop = it
            }
        }

        NavigationButton(
            navController = navController,
            title = "RouteA",
            targetRoute = RouteA,
            popUpToRoute = popUpToRoute,
            popUpToInclusive = popUpToInclusive,
            singleTop = isSingleTop
        )

        NavigationButton(
            navController = navController,
            title = "RouteB",
            targetRoute = RouteB,
            popUpToRoute = popUpToRoute,
            popUpToInclusive = popUpToInclusive,
            singleTop = isSingleTop
        )

        NavigationButton(
            navController = navController,
            title = "RouteC",
            targetRoute = RouteC,
            popUpToRoute = popUpToRoute,
            popUpToInclusive = popUpToInclusive,
            singleTop = isSingleTop
        )

        NavigationButton(
            navController = navController,
            title = "RouteD",
            targetRoute = RouteD,
            popUpToRoute = popUpToRoute,
            popUpToInclusive = popUpToInclusive,
            singleTop = isSingleTop
        )

        val currentBackStack: List<NavBackStackEntry> by navController.currentBackStack.collectAsState()

        Column(
            modifier = Modifier.verticalScroll(rememberScrollState())
        ) {
            currentBackStack.reversed().forEach {
                Text(
                    text = it.destination.route ?: it.destination.displayName,
                    modifier = Modifier.fillMaxWidth(),
                    fontSize = 18.sp
                )
            }
        }
    }
}

@Composable
private fun NavigationButton(
    navController: NavController,
    title: String,
    targetRoute: Any,
    popUpToRoute: Any? = null,
    popUpToInclusive: Boolean,
    singleTop: Boolean,
) {
    Button(
        modifier = Modifier.fillMaxWidth(),
        onClick = {
            navController.navigate(
                route = targetRoute
            ) {
                popUpToRoute?.let {
                    popUpTo(
                        route = it
                    ) {
                        inclusive = popUpToInclusive
                    }
                }

                launchSingleTop = singleTop
            }
        }
    ) {
        Text("Navigate to $title")
    }
}

@Composable
private fun CheckBoxWithText(
    modifier: Modifier = Modifier,
    title: String,
    enabled: Boolean = true,
    checked: Boolean,
    onCheckChange: (Boolean) -> Unit,
) {
    Row(
        modifier = modifier
            .clickable(
                enabled = enabled
            ) {
                onCheckChange(checked.not())
            }
            .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Checkbox(checked = checked, onCheckedChange = null, enabled = enabled)
        Spacer(Modifier.width(16.dp))
        Text(title)
    }
}