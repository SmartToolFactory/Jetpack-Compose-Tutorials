package com.smarttoolfactory.tutorial1_1basics.chapter6_graphics

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowCircleDown
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.PathMeasure
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.input.pointer.PointerInputChange
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.smarttoolfactory.tutorial1_1basics.R
import com.smarttoolfactory.tutorial1_1basics.ui.backgroundColor
import com.smarttoolfactory.tutorial1_1basics.ui.components.StyleableTutorialText
import com.smarttoolfactory.tutorial1_1basics.ui.components.TutorialText2
import kotlinx.coroutines.launch
import kotlin.math.atan2

@Preview
@Composable
fun Tutorial6_1Screen7() {
    TutorialContent()
}

@Preview
@Composable
private fun TutorialContent() {
    Column(
        modifier = Modifier
            .verticalScroll(rememberScrollState())
            .fillMaxSize()
            .background(backgroundColor)
            .padding(16.dp)
    ) {

//        PathMeasureSample1()
        Text(
            "PathMeasure",
            fontWeight = FontWeight.Bold,
            fontSize = 20.sp,
            modifier = Modifier.padding(8.dp)
        )
        StyleableTutorialText(
            text = "Use PathMeasure to get path segments, position and angle to track progress, angle or if user in bounds of a Path",
            bullets = false
        )

        TutorialText2(text = "PathMeasure segments")
        PathMeasureSample2()
        Spacer(modifier = Modifier.height(32.dp))
        TutorialText2(
            "Animate position and angle"
        )
        AnimateAngleAndPositionOnPathSample()
        Spacer(modifier = Modifier.height(32.dp))
        TutorialText2(
            "Track user path"
        )
        PathTrackingSample()
    }
}

@Preview
@Composable
private fun PathMeasureSample1() {
    val path = remember {
        Path()
    }

    val pathMeasure = remember {
        PathMeasure()
    }

    val pathSegmentList = remember {
        mutableStateListOf<Path>()
    }

    val segmentInfoList = remember {
        mutableStateListOf<PathSegmentInfo>()
    }


    Canvas(
        modifier = canvasModifier
    ) {
        val canvasWidth = size.width
        val canvasHeight = size.height
        val cx = canvasWidth / 2
        val cy = canvasHeight / 2
        val radius = (canvasHeight - 20.dp.toPx()) / 2

        if (path.isEmpty) {
            path.addPath(createPolygonPath(cx, cy, 8, radius))
            pathMeasure.setPath(path = path, forceClosed = false)

            val step = 1
            val pathLength = pathMeasure.length / 100f

            for ((index, percent) in (0 until 100 step step).withIndex()) {

                val destination = Path()

                val distance = pathLength * percent
                pathMeasure.getSegment(
                    startDistance = distance,
                    stopDistance = pathLength * (percent + step),
                    destination = destination
                )

                val position = pathMeasure.getPosition(distance = distance)
                val tangent = pathMeasure.getTangent(distance = distance)

                val tan = (360 + atan2(tangent.y, tangent.x) * 180 / Math.PI) % 360

                segmentInfoList.add(
                    PathSegmentInfo(
                        index = index,
                        position = position,
                        distance = distance,
                        tangent = tan
                    )
                )
                pathSegmentList.add(destination)
            }
        }

        pathSegmentList.forEach {
            drawPath(
                color = Color.LightGray,
                path = it,
                style = Stroke(
                    width = 4.dp.toPx(),
                )
            )
        }

        segmentInfoList.forEach {
            drawCircle(
                color = Color.Magenta,
                radius = 10f,
                center = it.position
            )
        }
    }
}

