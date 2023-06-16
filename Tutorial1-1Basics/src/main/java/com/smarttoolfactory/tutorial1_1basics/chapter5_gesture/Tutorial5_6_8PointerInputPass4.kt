package com.smarttoolfactory.tutorial1_1basics.chapter5_gesture

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Button
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.PointerEvent
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.PointerEventPass.Final
import androidx.compose.ui.input.pointer.PointerEventPass.Initial
import androidx.compose.ui.input.pointer.PointerEventPass.Main
import androidx.compose.ui.input.pointer.PointerInputChange
import androidx.compose.ui.input.pointer.PointerInputScope
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.input.pointer.positionChange
import androidx.compose.ui.input.pointer.positionChanged
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.toSize
import com.smarttoolfactory.tutorial1_1basics.chapter2_material_widgets.CheckBoxWithTextRippleFullRow
import com.smarttoolfactory.tutorial1_1basics.chapter6_graphics.ExposedSelectionMenu
import com.smarttoolfactory.tutorial1_1basics.ui.Blue400
import com.smarttoolfactory.tutorial1_1basics.ui.Pink400
import com.smarttoolfactory.tutorial1_1basics.ui.components.StyleableTutorialText
import kotlin.math.roundToInt

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
fun Tutorial5_6Screen8() {
    TutorialContent()
}

