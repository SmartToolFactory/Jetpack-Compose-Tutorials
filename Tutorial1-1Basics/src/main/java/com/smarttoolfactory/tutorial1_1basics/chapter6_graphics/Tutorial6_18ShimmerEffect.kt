package com.smarttoolfactory.tutorial1_1basics.chapter6_graphics

import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.smarttoolfactory.tutorial1_1basics.ui.components.StyleableTutorialText

@Preview
@Composable
fun Tutorial6_18Screen() {
    TutorialContent()
}

@Preview
@Composable
private fun TutorialContent() {
    ShimmerTest()
}

@Preview
@Composable
private fun ShimmerTest() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp)
    ) {
        StyleableTutorialText(
            text = "Items that control their animation State. " +
                    "When a new item enters composition its progress is restarted.",
            bullets = false
        )
        LazyColumn(
            modifier = Modifier.weight(1f),
            contentPadding = PaddingValues(8.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(50) {
                ShimmerRow()
            }
        }

        val transition = rememberInfiniteTransition(label = "shimmer")
        val progress by transition.animateFloat(
            initialValue = -2f,
            targetValue = 2f,
            animationSpec = infiniteRepeatable(
                animation = tween(
                    durationMillis = 3000,
                )
            ), label = "shimmer"
        )

        StyleableTutorialText(
            text = "Items that get animation progress from one controller. Since " +
                    "these items get progress from single Controller they are always synced",
            bullets = false
        )

        LazyColumn(
            modifier = Modifier.weight(1f),
            contentPadding = PaddingValues(8.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(50) {
                ShimmerRow(progress)
            }
        }
    }
}


@Composable
private fun ShimmerRow() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.Top
    ) {
        Box(
            modifier = Modifier
                .size(60.dp)
                .clip(RoundedCornerShape(8.dp))
                .shimmer()
        )

        Spacer(modifier = Modifier.width(8.dp))
        Column {
            Box(
                modifier = Modifier
                    .fillMaxWidth(.3f)
                    .height(20.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .shimmer()

            )
            Spacer(modifier = Modifier.height(12.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth(.5f)
                    .height(16.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .shimmer()

            )

        }
    }
}

@Composable
private fun ShimmerRow(progress: Float) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.Top
    ) {
        Box(
            modifier = Modifier
                .size(60.dp)
                .clip(RoundedCornerShape(8.dp))
                .drawShimmer(progress)
        )

        Spacer(modifier = Modifier.width(8.dp))
        Column {
            Box(
                modifier = Modifier
                    .fillMaxWidth(.3f)
                    .height(20.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .drawShimmer(progress)

            )
            Spacer(modifier = Modifier.height(12.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth(.5f)
                    .height(16.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .drawShimmer(progress)
            )
        }
    }
}

fun Modifier.shimmer(
    durationMillis: Int = 2500
) = composed {
    val transition = rememberInfiniteTransition(label = "shimmer")
    val progress by transition.animateFloat(
        initialValue = -2f,
        targetValue = 2f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = durationMillis,
            )
        ), label = "shimmer"
    )
    drawShimmer(progress)
}

fun Modifier.drawShimmer(progress: Float) = this.then(

    Modifier
        .drawWithContent {

            val width = size.width
            val height = size.height
            val offset = progress * width

            drawContent()
            val brush = Brush.linearGradient(
                colors = listOf(
                    Color.LightGray,
                    Color.LightGray,
                    Color.White,
                    Color.LightGray,
                    Color.LightGray
                ),
                start = Offset(offset, 0f),
                end = Offset(offset + width, height)
            )
            drawRect(brush)
        }
)
