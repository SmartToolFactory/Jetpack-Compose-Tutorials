package com.smarttoolfactory.tutorial1_1basics.chapter2_material_widgets

import android.graphics.Bitmap
import androidx.compose.foundation.Image
import androidx.compose.foundation.ScrollableColumn
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.CutCornerShape
import androidx.compose.foundation.shape.GenericShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.*
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.AmbientContext
import androidx.compose.ui.res.DeferredResource
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.res.loadImageResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import androidx.core.graphics.drawable.toBitmap
import coil.imageLoader
import coil.request.ImageRequest
import com.smarttoolfactory.tutorial1_1basics.R
import com.smarttoolfactory.tutorial1_1basics.components.*

// TODO Add PorterDuff blend modes and Coil Image loading
@Composable
fun Tutorial2_4Screen() {
    val imageBitmapSrc: ImageBitmap = imageResource(id = R.drawable.composite_src)
    val imageBitmapDst: ImageBitmap = imageResource(id = R.drawable.composite_dst)

//    androidx.compose.foundation.Canvas(modifier = Modifier.size(500.dp),
//        onDraw = {
//
//            drawImage(imageBitmapDst)
//            drawImage(imageBitmapSrc, blendMode = BlendMode.SrcOut)
//
//        })

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

        BasicImageExample()
        ImageVectorExample()
        ImagePainterExample()

        TutorialText(
            text = "2-) With Canvas we can draw on a ImageBitmap and set ImageBitmap to an Image."
        )
        DrawOnImageExample()

        TutorialText(
            text = "3-) Set shape or/and filter for the Image."
        )
        ImageShapeAndFilterExample()

//        TutorialText(
//            text = "4-) Use Coil to fetch an image resource from network and " +
//                    "set it to Image component."
//        )
//
//        ImageDownloadExample()

        TutorialText(
            text = "4-) ContentScale represents a rule to apply to scale a source " +
                    "rectangle to be inscribed into a destination."
        )
        ImageContentScaleExample()
    }
}

@Composable
fun ImageDownloadExample() {
    val url =
        "https://avatars3.githubusercontent.com/u/35650605?s=400&u=058086fd5c263f50f2fbe98ed24b5fbb7d437a4e&v=4"

    var image by remember { mutableStateOf<ImageBitmap?>(null) }

    val context = AmbientContext.current

    val request = ImageRequest.Builder(context)
        .data("https://www.example.com/image.jpg")
        .target { drawable ->
            // Handle the result.

            val imageBitmap =
                drawable.toBitmap(drawable.intrinsicWidth, drawable.intrinsicHeight).asImageBitmap()
            Image(imageBitmap, contentDescription = null)
        }
        .build()

    context.imageLoader.enqueue(request)

}


@Composable
private fun BasicImageExample() {
    TutorialText2(text = "ImageBitmap")
    val imageRes: ImageBitmap = imageResource(id = R.drawable.landscape1)
    Image(imageRes, contentDescription = null)
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
        Image(vectorRes1, modifier = Modifier.preferredSize(60.dp), contentDescription = null)

        val vectorRes2: ImageVector = vectorResource(id = R.drawable.vd_dashboard_active)
        Image(vectorRes2, modifier = Modifier.preferredSize(60.dp), contentDescription = null)

        val vectorRes3: ImageVector = vectorResource(id = R.drawable.vd_home_active)
        Image(vectorRes3, modifier = Modifier.preferredSize(60.dp), contentDescription = null)

        val vectorRes4: ImageVector = vectorResource(id = R.drawable.vd_notification_active)
        Image(vectorRes4, modifier = Modifier.preferredSize(60.dp), contentDescription = null)
    }
}

@Composable
fun ImagePainterExample() {

    TutorialText2(text = "Painter")

    val imageBitmap: ImageBitmap = imageResource(id = R.drawable.landscape3)

    val customPainter = remember {
        object : Painter() {

            override val intrinsicSize: Size
                get() = Size(imageBitmap.width.toFloat(), imageBitmap.height.toFloat())

            override fun DrawScope.onDraw() {
                drawImage(imageBitmap)
                drawLine(
                    color = Color.Red,
                    start = Offset(0f, 0f),
                    end = Offset(imageBitmap.width.toFloat(), imageBitmap.height.toFloat()),
                    strokeWidth = 5f
                )
            }
        }
    }
    Image(painter = customPainter, contentDescription = null)
}

