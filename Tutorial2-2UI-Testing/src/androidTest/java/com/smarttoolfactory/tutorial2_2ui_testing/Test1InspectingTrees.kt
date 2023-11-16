package com.smarttoolfactory.tutorial2_2ui_testing

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.test.assert
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.onRoot
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.compose.ui.test.printToLog
import com.smarttoolfactory.tutorial2_2ui_testing.ui.theme.ComposeTutorialsTheme
import org.junit.Rule
import org.junit.Test

class Test1InspectingTrees {

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
        composeTestRule.onRoot(useUnmergedTree = false).printToLog("test tag")
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
        composeTestRule.onRoot(useUnmergedTree = true).printToLog("test tag")

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
    fun merged_node_tree_test() {
        composeTestRule.setContent {
            Column(
                modifier = Modifier.semantics(mergeDescendants = true) {}
            ) {
                Text("Hello")
                Text("World")
            }
        }

        composeTestRule.onRoot(useUnmergedTree = false).printToLog("test tag")
        composeTestRule.onRoot(useUnmergedTree = true).printToLog("test tag")

        composeTestRule.onNodeWithText("Hello").assertIsDisplayed()
    }

    @Test
    fun button_clear_semantics_test() {
        composeTestRule.setContent {
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

        /*
            Printing with useUnmergedTree = 'false'
            Node #1 at (l=0.0, t=145.0, r=293.0, b=277.0)px
             |-Node #2 at (l=0.0, t=156.0, r=293.0, b=266.0)px
               ContentDescription = '[Button]'
               ClearAndSetSemantics = 'true'
         */
        composeTestRule.onRoot(useUnmergedTree = false).printToLog("test tag")

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
        composeTestRule.onRoot(useUnmergedTree = true).printToLog("test tag")

        composeTestRule.onNodeWithContentDescription("Button").performClick()
    }

    @Test
    fun merge_descendants_test() {
        composeTestRule.setContent {
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


        /*
            Printing with useUnmergedTree = 'false'
            Node #1 at (l=0.0, t=145.0, r=156.0, b=211.0)px
             |-Node #2 at (l=0.0, t=145.0, r=156.0, b=211.0)px
               ContentDescription = '[Like Button, favorite, Like Text]'
               Focused = 'false'
               Text = '[Like]'
               Actions = [OnClick, RequestFocus, GetTextLayoutResult]
               MergeDescendants = 'true'
         */
        composeTestRule.onRoot(useUnmergedTree = false).printToLog("test tag")

        /*
            Printing with useUnmergedTree = 'true'
            Node #1 at (l=0.0, t=145.0, r=156.0, b=211.0)px
             |-Node #2 at (l=0.0, t=145.0, r=156.0, b=211.0)px
               ContentDescription = '[Like Button]'
               Focused = 'false'
               Actions = [OnClick, RequestFocus]
               MergeDescendants = 'true'
                |-Node #3 at (l=0.0, t=145.0, r=66.0, b=211.0)px
                | ContentDescription = '[favorite]'
                | Role = 'Image'
                |-Node #5 at (l=88.0, t=145.0, r=156.0, b=197.0)px
                  Text = '[Like]'
                  ContentDescription = '[Like Text]'
                  Actions = [GetTextLayoutResult]
         */
        composeTestRule.onRoot(useUnmergedTree = true).printToLog("test tag")

        composeTestRule.onNodeWithContentDescription("Like Button").performClick()

    }

    @Test
    fun merge_textfield_descendants_test() {

        composeTestRule.setContent {
            var text by remember {
                mutableStateOf("")
            }

            TextField(
                modifier = Modifier.semantics {
                    contentDescription = "input"
                },
                label = {
                    Text("Label Text")
                },
                placeholder = {
                    Text("Placeholder Text")
                },
                value = text,
                onValueChange = {
                    text = it
                }
            )
        }

        composeTestRule.onNodeWithContentDescription("input").performTextInput("100")

        /*
            Printing with useUnmergedTree = 'false'
            Node #1 at (l=0.0, t=145.0, r=770.0, b=299.0)px
             |-Node #2 at (l=0.0, t=145.0, r=770.0, b=299.0)px
               ImeAction = 'Default'
               EditableText = '100'
               TextSelectionRange = 'TextRange(3, 3)'
               Focused = 'true'
               ContentDescription = '[input]'
               Text = '[Label Text]'
               Actions = [GetTextLayoutResult, SetText, InsertTextAtCursor, SetSelection, PerformImeAction, OnClick, OnLongClick, PasteText, RequestFocus, MagnifierPositionInRoot]
               MergeDescendants = 'true'
         */
        composeTestRule.onRoot(useUnmergedTree = false).printToLog("test tag")

        /*
            Printing with useUnmergedTree = 'true'
            Node #1 at (l=0.0, t=145.0, r=770.0, b=299.0)px
             |-Node #2 at (l=0.0, t=145.0, r=770.0, b=299.0)px
               ImeAction = 'Default'
               EditableText = '100'
               TextSelectionRange = 'TextRange(3, 3)'
               Focused = 'true'
               ContentDescription = '[input]'
               Actions = [GetTextLayoutResult, SetText, InsertTextAtCursor, SetSelection, PerformImeAction, OnClick, OnLongClick, PasteText, RequestFocus]
               MergeDescendants = 'true'
                |-Node #3 at (l=0.0, t=145.0, r=770.0, b=299.0)px
                   |-Node #7 at (l=44.0, t=167.0, r=209.0, b=211.0)px
                   | Text = '[Label Text]'
                   | Actions = [GetTextLayoutResult]
                   |-Node #9 at (l=44.0, t=218.0, r=726.0, b=270.0)px
                     Actions = [MagnifierPositionInRoot]
         */
        composeTestRule.onRoot(useUnmergedTree = true).printToLog("test tag")

        composeTestRule.onNodeWithContentDescription("input")
            .assert(hasText("100", ignoreCase = true))

        /*
            https://stackoverflow.com/a/73179947/5457853

            assertTextEquals() takes the value of Text and EditableText in your semantics
            node combines them and then does a check against the values you pass in.
            The order does not matter, just make sure to pass in the value
            of the Text as one of the arguments.
         */
        composeTestRule.onNodeWithContentDescription("input")
            .assertTextEquals("Label Text", "100")

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
