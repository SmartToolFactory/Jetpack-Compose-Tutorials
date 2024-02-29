package com.smarttoolfactory.tutorial1_1basics.chapter2_material_widgets

import android.content.res.Configuration
import android.widget.Toast
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.BottomSheetScaffold
import androidx.compose.material.BottomSheetState
import androidx.compose.material.BottomSheetValue
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.FabPosition
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Navigation
import androidx.compose.material.rememberBottomSheetScaffoldState
import androidx.compose.material.rememberBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.smarttoolfactory.tutorial1_1basics.model.places
import com.smarttoolfactory.tutorial1_1basics.ui.ComposeTutorialsTheme
import com.smarttoolfactory.tutorial1_1basics.ui.components.PlacesToBookVerticalComponent
import kotlinx.coroutines.launch

@ExperimentalAnimationApi
@ExperimentalMaterialApi
@Preview
@Composable
fun Tutorial2_10Screen1() {
    TutorialContent()
}

@ExperimentalAnimationApi
@ExperimentalMaterialApi
@Composable
private fun TutorialContent(initialState: BottomSheetValue = BottomSheetValue.Collapsed) {
    val context = LocalContext.current

    val bottomSheetScaffoldState = rememberBottomSheetScaffoldState(
        bottomSheetState = rememberBottomSheetState(
            initialValue = initialState,
            confirmStateChange = { bottomSheetValue: BottomSheetValue ->
                // This callback gets called twice in Jetpack Compose version 1.5.4
                println("State changed to $bottomSheetValue")
                Toast.makeText(
                    context,
                    "State changed to $bottomSheetValue",
                    Toast.LENGTH_SHORT
                ).show()
                true
            }
        )
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

    val currentValue: BottomSheetValue = bottomSheetState.currentValue
    val offset = bottomSheetState.requireOffset()

    val progress = bottomSheetState.progress

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xff6D4C41))
            .padding(top = 30.dp)
    ) {
        Text(
            color = Color.White,
            text = "isExpanded: ${bottomSheetState.isExpanded}\n" +
                    "isCollapsed: ${bottomSheetState.isCollapsed}"
        )

        Text(
            color = Color.White,
            text = "currentValue: ${currentValue}\n" +
                    "offset: $offset"
        )

        Text(
            color = Color.White,
            text = "progress: $progress"
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
@Composable
private fun TutorialContentPreview(
    @PreviewParameter(BottomSheetStateProvider::class)
    state: BottomSheetValue
) {
    ComposeTutorialsTheme {
        TutorialContent(initialState = state)
    }
}

@OptIn(ExperimentalMaterialApi::class)
private class BottomSheetStateProvider : PreviewParameterProvider<BottomSheetValue> {
    override val values: Sequence<BottomSheetValue>
        get() = sequenceOf(
            BottomSheetValue.Collapsed,
            BottomSheetValue.Expanded
        )
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