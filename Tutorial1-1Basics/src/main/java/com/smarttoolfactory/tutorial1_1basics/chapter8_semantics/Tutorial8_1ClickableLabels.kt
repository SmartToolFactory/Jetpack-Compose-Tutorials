package com.smarttoolfactory.tutorial1_1basics.chapter8_semantics

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.onClick
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.smarttoolfactory.tutorial1_1basics.ui.Green400
import com.smarttoolfactory.tutorial1_1basics.ui.Red400
import com.smarttoolfactory.tutorial1_1basics.ui.components.StyleableTutorialText
import com.smarttoolfactory.tutorial1_1basics.ui.components.TutorialHeader

@Preview
@Composable
fun Tutorial8Screen1() {
    // 🔥 You need to enable talkback on your emulator to observe
    // semantics nodes in Composables or check out Ui test tutorials
    TutorialContent()
}

@Composable
private fun TutorialContent() {
    Column(
        modifier = Modifier.fillMaxSize()
    ) {

        TutorialHeader("Click Labels")

        StyleableTutorialText(
            // This removes Text semantics to not announce anything
            modifier = Modifier.clearAndSetSemantics { },
            text = "1-) **Modifier.clickable(onClickLabel){}** " +
                    "changes default announcement of Talkback"
        )

        Box(
            Modifier
                .fillMaxWidth()
                .height(100.dp)
                .background(Green400)
                // TalkBack announces "Double tap to activate.
                .clickable {

                },
            contentAlignment = Alignment.Center
        ) {
            Text("Default Clickable", color = Color.White)
        }

        Spacer(modifier = Modifier.height(16.dp))

        Box(
            Modifier
                .fillMaxWidth()
                .height(100.dp)
                .background(Red400)
                .clickable(
                    // semantic / accessibility label for the onClick action
                    // TalkBack announces "Double tap to invoke custom action.
                    onClickLabel = "invoke custom action"
                ) {

                },
            contentAlignment = Alignment.Center
        ) {
            Text("Clickable with onClickLabel", color = Color.White)
        }

        Spacer(modifier = Modifier.height(16.dp))

        StyleableTutorialText(
            // This removes Text semantics to not announce anything
            modifier = Modifier.clearAndSetSemantics { },
            text = "2-) When a Composable has click action such as Button or Card using " +
                    "Modifier.semantics{onClick(label, action)}"
        )

        Button(
            modifier = Modifier.fillMaxWidth(),
            onClick = {}
        ) {
            Text("Button")
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            modifier = Modifier
                .fillMaxWidth()
                .semantics {
                    // TalkBack announces "Double tap to invoke semantics action.
                    onClick("invoke semantics action", action = null)
                },
            onClick = {}
        ) {
            Text("Button with semantics")
        }
    }
}

