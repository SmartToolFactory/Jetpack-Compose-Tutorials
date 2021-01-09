package com.smarttoolfactory.tutorial1_1basics.chapter2_material_widgets

import androidx.compose.foundation.Image
import androidx.compose.foundation.ScrollableColumn
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import com.smarttoolfactory.tutorial1_1basics.R
import com.smarttoolfactory.tutorial1_1basics.components.*

@Composable
fun Tutorial2_4Screen() {
    TutorialContent()
}

@Composable
private fun TutorialContent() {

    ScrollableColumn(modifier = Modifier.fillMaxSize()) {

        TutorialHeader(text = "Image")

        TutorialText(
            text = "1-) Image component lays out and draws a given  ImageBitmap, ImageVector," +
                    "or Painter."
        )

        TutorialText2(text = "ImageBitmap")
        val imageRes: ImageBitmap = imageResource(id = R.drawable.landscape1)
        Image(imageRes)

        ImageVectorExample()
        ImagePainterExample()

        TutorialText(
            text = "2-) ContentScale represents a rule to apply to scale a source " +
                    "rectangle to be inscribed into a destination."
        )
        ImageContentScaleExample()
    }
}

@Composable
private fun ImageContentScaleExample() {

    val imageModifier = Modifier
        .fillMaxWidth()
        .aspectRatio(4 / 3f)
        .background(Color.LightGray)


    val imageModifier2 = Modifier
        .fillMaxHeight()
        .aspectRatio(1f)
        .background(Color.LightGray)

    FullWidthColumn {

        val bitMap = imageResource(id = R.drawable.landscape10)

        TutorialText2(text = "Original")

        Image(
            bitmap = bitMap
        )

        TutorialText2(text = "ContentScale.None")

        Image(
            modifier = imageModifier,
            bitmap = bitMap,
            contentScale = ContentScale.None
        )

        TutorialText2(text = "ContentScale.Crop")

        Image(
            modifier = imageModifier,
            bitmap = bitMap,
            contentScale = ContentScale.Crop,
        )

        TutorialText2(text = "ContentScale.FillBounds")

        Image(
            modifier = imageModifier,
            bitmap = bitMap,
            contentScale = ContentScale.FillBounds
        )

        TutorialText2(text = "ContentScale.FillHeight")

        Image(
            modifier = imageModifier,
            bitmap = bitMap,
            contentScale = ContentScale.FillHeight
        )

        TutorialText2(text = "ContentScale.FillWidth")

        Image(
            modifier = imageModifier,
            bitmap = bitMap,
            contentScale = ContentScale.FillWidth
        )

        TutorialText2(text = "ContentScale.Fit")

        Image(
            modifier = imageModifier,
            bitmap = bitMap,
            contentScale = ContentScale.Fit
        )

        TutorialText2(text = "ContentScale.Inside")

        Image(
            modifier = imageModifier,
            bitmap = bitMap,
            contentScale = ContentScale.Inside
        )


        val bitmap2 = imageResource(id = R.drawable.landscape5)

        TutorialText2(text = "Original")

        Image(
            bitmap = bitmap2
        )

        TutorialText2(text = "ContentScale.None")

        Image(
            modifier = imageModifier2,
            bitmap = bitmap2,
            contentScale = ContentScale.None
        )

        TutorialText2(text = "ContentScale.Crop")

        Image(
            modifier = imageModifier2,
            bitmap = bitmap2,
            contentScale = ContentScale.Crop,
        )

        TutorialText2(text = "ContentScale.FillBounds")

        Image(
            modifier = imageModifier2,
            bitmap = bitmap2,
            contentScale = ContentScale.FillBounds
        )

        TutorialText2(text = "ContentScale.FillHeight")

        Image(
            modifier = imageModifier2,
            bitmap = bitmap2,
            contentScale = ContentScale.FillHeight
        )

        TutorialText2(text = "ContentScale.FillWidth")

        Image(
            modifier = imageModifier2,
            bitmap = bitmap2,
            contentScale = ContentScale.FillWidth
        )

        TutorialText2(text = "ContentScale.Fit")

        Image(
            modifier = imageModifier2,
            bitmap = bitmap2,
            contentScale = ContentScale.Fit
        )

        TutorialText2(text = "ContentScale.Inside")

        Image(
            modifier = imageModifier2,
            bitmap = bitmap2,
            contentScale = ContentScale.Inside
        )
    }
}

@Composable
fun ImagePainterExample() {

    TutorialText2(text = "Painter")

    val customPainter = remember {
        object : Painter() {

            override val intrinsicSize: Size
                get() = Size(100.0f, 100.0f)

            override fun DrawScope.onDraw() {
                drawRect(color = Color(0xfff44336))
            }
        }
    }
    Image(painter = customPainter, modifier = Modifier.preferredSize(100.dp, 100.dp))
}

@Composable
private fun ImageVectorExample() {
    TutorialText2(text = "ImageVector")
    FullWidthRow(
        modifier = Modifier
            .background(Color.LightGray)
            .padding(top = 16.dp, bottom = 16.dp),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        val vectorRes1: ImageVector = vectorResource(id = R.drawable.vd_clock_alarm)
        Image(vectorRes1, modifier = Modifier.preferredSize(60.dp))

        val vectorRes2: ImageVector = vectorResource(id = R.drawable.vd_dashboard_active)
        Image(vectorRes2, modifier = Modifier.preferredSize(60.dp))

        val vectorRes3: ImageVector = vectorResource(id = R.drawable.vd_home_active)
        Image(vectorRes3, modifier = Modifier.preferredSize(60.dp))

        val vectorRes4: ImageVector = vectorResource(id = R.drawable.vd_notification_active)
        Image(vectorRes4, modifier = Modifier.preferredSize(60.dp))
    }
}