package com.smarttoolfactory.tutorial1_1basics.chapter2_material_widgets

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Checkbox
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn

@ExperimentalMaterialApi
@Preview
@Composable
fun Tutorial2_14Screen() {
    val taskViewModel = TaskViewModel()
    TutorialContent(taskViewModel,taskViewModel::onCheckedChange)
}

@Composable
fun TutorialContent(taskViewModel: TaskViewModel, onCheckedChange: (Task) -> Unit) {
    val list by remember(taskViewModel) { taskViewModel.taskList }.collectAsStateWithLifecycle()
    LazyColumn {
        items(
            items = list,
            key = { it.id },
            itemContent = { task ->
                ListItem(
                    task = task,
                    onCheckedChange = { taskItem ->
                        onCheckedChange(taskItem)
                    }
                )
            }
        )
    }
}

@Composable
private fun ListItem(task: Task, onCheckedChange: (Task) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(64.dp)
            .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Checkbox(
            checked = task.isCompleted,// checked,
            onCheckedChange = { onCheckedChange(task) }
        )
        Spacer(Modifier.width(8.dp))
        Text(
            text = task.title,
            style = MaterialTheme.typography.body1,
            overflow = TextOverflow.Ellipsis,
            maxLines = 1
        )
    }
}

class TaskViewModel : ViewModel() {

    private val list = mutableListOf(
        Task(1L, "Task1", true),
        Task(2L, "Task2", false),
        Task(3L, "Task3", false),
        Task(4L, "Task4", false),
        Task(5L, "Task5", false),
        Task(6L, "Task6", false),
        Task(7L, "Task7", true),
        Task(8L, "Task8", false),
        Task(9L, "Task9", false),
        Task(10L, "Task10", false),
        Task(11L, "Task11", false),
        Task(12L, "Task12", false),
        Task(13L, "Task13", true),
        Task(14L, "Task14", false),
        Task(15L, "Task15", false),
    )

    private val _taskList: MutableStateFlow<List<Task>> = MutableStateFlow(list)

    val taskList: StateFlow<List<Task>>
        get() = _taskList.stateIn(viewModelScope, SharingStarted.WhileSubscribed(), listOf())

    fun onCheckedChange(task: Task) {

        val current = _taskList.value
        val replacement =
            current.map { if (it.id == task.id) it.copy(isCompleted = !it.isCompleted) else it }
        _taskList.value = replacement

    }
}

data class Task(val id: Long, var title: String, var isCompleted: Boolean)