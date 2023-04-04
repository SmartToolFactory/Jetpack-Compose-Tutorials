package com.smarttoolfactory.tutorial1_1basics.chapter5_gesture

import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.waitForUpOrCancellation
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.smarttoolfactory.tutorial1_1basics.ui.Blue400
import com.smarttoolfactory.tutorial1_1basics.ui.Green400
import com.smarttoolfactory.tutorial1_1basics.ui.Orange400
import com.smarttoolfactory.tutorial1_1basics.ui.Pink400
import com.smarttoolfactory.tutorial1_1basics.ui.components.StyleableTutorialText

@Composable
fun Tutorial5_6Screen5() {
    TutorialContent()
}

@Preview
@Composable
private fun TutorialContent() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(20.dp)
    ) {
        StyleableTutorialText(
            text = ""
        )

        val context = LocalContext.current

        val pass = PointerEventPass.Main

        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Pink400)
                .customTouch(
                    pass = pass,
                    onDown = {
                        Toast
                            .makeText(context, "Outer Touched", Toast.LENGTH_SHORT)
                            .show()
                    },
                    onUp = {
                        Toast
                            .makeText(context, "Outer Up", Toast.LENGTH_SHORT)
                            .show()
                    }
                )
                .padding(20.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Orange400)
                    .customTouch(
                        pass = pass,
                        onDown = {
                            Toast
                                .makeText(context, "Center Touched", Toast.LENGTH_SHORT)
                                .show()
                        },
                        onUp = {
                            Toast
                                .makeText(context, "Center Up", Toast.LENGTH_SHORT)
                                .show()
                        }
                    )
                    .padding(20.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Green400)
                        .customTouch(
                            pass = pass,
                            onDown = {
                                Toast
                                    .makeText(context, "Inner Touched", Toast.LENGTH_SHORT)
                                    .show()
                            },
                            onUp = {
                                Toast
                                    .makeText(context, "Inner Up", Toast.LENGTH_SHORT)
                                    .show()
                            }
                        )
                        .padding(20.dp)
                ) {
                    Button(
                        modifier = Modifier.fillMaxWidth(),
                        onClick = {
                            Toast.makeText(context, "Enabled Button Touched", Toast.LENGTH_SHORT)
                                .show()
                        }
                    ) {
                        Text(text = "Click")
                    }

                    Button(
                        enabled = false,
                        modifier = Modifier.fillMaxWidth(),
                        onClick = {
                            Toast.makeText(context, "Disabled Button Touched", Toast.LENGTH_SHORT)
                                .show()
                        }
                    ) {
                        Text(text = "Click")
                    }
                }
            }
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .height(2000.dp)
                .background(Blue400)
        ) {

        }

    }
}

fun Modifier.customTouch(
    pass: PointerEventPass,
    onDown: () -> Unit,
    onUp: () -> Unit
) = this.then(
    Modifier.pointerInput(Unit) {
        awaitEachGesture {
            val down = awaitFirstDown(pass = pass)
            onDown()
            waitForUpOrCancellation(pass)
            onUp()
        }
    }
)

@Preview
@Composable
private fun AnimatedContentPreview() {

    var isClicked by remember {
        mutableStateOf(false)
    }

    val density = LocalDensity.current

    Column(modifier = Modifier.fillMaxSize()) {

        Spacer(modifier = Modifier.height(20.dp))

        Button(
            modifier = Modifier.fillMaxWidth(),
            onClick = {
                isClicked = !isClicked
            }) {
            Text(text = "Click")
        }

        Spacer(modifier = Modifier.height(20.dp))

        Box(modifier = Modifier
            .fillMaxSize()
            .border(1.dp, Color.Blue)) {
            Column {
                AnimatedVisibility(
                    visible = isClicked,
                    enter = slideInVertically {
                        with(density) { -50.dp.roundToPx() }
                    } + fadeIn(initialAlpha = 0.3f),
                    exit = slideOutVertically {
                        with(density) { -50.dp.roundToPx() }
                    } + fadeOut(targetAlpha = 0.3f),
                    modifier = Modifier.weight(0.50f)
                ) {

                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color.Red)
                    )
                }
            }

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Green)
            )
        }
    }
}
