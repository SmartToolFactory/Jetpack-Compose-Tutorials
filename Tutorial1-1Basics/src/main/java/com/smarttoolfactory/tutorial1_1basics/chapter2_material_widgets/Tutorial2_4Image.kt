package com.smarttoolfactory.tutorial1_1basics.chapter2_material_widgets

import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.CutCornerShape
import androidx.compose.foundation.shape.GenericShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.smarttoolfactory.tutorial1_1basics.R
import com.smarttoolfactory.tutorial1_1basics.components.*
import dev.chrisbanes.accompanist.imageloading.ImageLoadState
import dev.chrisbanes.accompanist.imageloading.MaterialLoadingImage

// TODO Add PorterDuff blend modes and Coil Image loading
@Composable
fun Tutorial2_4Screen() {
    TutorialContent()
}

@Composable
private fun TutorialContent() {

    LazyColumn(modifier = Modifier.fillMaxSize()) {
        item {
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

            TutorialText(
                text = "4-) Use Glide library to fetch an image resource from network and " +
                        "set it to Image component."
            )

            ImageDownloadWithGlideExample()

            TutorialText(
                text = "4-) Use Accompanyst library to fetch an image resource from network and " +
                        "set it to Image component using Coil, Picasso and Glide"
            )
//            ImageAsyncDownloadExample()

            TutorialText(
                text = "5-) Use Accompanist library to fetch image resource from network"
            )

            TutorialText(
                text = "5-) ContentScale represents a rule to apply to scale a source " +
                        "rectangle to be inscribed into a destination."
            )
            ImageContentScaleExample()
        }
    }
}

@Composable
fun ImageDownloadWithGlideExample() {
    val url =
        "https://avatars3.githubusercontent.com/u/35650605?s=400&u=058086fd5c263f50f2fbe98ed24b5fbb7d437a4e&v=4"

    var imageBitmap by remember { mutableStateOf<ImageBitmap?>(null) }

    val sizeModifier = Modifier
        .fillMaxWidth()
        .width(150.dp)
    val context = LocalContext.current

    val glide = Glide.with(context)


    val target = object : CustomTarget<Bitmap>() {
        override fun onLoadCleared(placeholder: Drawable?) {
            imageBitmap = null
        }

        override fun onResourceReady(bitmap: Bitmap, transition: Transition<in Bitmap>?) {
            imageBitmap = bitmap.asImageBitmap()
        }
    }

    glide
        .asBitmap()
        .load(url)
        .into(target)

    Column(
        modifier = sizeModifier,
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        imageBitmap?.let { imgBitmap ->
            // Image is a pre-defined composable that lays out and draws a given [ImageBitmap].
            Image(bitmap = imgBitmap, contentDescription = null)
        }
    }
}


//@Composable
//fun ImageAsyncDownloadExample() {
//
//    val rowModifier = Modifier
//        .fillMaxHeight()
//        .padding(4.dp)
//
//    Row(modifier = rowModifier) {
//        CoilImage(
//            data = "https://source.unsplash.com/pGM4sjt_BdQ",
//            modifier = rowModifier.weight(1f),
//            content = loadImage(),
//        )
//        PicassoImage(
//            data = "https://source.unsplash.com/-LojFX9NfPY",
//            modifier = rowModifier.weight(1f),
//            content = loadImage()
//        )
//        GlideImage(
//            data = "https://source.unsplash.com/-LojFX9NfPY",
//            modifier = rowModifier.weight(1f),
//            content = loadImage()
//        )
//    }
//}

private fun loadImage(): @Composable (
BoxScope.(imageLoadState: ImageLoadState) -> Unit) = { imageState ->
    when (imageState) {
        is ImageLoadState.Success -> {
            MaterialLoadingImage(
                result = imageState,
                contentDescription = null,
                fadeInEnabled = true,
                fadeInDurationMs = 600,
                contentScale = ContentScale.Crop,
            )
        }
        is ImageLoadState.Error,
        is ImageLoadState.Empty -> {
            Image(
                painterResource(R.drawable.ic_launcher_foreground),
                contentDescription = null
            )
        }
        is ImageLoadState.Loading -> {
            Box(Modifier.matchParentSize()) {
                CircularProgressIndicator(Modifier.align(Alignment.Center))
            }
        }
    }
}


