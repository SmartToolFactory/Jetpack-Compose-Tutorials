package com.smarttoolfactory.tutorial1_1basics.chapter6_graphics

import android.graphics.RenderEffect
import android.graphics.Shader
import android.os.Build
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asComposeRenderEffect
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.smarttoolfactory.tutorial1_1basics.R
import com.smarttoolfactory.tutorial1_1basics.ui.components.StyleableTutorialText
import com.smarttoolfactory.tutorial1_1basics.ui.components.TutorialHeader
import com.smarttoolfactory.tutorial1_1basics.ui.components.TutorialText2
import kotlin.math.roundToInt

@Preview
@Composable
fun Tutorial6_40Screen2() {
    TutorialContent()
}


@Composable
private fun TutorialContent() {
    Column(modifier = Modifier.padding(8.dp)) {
        TutorialHeader(text = "RenderEffect")
        StyleableTutorialText(
            text = "Blue Composables using **RenderEffect.createBlurEffect** with varying values " +
                    "and with different edgeTreatments.",
            bullets = false
        )

        RenderEffectBlurSample()
    }
}

@Preview
@Composable
private fun RenderEffectBlurSample() {

    var blurRadiusX by remember {
        mutableFloatStateOf(10f)
    }

    var blurRadiusY by remember {
        mutableFloatStateOf(10f)
    }

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(8.dp),
        ) {
            TutorialText2(
                text = "Shader.TileMode.MIRROR",
                modifier = Modifier.padding(vertical = 16.dp)
            )
            Image(
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .width(260.dp)
                    .border(2.dp, Color.Red)
                    .graphicsLayer {
                        renderEffect = RenderEffect.createBlurEffect(
                            blurRadiusX, blurRadiusY,
                            Shader.TileMode.MIRROR
                        ).asComposeRenderEffect()

                    },
                painter = painterResource(R.drawable.landscape10),
                contentDescription = null,
                contentScale = ContentScale.FillBounds
            )

            TutorialText2(
                text = "Shader.TileMode.DECAL",
                modifier = Modifier.padding(vertical = 16.dp)
            )
            Image(
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .width(260.dp)
                    .border(2.dp, Color.Red)
                    .graphicsLayer {
                        renderEffect = RenderEffect.createBlurEffect(
                            blurRadiusX, blurRadiusY,
                            Shader.TileMode.DECAL
                        ).asComposeRenderEffect()

                    },
                painter = painterResource(R.drawable.landscape10),
                contentDescription = null,
                contentScale = ContentScale.FillBounds
            )

            TutorialText2(
                text = "Shader.TileMode.CLAMP",
                modifier = Modifier.padding(vertical = 16.dp)
            )
            Image(
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .width(260.dp)
                    .border(2.dp, Color.Red)
                    .graphicsLayer {
                        renderEffect = RenderEffect.createBlurEffect(
                            blurRadiusX, blurRadiusY,
                            Shader.TileMode.CLAMP
                        ).asComposeRenderEffect()

                    },
                painter = painterResource(R.drawable.landscape10),
                contentDescription = null,
                contentScale = ContentScale.FillBounds
            )

            TutorialText2(
                text = "Shader.TileMode.REPEAT",
                modifier = Modifier.padding(vertical = 16.dp)
            )
            Image(
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .width(260.dp)
                    .border(2.dp, Color.Red)
                    .graphicsLayer {
                        renderEffect = RenderEffect.createBlurEffect(
                            blurRadiusX, blurRadiusY,
                            Shader.TileMode.REPEAT
                        ).asComposeRenderEffect()

                    },
                painter = painterResource(R.drawable.landscape10),
                contentDescription = null,
                contentScale = ContentScale.FillBounds
            )


            Text("Blur radiusX: ${blurRadiusX.roundToInt()}")

            Slider(
                modifier = Modifier.padding(horizontal = 16.dp),
                value = blurRadiusX,
                onValueChange = {
                    blurRadiusX = it
                },
                valueRange = 0.01f..25f
            )

            Text("Blur radiusY: ${blurRadiusY.roundToInt()}")

            Slider(
                modifier = Modifier.padding(horizontal = 16.dp),
                value = blurRadiusY,
                onValueChange = {
                    blurRadiusY = it
                },
                valueRange = 0.01f..25f
            )
        }
    }
}