@Composable
private fun DrawOnImageExample() {
    /*
            Load the image in background thread.
            Until resource loading complete, this function returns deferred image resource
            with PendingResource. Once the loading finishes, recompose is scheduled and this
             function will return deferred image resource with LoadedResource or FailedResource.
         */
    val deferredResource: DeferredResource<ImageBitmap> =
        loadImageResource(id = R.drawable.landscape2)

    deferredResource.resource.resource?.let { imageBitmap ->

        // We need a MUTABLE Bitmap to draw on Canvas to not get IllegalArgumentException
        val bitmap = imageBitmap.asAndroidBitmap().copy(Bitmap.Config.ARGB_8888, true)

        val paint = Paint().apply {
            style = PaintingStyle.Stroke
            strokeWidth = 10f
            color = Color(0xff29B6F6)

        }

        // We need a ImageBitmap for Jetpack Compose Canvas
        val newImageBitmap = bitmap.asImageBitmap()

        val canvas = Canvas(newImageBitmap)

        canvas.drawRect(0f, 0f, 200f, 200f, paint = paint)
        canvas.drawCircle(
            Offset(
                newImageBitmap.width / 2 - 75f,
                newImageBitmap.height / 2 + 75f
            ), 150.0f, paint
        )

        Image(bitmap = newImageBitmap, contentDescription = null)
    }
}

