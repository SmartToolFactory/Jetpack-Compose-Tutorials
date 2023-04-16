package com.smarttoolfactory.tutorial1_1basics.chapter5_gesture

import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.waitForUpOrCancellation
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.PointerEvent
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.PointerInputChange
import androidx.compose.ui.input.pointer.PointerInputScope
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.input.pointer.positionChanged
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import com.smarttoolfactory.tutorial1_1basics.chapter2_material_widgets.CheckBoxWithTextRippleFullRow
import com.smarttoolfactory.tutorial1_1basics.chapter6_graphics.ExposedSelectionMenu
import com.smarttoolfactory.tutorial1_1basics.ui.components.StyleableTutorialText

@Composable
fun Tutorial5_6Screen7() {
    TutorialContent()
}

@Preview
@Composable
private fun TutorialContent() {
    Column(
        modifier = Modifier.fillMaxSize()
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
        val centerColor = Color(0xFFFFC107)

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

    }
}

private suspend fun PointerInputScope.customDrag(
    consume: Boolean = true,
    pass: PointerEventPass = PointerEventPass.Main,
    onGestureStart: (PointerInputChange) -> Unit = {},
    onGesture: (changes: List<PointerInputChange>) -> Unit,
    onGestureEnd: () -> Unit = {}
) {
    awaitEachGesture {

        val down = awaitFirstDown(
            requireUnconsumed = false,
            pass = pass
        )

        onGestureStart(down)

        do {

            val event: PointerEvent = awaitPointerEvent(
                pass = pass
            )

            onGesture(event.changes)

            if (consume) {
                event.changes.forEach {
                    if (it.positionChanged()) {
                        it.consume()
                    }
                }
            }

        } while (event.changes.any { it.pressed })
        onGestureEnd()
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
