package com.smarttoolfactory.tutorial1_1basics.chapter5_gesture

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Velocity
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.smarttoolfactory.tutorial1_1basics.ui.BlueGrey400

/**
 * Check official nested scroll source
 * https://developer.android.com/reference/kotlin/androidx/compose/ui/input/nestedscroll/package-summary#(androidx.compose.ui.Modifier).nestedScroll(androidx.compose.ui.input.nestedscroll.NestedScrollConnection,androidx.compose.ui.input.nestedscroll.NestedScrollDispatcher)
 */
@Preview
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

    //  Interface to connect to the nested scroll system.
    //
    //  Pass this connection to the [nestedScroll] modifier to participate in the nested scroll
    //  hierarchy and to receive nested scroll events when they are dispatched
    //  by the scrolling child
    //  (scrolling child - the element that actually receives scrolling events
    //  and dispatches them via  [NestedScrollDispatcher]).

    /*
        Pre-scroll. This callback is triggered when the descendant is about to perform a scroll
        operation and gives parent an opportunity to consume part of child's delta beforehand.
        This pass should happen every time scrollable components receives delta and dispatches
        it via NestedScrollDispatcher. Dispatching child should take into account how much
        all ancestors above the hierarchy consumed and adjust the consumption accordingly.

        Post-scroll. This callback is triggered when the descendant consumed the delta
        already (after taking into account what parents pre-consumed in 1.) and wants to
        notify the ancestors with the amount of delta unconsumed.
        This pass should happen every time scrollable components receives delta and dispatches
        it via NestedScrollDispatcher. Any parent that receives
        NestedScrollConnection.onPostScroll should consume no more than
        left and return the amount consumed.

        Pre-fling. Pass that happens when the scrolling descendant stopped dragging
        and about to fling with the some velocity.
        This callback allows ancestors to consume part of the velocity.
        This pass should happen before the fling itself happens.
        Similar to pre-scroll, parent can consume part of the velocity
        and nodes below (including the dispatching child) should adjust their
        logic to accommodate only the velocity left.

        Post-fling. Pass that happens after the scrolling descendant stopped
        flinging and wants to notify ancestors about that fact, providing
        velocity left to consume as a part of this. This pass should happen
        after the fling itself happens on the scrolling child.
        Ancestors of the dispatching node will have opportunity to fling themselves
        with the velocityLeft provided. Parent must call notifySelfFinish callback
        in order to continue the propagation of the velocity that is left to ancestors above.
     */

    val parentNestedConnection = remember {
        object : NestedScrollConnection {

            override fun onPreScroll(available: Offset, source: NestedScrollSource): Offset {
                println("🔥Parent onPreScroll() available: $available")

                text = "Parent onPreScroll()\n" +
                        "available: $available\n" +
                        "source: $source\n\n"
                return super.onPreScroll(available, source)
            }

            override fun onPostScroll(
                consumed: Offset,
                available: Offset,
                source: NestedScrollSource
            ): Offset {
                println("🔥🔥Parent onPostScroll() available: $available, consumed: $consumed")

                text += "Parent onPostScroll()\n" +
                        "consumed: $consumed\n" +
                        "available: $available\n" +
                        "source: $source\n\n"
                return super.onPostScroll(consumed, available, source)
            }

            override suspend fun onPreFling(available: Velocity): Velocity {
                text += "Parent onPreFling()\n" +
                        " available: $available\n\n"
                return super.onPreFling(available)
            }

            override suspend fun onPostFling(consumed: Velocity, available: Velocity): Velocity {
                text += "Parent onPostFling()\n" +
                        "consumed: $consumed\n" +
                        "available: $available\n\n"
                return super.onPostFling(consumed, available)
            }
        }
    }

    val childNestedScrollConnection = remember {
        object : NestedScrollConnection {
            override fun onPreScroll(
                available: Offset,
                source: NestedScrollSource
            ): Offset {
                println("Child onPreScroll() available: $available")
                return super.onPreScroll(available, source)
            }

            override fun onPostScroll(
                consumed: Offset,
                available: Offset,
                source: NestedScrollSource
            ): Offset {
                println("Child onPostScroll() available: $available, consumed: $consumed")
                return super.onPostScroll(consumed, available, source)
            }
        }
    }

    /*
        Prints something such as
        I  🔥Parent onPreScroll() available: Offset(0.0, -12.6)
        I  Child onPreScroll() available: Offset(0.0, -12.6)
        I  Child onPostScroll() available: Offset(0.0, 0.0), consumed: Offset(0.0, -12.6)
        I  🔥🔥Parent onPostScroll() available: Offset(0.0, 0.0), consumed: Offset(0.0, -12.6)
     */

    Box(
        Modifier
            .weight(1f)
            .nestedScroll(parentNestedConnection)
    ) {
        LazyColumn(
            modifier = Modifier.nestedScroll(
                connection = childNestedScrollConnection
            ),
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