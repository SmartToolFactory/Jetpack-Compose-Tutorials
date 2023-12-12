package com.smarttoolfactory.tutorial1_1basics.chapter2_material_widgets

import android.annotation.SuppressLint
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Expand
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.smarttoolfactory.tutorial1_1basics.R
import kotlinx.coroutines.launch

@ExperimentalAnimationApi
@ExperimentalMaterialApi
@Preview
@Composable
fun Tutorial2_10Screen2() {
    TutorialContent()
}

@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@ExperimentalAnimationApi
@ExperimentalMaterialApi
@Composable
private fun TutorialContent() {

    val modalBottomSheetState = rememberModalBottomSheetState(
        initialValue = ModalBottomSheetValue.HalfExpanded,
        skipHalfExpanded = false,
        confirmValueChange = { modalBottomSheetValue: ModalBottomSheetValue ->
            true
        }
    )
    val coroutineScope = rememberCoroutineScope()

    Scaffold(topBar = {
        TopAppBar(elevation = 8.dp, title = {
            Text("Modal BottomSheet")
        },

            actions = {
                IconButton(onClick = {
                    if (modalBottomSheetState.isVisible) {
                        coroutineScope.launch { modalBottomSheetState.hide() }
                    } else {
                        coroutineScope.launch { modalBottomSheetState.show() }
                    }
                }) {
                    Icon(imageVector = Icons.Filled.Expand, contentDescription = null)
                }
            })
    }) {

        ModalBottomSheetLayout(sheetState = modalBottomSheetState,
            sheetElevation = 8.dp,
            scrimColor = Color(0xccAAABBB),
            sheetContent = {
                // 🔥 Uncomment to see states on modal bottom sheet content
//                MainContent(modalBottomSheetState, Color(0xff4CAF50))
                SheetContent()
            },
            content = {
                MainContent(modalBottomSheetState)
            }
        )
    }
}

@ExperimentalMaterialApi
@Composable
private fun MainContent(
    modalBottomSheetState: ModalBottomSheetState, color: Color = Color(0xffE91E63)
) {


    // 🔥🔥 Don't read from state in recomposition use derivedStateOf instead
    // This is for demonstrating properties modalBottomSheetState
    // Check Tutorial 4-5-2 for derivedStateOf
    val currentValue: ModalBottomSheetValue = modalBottomSheetState.currentValue
    val targetValue: ModalBottomSheetValue = modalBottomSheetState.targetValue

    modalBottomSheetState.isVisible
    // 🔥🔥 These values are removed as of 1.4.0-alpha04
//    val direction = modalBottomSheetState.direction
//    val overflow = modalBottomSheetState.overflow.value
//    val offset = modalBottomSheetState.offset.value

//    val progress = modalBottomSheetState.progress
//    val fraction = progress.fraction
//    val from = progress.from.name
//    val to = progress.to.name

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(color)
            .padding(top = 16.dp)
    ) {
//        Text(
//            color = Color.White,
//            text = "direction:$direction\n" + "isExpanded: ${modalBottomSheetState.isVisible}\n"
//                    + "isAnimationRunning: ${modalBottomSheetState.isAnimationRunning}"
//        )
//
        Text(
            color = Color.White,
            text = "currentValue: ${currentValue}\n" + "targetValue: ${targetValue}\n"
                    + "isExpanded: ${modalBottomSheetState.isVisible}"
        )
//
//        Text(
//            color = Color.White,
//            text = "progress: $progress\n" + "fraction: ${fraction}\n"
//                    + "from: ${from}\n" + "to: $to"
//        )
    }
}


@ExperimentalMaterialApi
@Composable
private fun SheetContent() {
    Column {

        LazyColumn {

            items(userList) { item: String ->
                ListItem(icon = {
                    Image(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape),
                        painter = painterResource(id = R.drawable.avatar_1_raster),
                        contentDescription = null
                    )
                }, secondaryText = {
                    Text(text = "Secondary text")
                }) {
                    Text(text = item, fontSize = 18.sp)
                }
            }
        }
    }
}

val userList = listOf(
    "User1",
    "User2",
    "User3",
    "User4",
    "User5",
    "User6",
    "User7",
    "User8",
    "User9",
    "User10",
    "User11",
    "User12",
    "User13",
    "User14",
    "User15",
)