package com.smarttoolfactory.tutorial1_1basics.chapter5_gesture

import androidx.compose.animation.core.Animatable
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.input.pointer.PointerEvent
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.PointerInputChange
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.center
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.smarttoolfactory.tutorial1_1basics.R
import com.smarttoolfactory.tutorial1_1basics.chapter2_material_widgets.CheckBoxWithTextRippleFullRow
import com.smarttoolfactory.tutorial1_1basics.chapter5_gesture.gesture.detectDragGesture
import com.smarttoolfactory.tutorial1_1basics.chapter6_graphics.ExposedSelectionMenu
import com.smarttoolfactory.tutorial1_1basics.ui.Blue400
import com.smarttoolfactory.tutorial1_1basics.ui.backgroundColor
import kotlinx.coroutines.launch


@Preview
@Composable
fun DragPagerAndSwipeTest() {

    val images = remember {
        listOf(
            R.drawable.avatar_1_raster,
            R.drawable.avatar_2_raster,
            R.drawable.avatar_3_raster,
            R.drawable.avatar_4_raster,
            R.drawable.avatar_5_raster,
            R.drawable.avatar_6_raster,
        )
    }

    val images2 = remember {
        listOf(
            R.drawable.landscape1,
            R.drawable.landscape2,
            R.drawable.landscape3,
            R.drawable.landscape4
        )
    }

    val items = remember {
        mutableStateListOf<Int>().apply {
            repeat(7) {
                add(it)
            }
        }
    }

    val coroutineScope = rememberCoroutineScope()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundColor)
            .padding(16.dp)
    ) {

        var text by remember {
            mutableStateOf("")
        }

        var isDraggable by remember {
            mutableStateOf(false)
        }

        Box {
            items.forEachIndexed { index, _ ->

                val currentList = if (index % 2 == 0) images else images2
                val pagerState = rememberPagerState {
                    currentList.size
                }

                val animatable = remember {
                    Animatable(0f)
                }

                val offset = remember {
                    Animatable(0f)
                }

                val dragModifier = Modifier
                    .pointerInput(Unit) {
                        val width = size.width.toFloat()
                        val centerX = size.center.x.toFloat()

                        detectDragGesture(
                            requireUnconsumed = true,
                            shouldAwaitTouchSlop = false,
                            passForSlopDetection = PointerEventPass.Main,
                            passForDrag = PointerEventPass.Initial,
                            onDragStart = { change, offset ->
                                text = "onDragStart..."
                                isDraggable =
                                    change.position.x <= centerX &&
                                            pagerState.currentPage == 0 &&
                                            offset.x <= 0f
                                println("onDragStart offset: ${offset.x}")
                            },
                            onDrag = { change, dragAmount ->

                                if (isDraggable) {
                                    change.consume()
                                    val posX = change.position.x.coerceIn(0f, width)
                                    val tempPos = (posX - centerX).coerceAtMost(0f)

                                    if (tempPos == 0f) {
                                        isDraggable = false
                                    } else {
                                        var currentOffset = offset.value
                                        currentOffset += dragAmount.x * 1.25f
                                        currentOffset = currentOffset.coerceAtMost(0f)
                                        var angle = 60f * (currentOffset) / centerX
                                        angle = angle.coerceAtMost(0f)

                                        coroutineScope.launch {
                                            animatable.animateTo(angle)
                                        }

                                        coroutineScope.launch {
                                            offset.animateTo(currentOffset)
                                        }
                                    }
                                }

                                text = "onDrag...$dragAmount\n" +
                                        "offset: ${offset.value}, " +
                                        "angle: ${animatable.value}"

                            },
                            onDragCancel = {
                                text = "onDragCancel"
                                isDraggable = false
                            },
                            onDragEnd = {

                                val angle = animatable.value
                                if (angle < -20f) {
                                    coroutineScope.launch {
                                        animatable.animateTo(-70f)
                                        items.remove(items.lastIndex)
                                    }
                                    coroutineScope.launch {
                                        offset.animateTo(offset.value * 3f)
                                    }
                                } else {
                                    coroutineScope.launch {
                                        animatable.animateTo(0f)
                                    }
                                    coroutineScope.launch {
                                        offset.animateTo(0f)
                                    }
                                }
                                isDraggable = false
                                text = "onDragEnd"

                            }
                        )
                    }
                    .graphicsLayer {
                        val angle = animatable.value
                        val pos = offset.value
                        translationX = pos
                        translationY = -pos * .25f
                        rotationZ = angle
                        transformOrigin = TransformOrigin(.5f, 1f)
                    }

                Column(
                    modifier = if (index == items.lastIndex) dragModifier else Modifier
                        .fillMaxSize()

                ) {

                    Column(
                        modifier = Modifier
                            .clip(RoundedCornerShape(16.dp))
                            .border(1.dp, Color.LightGray, RoundedCornerShape(16.dp))
                            .background(Color.White, RoundedCornerShape(16.dp))
                    ) {
                        HorizontalPager(
                            modifier = Modifier,
                            state = pagerState
                        ) { page ->

                            Image(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .aspectRatio(4 / 5f),
                                painter = painterResource(currentList[page % currentList.size]),
                                contentScale = ContentScale.FillBounds,
                                contentDescription = null
                            )
                        }

                        Row(
                            Modifier
                                .fillMaxWidth()
                                .padding(vertical = 16.dp),
                            horizontalArrangement = Arrangement.Center
                        ) {
                            repeat(pagerState.pageCount) { page ->
                                val color =
                                    if (pagerState.currentPage == page) Blue400
                                    else Color.LightGray.copy(alpha = .6f)
                                Box(
                                    modifier = Modifier
                                        .padding(2.dp)
                                        .clip(CircleShape)
                                        .background(color)
                                        .size(12.dp)
                                )
                            }
                        }

                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(Color.White)
                                .padding(16.dp)
                        ) {
                            Spacer(Modifier.height(16.dp))
                            Text("List index: $index", fontSize = 24.sp)
                            Spacer(Modifier.height(8.dp))
                            Text(text, fontSize = 16.sp)
                        }
                    }
                }
            }
        }
    }
}

