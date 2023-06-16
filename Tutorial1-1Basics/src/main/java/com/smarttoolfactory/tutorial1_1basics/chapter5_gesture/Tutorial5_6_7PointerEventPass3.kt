package com.smarttoolfactory.tutorial1_1basics.chapter5_gesture

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.waitForUpOrCancellation
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
import com.smarttoolfactory.tutorial1_1basics.chapter2_material_widgets.CheckBoxWithTextRippleFullRow
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
fun Tutorial5_6Screen7() {
    TutorialContent()
}

@Composable
private fun TutorialContent() {
    Column(modifier = Modifier.fillMaxSize()) {
        ScrollTouchSample()
    }
}

@Composable
private fun ScrollTouchSample() {

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {

        val context = LocalContext.current

        var passOuter by remember {
            mutableStateOf(
                PointerEventPass.Main
            )
        }

        var passInner by remember {
            mutableStateOf(
                PointerEventPass.Main
            )
        }

        var outerConsumeDown by remember { mutableStateOf(false) }
        var innerConsumeDown by remember { mutableStateOf(false) }

        StyleableTutorialText(
            text = "Use **PointerEventPass** to change gesture direction and **consume** to " +
                    "intercept or prevent " +
                    "next **pointerInput** getting event",
            bullets = false
        )

        val outerColor = Color(0xFFFFA000)
        val innerColor = Color(0xFFFFC107)

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
        CheckBoxWithTextRippleFullRow(
            label = "outerConsumeDown",
            outerConsumeDown
        ) {
            outerConsumeDown = it
        }

        ExposedSelectionMenu(title = "Inner PointerEventPass",
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

        CheckBoxWithTextRippleFullRow(label = "innerConsumeDown", innerConsumeDown) {
            innerConsumeDown = it
        }

        Spacer(modifier = Modifier.height(20.dp))


        repeat(10) {
            val modifier = Modifier
                .shadow(4.dp, shape = RoundedCornerShape(8.dp))

            var gestureColorOuter by remember { mutableStateOf(outerColor) }
            var gestureColorInner by remember { mutableStateOf(innerColor) }

            val outerModifier = modifier
                .fillMaxWidth()
                .background(gestureColorOuter)
                .customTouch(
                    consume = outerConsumeDown,
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
                .padding(30.dp)

            val innerModifier = modifier
                .fillMaxWidth()
                .background(gestureColorInner)
                .customTouch(
                    consume = innerConsumeDown,
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

            Box(
                modifier = outerModifier,
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = innerModifier,
                    contentAlignment = Alignment.Center
                ) {
                    Button(
                        modifier = Modifier.fillMaxWidth(),
                        onClick = {
                            Toast.makeText(
                                context,
                                "Button Touched",
                                Toast.LENGTH_SHORT
                            )
                                .show()
                        }
                    ) {
                        Text(text = "Click")
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

private fun Modifier.customTouch(
    consume: Boolean = false,
    pass: PointerEventPass,
    onDown: () -> Unit,
    onUp: () -> Unit
) = this.then(
    Modifier.pointerInput(pass, consume) {
        awaitEachGesture {
            val down = awaitFirstDown(pass = pass)
            if (consume) {
                down.consume()
            }
            onDown()
            val up = waitForUpOrCancellation(pass)
            onUp()
            up?.let {
                if (consume) {
                    it.consume()
                }
            }
        }
    }
)
