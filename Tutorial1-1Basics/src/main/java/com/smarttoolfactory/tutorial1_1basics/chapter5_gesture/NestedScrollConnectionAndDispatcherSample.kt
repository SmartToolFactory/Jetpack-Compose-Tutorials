package com.smarttoolfactory.tutorial1_1basics.chapter5_gesture

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollDispatcher
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Preview
@Composable
fun NestedScrollConnectionAndDispatcherSample() {

    // Drag green to have it scroll parent if scroll value is not in -200 and 200 pixel
    // This example demonstrates child can dispatch scroll to parent

    val nestedScrollConnectionParent = remember {
        object : NestedScrollConnection {
            override fun onPreScroll(available: Offset, source: NestedScrollSource): Offset {
                println("Parent NestedScrollConnection onPreScroll() available: $available")

                // 🔥If we consume some amount we can scroll list while dragging green box
//                return Offset(0f, available.y * .9f)

                return super.onPreScroll(available, source)
            }

            override fun onPostScroll(
                consumed: Offset,
                available: Offset,
                source: NestedScrollSource
            ): Offset {
                println(
                    "Parent NestedScrollConnection onPostScroll() available: $available, " +
                            "consumed: $consumed"
                )
                return super.onPostScroll(consumed, available, source)
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .nestedScroll(nestedScrollConnectionParent)
            .verticalScroll(rememberScrollState())
    ) {

        val nestedScrollDispatcher = remember { NestedScrollDispatcher() }


        val nestedScrollConnection = remember {
            object : NestedScrollConnection {

                // 🔥These functions are not called
                override fun onPreScroll(available: Offset, source: NestedScrollSource): Offset {
                    println("Child NestedScrollConnection onPreScroll() available: $available")
                    return super.onPreScroll(available, source)
                }

                override fun onPostScroll(
                    consumed: Offset,
                    available: Offset,
                    source: NestedScrollSource
                ): Offset {
                    println("Child NestedScrollConnection onPostScroll() available: $available, consumed: $consumed")
                    return super.onPostScroll(consumed, available, source)
                }
            }
        }


        repeat(20) { index ->
            if (index == 3) {

                var offsetY by remember {
                    mutableFloatStateOf(0f)
                }
                Box(
                    contentAlignment = Alignment.CenterStart,
                    modifier = Modifier.fillMaxWidth()
                        .height(200.dp)
                        .border(4.dp, Color.Red)
                        .background(Color.Green)
                        .nestedScroll(nestedScrollConnection, nestedScrollDispatcher)
                        .pointerInput(Unit) {
                            detectDragGestures { change, dragAmount ->

                                val oldOffsetY = offsetY
                                val newOffsetY = (offsetY + dragAmount.y).coerceIn(-200f, 200f)
                                offsetY = newOffsetY

                                println("🔥 onDrag() before dispatching dragAmount: $dragAmount to parent")
                                val parentsConsumed = nestedScrollDispatcher.dispatchPreScroll(
                                    available = dragAmount,
                                    source = NestedScrollSource.UserInput
                                )

                                // adjust what's available to us since might have consumed smth
                                val adjustedAvailable = dragAmount.y - parentsConsumed.y

                                // we consume
                                val weConsumed = newOffsetY - oldOffsetY

                                // dispatch as a post scroll what's left after pre-scroll and our consumption
                                val totalConsumed = Offset(x = 0f, y = weConsumed) + parentsConsumed
                                val left = adjustedAvailable - weConsumed

                                println(
                                    "😹 onDrag(): offsetY: $offsetY, " +
                                            "newOffsetY: $newOffsetY, " +
                                            "parentsConsumed: ${parentsConsumed.y}, " +
                                            "weConsumed: $weConsumed, " +
                                            "left: $left"
                                )

                                nestedScrollDispatcher.dispatchPostScroll(
                                    consumed = totalConsumed,
                                    available = Offset(x = 0f, y = left),
                                    source = NestedScrollSource.UserInput
                                )
                            }
                        }
                ) {
                    Text(
                        modifier = Modifier.offset {
                            IntOffset(0, offsetY.toInt())
                        },
                        text = "Current offsetY: $offsetY",
                        fontSize = 24.sp
                    )
                }
            } else {
                Box(
                    modifier = Modifier.fillMaxWidth().height(200.dp).border(2.dp, Color.Blue)
                )
            }

        }

    }
}