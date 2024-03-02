package com.smarttoolfactory.tutorial1_1basics.chapter3_layout

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.LayoutCoordinates
import androidx.compose.ui.layout.boundsInParent
import androidx.compose.ui.layout.boundsInRoot
import androidx.compose.ui.layout.onPlaced
import androidx.compose.ui.layout.positionInParent
import androidx.compose.ui.layout.positionInRoot
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.smarttoolfactory.tutorial1_1basics.ui.components.TutorialHeader


@Preview(showBackground = true)
@Composable
fun Tutorial3_9Screen() {
    TutorialContent()
}

@Composable
private fun TutorialContent() {
    Column {
        TutorialHeader("Visibility Percentage")
        ScrollTest1()
    }
}

@Preview(showBackground = true)
@Composable
private fun ScrollTest1() {
    Column {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 32.dp)
                .weight(1f)
                .border(2.dp, Color.Black)
                .verticalScroll(rememberScrollState())
        ) {
            repeat(60) { index ->
                if (index == 15) {
                    MyCustomBox(
                        modifier = Modifier.fillMaxWidth().height(300.dp)
                    ) {
                        Column {
                            Box(
                                modifier = Modifier.fillMaxWidth().weight(3f)
                                    .background(Color.Yellow)
                            )
                            Box(
                                modifier = Modifier.fillMaxWidth().weight(4f).background(Color.Cyan)
                            )
                            Box(
                                modifier = Modifier.fillMaxWidth().weight(3f)
                                    .background(Color.Magenta)
                            )
                        }
                    }
                } else {
                    Text(
                        text = "Row $index",
                        fontSize = 24.sp,
                        modifier = Modifier.fillMaxWidth().padding(8.dp)
                    )
                }
            }
        }
        Box(modifier = Modifier.height(100.dp))

    }

}

@Composable
private fun MyCustomBox(
    modifier: Modifier = Modifier,
    threshold: Int = 30,
    content: @Composable () -> Unit
) {
    var isVisible by remember {
        mutableStateOf(false)
    }

    val context = LocalContext.current

    var visibleTime by remember {
        mutableLongStateOf(0L)
    }

    LaunchedEffect(isVisible) {

        if (isVisible) {
            visibleTime = System.currentTimeMillis()
            Toast.makeText(context, "😆 Item 30% threshold is passed $isVisible", Toast.LENGTH_SHORT)
                .show()
        } else if (visibleTime != 0L) {
            val currentTime = System.currentTimeMillis()
            val totalTime = currentTime - visibleTime
            Toast.makeText(context, "🥵 Item was visible for $totalTime ms", Toast.LENGTH_SHORT)
                .show()
        }
    }

    Box(
        modifier = modifier
            .border(6.dp, if (isVisible) Color.Green else Color.Red)
            .isVisible(threshold = threshold) {
                isVisible = it
            }
    ) {
        content()
    }
}

