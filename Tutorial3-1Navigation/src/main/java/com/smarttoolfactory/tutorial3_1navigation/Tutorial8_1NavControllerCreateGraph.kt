package com.smarttoolfactory.tutorial3_1navigation

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavGraph
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.createGraph
import androidx.navigation.navDeepLink

@Preview
@Composable
fun Tutorial8_1Screen() {
    MainContainer()
}

@Composable
private fun MainContainer() {
    val navController = rememberNavController()
    // Changing this destination changes where graph will start
    val startDestination = RouteA

    // This NavGraph will be used with NavHost this navHostController will be assigned
    val navGraph: NavGraph = remember(startDestination) {
        navController.createGraph(
            startDestination = startDestination
        ) {
            addNavGraph(navController)
        }
    }

    Column {
        Button(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            onClick = {
                // 🔥This doesn't work
//                navController.setGraph(
//                    graph = navController.createGraph(startDestination = RouteC) {
//                        addNavGraph(navController)
//                    }, startDestinationArgs = null
//                )

                // 🔥 This also doesn't work
                navController.graph.setStartDestination(RouteD)
                println("Start destination: ${navController.graph.startDestinationRoute}")

            }
        ) {
            Text("Set graph")
        }

        NavHost(
            modifier = Modifier.fillMaxSize(),
            navController = navController,
            graph = navGraph,
        )
    }
}

private fun NavGraphBuilder.addNavGraph(navController: NavController) {

    composable<RouteA> {
        RouteAScreen(navController)
    }

    composable<RouteB> {
        RouteBScreen(navController)
    }

    composable<RouteC>(
        deepLinks = listOf(
            navDeepLink<RouteC>(basePath = "$uri/routeC")
        )
    ) {
        RouteCScreen(navController)
    }

    composable<RouteD>(
        deepLinks = listOf(
            navDeepLink<RouteC>(basePath = "$uri/routeD")
        )
    ) {
        RouteDScreen(navController)
    }
}