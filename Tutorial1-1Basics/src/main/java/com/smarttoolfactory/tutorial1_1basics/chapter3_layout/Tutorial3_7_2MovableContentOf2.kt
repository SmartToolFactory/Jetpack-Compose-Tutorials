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

/**
 * https://cs.android.com/androidx/platform/frameworks/support/+/androidx-main:compose/runtime/design/movable-content.md
 *
 */
@Composable
fun Tutorial3_7Screen2() {
    TutorialContent()
}

@Preview
@Composable
private fun TutorialContent() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundColor)
            .verticalScroll(rememberScrollState())
            .padding(10.dp)
    ) {

        StyleableTutorialText(
            text = "By default, each item's state is keyed against the position of the item " +
                    "in the list or grid. The problem with this is that when items change " +
                    "their position, their state does not match their position anymore",
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
        list.removeFirstOrNull()
    }) {
        Text(text = "Delete First Item")
    }

    list.forEach { item ->
        StatefulCheckBox(item.title, item.checked)
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

    val movableItems =
        list.map { item ->
            movableContentOf {
                StatefulCheckBox(item.title, item.checked)
            }
        }

    Button(onClick = {
        list.removeFirstOrNull()
    }) {
        Text(text = "Delete First Item")
    }

    list.forEachIndexed { index, item ->
        movableItems[index]()
    }
}

@Composable
private fun StatefulCheckBox(title: String, isChecked: Boolean) {
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
    }
}

internal data class Item(val title: String, val checked: Boolean)