@Preview
@Composable
private fun PathMeasureSample2() {
    val vector = Icons.Default.ArrowCircleDown
    val painter = rememberVectorPainter(image = vector)

    val path = remember {
        Path()
    }

    val pathMeasure = remember {
        PathMeasure()
    }

    val pathSegmentList = remember {
        mutableStateListOf<Path>()
    }

    val segmentInfoList = remember {
        mutableStateListOf<PathSegmentInfo>()
    }

    var text by remember {
        mutableStateOf("")
    }

    var currentPosition by remember {
        mutableStateOf(
            Offset.Unspecified
        )
    }

    var nearestPositionIndex by remember {
        mutableIntStateOf(-1)
    }

    Canvas(
        modifier = canvasModifier
            .pointerInput(Unit) {
                detectDragGestures(
                    onDragStart = {
                        currentPosition = it
                        var distance = Float.MAX_VALUE

                        segmentInfoList.forEachIndexed { index, pathSegmentInfo ->
                            val currentDistance =
                                pathSegmentInfo.position.minus(currentPosition).getDistanceSquared()
                            if (currentDistance < distance) {
                                distance = currentDistance
                                nearestPositionIndex = index
                            }
                        }

                        segmentInfoList.getOrNull(nearestPositionIndex)?.let {
                            text =
                                "Nearest index: $nearestPositionIndex, position: ${it.position}, tangent: ${it.tangent}"
                        }
                    },
                    onDrag = { change: PointerInputChange, _ ->

                        currentPosition = change.position
                        var distance = Float.MAX_VALUE

                        segmentInfoList.forEachIndexed { index, pathSegmentInfo ->
                            val currentDistance =
                                pathSegmentInfo.position.minus(currentPosition).getDistanceSquared()
                            if (currentDistance < distance) {
                                distance = currentDistance
                                nearestPositionIndex = index
                            }
                        }

                        segmentInfoList.getOrNull(nearestPositionIndex)?.let {

                            text =
                                "Nearest index: $nearestPositionIndex, position: ${it.position}, tangent: ${it.tangent}"
                        }
                    }
                )
            }
    ) {
        val canvasWidth = size.width
        val canvasHeight = size.height
        val cx = canvasWidth / 2
        val cy = canvasHeight / 2
        val radius = (canvasHeight - 20.dp.toPx()) / 2

        if (path.isEmpty) {
            path.addPath(createPolygonPath(cx, cy, 8, radius))
            pathMeasure.setPath(path = path, forceClosed = false)

            val step = 1
            val pathLength = pathMeasure.length / 100f

            for ((index, percent) in (0 until 100 step step).withIndex()) {

                val destination = Path()

                val distance = pathLength * percent
                pathMeasure.getSegment(
                    startDistance = distance,
                    stopDistance = pathLength * (percent + step),
                    destination = destination
                )

                val position = pathMeasure.getPosition(distance = distance)
                val tangent = pathMeasure.getTangent(distance = distance)

                val tan = (360 + atan2(tangent.y, tangent.x) * 180 / Math.PI) % 360

                segmentInfoList.add(
                    PathSegmentInfo(
                        index = index,
                        position = position,
                        distance = distance,
                        tangent = tan
                    )
                )
                pathSegmentList.add(destination)
            }
        }

        pathSegmentList.forEach {
            drawPath(
                color = Color.LightGray,
                path = it,
                style = Stroke(
                    width = 4.dp.toPx(),
                )
            )
        }

        segmentInfoList.forEach {
            drawCircle(
                color = Color.Magenta,
                radius = 5f,
                center = it.position
            )
        }

        segmentInfoList.getOrNull(nearestPositionIndex)?.let {
            if (currentPosition != Offset.Unspecified) {
                drawLine(
                    color = Color.Blue,
                    start = it.position,
                    end = currentPosition,
                    strokeWidth = 4.dp.toPx()
                )
            }

            val iconSize = 140f

            withTransform(
                transformBlock = {
                    rotate(degrees = it.tangent.toFloat() - 90f, pivot = it.position)
                    translate(
                        left = it.position.x - iconSize / 2,
                        top = it.position.y - iconSize / 2
                    )
                }
            ) {
                with(painter) {
                    draw(
                        size = Size(iconSize, iconSize),
                        colorFilter = ColorFilter.tint(color = Color.Red)
                    )
                }
            }
        }
    }

    Text(text)
}


