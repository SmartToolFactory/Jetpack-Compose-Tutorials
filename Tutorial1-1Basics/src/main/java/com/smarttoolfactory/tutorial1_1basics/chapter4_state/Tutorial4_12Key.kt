package com.smarttoolfactory.tutorial1_1basics.chapter4_state

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import com.smarttoolfactory.tutorial1_1basics.ui.backgroundColor
import kotlin.random.Random

@Preview
@Composable
private fun FlowRowCompositionPreview() {
    val viewModel = viewModel<SomeViewModel>()
    MyComposable(viewModel)
}

@Composable
fun MyComposable(someViewModel: SomeViewModel) {

    Column(modifier = Modifier.fillMaxSize().background(backgroundColor)) {

        Button(
            modifier = Modifier.padding(16.dp).fillMaxWidth(),
            onClick = {
                someViewModel.filter()
            }
        ) {
            Text("Filter")
        }

        val itemList = someViewModel.itemList

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            contentPadding = PaddingValues(16.dp)
        ) {

            items(4) {
                Box(
                    modifier = Modifier
                        .background(Color.Red, RoundedCornerShape(16.dp))
                        .fillMaxWidth().height(100.dp)
                )
            }
            item {
                StaggeredList(
                    filter = someViewModel.filter.toString(),
                    itemList = itemList
                )
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun StaggeredList(
    filter: String,
    itemList: List<SomeData>,
) {
    BoxWithConstraints(
        modifier = Modifier
    ) {

        val itemWidth = (maxWidth - 8.dp) / 2

        FlowRow(
            modifier = Modifier.fillMaxSize(),
            maxItemsInEachRow = 2,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            itemList.forEachIndexed { index, it ->

                val height = if (index == 0) {
                    200.dp
                } else Random.nextInt(120, 180).dp

                key(it.id) {
                    MyRow(
                        modifier = Modifier.size(itemWidth, height),
                        item = it
                    )
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

    var counter by remember {
        mutableIntStateOf(0)
    }

    Column(
        modifier = modifier
            .shadow(2.dp, RoundedCornerShape(16.dp))
            .background(Color.White, RoundedCornerShape(16.dp))
            .padding(16.dp)
    ) {
        Text(
            "id: ${item.id}, value: ${item.value}"
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

class SomeViewModel : ViewModel() {

    private val list = listOf(
        SomeData(id = "1", value = "Row1"),
        SomeData(id = "2", value = "Row2"),
        SomeData(id = "3", value = "Row3"),
        SomeData(id = "4", value = "Row4"),
        SomeData(id = "5", value = "Row5")
    )

    var itemList by mutableStateOf(list)

    var filter: Int = 0

    fun filter() {
        if (filter % 3 == 0) {
            itemList = listOf(
                list[0],
                list[1],
                list[2]
            )
        } else if (filter % 3 == 1) {
            itemList = listOf(
                list[1],
                list[2]
            )
        } else {
            itemList = listOf(
                list[0],
                list[2],
                list[3]
            )
        }

        filter++
    }
}

data class SomeData(val id: String, val value: String)