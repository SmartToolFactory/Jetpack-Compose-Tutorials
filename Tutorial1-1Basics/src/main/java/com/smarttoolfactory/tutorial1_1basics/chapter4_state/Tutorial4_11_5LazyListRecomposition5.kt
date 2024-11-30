package com.smarttoolfactory.tutorial1_1basics.chapter4_state

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.combinedClickable
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
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshots.SnapshotStateList
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
import java.util.UUID


@Preview
@Composable
fun Tutorial4_11Screen5() {
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
        TutorialHeader(text = "LazyList Recomposition5")

        StyleableTutorialText(
            text = "In this example items are added to top which recomposes every item. When first item is " +
                    "removed remaining items get recomposed. Long clicking an item removes it from the list and " +
                    "recomposes items below deleted item.",
            bullets = false
        )

        val viewModel = EditViewModel()
        MainScreen(viewModel = viewModel)
    }
}

@Composable
private fun MainScreen(
    viewModel: EditViewModel,
) {


    // 🔥 In this example we made unstable ViewModel lambda stable
    // When rest of the MainScreen is composed it prevents each ListItem to be recomposed
    val onClick = remember {
        { index: Int ->
            viewModel.toggleSelection(index)
        }
    }

    val onLongClick = remember {
        { task: Task ->
            viewModel.deleteTask(task)
        }
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
                    Task(
                        id = id,
                        title = "Task $id"
                    )
                )
            }
        ) {
            Text("Add Task to Top")
        }

        Spacer(modifier = Modifier.height(10.dp))

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

        ListScreen(
            tasks = tasks,
            onItemClick = onClick,
            onItemLongClick = onLongClick

        )
    }
}

@Composable
private fun ListScreen(
    // 🔥🔥 In this example replacing Unstable List with SnapshotStateList
    // prevents recomposition for ListScreen scope even if ListItems are
    // already prevented recomposition with ViewModel lambda stabilization
    tasks: SnapshotStateList<Task>,
    onItemClick: (Int) -> Unit,
    onItemLongClick: (Task) -> Unit,
) {

    SideEffect {
        println("ListScreen is recomposing...$tasks")
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
            itemsIndexed(
                items = tasks,
                // 🔥 Using key keeps scroll position when items added or
                // removed before visible item
                key = { _: Int, task: Task ->
                    task.hashCode()
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
    item: Task,
    onItemClick: () -> Unit,
    onItemLongClick: (Task) -> Unit,
) {

    SideEffect {
        println("Recomposing ${item.title}, selected: ${item.isSelected}")
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

private class EditViewModel : ViewModel() {

    private val initialList = List(6) { index: Int ->
        val id = UUID.randomUUID().toString().apply {
            substring(
                startIndex = lastIndex - 6
            )
        }
        Task(id = id, title = "Task: $index")
    }

    val taskList = mutableStateListOf<Task>().apply {
        addAll(initialList)
    }

    fun toggleSelection(index: Int) {
        println("toggle index: $index")
        val item = taskList[index]
        val isSelected = item.isSelected
        taskList[index] = item.copy(isSelected = !isSelected)
    }

    fun deleteTask(task: Task) {
        taskList.remove(task)
    }

    fun deleteFirstTask() {
        taskList.removeAt(0)
    }

    fun addTaskToFirstIndex(task: Task) {
        taskList.add(0, task)
    }
}
