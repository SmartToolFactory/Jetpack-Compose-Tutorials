package com.smarttoolfactory.tutorial1_1basics.chapter6_graphics

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CutCornerShape
import androidx.compose.foundation.shape.GenericShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithCache
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
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp

@Preview
@Composable
fun ShapeCombineTest() {

    val density = LocalDensity.current


    Column(
        modifier = Modifier.fillMaxSize()
            .padding(16.dp)
    ) {

        OutlinedCard(
            shape = FusedShape(density),

            ) {
            Box(
                modifier = Modifier.size(200.dp, 100.dp),
                contentAlignment = Alignment.Center
            ) {
                Text("M3 Card")
            }
        }

        Spacer(Modifier.height(16.dp))

        ElevatedCard(
            shape = FusedShape(
                density = density,
                topStart = CornerShape.CutCorner(16.dp),
                topEnd = CornerShape.CutCorner(16.dp),
                bottomStart = CornerShape.RoundedCorner(16.dp),
                bottomEnd = CornerShape.RoundedCorner(16.dp)
            )
        ) {
            Box(
                modifier = Modifier.size(200.dp, 100.dp),
                contentAlignment = Alignment.Center
            ) {
                Text("M3 Card")
            }
        }

        Spacer(Modifier.height(16.dp))

        ElevatedCard(
            shape = FusedShape(
                density = density,
                topStart = CornerShape.RoundedCorner(16.dp),
                topEnd = CornerShape.CutCorner(16.dp),
                bottomStart = CornerShape.RoundedCorner(16.dp),
                bottomEnd = CornerShape.CutCorner(16.dp)
            ),
        ) {
            Box(
                modifier = Modifier.size(200.dp, 100.dp),
                contentAlignment = Alignment.Center
            ) {
                Text("M3 Card")
            }
        }

        Spacer(Modifier.height(16.dp))

        Button(
            modifier = Modifier.fillMaxWidth(),
            shape = FusedShape(density),
            onClick = {

            }
        ) {
            Text("M3 Button")
        }
    }
}

sealed class CornerShape(val radius: Dp) {
    class CutCorner(cornerRadius: Dp) : CornerShape(cornerRadius)
    class RoundedCorner(cornerRadius: Dp) : CornerShape(cornerRadius)
}

private fun FusedShape(
    density: Density,
    topStart: CornerShape = CornerShape.RoundedCorner(16.dp),
    topEnd: CornerShape = CornerShape.CutCorner(16.dp),
    bottomStart: CornerShape = CornerShape.CutCorner(16.dp),
    bottomEnd: CornerShape = CornerShape.RoundedCorner(16.dp),
) =
    GenericShape { size: Size, layoutDirection: LayoutDirection ->

        val cutCornerTopStart =
            if (topStart is CornerShape.CutCorner) topStart.radius else 0.dp
        val cutCornerTopEnd =
            if (topEnd is CornerShape.CutCorner) topEnd.radius else 0.dp
        val cutCornerBottomStart =
            if (bottomStart is CornerShape.CutCorner) bottomStart.radius else 0.dp
        val cutCornerBottomEnd =
            if (bottomEnd is CornerShape.CutCorner) bottomEnd.radius else 0.dp

        val roundedCornerTopStart =
            if (topStart is CornerShape.RoundedCorner) topStart.radius else 0.dp
        val roundedCornerTopEnd =
            if (topEnd is CornerShape.RoundedCorner) topEnd.radius else 0.dp
        val roundedCornerBottomStart =
            if (bottomStart is CornerShape.RoundedCorner) bottomStart.radius else 0.dp
        val roundedCornerBottomEnd =
            if (bottomEnd is CornerShape.RoundedCorner) bottomEnd.radius else 0.dp

        val cutoutOutline =
            CutCornerShape(
                topStart = cutCornerTopStart,
                topEnd = cutCornerTopEnd,
                bottomStart = cutCornerBottomStart,
                bottomEnd = cutCornerBottomEnd
            )
                .createOutline(
                    size,
                    layoutDirection,
                    density
                )

        val roundedCornerOutline =
            RoundedCornerShape(
                topStart = roundedCornerTopStart,
                topEnd = roundedCornerTopEnd,
                bottomStart = roundedCornerBottomStart,
                bottomEnd = roundedCornerBottomEnd
            ).createOutline(
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