@Composable
private fun ImageShapeAndFilterExample() {

    val avatarBitmap1 = imageResource(id = R.drawable.avatar_1_raster)
    val avatarBitmap2 = imageResource(id = R.drawable.avatar_2_raster)
    val avatarBitmap3 = imageResource(id = R.drawable.avatar_3_raster)
    val avatarBitmap4 = imageResource(id = R.drawable.avatar_4_raster)

    TutorialText2(text = "Shape")

    FullWidthRow(
        modifier = Modifier.preferredHeight(100.dp),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {

        Image(
            modifier = Modifier
                .clip(RoundedCornerShape(10.dp)),
            bitmap = avatarBitmap1,
            contentDescription = null
        )
        Image(
            modifier = Modifier
                .shadow(4.dp)
                .clip(CircleShape), bitmap = avatarBitmap2,
            contentDescription = null
        )
        Image(
            modifier = Modifier
                .shadow(5.dp, CutCornerShape(10.dp))
                .clip(CutCornerShape(10.dp)),
            bitmap = avatarBitmap3,
            contentDescription = null
        )

        // 🔥 Adding clip = true flag adds shadow and clips to shape
        Image(
            modifier = Modifier
                .shadow(2.dp, diamondShape, clip = true),
            bitmap = avatarBitmap4,
            contentDescription = null
        )
    }

    TutorialText2(text = "Color Filter")
    FullWidthRow(
        modifier = Modifier.preferredHeight(100.dp),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {


        Image(
            bitmap = avatarBitmap1,
            colorFilter = ColorFilter(color = Color.Green, blendMode = BlendMode.Darken),
            contentDescription = null
        )

        Image(
            bitmap = avatarBitmap1,
            colorFilter = ColorFilter(color = Color.Green, blendMode = BlendMode.Lighten),
            contentDescription = null
        )

        Image(
            bitmap = avatarBitmap1,
            colorFilter = ColorFilter(color = Color.Green, blendMode = BlendMode.DstOver),
            contentDescription = null
        )

        Image(
            bitmap = avatarBitmap1,
            colorFilter = ColorFilter(color = Color.Green, blendMode = BlendMode.Color),
            contentDescription = null
        )
    }

    // TODO PorterDuff mode is not working properly, or i couldn't figure it out yet
    // Check out https://stackoverflow.com/questions/65653560/jetpack-compose-applying-porterduffmode-to-image
//    val imageBitmapSrc: ImageBitmap = imageResource(id = R.drawable.composite_src)
//    val imageBitmapDst: ImageBitmap = imageResource(id = R.drawable.composite_dst)
//
//    val size = Size(imageBitmapSrc.width.toFloat(), imageBitmapSrc.height.toFloat())
//
//    val blendPainter = remember {
//        object : Painter() {
//
//            override val intrinsicSize: Size
//                get() = size
//
//            override fun DrawScope.onDraw() {
////                drawRect(Color(0xffBDBDBD), size = size)
//                drawImage(imageBitmapDst)
//                drawImage(imageBitmapSrc, blendMode = BlendMode.SrcOut)
//            }
//        }
//    }
//
//    Image(blendPainter)

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

        val imageBitmap: ImageBitmap = imageResource(id = R.drawable.landscape10)

        TutorialText2(text = "Original")

        Image(bitmap = imageBitmap, contentDescription = null)

        TutorialText2(text = "ContentScale.None")

        Image(
            modifier = imageModifier,
            bitmap = imageBitmap,
            contentScale = ContentScale.None,
            contentDescription = null
        )

        TutorialText2(text = "ContentScale.Crop")

        Image(
            modifier = imageModifier,
            bitmap = imageBitmap,
            contentScale = ContentScale.Crop,
            contentDescription = null
        )

        TutorialText2(text = "ContentScale.FillBounds")

        Image(
            modifier = imageModifier,
            bitmap = imageBitmap,
            contentScale = ContentScale.FillBounds,
            contentDescription = null
        )

        TutorialText2(text = "ContentScale.FillHeight")

        Image(
            modifier = imageModifier,
            bitmap = imageBitmap,
            contentScale = ContentScale.FillHeight,
            contentDescription = null
        )

        TutorialText2(text = "ContentScale.FillWidth")

        Image(
            modifier = imageModifier,
            bitmap = imageBitmap,
            contentScale = ContentScale.FillWidth,
            contentDescription = null
        )

        TutorialText2(text = "ContentScale.Fit")

        Image(
            modifier = imageModifier,
            bitmap = imageBitmap,
            contentScale = ContentScale.Fit,
            contentDescription = null
        )

        TutorialText2(text = "ContentScale.Inside")

        Image(
            modifier = imageModifier,
            bitmap = imageBitmap,
            contentScale = ContentScale.Inside,
            contentDescription = null
        )


        val bitmap2 = imageResource(id = R.drawable.landscape5)

        TutorialText2(text = "Original")

        Image(
            bitmap = bitmap2, contentDescription = null
        )

        TutorialText2(text = "ContentScale.None")

        Image(
            modifier = imageModifier2,
            bitmap = bitmap2,
            contentScale = ContentScale.None,
            contentDescription = null
        )

        TutorialText2(text = "ContentScale.Crop")

        Image(
            modifier = imageModifier2,
            bitmap = bitmap2,
            contentScale = ContentScale.Crop,
            contentDescription = null
        )

        TutorialText2(text = "ContentScale.FillBounds")

        Image(
            modifier = imageModifier2,
            bitmap = bitmap2,
            contentScale = ContentScale.FillBounds,
            contentDescription = null
        )

        TutorialText2(text = "ContentScale.FillHeight")

        Image(
            modifier = imageModifier2,
            bitmap = bitmap2,
            contentScale = ContentScale.FillHeight,
            contentDescription = null
        )

        TutorialText2(text = "ContentScale.FillWidth")

        Image(
            modifier = imageModifier2,
            bitmap = bitmap2,
            contentScale = ContentScale.FillWidth,
            contentDescription = null
        )

        TutorialText2(text = "ContentScale.Fit")

        Image(
            modifier = imageModifier2,
            bitmap = bitmap2,
            contentScale = ContentScale.Fit,
            contentDescription = null
        )

        TutorialText2(text = "ContentScale.Inside")

        Image(
            modifier = imageModifier2,
            bitmap = bitmap2,
            contentScale = ContentScale.Inside,
            contentDescription = null
        )
    }
}

private val diamondShape = GenericShape { size ->
    moveTo(size.width / 2f, 0f)
    lineTo(size.width, size.height / 2f)
    lineTo(size.width / 2f, size.height)
    lineTo(0f, size.height / 2f)
}


private val chatBubbleShape = GenericShape { size ->

    val corner = 5f

    Outline.Rounded(
        RoundRect(
            rect = size.toRect(),
            topLeft = corner.toRadius(),
            topRight = 0f.toRadius(),
            bottomRight = corner.toRadius(),
            bottomLeft = corner.toRadius()
        )
    )
}

private val triangleShape = GenericShape { size: Size ->


    moveTo(0f, 0f)
    lineTo(size.width, 0f)
    lineTo(0f, size.height)
    lineTo(0f, 0f)
}

fun Float.toRadius() = CornerRadius(this)

@Composable
fun chatBubble() {

    Row {
        Box() {

        }
    }


}