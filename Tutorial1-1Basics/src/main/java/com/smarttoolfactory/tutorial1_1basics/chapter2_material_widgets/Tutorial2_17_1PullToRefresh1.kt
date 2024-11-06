@file:OptIn(ExperimentalMaterialApi::class)

package com.smarttoolfactory.tutorial1_1basics.chapter2_material_widgets

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay

@Preview
@Composable
fun Tutorial2_17Screen1() {
    TutorialContent()
}

@Composable
private fun TutorialContent() {
    var isRefreshing by remember {
        mutableStateOf(false)
    }

    val context = LocalContext.current

    LaunchedEffect(isRefreshing) {
        if (isRefreshing) {
            delay(1000)
            isRefreshing = false
        }
    }

    val pullToRefreshState = rememberPullRefreshState(
        refreshing = isRefreshing,
        onRefresh = {
            isRefreshing = true
            Toast.makeText(context, "onRefresh called", Toast.LENGTH_SHORT).show()
        },
        refreshingOffset = 40.dp,
        refreshThreshold = 40.dp
    )

    Box(modifier = Modifier.fillMaxSize()) {

        Column(
            modifier = Modifier.pullRefresh(state = pullToRefreshState)
        ) {

            Button(
                modifier = Modifier.padding(16.dp).fillMaxWidth(),
                onClick = {
                    isRefreshing = isRefreshing.not()
                }
            ) {
                Text("Refreshing: $isRefreshing")
            }

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize(),
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
        }

        PullRefreshIndicator(
            modifier = Modifier
                .align(Alignment.TopCenter)
            // 🔥To display actual position of Composable, drawn into Canvas and translated in GraphicLayer
//                .border(2.dp, Color.Red)
            ,
            refreshing = isRefreshing,
            state = pullToRefreshState
        )
    }
}
