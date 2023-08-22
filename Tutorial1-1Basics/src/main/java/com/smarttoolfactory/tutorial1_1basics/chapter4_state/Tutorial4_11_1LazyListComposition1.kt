package com.smarttoolfactory.tutorial1_1basics.chapter4_state

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import com.smarttoolfactory.tutorial1_1basics.ui.backgroundColor
import com.smarttoolfactory.tutorial1_1basics.ui.components.StyleableTutorialText
import com.smarttoolfactory.tutorial1_1basics.ui.components.TutorialHeader
import kotlin.random.Random

@Preview
@Composable
fun Tutorial4_11Screen1() {
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

        TutorialHeader(text = "LazyList Recomposition1")

        StyleableTutorialText(
            text = "In this example when any **ListItem** is updated " +
                    "only that one is recomposed. Scrolling, or recomposing " +
                    " **MainScreen** via clicking Button triggers recomposition for " +
                    "**ListScreen** and every **ListItem**\n" +
                    "In next example we will fix recomposition for ListItems on " +
                    "**MainScreen** recomposition",
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
            onItemClick = viewModel::toggleSelection
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

@Composable
fun ListItem(item: Person, onItemClick: (Int) -> Unit) {

    SideEffect {
        println("Recomposing ${item.id}, selected: ${item.isSelected}")
    }

    Column(
        modifier = Modifier.border(3.dp, getRandomColor())
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clickable {
                    onItemClick(item.id)
                }
                .padding(8.dp)
        ) {
            Text("Index: Name ${item.name}", fontSize = 20.sp)
            if (item.isSelected) {
                Icon(
                    modifier = Modifier
                        .align(Alignment.CenterEnd)
                        .background(Color.Red, CircleShape),
                    imageVector = Icons.Default.Check,
                    contentDescription = "Selected",
                    tint = Color.Green,
                )
            }
        }
    }
}

data class Person(val id: Int, val name: String, val isSelected: Boolean = false)

class MyViewModel : ViewModel() {

    private val initialList = List(30) { index: Int ->
        Person(id = index, name = "Name$index")
    }

    val people = mutableStateListOf<Person>().apply {
        addAll(initialList)
    }

    fun toggleSelection(index: Int) {
        val item = people[index]
        val isSelected = item.isSelected
        people[index] = item.copy(isSelected = !isSelected)
    }
}

fun getRandomColor() = Color(
    Random.nextInt(256),
    Random.nextInt(256),
    Random.nextInt(256),
    alpha = 255
)