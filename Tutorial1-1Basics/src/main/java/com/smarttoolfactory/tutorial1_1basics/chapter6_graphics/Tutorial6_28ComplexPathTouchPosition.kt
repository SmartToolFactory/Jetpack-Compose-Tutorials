package com.smarttoolfactory.tutorial1_1basics.chapter6_graphics

import android.graphics.Matrix
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationVector1D
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathOperation
import androidx.compose.ui.graphics.asAndroidPath
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.input.pointer.PointerInputChange
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.smarttoolfactory.tutorial1_1basics.ui.Blue400
import com.smarttoolfactory.tutorial1_1basics.ui.Orange400
import com.smarttoolfactory.tutorial1_1basics.ui.components.TutorialHeader

@Preview
@Composable
fun Tutorial6_28Screen() {
    TutorialContent()
}

@Composable
private fun TutorialContent() {
    Column(
        modifier = Modifier
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        TutorialHeader(text = "Complex Path Touch and Animation")
        AnimatedMapSectionPathTouchSample()
        Spacer(modifier = Modifier.height(16.dp))
        PathTouchSample()
    }
}

@Preview
@Composable
private fun PathTouchSample() {

    var touchIndex by remember {
        mutableIntStateOf(-1)
    }
    val pathDataList = remember {
        mutableStateListOf<PathData>().apply {
            repeat(5) {
                val cx = 170f * (it + 1)
                val cy = 170f * (it + 1)
                val radius = 120f
                val sides = 3 + it
                val path = createPolygonPath(cx, cy, sides, radius)
                add(
                    PathData(
                        path = path,
                        center = Offset(0f, 0f)
                    )
                )
            }
        }
    }

    Canvas(
        modifier = Modifier
            .background(Blue400)
            .fillMaxWidth()
            .aspectRatio(1f)
            .pointerInput(Unit) {
                detectDragGestures(
                    onDragStart = { offset: Offset ->

                        val touchPath = Path().apply {
                            addRect(
                                Rect(
                                    center = offset,
                                    radius = .5f
                                )
                            )
                        }

                        pathDataList.forEachIndexed { index, data ->

                            val path = data.path

                            val differencePath =
                                Path.combine(
                                    operation = PathOperation.Difference,
                                    touchPath,
                                    path
                                )

                            val isInBounds = differencePath.isEmpty

                            if (isInBounds) {
                                touchIndex = index
                            }
                        }
                    },
                    onDrag = { change: PointerInputChange, dragAmount: Offset ->
                        val pathData = pathDataList.getOrNull(touchIndex)

                        pathData?.let {

                            val matrix = Matrix().apply {
                                postTranslate(dragAmount.x, dragAmount.y)
                            }

                            pathData.path.asAndroidPath().transform(matrix)

                            pathDataList[touchIndex] = it.copy(
                                center = dragAmount
                            )

                        }

                    },
                    onDragCancel = {
                        touchIndex = -1
                    },
                    onDragEnd = {
                        touchIndex = -1
                    }
                )
            }
    ) {

        pathDataList.forEachIndexed { index: Int, pathData: PathData ->

            val path = pathData.path

            if (touchIndex != index) {
                drawPath(
                    path,
                    color = Color.Black
                )
            }
        }

        pathDataList.getOrNull(touchIndex)?.let { pathData ->

            val path = pathData.path

            drawPath(
                path = path,
                color = Color.Green
            )
        }
    }
}


@Preview
@Composable
private fun MapSectionPathTouchSample() {

    val pathList = remember {
        Netherlands.PathMap.entries.map {
            val path = Path()

            path.apply {
                it.value.forEach {
                    addPath(it)
                }
            }
        }
    }

    val pathForScale = remember {
        Path().apply {
            pathList.forEach { path ->
                addPath(path)
            }
        }
    }

    var isScaled by remember {
        mutableStateOf(false)
    }

    var touchIndex by remember {
        mutableIntStateOf(-1)
    }

    Column {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1f)
                .background(Blue400)
        ) {


            Canvas(
                modifier = Modifier
                    .pointerInput(Unit) {
                        detectTapGestures {
                            val touchPath = Path().apply {
                                addRect(
                                    Rect(
                                        center = it,
                                        radius = .5f
                                    )
                                )
                            }

                            pathList.forEachIndexed { index, path ->

                                val differencePath =
                                    Path.combine(
                                        operation = PathOperation.Difference,
                                        touchPath,
                                        path
                                    )

                                val isInBounds = differencePath.isEmpty

                                if (isInBounds && touchIndex != index) {
                                    touchIndex = index
                                } else if (isInBounds) {
                                    touchIndex = -1
                                }
                            }

                        }
                    }
                    .fillMaxWidth()
                    .aspectRatio(1f)
                    .clipToBounds()
            ) {

                // Scale map once based on canvas size and if
                // scaled path bounds has top left offset nullify it with matrix
                if (isScaled.not()) {

                    val width = size.width
                    val height = size.height

                    val pathSize = pathForScale.getBounds().size

                    val scaleX = 0.8f * width / pathSize.width
                    val scaleY = 0.8f * height / pathSize.height

                    val scaleMatrix = Matrix().apply {
                        preScale(scaleX, scaleY)
                    }

                    pathForScale.asAndroidPath().transform(scaleMatrix)

                    val pathForScaleBounds = pathForScale.getBounds().size

                    val drawMatrix = Matrix().apply {
                        preScale(scaleX, scaleY)
                        postTranslate(
                            -pathForScale.getBounds().left, -pathForScale.getBounds().top
                        )
                        postTranslate(
                            (width - pathForScaleBounds.width) / 2,
                            (height - pathForScaleBounds.height) / 2
                        )
                    }

                    pathList.forEach { path: Path ->
                        path.asAndroidPath().transform(drawMatrix)

                    }

                    isScaled = true
                }

                pathList.forEachIndexed { index, path: Path ->

                    if (index != touchIndex) {
                        drawPath(path, Color.Black)
                        drawPath(path, color = Color.White, style = Stroke(1.dp.toPx()))
                    }
                }

                // Draw selected path last above others
                pathList.getOrNull(touchIndex)?.let {
                    drawPath(it, Color.Green)
                    drawPath(it, color = Color.White, style = Stroke(1.dp.toPx()))
                }
            }
        }
    }
}

