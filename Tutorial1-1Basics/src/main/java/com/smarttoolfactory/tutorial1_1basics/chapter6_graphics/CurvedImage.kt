package com.smarttoolfactory.tutorial1_1basics.chapter6_graphics

import android.graphics.Bitmap
import android.graphics.Paint
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Slider
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.smarttoolfactory.tutorial1_1basics.R
import kotlin.math.pow
import kotlin.math.sqrt
import androidx.core.graphics.createBitmap


@Composable
fun SpherizeImageMesh(
    source: ImageBitmap,
    curveFactor: Float = 0.7f,
    topBottomRatio: Float = 1f,
    steps: Int = 48,
    modifier: Modifier = Modifier
) {
    val bmp = remember(source) { source.asAndroidBitmap().copy(Bitmap.Config.ARGB_8888, true) }

    Canvas(modifier = modifier) {


        drawIntoCanvas { canvas ->
            val fw = size.width
            val fh = size.height

            // Scale source to canvas size (you can change this behavior)
            val scaled = createBitmap(fw.toInt(), fh.toInt())


            val scaledVertices = computeVerticesFromEllipse(
                bmp.getWidth(),
                bmp.getHeight(),
                steps,
                curveFactor,
                topBottomRatio
            )
            val columns =
                (scaledVertices.size / 4) - 1 // divide by 2 since for two points, divide by 2 again for two rows

            // Android Canvas drawBitmapMesh
            val paint = Paint(Paint.FILTER_BITMAP_FLAG)
            android.graphics.Canvas(scaled)
                .drawBitmapMesh(
                    bmp,
                    columns,
                    1,
                    scaledVertices,
                    0,
                    null,
                    0,
                    paint
                )

            drawImage(
                image = scaled.asImageBitmap()
            )
        }
    }
}

@Preview
@Composable
fun DemoSpherizeMesh() {
    Column {
        val imageBitmap = ImageBitmap.imageResource(R.drawable.landscape2)

        var curveFactor by remember {
            mutableStateOf(0.7f)
        }

        var topBottomRatio by remember {
            mutableStateOf(0.7f)
        }


        var step by remember {
            mutableStateOf(48f)
        }

        Text("Curve Factor: $curveFactor")
        Slider(
            value = curveFactor,
            onValueChange = {
                curveFactor = it
            }
        )

        Text("Top Bottom Ratio: $topBottomRatio")

        Slider(
            value = topBottomRatio,
            onValueChange = {
                topBottomRatio = it
            }
        )

        Text("Step: $step")

        Slider(
            value = step,
            valueRange = 12f..100f,
            onValueChange = {
                step = it
            }
        )

        Image(
            painter = painterResource(R.drawable.landscape2),
            contentDescription = null
        )

        SpherizeImageMesh(
            source = imageBitmap,
            curveFactor = curveFactor,
            topBottomRatio = topBottomRatio,
            steps = step.toInt(),
            modifier = Modifier.fillMaxSize().border(2.dp, Color.Red)
        )
    }
}

private fun computeVerticesFromEllipse(
    width: Int,
    height: Int,
    steps: Int,
    curveFactor: Float,
    topBottomRatio: Float
): FloatArray {
    var p = width / 2.0
    var q = 0.0
    var a = width / 2.0
    var b = curveFactor * a

    val vertices = FloatArray((steps - 1) * 4)
    var j = 0

    var increment = width / steps.toDouble()

    run {
        var i = 1
        while (i < steps) {
            vertices[j] = (increment * i.toDouble()).toFloat()
            vertices[j + 1] =
                -(-sqrt((1 - ((increment * i.toDouble()) - p).pow(2.0) / a.pow(2.0)) * b.pow(2.0)) + q).toFloat()
            i++
            j += 2
        }
    }

    val width2 = (topBottomRatio * width).toDouble()
    p = width2 / 2.0
    q = (width - width2) / 2.0
    a = width2 / 2.0
    b = curveFactor * a
    increment = width2 / steps.toDouble()

    val shift = width * (1.0 - topBottomRatio) / 2.0

    var i = 1
    while (i < steps) {
        vertices[j] = (increment * i.toDouble()).toFloat() + shift.toFloat()
        vertices[j + 1] =
            -(-sqrt((1 - ((increment * i.toDouble()) - p).pow(2.0) / a.pow(2.0)) * b.pow(2.0)) + q).toFloat() + height
        i++
        j = j + 2
    }

    return vertices
}