@Preview
@Composable
fun DragPagerTest() {

    val images = remember {
        listOf(
            R.drawable.avatar_1_raster,
            R.drawable.avatar_2_raster,
            R.drawable.avatar_3_raster,
            R.drawable.avatar_4_raster,
            R.drawable.avatar_5_raster,
            R.drawable.avatar_6_raster,
        )
    }

    val pagerState = rememberPagerState {
        5
    }

    var consumeNestedScroll by remember {
        mutableStateOf(false)
    }

    val nestedScrollConnection = remember {
        object : NestedScrollConnection {
            override fun onPreScroll(available: Offset, source: NestedScrollSource): Offset {
                println("onPreScroll() available: $available")
                if (consumeNestedScroll) {
                    return available
                }
                return super.onPreScroll(available, source)
            }

            override fun onPostScroll(
                consumed: Offset,
                available: Offset,
                source: NestedScrollSource,
            ): Offset {
                println("onPostScroll() available: $available, consumed: $consumed")
                return super.onPostScroll(consumed, available, source)
            }
        }
    }

    Column(
        modifier = Modifier
            .nestedScroll(nestedScrollConnection)
            .fillMaxSize()
            .background(backgroundColor)
            .padding(16.dp)

    ) {

        var text by remember {
            mutableStateOf("")
        }

        var shouldAwaitPointerEventScope by remember {
            mutableStateOf(true)
        }

        var requireUnconsumed by remember {
            mutableStateOf(true)
        }

        var passForSlopDetection by remember {
            mutableStateOf(
                PointerEventPass.Main
            )
        }

        var passForDrag by remember {
            mutableStateOf(
                PointerEventPass.Main
            )
        }

        Column(
            modifier = Modifier
                .weight(1f)
                .border(1.dp, Color.LightGray, RoundedCornerShape(16.dp))
                .clip(RoundedCornerShape(16.dp))
                .background(Color.White, RoundedCornerShape(16.dp))
                // This is default detectDragGestures
//                .pointerInput(Unit) {
//                    detectDragGestures(
//                        onDragStart = {
//                            text = "detectDragGestures() onDragStart..."
//                        },
//                        onDrag = { change, dragAmount ->
//                            text = "onDrag() dragAmount: $dragAmount"
//                        },
//                        onDragCancel = {
//                            text = "onDragCancel"
//                        },
//                        onDragEnd = {
//                            text = "onDragEnd"
//                        }
//                    )
//                }

                .pointerInput(
                    keys = arrayOf(
                        shouldAwaitPointerEventScope,
                        requireUnconsumed,
                        passForSlopDetection,
                        passForDrag
                    )
                ) {
                    // This one takes shouldAwaitTouchSlop param to not wait slope, or threshold
                    // has requireUnconsumed param to invoke awaitTouchSlope or drag even
                    // if scroll consumed them before
                    // pass to change propagation direction, with initial
                    // it can receive gestures from any gesture with other passes
                    // if there are any with initial pass it should be above them
                    // or in parent to be invoked first
                    detectDragGesture(
                        requireUnconsumed = requireUnconsumed,
                        shouldAwaitTouchSlop = shouldAwaitPointerEventScope,
                        passForSlopDetection = passForSlopDetection,
                        passForDrag = passForDrag,
                        onDragStart = { _, _ ->
                            text = "onDragStart..."
                        },
                        onDrag = { change, dragAmount ->
                            text = "onDrag...$dragAmount"
                            println("onDrag() callback")
//                            change.consume()
                        },
                        onDragCancel = {
                            text = "onDragCancel"
                        },
                        onDragEnd = {
                            text = "onDragEnd"
                        }
                    )
                }

        ) {
            HorizontalPager(
                modifier = Modifier.weight(1f),
                state = pagerState
            ) { page ->

                Image(
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(3 / 4f),
                    painter = painterResource(images[page % images.size]),
                    contentScale = ContentScale.FillBounds,
                    contentDescription = null
                )
            }

            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Spacer(Modifier.height(32.dp))
                Text("Title", fontSize = 24.sp)
                Spacer(Modifier.height(8.dp))
                Text(text, fontSize = 16.sp)
            }
        }

        Spacer(Modifier.height(16.dp))

        CheckBoxWithTextRippleFullRow(
            label = "consumeNestedScroll",
            state = consumeNestedScroll,
            onStateChange = {
                consumeNestedScroll = it
            }
        )

        CheckBoxWithTextRippleFullRow(
            label = "shouldAwaitPointerEventScope",
            state = shouldAwaitPointerEventScope,
            onStateChange = {
                shouldAwaitPointerEventScope = it
            }
        )

        CheckBoxWithTextRippleFullRow(
            label = "RequireUnconsumed",
            state = requireUnconsumed,
            onStateChange = {
                requireUnconsumed = it
            }
        )

        ExposedSelectionMenu(
            title = "PointerEventPass for slop",
            index = when (passForSlopDetection) {
                PointerEventPass.Initial -> 0
                PointerEventPass.Main -> 1
                else -> 2
            },
            options = listOf("Initial", "Main", "Final"),
            onSelected = {
                passForSlopDetection = when (it) {
                    0 -> PointerEventPass.Initial
                    1 -> PointerEventPass.Main
                    else -> PointerEventPass.Final
                }
            }
        )

        ExposedSelectionMenu(title = "PointerEventPass for drag",
            index = when (passForDrag) {
                PointerEventPass.Initial -> 0
                PointerEventPass.Main -> 1
                else -> 2
            },
            options = listOf("Initial", "Main", "Final"),
            onSelected = {
                passForDrag = when (it) {
                    0 -> PointerEventPass.Initial
                    1 -> PointerEventPass.Main
                    else -> PointerEventPass.Final
                }
            }
        )
    }
}

