package com.smarttoolfactory.tutorial1_1basics.chapter2_material_widgets

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.smarttoolfactory.tutorial1_1basics.model.Snack
import com.smarttoolfactory.tutorial1_1basics.model.snacks
import com.smarttoolfactory.tutorial1_1basics.ui.components.FullWidthRow
import com.smarttoolfactory.tutorial1_1basics.ui.components.SnackCard
import kotlinx.coroutines.launch

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
        })
}