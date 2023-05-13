package com.smarttoolfactory.tutorial1_1basics.chapter3_layout

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Button
import androidx.compose.material.Checkbox
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.movableContentOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.smarttoolfactory.tutorial1_1basics.ui.backgroundColor
import com.smarttoolfactory.tutorial1_1basics.ui.components.StyleableTutorialText

@Composable
fun Tutorial3_7Screen3() {
    TutorialContent()
}

@Preview
@Composable
private fun TutorialContent() {
    Column(
        modifier = Modifier
            .background(backgroundColor)
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(10.dp)
    ) {

        StyleableTutorialText(
            text = "If one item was inserted at the beginning of the collection " +
                    "the entire view would need to be recomposed instead of just the first " +
                    "item being created and the rest being reused unmodified. " +
                    "This can cause the UI to become confused if, for example, " +
                    "input fields with selection are used in the item block as the selection " +
                    "will not track with the data value but with the index order in the collection. " +
                    "If the user selected text in the first item, inserting a new item will cause " +
                    "selection to appear in the new item selecting what appears to be random " +
                    "text and the selection will be lost in the item the user might have expected " +
                    "to still have selection.",
            bullets = false
        )
        StatefulListSample()
        StyleableTutorialText(
            text = "With movableContentOf, the state can be preserved across such movements",
            bullets = false
        )
        MovableContentOfSample()
    }
}

@Composable
private fun StatefulListSample() {
    val list = remember {
        mutableStateListOf(
            Item("Item1", checked = false),
            Item("Item2", checked = true),
            Item("Item3", checked = true),
        )
    }

    Button(onClick = {
        list.add(0, Item("New Item ${list.size}", true))
    }) {
        Text(text = "Add Item")
    }

    list.forEach { item ->
        StatefulCheckBoxAndCounter(item.title, item.checked)
    }
}

@Composable
private fun MovableContentOfSample() {
    val list = remember {
        mutableStateListOf(
            Item("Item1", checked = false),
            Item("Item2", checked = true),
            Item("Item3", checked = true),
        )
    }

    val movableItems = list.movable { item: Item ->
        StatefulCheckBoxAndCounter(item.title, item.checked)
    }

    Button(onClick = {
        list.add(0, Item("New Item ${list.size}", true))
    }) {
        Text(text = "Add Item")
    }

    Column {
        list.forEach { item: Item ->
            movableItems(item)
        }
    }

}

@Composable
fun <T> List<T>.movable(
    transform: @Composable (item: T) -> Unit
): @Composable (item: T) -> Unit {
    val composedItems = remember(this) {
        mutableMapOf<T, @Composable () -> Unit>()
    }
    return { item: T ->
        composedItems.getOrPut(item) {
            movableContentOf {
                transform(item)
            }
        }.invoke()
    }
}

@Composable
private fun StatefulCheckBoxAndCounter(title: String, isChecked: Boolean) {
    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {

        var checked by remember {
            mutableStateOf(isChecked)
        }
        Text(text = title)
        Checkbox(checked = checked,
            onCheckedChange = {
                checked = it
            }
        )

        Counter()
    }
}