package com.smarttoolfactory.tutorial1_1basics.chapter3_layout

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.Measurable
import androidx.compose.ui.layout.Placeable
import androidx.compose.ui.layout.SubcomposeLayout
import androidx.compose.ui.layout.SubcomposeMeasureScope
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import com.smarttoolfactory.tutorial1_1basics.chapter3_layout.chat.widget.SubcomposeColumn
import com.smarttoolfactory.tutorial1_1basics.ui.*
import com.smarttoolfactory.tutorial1_1basics.ui.components.StyleableTutorialText
import com.smarttoolfactory.tutorial1_1basics.ui.components.TutorialText2

/**
 *  In this tutorial [SubcomposeLayout] usage is demonstrated with examples.
 *
 *  SubcomposeLayout can compose a layout in parts as name suggest by sub-composing it or
 *  can be used to remeasure children composables, after initial measurement which you might
 *  get longest width or height, to set every composable to required property.
 *  When remeasuring take into consideration that new measurement must be done with new
 *  [Constraints] that use that property as one of parameters.
 */
@Composable
fun Tutorial3_5Screen() {
    TutorialContent()
}

@Composable
private fun TutorialContent() {

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {

        StyleableTutorialText(
            text = "1-) **SubcomposeLayout** sub-composes the actual content during the measuring " +
                    "stage for example to use the values calculated during the measurement " +
                    "as params for the composition of the children.\n" +
                    "In this sample below we get main size to add its height as padding to second one."
        )
        SubComposeLayoutExample1()

        StyleableTutorialText(
            text = "2-) It's possible to **remeasure a composable** using a **SubcomposeLayout**. " +
                    "DynamicWidthLayout measures mainContent's max width first then measures " +
                    "dependent component using these. If **dependent component is longer than main" +
                    "one it remeasures main component** to match dependent component."
        )

        TutorialText2(text = "SubComposeColumn with 2 children and result")
        SubcomposeLayoutExample2()
        TutorialText2(text = "SubComposeColumn with multiple children")
        SubcomposeLayoutExample3()

        StyleableTutorialText(
            text = "3-) Instead of max width get max height and remeasure each Composable " +
                    "using this max height while having a horizontal scroll like news columns."
        )
        TutorialText2(text = "SubComposeRow like news column with same height")
        SubComposeRowExample()
    }
}

@Composable
private fun SubComposeLayoutExample1() {

    val density = LocalDensity.current.density

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
}

@Composable
private fun SubcomposeLayoutExample2() {
    var mainText by remember { mutableStateOf(TextFieldValue("Main Component")) }
    var dependentText by remember { mutableStateOf(TextFieldValue("Dependent Component")) }

    var maxWidthOf by remember { mutableStateOf(0.dp) }

    SubcomposeColumn(
        modifier = Modifier
            .padding(8.dp)
            .background(Color.LightGray)
            .padding(8.dp),
        mainContent = {

            println("🍏 SubcomposeColumn-> MainContent {} composed")

            Column(
                modifier = Modifier
                    .background(Orange400)
                    .padding(4.dp)
            ) {
                Text(
                    text = mainText.text,
                    modifier = Modifier
                        .background(Blue400)
                        .height(40.dp),
                    color = Color.White
                )
            }
        },
        dependentContent = { size: IntSize ->

            val density = LocalDensity.current.density

            // 🔥 Measure max width of main component in dp  retrieved
            // by subcompose of dependent component from IntSize
            val maxWidth = with(density) {
                size.width / this
            }.dp

            maxWidthOf = maxWidth

            println(
                "🍎 SubcomposeColumn-> DependentContent composed " +
                        "Dependent size: $size, "
                        + "maxWidth: $maxWidth"
            )

            Column(
                modifier = Modifier
                    .background(Pink400)
                    .padding(4.dp)
            ) {

                Text(
                    text = dependentText.text,
                    modifier = Modifier
                        .background(Green400),
                    color = Color.White
                )
            }
        }
    )

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

    Spacer(modifier = Modifier.height(8.dp))

    Text(
        text = "Width of main component",
        modifier = Modifier
            .padding(12.dp)
    )
    Text(
        text = "Max width from subCompose(): $maxWidthOf",
        modifier = Modifier
            .padding(12.dp)
            .width(maxWidthOf)
            .background(Color(0xff8D6E63)),
        color = Color.White
    )
}

@Composable
private fun SubcomposeLayoutExample3() {
    var text1 by remember { mutableStateOf(TextFieldValue("Text1 context")) }
    var text2 by remember { mutableStateOf(TextFieldValue("Text2 context")) }
    var text3 by remember { mutableStateOf(TextFieldValue("Text3 context")) }
    var text4 by remember { mutableStateOf(TextFieldValue("Text4 context")) }


    SubcomposeColumn(
        modifier = Modifier
            .padding(8.dp)
            .background(Color.LightGray)
            .padding(8.dp),
        content = {

            Column(
                modifier = Modifier
                    .background(Orange400)
                    .padding(4.dp)
            ) {
                Text(
                    text = text1.text,
                    modifier = Modifier.background(Blue400),
                    color = Color.White
                )
            }

            Column(
                modifier = Modifier
                    .background(Pink400)
                    .padding(4.dp)
            ) {

                Text(
                    text = text2.text,
                    modifier = Modifier.background(Green400),
                    color = Color.White
                )
            }


            Column(
                modifier = Modifier
                    .background(Blue400)
                    .padding(4.dp)
            ) {

                Text(
                    text = text3.text,
                    modifier = Modifier.background(Pink400),
                    color = Color.White
                )
            }

            Column(
                modifier = Modifier
                    .background(Green400)
                    .padding(4.dp)
            ) {

                Text(
                    text = text4.text,
                    modifier = Modifier.background(Orange400),
                    color = Color.White
                )
            }
        }
    )


    OutlinedTextField(
        modifier = Modifier
            .padding(horizontal = 8.dp)
            .fillMaxWidth(),
        value = text1,
        label = { Text("Text1") },
        placeholder = { Text("Set text to change main width") },
        onValueChange = { newValue: TextFieldValue ->
            text1 = newValue
        }
    )

    OutlinedTextField(
        modifier = Modifier
            .padding(horizontal = 8.dp)
            .fillMaxWidth(),
        value = text2,
        label = { Text("Text2") },
        placeholder = { Text("Set text to change main width") },
        onValueChange = { newValue: TextFieldValue ->
            text2 = newValue
        }
    )

    OutlinedTextField(
        modifier = Modifier
            .padding(horizontal = 8.dp)
            .fillMaxWidth(),
        value = text3,
        label = { Text("Text3") },
        placeholder = { Text("Set text to change main width") },
        onValueChange = { newValue: TextFieldValue ->
            text3 = newValue
        }
    )

    OutlinedTextField(
        modifier = Modifier
            .padding(horizontal = 8.dp)
            .fillMaxWidth(),
        value = text4,
        label = { Text("Text4") },
        placeholder = { Text("Set text to change main width") },
        onValueChange = { newValue: TextFieldValue ->
            text4 = newValue
        }
    )
}

