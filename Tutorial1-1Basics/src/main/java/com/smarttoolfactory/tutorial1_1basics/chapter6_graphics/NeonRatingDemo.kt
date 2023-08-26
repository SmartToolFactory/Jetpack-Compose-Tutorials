//package com.smarttoolfactory.tutorial1_1basics.chapter6_graphics
//
//import android.graphics.BlurMaskFilter
//import android.os.Build
//import androidx.annotation.RequiresApi
//import androidx.compose.animation.core.FastOutLinearInEasing
//import androidx.compose.animation.core.FastOutSlowInEasing
//import androidx.compose.animation.core.InfiniteTransition
//import androidx.compose.animation.core.LinearOutSlowInEasing
//import androidx.compose.animation.core.RepeatMode
//import androidx.compose.animation.core.animateFloat
//import androidx.compose.animation.core.infiniteRepeatable
//import androidx.compose.animation.core.keyframes
//import androidx.compose.animation.core.rememberInfiniteTransition
//import androidx.compose.animation.core.tween
//import androidx.compose.foundation.Canvas
//import androidx.compose.foundation.Image
//import androidx.compose.foundation.background
//import androidx.compose.foundation.border
//import androidx.compose.foundation.layout.Box
//import androidx.compose.foundation.layout.fillMaxHeight
//import androidx.compose.foundation.layout.fillMaxSize
//import androidx.compose.foundation.layout.fillMaxWidth
//import androidx.compose.foundation.layout.height
//import androidx.compose.foundation.layout.padding
//import androidx.compose.foundation.layout.size
//import androidx.compose.runtime.Composable
//import androidx.compose.runtime.getValue
//import androidx.compose.runtime.remember
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.geometry.CornerRadius
//import androidx.compose.ui.geometry.Offset
//import androidx.compose.ui.geometry.Size
//import androidx.compose.ui.graphics.Color
//import androidx.compose.ui.graphics.Color.Companion.Green
//import androidx.compose.ui.graphics.ColorFilter
//import androidx.compose.ui.graphics.ImageBitmap
//import androidx.compose.ui.graphics.Paint
//import androidx.compose.ui.graphics.PaintingStyle
//import androidx.compose.ui.graphics.StrokeCap
//import androidx.compose.ui.graphics.asAndroidBitmap
//import androidx.compose.ui.graphics.drawscope.Stroke
//import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
//import androidx.compose.ui.graphics.drawscope.scale
//import androidx.compose.ui.graphics.nativeCanvas
//import androidx.compose.ui.graphics.toArgb
//import androidx.compose.ui.graphics.toComposePaint
//import androidx.compose.ui.layout.ContentScale
//import androidx.compose.ui.res.imageResource
//import androidx.compose.ui.res.painterResource
//import androidx.compose.ui.text.ExperimentalTextApi
//import androidx.compose.ui.text.TextStyle
//import androidx.compose.ui.text.drawText
//import androidx.compose.ui.text.rememberTextMeasurer
//import androidx.compose.ui.tooling.preview.Preview
//import androidx.compose.ui.unit.IntSize
//import androidx.compose.ui.unit.dp
//import com.smarttoolfactory.tutorial1_1basics.R
//
//@OptIn(ExperimentalTextApi::class)
//@RequiresApi(Build.VERSION_CODES.Q)
//@Preview
//@Composable
//private fun Test() {
//
//    val textMeasurer = rememberTextMeasurer()
//
//    val imageBackground = ImageBitmap.imageResource(id = R.drawable.star_foreground)
//
//
//    val transition: InfiniteTransition = rememberInfiniteTransition()
//
//    // Infinite phase animation for PathEffect
//    val phase by transition.animateFloat(
//        initialValue = 0f,
//        targetValue = 1f,
//        animationSpec = infiniteRepeatable(
//            animation = keyframes {
//                durationMillis = 2000
//                0.3f at 0 with LinearOutSlowInEasing // for 0-15 ms
//                1f at 100 with FastOutLinearInEasing // for 15-75 ms
//                0.1f at 250 // ms
//                1f at 500 // ms
//                0.7f at 700 // ms
//                0.4f at 800 // ms
//                1f at 1000
//            },
//            repeatMode = RepeatMode.Reverse
//        ), label = ""
//    )
//
//    val paint = remember {
//        android.graphics.Paint().apply {
//            textSize = 200f
//            color = Color.White.toArgb()
//            isAntiAlias = true
////            maskFilter = BlurMaskFilter(
////                50f, BlurMaskFilter.Blur.INNER
////            )
//
//        }
//    }
//
//
//    val frameworkPaint = remember {
//        paint.toComposePaint().asFrameworkPaint()
//    }
//
//    val color = Color.Red
////
////    val transparent = color
////        .copy(alpha = 0f)
////        .toArgb()
////
////    frameworkPaint.color = transparent
//
//    frameworkPaint.setShadowLayer(
//        50f*phase,
//        0f,
//        0f,
//        color
//            .copy(alpha = phase)
//            .toArgb()
//    )
//
//
//    Box {
////        Image(
////            modifier = Modifier
////                .fillMaxWidth()
////                .height(200.dp),
////            painter = painterResource(id = R.drawable.brick_wall),
////            contentDescription = null,
////            contentScale = ContentScale.FillBounds
////        )
//        Canvas(
//            modifier = Modifier
//                .fillMaxSize()
//             .background(Color.Black)
//        ) {
//
//            with(drawContext.canvas.nativeCanvas) {
////            drawText("Hello", 200f, 200f, paint)
//
////                paint.setShadowLayer(
////                    50f * phase,
////                    0f,
////                    0f,
////                    android.graphics.Color.RED
////
////                )
//
////            drawBitmap(
////                imageBackground.asAndroidBitmap(),
////                0f,
////                0f,
////                paint
////            )
//
////                paint.color = android.graphics.Color.RED
////                drawText("Review", 200f, 200f, paint)
//                paint.toComposePaint().color = Color.White.copy(
//                    alpha = phase.coerceIn(0.5f,1f)
//                )
//                drawText("Review", 200f, 200f, paint)
//
//                drawText(
//                    textMeasurer = textMeasurer,
//                    text = "Review",
//                    topLeft = Offset(200f, 200f),
//                    style = TextStyle(
//                        color = Color.White.copy(alpha = .9f),
//                        fontSize = 200f.toSp()
//                    )
//                )
//
//            }
//
//
////        this.drawIntoCanvas {
////            it.drawRoundRect(
////                left = 600f,
////                top = 100f,
////                right = 1000f,
////                bottom = 500f,
////                radiusX = 5.dp.toPx(),
////                5.dp.toPx(),
////                paint = paint
////            )
////
////            drawRoundRect(
////                Color.White,
////                topLeft = Offset(600f, 100f),
////                size = Size(400f, 400f),
////                cornerRadius = CornerRadius(5.dp.toPx(), 5.dp.toPx()),
////                style = Stroke(width = 2.dp.toPx())
////            )
////
////            it.drawRoundRect(
////                left = 100f,
////                top = 100f,
////                right = 500f,
////                bottom = 500f,
////                radiusX = 5.dp.toPx(),
////                5.dp.toPx(),
////                paint = paint
////            )
////        }
//        }
//    }
//}