@Preview
@Composable
fun DragTest() {

    var textDefaultDrag by remember {
        mutableStateOf("")
    }

    var textCustomDrag by remember {
        mutableStateOf("")
    }

    Column(
        modifier = Modifier.fillMaxSize()
    ) {

        Text("Default drag")
        Box(modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(2f)
            .background(Color.Red)
            .pointerInput(Unit) {
                detectDragGestures(
                    onDragStart = {
                        textDefaultDrag = "onDragStart()"
                    },
                    onDrag = { change, dragAmount ->
                        textDefaultDrag = "onDrag() ${change.position}, dragAmount: $dragAmount"
                    },
                    onDragEnd = {
                        textDefaultDrag = "onDragEnd"
                    },
                    onDragCancel = {
                        textDefaultDrag = "onDragEnd"
                    }
                )
            }
        ) {
            Text(textDefaultDrag)
        }

        Text("Custom Drag")

        Box(modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(2f)
            .background(Color.Yellow)
            .pointerInput(Unit) {
                // This one takes shouldAwaitTouchSlop param to not wait slope, or threshold
                // has requireUnconsumed param to invoke awaitTouchSlope or drag even
                // if scroll consumed them before
                // pass to change propagation direction, with initial
                // it can receive gestures from any gesture with other passes
                // if there are any with initial pass it should be above them
                // or in parent to be invoked first
                detectDragGesture(
                    requireUnconsumed = false,
                    passForSlopDetection = PointerEventPass.Initial,
                    onDragStart = { _, _ ->
                        textCustomDrag = "onDragStart()"
                    },
                    onDrag = { change, dragAmount ->
                        textCustomDrag = "onDrag() ${change.position}, dragAmount: $dragAmount"
                    },
                    onDragEnd = {
                        textCustomDrag = "onDragEnd"
                    },
                    onDragCancel = {
                        textCustomDrag = "onDragEnd"
                    }
                )
            }
        ) {
            Text(textCustomDrag)
        }
    }
}

