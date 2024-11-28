@file:OptIn(ExperimentalMaterial3Api::class, ExperimentalSharedTransitionApi::class)

package com.smarttoolfactory.tutorial1_1basics.chapter9_animation

import androidx.activity.compose.BackHandler
import androidx.activity.compose.LocalOnBackPressedDispatcherOwner
import androidx.annotation.DrawableRes
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionOnScreen
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.round
import androidx.compose.ui.unit.toSize
import androidx.compose.ui.window.DialogProperties
import com.smarttoolfactory.tutorial1_1basics.R
import kotlinx.coroutines.android.awaitFrame

private sealed class AnimationScreen {
    data object List : AnimationScreen()
    data class Details(val item: Int, val rect: Rect) : AnimationScreen()
}

@Preview
@Composable
fun SharedElementsample2() {

    BottomSheetImagePicker()
}

@Composable
fun BottomSheetImagePicker() {

    var state by remember {
        mutableStateOf<AnimationScreen>(AnimationScreen.List)
    }

    var openBottomSheet by rememberSaveable { mutableStateOf(false) }
    val bottomSheetState = rememberModalBottomSheetState()

    // App Content
    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp)
    ) {

        Spacer(Modifier.weight(1f))

        Button(
            modifier = Modifier.fillMaxWidth(),
            onClick = {
                openBottomSheet = openBottomSheet.not()
            }
        ) {
            Text("Expand")
        }
    }

    if (openBottomSheet) {
        ModalBottomSheet(
            sheetState = bottomSheetState,
            modifier = Modifier.fillMaxSize().systemBarsPadding(),
            onDismissRequest = {
                openBottomSheet = false
            }
        ) {
            BottomSheetPickerContent { uri, rect ->
                state = AnimationScreen.Details(uri, rect)
            }
        }
    }

    if (state is AnimationScreen.Details) {
        BasicAlertDialog(
            modifier = Modifier.fillMaxSize().background(Color.Black),
            onDismissRequest = {
                state = AnimationScreen.List
            },
            properties = DialogProperties(usePlatformDefaultWidth = false)
        ) {

            var visible by remember {
                mutableStateOf(false)
            }

            var expanded by remember {
                mutableStateOf(false)
            }

            val dispatcher = LocalOnBackPressedDispatcherOwner.current

            BackHandler(visible) {
                visible = false
            }

            SharedTransitionLayout(modifier = Modifier.fillMaxSize()) {

                LaunchedEffect(Unit) {
                    awaitFrame()
                    visible = true
                }

                AnimatedContent(
                    modifier = Modifier.fillMaxSize(),
                    targetState = visible,
                    label = "",
                ) { visibleState ->

                    if (expanded.not()) {
                        expanded = visibleState && isTransitionActive.not()
                    }

                    val item = (state as? AnimationScreen.Details)?.item ?: R.drawable.landscape2
                    val rect = (state as? AnimationScreen.Details)?.rect ?: Rect.Zero

                    if (visibleState) {
                        Column(
                            modifier = Modifier.fillMaxSize(),
                            verticalArrangement = Arrangement.Center
                        ) {
                            Image(
                                painter = painterResource(item),
                                modifier = Modifier
                                    .sharedElement(
                                        state = rememberSharedContentState(key = item),
                                        animatedVisibilityScope = this@AnimatedContent,
                                        boundsTransform = gridBoundsTransform
                                    )
                                    .fillMaxWidth(),
                                contentScale = ContentScale.Crop,
                                contentDescription = null
                            )
                        }
                    } else {

                        LaunchedEffect(expanded, isTransitionActive) {
                            if (expanded && isTransitionActive.not()) {
                                dispatcher?.onBackPressedDispatcher?.onBackPressed()
                            }
                        }

                        Box(modifier = Modifier.fillMaxSize()) {
                            val density = LocalDensity.current
                            val width: Dp
                            val height: Dp

                            with(density) {
                                width = rect.width.toDp()
                                height = rect.height.toDp()
                            }
                            Image(
                                painter = painterResource(item),
                                modifier = Modifier
                                    .offset {
                                        rect.topLeft.round()
                                    }
                                    .size(width, height)
                                    .sharedElement(
                                        state = rememberSharedContentState(key = item),
                                        animatedVisibilityScope = this@AnimatedContent,
                                        boundsTransform = gridBoundsTransform
                                    ),
                                contentScale = ContentScale.Crop,
                                contentDescription = null
                            )
                        }
                    }
                }
            }

        }
    }

}

@Composable
private fun BottomSheetPickerContent(
    onClick: (Int, Rect) -> Unit,
) {

    val imageUris = remember {
        listOf(
            R.drawable.landscape1,
            R.drawable.landscape2,
            R.drawable.landscape3,
            R.drawable.landscape4,
            R.drawable.landscape5,
            R.drawable.landscape6,
            R.drawable.landscape7,
            R.drawable.landscape8,
            R.drawable.landscape9,
            R.drawable.landscape10
        )
    }

    val imageMap = remember {
        mutableStateMapOf<Int, Rect>()
    }

    val density = LocalDensity.current
    val statusBarHeight = WindowInsets.statusBars.getTop(density)

    LazyVerticalGrid(
        columns = GridCells.Fixed(3), // 3 columns
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(horizontal = 16.dp)
    ) {
        item(span = { GridItemSpan(2) }) {
            Box(
                modifier = Modifier
                    .size(200.dp)
                    .background(Color.Red, RoundedCornerShape(16.dp))
            )
        }

        item {
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                ImageItem(
                    modifier = Modifier.onGloballyPositioned {
                        imageMap[imageUris[0]] = Rect(
                            offset = Offset(
                                x = it.positionOnScreen().x,
                                y = it.positionOnScreen().y - statusBarHeight
                            ),
                            size = it.size.toSize()
                        )
                    },
                    uri = imageUris[0],
                    onClick = {
                        onClick(imageUris[0], imageMap[imageUris[0]] ?: Rect.Zero)
                    }
                )
                ImageItem(
                    modifier = Modifier.onGloballyPositioned {
                        imageMap[imageUris[1]] = Rect(
                            offset = Offset(
                                x = it.positionOnScreen().x,
                                y = it.positionOnScreen().y - statusBarHeight
                            ),
                            size = it.size.toSize()
                        )
                    },
                    uri = imageUris[1],
                    onClick = {
                        onClick(imageUris[1], imageMap[imageUris[1]] ?: Rect.Zero)

                    }
                )
            }
        }

        val otherImages = imageUris.drop(2)
        items(otherImages) { uri ->
            ImageItem(
                modifier = Modifier.onGloballyPositioned {
                    imageMap[uri] = Rect(
                        offset = Offset(
                            x = it.positionOnScreen().x,
                            y = it.positionOnScreen().y - statusBarHeight
                        ),
                        size = it.size.toSize()
                    )
                },
                uri = uri,
                onClick = {
                    onClick(uri, imageMap[uri] ?: Rect.Zero)

                }
            )
        }
    }
}

@Composable
private fun ImageItem(
    modifier: Modifier = Modifier,
    @DrawableRes uri: Int,
    onClick: (Int) -> Unit,
) {

    Image(
        modifier = modifier
            .clickable {
                onClick(uri)
            },
        painter = painterResource(uri),
        contentScale = ContentScale.Crop,
        contentDescription = null
    )
}
