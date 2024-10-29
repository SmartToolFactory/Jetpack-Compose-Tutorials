package com.smarttoolfactory.tutorial1_1basics.chapter6_graphics

import android.graphics.BlurMaskFilter
import android.graphics.PorterDuff
import android.graphics.PorterDuffXfermode
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Slider
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.toRect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.drawOutline
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp


@Preview
@Composable
fun InnerShadowSample() {

    Column(
        modifier = Modifier.fillMaxSize()
            .background(Color(0xff00796B))
            .padding(16.dp)
    ) {

        var blurRadiusWhite by remember {
            mutableFloatStateOf(5f)
        }

        var blurRadiusBlack by remember {
            mutableFloatStateOf(4f)
        }

        Text("Blur white: $blurRadiusWhite")


        Slider(
            value = blurRadiusWhite,
            onValueChange = {
                blurRadiusWhite = it
            },
            valueRange = 1f..20f
        )

        Text("Blur black: $blurRadiusBlack")
        Slider(
            value = blurRadiusBlack,
            onValueChange = {
                blurRadiusBlack = it
            },
            valueRange = 1f..20f
        )


        Row(
            modifier = Modifier
                .innerShadow(
                    shape = RoundedCornerShape(16.dp),
                    color = Color.White.copy(.8f),
                    x = (-2).dp,
                    y = (-2).dp,
                    blurRadius = blurRadiusWhite.dp
                )
                .innerShadow(
                    shape = RoundedCornerShape(16.dp),
                    color = Color.Black,
                    x = 2.dp,
                    y = 2.dp,
                    blurRadius = (blurRadiusBlack).dp
                )
                .background(Color(0xff009688), RoundedCornerShape(16.dp))
                .padding(16.dp)
        ) {
            Text(
                text = "Hello World",
                fontSize = 30.sp,

                )
        }
    }
}

fun Modifier.innerShadow(
    shape: Shape,
    color: Color = Color.Black,
    x: Dp = 2.dp,
    y: Dp = 2.dp,
    blurRadius: Dp = 4.dp,
) = composed {
    val paint = remember {
        Paint()
    }

    println("Paint: $paint")

    drawWithContent {
        drawContent()

        val outline = shape.createOutline(size, layoutDirection, this)

        paint.color = color

        with(drawContext.canvas) {
            saveLayer(size.toRect(), paint)

            drawOutline(outline, paint)

            val frameworkPaint = paint.asFrameworkPaint()

            frameworkPaint.apply {
                xfermode = PorterDuffXfermode(PorterDuff.Mode.DST_OUT)
                maskFilter =
                    BlurMaskFilter(blurRadius.toPx(), BlurMaskFilter.Blur.NORMAL)
            }

            paint.color = Color.Black

            translate(x.toPx(), y.toPx())
            drawOutline(outline, paint)

            frameworkPaint.xfermode = null
            frameworkPaint.maskFilter = null
            restore()
        }
    }
}