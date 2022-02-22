package com.smarttoolfactory.tutorial1_1basics.chapter6_graphics

import android.graphics.Paint
import android.widget.Toast
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.awaitDragOrCancellation
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.awaitTouchSlopOrCancellation
import androidx.compose.foundation.gestures.forEachGesture
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.*
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.graphics.PathSegment
import androidx.core.graphics.flatten
import com.smarttoolfactory.tutorial1_1basics.R
import com.smarttoolfactory.tutorial1_1basics.chapter2_material_widgets.CheckBoxWithTextRippleFullRow
import com.smarttoolfactory.tutorial1_1basics.chapter5_gesture.MotionEvent
import com.smarttoolfactory.tutorial1_1basics.ui.*
import com.smarttoolfactory.tutorial1_1basics.ui.components.StyleableTutorialText
import com.smarttoolfactory.tutorial1_1basics.ui.components.TutorialText2
import kotlin.math.roundToInt


@Composable
fun Tutorial6_4Screen1() {
    TutorialContent()
}

@Composable
private fun TutorialContent() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundColor)
            .verticalScroll(rememberScrollState())
    ) {

        Text(
            "Draw via Gestures",
            fontWeight = FontWeight.Bold,
            fontSize = 20.sp,
            modifier = Modifier.padding(8.dp)
        )
        StyleableTutorialText(
            text = "Drawing samples with **awaitPointerEventScope** to get touch " +
                    "event states and position, paths to save quads/lines to draw on Canvas." +
                    "\n Examples here only use one path to draw and one path to erase at most, " +
                    "erase being **BlendMode.Clear**." +
                    "Because of this any drawing above erase path will also look like erased.",
            bullets = false
        )
        TutorialText2(text = "Draw with Touch")
        TouchDrawMotionEventsAndPathExample()
        TutorialText2(
            text = "Drawing using drag gesture",
            modifier = Modifier.padding(top = 12.dp)
        )
        TouchDrawWithDragGesture()
//        TutorialText2(
//            text = "Drawing with properties and erase",
//            modifier = Modifier.padding(top = 12.dp)
//        )
//        TouchDrawWithPropertiesAndEraseExample()
//
//        TutorialText2(
//            text = "Draw on Image",
//            modifier = Modifier.padding(top = 12.dp)
//        )
//        TouchDrawImageExample()
//        TutorialText2(
//            text = "Draw Touch Segments",
//            modifier = Modifier.padding(top = 12.dp)
//        )
//        TouchDrawPathSegmentsExample()
//        TutorialText2(
//            text = "Touch Mode moves path",
//            modifier = Modifier.padding(top = 12.dp)
//        )
//        TouchDrawWithMovablePathExample()

    }
}

