@file:Suppress("DEPRECATION")

package com.smarttoolfactory.tutorial1_1basics.chapter6_graphics

import android.content.Context
import android.graphics.Bitmap
import android.renderscript.Allocation
import android.renderscript.Element
import android.renderscript.RenderScript
import android.renderscript.ScriptIntrinsicBlur
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.smarttoolfactory.tutorial1_1basics.R
import com.smarttoolfactory.tutorial1_1basics.ui.components.StyleableTutorialText
import com.smarttoolfactory.tutorial1_1basics.ui.components.TutorialHeader
import com.smarttoolfactory.tutorial1_1basics.ui.components.TutorialText2
import kotlin.math.roundToInt


@Preview
@Composable
fun Tutorial6_40Screen1() {
    TutorialContent()
}


@Composable
private fun TutorialContent() {
    Column(modifier = Modifier.padding(8.dp)) {
        TutorialHeader(text = "RenderScript")
        StyleableTutorialText(
            text = "Blue Bitmap using **RenderScript**",
            bullets = false
        )
        RenderScriptBlursample()
    }
}

@Preview
@Composable
private fun RenderScriptBlursample() {

    Column(
        modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState())
    ) {

        val imageBitmap = ImageBitmap.imageResource(R.drawable.landscape10)

        var blurRadius by remember {
            mutableFloatStateOf(10f)
        }

        val context = LocalContext.current

        val blurredBitmap by remember(imageBitmap, blurRadius) {
            mutableStateOf(blurBitmap(context, imageBitmap.asAndroidBitmap(), blurRadius))
        }

        TutorialText2(text = "Default")

        Image(
            modifier = Modifier.fillMaxWidth().aspectRatio(3 / 2f),
            bitmap = imageBitmap,
            contentDescription = null,
            contentScale = ContentScale.FillBounds
        )


        TutorialText2(text = "Blurred")
        Image(
            modifier = Modifier.fillMaxWidth().aspectRatio(3 / 2f),
            bitmap = blurredBitmap.asImageBitmap(),
            contentDescription = null,
            contentScale = ContentScale.FillBounds
        )


        Text("Blur radius: ${blurRadius.roundToInt()}")

        Slider(
            modifier = Modifier.padding(horizontal = 16.dp),
            value = blurRadius,
            onValueChange = {
                blurRadius = it
            },
            valueRange = 0.01f..25f
        )
    }
}

fun blurBitmap(context: Context, bitmap: Bitmap, blurRadius: Float): Bitmap {

    val bitmapToBlur = bitmap.copy(Bitmap.Config.ARGB_8888, true)

    val renderScript = RenderScript.create(context)
    val input = Allocation.createFromBitmap(renderScript, bitmapToBlur)
    val output = Allocation.createTyped(renderScript, input.type)

    ScriptIntrinsicBlur.create(renderScript, Element.U8_4(renderScript)).apply {
        setRadius(blurRadius)
        setInput(input)
        forEach(output)
    }

    output.copyTo(bitmapToBlur)
    renderScript.destroy()

    return bitmapToBlur
}
