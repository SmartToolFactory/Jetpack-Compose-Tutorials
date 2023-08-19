package com.smarttoolfactory.tutorial1_1basics.chapter3_layout

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.Measurable
import androidx.compose.ui.layout.MeasurePolicy
import androidx.compose.ui.layout.MeasureResult
import androidx.compose.ui.layout.MeasureScope
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.smarttoolfactory.tutorial1_1basics.ui.components.StyleableTutorialText
import com.smarttoolfactory.tutorial1_1basics.ui.components.TutorialHeader

@Preview
@Composable
fun Tutorial3_7Screen2() {
    TutorialContent()
}

@Composable
private fun TutorialContent() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(10.dp)
    ) {

        TutorialHeader(text = "Composition-Layout Phases2")
        StyleableTutorialText(
            text = "Layout phase of a Composable is called only when content needs " +
                    "new layout measurement and placement. In this example custom Layout with " +
                    "red border has 140.dp width and calls Layout only when content width " +
                    "goes above or below 140.dp to increase or decrease height.\n" +
                    "Layout with blue border calls whenever content changes because it needs to " +
                    "measure and place according to new content params",
            bullets = false
        )
        LayoutPhasesSample()
    }
}


@Composable
private fun LayoutPhasesSample() {
    var text by remember {
        mutableStateOf("Type Text")
    }

    OutlinedTextField(
        modifier = Modifier
            .fillMaxWidth(),
        value = text,
        onValueChange = { text = it }
    )

    Spacer(modifier = Modifier.height(10.dp))

    // 🔥 This one calls Layout's MeasureScope and PlacementScope only Text width
    // passed above or below 140.dp
    Column(Modifier.fillMaxSize()) {
        CustomLayout(
            modifier = Modifier
                .width(140.dp)
                .border(2.dp, Color.Red),
            label = "🚀 With 140.dp with"
        ) {
            SideEffect {
                println("🚀 CustomLayout Scope 140.dp recomposing...")
            }
            Text(text = text, fontSize = 20.sp)
        }


        // 🔥🔥 This one calls Layout's MeasureScope and PlacementScope every time Text width
        // changes because it has to adjust width and height its content(Text) dimensions change
        CustomLayout(
            modifier = Modifier
                .border(2.dp, Color.Blue),
            label = "🔥 No size"
        ) {
            SideEffect {
                println("🔥 CustomLayout Scope No size recomposing...")
            }
            Text(text = text, fontSize = 20.sp)
        }
    }
}

@Composable
private fun CustomLayout(
    modifier: Modifier = Modifier,
    label: String,
    content: @Composable () -> Unit
) {

    // This measure policy is created every time CustomLayout is recomposed
    val measurePolicy = object : MeasurePolicy {
        override fun MeasureScope.measure(
            measurables: List<Measurable>,
            constraints: Constraints
        ): MeasureResult {
            val placeables = measurables.map { measurable: Measurable ->
                measurable.measure(constraints)
            }

            val totalWidth = placeables.maxOf { it.width }
            val totalHeight = placeables.sumOf { it.height }

            println(
                "🍏$label MeasureScope " +
                        "totalWidth: $totalWidth, " +
                        "totalHeight: $totalHeight"
            )

            var y = 0
            return layout(totalWidth, totalHeight) {
                println("🍏🍏$label PlacementScope")
                placeables.forEach {
                    it.place(0, y)
                    y += it.height
                }
            }
        }

    }

    SideEffect {
        println("😀 $label CustomComposable composed measurePolicy: $measurePolicy")
    }

    Layout(
        modifier = modifier,
        content = content,
        measurePolicy = measurePolicy
    )
}