@Composable
private fun TouchDrawMotionEventsAndPathExample() {

    // Path is what is used for drawing line on Canvas
    val path = remember { Path() }

    // This is motion state. Initially or when touch is completed state is at MotionEvent.Idle
    // When touch is initiated state changes to MotionEvent.Down, when pointer is moved MotionEvent.Move,
    // after removing pointer we go to MotionEvent.Up to conclude drawing and then to MotionEvent.Idle
    // to not have undesired behavior when this composable recomposes. Leaving state at MotionEvent.Up
    // causes incorrect drawing.
    var motionEvent by remember { mutableStateOf(MotionEvent.Idle) }
    // This is our motion event we get from touch motion
    var currentPosition by remember { mutableStateOf(Offset.Unspecified) }
    // This is previous motion event before next touch is saved into this current position
    var previousPosition by remember { mutableStateOf(Offset.Unspecified) }

    // color and text are for debugging and observing state changes and position
    var gestureColor by remember { mutableStateOf(Color.White) }

    // This text is drawn to Canvas
    val canvasText = remember { StringBuilder() }

    val paint = remember {
        Paint().apply {
            textSize = 40f
            color = Color.Black.toArgb()
        }
    }

    // 🔥🔥 If pointer is moved fast, Canvas misses, only Canvas misses it, events work fine
    // MotionEvent.Down events, and skips first down
    // This flag makes sure that first down is always processed by Canvas
    var canvasConsumedDown by remember { mutableStateOf(true) }

    val drawModifier = canvasModifier
        .background(gestureColor)

        // This is for checking Recomposition since Text observes mutable state
        // of position and motion event recomposition of Canvas happens each time.
        // Commenting text will have this recompose only when Composable function is recomposed

//        .background(getRandomColor())
        .pointerInput(Unit) {
            forEachGesture {
                awaitPointerEventScope {

                    // Wait for at least one pointer to press down, and set first contact position
                    val down: PointerInputChange = awaitFirstDown()

                    canvasConsumedDown = false
                    currentPosition = down.position
                    motionEvent = MotionEvent.Down
                    gestureColor = Blue400

                    down.consumeDownChange()

                    // ✏️ ALTERNATIVE 1, this is how default gestures do with while(true) loop
                    // Main pointer is the one that is down initially
                    var pointerId = down.id

                    while (true) {

                        val event: PointerEvent = awaitPointerEvent()


                        val anyPressed = event.changes.any { pointer ->

                            val pressed = pointer.pressed
                            if (pressed) {
                                // This necessary to prevent other gestures or scrolling
                                // when at least one pointer is down on canvas to draw
                                pointer.consumePositionChange()
                            }

                            pressed
                        }


                        // All of the pointers are up
                        if (!anyPressed) {
                            motionEvent = MotionEvent.Up
                            gestureColor = Color.White
                            break
                        } else {
                            if (canvasConsumedDown) {

                                // Get pointer that is down, if first pointer is up
                                // get another and use it if other pointers are also down
                                // event.changes.first() doesn't return same order
                                val pointerInputChange =
                                    event.changes.firstOrNull { it.id == pointerId }
                                        ?: event.changes.first()

                                // Next time will check same pointer with this id
                                pointerId = pointerInputChange.id

                                // Get first pointer that is down
                                // If original pointer is down we get the one next in line
//                                val pointerInputChange = event.changes.first()
                                currentPosition = pointerInputChange.position
                                motionEvent = MotionEvent.Move
                                gestureColor = Green400
                            }
                        }
                    }

                    /*
            ✏️ ALTERNATIVE 2, this is how official docs on  do it
            https://developer.android.google.cn/reference/kotlin/androidx/compose/foundation/gestures/
            package-summary#(androidx.compose.ui.input.pointer.PointerEvent).calculateCentroid(kotlin.Boolean)
         */
//                    do {
//                        // This PointerEvent contains details including events, id, position and more
//                        val event: PointerEvent = awaitPointerEvent()
//
//                        val pointer = event.changes.first()
//
//                        println("🚀 MOVE id: ${pointer.id}, pressed: ${pointer.pressed}")
//                        if (canvasConsumedDown) {
//                            currentPosition = pointer.position
//                            motionEvent = MotionEvent.Move
//                            gestureColor = Green400
//                        }
//
//                    } while (
//                        event.changes.any { pointerInputChange ->
//                            val pressed = pointerInputChange.pressed
//
//                            if (pressed) {
//                                // This necessary to prevent other gestures or scrolling
//                                // when at least one pointer is down on canvas to draw
//                                pointerInputChange.consumePositionChange()
//                            }
//                            pressed
//                        }
//                    )
//                    motionEvent = MotionEvent.Up
//                    gestureColor = Color.White
                }
            }
        }

    Canvas(modifier = drawModifier) {

        println("🔥 CANVAS $motionEvent, position: $currentPosition")

        when (motionEvent) {
            MotionEvent.Down -> {
                path.moveTo(currentPosition.x, currentPosition.y)
                previousPosition = currentPosition
                canvasConsumedDown = true
                canvasText.clear()
                canvasText.append("MotionEvent.Down pos: $currentPosition\n")
            }

            MotionEvent.Move -> {
                if (canvasConsumedDown) {
                    path.quadraticBezierTo(
                        previousPosition.x,
                        previousPosition.y,
                        (previousPosition.x + currentPosition.x) / 2,
                        (previousPosition.y + currentPosition.y) / 2

                    )
                    canvasText.append("MotionEvent.Move pos: $currentPosition\n")

                    previousPosition = currentPosition
                }
            }

            MotionEvent.Up -> {
                path.lineTo(currentPosition.x, currentPosition.y)
                canvasText.append("MotionEvent.Up pos: $currentPosition\n")
                currentPosition = Offset.Unspecified
                previousPosition = currentPosition
                motionEvent = MotionEvent.Idle

            }

            else -> {
                canvasText.append("MotionEvent.Idle\n")
            }
        }

        drawPath(
            color = Color.Red,
            path = path,
            style = Stroke(width = 4.dp.toPx(), cap = StrokeCap.Round, join = StrokeJoin.Round)
        )

        drawText(text = canvasText.toString(), x = 0f, y = 60f, paint)

    }
}

