package com.smarttoolfactory.tutorial1_1basics.chapter5_gesture

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.waitForUpOrCancellation
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.smarttoolfactory.tutorial1_1basics.chapter2_material_widgets.CheckBoxWithTextRippleFullRow
import com.smarttoolfactory.tutorial1_1basics.chapter6_graphics.ExposedSelectionMenu
import com.smarttoolfactory.tutorial1_1basics.ui.Blue400
import com.smarttoolfactory.tutorial1_1basics.ui.components.StyleableTutorialText

@Composable
fun Tutorial5_6Screen8() {
    TutorialContent()
}

@Preview
@Composable
private fun TutorialContent() {
    Column(modifier = Modifier.fillMaxSize()) {
        ScrollTouchSample()
    }
}

@Composable
private fun ScrollTouchSample() {
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

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            items(100) {

                val modifier = Modifier
                    .shadow(4.dp, shape = RoundedCornerShape(8.dp))

                var gestureColorOuter by remember { mutableStateOf(outerColor) }
                var gestureColorCenter by remember { mutableStateOf(centerColor) }

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
                    .background(gestureColorCenter)
                    .customTouch(
                        consume = innerConsumeDown,
                        pass = passInner,
                        onDown = {
                            gestureColorCenter = Blue400
                            Toast
                                .makeText(context, "Inner Touched", Toast.LENGTH_SHORT)
                                .show()
                        },
                        onUp = {
                            gestureColorCenter = centerColor
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

            }
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
