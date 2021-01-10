package com.smarttoolfactory.tutorial1_1basics

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.setContent
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigate
import androidx.navigation.compose.rememberNavController
import com.smarttoolfactory.tutorial1_1basics.chapter1_basics.Tutorial1_1Screen
import com.smarttoolfactory.tutorial1_1basics.chapter1_basics.Tutorial1_2Screen
import com.smarttoolfactory.tutorial1_1basics.chapter2_material_widgets.Tutorial2_1Screen
import com.smarttoolfactory.tutorial1_1basics.chapter2_material_widgets.Tutorial2_2Screen
import com.smarttoolfactory.tutorial1_1basics.chapter2_material_widgets.Tutorial2_3Screen
import com.smarttoolfactory.tutorial1_1basics.chapter2_material_widgets.Tutorial2_4Screen
import com.smarttoolfactory.tutorial1_1basics.components.TutorialSectionCard
import com.smarttoolfactory.tutorial1_1basics.model.*
import com.smarttoolfactory.tutorial1_1basics.ui.ComposeTutorialsTheme

/**
 * This is the single and only Activity that contains Composable Tutorial list.
 *
 * * Tutorial navigation is done via [NavController] and ```composable``` extension function
 * for [NavGraphBuilder]
 */
class MainActivity : AppCompatActivity() {

    private lateinit var tutorialList: List<TutorialSectionModel>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            ComposeTutorialsTheme {
                ComposeNavigation()
            }
        }
    }

    @Composable
    fun ComposeNavigation() {

        // NavController for navigating between Composable Screens
        val navController = rememberNavController()

        // Create Tutorial List
        tutorialList = createTutorialList()

        // Create Navigation for each Composable Page
        NavHost(
            navController = navController,
            startDestination = "start_destination"
        ) {

            composable("start_destination") {
                TutorialComponent(tutorialList, navController)
            }

            // Set navigation route as title of tutorial card
            // and invoke @Composable inside lambda of this card.
            tutorialList.forEach { model ->
                composable(model.title) {
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
                modifier = Modifier.padding(top = 16.dp),
                content = {
                    items(tutorialList) { item: TutorialSectionModel ->
                        TutorialSectionCard(item) {
                            navController.navigate(item.title)
                        }
                    }
                })
        }
    }

    /**
     * Create list of tutorials with titles, action that navigates to composable function
     * inside lambda.
     *
     * * Tags are for search purposes if there is a Search Component exists.
     */
    @Composable
    private fun createTutorialList(): List<TutorialSectionModel> {

        val tutorial1_1 = TutorialSectionModel(
            title = "1-1 Column, Row, Box, Modifiers",
            action = {
                Tutorial1_1Screen()
            },
            description = "Create Rows, Columns and Box, how to add modifiers to " +
                    "composables. Set padding, margin, alignment other properties of composables.",
            tags = listOf(
                TAG_COMPOSE,
                TAG_COMPOSE_COLUMN,
                TAG_COMPOSE_ROW,
                TAG_COMPOSE_BOX,
                TAG_COMPOSE_MODIFIER
            )
        )

        val tutorial1_2 = TutorialSectionModel(
            title = "1-2 Surface, Shape, Clickable",
            description = "Create and modify Surface to draw background for Composables," +
                    " add click action to any composable. Set weight or offset modifiers.",
            action = {
                Tutorial1_2Screen()
            },
            tags = listOf(
                TAG_COMPOSE,
                TAG_COMPOSE_MODIFIER,
                TAG_COMPOSE_SURFACE,
                TAG_COMPOSE_SHAPE,
                TAG_COMPOSE_CLICKABLE
            )
        )

        val tutorial1_3 = TutorialSectionModel(
            title = "1-3 Modifiers",
            description = "Change modifiers such as padding, dimensions, shadow," +
                    " background, and more.",
            action = {
                Tutorial1_2Screen()
            },
            tags = listOf(
                TAG_COMPOSE,
                TAG_COMPOSE_MODIFIER
            )
        )

        val tutorial2_1 = TutorialSectionModel(
            title = "2-1 Text",
            description = "Create Text component with different properties such as " +
                    "color, background, font weight, family, style, spacing and others.",
            action = {
                Tutorial2_1Screen()
            },
            tags = listOf(
                TAG_COMPOSE,
                TAG_TEXT
            )
        )

        val tutorial2_2 = TutorialSectionModel(
            title = "2-2 Button",
            description = "Create Button with text and/or with image, Floating Action Button " +
                    ", or Chips. Modify properties of buttons such as color, text, or state.",
            action = {
                Tutorial2_2Screen()
            },
            tags = listOf(
                TAG_COMPOSE,
                TAG_BUTTON,
                TAG_ICON_BUTTON,
                TAG_FAB_BUTTON,
                TAG_CHIP
            )
        )

        val tutorial2_3 = TutorialSectionModel(
            title = "2-3 TextField",
            description = "Create TextField component with regular style or outlined. Set error," +
                    " colors, state, icons, and IME actions.",
            action = {
                Tutorial2_3Screen()
            },
            tags = listOf(
                TAG_COMPOSE,
                TAG_TEXT_FIELD,
                TAG_OUTLINED_TEXT_FIELD,
                TAG_IME,
                TAG_REGEX
            )
        )

        val tutorial2_4 = TutorialSectionModel(
            title = "2-4 Image",
            description = "Create Image to display images, set image and crop styles. Use " +
                    "resource to retrieve images and Coil for fetching image from remote sources.",
            action = {
                Tutorial2_4Screen()
            },
            tags = listOf(
                TAG_COMPOSE,
                TAG_IMAGE,
                TAG_DRAWABLE,
                TAG_VECTOR_DRAWABLE,
                TAG_BITMAP
            )
        )

        return listOf(
            tutorial1_1,
            tutorial1_2,
            tutorial2_1,
            tutorial2_2,
            tutorial2_3,
            tutorial2_4
        )
    }
}