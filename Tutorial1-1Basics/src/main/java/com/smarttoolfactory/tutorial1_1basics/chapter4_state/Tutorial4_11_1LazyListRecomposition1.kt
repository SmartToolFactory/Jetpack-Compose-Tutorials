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
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import com.smarttoolfactory.tutorial1_1basics.ui.backgroundColor
import com.smarttoolfactory.tutorial1_1basics.ui.components.StyleableTutorialText
import com.smarttoolfactory.tutorial1_1basics.ui.components.TutorialHeader
import com.smarttoolfactory.tutorial1_1basics.ui.components.getRandomColor

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
        mutableIntStateOf(0)
    }

    Column(
        modifier = Modifier.padding(8.dp),

        ) {
        val people = viewModel.people

        Text(text = "Counter $counter")

        Button(onClick = { counter++ }) {
            Text(text = "Increase Counter")
        }

        Spacer(modifier = Modifier.height(10.dp))

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
        Spacer(modifier = Modifier.height(10.dp))
        LazyColumn(
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
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
        modifier = Modifier
            .shadow(2.dp, RoundedCornerShape(8.dp))
            .border(2.dp, getRandomColor(), RoundedCornerShape(8.dp))
            .background(Color.White)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clickable {
                    onItemClick(item.id)
                }
                .padding(8.dp)
        ) {
            Text("Index: ${item.id}, ${item.name}", fontSize = 20.sp)
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
        Person(id = index, name = "Person: $index")
    }

    val people = mutableStateListOf<Person>().apply {
        addAll(initialList)
    }

    fun toggleSelection(index: Int) {
        val item = people[index]
        val isSelected = item.isSelected
        people[index] = item.copy(isSelected = !isSelected)
    }

    // 🔥 If you use list and call updateItemSelection whole list is recomposed
    // when you chance one item selection status
    var personList by mutableStateOf(initialList)

    // 🔥 setting new value to MutableState triggers recomposition for whole LazyColumn
    fun updateItemSelection(id: Int) {
        val newList = personList.map {
            if (it.id == id) {
                it.copy(isSelected = !it.isSelected)
            } else {
                it
            }
        }
        personList = newList
    }

}
