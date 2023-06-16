package com.smarttoolfactory.tutorial1_1basics.chapter5_gesture

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.waitForUpOrCancellation
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
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
import com.smarttoolfactory.tutorial1_1basics.ui.Blue400
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
fun Tutorial5_6Screen5() {
    TutorialContent()
}

@Composable
private fun TutorialContent() {
    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        StyleableTutorialText(
            text = "**PointerEventPass** determines in which direction touch will propagate. " +
                    "By default, **PointerEventPass.Main** causes motion to " +
                    "traverse from descendant to ancestor. Setting Initial will propagate " +
                    "motion from outer even if disabled Button is clicked",
            bullets = false
        )

        val context = LocalContext.current

        var passOuter by remember {
            mutableStateOf(
                PointerEventPass.Main
            )
        }

        var passMiddle by remember {
            mutableStateOf(
                PointerEventPass.Main
            )
        }

        var passInner by remember {
            mutableStateOf(
                PointerEventPass.Main
            )
        }

        val outerColor = Color(0xFFFFA000)
        val middleColor = Color(0xFFFFC107)
        val innerColor = Color(0xFFFFD54F)

        var gestureColorOuter by remember { mutableStateOf(outerColor) }
        var gestureColorMiddle by remember { mutableStateOf(middleColor) }
        var gestureColorInner by remember { mutableStateOf(innerColor) }

        ExposedSelectionMenu(title = "Outer PointerEventPass",
            index = when (passOuter) {
                PointerEventPass.Initial -> 0
                PointerEventPass.Main -> 1
                else -> 2
            },
            options = listOf("Initial", "Main", "Final"),
            onSelected = {
                passOuter = when (it) {
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
            index = when (passInner) {
                PointerEventPass.Initial -> 0
                PointerEventPass.Main -> 1
                else -> 2
            },
            options = listOf("Initial", "Main", "Final"),
            onSelected = {
                passInner = when (it) {
                    0 -> PointerEventPass.Initial
                    1 -> PointerEventPass.Main
                    else -> PointerEventPass.Final
                }
            }
        )

        val modifier = Modifier
            .shadow(4.dp, shape = RoundedCornerShape(8.dp))
            .fillMaxWidth()
            .aspectRatio(1f)

        val outerModifier = modifier
            .background(gestureColorOuter)
            .customTouch(
                pass = passOuter,
                onDown = {
                    gestureColorOuter = Blue400
                    Toast
                        .makeText(context, "Outer Touched", Toast.LENGTH_SHORT)
                        .show()
                },
                onUp = {
                    gestureColorOuter = outerColor
                    Toast
                        .makeText(context, "Outer Up", Toast.LENGTH_SHORT)
                        .show()
                }
            )
            .padding(40.dp)

        val middleModifier = modifier
            .background(gestureColorMiddle)
            .customTouch(
                pass = passMiddle,
                onDown = {
                    gestureColorMiddle = Blue400
                    Toast
                        .makeText(context, "Middle Touched", Toast.LENGTH_SHORT)
                        .show()
                },
                onUp = {
                    gestureColorMiddle = middleColor
                    Toast
                        .makeText(context, "Middle Up", Toast.LENGTH_SHORT)
                        .show()
                }
            )
            .padding(40.dp)

        val innerModifier = modifier
            .background(gestureColorInner)
            .customTouch(
                pass = passInner,
                onDown = {
                    gestureColorInner = Blue400
                    Toast
                        .makeText(context, "Inner Touched", Toast.LENGTH_SHORT)
                        .show()
                },
                onUp = {
                    gestureColorInner = innerColor
                    Toast
                        .makeText(context, "Inner Up", Toast.LENGTH_SHORT)
                        .show()
                }
            )
            .padding(30.dp)

        Column(
            modifier = Modifier
                .background(Color.LightGray)
                .fillMaxWidth()
                .padding(horizontal = 30.dp, vertical = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Column(
                modifier = outerModifier
            ) {
                Column(
                    modifier = middleModifier
                ) {
                    Column(
                        modifier = innerModifier,
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Button(
                            modifier = Modifier.fillMaxWidth(),
                            onClick = {
                                Toast.makeText(
                                    context,
                                    "Enabled Button Touched",
                                    Toast.LENGTH_SHORT
                                )
                                    .show()
                            }
                        ) {
                            Text(text = "Click")
                        }

                        Button(
                            enabled = false,
                            modifier = Modifier.fillMaxWidth(),
                            onClick = {
                                Toast.makeText(
                                    context,
                                    "Disabled Button Touched",
                                    Toast.LENGTH_SHORT
                                )
                                    .show()
                            }
                        ) {
                            Text(text = "Click")
                        }
                    }
                }
            }
        }
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