@Preview
@Composable
fun PagerConsumeTest() {
    Column(
        modifier = Modifier.fillMaxSize()
    ) {

        val pagerState = rememberPagerState { 5 }

        var text by remember {
            mutableStateOf("")
        }

        var consumeDown by remember {
            mutableStateOf(false)
        }

        var consumeMove by remember {
            mutableStateOf(false)
        }
        Text(text, fontSize = 20.sp, modifier = Modifier.height(90.dp))

        CheckBoxWithTextRippleFullRow(
            label = "consumeDown",
            state = consumeDown,
            onStateChange = {
                consumeDown = it
            }
        )

        CheckBoxWithTextRippleFullRow(
            label = "consumeMove",
            state = consumeMove,
            onStateChange = {
                consumeMove = it
            }
        )

        HorizontalPager(
            modifier = Modifier
                .border(2.dp, Color.Red)
                .customTouch(
                    onDown = { pointer ->
                        text = " onDown() id: ${pointer.id}\n" +
                                "consumed: ${pointer.isConsumed}\n" +
                                "position: ${pointer.position}\n"

                        if (consumeDown) {
                            pointer.consume()
                        }
                    },
                    onMove = { changes ->
                        changes.firstOrNull()?.let {
                            text = " onMove() id: ${it.id}\n" +
                                    "consumed: ${it.isConsumed}\n" +
                                    "position: ${it.position}\n"
                        }

                        if (consumeMove) {
                            changes.forEach { it.consume() }
                        }
                    },
                    onUp = {
                        text = "top onUp()"
                    }
                ),
            state = pagerState
        ) { page ->
            Column(modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.Center) {
                Text("Page: $page", fontSize = 36.sp)
            }
        }
    }
}

