package com.smarttoolfactory.tutorial1_1basics.chapter2_material_widgets

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.smarttoolfactory.tutorial1_1basics.model.Snack
import com.smarttoolfactory.tutorial1_1basics.model.snacks
import com.smarttoolfactory.tutorial1_1basics.ui.components.SnackCard

@Composable
fun Tutorial2_5Screen1() {
    TutorialContent()
}

@Composable
private fun TutorialContent() {
    LazyColumn(
        modifier = Modifier.padding(top = 8.dp),
        content = {
            items(snacks) { item: Snack ->
                SnackCard(snack = item)
            }
        }
    )
}