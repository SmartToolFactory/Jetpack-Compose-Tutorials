package com.smarttoolfactory.tutorial1_1basics.chapter6_graphics

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.GenericShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.CompositingStrategy
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathOperation
import androidx.compose.ui.graphics.drawOutline
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toAndroidRectF
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.smarttoolfactory.tutorial1_1basics.R

@Preview
@Composable
private fun BlendModeTest1() {

    Column(
        modifier = Modifier
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        val triangleShape = remember {
            GenericShape { size: Size, layoutDirection: LayoutDirection ->
                moveTo(0f, 0f)
                lineTo(size.width / 2, size.height)
                lineTo(size.width, 0f)
                lineTo(0f, 0f)

            }
        }

        val density = LocalDensity.current
        val color = Color.Black.copy(alpha = .4f)
        val offsetDp = 40.dp


        Box(
            modifier = Modifier
                .padding(bottom = offsetDp / 2)


                .drawWithCache {

                    val outline = triangleShape.createOutline(
                        size = Size(40.dp.toPx(), 40.dp.toPx()),
                        layoutDirection = layoutDirection,
                        density = density
                    )

                    val outline2 = triangleShape.createOutline(
                        size = Size(40.dp.toPx() + 2, 40.dp.toPx() + 2),
                        layoutDirection = layoutDirection,
                        density = density
                    )


                    val offset = offsetDp.toPx()

                    onDrawWithContent {

                        with(drawContext.canvas.nativeCanvas) {
                            val checkPoint = saveLayer(null, null)

                            // Destination
                            drawContent()

                            // Source
                            translate(
                                left = (size.width - outline.bounds.size.width) / 2,
                                top = size.height - offset / 2
                            ) {
                                drawOutline(
                                    outline = outline,
                                    color = color.copy(alpha = 1f),
                                    blendMode = BlendMode.DstAtop
                                )
                            }

                            // Draw this behind destination
                            translate(
                                left = (size.width - outline2.bounds.size.width) / 2,
                                top = size.height - offset / 2
                            ) {
                                drawOutline(
                                    outline = outline2,
                                    color = color,
                                    blendMode = BlendMode.DstAtop
                                )
                            }
                            restoreToCount(checkPoint)
                        }
                    }
                }
        ) {
            Box(
                modifier = Modifier.size(100.dp).background(color, RoundedCornerShape(45)),
                contentAlignment = Alignment.Center
            ) {
                Text("Bubble Content", color = Color.White)
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.Q)
@Preview
@Composable
private fun BlendModeSample2() {

    var selectedIndex by remember { mutableStateOf(0) }
    var blendMode: BlendMode by remember { mutableStateOf(BlendMode.Clear) }

    val paint = remember {
        Paint().apply {
            color = Color.Blue
        }
    }
    Column {
        Image(
            modifier = Modifier
                .size(135.dp)
                .graphicsLayer {
                    compositingStrategy = CompositingStrategy.Offscreen
                }
                .drawWithContent {

                    val radius = size.width - 28.dp.toPx()


                    val rect = Rect(
                        offset = Offset.Zero,
                        size = size
                    )
                    drawContent()

                    with(drawContext.canvas.nativeCanvas) {

                        drawRect(rect.toAndroidRectF(), paint.asFrameworkPaint())
                        paint.blendMode = BlendMode.SrcOut
                        paint.color = Color.Red

                        drawCircle(
                            radius, radius, radius, paint.asFrameworkPaint()
                        )

                        paint.blendMode = BlendMode.SrcOut
                        paint.color = Color.Green
                        drawRect(rect.toAndroidRectF(), paint.asFrameworkPaint())
                    }
                },
            painter = painterResource(R.drawable.landscape2),
            contentScale = ContentScale.Crop,
            contentDescription = null
        )

        androidx.compose.material.Text(
            text = "Src BlendMode: $blendMode",
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(8.dp)
        )

        BlendModeSelection(
            modifier = Modifier
                .height(200.dp)
                .verticalScroll(rememberScrollState()),
            selectedIndex = selectedIndex,
            onBlendModeSelected = { index, mode ->
                blendMode = mode
                selectedIndex = index
            }
        )
    }
}

@Preview
@Composable
private fun BlendModeSample3() {
    Column(
        modifier = Modifier.padding(20.dp)
    ) {
        Image(
            modifier = Modifier
                .size(135.dp)
                .graphicsLayer {
                    compositingStrategy = CompositingStrategy.Offscreen
                }
                .drawWithCache {

                    val radius = size.width - 28.dp.toPx()
                    val path = Path()
                    val pathCircle = Path()

                    if (path.isEmpty) {
                        path.addRect(
                            Rect(
                                offset = Offset.Zero,
                                size = size
                            )
                        )

                    }

                    if (pathCircle.isEmpty) {
                        pathCircle.addOval(
                            Rect(
                                center = Offset(
                                    radius, radius
                                ),
                                radius = radius
                            )
                        )
                    }

                    path.op(path, pathCircle, PathOperation.Difference)

                    onDrawWithContent {
                        drawContent()
                        drawPath(path, Color.Red, blendMode = BlendMode.DstOut)

                    }
                },
            painter = painterResource(R.drawable.landscape2),
            contentScale = ContentScale.Crop,
            contentDescription = null
        )
    }
}

@Preview
@Composable
private fun BlendModeSample4() {
    Column(
        modifier = Modifier.padding(20.dp)
    ) {

        val image = ImageBitmap.imageResource(R.drawable.landscape1)

        Canvas(
            modifier = Modifier
                .size(135.dp)
                .graphicsLayer {
                    compositingStrategy = CompositingStrategy.Offscreen
                }
        ) {
            val radius = size.width - 28.dp.toPx()

            drawCircle(
                color = Color.Red,
                radius = radius,
                center = Offset(radius, radius),
            )

            drawImage(
                image,
                dstSize = IntSize(size.width.toInt(), size.height.toInt()),
                blendMode = BlendMode.SrcOut
            )
        }
    }
}
