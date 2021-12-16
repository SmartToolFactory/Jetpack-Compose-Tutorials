package com.smarttoolfactory.tutorial1_1basics.chapter3_layout

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.Measurable
import androidx.compose.ui.layout.Placeable
import androidx.compose.ui.layout.SubcomposeLayout
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import com.smarttoolfactory.tutorial1_1basics.chapter4_state.blue400
import com.smarttoolfactory.tutorial1_1basics.chapter4_state.green400
import com.smarttoolfactory.tutorial1_1basics.chapter4_state.orange400
import com.smarttoolfactory.tutorial1_1basics.chapter4_state.pink400
import com.smarttoolfactory.tutorial1_1basics.ui.components.StyleableTutorialText

@Composable
fun Tutorial3_5Screen() {
    TutorialContent()
}

@Composable
private fun TutorialContent() {

    val density = LocalDensity.current.density

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {

        StyleableTutorialText(
            text = "1-) **SubcomposeLayout** subcompose the actual content during the measuring " +
                    "stage for example to use the values calculated during the measurement " +
                    "as params for the composition of the children.\n" +
                    "In this sample below we get main size to add his height as padding to second one."
        )
        SubComponent(
            modifier = Modifier
                .padding(8.dp)
                .background(Color.LightGray)
                .padding(4.dp),
            mainContent = {

                println("🤘 SubComponent-> MainContent {}")
                Text(
                    "MainContent",
                    modifier = Modifier
                        .background(Color(0xffF44336))
                        .height(60.dp),
                    color = Color.White
                )
            },
            dependentContent = { size: IntSize ->

                println("🤘 SubComponent-> DependentContent {} Dependent size: $size")

                val paddingTop = with(density) {
                    size.height / this
                }.dp

                Column(modifier = Modifier.padding(top = paddingTop)) {

                    Text(
                        "Dependent Content",
                        modifier = Modifier
                            .background(Color(0xff9C27B0)),
                        color = Color.White
                    )
                }
            }
        )

        StyleableTutorialText(
            text = "2-) It's possible to **remeasure a composable** using a **SubcomposeLayout**. " +
                    "DynamicWidthLayout measures mainContent's max width first then measures " +
                    "dependent component using these. If **dependent component is longer than main" +
                    "one it remeasures main component** to match dependent component."
        )

        var mainText by remember { mutableStateOf(TextFieldValue("Main Component")) }
        var dependentText by remember { mutableStateOf(TextFieldValue("Dependent Component")) }

        var maxWidthOf by remember { mutableStateOf(0.dp) }

        OutlinedTextField(
            modifier = Modifier
                .padding(horizontal = 8.dp)
                .fillMaxWidth(),
            value = mainText,
            label = { Text("Main") },
            placeholder = { Text("Set text to change main width") },
            onValueChange = { newValue: TextFieldValue ->
                mainText = newValue
            }
        )

        OutlinedTextField(
            modifier = Modifier
                .padding(horizontal = 8.dp)
                .fillMaxWidth(),
            value = dependentText,
            label = { Text("Dependent") },
            placeholder = { Text("Set text to change dependent width") },
            onValueChange = { newValue ->
                dependentText = newValue
            }
        )

        DynamicWidthLayout(
            modifier = Modifier
                .padding(8.dp)
                .background(Color.LightGray)
                .padding(8.dp),
            mainContent = {

                println("🍏 DynamicWidthLayout-> MainContent {} composed")

                Column(
                    modifier = Modifier
                        .background(orange400)
                        .padding(4.dp)
                ) {
                    Text(
                        text = mainText.text,
                        modifier = Modifier
                            .background(blue400)
                            .height(40.dp),
                        color = Color.White
                    )
                }
            },
            dependentContent = { size: IntSize ->


                // 🔥 Measure max width of main component in dp  retrieved
                // by subCompose of dependent component from IntSize
                val maxWidth = with(density) {
                    size.width / this
                }.dp

                maxWidthOf = maxWidth

                println(
                    "🍎 DynamicWidthLayout-> DependentContent composed " +
                            "Dependent size: $size, "
                            + "maxWidth: $maxWidth"
                )

                Column(
                    modifier = Modifier
                        .background(pink400)
                        .padding(4.dp)
                ) {

                    Text(
                        text = dependentText.text,
                        modifier = Modifier
                            .background(green400),
                        color = Color.White
                    )
                }
            }
        )

        Spacer(modifier = Modifier.height(8.dp))


        Text("This is width of main component")
        Text(
            "Max width from subCompose(): $maxWidthOf dp",
            modifier = Modifier
                .width(maxWidthOf)
                .background(Color(0xff8D6E63)),
            color = Color.White
        )
    }
}

