package com.smarttoolfactory.tutorial1_1basics.chapter6_graphics

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CutCornerShape
import androidx.compose.foundation.shape.GenericShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.CompositingStrategy
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathOperation
import androidx.compose.ui.graphics.addOutline
import androidx.compose.ui.graphics.drawOutline
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp

@Preview
@Composable
fun ShapeCombineTest() {

    val density = LocalDensity.current

    val shape = remember {
        GenericShape { size: Size, layoutDirection: LayoutDirection ->
            val cutoutOutline =
                CutCornerShape(bottomStart = 16.dp, topEnd = 16.dp).createOutline(
                    size,
                    layoutDirection,
                    density
                )

            val roundedCornerOutline =
                RoundedCornerShape(topStart = 16.dp, bottomEnd = 16.dp).createOutline(
                    size,
                    layoutDirection,
                    density
                )

            val path1 = Path().apply {
                addOutline(cutoutOutline)
            }

            val path2 = Path().apply {
                addOutline(roundedCornerOutline)
            }

            addPath(
                Path.combine(
                    operation = PathOperation.Intersect,
                    path1 = path1,
                    path2 = path2
                )
            )
        }
    }

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp)
    ) {

        Box(
            modifier = Modifier
                .size(200.dp, 100.dp)
                .shadow(8.dp, shape)
                .border(2.dp, Color.Green, shape)
                .background(Color.Red, shape)
        )

    }
}

@Composable
private fun ShapeFusionBox() {


    val density = LocalDensity.current

    Box(
        modifier = Modifier
            .size(200.dp, 100.dp)
            .graphicsLayer {
                compositingStrategy = CompositingStrategy.Offscreen
            }
            .drawWithCache {
                val cutoutOutline =
                    CutCornerShape(bottomStart = 16.dp, topEnd = 16.dp).createOutline(
                        size,
                        layoutDirection,
                        density
                    )

                val roundedCornerOutline =
                    RoundedCornerShape(topStart = 16.dp, bottomEnd = 16.dp).createOutline(
                        size,
                        layoutDirection,
                        density
                    )

                onDrawBehind {

                    // You can draw with painter or ImageBitmap here
                    drawOutline(
                        outline = roundedCornerOutline,
                        color = Color.Blue
                    )

                    // You can draw with painter or ImageBitmap here
                    drawOutline(
                        outline = cutoutOutline,
                        color = Color.Blue,
                        blendMode = BlendMode.Xor
                    )

                    drawRect(
                        color = Color.Red,
                        blendMode = BlendMode.SrcOut
                    )
                }
            }
    )
}
