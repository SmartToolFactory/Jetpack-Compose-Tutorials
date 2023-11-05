package com.smarttoolfactory.tutorial1_1basics.chapter2_material_widgets

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationVector1D
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.animateScrollBy
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.smarttoolfactory.tutorial1_1basics.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.Collections.swap
import java.util.UUID
import kotlin.math.abs

private class MyData(val uuid: String, val value: String)

// TODO Fix increasing swap animations
@Preview
@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun AnimatedList() {
    Column(modifier = Modifier.fillMaxSize()) {

        val items: SnapshotStateList<MyData> = remember {
            mutableStateListOf<MyData>().apply {
                repeat(20) {
                    add(MyData(uuid = UUID.randomUUID().toString(), "Row $it"))
                }
            }
        }

        val lazyListState = rememberLazyListState()

        val duration = 300

        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            state = lazyListState,
            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 0.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            items(
                items = items,
                key = {
                    it.uuid
                }
            ) {
                Row(
                    modifier = Modifier

                        .animateItemPlacement(
                            tween(durationMillis = duration)
                        )
                        .shadow(1.dp, RoundedCornerShape(8.dp))
                        .background(Color.White)
                        .fillMaxWidth()
                        .padding(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Image(
                        modifier = Modifier
                            .clip(RoundedCornerShape(10.dp))
                            .size(50.dp),
                        painter = painterResource(id = R.drawable.landscape1),
                        contentScale = ContentScale.FillBounds,
                        contentDescription = null
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(it.value, fontSize = 18.sp)
                }
            }
        }

        var fromString by remember {
            mutableStateOf("17")
        }

        var toString by remember {
            mutableStateOf("0")
        }

        var animate by remember { mutableStateOf(false) }

        if (animate) {

            val from = try {
                Integer.parseInt(fromString)
            } catch (e: Exception) {
                0
            }

            val to = try {
                Integer.parseInt(toString)
            } catch (e: Exception) {
                0
            }

            animatedSwap(
                lazyListState = lazyListState,
                items = items,
                from = from,
                to = to,
                duration = duration
            ) {
                animate = false
            }
        }

        Row(modifier = Modifier.fillMaxWidth()) {

            TextField(
                value = fromString,
                onValueChange = {
                    fromString = it
                },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )

            TextField(
                value = toString,
                onValueChange = {
                    toString = it
                },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )

        }

        Button(
            modifier = Modifier
                .padding(8.dp)
                .fillMaxWidth(),
            onClick = {
                animate = true
            }
        ) {
            Text("Swap")
        }
    }
}

private fun alternativeAnimate(
    from: Int,
    to: Int,
    coroutineScope: CoroutineScope,
    lazyListState: LazyListState,
    animatable: Animatable<Int, AnimationVector1D>,
    items: SnapshotStateList<MyData>
) {

    val difference = from - to
    var currentValue: Int = from

    println("🔥 Alternative from: $from, to: $to, difference: $difference, currentValue: $currentValue")

    coroutineScope.launch {
        animatable.snapTo(from)

        animatable.animateTo(to,
            tween(350 * abs(difference), easing = LinearEasing),
            block = {
                val nextValue = this.value
                if (abs(currentValue - nextValue) == 1) {
                    coroutineScope.launch {

                        val visibleItems =
                            lazyListState.layoutInfo.visibleItemsInfo.map { it.index }

                        println("Visible items: $visibleItems, next: $nextValue")

//                        if (nextValue == 0 || visibleItems.contains(nextValue).not()) {
//                            lazyListState.scrollToItem(nextValue)
//                        }

                        swap(items, currentValue, nextValue)
                        currentValue = nextValue


                    }
                }
            }
        )
    }
}

@Composable
private fun animatedSwap(
    lazyListState: LazyListState,
    items: SnapshotStateList<MyData>,
    from: Int,
    to: Int,
    duration: Int,
    onFinish: () -> Unit
) {


    LaunchedEffect(key1 = Unit) {

        val difference = from - to
        val increasing = difference < 0

        var currentValue: Int = from

        var visibleItems =
            lazyListState.layoutInfo.visibleItemsInfo

        var visibleItemIndices = visibleItems.map { it.index }

        // If current item is not in viewPort animate to it first before starting scrolling
        if (visibleItemIndices.contains(currentValue).not()) {
            // Depending on direction add or subtract one to set item at the bottom
            val offset = if (increasing) 0 else +1
            val scrollIndex = (currentValue - visibleItemIndices.size + offset).coerceIn(
                0, items.lastIndex
            )
            lazyListState.animateScrollToItem(scrollIndex)
            delay(100)
        }

        repeat(abs(difference)) {


            val temp = currentValue

            if (increasing) {
                currentValue++
            } else {
                currentValue--
            }

            visibleItems =
                lazyListState.layoutInfo.visibleItemsInfo

            visibleItemIndices = visibleItems.map { it.index }


            if (!increasing && currentValue == 0) {
                swap(items, temp, currentValue)
                val firstItemHeight = visibleItems.firstOrNull()?.size ?: 0
                // sometimes it doesn't exactly scroll to 0 position with this multiplier
                // make sure that we always scroll to top position of LazyColumn
                lazyListState.animateScrollBy((-firstItemHeight * 1.05f), tween(duration))
                delay(duration.toLong())

            } else if (it != 0 && visibleItemIndices.contains(currentValue - 2).not()) {
                // Depending on direction add or subtract one to set item at the bottom
                val offset = if (increasing) 0 else 0
                val scrollIndex = (currentValue - visibleItemIndices.size + offset).coerceIn(
                    0, items.lastIndex
                )

                println("REPLACING currentValue: $currentValue, visibleItemIndices: $visibleItemIndices")
                swap(items, temp, currentValue)
                delay(duration.toLong())
                lazyListState.animateScrollToItem(scrollIndex)

            } else {
                swap(items, temp, currentValue)
                delay(duration.toLong())
            }

            println("END")

        }
        onFinish()
    }
}