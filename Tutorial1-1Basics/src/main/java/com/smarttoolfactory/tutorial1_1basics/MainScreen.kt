package com.smarttoolfactory.tutorial1_1basics

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Scaffold
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.*
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.smarttoolfactory.tutorial1_1basics.model.TutorialSectionModel
import com.smarttoolfactory.tutorial1_1basics.model.createTutorialList
import com.smarttoolfactory.tutorial1_1basics.ui.components.TutorialSectionCard

@ExperimentalAnimationApi
@ExperimentalComposeUiApi
@ExperimentalFoundationApi
@Composable
fun MainScreen() {

    // NavController for navigating between Composable Screens
    val navController = rememberNavController()

    // Create Tutorial List
    val list: SnapshotStateList<TutorialSectionModel> =
        createTutorialList {
            navController.navigateUp()
        }.toMutableStateList()

    val tutorialList: SnapshotStateList<TutorialSectionModel> = remember { list }

    // Create Navigation for each Composable Page
    NavHost(
        navController = navController,
        startDestination = "start_destination"
    ) {

        composable(route = "start_destination") { navBackEntryStack ->
            TutorialComponent(tutorialList, navController)
        }

        // Set navigation route as title of tutorial card
        // and invoke @Composable inside lambda of this card.
        tutorialList.forEach { model ->
            composable(route = model.title) { navBackEntryStack ->
                model.action?.invoke()
            }
        }
    }
}

/**
 * This function contains [Scaffold] which implements
 * the basic material design visual layout structure, and [LazyColumn] which
 * is ```RecyclerView``` counterpart in compose.
 */
@ExperimentalAnimationApi
@Composable
private fun TutorialComponent(
    tutorialList: List<TutorialSectionModel>,
    navController: NavHostController
) {

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(text = "Tutorial 1-1 Basics")
                }
            )
        })
    {
        TutorialListContent(tutorialList, navController)
    }
}

@ExperimentalAnimationApi
@Composable
private fun TutorialListContent(
    tutorialList: List<TutorialSectionModel>,
    navController: NavHostController
) {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Color(0xffEEEEEE)
    ) {
        // List of Tutorials
        LazyColumn(
            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            content = {

                items(tutorialList) { item: TutorialSectionModel ->

                    var isExpanded by remember(key1 = item.title) { mutableStateOf(false) }

                    TutorialSectionCard(
                        model = item,
                        onClick = {
                            navController.navigate(item.title)
                        },
                        onExpandClicked = {
                            isExpanded = !isExpanded
                        },
                        expanded = isExpanded
                    )
                }
            }
        )
    }
}