//@Composable
//private fun TouchDrawMotionEventsAndPathExample() {
//
//    // Path is what is used for drawing line on Canvas
//    val path = remember { Path() }
//
//    // This is motion state. Initially or when touch is completed state is at MotionEvent.Idle
//    // When touch is initiated state changes to MotionEvent.Down, when pointer is moved MotionEvent.Move,
//    // after removing pointer we go to MotionEvent.Up to conclude drawing and then to MotionEvent.Idle
//    // to not have undesired behavior when this composable recomposes. Leaving state at MotionEvent.Up
//    // causes incorrect drawing.
//    var motionEvent by remember { mutableStateOf(MotionEvent.Idle) }
//    // This is our motion event we get from touch motion
//    var currentPosition by remember { mutableStateOf(Offset.Unspecified) }
//    // This is previous motion event before next touch is saved into this current position
//    var previousPosition by remember { mutableStateOf(Offset.Unspecified) }
//
//    // color and text are for debugging and observing state changes and position
//    var gestureColor by remember { mutableStateOf(Color.White) }
//
//    // This text is drawn to Canvas
//    val canvasText = remember { StringBuilder() }
//
//    val paint = remember {
//        Paint().apply {
//            textSize = 40f
//            color = Color.Black.toArgb()
//        }
//    }
//
//    // 🔥🔥 If pointer is moved fast, Canvas misses, only Canvas misses it, events work fine
//    // MotionEvent.Down events, and skips first down
//    // This flag makes sure that first down is always processed by Canvas
//    var canvasConsumedDown by remember { mutableStateOf(true) }
//
//    val drawModifier = canvasModifier
//        .background(gestureColor)
//
//        // This is for checking Recomposition since Text observes mutable state
//        // of position and motion event recomposition of Canvas happens each time.
//        // Commenting text will have this recompose only when Composable function is recomposed
//
////        .background(getRandomColor())
//        .pointerInput(Unit) {
//            forEachGesture {
//                awaitPointerEventScope {
//
//                    // Wait for at least one pointer to press down, and set first contact position
//                    val down: PointerInputChange = awaitFirstDown()
//
//                    canvasConsumedDown = false
//                    currentPosition = down.position
//                    motionEvent = MotionEvent.Down
//                    gestureColor = Blue400
//
//                    down.consumeDownChange()
//
//                    do {
//                        // This PointerEvent contains details including events, id, position and more
//                        val event: PointerEvent = awaitPointerEvent()
//
//                        if (canvasConsumedDown) {
//                            currentPosition = event.changes.first().position
//                            motionEvent = MotionEvent.Move
//                            gestureColor = Green400
//                        }
//
//                    } while (
//                        event.changes.any { pointerInputChange ->
//                            val pressed = pointerInputChange.pressed
//
//                            if (pressed) {
//                                // This necessary to prevent other gestures or scrolling
//                                // when at least one pointer is down on canvas to draw
//                                pointerInputChange.consumePositionChange()
//                            }
//                            pressed
//                        }
//                    )
//                    motionEvent = MotionEvent.Up
//                    gestureColor = Color.White
//                }
//            }
//        }
//
//    Canvas(modifier = drawModifier) {
//
//        println("🔥 CANVAS $motionEvent, position: $currentPosition")
//
//        when (motionEvent) {
//            MotionEvent.Down -> {
//                path.moveTo(currentPosition.x, currentPosition.y)
//                previousPosition = currentPosition
//                canvasConsumedDown = true
//                canvasText.clear()
//                canvasText.append("MotionEvent.Down pos: $currentPosition\n")
//            }
//
//            MotionEvent.Move -> {
//                if (canvasConsumedDown) {
//                    path.quadraticBezierTo(
//                        previousPosition.x,
//                        previousPosition.y,
//                        (previousPosition.x + currentPosition.x) / 2,
//                        (previousPosition.y + currentPosition.y) / 2
//
//                    )
//                    canvasText.append("MotionEvent.Move pos: $currentPosition\n")
//
//                    previousPosition = currentPosition
//                }
//            }
//
//            MotionEvent.Up -> {
//                path.lineTo(currentPosition.x, currentPosition.y)
//                canvasText.append("MotionEvent.Up pos: $currentPosition\n")
//                currentPosition = Offset.Unspecified
//                previousPosition = currentPosition
//                motionEvent = MotionEvent.Idle
//
//            }
//
//            else -> {
//                canvasText.append("MotionEvent.Idle\n")
//            }
//        }
//
//        drawPath(
//            color = Color.Red,
//            path = path,
//            style = Stroke(width = 4.dp.toPx(), cap = StrokeCap.Round, join = StrokeJoin.Round)
//        )
//
//        drawText(text = canvasText.toString(), x = 0f, y = 60f, paint)
//
//    }
//}

