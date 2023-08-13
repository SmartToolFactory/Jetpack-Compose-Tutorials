package com.smarttoolfactory.tutorial1_1basics.chapter3_layout

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.layout
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Preview
@Composable
fun Tutorial3_3Screen2() {
    TutorialContent()
}

@Composable
private fun TutorialContent() {
    BoxWithConstraints(
        modifier = Modifier
            .border(3.dp, Color.Red)
            .layout { measurable, constraints ->

                val placeable = measurable.measure(constraints)
                println("First constraints: $constraints, placeable width: ${placeable.width}")

                layout(placeable.width, placeable.height) {
                    placeable.placeRelative(0, 0)
                }
            }
            .sizeIn(minWidth = 100.dp, maxWidth = 300.dp)
            .border(4.dp, Color.Green)
            .layout { measurable, constraints ->

                val placeable = measurable.measure(
                    constraints.copy(
                        minWidth = 420,
                        maxWidth = 650,
                        minHeight = 420,
                        maxHeight = 650
                    )
                )
                println("Second constraints: $constraints, placeable width: ${placeable.width}")

                layout(constraints.maxWidth, constraints.maxHeight) {
                    placeable.placeRelative(0, 0)
                }
            }
            .border(3.dp, Color.Yellow)
            .size(150.dp)
            .border(3.dp, Color.Magenta)
            .layout { measurable, constraints ->

                val placeable = measurable.measure(constraints)
                println("Third constraints: $constraints, placeable width: ${placeable.width}")
                layout(placeable.width, placeable.height) {
                    placeable.placeRelative(0, 0)
                }
            }
            .border(5.dp, Color.Blue)
            .size(100.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(text = "Min width: $minWidth, maxWidth: $maxWidth",
            modifier = Modifier.padding(5.dp)
        )
    }
}