fun Modifier.isVisible(
    threshold: Int,
    onVisibilityChange: (Boolean) -> Unit
) = this.then(
    Modifier.onPlaced { layoutCoordinates: LayoutCoordinates ->
        val layoutHeight = layoutCoordinates.size.height
        val thresholdHeight = layoutHeight * threshold / 100
        val layoutTop = layoutCoordinates.positionInRoot().y
        val layoutBottom = layoutTop + layoutHeight

        val parent =
            layoutCoordinates.parentLayoutCoordinates

        val parent2 =
            layoutCoordinates.parentCoordinates

        val parent3 =
            layoutCoordinates.parentCoordinates?.parentCoordinates

        val parent4 =
            layoutCoordinates.parentLayoutCoordinates?.parentLayoutCoordinates

        println(
            "🔥 ParentCoordinates : : ${layoutCoordinates.parentCoordinates?.positionInRoot()}, " +
                    "parentLayoutCoordinates: " +
                    "${layoutCoordinates.parentLayoutCoordinates?.positionInRoot()}"
        )

        println(
            "🥹 ParentCoordinates : : ${layoutCoordinates.parentCoordinates?.size}, " +
                    "parentLayoutCoordinates: " +
                    "${layoutCoordinates.parentLayoutCoordinates?.size}"
        )


        println(
            "🍇parent boundsInRoot: ${parent?.boundsInRoot()}, " +
                    "boundsInParent: ${parent?.boundsInParent()}\n" +
                    "positionInRoot: ${parent?.positionInRoot()}, " +
                    "positionInParent: ${parent?.positionInParent()}, " +
                    "size: ${parent?.size}\n"
        )

        println(
            "🍊parent2 boundsInRoot: ${parent2?.boundsInRoot()}, " +
                    "boundsInParent: ${parent2?.boundsInParent()}\n " +
                    "positionInRoot: ${parent2?.positionInRoot()}, " +
                    "positionInParent: ${parent2?.positionInParent()}, " +
                    "size: ${parent2?.size}\n"
        )

        println(
            "🍏parent3 boundsInRoot: ${parent3?.boundsInRoot()}, " +
                    "boundsInParent: ${parent3?.boundsInParent()}\n" +
                    "positionInRoot: ${parent3?.positionInRoot()}, " +
                    "positionInParent: ${parent3?.positionInParent()}, " +
                    "size: ${parent3?.size}\n"
        )

        println(
            "🌻parent4 boundsInRoot: ${parent4?.boundsInRoot()}, " +
                    "boundsInParent: ${parent4?.boundsInParent()}\n" +
                    "positionInRoot: ${parent4?.positionInRoot()}, " +
                    "positionInParent: ${parent4?.positionInParent()}, " +
                    "size: ${parent4?.size}\n"
        )

        parent?.boundsInRoot()?.let { rect: Rect ->
            val parentTop = rect.top
            val parentBottom = rect.bottom


            if (
                parentBottom - layoutTop > thresholdHeight &&
                (parentTop < layoutBottom - thresholdHeight)
            ) {
                onVisibilityChange(true)
            } else {
                onVisibilityChange(false)

            }
        }
    }
)

@Preview(showBackground = true)
@Composable
private fun ScrollTest2() {
    Column {

        var isVisible by remember {
            mutableStateOf(false)
        }

        var coordinates by remember {
            mutableStateOf<LayoutCoordinates?>(null)
        }

        val context = LocalContext.current

        var visibleTime by remember {
            mutableLongStateOf(0L)
        }

        LaunchedEffect(isVisible) {

            if (isVisible) {
                visibleTime = System.currentTimeMillis()
                Toast.makeText(
                    context,
                    "😆 Item 30% threshold is passed $isVisible",
                    Toast.LENGTH_SHORT
                )
                    .show()
            } else if (visibleTime != 0L) {
                val currentTime = System.currentTimeMillis()
                val totalTime = currentTime - visibleTime
                Toast.makeText(context, "🥵 Item was visible for $totalTime ms", Toast.LENGTH_SHORT)
                    .show()
            }
        }


        Column {

            Box(modifier = Modifier.height(50.dp))

//        LazyColumn(
//            modifier = Modifier
//                .onPlaced { layoutCoordinates: LayoutCoordinates ->
//                    coordinates = layoutCoordinates
//                }
//                .weight(1f)
//                .fillMaxSize()
//                .border(2.dp, Color.Black)
//        ) {
//            items(60) { index: Int ->
//                if (index == 15) {
//                    Column(
//                        modifier = Modifier.fillMaxWidth().height(300.dp)
//                            .border(6.dp, if (isVisible) Color.Green else Color.Red)
//                            .isVisible(parentCoordinates = coordinates, threshold = 30) {
//                                isVisible = it
//                            }
//                    ) {
//                        Box(modifier = Modifier.fillMaxWidth().weight(3f).background(Color.Yellow))
//                        Box(modifier = Modifier.fillMaxWidth().weight(4f).background(Color.Cyan))
//                        Box(modifier = Modifier.fillMaxWidth().weight(3f).background(Color.Magenta))
//                    }
//                } else {
//                    Text(
//                        text = "Row $index",
//                        fontSize = 24.sp,
//                        modifier = Modifier.fillMaxWidth().padding(8.dp)
//                    )
//                }
//            }
//        }

            Column(
                modifier = Modifier
                    .onPlaced { layoutCoordinates: LayoutCoordinates ->
                        coordinates = layoutCoordinates
                    }
                    .weight(1f)
                    .fillMaxSize()
                    .border(2.dp, Color.Black)
                    .verticalScroll(rememberScrollState())
            ) {
                repeat(60) { index ->
                    if (index == 15) {
                        Column(
                            modifier = Modifier.fillMaxWidth().height(300.dp)
                                .border(6.dp, if (isVisible) Color.Green else Color.Red)
                                .isVisible(parentCoordinates = coordinates, threshold = 30) {
                                    isVisible = it
                                }
                        ) {
                            Box(
                                modifier = Modifier.fillMaxWidth().weight(3f)
                                    .background(Color.Yellow)
                            )
                            Box(
                                modifier = Modifier.fillMaxWidth().weight(4f).background(Color.Cyan)
                            )
                            Box(
                                modifier = Modifier.fillMaxWidth().weight(3f)
                                    .background(Color.Magenta)
                            )
                        }
                    } else {
                        Text(
                            text = "Row $index",
                            fontSize = 24.sp,
                            modifier = Modifier.fillMaxWidth().padding(8.dp)
                        )
                    }
                }
            }
            Box(modifier = Modifier.height(100.dp))

        }
    }
}

