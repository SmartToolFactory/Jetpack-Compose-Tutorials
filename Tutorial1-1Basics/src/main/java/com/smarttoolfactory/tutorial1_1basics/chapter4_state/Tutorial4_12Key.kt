package com.smarttoolfactory.tutorial1_1basics.chapter4_state

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.scaleIn
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import java.util.UUID

@Preview
@Composable
fun Test() {
    val viewModel = viewModel<SomeViewModel>()
    MyComposable(viewModel)
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun MyComposable(someViewModel: SomeViewModel) {

    Column(modifier = Modifier) {

        Button(
            modifier = Modifier.padding(16.dp).fillMaxWidth(),
            onClick = {
                someViewModel.filter()
            }
        ) {
            Text("Filter")
        }

        val itemList = someViewModel.itemList

        BoxWithConstraints(
            modifier = Modifier.padding(horizontal = 16.dp)
        ) {

            val itemWidth = (maxWidth - 8.dp) / 2

            FlowRow(
                modifier = Modifier.fillMaxSize(),
                maxItemsInEachRow = 2,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                itemList.forEach {
                    key(it.id) {
                        MyRow(
                            modifier = Modifier.size(itemWidth),
                            item = it
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun MyRow(
    modifier: Modifier = Modifier,
    item: SomeData,
) {

    var visible by remember {
        mutableStateOf(false)
    }

    var counter by remember {
        mutableIntStateOf(0)
    }

    LaunchedEffect(visible) {
        println("Composing $item")
        if (visible.not()) {
            visible = true
        }
    }

    val context = LocalContext.current

    DisposableEffect(Unit) {
        onDispose {
            Toast.makeText(context, "Item ${item.id} is leaving composition", Toast.LENGTH_SHORT).show()
        }
    }

    AnimatedVisibility(
        visible = visible,
        enter = fadeIn(
            tween(300)
        ) + scaleIn(
            tween(300)
        )
    ) {
        Column(
            modifier = modifier
                .shadow(2.dp, RoundedCornerShape(16.dp))
                .background(Color.White, RoundedCornerShape(16.dp))
                .padding(16.dp)
        ) {
            Text(
                "id: ${item.id}, value: ${item.value}\n" +
                        "unique: ${item.uniqueId}"
            )
            Spacer(modifier = Modifier.weight(1f))
            Button(
                modifier = Modifier.fillMaxWidth(),
                onClick = {
                    counter++
                }
            ) {
                Text("Counter: $counter")
            }
        }
    }
}

class SomeViewModel : ViewModel() {

    private val list = listOf(
        SomeData(id = "1", value = "Row1"),
        SomeData(id = "2", value = "Row2"),
        SomeData(id = "3", value = "Row3"),
        SomeData(id = "4", value = "Row4"),
        SomeData(id = "5", value = "Row5")
    )

    var itemList by mutableStateOf(list)

    private var counter: Int = 0

    fun filter() {
        if (counter % 3 == 0) {
            itemList = listOf(
                list[0].copy(uniqueId = UUID.randomUUID().toString()),
                list[1].copy(uniqueId = UUID.randomUUID().toString()),
                list[2].copy(uniqueId = UUID.randomUUID().toString())
            )
        } else if (counter % 3 == 1) {
            itemList = listOf(
                list[1].copy(uniqueId = UUID.randomUUID().toString()),
                list[2].copy(uniqueId = UUID.randomUUID().toString())
            )
        } else {
            itemList = listOf(
                list[0].copy(uniqueId = UUID.randomUUID().toString()),
                list[2].copy(uniqueId = UUID.randomUUID().toString()),
                list[3].copy(uniqueId = UUID.randomUUID().toString())
            )
        }

        counter++
    }
}

data class SomeData(val id: String, val value: String, var uniqueId: String = "")