@Composable
private fun SubComponent(
    modifier: Modifier = Modifier,
    mainContent: @Composable () -> Unit,
    dependentContent: @Composable (IntSize) -> Unit
) {

    SubcomposeLayout(modifier = modifier) { constraints ->

        val mainPlaceables: List<Placeable> = subcompose(SlotsEnum.Main, mainContent).map {
            it.measure(constraints)
        }

        val maxSize =
            mainPlaceables.fold(IntSize.Zero) { currentMax: IntSize, placeable: Placeable ->
                IntSize(
                    width = maxOf(currentMax.width, placeable.width),
                    height = maxOf(currentMax.height, placeable.height)
                )
            }

        layout(maxSize.width, maxSize.height) {

            println("🔥 SubcomposeLayout-> layout() maxSize width: ${maxSize.width}, height: ${maxSize.height}")
            mainPlaceables.forEach { it.placeRelative(0, 0) }

            subcompose(SlotsEnum.Dependent) {
                dependentContent(maxSize)
            }.forEach {
                it.measure(constraints).placeRelative(0, 0)
            }

        }
    }
}

@Composable
private fun DynamicWidthLayout(
    modifier: Modifier = Modifier,
    mainContent: @Composable () -> Unit,
    dependentContent: @Composable (IntSize) -> Unit
) {

    SubcomposeLayout(modifier = modifier) { constraints ->


        var mainPlaceables: List<Placeable> = subcompose(SlotsEnum.Main, mainContent).map {
            it.measure(constraints)
        }

        var maxSize =
            mainPlaceables.fold(IntSize.Zero) { currentMax: IntSize, placeable: Placeable ->
                IntSize(
                    width = maxOf(currentMax.width, placeable.width),
                    height = maxOf(currentMax.height, placeable.height)
                )
            }

        val dependentMeasurables: List<Measurable> = subcompose(SlotsEnum.Dependent) {
            // 🔥🔥 Send maxSize of mainComponent to
            // dependent composable in case it might be used
            dependentContent(maxSize)
        }

        val dependentPlaceables: List<Placeable> = dependentMeasurables
            .map { measurable: Measurable ->
                measurable.measure(Constraints(maxSize.width, constraints.maxWidth))
            }

        // Get maximum width of dependent composable
        val maxWidth = dependentPlaceables.maxOf { it.width }


        println("🔥 DynamicWidthLayout-> maxSize width: ${maxSize.width}, height: ${maxSize.height}")

        // If width of dependent composable is longer than main one, remeasure main one
        // with dependent composable's width using it as minimumWidthConstraint
        if (maxWidth > maxSize.width) {

            println("🚀 DynamicWidthLayout REMEASURE MAIN COMPONENT")

            // !!! 🔥🤔 CANNOT use SlotsEnum.Main here why?
            mainPlaceables = subcompose(2, mainContent).map {
                it.measure(Constraints(maxWidth, constraints.maxWidth))
            }
        }

        // Our final maxSize is longest width and total height of main and dependent composables
        maxSize = IntSize(
            maxSize.width.coerceAtLeast(maxWidth),
            maxSize.height + dependentPlaceables.maxOf { it.height }
        )


        layout(maxSize.width, maxSize.height) {

            println(
                "🔥🔥 DynamicWidthLayout-> layout()-> " +
                        "maxSize width: ${maxSize.width}, height: ${maxSize.height}, " +
                        "maxWidth: $maxWidth"
            )

            // Place layouts
            mainPlaceables.forEach { it.placeRelative(0, 0) }
            dependentPlaceables.forEach {
                it.placeRelative(0, mainPlaceables.maxOf { it.height })
            }
        }
    }
}


enum class SlotsEnum { Main, Dependent }

