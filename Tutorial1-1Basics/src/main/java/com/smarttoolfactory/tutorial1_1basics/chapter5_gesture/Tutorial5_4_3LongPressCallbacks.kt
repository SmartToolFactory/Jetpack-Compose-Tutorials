package com.smarttoolfactory.tutorial1_1basics.chapter5_gesture

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.PointerEvent
import androidx.compose.ui.input.pointer.PointerInputChange
import androidx.compose.ui.input.pointer.PointerInputScope
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.smarttoolfactory.tutorial1_1basics.ui.components.StyleableTutorialText
import kotlinx.coroutines.coroutineScope

@Preview
@Composable
fun Tutorial5_4Screen4() {
    TutorialContent()
}

@Preview
@Composable
private fun TutorialContent() {
    Column(
        modifier = Modifier
            .padding(8.dp)
            .fillMaxSize()
    ) {
        StyleableTutorialText(
            text = "Using **PointerInputScope** extension functions create " +
                    "callbacks for press and long press events.",
            bullets = false
        )

        var gestureColor by remember { mutableStateOf(Color.LightGray) }
        val context = LocalContext.current

        Box(
            Modifier
                .fillMaxWidth()
                .background(gestureColor)
                .height(200.dp)
                .background(gestureColor)
                .pointerInput(Unit) {
                    detectPressGestures(
                        onPressStart = {
                            gestureColor = Color.Yellow
                            Toast
                                .makeText(
                                    context,
                                    "Press Start",
                                    Toast.LENGTH_SHORT
                                )
                                .show()
                        },
                        onLongPress = {
                            gestureColor = Color.Blue
                            Toast
                                .makeText(
                                    context,
                                    "Long Press!",
                                    Toast.LENGTH_SHORT
                                )
                                .show()

                        },
                        onLongPressEnd = {
                            gestureColor = Color.Black
                            Toast
                                .makeText(
                                    context,
                                    "Long Press ENDS",
                                    Toast.LENGTH_SHORT
                                )
                                .show()
                        },
                        onPressEnd = {
                            Toast
                                .makeText(
                                    context,
                                    "Press ENDS",
                                    Toast.LENGTH_SHORT
                                )
                                .show()
                            gestureColor = Color.Cyan
                        }
                    )
                }
        ) {

        }

    }
}


suspend fun PointerInputScope.detectPressGestures(
    onLongPress: ((Offset) -> Unit)? = null,
    onPressStart: ((Offset) -> Unit)? = null,
    onPressEnd: (() -> Unit)? = null,
    onLongPressEnd: (() -> Unit)? = null,
) = coroutineScope {

    awaitEachGesture {
        val down = awaitFirstDown()
        down.consume()

        val downTime = System.currentTimeMillis()
        onPressStart?.invoke(down.position)

        val longPressTimeout = onLongPress?.let {
            viewConfiguration.longPressTimeoutMillis
        } ?: (Long.MAX_VALUE / 2)

        var longPressInvoked = false

        do {
            val event: PointerEvent = awaitPointerEvent()
            val currentTime = System.currentTimeMillis()

            if (!longPressInvoked && currentTime - downTime >= longPressTimeout) {
                onLongPress?.invoke(event.changes.first().position)
                longPressInvoked = true
            }

            event.changes
                .forEach { pointerInputChange: PointerInputChange ->
                    pointerInputChange.consume()
                }


        } while (event.changes.any { it.pressed })

        if (longPressInvoked) {
            onLongPressEnd?.invoke()
        } else {
            onPressEnd?.invoke()
        }

    }
}
