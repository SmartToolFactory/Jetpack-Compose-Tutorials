package com.smarttoolfactory.tutorial1_1basics.chapter3_layout

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Button
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
fun Tutorial4_8_1Screen() {
    TutorialContent()
}

@Composable
private fun TutorialContent() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {

        TutorialHeader(text = "RememberMeasurePolicy")

        StyleableTutorialText(
            text = "Remembering MeasurePolicy prevents creating new instance when " +
                    "a value is read inside MeasurePolicy or CustomLayout. " +
                    "Even if value is not used " +
                    "reading it inside MeasurePolicy triggers recomposition and  " +
                    "new instance to be created when not remembered.",
            bullets = false
        )

        // 🔥 Reading counter causes forces MeasurePolicy when Composable is recomposed with
        // new counter value
        MeasurePolicyExample1()
    }
}

@Composable
private fun MeasurePolicyExample1() {
    var text by remember {
        mutableStateOf("Type Text")
    }

    var counter by remember {
        mutableStateOf(0)
    }

    Button(
        modifier = Modifier.fillMaxWidth(),
        onClick = { counter++ }
    ) {
        Text("Counter: $counter")
    }

    Spacer(modifier = Modifier.height(10.dp))

    OutlinedTextField(
        modifier = Modifier
            .fillMaxWidth(),
        value = text,
        onValueChange = { text = it }
    )

    Spacer(modifier = Modifier.height(10.dp))

    CustomLayoutNoRemember(
        modifier = Modifier
            .width(140.dp)
            .border(2.dp, Color.Red),
        verticalSpace = counter * 10f
    ) {
        Text(text = text, fontSize = 20.sp)
    }

    Spacer(modifier = Modifier.height(10.dp))

    CustomLayoutWithRememberPolicy(
        modifier = Modifier
            .width(140.dp)
            .border(2.dp, Color.Blue),
        verticalSpace = counter * 2f
    ) {
        Text(text = text, fontSize = 20.sp)
    }

}


@Composable
private fun CustomLayoutNoRemember(
    modifier: Modifier = Modifier,
    verticalSpace: Float,
    content: @Composable () -> Unit
) {

    val measuresPolicy = object : MeasurePolicy {

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
                "🍏 CustomLayoutNoRemember() MeasureScope " +
                        "verticalSpace: $verticalSpace, " +
                        "totalWidth: $totalWidth, " +
                        "totalHeight: $totalHeight"
            )

            var y = 0
            return layout(totalWidth, totalHeight) {
                println("🍏🍏 CustomLayoutNoRemember() PlacementScope")

                placeables.forEach {
                    it.place(0, y)
                    y += it.height
                }
            }
        }
    }

    SideEffect {
        println(
            "CustomLayout() composed " +
                    "measurePolicy: ${measuresPolicy.hashCode()}"
        )
    }

    Layout(
        modifier = modifier,
        content = content,
        measurePolicy = measuresPolicy
    )
}

@Composable
private fun CustomLayoutWithRememberPolicy(
    modifier: Modifier = Modifier,
    verticalSpace: Float,
    content: @Composable () -> Unit
) {

    val measuresPolicy = remember {
        object : MeasurePolicy {

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
                    "🍏 CustomLayoutWithRememberPolicy() MeasureScope " +
                            "verticalSpace: $verticalSpace, " +
                            "totalWidth: $totalWidth, " +
                            "totalHeight: $totalHeight"
                )

                var y = 0
                return layout(totalWidth, totalHeight) {
                    println("🍏🍏 CustomLayoutWithRememberPolicy() PlacementScope")
                    placeables.forEach {
                        it.place(0, y)
                        y += it.height
                    }
                }
            }
        }
    }

    SideEffect {
        println(
            "CustomLayoutWithRememberPolicy() composed " +
                    "measurePolicy: ${measuresPolicy.hashCode()}"
        )

    }
    Layout(
        modifier = modifier,
        content = content,
        measurePolicy = measuresPolicy
    )

}

