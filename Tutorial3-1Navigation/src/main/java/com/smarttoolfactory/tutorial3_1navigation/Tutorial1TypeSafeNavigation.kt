package com.smarttoolfactory.tutorial3_1navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.dropUnlessResumed
import androidx.navigation.NavBackStackEntry
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute

@Preview
@Composable
fun Tutorial1Screen() {

    /*
        In this example composable<Home> is used for Routes
        for type safe navigation

        To navigate call  navController.navigate(route = profile)
        And in destination call val profile: Profile = navBackStackEntry.toRoute<Profile>()
        and pass Data to ProfileScreen
     */

    val navController = rememberNavController()
    NavHost(
        navController = navController,
        startDestination = Home
    ) {

        composable<Home> { navBackStackEntry: NavBackStackEntry ->
            val destination = navBackStackEntry.destination

            println(
                "HOME\n" +
                        "navBackStackEntry: $navBackStackEntry\n" +
                        "arguments: ${navBackStackEntry.arguments}"
            )

            println(
                "Destination: route:${destination.route}, " +
                        "navigatorName: ${destination.navigatorName}, " +
                        "label: ${destination.label}"
            )
            /*
                Prints:
I  HOME
I  navBackStackEntry: NavBackStackEntry(8b76d2f8-37d9-4678-b453-defeae29ad70) destination=Destination(0x1e9b63ca) route=com.smarttoolfactory.tutorial3_1navigation.Home
I  arguments: null
             */

            HomeScreen { profile: Profile ->
                navController.navigate(route = profile)
            }
        }

        composable<Profile> { navBackStackEntry: NavBackStackEntry ->
            val destination = navBackStackEntry.destination

            println(
                "PROFILE\n" +
                        "navBackStackEntry: $navBackStackEntry\n" +
                        "arguments: ${navBackStackEntry.arguments}"
            )

            println(
                "Destination: route:${destination.route}, " +
                        "navigatorName: ${destination.navigatorName}, " +
                        "label: ${destination.label}"
            )
            /*
                 Prints:
 I  PROFILE
 I  navBackStackEntry: NavBackStackEntry(61b502de-e4db-4c1d-bf2c-5b4a62b759c6) destination=Destination(0xb8b5b9fd) route=com.smarttoolfactory.tutorial3_1navigation.Profile/{id}
 I  arguments: Bundle[{android-support-nav:controller:deepLinkIntent=Intent { dat=android-app://androidx.navigation/com.smarttoolfactory.tutorial3_1navigation.Profile/{id} }, id=9}]
 */
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
            verticalArrangement = Arrangement.spacedBy(8.dp),
            contentPadding = PaddingValues(16.dp)
        ) {
            items(list) {
                Row(
                    modifier = Modifier.fillMaxWidth()
                        .shadow(4.dp, RoundedCornerShape(16.dp))
                        .background(Color.White)
                        .clickable(
                            onClick = dropUnlessResumed {
                                onClick(it)
                            }
                        )
                        .padding(16.dp)
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
        modifier = Modifier.fillMaxSize().padding(16.dp)
    ) {
        Text(
            text = "Profile: ${profile.id}",
            fontSize = 40.sp,
            fontWeight = FontWeight.Bold
        )
    }
}