@Preview
@Composable
fun AnimateAngleAndPositionOnPathSample() {

    val painter = painterResource(R.drawable.tg_icon)
    val path = remember {
        Path()
    }

    val trackPath = remember {
        Path()
    }

    val pathMeasure = remember {
        PathMeasure()
    }

    val animatable = remember {
        Animatable(0f)
    }

    val coroutineScope = rememberCoroutineScope()

    Canvas(modifier = canvasModifier.padding(16.dp)) {

        val canvasHeight = size.height

        if (path.isEmpty) {
            val points = getSinusoidalPoints(size)
            path.apply {
                moveTo(0f, canvasHeight / 2f)
                points.forEach { offset: Offset ->
                    lineTo(offset.x, offset.y)
                }
            }

            pathMeasure.setPath(path = path, forceClosed = false)
        }

        val pathLength = pathMeasure.length
        val progress = animatable.value.coerceIn(0f, 1f)
        val distance = pathLength * progress

        val position = pathMeasure.getPosition(distance)
        val tangent = pathMeasure.getTangent(distance)
        val tan = (360 + atan2(tangent.y, tangent.x) * 180 / Math.PI) % 360
        pathMeasure.getSegment(startDistance = 0f, stopDistance = distance, trackPath)

        drawPath(
            path = path,
            color = Color.LightGray,
            style = Stroke(
                width = 2.dp.toPx(),
                pathEffect = PathEffect.dashPathEffect(floatArrayOf(20f, 20f))
            )
        )

        drawPath(
            path = trackPath,
            color = Color.Green,
            style = Stroke(
                width = 2.dp.toPx()
            )
        )

        val iconSize = painter.intrinsicSize.width

        withTransform(
            transformBlock = {
                rotate(degrees = tan.toFloat() + 28, pivot = position)
                translate(
                    left = position.x - iconSize / 2,
                    top = position.y - iconSize / 2
                )
            }
        ) {
            with(painter) {
                draw(
                    size = painter.intrinsicSize
                )
            }
        }
    }

    Button(
        modifier = Modifier.fillMaxWidth(),
        onClick = {
            coroutineScope.launch {
                trackPath.reset()
                animatable.snapTo(0f)
                animatable.animateTo(
                    targetValue = 1f,
                    animationSpec = tween(3000, easing = LinearEasing)
                )
            }
        }
    ) {
        Text("Animate")
    }
}