/**
 * This example uses **awaitTouchSlopOrCancellation** and **awaitDragOrCancellation**
 * to set touch event state, and position.
 */
@Composable
private fun TouchDrawWithDragGesture() {

    val path = remember { Path() }
    var motionEvent by remember { mutableStateOf(MotionEvent.Idle) }
    var currentPosition by remember { mutableStateOf(Offset.Unspecified) }

    // color and text are for debugging and observing state changes and position
    var gestureColor by remember { mutableStateOf(Color.White) }

    // This text is drawn to Canvas
    val canvasText = remember { StringBuilder() }

    val paint = remember {
        Paint().apply {
            textSize = 40f
            color = Color.Black.toArgb()
        }
    }

    val drawModifier = canvasModifier
        .background(gestureColor)
//        .background(getRandomColor())
        .pointerInput(Unit) {
            forEachGesture {
                awaitPointerEventScope {

                    val down: PointerInputChange = awaitFirstDown().also {
                        motionEvent = MotionEvent.Down
                        currentPosition = it.position
                        gestureColor = Blue400
                    }

                    // 🔥 Waits for drag threshold to be passed by pointer
                    // or it returns null if up event is triggered
                    var change: PointerInputChange? =
                        awaitTouchSlopOrCancellation(down.id) { change: PointerInputChange, over: Offset ->
                            change.consumePositionChange()
                            gestureColor = Brown400
                        }


                    if (change == null) {
                        gestureColor = Yellow400
                        motionEvent = MotionEvent.Up
                    } else {

                        while (change != null && change.pressed) {

                            // 🔥 Calls awaitPointerEvent() in a while loop and checks drag change
                            change = awaitDragOrCancellation(change.id)

                            if (change != null && change.pressed) {
                                gestureColor = Green400
                                motionEvent = MotionEvent.Move
                                currentPosition = change.position
                                change.consumePositionChange()
                            }
                        }

                        motionEvent = MotionEvent.Up
                        gestureColor = Color.White
                    }
                }
            }
        }

    Canvas(modifier = drawModifier) {

        println("🔥 CANVAS $motionEvent, position: $currentPosition")

        when (motionEvent) {
            MotionEvent.Down -> {
                path.moveTo(currentPosition.x, currentPosition.y)
                canvasText.clear()
                canvasText.append("MotionEvent.Down\n")
            }
            MotionEvent.Move -> {
                if (currentPosition != Offset.Unspecified) {
                    path.lineTo(currentPosition.x, currentPosition.y)
                    canvasText.append("MotionEvent.Move\n")
                }
            }

            MotionEvent.Up -> {
                path.lineTo(currentPosition.x, currentPosition.y)
                canvasText.append("MotionEvent.Up\n")
                motionEvent = MotionEvent.Idle
            }

            else -> {
                canvasText.append("MotionEvent.Idle\n")
            }
        }

        drawPath(
            color = Color.Red,
            path = path,
            style = Stroke(width = 4.dp.toPx(), cap = StrokeCap.Round, join = StrokeJoin.Round)
        )

        drawText(text = canvasText.toString(), x = 0f, y = 100f, paint)
    }
}

/**
 * Another drawing example. This example tracks positions and have a [PathOption] that
 * stores properties for current drawing.
 *
 * Eraser uses eraserPath and BlendMode.Clear to hide draw path.
 * Since only one path to draw and one path to delete is used this one updates whole
 * drawing when a property is changed
 */
