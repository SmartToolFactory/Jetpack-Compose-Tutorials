package com.smarttoolfactory.tutorial1_1basics.chapter5_gesture

import android.widget.Toast
import androidx.compose.foundation.background
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.smarttoolfactory.tutorial1_1basics.chapter6_graphics.ExposedSelectionMenu
import com.smarttoolfactory.tutorial1_1basics.ui.components.StyleableTutorialText


@Composable
fun Tutorial5_6Screen6() {
    TutorialContent()
}

@Preview
@Composable
private fun TutorialContent() {
    val context = LocalContext.current

    var passTop by remember {
        mutableStateOf(
            PointerEventPass.Main
        )
    }

    var passCenter by remember {
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
            pass = passCenter,
            onDown = {
                Toast
                    .makeText(context, "Center PointerInput Down", Toast.LENGTH_SHORT)
                    .show()
            },
            onUp = {
                Toast
                    .makeText(context, "Center PointerInput Up", Toast.LENGTH_SHORT)
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
                    "traverse from descendant to ancestor. Setting Initial will propagate " +
                    "motion from outer even if disabled Button is clicked",
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

        ExposedSelectionMenu(title = "Center PointerEventPass",
            index = when (passCenter) {
                PointerEventPass.Initial -> 0
                PointerEventPass.Main -> 1
                else -> 2
            },
            options = listOf("Initial", "Main", "Final"),
            onSelected = {
                passCenter = when (it) {
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