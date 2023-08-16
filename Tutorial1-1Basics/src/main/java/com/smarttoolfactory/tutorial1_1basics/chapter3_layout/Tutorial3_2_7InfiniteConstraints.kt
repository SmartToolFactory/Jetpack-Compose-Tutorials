package com.smarttoolfactory.tutorial1_1basics.chapter3_layout

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.Placeable
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.smarttoolfactory.tutorial1_1basics.ui.components.StyleableTutorialText
import com.smarttoolfactory.tutorial1_1basics.ui.components.TutorialHeader

@Preview
@Composable
fun Tutorial3_2Screen7() {
    TutorialContent()
}

@Preview
@Composable
private fun TutorialContent() {

    Column(
        modifier = Modifier
            .fillMaxSize()
    ) {

        TutorialHeader(text = "Infinite Constraints")

        StyleableTutorialText(
            text = "Infinite constraints or **Constraints.Infinity** " +
                    "are passed when **Modifier.scroll** modifier is assigned or explicitly passed " +
                    "from parent. There are some limits using infinite constraints. For, instance " +
                    "any mathematical operation with **Constraints.Infinity** throws exception when " +
                    "measuring a **Measurable**",
            bullets = false
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .border(2.dp, Color.Green)
        ) {
            CustomLayout(
                modifier = Modifier
                    .border(4.dp, Color.Red)
            ) {
                Text("Hello World", modifier = Modifier.border(5.dp, Color.Blue))
            }
        }

        // 🔥Intrinsic Modifier calls Layout twice, first with 0, Constraints.Infinity
        // then the layout width from this measurement second time
//        CustomLayout(
//            modifier = Modifier
//                .border(2.dp, Color.Green)
//                .height(IntrinsicSize.Min)
//        ) {
//            Text("Hello World", modifier = Modifier.border(2.dp, Color.Blue))
//            Box(modifier = Modifier
//                .width(100.dp)
//                .height(40.dp)
//                .background(Color.Red))
//        }
    }
}

@Composable
private fun CustomLayout(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    Layout(
        modifier = modifier,
        content = content
    ) { measurables, constraints ->

        println("🍏 CustomLayout Measurement Scope constraints(): $constraints")
        val wrappedConstraints = constraints.copy(
            // 🔥🔥 1- minHeight cannot be greater than maxHeight
            // and both minHeight and maxHeight cannot be Constraints.Infinity
            // because a Placeable cannot have infinite size
            /*
               THROWS: Can't represent a size of 2147483647 in Constraints
             */
            // Comment out to see behavior
//            minHeight = Constraints.Infinity,
//            maxHeight = Constraints.Infinity

            /*
              THROWS Can't represent a size of 1073741823 in Constraints
           */
            // 🔥🔥 2- Mathematical operations with Constraints.Infinity are not allowed
            // If it's Constraints.Infinity check before doing operations
            // Comment out to see behavior
//            maxHeight = constraints.maxHeight/2
        )

        val placeables = measurables.map {
            it.measure(wrappedConstraints)
        }

        var y = 0

        val layoutWidth = placeables.maxOf { it.width }
        // 🔥🔥 3-) Having Constraints.Infinity with Intrinsic.Min/MaxHeight throws
        // exception Can't represent a size of 2147483647 in Constraints
        val layoutHeight = constraints.maxHeight

        // This is for not passing infinite min and max constraints on second Placement Scope
        // Pass if we use Intrinsic height
//        val layoutHeight = if (constraints.hasFixedHeight && constraints.hasBoundedHeight){
//            constraints.maxHeight
//        }else {
//            placeables.sumOf { it.height }
//        }

        layout(layoutWidth, layoutHeight) {
            println("🍎 CustomLayout Placement Scope constraints(): $constraints")

            placeables.forEach { placeable: Placeable ->
                placeable.placeRelative(0, y)
                y += placeable.height
            }
        }
    }
}
