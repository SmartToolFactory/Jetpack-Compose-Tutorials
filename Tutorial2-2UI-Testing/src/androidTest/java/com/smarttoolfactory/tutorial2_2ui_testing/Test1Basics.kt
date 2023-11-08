package com.smarttoolfactory.tutorial2_2ui_testing

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.onRoot
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.printToLog
import com.smarttoolfactory.tutorial2_2ui_testing.ui.theme.ComposeTutorialsTheme
import org.junit.Rule
import org.junit.Test

class MyComposeTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun MyTest() {
        // Start the app
        composeTestRule.setContent {
            ComposeTutorialsTheme {
                Button(onClick = {}) {
                    Icon(
                        imageVector = Icons.Filled.Favorite,
                        contentDescription = null
                    )
                    Spacer(Modifier.size(ButtonDefaults.IconSpacing))
                    Text("Like")
                }
            }
        }
        // Log the full semantics tree
        /*
            Printing with useUnmergedTree = 'false'
            Node #1 at (l=0.0, t=145.0, r=293.0, b=277.0)px
             |-Node #2 at (l=0.0, t=156.0, r=293.0, b=266.0)px
               Focused = 'false'
               Role = 'Button'
               Text = '[Like]'
               Actions = [OnClick, RequestFocus, GetTextLayoutResult]
               MergeDescendants = 'true'
         */
        composeTestRule.onRoot(useUnmergedTree = false).printToLog("useUnmergedTree")
        /*
           Printing with useUnmergedTree = 'true'
            Node #1 at (l=0.0, t=145.0, r=293.0, b=277.0)px
             |-Node #2 at (l=0.0, t=156.0, r=293.0, b=266.0)px
               Focused = 'false'
               Role = 'Button'
               Actions = [OnClick, RequestFocus]
               MergeDescendants = 'true'
                |-Node #6 at (l=154.0, t=185.0, r=227.0, b=237.0)px
                  Text = '[Like]'
                  Actions = [GetTextLayoutResult]
         */
        composeTestRule.onRoot(useUnmergedTree = true).printToLog("useUnmergedTree")

        // Assert that Button with text Like is on screen
        composeTestRule.onNodeWithText("Like").assertIsDisplayed()
    }

    @Test
    fun button_with_text_displayed() {
        composeTestRule.setContent {
            ButtonClickSample()
        }
        composeTestRule.onNodeWithText("Counter 0").assertIsDisplayed()
    }

    @Test
    fun button_with_text_click_increases_counter() {
        composeTestRule.setContent {
            ButtonClickSample()
        }
        composeTestRule.onNodeWithText("Counter 0").performClick()
        composeTestRule.onNodeWithText("Counter 1").assertIsDisplayed()
    }

    @Test
    fun button_with_contentDescription_is_displayed() {
        composeTestRule.setContent {
            ButtonClickSample2()
        }
        composeTestRule.onNodeWithContentDescription("Counter").assertIsDisplayed()
    }

    @Test
    fun button_with_contentDescription_click_increases_counter() {
        composeTestRule.setContent {
            ButtonClickSample2()
        }
        composeTestRule.onNodeWithContentDescription("Counter").performClick()
        composeTestRule.onNodeWithContentDescription("Counter")
            .assertTextEquals("Counter 1")
    }

    @Test
    fun button_clear_semantics_test() {
        composeTestRule.setContent {
            ButtonClearSemanticsSample()
        }

        /*
            Printing with useUnmergedTree = 'false'
            Node #1 at (l=0.0, t=145.0, r=293.0, b=277.0)px
             |-Node #2 at (l=0.0, t=156.0, r=293.0, b=266.0)px
               ContentDescription = '[Button]'
               ClearAndSetSemantics = 'true'
         */
        composeTestRule.onRoot(useUnmergedTree = false).printToLog("useUnmergedTree")

        /*
            Printing with useUnmergedTree = 'true'
            Node #1 at (l=0.0, t=145.0, r=293.0, b=277.0)px
             |-Node #2 at (l=0.0, t=156.0, r=293.0, b=266.0)px
               ContentDescription = '[Button]'
               ClearAndSetSemantics = 'true'
                |-Node #4 at (l=66.0, t=178.0, r=132.0, b=244.0)px
                | ContentDescription = '[favorite]'
                | Role = 'Image'
                |-Node #6 at (l=154.0, t=185.0, r=227.0, b=237.0)px
                  Text = '[Like]'
                  ContentDescription = '[Like Text]'
                  Actions = [GetTextLayoutResult]
         */
        composeTestRule.onRoot(useUnmergedTree = true).printToLog("useUnmergedTree")

        composeTestRule.onNodeWithContentDescription("Button").performClick()
    }

    @Test
    fun merge_descendants_test() {
        composeTestRule.setContent {
            MergeSemanticsSample()
        }


        composeTestRule.onRoot(useUnmergedTree = false).printToLog("useUnmergedTree")

        composeTestRule.onRoot(useUnmergedTree = true).printToLog("useUnmergedTree")
        composeTestRule.onNodeWithContentDescription("Like Button").performClick()

    }
}

@Composable
fun ButtonClickSample() {
    var counter by remember {
        mutableStateOf(0)
    }

    Button(
        onClick = {
            counter++
        }
    ) {
        Text("Counter $counter")
    }
}

@Composable
fun ButtonClickSample2() {
    var counter by remember {
        mutableStateOf(0)
    }

    Button(
        // Setting a contentDescription it's possible to find this via this description
        modifier = Modifier.semantics {
            contentDescription = "Counter"
        },
        onClick = {
            counter++
        }
    ) {
        Text("Counter $counter")
    }
}

@Composable
fun ButtonClearSemanticsSample() {
    Button(
        // Clear semantics for button and set contentDescription
        modifier = Modifier.clearAndSetSemantics {
            contentDescription = "Button"
        },
        onClick = {}
    ) {

        Icon(
            imageVector = Icons.Filled.Favorite,
            contentDescription = "favorite"
        )
        Spacer(Modifier.size(ButtonDefaults.IconSpacing))
        // We can find this Text with  contentDescription
        Text("Like", modifier = Modifier.semantics {
            contentDescription = "Like Text"
        }
        )
    }
}

@Composable
private fun MergeSemanticsSample() {
    Row(
        modifier = Modifier
            .clickable { }
            .semantics(mergeDescendants = true) {
                contentDescription = "Like Button"
            }
    ) {
        Icon(
            imageVector = Icons.Filled.Favorite,
            contentDescription = "favorite"
        )
        Spacer(Modifier.size(ButtonDefaults.IconSpacing))
        // We can find this Text with  contentDescription
        Text("Like", modifier = Modifier.semantics {
            contentDescription = "Like Text"
        }
        )
    }
}