@Composable
private fun TutorialContent() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(
                rememberScrollState()
            )
    ) {

        val sizeOuter = 250.dp
        val sizeInner = 125.dp

        var size by remember { mutableStateOf(Size.Zero) }

        val context = LocalContext.current

        var offsetOuter by remember { mutableStateOf(Offset.Zero) }
        var offsetInner by remember { mutableStateOf(Offset.Zero) }

        val outerColor = Color(0xFFFFA000)
        val innerColor = Color(0xFFFFD54F)

        var gestureColorOuter by remember { mutableStateOf(outerColor) }
        var gestureColorInner by remember { mutableStateOf(innerColor) }

        /*
             FLAGS for consuming events which effects gesture propagation
         */
        var outerRequireUnconsumed by remember { mutableStateOf(true) }
        var outerConsumeDown by remember { mutableStateOf(false) }
        var outerConsumePositionChange by remember { mutableStateOf(false) }
        var passOuter by remember {
            mutableStateOf(
                PointerEventPass.Main
            )
        }

        var innerRequireUnconsumed by remember { mutableStateOf(true) }
        var innerConsumeDown by remember { mutableStateOf(false) }
        var innerConsumePositionChange by remember { mutableStateOf(false) }
        var passInner by remember {
            mutableStateOf(
                PointerEventPass.Main
            )
        }

        val outerModifier = Modifier
            // 🔥 Change offset(position) of outer box by dragging it
            .offset {
                IntOffset(offsetOuter.x.roundToInt(), offsetOuter.y.roundToInt())
            }
            .border(4.dp, color = gestureColorOuter, shape = RoundedCornerShape(8.dp))
            .size(sizeOuter)
            .pointerInput(
                outerRequireUnconsumed,
                outerConsumeDown,
                outerConsumePositionChange,
                passOuter
            ) {
                customDrag(
                    requireConsumed = outerRequireUnconsumed,
                    consumeDown = outerConsumeDown,
                    consumePositionChange = outerConsumePositionChange,
                    pass = passOuter,
                    onGestureStart = {
                        gestureColorOuter = Pink400

                        Toast
                            .makeText(context, "Outer Gesture Start", Toast.LENGTH_SHORT)
                            .show()
                    },
                    onGesture = { pointerInputChange: PointerInputChange ->
                        gestureColorOuter = Blue400

                        // Calculate offset for this composable after move
                        val summed = offsetOuter + pointerInputChange.positionChange()
                        val newValue = Offset(
                            x = summed.x.coerceIn(0f, size.width - sizeOuter.toPx()),
                            y = summed.y.coerceIn(0f, size.height - sizeOuter.toPx())
                        )
                        offsetOuter = newValue
                    },
                    onGestureEnd = {
                        gestureColorOuter = innerColor
                        Toast
                            .makeText(context, "Outer Gesture END", Toast.LENGTH_SHORT)
                            .show()
                    }
                )
            }


        val innerModifier = Modifier
            // 🔥 Change offset(position) of outer box by dragging it
            .offset {
                IntOffset(offsetInner.x.roundToInt(), offsetInner.y.roundToInt())
            }
            .border(4.dp, color = gestureColorInner, shape = RoundedCornerShape(8.dp))
            .size(sizeInner)
            .pointerInput(
                innerRequireUnconsumed,
                innerConsumeDown,
                innerConsumePositionChange,
                passInner
            ) {
                customDrag(
                    requireConsumed = innerRequireUnconsumed,
                    consumeDown = innerConsumeDown,
                    consumePositionChange = innerConsumePositionChange,
                    pass = passInner,
                    onGestureStart = {
                        gestureColorInner = Pink400

                        Toast
                            .makeText(context, "Inner Gesture Start", Toast.LENGTH_SHORT)
                            .show()
                    },
                    onGesture = { pointerInputChange: PointerInputChange ->
                        gestureColorInner = Blue400

                        val summed = offsetInner + pointerInputChange.positionChange()
                        val newValue = Offset(
                            x = summed.x.coerceIn(
                                -sizeInner.toPx() / 2,
                                sizeOuter.toPx() - 3 * sizeInner.toPx() / 2
                            ),
                            y = summed.y.coerceIn(
                                -sizeInner.toPx() / 2,
                                sizeOuter.toPx() - 3 * sizeInner.toPx() / 2
                            )
                        )
                        offsetInner = newValue
                    },
                    onGestureEnd = {
                        gestureColorInner = outerColor

                        Toast
                            .makeText(context, "Inner Gesture END", Toast.LENGTH_SHORT)
                            .show()
                    }
                )
            }

        StyleableTutorialText(
            text = "Use **PointerEventPass** to change gesture direction and **consume** to " +
                    "intercept or prevent " +
                    "next **pointerInput** getting event",
            bullets = false
        )

        Box(
            Modifier
                .padding(vertical = 8.dp, horizontal = 12.dp)
                .fillMaxWidth()
                .background(Color.LightGray)
                .height(300.dp)
                .onSizeChanged {
                    size = it.toSize()
                }
        ) {
            Box(
                modifier = outerModifier,
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = innerModifier,
                    contentAlignment = Alignment.Center

                ) {
                    Button(
                        onClick = {
                            Toast.makeText(context, "Button Clicked", Toast.LENGTH_SHORT).show()
                        }
                    ) {
                        Text(
                            modifier = Modifier.padding(horizontal = 8.dp),
                            text = "Click"
                        )
                    }
                }
            }
        }


        /*
            CONTROLS
        */
        var innerCheckBoxesExpanded by remember { mutableStateOf(true) }
        var outerCheckBoxesExpanded by remember { mutableStateOf(true) }

        Row(
            modifier = Modifier
                .padding(horizontal = 4.dp)
                .fillMaxWidth()
                .clickable {
                    innerCheckBoxesExpanded = !innerCheckBoxesExpanded
                },
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                "INNER Composable", modifier = Modifier
                    .weight(1f)

                    .padding(horizontal = 10.dp, vertical = 8.dp),
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                color = innerColor
            )
            // Change vector drawable to expand more or less based on state of expanded
            Icon(
                imageVector = if (innerCheckBoxesExpanded) Icons.Filled.ExpandLess
                else Icons.Filled.ExpandMore,
                contentDescription = null
            )
        }

        AnimatedVisibility(visible = innerCheckBoxesExpanded) {
            Column {
                CheckBoxWithTextRippleFullRow(
                    label = "innerRequireUnconsumed",
                    innerRequireUnconsumed
                ) {
                    innerRequireUnconsumed = it
                }
                CheckBoxWithTextRippleFullRow(label = "innerConsumeDown", innerConsumeDown) {
                    innerConsumeDown = it
                }
                CheckBoxWithTextRippleFullRow(
                    label = "innerConsumePositionChange",
                    innerConsumePositionChange
                ) {
                    innerConsumePositionChange = it
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
            }
        }

        Row(
            modifier = Modifier
                .padding(horizontal = 4.dp)
                .fillMaxWidth()
                .clickable {
                    outerCheckBoxesExpanded = !outerCheckBoxesExpanded
                },
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                "OUTER Composable", modifier = Modifier
                    .weight(1f)

                    .padding(horizontal = 10.dp, vertical = 8.dp),
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                color = outerColor
            )
            // Change vector drawable to expand more or less based on state of expanded
            Icon(
                imageVector = if (outerCheckBoxesExpanded) Icons.Filled.ExpandLess
                else Icons.Filled.ExpandMore,
                contentDescription = null
            )
        }
        AnimatedVisibility(visible = outerCheckBoxesExpanded) {
            Column {
                CheckBoxWithTextRippleFullRow(
                    label = "outerRequireUnconsumed",
                    outerRequireUnconsumed
                ) {
                    outerRequireUnconsumed = it
                }
                CheckBoxWithTextRippleFullRow(label = "outerConsumeDown", outerConsumeDown) {
                    outerConsumeDown = it
                }
                CheckBoxWithTextRippleFullRow(
                    label = "outerConsumePositionChange",
                    outerConsumePositionChange
                ) {
                    outerConsumePositionChange = it
                }


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

            }
        }
    }
}

private suspend fun PointerInputScope.customDrag(
    requireConsumed: Boolean = false,
    consumeDown: Boolean = true,
    consumePositionChange: Boolean = true,
    pass: PointerEventPass = PointerEventPass.Main,
    onGestureStart: (PointerInputChange) -> Unit = {},
    onGesture: (change: PointerInputChange) -> Unit,
    onGestureEnd: () -> Unit = {}
) {
    awaitEachGesture {

        val down = awaitFirstDown(
            requireUnconsumed = requireConsumed,
            pass = pass
        )

        onGestureStart(down)

        if (consumeDown) {
            down.consume()
        }

        // Main pointer is the one that is down initially
        var pointerId = down.id

        do {

            val event: PointerEvent = awaitPointerEvent(
                pass = pass
            )

            val pointerInputChange =
                event.changes.firstOrNull { it.id == pointerId }
                    ?: event.changes.first()

            // Next time will check same pointer with this id
            pointerId = pointerInputChange.id

            onGesture(pointerInputChange)

            if (consumePositionChange) {
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