@Preview
@Composable
private fun PathTrackingSample() {
    val nearestTouchDistance = 70

    val vector = Icons.Default.ArrowCircleDown
    val painter = rememberVectorPainter(image = vector)

    val path = remember {
        Path()
    }

    val trackPath = remember {
        Path()
    }

    val userPath = remember {
        Path()
    }

    val pathMeasure = remember {
        PathMeasure()
    }

    val segmentInfoList = remember {
        mutableStateListOf<PathSegmentInfo>()
    }

    var currentIndex by remember {
        mutableIntStateOf(0)
    }

    var completedIndex by remember {
        mutableIntStateOf(-1)
    }

    var isTouched by remember {
        mutableStateOf(false)
    }

    var text by remember {
        mutableStateOf("")
    }

    Column {
        Canvas(
            modifier = canvasModifier
                .pointerInput(Unit) {
                    detectDragGestures(
                        onDragStart = {
                            isTouched = false

                            val currentPosition = it
                            var distance = Float.MAX_VALUE
                            var tempIndex = -1

                            segmentInfoList.forEachIndexed { index, pathSegmentInfo ->
                                val currentDistance =
                                    pathSegmentInfo.position.minus(currentPosition)
                                        .getDistanceSquared()

                                if (currentDistance < nearestTouchDistance * nearestTouchDistance &&
                                    currentDistance < distance
                                ) {
                                    distance = currentDistance
                                    tempIndex = index
                                }
                            }

                            val validTouch = if (completedIndex == segmentInfoList.lastIndex) {
                                trackPath.reset()
                                tempIndex == 0
                            } else {
                                (tempIndex in completedIndex..completedIndex + 2)
                            }

                            if (validTouch) {
                                currentIndex = tempIndex.coerceAtLeast(0)
                                isTouched = true
                                text = "Touched index $currentIndex"
                                userPath.moveTo(currentPosition.x, currentPosition.y)
                            } else {
                                text =
                                    "Not correct position" +
                                            "\ntempIndex: $tempIndex, nearestPositionIndex: $currentIndex"
                            }
                        },
                        onDrag = { change: PointerInputChange, _ ->

                            if (isTouched) {
                                val currentPosition = change.position
                                var distance = Float.MAX_VALUE
                                var tempIndex = -1

                                segmentInfoList.forEachIndexed { index, pathSegmentInfo ->
                                    val currentDistance =
                                        pathSegmentInfo.position.minus(currentPosition)
                                            .getDistanceSquared()

                                    if (currentDistance < distance) {
                                        distance = currentDistance
                                        tempIndex = index
                                    }
                                }

                                text = "tempIndex: $tempIndex, currentIndex: $currentIndex, " +
                                        "completedIndex: $completedIndex"

                                val dragMinDistance =
                                    (nearestTouchDistance * .65f * nearestTouchDistance * .65)

                                // At last item reset
                                if (completedIndex == segmentInfoList.lastIndex) {
                                    trackPath.reset()
                                    currentIndex = tempIndex
                                } else if (distance > dragMinDistance) {
                                    text = "on drag You moved out of path"
                                    isTouched = false
                                } else if (tempIndex < completedIndex) {
                                    text =
                                        "on drag You moved back" +
                                                "\ntempIndex: $tempIndex, completedIndex: $completedIndex"
                                    isTouched = false
                                } else {
                                    currentIndex = tempIndex
                                }

                                userPath.lineTo(currentPosition.x, currentPosition.y)
                            }
                        }
                    )
                }
        ) {
            val canvasWidth = size.width
            val canvasHeight = size.height
            val cx = canvasWidth / 2
            val cy = canvasHeight / 2
            val radius = (canvasHeight - 20.dp.toPx()) / 2

            if (path.isEmpty) {
                path.addPath(createPolygonPath(cx, cy, 8, radius))
                pathMeasure.setPath(path = path, forceClosed = false)

                val step = 1
                val pathLength = pathMeasure.length / 100f

                for ((index, percent) in (0 until 100 step step).withIndex()) {

                    val destination = Path()

                    val distance = pathLength * percent
                    pathMeasure.getSegment(
                        startDistance = distance,
                        stopDistance = pathLength * (percent + step),
                        destination = destination
                    )

                    val position = pathMeasure.getPosition(distance = distance)
                    val tangent = pathMeasure.getTangent(distance = distance)

                    val tan = (360 + atan2(tangent.y, tangent.x) * 180 / Math.PI) % 360

                    segmentInfoList.add(
                        PathSegmentInfo(
                            index = index,
                            position = position,
                            distance = distance,
                            tangent = tan
                        )
                    )
                }
            }

            drawPath(
                path = path,
                color = Color.LightGray,
                style = Stroke(
                    width = 2.dp.toPx(),
                    pathEffect = PathEffect.dashPathEffect(floatArrayOf(20f, 20f))
                )
            )

            segmentInfoList.getOrNull(currentIndex)?.let {


                val isPathReceived = pathMeasure.getSegment(
                    startDistance = 0f,
                    stopDistance = it.distance,
                    destination = trackPath
                )

                if (isPathReceived) {
                    drawPath(
                        path = trackPath,
                        color = Color.Green,
                        style = Stroke(
                            width = 12.dp.toPx(),
                            cap = StrokeCap.Round,
                            join = StrokeJoin.Round,
                            pathEffect = PathEffect.cornerPathEffect(30f)
                        )
                    )
                }

                completedIndex = currentIndex

                val iconSize = nearestTouchDistance * 2f

                withTransform(
                    transformBlock = {
                        rotate(degrees = it.tangent.toFloat() - 90f, pivot = it.position)
                        translate(
                            left = it.position.x - iconSize / 2,
                            top = it.position.y - iconSize / 2
                        )
                    }
                ) {
                    with(painter) {
                        draw(
                            size = Size(iconSize, iconSize),
                            colorFilter = ColorFilter.tint(
                                color = if (isTouched) Color.Blue else Color.Red
                            )
                        )
                    }
                }
            }

            if (userPath.isEmpty.not()) {
                drawPath(
                    path = userPath,
                    color = Color.Black,
                    style = Stroke(2.dp.toPx())
                )
            }
        }


        Button(
            modifier = Modifier.fillMaxWidth(),
            onClick = {
                currentIndex = 0
                completedIndex = -1
                userPath.reset()
                trackPath.reset()
                isTouched = false
            }
        ) {
            Text("Reset")
        }
        Text(
            modifier = Modifier.fillMaxWidth().height(48.dp),
            text = text,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

data class PathSegmentInfo(
    val index: Int,
    val position: Offset,
    val distance: Float,
    val tangent: Double,
    val isCompleted: Boolean = false
)

private val canvasModifier = Modifier
    .shadow(1.dp)
    .background(Color.White)
    .fillMaxWidth()
    .aspectRatio(1f)