@Composable
private fun BasicImageExample() {
    TutorialText2(text = "ImageBitmap")
    val painter: Painter = painterResource(id = R.drawable.landscape1)
    Image(painter, contentDescription = null)
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
        val vectorRes1: Painter = painterResource(id = R.drawable.vd_clock_alarm)
        Image(vectorRes1, modifier = Modifier.size(60.dp), contentDescription = null)

        val vectorRes2: Painter = painterResource(id = R.drawable.vd_dashboard_active)
        Image(vectorRes2, modifier = Modifier.size(60.dp), contentDescription = null)

        val vectorRes3: Painter = painterResource(id = R.drawable.vd_home_active)
        Image(vectorRes3, modifier = Modifier.size(60.dp), contentDescription = null)

        val vectorRes4: Painter = painterResource(id = R.drawable.vd_notification_active)
        Image(vectorRes4, modifier = Modifier.size(60.dp), contentDescription = null)
    }
}

@Composable
fun ImagePainterExample() {

    TutorialText2(text = "Painter")

    val imageBitmap: ImageBitmap = ImageBitmap.imageResource(
        LocalContext.current.resources,
        R.drawable.landscape3
    )

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

    // TODO Deprecated, update to beta01
//    val deferredResource: DeferredResource<ImageBitmap> =
//        loadImageResource(id = R.drawable.landscape2)
//
//    deferredResource.resource.resource?.let { imageBitmap ->
//
//        // We need a MUTABLE Bitmap to draw on Canvas to not get IllegalArgumentException
//        val bitmap = imageBitmap.asAndroidBitmap().copy(Bitmap.Config.ARGB_8888, true)
//
//        val paint = Paint().apply {
//            style = PaintingStyle.Stroke
//            strokeWidth = 10f
//            color = Color(0xff29B6F6)
//
//        }
//
//        // We need a ImageBitmap for Jetpack Compose Canvas
//        val newImageBitmap = bitmap.asImageBitmap()
//
//        val canvas = Canvas(newImageBitmap)
//
//        canvas.drawRect(0f, 0f, 200f, 200f, paint = paint)
//        canvas.drawCircle(
//            Offset(
//                newImageBitmap.width / 2 - 75f,
//                newImageBitmap.height / 2 + 75f
//            ), 150.0f, paint
//        )
//
//        Image(bitmap = newImageBitmap, contentDescription = null)
//    }
}

