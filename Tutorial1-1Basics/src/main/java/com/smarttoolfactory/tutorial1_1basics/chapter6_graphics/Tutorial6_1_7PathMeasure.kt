package com.smarttoolfactory.tutorial1_1basics.chapter6_graphics

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
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowCircleDown
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathMeasure
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.input.pointer.PointerInputChange
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.smarttoolfactory.tutorial1_1basics.ui.backgroundColor
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

        PathMeasureSample1()

        Spacer(modifier = Modifier.height(32.dp))
        PathMeasureSample2()
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
    val path = remember {
        Path()
    }

    val vector = Icons.Default.ArrowCircleDown
    val painter = rememberVectorPainter(image = vector)

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

//        segmentInfoList.forEach {
//            drawCircle(
//                color = Color.Magenta,
//                radius = 10f,
//                center = it.position
//            )
//        }

        segmentInfoList.getOrNull(nearestPositionIndex)?.let {
            if (currentPosition != Offset.Unspecified) {
                drawLine(
                    color = Color.Blue,
                    start = it.position,
                    end = currentPosition,
                    strokeWidth = 4.dp.toPx()
                )
            }

            val iconSize = 100f

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
    Canvas(modifier = canvasModifier.fillMaxWidth().aspectRatio(1f)) {
        val points = getSinusoidalPoints(size)
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