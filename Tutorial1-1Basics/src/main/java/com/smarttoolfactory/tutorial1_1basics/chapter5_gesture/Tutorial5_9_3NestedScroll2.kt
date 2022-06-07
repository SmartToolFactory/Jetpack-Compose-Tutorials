package com.smarttoolfactory.tutorial1_1basics.chapter5_gesture

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollDispatcher
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.unit.Velocity
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.smarttoolfactory.tutorial1_1basics.ui.BlueGrey400

@Composable
fun Tutorial5_9Screen3() {
    TutorialContent()
}

@Composable
private fun TutorialContent() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(8.dp)
    ) {
        NestedScrollExample()
    }
}

@Composable
private fun ColumnScope.NestedScrollExample() {

    var text by remember { mutableStateOf("") }

    val nestedScrollDispatcher = NestedScrollDispatcher()

    //  Interface to connect to the nested scroll system.
    //
    //  Pass this connection to the [nestedScroll] modifier to participate in the nested scroll
    //  hierarchy and to receive nested scroll events when they are dispatched
    //  by the scrolling child
    //  (scrolling child - the element that actually receives scrolling events
    //  and dispatches them via  [NestedScrollDispatcher]).

    val nestedScrollConnection = remember {
        object : NestedScrollConnection {

            override fun onPreScroll(available: Offset, source: NestedScrollSource): Offset {
                text = "onPreScroll()\n" +
                        "available: $available\n" +
                        "source: $source\n\n"
                return super.onPreScroll(available, source)
            }

            override fun onPostScroll(
                consumed: Offset,
                available: Offset,
                source: NestedScrollSource
            ): Offset {
                text += "onPostScroll()\n" +
                        "consumed: $consumed\n" +
                        "available: $available\n" +
                        "source: $source\n\n"
                return super.onPostScroll(consumed, available, source)
            }

            override suspend fun onPreFling(available: Velocity): Velocity {
                text += "onPreFling()\n" +
                        " available: $available\n\n"
                return super.onPreFling(available)
            }

            override suspend fun onPostFling(consumed: Velocity, available: Velocity): Velocity {
                text += "onPostFling()\n" +
                        "consumed: $consumed\n" +
                        "available: $available\n\n"
                return super.onPostFling(consumed, available)
            }
        }
    }

    Box(
        Modifier
            .weight(1f)
            .nestedScroll(nestedScrollConnection)
    ) {
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(100) {
                Text(
                    text = "I'm item $it",
                    modifier = Modifier
                        .shadow(1.dp, RoundedCornerShape(5.dp))
                        .fillMaxWidth()
                        .background(Color.LightGray)
                        .padding(12.dp),
                    fontSize = 16.sp,
                    color = Color.White
                )
            }
        }
    }

    Spacer(modifier = Modifier.height(10.dp))
    Text(
        text = text,
        modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState())
            .height(250.dp)
            .padding(10.dp)
            .background(BlueGrey400),
        fontSize = 16.sp,
        color = Color.White
    )
}