@Composable
private fun TouchDrawWithPropertiesAndEraseExample() {

    val context = LocalContext.current

    // Path used for drawing
    val drawPath = remember { Path() }
    // Path used for erasing. In this example erasing is faked by drawing with canvas color
    // above draw path.
    val erasePath = remember { Path() }

    var motionEvent by remember { mutableStateOf(MotionEvent.Idle) }
    // This is our motion event we get from touch motion
    var currentPosition by remember { mutableStateOf(Offset.Unspecified) }
    // This is previous motion event before next touch is saved into this current position
    var previousPosition by remember { mutableStateOf(Offset.Unspecified) }

    var eraseMode by remember { mutableStateOf(false) }

    val pathOption = rememberPathOption()

    // If we move pointer fast, Canvas misses MotionEvent.Down events, and skips first down
    // this flag makes sure that first down is always processed by Canvas
    var isCanvasDrawDown by remember { mutableStateOf(true) }

    val drawModifier = canvasModifier
        .background(Color.White)
        .pointerInput(Unit) {
            forEachGesture {
                awaitPointerEventScope {

                    // Wait for at least one pointer to press down, and set first contact position
                    val down = awaitFirstDown()

                    motionEvent = MotionEvent.Down
                    currentPosition = down.position
//                    down.consumeDownChange()
                    println("🍏 Modifier AFTER DOWN down: $down")
                    isCanvasDrawDown = false

                    do {
                        // This PointerEvent contains details including events, id,
                        // position and more
                        val event: PointerEvent = awaitPointerEvent()
                        println(
                            "🍏🍏 Modifier DOWN " +
                                    "changedToDown: ${down.changedToDownIgnoreConsumed()}, " +
                                    "anyChangeConsumed: ${down.anyChangeConsumed()}"
                        )
                        if (isCanvasDrawDown) {
                            motionEvent = MotionEvent.Move
                            currentPosition = event.changes.first().position
                        }
                    } while (event.changes.any {
                            val pressed = it.pressed
                            if (pressed) {
                                it.consumeAllChanges()
                            }
                            pressed
                        }
                    )

                    motionEvent = MotionEvent.Up
                }
            }
        }

    Canvas(modifier = drawModifier) {

        // Draw or erase depending on erase mode is active or not
        val currentPath = if (eraseMode) erasePath else drawPath

        println("🔥 CANVAS $motionEvent, position: $currentPosition")

        when (motionEvent) {

            MotionEvent.Down -> {
                currentPath.moveTo(currentPosition.x, currentPosition.y)
                previousPosition = currentPosition
                isCanvasDrawDown = true

            }
            MotionEvent.Move -> {

                if (isCanvasDrawDown) {
                    currentPath.quadraticBezierTo(
                        previousPosition.x,
                        previousPosition.y,
                        (previousPosition.x + currentPosition.x) / 2,
                        (previousPosition.y + currentPosition.y) / 2

                    )
                    previousPosition = currentPosition
                }
            }

            MotionEvent.Up -> {
                currentPath.lineTo(currentPosition.x, currentPosition.y)
            }
            else -> Unit
        }

        with(drawContext.canvas.nativeCanvas) {
            val checkPoint = saveLayer(null, null)

            // Destination
            drawPath(
                color = pathOption.color,
                path = drawPath,
                style = Stroke(
                    width = pathOption.strokeWidth,
                    cap = pathOption.strokeCap,
                    join = pathOption.strokeJoin
                )
            )

            // Source
            drawPath(
                color = Color.Transparent,
                path = erasePath,
                style = Stroke(
                    width = 30f,
                    cap = StrokeCap.Round,
                    join = StrokeJoin.Round
                ),
                blendMode = BlendMode.Clear
            )
            restoreToCount(checkPoint)
        }
    }

    DrawingControl(
        modifier = Modifier
            .padding(bottom = 8.dp, start = 8.dp, end = 8.dp)
            .shadow(1.dp, RoundedCornerShape(8.dp))
            .fillMaxWidth()
            .background(Color.White)
            .padding(4.dp),
        pathOption = pathOption,
        eraseModeOn = eraseMode
    ) {
        motionEvent = MotionEvent.Idle
        eraseMode = it
        if (eraseMode)
            Toast.makeText(context, "Erase Mode On", Toast.LENGTH_SHORT).show()
    }
}


/**
 * In this example of drawing white canvas, draw on an image that drawn to canvas
 */
