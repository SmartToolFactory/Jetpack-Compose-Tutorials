package com.smarttoolfactory.tutorial2_2ui_testing

import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.test.assert
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.performTextClearance
import androidx.compose.ui.test.performTextInput
import androidx.compose.ui.test.performTextReplacement
import org.junit.Rule
import org.junit.Test

class Test2TextField {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun performTextInput_TextField_Test() {

        composeTestRule.setContent {
            TextFieldSample()
        }

        // set TextField text
        composeTestRule
            .onNodeWithContentDescription("input")
            // Sends the given text to this node in similar way to IME.
            .performTextInput("100")

        // assert that text is set
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

        // 🔥 This is for displaying TextField, do not use sleep in actual tests
        Thread.sleep(1000)
    }

    @Test
    fun performTextInput_twice_TextField_Test() {

        composeTestRule.setContent {
            TextFieldSample()
        }

        // set TextField text
        composeTestRule
            .onNodeWithContentDescription("input")
            // Sends the given text to this node in similar way to IME.
            .performTextInput("100")


        // add another char to TextField text
        composeTestRule
            .onNodeWithContentDescription("input")
            // Sends the given text to this node in similar way to IME.
            .performTextInput("1")

        // assert that text is set
        composeTestRule.onNodeWithContentDescription("input")
            .assert(hasText("1001", ignoreCase = true))

        // 🔥 This is for displaying TextField, do not use sleep in actual tests
        Thread.sleep(1000)
    }

    @Test
    fun performTextReplacement_TextField_Test() {

        composeTestRule.setContent {
            TextFieldSample()
        }

        // set TextField text
        composeTestRule
            .onNodeWithContentDescription("input")
            // Sends the given text to this node in similar way to IME.
            .performTextInput("100")


        // replace TextField text
        composeTestRule
            .onNodeWithContentDescription("input")
            // Replaces existing text with the given text in this node in similar way to IME.
            //This does not reflect text selection. All the text gets cleared out and new inserted.
            .performTextReplacement("200")

        // assert that text is set
        composeTestRule.onNodeWithContentDescription("input")
            .assert(hasText("200", ignoreCase = true))

        // 🔥 This is for displaying TextField, do not use sleep in actual tests
        Thread.sleep(1000)
    }

    @Test
    fun performTextClearance_TextField_Test() {

        composeTestRule.setContent {
            TextFieldSample()
        }

        // set TextField text
        composeTestRule
            .onNodeWithContentDescription("input")
            // Sends the given text to this node in similar way to IME.
            .performTextInput("100")


        // replace TextField text
        composeTestRule
            .onNodeWithContentDescription("input")
            // Clears the text in this node in similar way to IME.
            .performTextClearance()

        // assert that text is set
        composeTestRule.onNodeWithContentDescription("input")
            .assert(hasText("", ignoreCase = true))

        // 🔥 This is for displaying TextField, do not use sleep in actual tests
        Thread.sleep(1000)
    }

}

@Composable
private fun TextFieldSample() {
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