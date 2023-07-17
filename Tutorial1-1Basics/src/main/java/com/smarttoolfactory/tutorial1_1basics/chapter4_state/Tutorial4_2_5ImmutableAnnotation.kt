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
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.smarttoolfactory.tutorial1_1basics.ui.components.StyleableTutorialText
import com.smarttoolfactory.tutorial1_1basics.ui.components.getRandomColor

@Preview
@Composable
fun Tutorial4_2_5Screen() {
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
            text = "In this example **StableImmutableComposable** wraps **unstable List** " +
                    "with **@Immutable** when called during recomposition, " +
                    "compose is able to skip the function if all of the " +
                    "parameters are equal with their previous values.",
            bullets = false
        )
        ImmutableAnnotationSample()

    }
}


// This class is Unstable
data class ListWrapper(val list: List<Int>)

// 🔥This class is Stable
@Immutable
data class ImmutableListWrapper(val list: List<Int>)

@Preview
@Composable
private fun ImmutableAnnotationSample() {

    var counter by remember {
        mutableStateOf(0)
    }

    val listWrapper by remember {
        mutableStateOf(ListWrapper(listOf(1, 2, 3)))
    }

    val immutableListWrapper by remember {
        mutableStateOf(ImmutableListWrapper(listOf(1, 2, 3)))
    }

    Column(
        modifier = Modifier
            .padding(4.dp)
            .shadow(1.dp, shape = CutCornerShape(topEnd = 8.dp))
            .background(getRandomColor())
            .fillMaxWidth()
            .padding(4.dp)
    ) {
        Button(
            modifier = Modifier.fillMaxWidth(),
            onClick = { counter++ }) {
            Text(text = "Counter: $counter")
        }

        // This scope is recomposed because counter is read here
        // Stable types do not get recomposed when there input don't change
        Counter(text = "Counter $counter")
        UnstableImmutableComposable(listWrapper)
        StableImmutableComposable(immutableListWrapper = immutableListWrapper)
    }
}

@Composable
private fun UnstableImmutableComposable(
    listWrapper: ListWrapper
) {
    SideEffect {
        println("🍏 UnstableImmutableComposable()")
    }
    Column(
        modifier = Modifier
            .padding(4.dp)
            .shadow(1.dp, shape = CutCornerShape(topEnd = 8.dp))
            .background(getRandomColor())
            .fillMaxWidth()
            .padding(4.dp)
    ) {
        Text(text = "UnstableImmutableComposable() list: $listWrapper")
    }
}

@Composable
private fun StableImmutableComposable(
    immutableListWrapper: ImmutableListWrapper
) {
    SideEffect {
        println("🍎 StableImmutableComposable()")
    }
    Column(
        modifier = Modifier
            .padding(4.dp)
            .shadow(1.dp, shape = CutCornerShape(topEnd = 8.dp))
            .background(getRandomColor())
            .fillMaxWidth()
            .padding(4.dp)
    ) {
        Text(text = "StableImmutableComposable() list: $immutableListWrapper")
    }
}

@Composable
private fun Counter(
    text: String
) {
    SideEffect {
        println("Counter()")
    }
    Column(
        modifier = Modifier
            .padding(4.dp)
            .shadow(1.dp, shape = CutCornerShape(topEnd = 8.dp))
            .background(getRandomColor())
            .fillMaxWidth()
            .padding(4.dp)
    ) {
        Text(text = "Counter() text: $text")
    }
}

