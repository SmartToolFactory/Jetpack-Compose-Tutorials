package com.smarttoolfactory.tutorial1_1basics.model

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.runtime.Composable
import androidx.compose.ui.ExperimentalComposeUiApi
import com.smarttoolfactory.tutorial1_1basics.chapter1_basics.Tutorial1_1Screen
import com.smarttoolfactory.tutorial1_1basics.chapter1_basics.Tutorial1_2Screen
import com.smarttoolfactory.tutorial1_1basics.chapter2_material_widgets.*

/**
 * Create list of tutorials with titles, action that navigates to composable function
 * inside lambda.
 *
 * * Tags are for search purposes if there is a Search Component exists.
 */
@ExperimentalFoundationApi
@ExperimentalComposeUiApi
@Composable
fun createTutorialList(onBack: () -> Unit): List<TutorialSectionModel> {

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
            TAG_TOP_APP_BAR_MENU,
            TAG_TAB_ROW,
            TAG_TAB,
            TAG_TAB_INDICATOR,
            TAG_TAB_SCROLLABLE
        )
    )

    val tutorial2_7 = TutorialSectionModel(
        title = "2-7 BottomNavigation",
        description = "Material Design bottom navigation.\n" +
                "Bottom navigation bars allow movement between primary destinations in an app.\n" +
                "BottomNavigation should contain multiple BottomNavigationItems, each representing a singular destination.",
        action = {
            Tutorial2_7Screen()
        },
        tags = listOf(
            TAG_COMPOSE,
            TAG_BOTTOM_NAVIGATION,
            TAG_BOTTOM_NAVIGATION_ITEM
        )
    )

    val tutorial2_8 = TutorialSectionModel(
        title = "2-8 BottomAppBar",
        description = "Material Design bottom app bar.\n" +
                "A bottom app bar displays navigation and key actions at the bottom of screens.",
        action = {
            Tutorial2_8Screen(onBack)
        },
        tags = listOf(
            TAG_COMPOSE,
            TAG_BOTTOM_APP_BAR
        )
    )

    val tutorial2_9 = TutorialSectionModel(
        title = "2-9 Side Navigation",
        description = "Navigate using side navigation. Navigate after popping from stack. Open or close drawer with scaffoldState.drawerState",
        action = {
            Tutorial2_9Screen()
        },
        tags = listOf(
            TAG_COMPOSE,
            TAG_SCAFFOLD,
            TAG_NAVIGATION_DRAWER,
            TAG_DRAWER_STATE
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
        tutorial2_5_5,
        tutorial2_6,
        tutorial2_7,
        tutorial2_8,
        tutorial2_9
    )
}