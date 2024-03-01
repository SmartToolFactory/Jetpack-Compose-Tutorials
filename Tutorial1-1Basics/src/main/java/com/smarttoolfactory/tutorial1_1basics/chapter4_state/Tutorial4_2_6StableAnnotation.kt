package com.smarttoolfactory.tutorial1_1basics.chapter4_state

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CutCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.smarttoolfactory.tutorial1_1basics.ui.components.StyleableTutorialText
import com.smarttoolfactory.tutorial1_1basics.ui.components.getRandomColor
import kotlin.random.Random

@Preview
@Composable
fun Tutorial4_2_6Screen() {
    TutorialContent()
}

@Composable
private fun TutorialContent() {

    Column(
        Modifier
            .verticalScroll(rememberScrollState())
            .fillMaxSize()
            .padding(8.dp)
    ) {

        StyleableTutorialText(
            text = "In this example **@Stable** annotation makes **StableUiState** " +
                    "which contains an unstable type **List**. If the list was not present" +
                    " **StableUiState** wouldn't require this annotation. In inner scope " +
                    "reading **count** or **text** triggers recomposition for " +
                    "unstable Composables even if their inputs do not change.",
            bullets = false
        )
        StableAnnotationSample()
    }
}


class UnstableUiState(
    // 🔥 List is unstable, passing it as a param
    // makes this type Unstable
    var list: List<Int> = listOf(1, 2, 3)
) {
    var count by mutableIntStateOf(0)
    var text by mutableStateOf("UnstableUiState")
}

@Stable
class StableUiState(
    // 🔥 List is unstable but adding @Stable annotation
    // makes this type Stable
    var list: List<Int> = listOf(1, 2, 3)
) {
    var count by mutableIntStateOf(0)
    var text by mutableStateOf("StableUiState")
}

@Preview
@Composable
private fun StableAnnotationSample() {
    var counter by remember {
        mutableIntStateOf(0)
    }

    var unStableUiState by remember {
        mutableStateOf(UnstableUiState())
    }


    var stableUiState by remember {
        mutableStateOf(StableUiState())
    }

    // Since Column does not create a scope every time
    // counter is read whole composables that are not
    // skippable are composed
    Column(
        modifier = Modifier
            .padding(4.dp)
            .shadow(1.dp, shape = CutCornerShape(topEnd = 8.dp))
            .background(getRandomColor())
            .fillMaxWidth()
            .padding(4.dp)
    ) {

        SideEffect {
            println("Column recomposing...")
        }

        Button(
            modifier = Modifier.fillMaxWidth(),
            onClick = { counter++ }) {
            Text(text = "Counter: $counter")
        }



        Button(
            modifier = Modifier.fillMaxWidth(),
            onClick = {
                unStableUiState = UnstableUiState(
                    List(3) {
                        Random.nextInt(100)
                    }
                )

                stableUiState = StableUiState(
                    List(3) {
                        Random.nextInt(100)
                    }
                )

            }
        ) {
            Text(text = "Create new State")
        }

        Button(
            modifier = Modifier.fillMaxWidth(),
            onClick = {
                // Updating List does not trigger recomposition
                // because List is not a MutableState value
                unStableUiState.list = List(3) {
                    Random.nextInt(100)
                }
                stableUiState.list = List(3) {
                    Random.nextInt(100)
                }
            }
        ) {
            Text(text = "Create new List for State")
        }

        Button(
            modifier = Modifier.fillMaxWidth(),
            onClick = {
                unStableUiState.count++
                stableUiState.count++
            }
        ) {
            Text(text = "Update State count param")
        }

        Button(
            modifier = Modifier.fillMaxWidth(),
            onClick = {
                unStableUiState.text = "unStableUiState ${Random.nextInt(100)}"
                stableUiState.text = "stableUiState ${Random.nextInt(100)}"
            }
        ) {
            Text(text = "Update State text param")
        }

        // This scope gets recomposed because counter is read here
        Text(text = "Counter: $counter")
        Text(text = "UnstableComposable")
        UnstableComposable(unstableUiState = unStableUiState)
        Text(text = "StableComposable")
        StableComposable(stableUiState = stableUiState)
    }
}

@Composable
private fun UnstableComposable(unstableUiState: UnstableUiState) {
    Column(
        modifier = Modifier
            .padding(4.dp)
            .shadow(1.dp, shape = CutCornerShape(topEnd = 8.dp))
            .background(getRandomColor())
            .fillMaxWidth()
            .padding(4.dp)
    ) {
        // When count, list or text is read
        // Composables that are not skippable are recomposed
        SideEffect {
            println("🍏 UnstableComposable scope...")
        }
        ListComposable(unstableUiState.list)
        ValueComposable(count = unstableUiState.count)
        TextComposable(text = unstableUiState.text)
    }
}

@Composable
private fun StableComposable(stableUiState: StableUiState) {
    Column(
        modifier = Modifier
            .padding(4.dp)
            .shadow(1.dp, shape = CutCornerShape(topEnd = 8.dp))
            .background(getRandomColor())
            .fillMaxWidth()
            .padding(4.dp)
    ) {

        // When count, list or text is read
        // Composables that are not skippable are recomposed
        SideEffect {
            println("🍎 StableComposable scope...")
        }
        ListComposable(stableUiState.list)
        ValueComposable(count = stableUiState.count)
        TextComposable(text = stableUiState.text)
    }
}

@Composable
private fun ValueComposable(count: Int) {
    Column(
        modifier = Modifier
            .padding(4.dp)
            .shadow(1.dp, shape = CutCornerShape(topEnd = 8.dp))
            .background(getRandomColor())
            .fillMaxWidth()
            .padding(4.dp)
    ) {
        Text(text = "ValueComposable() count: $count")
    }
}

@Composable
private fun ListComposable(
    list: List<Int>
) {
    Column(
        modifier = Modifier
            .padding(4.dp)
            .shadow(1.dp, shape = CutCornerShape(topEnd = 8.dp))
            .background(getRandomColor())
            .fillMaxWidth()
            .padding(4.dp)
    ) {
        list.forEach {
            Text(text = "ListComposable() value: $it")
        }
    }
}

@Composable
private fun TextComposable(text: String) {
    SideEffect {
        println("TextComposable()")
    }
    Column(
        modifier = Modifier
            .padding(4.dp)
            .shadow(1.dp, shape = CutCornerShape(topEnd = 8.dp))
            .background(getRandomColor())
            .fillMaxWidth()
            .padding(4.dp)
    ) {
        Text(text = "TextComposable() text: $text")
    }
}
