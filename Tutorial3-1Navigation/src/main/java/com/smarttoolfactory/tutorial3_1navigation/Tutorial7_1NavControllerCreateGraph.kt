package com.smarttoolfactory.tutorial3_1navigation

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavController
import androidx.navigation.NavGraph
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.createGraph

@Preview
@Composable
fun Tutorial7_1Screen() {
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

    NavHost(
        modifier = Modifier.fillMaxSize(),
        navController = navController,
        graph = navGraph,
    )

    // Other Alternative
//        NavHost(
//            modifier = Modifier.fillMaxSize(),
//            navController = navController,
//            startDestination = RouteA
//        ) {
//            addNavGraph(navController)
//        }
}

private fun NavGraphBuilder.addNavGraph(navController: NavController) {

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
