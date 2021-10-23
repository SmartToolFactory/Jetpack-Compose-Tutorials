package com.smarttoolfactory.tutorial1_1basics

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.runtime.Composable
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.smarttoolfactory.tutorial1_1basics.model.TutorialSectionModel

@ExperimentalComposeUiApi
@ExperimentalFoundationApi
@ExperimentalMaterialApi
@ExperimentalAnimationApi
@Composable
fun TutorialNavGraph(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
    startDestination: String = Destinations.BASICS_START,
) {

    val mainViewModel: HomeViewModel = viewModel()

    val componentTutorialList: List<TutorialSectionModel> = createComponentTutorialList {
        navController.navigateUp()
    }

    val layoutTutorialList = createLayoutTutorialList()

    mainViewModel.componentTutorialList = componentTutorialList
    mainViewModel.layoutTutorials = layoutTutorialList

    if (mainViewModel.tutorialList.isEmpty()) {
        mainViewModel.tutorialList.add(componentTutorialList)
        mainViewModel.tutorialList.add(layoutTutorialList)
    }

//    println("🍏 TutorialNavGraph(): mainViewModel: mainViewModel, list: ${mainViewModel.componentTutorialList.hashCode()}")

    // Create Navigation for each Composable Page
    NavHost(
        modifier = modifier,
        navController = navController,
        startDestination = startDestination
    ) {

        // BASIC TUTORIALS
        composable(route = Destinations.BASICS_START) { navBackEntryStack ->
            HomeScreen(
                viewModel = mainViewModel,
                navigateToTutorial = { tutorialTitle ->
                    navController.navigate(tutorialTitle)
                }
            )
        }

        // Set navigation route as title of tutorial card
        // and invoke @Composable inside lambda of this card.
        mainViewModel.tutorialList.forEach { list ->
            list.forEach { model ->
                composable(route = model.title) { navBackEntryStack ->
                    // 🔥 These are @Composable screens such as Tutorial2_1Screen()
                    model.action?.invoke()
                }
            }
        }
    }
}

object Destinations {
    const val BASICS_START = "start_destinations"
}