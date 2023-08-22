package com.smarttoolfactory.tutorial1_1basics.chapter4_state

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.smarttoolfactory.tutorial1_1basics.ui.backgroundColor
import com.smarttoolfactory.tutorial1_1basics.ui.components.StyleableTutorialText
import com.smarttoolfactory.tutorial1_1basics.ui.components.TutorialHeader

@Preview
@Composable
fun Tutorial4_11Screen2() {
    TutorialContent()
}

@Composable
private fun TutorialContent() {
    Column(
        modifier = Modifier
            .background(backgroundColor)
            .fillMaxSize()
            .padding(10.dp)
    ) {
        TutorialHeader(text = "LazyList Recomposition2")
        Modifier.clipToBounds()
        StyleableTutorialText(
            text = "In this example when **MainScreen** is composed **ListItems are not " +
                    "recomposed because we changed ViewModel callback. But since **ListScreen** " +
                    "is still unstable it gets recomposed while its items don't.\n" +
                    "In next example we will stabilize **ListScreen**.",
            bullets = false
        )

        val viewModel = MyViewModel()
        MainScreen(viewModel = viewModel)
    }
}

@Composable
private fun MainScreen(
    viewModel: MyViewModel
) {

    var counter by remember {
        mutableStateOf(0)
    }

    // 🔥 In this example we made unstable ViewModel lambda stable
    // When rest of the MainScreen is composed it prevents each ListItem to be recomposed
    val onClick = remember {
        { index: Int ->
            viewModel.toggleSelection(index)
        }
    }

    Column(
        modifier = Modifier.padding(8.dp),

        ) {
        val people = viewModel.people

        Text(text = "Counter $counter")

        Button(onClick = { counter++ }) {
            Text(text = "Increase Counter")
        }

        Spacer(modifier = Modifier.height(40.dp))

        ListScreen(
            people = people,
            onItemClick = onClick
        )
    }
}

@Composable
private fun ListScreen(
    people: List<Person>,
    onItemClick: (Int) -> Unit
) {

    SideEffect {
        println("ListScreen is recomposing...$people")
    }

    Column {
        Text(
            text = "Header",
            modifier = Modifier.border(2.dp, getRandomColor()),
            fontSize = 30.sp
        )
        Spacer(modifier = Modifier.height(20.dp))
        LazyColumn(
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(2.dp),
            modifier = Modifier
                .fillMaxSize()
                .border(3.dp, getRandomColor(), RoundedCornerShape(8.dp))
        ) {
            items(
                items = people,
                key = { it.hashCode() }
            ) {
                ListItem(item = it, onItemClick = onItemClick)
            }
        }
    }
}
