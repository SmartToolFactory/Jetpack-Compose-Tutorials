package com.smarttoolfactory.tutorial1_1basics.chapter4_state

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Button
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
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
import com.smarttoolfactory.tutorial1_1basics.ui.components.StyleableTutorialText

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

        StyleableTutorialText(
            text = "Remembering MeasurePolicy prevents creating new instance when " +
                    "a value is read inside MeasurePolicy. Even if value is not used " +
                    "reading it inside MeasurePolicy causes new instance to be created " +
                    "when not remembered.",
            bullets = false
        )

        // 🔥 Reading counter causes forces MeasurePolicy when Composable is recomposed with
        // new counter value
        MeasurePolicyExample1()

        StyleableTutorialText(
            text = "Using Modifiers like border or shadow causes new instance of " +
                    "MeasurePolicy to be created. Remembering it prevents creation of " +
                    "new instances on recomposition when new instance is not required " +
                    "by design.",
            bullets = false
        )
        // 🔥 Changing text in this example creates new instance of MeasurePolicy because
        // we used Modifier.border in this example
        MeasurePolicyExample2()

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

    CustomComposable(
        verticalSpace = counter * 10f
    ) {
        Text(text = text)
        if (counter > 2) {
            Text(text = " New Text")
        }
    }

    Spacer(modifier = Modifier.height(10.dp))

    CustomComposableWithRemember(
        verticalSpace = counter * 2f
    ) {
        Text(text = text)
        if (counter > 3) {
            Text(text = " New Text")
        }
    }

}

@Composable
private fun MeasurePolicyExample2() {
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

    CustomComposable(
        modifier = Modifier.border(1.dp, Color.Red),
        verticalSpace = counter * 10f
    ) {
        Text(text = text)
        if (counter > 2) {
            Text(text = " New Text")
        }
    }

    Spacer(modifier = Modifier.height(10.dp))

    CustomComposableWithRemember(
        modifier = Modifier.border(1.dp, Color.Blue),
        verticalSpace = counter * 2f
    ) {
        Text(text = text)
        if (counter > 3) {
            Text(text = " New Text")
        }
    }
}

@Composable
private fun CustomComposable(
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
                "🚙 CustomComposable() verticalSpace: $verticalSpace, " +
                        "totalWidth: $totalWidth, totalHeight: $totalHeight"
            )

            var y = 0
            return layout(totalWidth, totalHeight) {
                placeables.forEach {
                    it.place(0, y)
                    y += it.height
                }
            }
        }
    }

    println("🍏 CustomComposable() measurePolicy: ${measuresPolicy.hashCode()}")

    Layout(
        modifier = modifier,
        content = content,
        measurePolicy = measuresPolicy
    )
}

@Composable
private fun CustomComposableWithRemember(
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
                    "🚗 CustomComposableWithRemember() verticalSpace: $verticalSpace, " +
                            "totalWidth: $totalWidth, totalHeight: $totalHeight"
                )

                var y = 0
                return layout(totalWidth, totalHeight) {
                    placeables.forEach {
                        it.place(0, y)
                        y += it.height
                    }
                }
            }
        }
    }

    println("🍎 CustomComposableWithRemember() measurePolicy: ${measuresPolicy.hashCode()}")

    Layout(
        modifier = modifier,
        content = content,
        measurePolicy = measuresPolicy
    )

}

enum class CustomAlignment {
    Vertical, Horizontal
}
