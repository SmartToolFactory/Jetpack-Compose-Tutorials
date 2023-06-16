package com.smarttoolfactory.tutorial1_1basics.chapter4_state

import androidx.compose.foundation.background
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
import androidx.compose.ui.draw.shadow
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
fun Tutorial4_8_2Screen() {
    TutorialContent()
}

@Preview
@Composable
private fun TutorialContent() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {

        StyleableTutorialText(
            text = "Remembering measure policy prevents re-creating it every time " +
                    "counter object send as parameter changes. Any time parameter " +
                    "passed to MeasurePolicy is updated new measure policy object " +
                    "is created. In the example below layout alignment is only " +
                    "changed once but every time button is clicked first Composable " +
                    "creates new MeasurePolicy object with modifiers like shadow",
            bullets = false
        )

        // 🔥🔥 If you remove Modifier.shadow new instance will
        // be created only when custom alignment changes. With shadow content is recomposed
        // on text changes too.

        var text by remember {
            mutableStateOf("Type Text")
        }

        var counter by remember {
            mutableStateOf(0)
        }


        val customAlignment = if (counter < 5)
            CustomAlignment.Vertical else CustomAlignment.Horizontal

        Button(
            modifier = Modifier.fillMaxWidth(),
            onClick = { counter++ }
        ) {
            Text("Counter: $counter")
        }

        OutlinedTextField(
            modifier = Modifier.fillMaxWidth(),
            value = text,
            onValueChange = { text = it }
        )

        Spacer(modifier = Modifier.height(10.dp))

        CustomComposable(
            Modifier
                    // Using shadow Modifier recomposes content on text changes too
                .shadow(3.dp)
                .background(Color.White)
                .padding(2.dp),
            customAlignment = customAlignment
        ) {
            Text(text = text)
            if (counter > 3) {
                Text(text = "New Text")
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        CustomComposableWithRemember(
            Modifier
                // Using shadow Modifier recomposes content on text changes too
                .shadow(3.dp)
                .background(Color.White)
                .padding(2.dp),
            customAlignment = customAlignment
        ) {
            Text(text = text)
            if (counter > 3) {
                Text(text = "New Text")
            }
        }
    }
}


@Composable
private fun CustomComposable(
    modifier: Modifier = Modifier,
    customAlignment: CustomAlignment,
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

            val totalWidth = if (customAlignment == CustomAlignment.Vertical) {
                placeables.maxOf { it.width }
            } else {
                placeables.sumOf { it.width }
            }
            val totalHeight = if (customAlignment == CustomAlignment.Vertical) {
                placeables.sumOf { it.height }
            } else {
                placeables.maxOf { it.height }
            }

            var x = 0
            var y = 0
            return layout(totalWidth, totalHeight) {
                placeables.forEach {
                    it.place(x, y)
                    if (customAlignment == CustomAlignment.Vertical) {
                        y += it.height
                    } else {
                        x += it.width
                    }
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
    customAlignment: CustomAlignment,
    content: @Composable () -> Unit
) {

    val measuresPolicy = remember(customAlignment) {
        object : MeasurePolicy {

            override fun MeasureScope.measure(
                measurables: List<Measurable>,
                constraints: Constraints
            ): MeasureResult {

                val placeables = measurables.map { measurable: Measurable ->
                    measurable.measure(constraints)
                }

                val totalWidth = if (customAlignment == CustomAlignment.Vertical) {
                    placeables.maxOf { it.width }
                } else {
                    placeables.sumOf { it.width }
                }
                val totalHeight = if (customAlignment == CustomAlignment.Vertical) {
                    placeables.sumOf { it.height }
                } else {
                    placeables.maxOf { it.height }
                }

                var x = 0
                var y = 0
                return layout(totalWidth, totalHeight) {
                    placeables.forEach {
                        it.place(x, y)
                        if (customAlignment == CustomAlignment.Vertical) {
                            y += it.height
                        } else {
                            x += it.width
                        }
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