fun Modifier.isVisible(
    parentCoordinates: LayoutCoordinates?,
    threshold: Int,
    onVisibilityChange: (Boolean) -> Unit
) = composed {

    val view = LocalView.current

    Modifier.onPlaced { layoutCoordinates: LayoutCoordinates ->

        if (parentCoordinates == null) return@onPlaced

        val parentLayoutCoordinates = layoutCoordinates.parentLayoutCoordinates
        val parentCoordinates2 = layoutCoordinates.parentCoordinates

        println(
            "🔥 layoutCoordinates as param\n" +
                    "positionInRoot ${parentCoordinates.positionInRoot()}, " +
                    "positionInParent ${parentCoordinates.positionInParent()}\n" +
                    "boundsInRoot ${parentCoordinates.boundsInRoot()}, " +
                    "boundsInParent ${parentCoordinates.boundsInParent()}\n" +
                    "layoutCoordinates.parentLayoutCoordinates\n" +
                    "positionInRoot ${parentLayoutCoordinates?.positionInRoot()}, " +
                    "positionInParent ${parentLayoutCoordinates?.positionInParent()}\n" +
                    "boundsInRoot ${parentLayoutCoordinates?.boundsInRoot()}, " +
                    "boundsInParent ${parentLayoutCoordinates?.boundsInParent()}\n" +
                    "layoutCoordinates.parentCoordinates2\n" +
                    "positionInRoot ${parentCoordinates2?.positionInRoot()}, " +
                    "positionInParent ${parentCoordinates2?.positionInParent()}\n" +
                    "boundsInRoot ${parentCoordinates2?.boundsInRoot()}, " +
                    "boundsInParent ${parentCoordinates2?.boundsInParent()}\n"
        )

        val layoutHeight = layoutCoordinates.size.height
        val thresholdHeight = layoutHeight * threshold / 100
        val layoutTop = layoutCoordinates.positionInRoot().y

        val parentTop = parentCoordinates.positionInParent().y

        val parentHeight = parentCoordinates.size.height
        val parentBottom = (parentTop + parentHeight).coerceAtMost(view.height.toFloat())

        if (
            parentBottom - layoutTop > thresholdHeight &&
            (layoutTop - parentTop > thresholdHeight - layoutHeight)
        ) {
            onVisibilityChange(true)
        } else {
            onVisibilityChange(false)
        }
    }
}
