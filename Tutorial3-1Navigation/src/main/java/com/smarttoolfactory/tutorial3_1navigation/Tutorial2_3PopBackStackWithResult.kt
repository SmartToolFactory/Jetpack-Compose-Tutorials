package com.smarttoolfactory.tutorial3_1navigation

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.dropUnlessResumed
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import kotlinx.serialization.Serializable


sealed class MyNavigation {
    @Serializable
    data class Home(
        val id: Int,
    ) : MyNavigation()

    @Serializable
    data object Detail : MyNavigation()

    @Serializable
    data object Final : MyNavigation()
}

@SuppressLint("RestrictedApi")
@Preview
@Composable
fun PopUpBackStackWithResultSample() {
    val navController = rememberNavController()

    NavHost(
        modifier = Modifier.fillMaxSize(),
        navController = navController,
        startDestination = MyNavigation.Home(-1)
    ) {
        composable<MyNavigation.Home> { navBackstackEntry: NavBackStackEntry ->
            val id = navBackstackEntry.savedStateHandle.get<Int>("id") ?: 0

            HomeScreen(
                id = id,
                onNavigateToDetail = { navController.navigate(MyNavigation.Detail) }
            )
        }

        composable<MyNavigation.Detail> {
            DetailScreen(
                onNavigateToFinal = { navController.navigate(MyNavigation.Final) }
            )
        }

        composable<MyNavigation.Final> {

            val backStack by navController.currentBackStack.collectAsState()

            FinalScreen(
                onNavigateBackToHome = { newId ->
                    val startId = navController.graph.findStartDestination().id
                    navController.getBackStackEntry(startId).savedStateHandle["id"] = newId
                    navController.popBackStack(
                        destinationId = startId,
                        inclusive = false
                    )

                    // Alternative for getting NavDestination
                    /*                    val firstDestination: NavDestination? = backStack.firstOrNull {
                                            it.destination.hasRoute(MyNavigation.Home::class)
                                        }?.destination

                                        firstDestination?.let {
                                            val startId = firstDestination.id

                                            navController.getBackStackEntry(startId).savedStateHandle["id"] = newId

                                            navController.popBackStack(
                                                destinationId = startId,
                                                inclusive = false
                                            )
                                        }*/
                }
            )
        }
    }
}

@Composable
private fun HomeScreen(id: Int, onNavigateToDetail: () -> Unit) {

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),

        ) {
        Text("Home Screen Id: $id", fontSize = 32.sp)

        Spacer(Modifier.weight(1f))
        Button(
            modifier = Modifier.fillMaxWidth(),
            onClick = onNavigateToDetail
        ) {
            Text("Navigate to detail")
        }
    }
}

@Composable
private fun DetailScreen(onNavigateToFinal: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp)
    ) {

        Text("Detail Screen", fontSize = 32.sp)
        Spacer(Modifier.weight(1f))
        Button(
            modifier = Modifier.fillMaxWidth(),
            onClick = onNavigateToFinal
        ) {
            Text("Navigate to Final")
        }
    }
}

@Composable
private fun FinalScreen(onNavigateBackToHome: (Int) -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp)
    ) {

        var newId by remember {
            mutableIntStateOf(0)
        }
        Text("Final Screen", fontSize = 32.sp)

        Spacer(Modifier.height(60.dp))

        OutlinedTextField(
            modifier = Modifier.fillMaxWidth(),
            value = newId.toString(),
            onValueChange = { text ->
                text.toIntOrNull()?.let {
                    newId = it
                }
            },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.NumberPassword
            )
        )
        Spacer(Modifier.weight(1f))

        Button(
            modifier = Modifier.fillMaxWidth(),
            onClick = dropUnlessResumed {
                onNavigateBackToHome(newId)
            }
        ) {
            Text("Popup back to Home")
        }
    }
}
