package com.smarttoolfactory.tutorial1_1basics.chapter3_layout

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.layout
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.Velocity
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay

@Preview
@Composable
fun LazyCompositionCount() {
    BoxWithConstraints {
        val itemWidth = maxWidth / 2 - 40.dp

        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            Text("Default Behavior")

            LazyRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = PaddingValues(16.dp),
            ) {
                items(30) {
                    var loading by remember {
                        mutableStateOf(true)
                    }

                    LaunchedEffect(Unit) {
                        println("Composing First LazyRow item: $it")
                        delay(1000)
                        loading = false
                    }
                    MyRow(itemWidth, loading, it)
                }
            }

            Text("Compose 4 items offscreen in right direction")

            LazyRow(
                modifier = Modifier.fillMaxWidth().layout { measurable, constraints ->
                    val width = constraints.maxWidth + 4 * itemWidth.roundToPx()
                    val wrappedConstraints = constraints.copy(minWidth = width, maxWidth = width)

                    val placeable = measurable.measure(wrappedConstraints)

                    layout(
                        placeable.width, placeable.height
                    ) {
                        val xPos = (placeable.width - constraints.maxWidth) / 2
                        placeable.placeRelative(xPos, 0)
                    }
                },
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = PaddingValues(16.dp)
            ) {
                items(30) {
                    var loading by remember {
                        mutableStateOf(true)
                    }

                    LaunchedEffect(Unit) {
                        println("Composing Second LazyRow item: $it")
                        delay(1000)
                        loading = false
                    }
                    MyRow(itemWidth, loading, it)
                }
            }

            Text("Compose4  items offscreen in right and limit fling")

            LazyRow(
                modifier = Modifier
                    .nestedScroll(rememberFlingNestedScrollConnection())
                    .fillMaxWidth()
                    .layout { measurable, constraints ->
                        val width = constraints.maxWidth + 4 * itemWidth.roundToPx()
                        val wrappedConstraints = constraints.copy(minWidth = width, maxWidth = width)

                        val placeable = measurable.measure(wrappedConstraints)

                        layout(
                            placeable.width, placeable.height
                        ) {
                            val xPos = (placeable.width - constraints.maxWidth) / 2
                            placeable.placeRelative(xPos, 0)
                        }
                    },
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = PaddingValues(16.dp)
            ) {
                items(30) {
                    var loading by remember {
                        mutableStateOf(true)
                    }

                    LaunchedEffect(Unit) {
                        println("Composing Third LazyRow item: $it")
                        delay(1000)
                        loading = false
                    }
                    MyRow(itemWidth, loading, it)
                }
            }

            Text("Compose 4 items offscreen in both scroll directions")

            LazyRow(
                modifier = Modifier
                    .nestedScroll(rememberFlingNestedScrollConnection())
                    .fillMaxWidth()
                    .layout { measurable, constraints ->
                        val width = constraints.maxWidth + 8 * itemWidth.roundToPx()
                        val wrappedConstraints = constraints.copy(minWidth = width, maxWidth = width)

                        val placeable = measurable.measure(wrappedConstraints)

                        layout(
                            placeable.width, placeable.height
                        ) {
                            placeable.placeRelative(0, 0)
                        }
                    },
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = PaddingValues(
                    vertical = 16.dp,
                    horizontal = 16.dp + itemWidth * 4
                )
            ) {
                items(30) {
                    var loading by remember {
                        mutableStateOf(true)
                    }

                    LaunchedEffect(Unit) {
                        println("Composing Forth LazyRow item: $it")
                        delay(1000)
                        loading = false
                    }
                    MyRow(itemWidth, loading, it)
                }
            }

        }
    }
}

@Composable
private fun MyRow(itemWidth: Dp, loading: Boolean, it: Int) {
    Box(
        modifier = Modifier
            .size(itemWidth, 100.dp)
            .background(if (loading) Color.Red else Color.Green, RoundedCornerShape(16.dp))
            .padding(16.dp)
    ) {
        Text("Row $it", fontSize = 26.sp, color = Color.White)
    }
}

@Composable
fun rememberFlingNestedScrollConnection() = remember {
    object : NestedScrollConnection {

        override suspend fun onPreFling(available: Velocity): Velocity {
            val threshold = 3000f
            val availableX = available.x
            val consumed = if (availableX > threshold) {
                availableX - threshold
            } else if (availableX < -threshold) {
                availableX + threshold
            } else {
                0f
            }
            return Velocity(consumed, 0f)
        }

    }
}