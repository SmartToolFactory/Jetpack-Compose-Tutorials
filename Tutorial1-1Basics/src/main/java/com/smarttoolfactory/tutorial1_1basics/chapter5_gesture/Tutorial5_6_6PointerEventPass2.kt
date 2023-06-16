package com.smarttoolfactory.tutorial1_1basics.chapter5_gesture

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.waitForUpOrCancellation
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.PointerEventPass.Final
import androidx.compose.ui.input.pointer.PointerEventPass.Initial
import androidx.compose.ui.input.pointer.PointerEventPass.Main
import androidx.compose.ui.input.pointer.PointerInputChange
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.smarttoolfactory.tutorial1_1basics.chapter6_graphics.ExposedSelectionMenu
import com.smarttoolfactory.tutorial1_1basics.ui.components.StyleableTutorialText


/**
 * The enumeration of passes where [PointerInputChange] traverses up and down the UI tree.
 *
 * PointerInputChanges traverse throw the hierarchy in the following passes:
 *
 * 1. [Initial]: Down the tree from ancestor to descendant.
 * 2. [Main]: Up the tree from descendant to ancestor.
 * 3. [Final]: Down the tree from ancestor to descendant.
 *
 * These passes serve the following purposes:
 *
 * 1. Initial: Allows ancestors to consume aspects of [PointerInputChange] before descendants.
 * This is where, for example, a scroller may block buttons from getting tapped by other fingers
 * once scrolling has started.
 * 2. Main: The primary pass where gesture filters should react to and consume aspects of
 * [PointerInputChange]s. This is the primary path where descendants will interact with
 * [PointerInputChange]s before parents. This allows for buttons to respond to a tap before a
 * container of the bottom to respond to a tap.
 * 3. Final: This pass is where children can learn what aspects of [PointerInputChange]s were
 * consumed by parents during the [Main] pass. For example, this is how a button determines that
 * it should no longer respond to fingers lifting off of it because a parent scroller has
 * consumed movement in a [PointerInputChange].
 */
@Preview
@Composable
fun Tutorial5_6Screen6() {
    TutorialContent()
}

@Composable
private fun TutorialContent() {
    val context = LocalContext.current

    var passTop by remember {
        mutableStateOf(
            PointerEventPass.Main
        )
    }

    var passMiddle by remember {
        mutableStateOf(
            PointerEventPass.Main
        )
    }

    var passBottom by remember {
        mutableStateOf(
            PointerEventPass.Main
        )
    }

    val modifier = Modifier
        .fillMaxSize()
        .background(Color(0xffBDBDBD))
        .customTouch(
            pass = passTop,
            onDown = {
                Toast
                    .makeText(context, "Top PointerInput Down", Toast.LENGTH_SHORT)
                    .show()
            },
            onUp = {
                Toast
                    .makeText(context, "Top PointerInput Up", Toast.LENGTH_SHORT)
                    .show()
            }
        )
        .customTouch(
            pass = passMiddle,
            onDown = {
                Toast
                    .makeText(context, "Middle PointerInput Down", Toast.LENGTH_SHORT)
                    .show()
            },
            onUp = {
                Toast
                    .makeText(context, "Middle PointerInput Up", Toast.LENGTH_SHORT)
                    .show()
            }
        )
        .customTouch(
            pass = passBottom,
            onDown = {
                Toast
                    .makeText(context, "Bottom PointerInput Down", Toast.LENGTH_SHORT)
                    .show()
            },
            onUp = {
                Toast
                    .makeText(context, "Bottom PointerInput Up", Toast.LENGTH_SHORT)
                    .show()
            }
        )

    Column(
        modifier = Modifier.fillMaxSize()
    ) {

        StyleableTutorialText(
            text = "**PointerEventPass** determines in which direction touch will propagate. " +
                    "By default, **PointerEventPass.Main** causes motion to " +
                    "traverse from bottom to upper **pointerInput**. " +
                    "Changing **PointerEventPass** changes in which order which block is called.",
            bullets = false
        )

        ExposedSelectionMenu(title = "Outer PointerEventPass",
            index = when (passTop) {
                PointerEventPass.Initial -> 0
                PointerEventPass.Main -> 1
                else -> 2
            },
            options = listOf("Initial", "Main", "Final"),
            onSelected = {
                passTop = when (it) {
                    0 -> PointerEventPass.Initial
                    1 -> PointerEventPass.Main
                    else -> PointerEventPass.Final
                }
            }
        )

        ExposedSelectionMenu(title = "Middle PointerEventPass",
            index = when (passMiddle) {
                PointerEventPass.Initial -> 0
                PointerEventPass.Main -> 1
                else -> 2
            },
            options = listOf("Initial", "Main", "Final"),
            onSelected = {
                passMiddle = when (it) {
                    0 -> PointerEventPass.Initial
                    1 -> PointerEventPass.Main
                    else -> PointerEventPass.Final
                }
            }
        )

        ExposedSelectionMenu(
            title = "Inner PointerEventPass",
            index = when (passBottom) {
                PointerEventPass.Initial -> 0
                PointerEventPass.Main -> 1
                else -> 2
            },
            options = listOf("Initial", "Main", "Final"),
            onSelected = {
                passBottom = when (it) {
                    0 -> PointerEventPass.Initial
                    1 -> PointerEventPass.Main
                    else -> PointerEventPass.Final
                }
            }
        )

        Spacer(modifier = Modifier.height(20.dp))

        GestureDisplayBox(
            modifier = modifier, gestureText = "Click to see how gestures " +
                    "traverse up or down PointerInputs."
        )
    }
}

private fun Modifier.customTouch(
    pass: PointerEventPass,
    onDown: () -> Unit,
    onUp: () -> Unit
) = this.then(
    Modifier.pointerInput(pass) {
        awaitEachGesture {
            awaitFirstDown(pass = pass)
            onDown()
            waitForUpOrCancellation(pass)
            onUp()
        }
    }
)