@Composable
private fun TouchDrawImageExample() {

    val context = LocalContext.current

    // This is the image to draw onto
    val dstBitmap = ImageBitmap.imageResource(id = R.drawable.landscape10)

    // Path used for drawing
    val drawPath = remember { Path() }
    // Path used for erasing. In this example erasing is faked by drawing with canvas color
    // above draw path.
    val erasePath = remember { Path() }

    var motionEvent by remember { mutableStateOf(MotionEvent.Idle) }
    // This is our motion event we get from touch motion
    var currentPosition by remember { mutableStateOf(Offset.Unspecified) }
    // This is previous motion event before next touch is saved into this current position
    var previousPosition by remember { mutableStateOf(Offset.Unspecified) }

    var eraseMode by remember { mutableStateOf(false) }

    val pathOption = rememberPathOption()

    val drawModifier = canvasModifier
        .background(Color.White)
        .pointerInput(Unit) {
            forEachGesture {
                awaitPointerEventScope {

                    // Wait for at least one pointer to press down, and set first contact position
                    awaitFirstDown().also {
                        motionEvent = MotionEvent.Down
                        currentPosition = it.position
                    }

                    do {
                        // This PointerEvent contains details including events, id,
                        // position and more
                        val event: PointerEvent = awaitPointerEvent()
                        motionEvent = MotionEvent.Move
                        currentPosition = event.changes.first().position
                    } while (event.changes.any {
                            val pressed = it.pressed
                            if (pressed) {
                                it.consumePositionChange()
                            }
                            pressed
                        }
                    )

                    motionEvent = MotionEvent.Up
                }
            }
        }

    Canvas(modifier = drawModifier) {

        val canvasWidth = size.width.roundToInt()
        val canvasHeight = size.height.roundToInt()

        // Draw or erase depending on erase mode is active or not
        val currentPath = if (eraseMode) erasePath else drawPath

        when (motionEvent) {

            MotionEvent.Down -> {
                currentPath.moveTo(currentPosition.x, currentPosition.y)
                previousPosition = currentPosition

            }
            MotionEvent.Move -> {

                currentPath.quadraticBezierTo(
                    previousPosition.x,
                    previousPosition.y,
                    (previousPosition.x + currentPosition.x) / 2,
                    (previousPosition.y + currentPosition.y) / 2

                )
                previousPosition = currentPosition
            }

            MotionEvent.Up -> {
                currentPath.lineTo(currentPosition.x, currentPosition.y)
            }
            else -> Unit
        }

        // Draw Image first
        drawImage(
            image = dstBitmap,
            srcSize = IntSize(dstBitmap.width, dstBitmap.height),
            dstSize = IntSize(canvasWidth, canvasHeight)
        )

        with(drawContext.canvas.nativeCanvas) {
            val checkPoint = saveLayer(null, null)

            // Destination
            drawPath(
                color = pathOption.color,
                path = drawPath,
                style = Stroke(
                    width = pathOption.strokeWidth,
                    cap = pathOption.strokeCap,
                    join = pathOption.strokeJoin
                )
            )

            // Source
            drawPath(
                color = Color.Transparent,
                path = erasePath,
                style = Stroke(
                    width = 30f,
                    cap = StrokeCap.Round,
                    join = StrokeJoin.Round
                ),
                blendMode = BlendMode.Clear
            )
            restoreToCount(checkPoint)
        }
    }

    DrawingControl(
        modifier = Modifier
            .padding(bottom = 8.dp, start = 8.dp, end = 8.dp)
            .shadow(1.dp, RoundedCornerShape(8.dp))
            .fillMaxWidth()
            .background(Color.White)
            .padding(4.dp),
        pathOption = pathOption,
        eraseModeOn = eraseMode
    ) {
        motionEvent = MotionEvent.Idle
        eraseMode = it
        if (eraseMode)
            Toast.makeText(context, "Erase Mode On", Toast.LENGTH_SHORT).show()
    }
}

/**
 * This example draws path segments, [PathSegment] of drawn path. Select start or/and end
 * segments to display them as circles.
 */
