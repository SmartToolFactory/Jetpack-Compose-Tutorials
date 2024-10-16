@file:OptIn(ExperimentalMaterial3Api::class)

package com.smarttoolfactory.tutorial3_1navigation

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

@Preview
@Composable
private fun App() {

    val navController = rememberNavController()

    NavHost(
        modifier = Modifier.systemBarsPadding().fillMaxSize(),
        navController = navController,
        enterTransition = { EnterTransition.None },
        exitTransition = { ExitTransition.None },
        startDestination = Routes.SETTINGS_ROUTE
    ) {

        composable(Routes.HOME_ROUTE) {
            MainScreen {
                navController.navigate(Routes.SETTINGS_ROUTE)
            }
        }
        composable(
            route = Routes.SETTINGS_ROUTE,
            enterTransition = {
                slideIntoContainer(
                    AnimatedContentTransitionScope.SlideDirection.Right,
                    animationSpec = tween(300)
                )
            },
            exitTransition = {
                slideOutOfContainer(
                    AnimatedContentTransitionScope.SlideDirection.Left,
                    animationSpec = tween(50)
                )
            },
            popEnterTransition = {
                fadeIn(animationSpec = tween(50))
            },
            popExitTransition = {
                slideOutOfContainer(
                    AnimatedContentTransitionScope.SlideDirection.Right,
                    animationSpec = tween(300)
                )
            }
        ) {

            val coroutineScope = rememberCoroutineScope()
            val offsetX = remember {
                Animatable(0f)
            }

            var isDetailScreen by remember {
                mutableStateOf(false)
            }

            var data by remember {
                mutableStateOf<Holder?>(null)
            }

            LaunchedEffect(Unit) {
                snapshotFlow {
                    offsetX.value
                }
                    .map { it != 0f }
                    .collectLatest {
                        isDetailScreen = it
                    }
            }

            BackHandler(isDetailScreen) {
                coroutineScope.launch {
                    offsetX.animateTo(
                        0f,
                        animationSpec = tween(durationMillis = 300)
                    )
                }
            }

            Column {

                TopAppBar(
                    navigationIcon = {
                        IconButton(
                            onClick = {
                                if (isDetailScreen) {
                                    coroutineScope.launch {
                                        offsetX.animateTo(
                                            0f,
                                            animationSpec = tween(durationMillis = 300)
                                        )
                                    }
                                }
                            }
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Default.ArrowBack,
                                contentDescription = null
                            )
                        }
                    },
                    title = {
                        Text(if (isDetailScreen) "Preview" else "Main")
                    }
                )

                val dragModifier = Modifier.pointerInput(Unit) {
                    val componentWidth = size.width
                    detectHorizontalDragGestures(
                        onDragEnd = {
                            coroutineScope.launch {
                                if (offsetX.value > componentWidth / 2f) {
                                    offsetX.animateTo(
                                        componentWidth.toFloat(),
                                        animationSpec = tween(durationMillis = 300)
                                    )
                                } else {
                                    offsetX.animateTo(
                                        0f,
                                        animationSpec = tween(durationMillis = 300)

                                    )
                                }
                            }
                        }
                    ) { _, dragAmount ->
                        coroutineScope.launch {
                            offsetX.snapTo(
                                (offsetX.value - dragAmount).coerceIn(
                                    0f,
                                    componentWidth.toFloat()
                                )
                            )
                        }
                    }
                }
                BoxWithConstraints(
                    modifier = Modifier.fillMaxSize()
                ) {


                    val width = constraints.maxWidth.toFloat()


                    Box(
                        modifier = Modifier
                            .graphicsLayer {
                                translationX = -offsetX.value
                            }
                    ) {
                        MainScreen {
                            coroutineScope.launch {
                                data = Holder(it)
                                isDetailScreen = true
                                offsetX.animateTo(
                                    targetValue = width,
                                    animationSpec = tween(300)
                                )
                            }
                        }
                    }

                    data?.let {
                        if (isDetailScreen) {
                            Box(
                                modifier = Modifier
                                    .zIndex(100f)
                                    .then(dragModifier)
                                    .graphicsLayer {
                                        translationX = size.width - offsetX.value
                                    }
                            ) {
                                PreviewScreen(it.value)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun MainScreen(
    onClick: (Int) -> Unit,
) {

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {


        LazyColumn(
            state = rememberLazyListState(),
            contentPadding = PaddingValues(vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier
        )
        {
            items(120) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            onClick(it)
                        },
                    elevation = CardDefaults.cardElevation(4.dp),
                    colors = CardDefaults.cardColors(Color.White)
                ) {
                    Row {
                        Text(text = "Hello $it", modifier = Modifier.padding(16.dp))
                        Spacer(modifier = Modifier.weight(1f))
                        Text(text = "World $it", modifier = Modifier.padding(16.dp))
                    }
                }
            }
        }
    }
}

@Composable
fun PreviewScreen(
    value: Int,
) {

    Box(
        modifier = Modifier
            .fillMaxSize()

    ) {
        Column(
            Modifier
                .fillMaxSize()
                .background(Color.Green)
                .padding(horizontal = 5.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = "Hello $value", fontSize = 20.sp)
        }
    }
}

private object Routes {
    const val HOME_ROUTE = "Home"
    const val SETTINGS_ROUTE = "Settings"
}

data class Holder(val value: Int)
