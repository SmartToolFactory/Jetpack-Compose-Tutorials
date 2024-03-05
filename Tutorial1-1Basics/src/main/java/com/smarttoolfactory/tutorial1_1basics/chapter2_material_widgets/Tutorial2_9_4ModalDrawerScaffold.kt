package com.smarttoolfactory.tutorial1_1basics.chapter2_material_widgets

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Divider
import androidx.compose.material.DrawerValue
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ModalDrawer
import androidx.compose.material.Scaffold
import androidx.compose.material.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch

@ExperimentalMaterialApi
@Composable
fun Tutorial2_9Screen4() {
    TutorialContent()
}

@ExperimentalMaterialApi
@Composable
private fun TutorialContent() {
    ModalDrawerComponent()
}

@ExperimentalMaterialApi
@Preview(showBackground = true)
@Composable
private fun ModalDrawerPreview(
    @PreviewParameter(DrawerStateProvider::class)
    drawerValue: DrawerValue
) {
    ModalDrawerComponent(drawerValue = drawerValue)
}

@ExperimentalMaterialApi
@Composable
private fun ModalDrawerComponent(drawerValue: DrawerValue = DrawerValue.Closed) {
    val drawerState = rememberDrawerState(drawerValue)
    val coroutineScope = rememberCoroutineScope()
    val openDrawer: () -> Unit = { coroutineScope.launch { drawerState.open() } }
    val closeDrawer: () -> Unit = { coroutineScope.launch { drawerState.close() } }
    var selectedIndex by remember { mutableIntStateOf(0) }

    ModalDrawer(
        drawerState = drawerState,
        drawerShape = RoundedCornerShape(topEnd = 24.dp),
        drawerContent = {
            ModalDrawerContentHeader()
            Divider()
            ModelDrawerContentBody(
                selectedIndex,
                onSelected = {
                    selectedIndex = it
                },
                closeDrawer = closeDrawer
            )
        },
        content = {
            Scaffold(
                topBar = {
                    ModalDrawerTopAppBar(openDrawer)
                }
            ) { contentPadding ->
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(contentPadding)
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Column(modifier = Modifier.fillMaxSize()) {
                        ModalContent(openDrawer)
                    }
                }
            }
        }
    )
}