package com.smarttoolfactory.tutorial1_1basics.chapter3_layout

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.Measurable
import androidx.compose.ui.layout.Placeable
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.dp
import com.smarttoolfactory.tutorial1_1basics.ui.Green400
import com.smarttoolfactory.tutorial1_1basics.ui.Orange400
import com.smarttoolfactory.tutorial1_1basics.ui.Pink400
import com.smarttoolfactory.tutorial1_1basics.ui.components.StyleableTutorialText
import com.smarttoolfactory.tutorial1_1basics.ui.components.TutorialHeader
import com.smarttoolfactory.tutorial1_1basics.ui.components.TutorialText2

@Preview
@Composable
fun Tutorial3_2Screen6() {
    TutorialContent()
}

@Composable
private fun TutorialContent() {
    Column(modifier = Modifier
        .fillMaxSize()
        .padding(10.dp)) {
        ConstraintsAndSiblingsSample()
    }
}

@Composable
private fun ConstraintsAndSiblingsSample() {

    var layoutWidth by remember { mutableStateOf(700f) }

    TutorialHeader(text = "Sibling Constraints")

    StyleableTutorialText(
        text = "In this example we set layout width via slider but when **layoutWidth** " +
                "is not in range of min-max width of **Constraints** coming " +
                "from **Modifier** layout content(Orange) is placed as in previous" +
                " tutorial. Orange content is measured in a range between 0-and min of layout " +
                "width and Constraints.maxWidth.",
        bullets = false
    )

    TutorialText2(
        text = "No size Modifier 0- parent width"
    )
    Row(modifier = Modifier.fillMaxWidth()) {

        CustomLayout(
            modifier = Modifier.border(2.dp, Green400),
            layoutWidth = layoutWidth.toInt()
        ) {
            BoxWithConstraints {
                Text(
                    text = "Constraints: $constraints",
                    color = Color.White,
                    modifier = Modifier
                        .shadow(2.dp, RoundedCornerShape(8.dp))
                        .background(Orange400)
                )
            }
        }
        Box(
            modifier = Modifier
                .width(100.dp)
                .height(50.dp)
                .border(2.dp, Pink400)
        )
    }

    Spacer(modifier = Modifier.height(20.dp))

    TutorialText2(
        text = "Modifier.width(200.dp)"
    )
    Row(modifier = Modifier.fillMaxWidth()) {

        // 🔥 When layoutWidth is not equal to pixel value of 200.dp content is moved
        CustomLayout(
            modifier = Modifier
                .border(2.dp, Green400)
                .width(200.dp),
            layoutWidth = layoutWidth.toInt()
        ) {
            BoxWithConstraints {
                Text(
                    text = "Constraints: $constraints",
                    color = Color.White,
                    modifier = Modifier
                        .shadow(2.dp, RoundedCornerShape(8.dp))
                        .background(Orange400)
                )
            }
        }

        Box(
            modifier = Modifier
                .width(100.dp)
                .height(50.dp)
                .border(2.dp, Pink400)
        )
    }

    Spacer(modifier = Modifier.height(20.dp))

    TutorialText2(
        text = "Modifier.widthIn(min = 100.dp, max = 200.dp)"
    )
    // 🔥 When layoutWidth is not in range of pixel value of 100.dp and 200.dp content is moved
    Row(modifier = Modifier.fillMaxWidth()) {

        CustomLayout(
            modifier = Modifier
                .border(2.dp, Green400)
                .widthIn(min = 100.dp, max = 200.dp),
            layoutWidth = layoutWidth.toInt()
        ) {
            BoxWithConstraints {
                Text(
                    text = "Constraints: $constraints",
                    color = Color.White,
                    modifier = Modifier
                        .shadow(2.dp, RoundedCornerShape(8.dp))
                        .background(Orange400)
                )
            }
        }

        Box(
            modifier = Modifier
                .width(100.dp)
                .height(50.dp)
                .border(2.dp, Pink400)
        )
    }

    TutorialText2(
        text = "Width of the layout(width) of Composable. If it's not in bounds " +
                "of original Constraints " +
                "content is placed difference between " +
                "layout width and (min/max) constraints width"
    )

    SliderWithLabel(
        label = "Layout Width: ${layoutWidth.toInt()}",
        value = layoutWidth
    ) {
        layoutWidth = it
    }

}


@Composable
private fun CustomLayout(
    modifier: Modifier = Modifier,
    layoutWidth: Int,
    content: @Composable () -> Unit
) {

    Layout(
        modifier = modifier,
        content = content
    ) { measurables: List<Measurable>, constraints: Constraints ->


        val placeables = measurables.map { measurable: Measurable ->
            measurable.measure(
                constraints.copy(
                    minWidth = 0,
                    maxWidth = layoutWidth.coerceAtMost(constraints.maxWidth),
                    // This is for demonstration purposed
                    // to not increase height a lot to not cause overflow from screen
                    maxHeight = 300
                )
            )
        }

        val totalHeight = placeables.sumOf { it.height }
        var posY = 0
        // 🔥🔥 Changing  width changes where this Composable is positioned if it's not
        // in constraints range.
        // For instance, if layoutWidth is 600px with Constraints maxWidth=550px content of
        // this Composable is pushed 25px to left
        layout(width = layoutWidth, height = totalHeight) {
            placeables.forEach { placeable: Placeable ->
                placeable.placeRelative(0, posY)
                posY += placeable.height
            }
        }
    }
}