@Composable
private fun TouchDrawPathSegmentsExample() {

    val path = remember { Path() }
    var motionEvent by remember { mutableStateOf(MotionEvent.Idle) }
    var currentPosition by remember { mutableStateOf(Offset.Unspecified) }

    var displaySegmentStart by remember { mutableStateOf(true) }
    var displaySegmentEnd by remember { mutableStateOf(true) }

    val drawModifier = canvasModifier
        .background(Color.White)
        .pointerInput(Unit) {
            forEachGesture {
                awaitPointerEventScope {

                    // Wait for at least one pointer to press down, and set first contact position
                    val down: PointerInputChange = awaitFirstDown().also {
                        motionEvent = MotionEvent.Down
                        currentPosition = it.position
                    }

                    do {
                        // This PointerEvent contains details including events, id, position and more
                        val event: PointerEvent = awaitPointerEvent()
                        motionEvent = MotionEvent.Move
                        currentPosition = event.changes.first().position
                    } while (event.changes.any {
                            val pressed = it.pressed
                            if (pressed) {
                                it.consumePositionChange()
                            }
                            pressed
                        }
                    )

                    motionEvent = MotionEvent.Up
                }
            }
        }

    Canvas(modifier = drawModifier) {

        when (motionEvent) {
            MotionEvent.Down -> {
                path.moveTo(currentPosition.x, currentPosition.y)
            }
            MotionEvent.Move -> {

                if (currentPosition != Offset.Unspecified) {
                    path.lineTo(currentPosition.x, currentPosition.y)
                }
            }

            MotionEvent.Up -> {
                path.lineTo(currentPosition.x, currentPosition.y)
            }

            else -> Unit
        }


        drawPath(
            color = Color.Red,
            path = path,
            style = Stroke(width = 4.dp.toPx(), cap = StrokeCap.Round, join = StrokeJoin.Round)
        )

        if (displaySegmentStart || displaySegmentEnd) {
            val segments: Iterable<PathSegment> = path.asAndroidPath().flatten()

            segments.forEach { pathSegment: PathSegment ->

                if (displaySegmentStart) {
                    drawCircle(
                        color = Purple400,
                        center = Offset(pathSegment.start.x, pathSegment.start.y),
                        radius = 8f
                    )
                }

                if (displaySegmentEnd) {

                    drawCircle(
                        color = Color.Green,
                        center = Offset(pathSegment.end.x, pathSegment.end.y),
                        radius = 8f,
                        style = Stroke(2f)
                    )
                }
            }
        }
    }

    Column(modifier = Modifier.padding(horizontal = 20.dp)) {
        CheckBoxWithTextRippleFullRow("Display Segment Start", displaySegmentStart) {
            displaySegmentStart = it
        }
        CheckBoxWithTextRippleFullRow("Display Segment End", displaySegmentEnd) {
            displaySegmentEnd = it
        }
    }
}

/**
 * This example uses [PathSegment]s to get handle points to move [Path]s. When initial touch
 * point, turned to [Rect] with radius contains either [PathSegment.getStart] or [PathSegment.getEnd]
 * that [Path] is considered touch. Dragging, translates [Path] same as
 * [PointerInputChange.positionChange] amount
 */
