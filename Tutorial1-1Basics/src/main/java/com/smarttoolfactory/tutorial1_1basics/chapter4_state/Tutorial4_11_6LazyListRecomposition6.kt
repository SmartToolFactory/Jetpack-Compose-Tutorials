package com.smarttoolfactory.tutorial1_1basics.chapter4_state

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.Icon
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
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
import com.smarttoolfactory.tutorial1_1basics.ui.components.TutorialHeader
import com.smarttoolfactory.tutorial1_1basics.ui.components.getRandomColor
import java.util.UUID

@Preview
@Composable
fun Tutorial4_11Screen6() {
    // 🔥 Adding item to the top recomposes every item as removing first item
    // 🔥 Using key keeps scroll position when items added or removed before visible item
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
        TutorialHeader(text = "LazyList Recomposition6")

        val viewModel = AddRemoveSwapViewModel()
        MainScreen(viewModel = viewModel)
    }
}

@Composable
private fun MainScreen(
    viewModel: AddRemoveSwapViewModel,
) {

    val onClick = remember {
        { index: Int ->
            viewModel.toggleSelection(index)
        }
    }

    val onLongClick = remember {
        { task: EditableTask ->
            viewModel.deleteTask(task)
        }
    }

    val swap = remember {
        { firstIndex: Int, secondIndex: Int ->
            viewModel.swap(firstIndex, secondIndex)
        }
    }

    var firstIndex by remember {
        mutableIntStateOf(0)
    }

    var secondIndex by remember {
        mutableIntStateOf(4)
    }

    Column(
        modifier = Modifier.padding(8.dp),

        ) {
        val tasks = viewModel.taskList

        Button(
            modifier = Modifier.fillMaxWidth(),
            onClick = {
                val id = UUID.randomUUID().toString().take(12)
                viewModel.addTaskToFirstIndex(
                    EditableTask(
                        id = id,
                        title = "Task $id",
                        editTime = System.currentTimeMillis()
                    )
                )
            }
        ) {
            Text("Add Task to Top")
        }

        Button(
            modifier = Modifier.fillMaxWidth(),
            onClick = {
                tasks.lastOrNull()?.apply {
                    viewModel.deleteFirstTask()
                }
            }
        ) {
            Text("Delete Task from Top")
        }

        Spacer(modifier = Modifier.height(10.dp))

        Row {
            OutlinedTextField(
                modifier = Modifier.weight(1f),
                label = {
                    Text("First Index")
                },
                value = "$firstIndex",
                onValueChange = {
                    it.toIntOrNull()?.let {
                        firstIndex = it
                    }
                }
            )

            Spacer(modifier = Modifier.width(8.dp))

            OutlinedTextField(
                modifier = Modifier.weight(1f),
                label = {
                    Text("Second Index")
                },
                value = "$secondIndex",
                onValueChange = {
                    it.toIntOrNull()?.let {
                        secondIndex = it
                    }
                }
            )
        }

        Button(
            modifier = Modifier.fillMaxWidth(),
            onClick = {
                swap(firstIndex, secondIndex)
            }
        ) {
            Text("Swap $firstIndex with $secondIndex")
        }

        ListScreen(
            tasks = tasks,
            onItemClick = onClick,
            onItemLongClick = onLongClick

        )
    }
}

@Composable
private fun ListScreen(
    tasks: List<EditableTask>,
    onItemClick: (Int) -> Unit,
    onItemLongClick: (EditableTask) -> Unit,
) {

    SideEffect {
        println("ListScreen is recomposing...$tasks")
    }

    Column {
        LazyColumn(
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier
                .fillMaxSize()
                .border(3.dp, getRandomColor(), RoundedCornerShape(8.dp))
        ) {
            itemsIndexed(
                items = tasks,
                // 🔥 Using key keeps scroll position when items added or
                // removed before visible item
                key = { _: Int, task: EditableTask ->
                    task.id
                }
            ) { index, task ->
                TaskListItem(
                    item = task,
                    onItemClick = {
                        onItemClick(index)
                    },
                    onItemLongClick = onItemLongClick
                )
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun TaskListItem(
    modifier: Modifier = Modifier,
    item: EditableTask,
    onItemClick: () -> Unit,
    onItemLongClick: (EditableTask) -> Unit,
) {

    SideEffect {
        println("Recomposing ${item.title}, selected: ${item.isSelected}")
    }

    Column(
        modifier = modifier
            .shadow(2.dp, RoundedCornerShape(8.dp))
            .border(2.dp, getRandomColor(), RoundedCornerShape(8.dp))
            .background(Color.White)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .combinedClickable(
                    onClick = {
                        onItemClick()
                    },
                    onLongClick = {
                        onItemLongClick(item)
                    }
                )
                .padding(8.dp)
        ) {
            Text("${item.title}, id: ${item.id.substring(startIndex = item.id.length - 6)}", fontSize = 20.sp)

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

private class AddRemoveSwapViewModel : ViewModel() {

    private val initialList = List(8) { index: Int ->
        val id = UUID.randomUUID().toString().apply {
            substring(startIndex = lastIndex - 6)
        }
        EditableTask(id = id, title = "Task: $index", editTime = System.currentTimeMillis())
    }

    val taskList = mutableStateListOf<EditableTask>().apply {
        addAll(initialList)
    }

    fun toggleSelection(index: Int) {
        val item = taskList[index]
        val isSelected = item.isSelected
        taskList[index] = item.copy(isSelected = !isSelected, editTime = System.currentTimeMillis())
        taskList.sortByDescending { it.editTime }
    }

    fun swap(firsTaskIndex: Int, secondTaskIndex: Int) {
        val firstTask = taskList[firsTaskIndex]
        val secondTask = taskList[secondTaskIndex]
        taskList[firsTaskIndex] = secondTask
        taskList[secondTaskIndex] = firstTask
    }

    fun deleteTask(task: EditableTask) {
        taskList.remove(task)
    }

    fun deleteFirstTask() {
        taskList.removeAt(0)
    }

    fun addTaskToFirstIndex(task: EditableTask) {
        taskList.add(0, task)
    }
}

internal data class EditableTask(
    val id: String,
    val title: String,
    val isSelected: Boolean = false,
    val editTime: Long,
)
