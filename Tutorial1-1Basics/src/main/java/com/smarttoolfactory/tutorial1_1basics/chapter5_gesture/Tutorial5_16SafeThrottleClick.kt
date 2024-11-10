package com.smarttoolfactory.tutorial1_1basics.chapter5_gesture

import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.waitForUpOrCancellation
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.smarttoolfactory.tutorial1_1basics.ui.components.StyleableTutorialText
import com.smarttoolfactory.tutorial1_1basics.ui.components.TutorialHeader

@Preview
@Composable
fun Tutorial5_16Screen() {
    TutorialContent()
}

@Composable
private fun TutorialContent() {
    Column(
        modifier = Modifier.fillMaxSize()
    ) {

        TutorialHeader("Throttled Click")
        StyleableTutorialText(
            text = "In this sample second button uses custom **throttleClick** Modifier to invoke clicks initially and" +
                    " only after timeOut is passed for subsequent clicks.",
            bullets = false
        )
        SafeClickSample()
    }
}

fun Modifier.throttleClick(
    timeout: Long = 300,
    pass: PointerEventPass = PointerEventPass.Main,
    onClick: () -> Unit,
) = composed {
    Modifier.pointerInput(timeout) {

        var lastDownTime = 0L

        awaitEachGesture {
            val down = awaitFirstDown(
                pass = pass
            )

            val uptimeMillis = down.uptimeMillis

            val diff = (uptimeMillis - lastDownTime)

            val up = waitForUpOrCancellation(
                pass = pass
            )
            up?.let {
                if (diff >= timeout) {
                    onClick()
                    lastDownTime = uptimeMillis
                }
            }
        }
    }
}

@Preview
@Composable
private fun SafeClickSample() {

    var counter1 by remember {
        mutableIntStateOf(0)
    }

    var counter2 by remember {
        mutableIntStateOf(0)
    }

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp)
    ) {

        Button(
            modifier = Modifier.fillMaxWidth(),
            onClick = {
                counter1++
            }
        ) {
            Text("Default Counter: $counter1")
        }

        Button(
            modifier = Modifier.fillMaxWidth().throttleClick(
                pass = PointerEventPass.Initial
            ) {
                counter2++
            },
            onClick = {}
        ) {
            Text("Throttled Counter: $counter2")
        }
    }
}
