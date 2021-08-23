package com.smarttoolfactory.tutorial1_1basics

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Scaffold
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.*
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.smarttoolfactory.tutorial1_1basics.chapter1_basics.Tutorial1_1Screen
import com.smarttoolfactory.tutorial1_1basics.chapter1_basics.Tutorial1_2Screen
import com.smarttoolfactory.tutorial1_1basics.chapter2_material_widgets.*
import com.smarttoolfactory.tutorial1_1basics.ui.components.TutorialSectionCard
import com.smarttoolfactory.tutorial1_1basics.model.*
import com.smarttoolfactory.tutorial1_1basics.ui.ComposeTutorialsTheme

/**
 * This is the single and only Activity that contains Composable Tutorial list.
 *
 * * Tutorial navigation is done via [NavController] and ```composable``` extension function
 * for [NavGraphBuilder]
 */
@ExperimentalFoundationApi
@ExperimentalComposeUiApi
class MainActivity : AppCompatActivity() {


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
                }
            )
        }
    }

    /**
     * Create list of tutorials with titles, action that navigates to composable function
     * inside lambda.
     *
     * * Tags are for search purposes if there is a Search Component exists.
     */
    @Composable
    private fun createTutorialList(onBack: () -> Unit): List<TutorialSectionModel> {

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
                TAG_TEXT,
                TAG_FONT_STYLE,
                TAG_ANNOTATED_STRING,
                TAG_HYPERLINK
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
                TAG_VISUAL_TRANSFORMATION,
                TAG_REGEX
            )
        )

        val tutorial2_4 = TutorialSectionModel(
            title = "2-4 Image",
            description = "Create Image to display images, set image and crop styles. " +
                    "Change shape of Image or apply ColorFilter and PorterDuff modes.",
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

        val tutorial2_5_1 = TutorialSectionModel(
            title = "2-5-1 LazyColumn1",
            description = "LazyColumn is counterpart of vertical RecyclerView in Compose",
            action = {
                Tutorial2_5Screen1()
            },
            tags = listOf(
                TAG_COMPOSE,
                TAG_LAZY_COLUMN,
                TAG_LAZY_COLUMN_VERTICAL_ARRANGEMENT,
                TAG_LAZY_COLUMN_CONTENT_PADDING
            )
        )

        val tutorial2_5_2 = TutorialSectionModel(
            title = "2-5-2 LazyColumn2",
            description = "LazyColumn scroll state and modify dynamic list",
            action = {
                Tutorial2_5Screen2()
            },
            tags = listOf(
                TAG_COMPOSE,
                TAG_LAZY_COLUMN,
                TAG_LAZY_COLUMN_VERTICAL_ARRANGEMENT,
                TAG_LAZY_COLUMN_CONTENT_PADDING,
                TAG_LAZY_COLUMN_SCROLL,
                TAG_LAZY_COLUMN_DYNAMIC_SIZE
            )
        )

        val tutorial2_5_3 = TutorialSectionModel(
            title = "2-5-3 LazyRow",
            description = "LazyColumn is counterpart of horizontal RecyclerView in Compose",
            action = {
                Tutorial2_5Screen3()
            },
            tags = listOf(
                TAG_COMPOSE,
                TAG_LAZY_ROW,
                TAG_LAZY_COLUMN_VERTICAL_ARRANGEMENT,
                TAG_LAZY_COLUMN_CONTENT_PADDING
            )
        )

        val tutorial2_5_4 = TutorialSectionModel(
            title = "2-5-4 StickyHeader",
            description = "LazyColumn with StickyHeaders",
            action = {
                Tutorial2_5Screen4()
            },
            tags = listOf(
                TAG_COMPOSE,
                TAG_LAZY_COLUMN,
                TAG_LAZY_COLUMN_STICKY_HEADER,
                TAG_LAZY_COLUMN_VERTICAL_ARRANGEMENT,
                TAG_LAZY_COLUMN_CONTENT_PADDING
            )
        )

        val tutorial2_5_5 = TutorialSectionModel(
            title = "2-5-5 LazyVerticalGrid",
            description = "Grid style item display",
            action = {
                Tutorial2_5Screen5()
            },
            tags = listOf(
                TAG_COMPOSE,
                TAG_LAZY_VERTICAL_GRID,
                TAG_GRID_CELL,
                TAG_LAZY_COLUMN_VERTICAL_ARRANGEMENT,
                TAG_LAZY_COLUMN_CONTENT_PADDING
            )
        )

        val tutorial2_6 = TutorialSectionModel(
            title = "2-6 TopAppbar&Tabs",
            description = "The top app bar displays information and actions relating to the current screen.",
            action = {
                Tutorial2_6Screen(onBack)
            },
            tags = listOf(
                TAG_COMPOSE,
                TAG_TOP_APP_BAR,
                TAG_TOP_APP_BAR_MENU
            )
        )

        return listOf(
            tutorial1_1,
            tutorial1_2,
            tutorial2_1,
            tutorial2_2,
            tutorial2_3,
            tutorial2_4,
            tutorial2_5_1,
            tutorial2_5_2,
            tutorial2_5_3,
            tutorial2_5_4,
//            tutorial2_5_5,
            tutorial2_6
        )
    }
}