@Composable
private fun ImageShapeAndFilterExample() {

    val avatarBitmap1: Painter = painterResource(id = R.drawable.avatar_1_raster)
    val avatarBitmap2 = painterResource(id = R.drawable.avatar_2_raster)
    val avatarBitmap3 = painterResource(id = R.drawable.avatar_3_raster)
    val avatarBitmap4 = painterResource(id = R.drawable.avatar_4_raster)

    TutorialText2(text = "Shape")

    FullWidthRow(
        modifier = Modifier.height(100.dp),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {

        Image(
            modifier = Modifier
                .clip(RoundedCornerShape(10.dp)),
            painter = avatarBitmap1,
            contentDescription = null
        )
        Image(
            modifier = Modifier
                .shadow(4.dp, CircleShape)
                .clip(CircleShape),
            painter = avatarBitmap2,
            contentDescription = null
        )
        Image(
            modifier = Modifier
                .shadow(5.dp, CutCornerShape(10.dp))
                .clip(CutCornerShape(10.dp)),
            painter = avatarBitmap3,
            contentDescription = null
        )

        // 🔥 Adding clip = true flag adds shadow and clips to shape
        Image(
            modifier = Modifier
                .shadow(2.dp, diamondShape, clip = true),
            painter = avatarBitmap4,
            contentDescription = null
        )
    }

    TutorialText2(text = "Color Filter")
    FullWidthRow(
        modifier = Modifier.height(100.dp),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {

        Image(
            painter = avatarBitmap1,
            colorFilter = ColorFilter.tint(color = Color.Green, blendMode = BlendMode.Darken),
            contentDescription = null
        )

        Image(
            painter = avatarBitmap1,
            colorFilter = ColorFilter.tint(color = Color.Green, blendMode = BlendMode.Lighten),
            contentDescription = null
        )

        Image(
            painter = avatarBitmap1,
            colorFilter = ColorFilter.tint(color = Color.Green, blendMode = BlendMode.DstOver),
            contentDescription = null
        )

        Image(
            painter = avatarBitmap1,
            colorFilter = ColorFilter.tint(color = Color.Green, blendMode = BlendMode.Color),
            contentDescription = null
        )
    }

    // TODO PorterDuff mode is not working properly, or i couldn't figure it out yet
    // Check out https://stackoverflow.com/questions/65653560/jetpack-compose-applying-porterduffmode-to-image
//    val imageBitmapSrc: ImageBitmap = imageFromResource(
//        LocalContext.current.resources,
//        R.drawable.composite_src
//    )
//    val imageBitmapDst: ImageBitmap = imageFromResource(
//        LocalContext.current.resources,
//        R.drawable.composite_dst
//    )
//
//    val size = Size(imageBitmapSrc.width.toFloat() * 2, imageBitmapSrc.height.toFloat() * 2)
//
//    val blendPainter = remember {
//        object : Painter() {
//
//            override val intrinsicSize: Size
//                get() = size
//
//            override fun DrawScope.onDraw() {
//
//                drawRect(color = Color.Green, blendMode = BlendMode.Clear)
//                drawImage(image = imageBitmapDst)
//                drawImage(image =imageBitmapDst, blendMode = BlendMode.Lighten)
//            }
//        }
//    }
//
//    Image(blendPainter, contentDescription = null)
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

        val painter = painterResource(id = R.drawable.landscape10)

        TutorialText2(text = "Original")

        Image(painter = painter, contentDescription = null)

        TutorialText2(text = "ContentScale.None")

        Image(
            modifier = imageModifier,
            painter = painter,
            contentScale = ContentScale.None,
            contentDescription = null
        )

        TutorialText2(text = "ContentScale.Crop")

        Image(
            modifier = imageModifier,
            painter = painter,
            contentScale = ContentScale.Crop,
            contentDescription = null
        )

        TutorialText2(text = "ContentScale.FillBounds")

        Image(
            modifier = imageModifier,
            painter = painter,
            contentScale = ContentScale.FillBounds,
            contentDescription = null
        )

        TutorialText2(text = "ContentScale.FillHeight")

        Image(
            modifier = imageModifier,
            painter = painter,
            contentScale = ContentScale.FillHeight,
            contentDescription = null
        )

        TutorialText2(text = "ContentScale.FillWidth")

        Image(
            modifier = imageModifier,
            painter = painter,
            contentScale = ContentScale.FillWidth,
            contentDescription = null
        )

        TutorialText2(text = "ContentScale.Fit")

        Image(
            modifier = imageModifier,
            painter = painter,
            contentScale = ContentScale.Fit,
            contentDescription = null
        )

        TutorialText2(text = "ContentScale.Inside")

        Image(
            modifier = imageModifier,
            painter = painter,
            contentScale = ContentScale.Inside,
            contentDescription = null
        )


        val painter2: Painter = painterResource(id = R.drawable.landscape5)

        TutorialText2(text = "Original")

        Image(
            painter = painter2, contentDescription = null
        )

        TutorialText2(text = "ContentScale.None")

        Image(
            modifier = imageModifier2,
            painter = painter2,
            contentScale = ContentScale.None,
            contentDescription = null
        )

        TutorialText2(text = "ContentScale.Crop")

        Image(
            modifier = imageModifier2,
            painter = painter2,
            contentScale = ContentScale.Crop,
            contentDescription = null
        )

        TutorialText2(text = "ContentScale.FillBounds")

        Image(
            modifier = imageModifier2,
            painter = painter2,
            contentScale = ContentScale.FillBounds,
            contentDescription = null
        )

        TutorialText2(text = "ContentScale.FillHeight")

        Image(
            modifier = imageModifier2,
            painter = painter2,
            contentScale = ContentScale.FillHeight,
            contentDescription = null
        )

        TutorialText2(text = "ContentScale.FillWidth")

        Image(
            modifier = imageModifier2,
            painter = painter2,
            contentScale = ContentScale.FillWidth,
            contentDescription = null
        )

        TutorialText2(text = "ContentScale.Fit")

        Image(
            modifier = imageModifier2,
            painter = painter2,
            contentScale = ContentScale.Fit,
            contentDescription = null
        )

        TutorialText2(text = "ContentScale.Inside")

        Image(
            modifier = imageModifier2,
            painter = painter2,
            contentScale = ContentScale.Inside,
            contentDescription = null
        )
    }
}

private val diamondShape = GenericShape { size: Size, layoutDirection: LayoutDirection ->
    moveTo(size.width / 2f, 0f)
    lineTo(size.width, size.height / 2f)
    lineTo(size.width / 2f, size.height)
    lineTo(0f, size.height / 2f)
}


private val triangleShape = GenericShape { size: Size, layoutDirection: LayoutDirection ->
    val path = Path()
    path.apply {

        moveTo(0f, 0f)
        lineTo(size.width, 0f)
        lineTo(0f, size.height)
        lineTo(0f, 0f)
    }

    addPath(path = path)
}