@Composable
private fun TouchDrawWithMovablePathExample() {

    val context = LocalContext.current

    // Path used for drawing
    val drawPath = remember { Path() }
    // Path used for erasing. In this example erasing is faked by drawing with canvas color
    // above draw path.
    val erasePath = remember { Path() }

    // Canvas touch state. Idle by default, Down at first contact, Move while dragging and UP
    // when first pointer is up
    var motionEvent by remember { mutableStateOf(MotionEvent.Idle) }

    // This is our motion event we get from touch motion
    var currentPosition by remember { mutableStateOf(Offset.Unspecified) }

    // This is previous motion event before next touch is saved into this current position
    var previousPosition by remember { mutableStateOf(Offset.Unspecified) }

    var drawMode by remember { mutableStateOf(DrawMode.Draw) }

    val pathOption = rememberPathOption()

    // Check if path is touched in Touch Mode
    var isPathTouched by remember { mutableStateOf(false) }


    val drawModifier = canvasModifier
        .background(Color.White)
        .pointerInput(Unit) {
            forEachGesture {
                awaitPointerEventScope {

                    // Wait for at least one pointer to press down, and set first contact position
                    awaitFirstDown().also {

                        motionEvent = MotionEvent.Down
                        currentPosition = it.position

                        if (drawMode == DrawMode.Touch) {

                            val rect = Rect(currentPosition, 25f)

                            val segments: Iterable<PathSegment> = drawPath
                                .asAndroidPath()
                                .flatten()

                            segments.forEach { pathSegment: PathSegment ->

                                val start = pathSegment.start
                                val end = pathSegment.end

                                if (!isPathTouched && (rect.contains(Offset(start.x, start.y)) ||
                                            rect.contains(Offset(end.x, end.y)))
                                ) {
                                    isPathTouched = true
                                    return@forEach
                                }
                            }
                        }
                    }

                    do {
                        // This PointerEvent contains details including events, id,
                        // position and more
                        val event: PointerEvent = awaitPointerEvent()
                        motionEvent = MotionEvent.Move
                        currentPosition = event.changes.first().position

                    } while (event.changes.any {
                            val pressed = it.pressed
                            if (pressed) {
                                if (drawMode == DrawMode.Touch && isPathTouched) {
                                    // Move draw and erase paths as much as the distance that
                                    // the pointer has moved on the screen minus any distance
                                    // that has been consumed.
                                    drawPath.translate(it.positionChange())
                                    erasePath.translate(it.positionChange())
                                }
                                it.consumePositionChange()
                            }
                            pressed
                        }
                    )

                    motionEvent = MotionEvent.Up
                    isPathTouched = false
                }
            }
        }

    Canvas(modifier = drawModifier) {

        // Draw or erase depending on erase mode is active or not
        val currentPath = if (drawMode == DrawMode.Erase) erasePath else drawPath

        when (motionEvent) {

            MotionEvent.Down -> {
                if (drawMode != DrawMode.Touch) {
                    currentPath.moveTo(currentPosition.x, currentPosition.y)
                }

                previousPosition = currentPosition

            }
            MotionEvent.Move -> {

                if (drawMode != DrawMode.Touch) {
                    currentPath.quadraticBezierTo(
                        previousPosition.x,
                        previousPosition.y,
                        (previousPosition.x + currentPosition.x) / 2,
                        (previousPosition.y + currentPosition.y) / 2

                    )
                }

                previousPosition = currentPosition
            }

            MotionEvent.Up -> {
                if (drawMode != DrawMode.Touch) {
                    currentPath.lineTo(currentPosition.x, currentPosition.y)
                }

            }
            else -> Unit
        }

        with(drawContext.canvas.nativeCanvas) {

            val checkPoint = saveLayer(null, null)

            // Destination
            drawPath(
                color = pathOption.color,
                path = drawPath,
                style = Stroke(
                    width = pathOption.strokeWidth,
                    cap = pathOption.strokeCap,
                    join = pathOption.strokeJoin,
                    pathEffect = if (isPathTouched) PathEffect.dashPathEffect(
                        floatArrayOf(
                            20f,
                            20f
                        )
                    ) else null
                )
            )

            // Source
            drawPath(
                color = Color.Transparent,
                path = erasePath,
                style = Stroke(
                    width = 30f,
                    cap = StrokeCap.Round,
                    join = StrokeJoin.Round
                ),
                blendMode = BlendMode.Clear
            )

            restoreToCount(checkPoint)
        }
    }

    DrawingControlExtended(modifier = Modifier
        .padding(bottom = 8.dp, start = 8.dp, end = 8.dp)
        .shadow(1.dp, RoundedCornerShape(8.dp))
        .fillMaxWidth()
        .background(Color.White)
        .padding(4.dp),
        pathOption = pathOption,
        drawMode = drawMode,
        onDrawModeChanged = {
            motionEvent = MotionEvent.Idle
            drawMode = it
            Toast.makeText(
                context, "Draw Mode: $drawMode", Toast.LENGTH_SHORT
            ).show()
        }
    )
}

private val canvasModifier = Modifier
    .padding(8.dp)
    .shadow(1.dp)
    .fillMaxWidth()
    .height(300.dp)
    .clipToBounds()


@Composable
private fun rememberPathOption(
    strokeWidth: Float = 10f,
    color: Color = Color.Red,
    strokeCap: StrokeCap = StrokeCap.Round,
    strokeJoin: StrokeJoin = StrokeJoin.Round
): PathOption {
    return remember {
        PathOption(strokeWidth, color, strokeCap, strokeJoin)
    }
}

class PathOption(
    strokeWidth: Float = 10f,
    color: Color = Color.Black,
    strokeCap: StrokeCap = StrokeCap.Round,
    strokeJoin: StrokeJoin = StrokeJoin.Round
) {
    var strokeWidth by mutableStateOf(strokeWidth)
    var color by mutableStateOf(color)
    var strokeCap by mutableStateOf(strokeCap)
    var strokeJoin by mutableStateOf(strokeJoin)
    var eraseMode = false
}

private fun DrawScope.drawText(text: String, x: Float, y: Float, paint: Paint) {


    val lines = text.split("\n")
    // 🔥🔥 There is not a built-in function as of 1.0.0
    // for drawing text so we get the native canvas to draw text and use a Paint object
    val nativeCanvas = drawContext.canvas.nativeCanvas

    lines.indices.withIndex().forEach { (posY, i) ->
        nativeCanvas.drawText(lines[i], x, posY * 40 + y, paint)
    }
}


