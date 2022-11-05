package com.smarttoolfactory.tutorial1_1basics.chapter6_graphics

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.GenericShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.smarttoolfactory.tutorial1_1basics.ui.backgroundColor
import com.smarttoolfactory.tutorial1_1basics.ui.components.StyleableTutorialText


@Composable
fun Tutorial6_8Screen1() {
    TutorialContent()
}

@Composable
private fun TutorialContent() {

    Column(
        modifier = Modifier.fillMaxSize().background(backgroundColor)
    ) {
        StyleableTutorialText(
            text = "Use **Path.cubicTo** to create a cutout with Bezier curves and " +
                    "**Path.arcTo** to round corners to draw cutout shape",
            bullets = false
        )
        CustomArcShapeSample()
    }
}

@Composable
private fun CustomArcShapeSample() {
    Column(
        modifier = Modifier
            .fillMaxSize()
    ) {

        val content = @Composable {
            Column(
                modifier = Modifier
                    .fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                Spacer(modifier = Modifier.height(20.dp))

                Text(
                    "Payment Failed",
                    color = MaterialTheme.colors.error,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                )
                Spacer(modifier = Modifier.height(10.dp))
                Text("Sorry !", fontSize = 26.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(14.dp))
                Text("Your transfer to bank failed", color = Color.Gray)
            }
        }

        val content2 = @Composable {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .border(1.dp, Color.Green),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                Spacer(modifier = Modifier.height(20.dp))

                Text(
                    "Payment Failed",
                    color = MaterialTheme.colors.error,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                )
                Spacer(modifier = Modifier.height(10.dp))
                Text("Sorry !", fontSize = 26.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(14.dp))
                Text("Your transfer to bank failed", color = Color.Gray)
            }
        }

        CustomArcShape(
            modifier = Modifier
                .padding(10.dp)
                .fillMaxWidth()
                .height(250.dp)
        ) {
            content()
        }

        Spacer(modifier = Modifier.height(40.dp))

        CustomArcShape(
            modifier = Modifier
                .padding(10.dp)
                .fillMaxWidth()
                .height(250.dp)
        ) {
            content2()
        }
    }
}

@Composable
private fun CustomArcShape(
    modifier: Modifier,
    elevation: Dp = 4.dp,
    color: Color = MaterialTheme.colors.surface,
    contentColor: Color = contentColorFor(color),
    content: @Composable () -> Unit
) {

    Column {
        val diameter = 60.dp
        val radiusDp = diameter / 2

        val cornerRadiusDp = 10.dp

        val density = LocalDensity.current
        val cutoutRadius = density.run { radiusDp.toPx() }
        val cornerRadius = density.run { cornerRadiusDp.toPx() }

        val shape = remember {
            GenericShape { size: Size, layoutDirection: LayoutDirection ->
                this.roundedRectanglePath(
                    size = size,
                    cornerRadius = cornerRadius,
                    fabRadius = cutoutRadius * 2
                )
            }
        }

        Spacer(modifier = Modifier.height(diameter / 2))

        Box(contentAlignment = Alignment.TopCenter) {

            Icon(
                modifier = Modifier
                    .offset(y = -diameter / 5)
                    .background(Color(0xffD32F2F), CircleShape)
                    .size(diameter)
                    .drawBehind {
                        drawCircle(
                            Color.Red.copy(.5f),
                            radius = 1.3f * size.width / 2
                        )

                        drawCircle(
                            Color.Red.copy(.3f),
                            radius = 1.5f * size.width / 2
                        )
                    }
                    .align(Alignment.TopCenter)
                    .padding(8.dp),
                tint = Color.White,
                imageVector = Icons.Filled.Close,
                contentDescription = "Close"
            )

            Surface(
                modifier = modifier,
                shape = shape,
                elevation = elevation,
                color = color,
                contentColor = contentColor
            ) {
                Column {
                    Spacer(modifier = Modifier.height(diameter))
                    content()
                }
            }
        }
    }
}

