package com.smarttoolfactory.tutorial1_1basics.chapter3_layout

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.layout
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.smarttoolfactory.tutorial1_1basics.ui.components.StyleableTutorialText

@Preview
@Composable
fun Tutorial3_3Screen2() {
    TutorialContent()
}

@Composable
private fun TutorialContent() {
    Column(
        Modifier.fillMaxSize()
    ) {
        StyleableTutorialText(
            text = "**Modifier.layout{}** creates a LayoutModifier that allows " +
                    "changing how the wrapped element is measured and laid out.\n" +
                    "layout order is from bottom to top but Constraints come from top to bottom " +
                    "and disregarded or adjusted to min or max of existing Constraints " +
                    "when it's not in bounds.",
            bullets = false
        )

        // Also change placement position to show it affects Modifiers or
        // Constraints after Modifier.layout

        /*
            Prints:
            // ! These values are on a Pixel 5 emulator
        I  🍎 Bottom Measurement phase  constraints: Constraints(minWidth = 495, maxWidth = 495, minHeight = 495, maxHeight = 495), placeable width: 495
        I  🍏 Middle Measurement phase constraints: Constraints(minWidth = 275, maxWidth = 825, minHeight = 0, maxHeight = 1678), placeable width: 495
        I  🌻Top Measurement phase constraints: Constraints(minWidth = 0, maxWidth = 1080, minHeight = 0, maxHeight = 1678), placeable width: 825
        I  🌻🌻 Top Placement Phase
        I  🍏🍏 Middle Placement Phase
        I  🍎🍎 Bottom Placement Phase
         */
        BoxWithConstraints(
            modifier = Modifier
                .border(3.dp, Color.Red)
                // This layout's Constraints come from parent (0-parent width, 0-parent height)
                .layout { measurable, constraints ->

                    val placeable = measurable.measure(constraints)
                    println(
                        "🌻Top Measurement phase " +
                                "constraints: $constraints, placeable width: ${placeable.width}"
                    )

                    layout(constraints.maxWidth, constraints.maxHeight) {
                        println("🌻🌻 Top Placement Phase")
                        placeable.placeRelative(50, 0)
                    }
                }
                // 🔥 This sizeIn range is passed to bottom Modifier.layout
                .sizeIn(minWidth = 100.dp, maxWidth = 300.dp)
                .border(4.dp, Color.Green, RoundedCornerShape(8.dp))
                .layout { measurable, constraints ->

                    // 🔥Measuring this Measurable with this Constraints
                    // passes it to next LayoutModifier or LayoutModifierNode
                    val placeable = measurable.measure(
                        constraints.copy(
                            minWidth = 180.dp.roundToPx(),
                            maxWidth = 250.dp.roundToPx(),
                            minHeight = 180.dp.roundToPx(),
                            maxHeight = 250.dp.roundToPx()
                        )
                    )
                    println(
                        "🍏 Middle Measurement phase " +
                                "constraints: $constraints, placeable width: ${placeable.width}"
                    )

                    layout(constraints.maxWidth, constraints.maxHeight) {
                        println("🍏🍏 Middle Placement Phase")
                        placeable.placeRelative(0, 50)
                    }
                }
                .border(2.dp, Color.Yellow, RoundedCornerShape(8.dp))
                // Uncomment size modifiers to see how Constraints change
                // 🔥🔥 This Constraints minWidth = 150.dp, maxWidth = 150.dp is not
                // in bounds of Constraints that placeable measured above
                // Because it's smaller than minWidth, minWidth and maxWidth
                // is changed to 180.dp from layout above
                .size(150.dp)
                // This Constraints minWidth = 240.dp, maxWidth = 240.dp is valid
                // for 180.dp-250.dp range
//                .size(240.dp)
                .border(5.dp, Color.Magenta, RoundedCornerShape(8.dp))
                .layout { measurable, constraints ->

                    val placeable = measurable.measure(constraints)
                    println(
                        "🍎 Bottom Measurement phase  " +
                                "constraints: $constraints, placeable width: ${placeable.width}"
                    )
                    layout(placeable.width, placeable.height) {
                        println("🍎🍎 Bottom Placement Phase")
                        placeable.placeRelative(-50, -50)
                    }
                }
                .border(5.dp, Color.Blue, RoundedCornerShape(8.dp))
                .size(100.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "Min width: $minWidth\n" +
                        "maxWidth: $maxWidth",
                modifier = Modifier.padding(5.dp)
            )
        }
    }
}