@Preview
@Composable
fun DragCustomPropagationTest() {

    var textTop by remember {
        mutableStateOf("")
    }

    var textDrag by remember {
        mutableStateOf("")
    }

    var textBottom by remember {
        mutableStateOf("")
    }

    var shouldAwaitPointerEventScope by remember {
        mutableStateOf(true)
    }

    var requireUnconsumed by remember {
        mutableStateOf(false)
    }

    var passForSlopDetection by remember {
        mutableStateOf(
            PointerEventPass.Main
        )
    }

    var passForDrag by remember {
        mutableStateOf(
            PointerEventPass.Main
        )
    }

    Column {
        CheckBoxWithTextRippleFullRow(
            label = "shouldAwaitPointerEventScope",
            state = shouldAwaitPointerEventScope,
            onStateChange = {
                shouldAwaitPointerEventScope = it
            }
        )

        CheckBoxWithTextRippleFullRow(
            label = "RequireUnconsumed",
            state = requireUnconsumed,
            onStateChange = {
                requireUnconsumed = it
            }
        )

        ExposedSelectionMenu(title = "PointerEventPass for slop",
            index = when (passForSlopDetection) {
                PointerEventPass.Initial -> 0
                PointerEventPass.Main -> 1
                else -> 2
            },
            options = listOf("Initial", "Main", "Final"),
            onSelected = {
                passForSlopDetection = when (it) {
                    0 -> PointerEventPass.Initial
                    1 -> PointerEventPass.Main
                    else -> PointerEventPass.Final
                }
            }
        )

        ExposedSelectionMenu(title = "PointerEventPass for drag",
            index = when (passForDrag) {
                PointerEventPass.Initial -> 0
                PointerEventPass.Main -> 1
                else -> 2
            },
            options = listOf("Initial", "Main", "Final"),
            onSelected = {
                passForDrag = when (it) {
                    0 -> PointerEventPass.Initial
                    1 -> PointerEventPass.Main
                    else -> PointerEventPass.Final
                }
            }
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .border(2.dp, Color.Red)
                .customTouch(
                    onDown = {
                        textTop = "top onDown() id: ${it.hashCode()}, " +
                                "consumed: ${it.isConsumed}, " +
                                "position: ${it.position}\n"
                        println(
                            "top onDown() id: ${it.hashCode()}, " +
                                    "consumed: ${it.isConsumed}, " +
                                    "position: ${it.position}\n"
                        )
                    },
                    onMove = { changes ->
                        changes.firstOrNull()?.let {
                            textTop = "top onMove() id: ${it.hashCode()}, " +
                                    "consumed: ${it.isConsumed}, " +
                                    "position: ${it.position}\n"

                            println(
                                "top onMove() id: ${it.hashCode()}, " +
                                        "consumed: ${it.isConsumed}, " +
                                        "position: ${it.position}\n"
                            )
                        }

                        changes.forEach {
                            it.consume()
                        }
                    },
                    onUp = {
                        textTop = "top onUp()"
                    }
                )
                .pointerInput(
                    keys = arrayOf(
                        shouldAwaitPointerEventScope,
                        requireUnconsumed,
                        passForSlopDetection,
                        passForDrag
                    )
                ) {

                    detectDragGesture(
                        shouldAwaitTouchSlop = shouldAwaitPointerEventScope,
                        requireUnconsumed = requireUnconsumed,
                        passForSlopDetection = passForSlopDetection,
                        passForDrag = passForDrag,
                        onDragStart = { _, it ->
                            textDrag = "onDragStart() $it"
                        },
                        onDrag = { change: PointerInputChange, dragAmount: Offset ->
                            textDrag =
                                "onDrag() id: ${change.id}, " +
                                        "consumed: ${change.isConsumed}, " +
                                        "position: ${change.position}\n"
                        },
                        onDragEnd = {
                            textDrag = "onDragEnd()"
                        },
                        onDragCancel = {
                            textDrag = "onDragCancel()"
                        }
                    )
                }

                .customTouch(
                    onDown = {

                        println(
                            "bottom onDown() id: ${it.hashCode()}, " +
                                    "consumed${it.isConsumed}, " +
                                    "position: ${it.position}\n"
                        )

                        textBottom = "bottom onDown() id: ${it.hashCode()}, " +
                                "consumed${it.isConsumed}, " +
                                "position: ${it.position}\n"
                    },
                    onMove = { changes ->
                        changes.firstOrNull()?.let {
                            println(
                                "bottom onMove() id: ${it.hashCode()}, " +
                                        "consumed: ${it.isConsumed}, " +
                                        "position: ${it.position}\n"
                            )
                            textBottom = "bottom onMove() id: ${it.hashCode()}, " +
                                    "consumed: ${it.isConsumed}, " +
                                    "position: ${it.position}\n"
                        }

                    },
                    onUp = {
                        textBottom = "bottom onUp()"
                    }
                )

        ) {
            Text(textTop, fontSize = 18.sp)
            Text(textDrag, fontSize = 18.sp)
            Text(textBottom, fontSize = 18.sp)
        }

    }

}

