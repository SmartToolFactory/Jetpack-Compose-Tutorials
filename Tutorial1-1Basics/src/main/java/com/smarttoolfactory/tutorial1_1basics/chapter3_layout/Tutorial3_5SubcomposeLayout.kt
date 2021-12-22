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
import androidx.compose.ui.layout.Placeable
import androidx.compose.ui.layout.SubcomposeLayout
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import com.smarttoolfactory.tutorial1_1basics.chapter3_layout.chat.widget.SubcomposeColumn
import com.smarttoolfactory.tutorial1_1basics.ui.Blue400
import com.smarttoolfactory.tutorial1_1basics.ui.Green400
import com.smarttoolfactory.tutorial1_1basics.ui.Orange400
import com.smarttoolfactory.tutorial1_1basics.ui.Pink400
import com.smarttoolfactory.tutorial1_1basics.ui.components.StyleableTutorialText
import com.smarttoolfactory.tutorial1_1basics.ui.components.TutorialText2

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
            text = "1-) **SubcomposeLayout** subcompose the actual content during the measuring " +
                    "stage for example to use the values calculated during the measurement " +
                    "as params for the composition of the children.\n" +
                    "In this sample below we get main size to add his height as padding to second one."
        )
        SubComposeLayoutSample1()

        StyleableTutorialText(
            text = "2-) It's possible to **remeasure a composable** using a **SubcomposeLayout**. " +
                    "DynamicWidthLayout measures mainContent's max width first then measures " +
                    "dependent component using these. If **dependent component is longer than main" +
                    "one it remeasures main component** to match dependent component."
        )

        TutorialText2(text = "SubComposeColumn with 2 children and result")
        SubcomposeLayoutSample2()
        TutorialText2(text = "SubComposeColumn with multiple children")
        SubcomposeLayoutSample3()


    }
}

@Composable
private fun SubComposeLayoutSample1() {

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
private fun SubcomposeLayoutSample2() {
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
private fun SubcomposeLayoutSample3() {
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

enum class SlotsEnum { Main, Dependent }

