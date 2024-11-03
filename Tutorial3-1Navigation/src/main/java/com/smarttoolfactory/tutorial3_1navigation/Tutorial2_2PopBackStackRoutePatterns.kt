@file:OptIn(ExperimentalMaterial3Api::class)

package com.smarttoolfactory.tutorial3_1navigation

import android.annotation.SuppressLint
import androidx.compose.animation.AnimatedContentTransitionScope.SlideDirection
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.sharp.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
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
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavGraph
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute

@Preview
@Composable
fun Tutorial2_2Screen() {
    /*
        In this example popUpBackStack navigates on destination back or
        screen with Profile(id) by using navController.popBackStack(selectedProfile, false)
     */

    val navController = rememberNavController()
    NavHost(
        modifier = Modifier.fillMaxSize(),
        navController = navController,
        startDestination = Profile("Start"),
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
        },
    ) {
        composable<Profile> { navBackStackEntry: NavBackStackEntry ->
            val profile = navBackStackEntry.toRoute<Profile>()

            println("Profile: $profile")

            RouteScreen(
                profile = profile,
                navController = navController
            )
        }
    }
}

@SuppressLint("RestrictedApi")
@Composable
private fun RouteScreen(
    modifier: Modifier = Modifier,
    profile: Profile,
    navController: NavController,
) {

    var text by rememberSaveable {
        mutableStateOf(profile.id)
    }

    val selectedProfile by remember {
        derivedStateOf {
            Profile(text)
        }
    }

    var popUpToRoute by rememberSaveable { mutableStateOf<Any?>(null) }

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
        TopAppBar(
            modifier = Modifier.fillMaxWidth(),
            title = {
                Text(
                    text = profile.id,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
            },
            navigationIcon = {
                IconButton(
                    onClick = {
                        // 🔥🔥If Profile with id exists in back stack navigates back to it
                        navController.popBackStack(
                            route = selectedProfile,
                            inclusive = false
                        )
                    }
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Sharp.ArrowBack,
                        contentDescription = null
                    )
                }
            }
        )

        OutlinedTextField(
            modifier = Modifier.fillMaxWidth(),
            value = text,
            label = {
                Text("Destination")
            },
            onValueChange = { text = it }
        )

        ExposedSelectionMenu(title = "navigate PopUpTo",
            index = when (popUpToRoute) {
                null -> 0
                else -> 1
            },
            options = listOf("no popUpTo", "Profile"),
            onSelected = {
                popUpToRoute = when (it) {
                    0 -> null
                    else -> selectedProfile
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

        Spacer(Modifier.width(16.dp))

        Button(
            modifier = Modifier.fillMaxWidth(),
            onClick = {
                /*
                 🔥Attempts to pop the controller's back stack. Analogous to when the user presses
                 the system Back button when the associated navigation host has focus.
                 */
                navController.popBackStack()
            }
        ) {
            Text("Pop Back Stack")
        }

        Spacer(Modifier.width(16.dp))

        Button(
            modifier = Modifier.fillMaxWidth(),
            onClick = {
                navController.navigate(
                    route = selectedProfile
                ) {
                    popUpToRoute?.let {
                        popUpTo(
                            route = it
                        ) {
                            inclusive = popUpToInclusive
                        }
                    }

                    launchSingleTop = isSingleTop
                }
            }
        ) {
            Text("Navigate $selectedProfile")
        }

        val currentBackStack: List<NavBackStackEntry> by navController.currentBackStack.collectAsState()
        val packageName = LocalContext.current.packageName

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {

            // Don't do looped operations in actual code, it's for demonstration
            items(items = currentBackStack.reversed()) { backStackEntry ->
                Row(
                    modifier = Modifier
                        .shadow(4.dp, RoundedCornerShape(8.dp))
                        .background(Color.White)
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {

                    Text(
                        modifier = Modifier.alignByBaseline(),
                        text = backStackEntry.destination.route
                            ?.replace("$packageName.", "")
                            ?.replace(
                                "BottomNavigationRoute.",
                                ""
                            ) ?: backStackEntry.destination.displayName,
                        fontSize = 16.sp
                    )

                    val destinationText =
                        if (backStackEntry.destination is NavGraph) "NavGraph" else "NavDestination"

                    val id =
                        if (backStackEntry.destination.hasRoute(Profile::class) &&
                            backStackEntry.destination !is NavGraph
                        ) {
                            "id=" + backStackEntry.toRoute<Profile>().id
                        } else ""

                    Text(
                        modifier = Modifier.alignByBaseline(),
                        text = id,
                        fontSize = 12.sp
                    )
                    Spacer(Modifier.width(4.dp))
                    Text(
                        modifier = Modifier.alignByBaseline(),
                        text = "($destinationText)",
                        fontSize = 10.sp
                    )
                }
            }
        }
    }
}