@Preview
@Composable
fun DragPropagationTest() {

    var textTop by remember {
        mutableStateOf("")
    }

    var textDrag by remember {
        mutableStateOf("")
    }

    var textBottom by remember {
        mutableStateOf("")
    }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .customTouch(
                onDown = {
                    textTop = "top onDown() id: ${it.id}, " +
                            "consumed: ${it.isConsumed}, " +
                            "position: ${it.position}\n"
                },
                onMove = { changes ->
                    changes.firstOrNull()?.let {
                        textTop = "top onMove() id: ${it.id}, " +
                                "consumed: ${it.isConsumed}, " +
                                "position: ${it.position}\n"
                    }
                },
                onUp = {
                    textTop = "top onUp()"
                }
            )
            .pointerInput(Unit) {
                detectDragGestures(
                    onDragStart = {
                        textDrag = "onDragStart() $it"
                    },
                    onDrag = { change: PointerInputChange, dragAmount: Offset ->
                        textDrag =
                            "onDrag() id: ${change.id}, " +
                                    "consumed: ${change.isConsumed}, " +
                                    "position: ${change.position}\n"
                    },
                    onDragEnd = {
                        textDrag = "onDragEnd()"
                    }
                )
            }

            .customTouch(
                onDown = {

                    it.consume()

                    textBottom = "bottom onDown() id: ${it.id}, " +
                            "consumed${it.isConsumed}, " +
                            "position: ${it.position}\n"

                },
                onMove = { changes ->
                    changes.firstOrNull()?.let {
                        textBottom = "bottom onMove() id: ${it.id}, " +
                                "consumed: ${it.isConsumed}, " +
                                "position: ${it.position}\n"
                    }
                },
                onUp = {
                    textBottom = "bottom onUp()"
                }
            )

    ) {
        Text(textTop, fontSize = 18.sp)
        Text(textDrag, fontSize = 18.sp)
        Text(textBottom, fontSize = 18.sp)
    }
}

private fun Modifier.customTouch(
    pass: PointerEventPass = PointerEventPass.Main,
    onDown: (pointer: PointerInputChange) -> Unit,
    onMove: (changes: List<PointerInputChange>) -> Unit,
    onUp: () -> Unit,
) = this.then(
    Modifier.pointerInput(pass) {
        awaitEachGesture {
            val down = awaitFirstDown(pass = pass, requireUnconsumed = false)
            onDown(down)
            do {
                val event: PointerEvent = awaitPointerEvent(
                    pass = pass
                )

                onMove(event.changes)

            } while (event.changes.any { it.pressed })
            onUp()
        }
    }
)
