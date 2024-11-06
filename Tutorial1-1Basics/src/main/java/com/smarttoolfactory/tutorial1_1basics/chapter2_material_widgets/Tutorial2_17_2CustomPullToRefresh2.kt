@file:OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterialApi::class)

package com.smarttoolfactory.tutorial1_1basics.chapter2_material_widgets

import android.widget.Toast
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay

@Preview
@Composable
fun Tutorial2_17Screen2() {
    TutorialContent()
}

@Composable
private fun TutorialContent() {
    var isRefreshing by remember {
        mutableStateOf(false)
    }

    val context = LocalContext.current
    val refreshThreshold = 60.dp

    val pullToRefreshState = rememberPullRefreshState(
        refreshing = isRefreshing,
        onRefresh = {
            isRefreshing = true
            Toast.makeText(context, "onRefresh called", Toast.LENGTH_SHORT).show()
        },
        refreshThreshold = refreshThreshold,
        refreshingOffset = 50.dp
    )

    LaunchedEffect(isRefreshing) {
        if (isRefreshing) {
            delay(1500)
            isRefreshing = false
        }
    }

    val density = LocalDensity.current
    val refreshThresholdPx = with(density) { refreshThreshold.toPx() }

    val offset by remember {
        derivedStateOf {
            if (isRefreshing) {
                refreshThresholdPx
            } else if (
                pullToRefreshState.progress <= 1f
            ) {
                (refreshThresholdPx * pullToRefreshState.progress).coerceAtLeast(0f)

            } else {
                refreshThresholdPx + (refreshThresholdPx * (pullToRefreshState.progress - 1) * .2f).coerceAtLeast(0f)
            }
        }
    }

    val animatedOffset by animateFloatAsState(
        targetValue = offset,
        label = "pull to refresh"
    )

    Box(modifier = Modifier.fillMaxSize()) {

        Column(
            modifier = Modifier
                .pullRefresh(pullToRefreshState)
        ) {

            LazyColumn(
                modifier = Modifier
                    .graphicsLayer {
                        translationY = animatedOffset
                    }
                    .weight(1f),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(100) {
                    Text(
                        modifier = Modifier.fillMaxWidth()
                            .shadow(4.dp, RoundedCornerShape(16.dp))
                            .background(Color.White)
                            .padding(16.dp),
                        text = "Row: $it",
                        fontSize = 18.sp
                    )
                }
            }

            Text("Position: $offset, progress: ${pullToRefreshState.progress}")

            Button(
                modifier = Modifier.padding(16.dp).fillMaxWidth(),
                onClick = {
                    isRefreshing = isRefreshing.not()
                }
            ) {
                Text("Refreshing: $isRefreshing")
            }
        }

        Box(
            modifier = Modifier
                .align(Alignment.TopCenter)
                // 🔥 default Modifier for transform and scale
//                .pullRefreshIndicatorTransform(state = pullToRefreshState, scale = true)
                // 🔥 custom Modifier for transform and scale
                .graphicsLayer {
                    translationY = (animatedOffset - refreshThresholdPx / 2)
                        .coerceAtMost(refreshThresholdPx * .25f)
                    scaleX = if (isRefreshing) 1f else pullToRefreshState.progress.coerceIn(0f, 1f)
                    scaleY = if (isRefreshing) 1f else pullToRefreshState.progress.coerceIn(0f, 1f)
                }
                .shadow(2.dp, CircleShape)
                .background(Color.White, CircleShape)
                .padding(4.dp)

        ) {
            CircularProgressIndicator()
        }
    }
}