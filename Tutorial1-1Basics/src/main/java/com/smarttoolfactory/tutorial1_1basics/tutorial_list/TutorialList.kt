package com.smarttoolfactory.tutorial1_1basics

import Tutorial2_10Screen3
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.runtime.Composable
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import com.smarttoolfactory.tutorial1_1basics.chapter1_basics.Tutorial1_1Screen
import com.smarttoolfactory.tutorial1_1basics.chapter1_basics.Tutorial1_2Screen
import com.smarttoolfactory.tutorial1_1basics.chapter2_material_widgets.*
import com.smarttoolfactory.tutorial1_1basics.chapter3_layout.*
import com.smarttoolfactory.tutorial1_1basics.chapter3_layout.Tutorial3_2Screen1
import com.smarttoolfactory.tutorial1_1basics.model.TutorialSectionModel
import com.smarttoolfactory.tutorial1_1basics.tutorial_list.*

/**
 * Create list of tutorials with titles, action that navigates to composable function
 * inside lambda.
 *
 * * Tags are for search purposes if there is a Search Component exists.
 */
@ExperimentalAnimationApi
@OptIn(ExperimentalMaterialApi::class)
@ExperimentalFoundationApi
@ExperimentalComposeUiApi
@Composable
fun createComponentTutorialList(onBack: () -> Unit): List<TutorialSectionModel> {
    val tutorial1_1 = TutorialSectionModel(
        title = stringResource(R.string.title1_1),
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
        title = stringResource(R.string.title1_2),
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


    val tutorial2_1 = TutorialSectionModel(
        title = stringResource(R.string.title2_1),
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
        title = stringResource(R.string.title2_2),
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
        title = stringResource(R.string.title2_3),
        description = "Create TextField component with regular style or outlined. Set error," +
                " colors, state, icons, VisualTransformations for phone or credit card, and IME actions.",
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
        title = stringResource(R.string.title2_4),
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
        title = stringResource(R.string.title2_5_1),
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
        title = stringResource(R.string.title2_5_2),
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
        title = stringResource(R.string.title2_5_3),
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
        title = stringResource(R.string.title2_5_4),
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
        title = stringResource(R.string.title2_5_5),
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

    val tutorial2_5_6 = TutorialSectionModel(
        title = stringResource(R.string.title2_5_6),
        description = "Create one-line, two-line, three-line or combine other components to build list items using built-in ListItem component",
        action = {
            Tutorial2_5Screen6()
        },
        tags = listOf(
            TAG_COMPOSE,
            TAG_LAZY_COLUMN,
            TAG_LIST_ITEM
        )
    )

    val tutorial2_6 = TutorialSectionModel(
        title = stringResource(R.string.title2_6),
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
        title = stringResource(R.string.title2_7),
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
        title = stringResource(R.string.title2_8),
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

    val tutorial2_9_1 = TutorialSectionModel(
        title = stringResource(R.string.title2_9_1),
        description = "Navigate using side navigation. Navigate after popping from stack. Open or close drawer with scaffoldState.drawerState",
        action = {
            Tutorial2_9Screen1()
        },
        tags = listOf(
            TAG_COMPOSE,
            TAG_SCAFFOLD,
            TAG_NAVIGATION_DRAWER,
            TAG_DRAWER_STATE
        )
    )

    val tutorial2_9_2 = TutorialSectionModel(
        title = stringResource(R.string.title2_9_2),
        description = "Modal navigation drawers block interaction with the rest of an app’s content with a scrim. They are elevated above most of the app’s UI and don’t affect the screen’s layout grid.",
        action = {
            Tutorial2_9Screen2()
        },
        tags = listOf(
            TAG_COMPOSE,
            TAG_MODAL_DRAWER,
            TAG_DRAWER_STATE
        )
    )

    val tutorial2_9_3 = TutorialSectionModel(
        title = stringResource(R.string.title2_9_3),
        description = "ModalDrawer sample with Scaffold. ModalDrawer is inside content Scaffold.",
        action = {
            Tutorial2_9Screen3()
        },
        tags = listOf(
            TAG_COMPOSE,
            TAG_SCAFFOLD,
            TAG_MODAL_DRAWER,
            TAG_DRAWER_STATE
        )
    )

    val tutorial2_9_4 = TutorialSectionModel(
        title = stringResource(R.string.title2_9_4),
        description = "Another ModalDrawer sample with Scaffold. Scaffold is inside content of ModalDrawer",
        action = {
            Tutorial2_9Screen4()
        },
        tags = listOf(
            TAG_COMPOSE,
            TAG_SCAFFOLD,
            TAG_MODAL_DRAWER,
            TAG_DRAWER_STATE
        )
    )

    val tutorial2_10_1 = TutorialSectionModel(
        title = stringResource(R.string.title2_10_1),
        description = "Create bottom sheet using BottomSheetScaffold and rememberBottomSheetScaffoldState",
        action = {
            Tutorial2_10Screen1()
        },
        tags = listOf(
            TAG_COMPOSE,
            TAG_SCAFFOLD_BOTTOM_SHEET,
            TAG_BOTTOM_SHEET,
            TAG_BOTTOM_SHEET_STATE
        )
    )

    val tutorial2_10_2 = TutorialSectionModel(
        title = stringResource(R.string.title2_10_2),
        description = "Create modal bottom sheet using BottomSheetScaffold and rememberBottomSheetScaffoldState",
        action = {
            Tutorial2_10Screen2()
        },
        tags = listOf(
            TAG_COMPOSE,
            TAG_MODAL_BOTTOM_SHEET,
            TAG_MODAL_BOTTOM_SHEET_VALUE
        )
    )

    val tutorial2_10_3 = TutorialSectionModel(
        title = stringResource(R.string.title2_10_3),
        description = "Bottom navigation drawers are modal drawers that are anchored to the bottom of the screen.",
        action = {
            Tutorial2_10Screen3()
        },
        tags = listOf(
            TAG_COMPOSE,
            TAG_BOTTOM_DRAWER
        )
    )

    val tutorial2_10_4 = TutorialSectionModel(
        title = stringResource(R.string.title2_10_4),
        description = "BottomDrawer with BottomAppBar.",
        action = {
            Tutorial2_10Screen4()
        },
        tags = listOf(
            TAG_COMPOSE,
            TAG_BOTTOM_DRAWER,
            TAG_BOTTOM_APP_BAR,
            TAG_SCAFFOLD
        )
    )
    val tutorial2_10_5 = TutorialSectionModel(
        title = stringResource(R.string.title2_10_5),
        description = "A backdrop appears behind all other surfaces in an app, displaying contextual and actionable content.",
        action = {
            Tutorial2_10Screen5()
        },
        tags = listOf(
            TAG_COMPOSE,
            TAG_BACKDROP_SCAFFOLD
        )
    )

    val tutorial2_11 = TutorialSectionModel(
        title = stringResource(R.string.title2_11),
        description = "Samples for Snackbar, ProgressIndicator, Slider, CheckBox, TriStateCheckBox, RadioButton with groups, and Switch.",
        action = {
            Tutorial2_11Screen()
        },
        tags = listOf(
            TAG_COMPOSE,
            TAG_SNACK_BAR,
            TAG_LINEAR_PROGRESS_INDICATOR,
            TAG_CIRCULAR_PROGRESS_INDICATOR,
            TAG_SLIDER,
            TAG_CHECKBOX,
            TAG_RADIO_BUTTON,
            TAG_SWITCH,
            TAG_LIST_ITEM
        )
    )

    val tutorial2_12 = TutorialSectionModel(
        title = stringResource(R.string.title2_12),
        description = "Create Dialog, and AlertDialogs with standard and custom layouts. Implement on dismiss logic and get result when dialog is closed.",
        action = {
            Tutorial2_12Screen()
        },
        tags = listOf(
            TAG_COMPOSE,
            TAG_DIALOG,
            TAG_ALERT_DIALOG,
            TAG_DIALOG_PROPERTIES
        )
    )

    val tutorial2_13 = TutorialSectionModel(
        title = stringResource(R.string.title2_13),
        description = "Create a composable that can be dismissed by swiping left or right.",
        action = {
            Tutorial2_13Screen()
        },
        tags = listOf(
            TAG_COMPOSE,
            TAG_SWIPE_TO_DISMISS
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
        tutorial2_5_6,
        tutorial2_6,
        tutorial2_7,
        tutorial2_8,
        tutorial2_9_1,
        tutorial2_9_2,
        tutorial2_9_3,
        tutorial2_9_4,
        tutorial2_10_1,
        tutorial2_10_2,
        tutorial2_10_3,
        tutorial2_10_4,
        tutorial2_10_5,
        tutorial2_11,
        tutorial2_12,
        tutorial2_13
    )
}

@ExperimentalAnimationApi
@OptIn(ExperimentalMaterialApi::class)
@ExperimentalFoundationApi
@ExperimentalComposeUiApi
@Composable
fun createLayoutTutorialList(): List<TutorialSectionModel> {

    val tutorial3_1 = TutorialSectionModel(
        title = stringResource(R.string.title3_1),
        description = "Create custom modifiers using layout, Measurable, Constraint, Placeable," +
                " and LayoutModifier",
        action = {
            Tutorial3_1Screen()
        },
        tags = listOf(
            TAG_COMPOSE,
            TAG_CUSTOM_MODIFIER,
            TAG_MEASURABLE,
            TAG_CONSTRAINT,
            TAG_PLACEABLE,
            TAG_LAYOUT_MODIFIER
        ),
        tagColor = Color(0xffFFEB3B)
    )

    val tutorial3_2_1 = TutorialSectionModel(
        title = stringResource(R.string.title3_2_1),
        description = "Create custom layout using using layout, Measurable, Constraint, Placeable",
        action = {
            Tutorial3_2Screen1()
        },
        tags = listOf(
            TAG_COMPOSE,
            TAG_CUSTOM_LAYOUT,
            TAG_MEASURABLE,
            TAG_CONSTRAINT,
            TAG_PLACEABLE
        ),
        tagColor = Color(0xffFFEB3B)
    )

    val tutorial3_2_2 = TutorialSectionModel(
        title = "3-2-2 Custom Layout",
        description = "Create custom layout using using MeasurePolicy and use intrinsic dimensions",
        action = {
            Tutorial3_2Screen2()
        },
        tags = listOf(
            TAG_COMPOSE,
            TAG_MEASURE_POLICY,
            TAG_INTRINSIC_WIDTH,
            TAG_INTRINSIC_HEIGHT,
            TAG_CUSTOM_LAYOUT,
            TAG_MEASURABLE,
            TAG_CONSTRAINT,
            TAG_PLACEABLE
        ),
        tagColor = Color(0xffFFEB3B)
    )

    val tutorial3_3_1 = TutorialSectionModel(
        title = stringResource(R.string.title_3_3_1),
        description = "Add custom modifiers to Composable inside a custom layout using it's scope",
        action = {
            Tutorial3_3Screen1()
        },
        tags = listOf(
            TAG_COMPOSE,
            TAG_CUSTOM_LAYOUT,
            TAG_SCOPE,
            TAG_PARENT_DATA_MODIFIER,
            TAG_MEASURABLE,
            TAG_CONSTRAINT,
            TAG_PLACEABLE
        ),
        tagColor = Color(0xffFFEB3B)
    )

    val tutorial3_4 = TutorialSectionModel(
        title = stringResource(R.string.title_3_4),
        description = "BoxWithConstraints is a composable that defines its own content " +
                "according to the available space, based on the incoming constraints " +
                "or the current LayoutDirection. ",
        action = {
            Tutorial3_4Screen()
        },
        tags = listOf(
            TAG_COMPOSE,
            TAG_BOX_WITH_CONSTRAINTS
        ),
        tagColor = Color(0xffFFEB3B)
    )

    return listOf(
        tutorial3_1,
        tutorial3_2_1,
        tutorial3_2_2,
        tutorial3_3_1,
        tutorial3_4,
    )
}