fun Path.roundedRectanglePath(
    size: Size,
    cornerRadius: Float,
    fabRadius: Float,
) {

    val centerX = size.width / 2
    val x0 = centerX - fabRadius * 1.15f
    val y0 = 0f

    // offset of the first control point (top part)
    val topControlX = x0 + fabRadius * .5f
    val topControlY = y0

    // offset of the second control point (bottom part)
    val bottomControlX = x0
    val bottomControlY = y0 + fabRadius

    // first curve
    // set the starting point of the curve (P2)
    val firstCurveStart = Offset(x0, y0)

    // set the end point for the first curve (P3)
    val firstCurveEnd = Offset(centerX, fabRadius * 1f)

    // set the first control point (C1)
    val firstCurveControlPoint1 = Offset(
        x = topControlX,
        y = topControlY
    )

    // set the second control point (C2)
    val firstCurveControlPoint2 = Offset(
        x = bottomControlX,
        y = bottomControlY
    )

    // second curve
    // end of first curve and start of second curve is the same (P3)
    val secondCurveStart = Offset(
        x = firstCurveEnd.x,
        y = firstCurveEnd.y
    )

    // end of the second curve (P4)
    val secondCurveEnd = Offset(
        x = centerX + fabRadius * 1.15f,
        y = 0f
    )

    // set the first control point of second curve (C4)
    val secondCurveControlPoint1 = Offset(
        x = secondCurveStart.x + fabRadius,
        y = bottomControlY
    )

    // set the second control point (C3)
    val secondCurveControlPoint2 = Offset(
        x = secondCurveEnd.x - fabRadius / 2,
        y = topControlY
    )

    // Top left arc
    val radius = cornerRadius * 2

    arcTo(
        rect = Rect(
            left = 0f,
            top = 0f,
            right = radius,
            bottom = radius
        ),
        startAngleDegrees = 180.0f,
        sweepAngleDegrees = 90.0f,
        forceMoveTo = false
    )

    lineTo(x = firstCurveStart.x, y = firstCurveStart.y)

    // bezier curve with (P2, C1, C2, P3)
    cubicTo(
        x1 = firstCurveControlPoint1.x,
        y1 = firstCurveControlPoint1.y,
        x2 = firstCurveControlPoint2.x,
        y2 = firstCurveControlPoint2.y,
        x3 = firstCurveEnd.x,
        y3 = firstCurveEnd.y
    )

    // bezier curve with (P3, C4, C3, P4)
    cubicTo(
        x1 = secondCurveControlPoint1.x,
        y1 = secondCurveControlPoint1.y,
        x2 = secondCurveControlPoint2.x,
        y2 = secondCurveControlPoint2.y,
        x3 = secondCurveEnd.x,
        y3 = secondCurveEnd.y
    )

    lineTo(x = size.width - cornerRadius, y = 0f)

    // Top right arc
    arcTo(
        rect = Rect(
            left = size.width - radius,
            top = 0f,
            right = size.width,
            bottom = radius
        ),
        startAngleDegrees = -90.0f,
        sweepAngleDegrees = 90.0f,
        forceMoveTo = false
    )

    lineTo(x = 0f + size.width, y = size.height - cornerRadius)

    // Bottom right arc
    arcTo(
        rect = Rect(
            left = size.width - radius,
            top = size.height - radius,
            right = size.width,
            bottom = size.height
        ),
        startAngleDegrees = 0f,
        sweepAngleDegrees = 90.0f,
        forceMoveTo = false
    )

    lineTo(x = cornerRadius, y = size.height)

    // Bottom left arc
    arcTo(
        rect = Rect(
            left = 0f,
            top = size.height - radius,
            right = radius,
            bottom = size.height
        ),
        startAngleDegrees = 90.0f,
        sweepAngleDegrees = 90.0f,
        forceMoveTo = false
    )

    lineTo(x = 0f, y = cornerRadius)
    close()
}