@Preview
@Composable
private fun AnimatedMapSectionPathTouchSample() {

    var isScaled by remember {
        mutableStateOf(false)
    }

    val animatedMapDataList = remember {
        Netherlands.PathMap.entries.map {
            val path = Path()

            path.apply {
                it.value.forEach {
                    addPath(it)
                }
            }

            AnimatedMapData(path = path)
        }
    }

    val pathForScale = remember {
        Path().apply {
            animatedMapDataList.forEach { data ->
                addPath(data.path)
            }
        }
    }

    // This is for animating paths on selection or deselection animations
    animatedMapDataList.forEach {
        LaunchedEffect(key1 = it.isSelected) {
            val targetValue = if (it.isSelected) 1.2f else 1f

            it.animatable.animateTo(targetValue, animationSpec = tween(1000))
        }
    }

    Column {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1f)
                .background(Blue400)
        ) {


            Canvas(
                modifier = Modifier
                    .pointerInput(Unit) {
                        detectTapGestures {

                            val touchPath = Path().apply {
                                addRect(
                                    Rect(
                                        center = it,
                                        radius = .5f
                                    )
                                )
                            }

                            animatedMapDataList.forEachIndexed { index, data ->

                                val path = data.path
                                val differencePath =
                                    Path.combine(
                                        operation = PathOperation.Difference,
                                        touchPath,
                                        path
                                    )

                                val isInBounds = differencePath.isEmpty
                                if (isInBounds) {
                                    data.isSelected = data.isSelected.not()
                                } else {
                                    data.isSelected = false
                                }
                            }

                        }
                    }
                    .fillMaxWidth()
                    .aspectRatio(1f)
                    .clipToBounds()
            ) {

                // Scale map once based on canvas size and if
                // scaled path bounds has top left offset nullify it with matrix
                if (isScaled.not()) {

                    val width = size.width
                    val height = size.height

                    val pathSize = pathForScale.getBounds().size


                    val scaleX = 0.8f * width / pathSize.width
                    val scaleY = 0.8f * height / pathSize.height

                    val scaleMatrix = Matrix().apply {
                        preScale(scaleX, scaleY)
                    }

                    pathForScale.asAndroidPath().transform(scaleMatrix)
                    val pathForScaleBounds = pathForScale.getBounds().size

                    val drawMatrix = Matrix().apply {
                        preScale(scaleX, scaleY)
                        postTranslate(
                            -pathForScale.getBounds().left, -pathForScale.getBounds().top
                        )
                        postTranslate(
                            (width - pathForScaleBounds.width) / 2,
                            (height - pathForScaleBounds.height) / 2
                        )
                    }

                    animatedMapDataList.forEach { data: AnimatedMapData ->
                        data.path.asAndroidPath().transform(drawMatrix)
                    }

                    isScaled = true
                }

                animatedMapDataList.forEach { data ->

                    val path = data.path

                    if (data.isSelected.not()) {
                        withTransform(
                            {
                                val scale = data.animatable.value
                                scale(
                                    scaleX = scale,
                                    scaleY = scale,
                                    // Set scale position as center of path
                                    pivot = data.path.getBounds().center
                                )
                            }
                        ) {
                            drawPath(path, Color.Black)
                            drawPath(path, color = Color.White, style = Stroke(1.dp.toPx()))
                        }
                    }
                }

                // Draw selected path above other paths
                animatedMapDataList.firstOrNull { it.isSelected }?.let { data ->

                    val path = data.path

                    withTransform(
                        {
                            val scale = data.animatable.value
                            scale(
                                scaleX = scale,
                                scaleY = scale,
                                // Set scale position as center of path
                                pivot = data.path.getBounds().center
                            )
                        }
                    ) {
                        drawPath(
                            path = path,
                            color = lerp(
                                start = Color.Black,
                                stop = Orange400,
                                // animate color via linear interpolation
                                fraction = (data.animatable.value - 1f) / 0.2f
                            )
                        )
                        drawPath(path, color = Color.White, style = Stroke(1.dp.toPx()))

                    }
                }
            }
        }
    }
}

@Immutable
data class PathData(
    val path: Path,
    val center: Offset
)

@Stable
internal class AnimatedMapData(
    val path: Path,
    selected: Boolean = false,
    val animatable: Animatable<Float, AnimationVector1D> = Animatable(1f)
) {
    var isSelected by mutableStateOf(selected)
}
