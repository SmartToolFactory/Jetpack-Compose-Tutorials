package com.smarttoolfactory.tutorial1_1basics.chapter2_material_widgets

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.GridCells
import androidx.compose.foundation.lazy.LazyVerticalGrid
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.smarttoolfactory.tutorial1_1basics.model.Snack
import com.smarttoolfactory.tutorial1_1basics.model.snacks
import com.smarttoolfactory.tutorial1_1basics.ui.components.GridSnackCard

@ExperimentalFoundationApi
@Composable
fun Tutorial2_5Screen5() {
    TutorialContent()
}

@ExperimentalFoundationApi
@Composable
private fun TutorialContent() {
    LazyVerticalGrid(
        contentPadding = PaddingValues(12.dp),
        modifier = Modifier.background(Color(0xffECEFF1)),
        cells = GridCells.Fixed(3),
        content = {
            items(items = snacks, itemContent = { snack: Snack ->
                GridSnackCard(snack = snack)
            })
        })
}