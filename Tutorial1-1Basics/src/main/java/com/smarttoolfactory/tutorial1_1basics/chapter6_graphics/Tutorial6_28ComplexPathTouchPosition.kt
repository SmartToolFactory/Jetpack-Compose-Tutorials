package com.smarttoolfactory.tutorial1_1basics.chapter6_graphics

import android.graphics.Matrix
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationVector1D
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.LaunchedEffect
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
    val pathList = remember {
        mutableStateListOf<Path>()
    }

    var touchPosition by remember {
        mutableStateOf(Offset.Zero)
    }


    Canvas(
        modifier = Modifier
            .background(Blue400)
            .fillMaxWidth()
            .aspectRatio(1f)
            .pointerInput(Unit) {
                detectTapGestures {
                    touchPosition = it
                }
            }
    ) {

        if (pathList.isEmpty()) {
            repeat(5) {
                val cx = 170f * (it + 1)
                val cy = 170f * (it + 1)
                val radius = 120f
                val sides = 3 + it
                val path = createPolygonPath(cx, cy, sides, radius)
                pathList.add(path)
            }
        }

        val touchPath = Path().apply {
            addRect(
                Rect(
                    center = touchPosition,
                    radius = .5f
                )
            )
        }

        pathList.forEachIndexed { index, path: Path ->
            val differencePath = Path.combine(operation = PathOperation.Difference, touchPath, path)

            val isTouched = differencePath.isEmpty

            drawPath(
                path,
                color = if (isTouched) Color.Green else Color.Black
            )
        }
    }
}

@Preview
@Composable
private fun MapPathTouchSample() {
    val pathList = Netherlands.PathMap.entries.map {
        it.value
    }

    val pathForScale = remember {
        Path().apply {
            pathList.forEach { paths: List<Path> ->
                paths.forEach { path: Path ->
                    addPath(path)
                }
            }
        }
    }

    var touchPosition by remember {
        mutableStateOf(Offset.Zero)
    }

    var isTouched by remember {
        mutableStateOf(false)
    }

    var bounds by remember {
        mutableStateOf<Rect>(Rect.Zero)
    }

    Column {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1f)
                .border(2.dp, Color.Magenta)
        ) {


            val matrix = Matrix().apply {
                preScale(5f, 5f)
                postTranslate(-140f, 0f)
            }

            pathForScale.asAndroidPath().transform(matrix)

            bounds = pathForScale.getBounds()

            Canvas(
                modifier = Modifier
                    .pointerInput(Unit) {
                        detectTapGestures {
                            touchPosition = it
                        }
                    }
                    .fillMaxWidth()
                    .aspectRatio(1f)
                    .border(3.dp, Color.Green)
                    .clipToBounds()
            ) {

                val touchPath = Path().apply {
                    addRect(
                        Rect(
                            center = touchPosition,
                            radius = .5f
                        )
                    )
                }


                val differencePath =
                    Path.combine(operation = PathOperation.Difference, touchPath, pathForScale)

                isTouched = differencePath.isEmpty

                drawPath(pathForScale, if (isTouched) Color.Green else Color.Black)
            }
        }

        Text("Bounds: $bounds")
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

                val matrix = Matrix().apply {
                    preScale(5f, 5f)
                    postTranslate(-140f, 0f)
                }
                this.asAndroidPath().transform(matrix)
            }
        }
    }

    var touchPosition by remember {
        mutableStateOf(Offset.Zero)
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
                            touchIndex = -1
                            touchPosition = it
                        }
                    }
                    .fillMaxWidth()
                    .aspectRatio(1f)
                    .clipToBounds()
            ) {

                val touchPath = Path().apply {
                    addRect(
                        Rect(
                            center = touchPosition,
                            radius = .5f
                        )
                    )
                }

                pathList.forEachIndexed { index, path: Path ->

                    val differencePath =
                        Path.combine(operation = PathOperation.Difference, touchPath, path)

                    val isTouched = differencePath.isEmpty
                    if (isTouched) {
                        touchIndex = index
                    }

                    if (isTouched.not()) {
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
    val animatedMapDataList = remember {
        Netherlands.PathMap.entries.map {
            val path = Path()

            path.apply {
                it.value.forEach {
                    addPath(it)
                }

                val matrix = Matrix().apply {
                    preScale(5f, 5f)
                    postTranslate(-140f, 0f)
                }
                this.asAndroidPath().transform(matrix)
            }

            AnimatedMapData(path = path)
        }
    }


    animatedMapDataList.forEachIndexed { index, it ->
        LaunchedEffect(key1 = it.isSelected) {
            val targetValue = if (it.isSelected) 1.2f else 1f
            println("Index: $index, selected: ${it.isSelected}")

            it.animatable.animateTo(targetValue, animationSpec = tween(1000))
        }
    }


    var touchPosition by remember {
        mutableStateOf(Offset.Zero)
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
                            touchPosition = it
                        }
                    }
                    .fillMaxWidth()
                    .aspectRatio(1f)
                    .clipToBounds()
            ) {

                val touchPath = Path().apply {
                    addRect(
                        Rect(
                            center = touchPosition,
                            radius = .5f
                        )
                    )
                }

                animatedMapDataList.forEach { data ->

                    val path = data.path

                    val differencePath =
                        Path.combine(operation = PathOperation.Difference, touchPath, path)

                    data.isSelected = differencePath.isEmpty

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
                        drawPath(path, if (data.isSelected) Orange400 else Color.Black)
                        drawPath(path, color = Color.White, style = Stroke(1.dp.toPx()))
                    }
                }
            }
        }
    }
}

@Immutable
internal class AnimatedMapData(
    val path: Path,
    selected: Boolean = false,
    val animatable: Animatable<Float, AnimationVector1D> = Animatable(1f)
) {
    var isSelected by mutableStateOf(selected)
}
