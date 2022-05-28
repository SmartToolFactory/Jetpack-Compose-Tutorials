package com.smarttoolfactory.tutorial1_1basics.chapter2_material_widgets

import android.content.res.Configuration
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Navigation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.smarttoolfactory.tutorial1_1basics.model.places
import com.smarttoolfactory.tutorial1_1basics.ui.ComposeTutorialsTheme
import com.smarttoolfactory.tutorial1_1basics.ui.components.PlacesToBookVerticalComponent
import kotlinx.coroutines.launch

@ExperimentalAnimationApi
@ExperimentalMaterialApi
@Composable
fun Tutorial2_10Screen1() {
    TutorialContent()
}

@ExperimentalAnimationApi
@ExperimentalMaterialApi
@Composable
private fun TutorialContent() {
    val bottomSheetScaffoldState = rememberBottomSheetScaffoldState(
        bottomSheetState = BottomSheetState(BottomSheetValue.Collapsed)
    )

    BottomSheetScaffold(
        scaffoldState = bottomSheetScaffoldState,
        sheetElevation = 8.dp,
        sheetShape = RoundedCornerShape(
            bottomStart = 0.dp,
            bottomEnd = 0.dp,
            topStart = 12.dp,
            topEnd = 12.dp
        ),
        sheetGesturesEnabled = true,
        sheetContent = {
            SheetContent()
        },
        drawerGesturesEnabled = true,
        drawerScrimColor = Color(0xff000000),
        // This is the height in collapsed state
        sheetPeekHeight = 70.dp,
        floatingActionButton = {
            FloatingActionButtonComponent(bottomSheetScaffoldState.bottomSheetState)
        },
        floatingActionButtonPosition = FabPosition.End
    ) {
        MainContent(bottomSheetScaffoldState.bottomSheetState)
    }
}

@ExperimentalMaterialApi
@Composable
private fun MainContent(bottomSheetState: BottomSheetState) {

    val direction = bottomSheetState.direction
    val currentValue: BottomSheetValue = bottomSheetState.currentValue
    val targetValue: BottomSheetValue = bottomSheetState.targetValue
    val overflow = bottomSheetState.overflow.value
    val offset = bottomSheetState.offset.value

    val progress = bottomSheetState.progress
    val fraction = progress.fraction
    val from = progress.from.name
    val to = progress.to.name

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xff6D4C41))
            .padding(top = 30.dp)
    ) {
        Text(
            color = Color.White,
            text = "direction:$direction\n" +
                    "isExpanded: ${bottomSheetState.isExpanded}\n" +
                    "isCollapsed: ${bottomSheetState.isCollapsed}\n" +
                    "isAnimationRunning: ${bottomSheetState.isAnimationRunning}"
        )

        Text(
            color = Color.White,
            text = "currentValue: ${currentValue}\n" +
                    "targetValue: ${targetValue}\n" +
                    "overflow: ${overflow}\n" +
                    "offset: $offset"
        )

        Text(
            color = Color.White,
            text = "progress: $progress\n" +
                    "fraction: ${fraction}\n" +
                    "from: ${from}\n" +
                    "to: $to"
        )
    }
}


@ExperimentalMaterialApi
@Composable
private fun FloatingActionButtonComponent(
    bottomSheetState: BottomSheetState
) {
    val coroutineScope = rememberCoroutineScope()
    FloatingActionButton(
        onClick = {
            coroutineScope.launch {
                if (bottomSheetState.isCollapsed) {
                    bottomSheetState.expand()
                } else {
                    bottomSheetState.collapse()
                }
            }
        },
        backgroundColor = Color(0xffFFA000)
    ) {
        Icon(
            Icons.Filled.Navigation, tint = Color.White,
            contentDescription = null
        )
    }
}

@Composable
private fun SheetContent() {
    Column(
        modifier = Modifier
            // Min height doesn't have effect since peekHeight is
            // used for BottomSheetValue.Collapsed
            // max height determines how high content can be when
            // BottomSheetValue.Expanded
            .heightIn(min = 300.dp, max = 500.dp)
    ) {
        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Places to Visit",
            textAlign = TextAlign.Center,
            fontWeight = FontWeight.Bold,
            color = Color(0xffFDD835),
            fontSize = 24.sp,
            modifier = Modifier.padding(8.dp)
        )
        LazyColumn(
            contentPadding = PaddingValues(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(places) { place ->
                PlacesToBookVerticalComponent(place = place)
            }
        }
    }
}

@ExperimentalAnimationApi
@ExperimentalMaterialApi
@Preview
@Preview("dark", uiMode = Configuration.UI_MODE_NIGHT_YES)
@Preview(device = Devices.PIXEL_C)
@Composable
private fun FloatingActionButtonComponentPreview() {

    val bottomSheetState = rememberBottomSheetScaffoldState(
        bottomSheetState = BottomSheetState(BottomSheetValue.Collapsed)
    ).bottomSheetState

    ComposeTutorialsTheme {
        FloatingActionButtonComponent(bottomSheetState)
    }
}

@ExperimentalAnimationApi
@ExperimentalMaterialApi
@Preview
@Preview("dark", uiMode = Configuration.UI_MODE_NIGHT_YES)
@Preview(device = Devices.PIXEL_C)
@Composable
private fun TutorialContentPreview() {
    ComposeTutorialsTheme {
        TutorialContent()
    }
}

@ExperimentalAnimationApi
@ExperimentalMaterialApi
@Preview
@Preview("dark", uiMode = Configuration.UI_MODE_NIGHT_YES)
@Preview(device = Devices.PIXEL_C)
@Composable
private fun MainContentPreview() {

    val bottomSheetState = rememberBottomSheetScaffoldState(
        bottomSheetState = BottomSheetState(BottomSheetValue.Collapsed)
    ).bottomSheetState

    ComposeTutorialsTheme {
        MainContent(bottomSheetState)
    }
}

@ExperimentalAnimationApi
@ExperimentalMaterialApi
@Preview
@Preview("dark", uiMode = Configuration.UI_MODE_NIGHT_YES)
@Preview(device = Devices.PIXEL_C)
@Composable
private fun SheetContentPreview() {
    ComposeTutorialsTheme {
        SheetContent()
    }
}