@Composable
private fun SubComposeRowExample() {
    SubcomposeRow(
        modifier = Modifier.horizontalScroll(state = rememberScrollState())
    ) {
        Item(
            text = "Lorem ipsum dolor sit amet, consetetur sadipscing elitr, sed diam nonumy " +
                    "eirmod tempor invidunt ut labore et dolore magna aliquyam"
        )

        Spacer(modifier = Modifier.width(20.dp))

        Item(
            text = "Lorem ipsum dolor sit amet, consetetur sadipscing elitr, sed diam nonumy " +
                    "eirmod tempor invidunt ut labore et dolore magna aliquyam erat, sed diam " +
                    "voluptua. At"
        )

        Spacer(modifier = Modifier.width(20.dp))

        Item(
            text = "Lorem ipsum dolor sit amet, consetetur sadipscing elitr, sed diam"
        )
    }
}

/**
 * Layout that uses [SubcomposeLayout] to pass dimension of [mainContent] to [dependentContent]
 * using [SubcomposeMeasureScope.subcompose] function.
 *
 * SubcomposeLayout can compose a layout in parts as name suggest by sub-composing it or
 *  can be used to remeasure children composables, after initial measurement which you might
 *  get longest width or height, to set every composable to required property.
 *  When remeasuring take into consideration that new measurement must be done with new
 *  [Constraints] that use that property as one of parameters.
 */
@Composable
private fun SubComponent(
    modifier: Modifier = Modifier,
    mainContent: @Composable () -> Unit,
    dependentContent: @Composable (IntSize) -> Unit
) {

    SubcomposeLayout(modifier = modifier) { constraints ->

        // Subcompose(compose only a section) main content and get Placeable
        val mainPlaceables: List<Placeable> = subcompose(SlotsEnum.Main, mainContent).map {
            it.measure(constraints)
        }

        // Get max width and height of main component
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

            // Get List<Measurable> from subcompose function then get List<Placeable> and place them
            subcompose(SlotsEnum.Dependent) {
                dependentContent(maxSize)
            }.forEach {
                it.measure(constraints).placeRelative(0, 0)
            }
        }
    }
}

enum class SlotsEnum { Main, Dependent }

@Composable
private fun Item(
    modifier: Modifier = Modifier,
    text: String,
) {
    Column(
        modifier = modifier
            .width(200.dp),
        horizontalAlignment = Alignment.End,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Column {
            Text("Some static text")

            // Dynamic text
            Text(
                text,
                modifier = Modifier.padding(top = 5.dp)
            )
        }

        // The space between these two composables should be flexible,
        // hence the outer column with Arrangement.SpaceBetween

        Button(
            modifier = Modifier.padding(top = 20.dp),
            colors = ButtonDefaults.buttonColors(
                backgroundColor = Red400,
                contentColor = Color.White
            ),
            shape = RoundedCornerShape(8.dp),
            onClick = {}
        ) {
            Text("Button")
        }
    }
}

@Composable
private fun SubcomposeRow(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit = {},
) {

    SubcomposeLayout(modifier = modifier) { constraints ->

        var subcomposeIndex = 0

        var placeables: List<Placeable> = subcompose(subcomposeIndex++, content).map {
            it.measure(constraints)
        }

        var rowSize =
            placeables.fold(IntSize.Zero) { currentMax: IntSize, placeable: Placeable ->
                IntSize(
                    width = currentMax.width + placeable.width,
                    height = maxOf(currentMax.height, placeable.height)
                )
            }

        // Remeasure every element using height of tallest item using it as min height for
        // every composable
        if (!placeables.isNullOrEmpty() && placeables.size > 1) {
            placeables = subcompose(subcomposeIndex, content).map { measurable: Measurable ->
                measurable.measure(
                    Constraints(
                        minHeight = rowSize.height,
                        maxHeight = constraints.maxHeight
                    )
                )
            }

            rowSize =
                placeables.fold(IntSize.Zero) { currentMax: IntSize, placeable: Placeable ->
                    IntSize(
                        width = currentMax.width + placeable.width,
                        height = maxOf(currentMax.height, placeable.height)
                    )
                }
        }

        layout(rowSize.width, rowSize.height) {
            var xPos = 0
            placeables.forEach { placeable: Placeable ->
                placeable.placeRelative(xPos, 0)
                xPos += placeable.width
            }

        }
    }
}
