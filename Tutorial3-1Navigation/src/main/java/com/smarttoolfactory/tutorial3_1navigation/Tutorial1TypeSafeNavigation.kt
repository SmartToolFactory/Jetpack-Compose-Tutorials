package com.smarttoolfactory.tutorial3_1navigation

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavBackStackEntry
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute

@Composable
fun Tutorial1Screen() {

    val navController = rememberNavController()
    NavHost(
        navController = navController,
        startDestination = Home
    ) {

        composable<Home> {
            HomeScreen {
                navController.navigate(
                    route = it
                )
            }
        }

        composable<Profile> { navBackStackEntry: NavBackStackEntry ->
            val profile: Profile = navBackStackEntry.toRoute<Profile>()
            ProfileScreen(profile)
        }
    }
}

@Composable
private fun HomeScreen(
    onClick: (Profile) -> Unit,
) {
    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        val list = remember {
            List(20) {
                Profile(it.toString())
            }
        }

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(list) {
                Row(
                    modifier = Modifier.fillMaxWidth().clickable {
                        onClick(it)
                    }
                ) {
                    Text("Profile ${it.id}")
                }
            }
        }
    }
}

@Composable
private fun ProfileScreen(profile: Profile) {
    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        Text("Profile: ${profile.id}")
    }
}
