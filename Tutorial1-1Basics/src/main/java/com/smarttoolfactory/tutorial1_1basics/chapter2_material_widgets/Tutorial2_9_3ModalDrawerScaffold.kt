package com.smarttoolfactory.tutorial1_1basics.chapter2_material_widgets

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.shape.CutCornerShape
import androidx.compose.material.Divider
import androidx.compose.material.DrawerValue
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ModalDrawer
import androidx.compose.material.Scaffold
import androidx.compose.material.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch

@ExperimentalMaterialApi
@Preview
@Composable
fun Tutorial2_9Screen3() {
    TutorialContent()
}

@ExperimentalMaterialApi
@Composable
private fun TutorialContent() {
    ModalDrawerComponent()
}

@ExperimentalMaterialApi
@Composable
private fun ModalDrawerComponent() {
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val coroutineScope = rememberCoroutineScope()
    val openDrawer: () -> Unit = { coroutineScope.launch { drawerState.open() } }
    val closeDrawer: () -> Unit = { coroutineScope.launch { drawerState.close() } }
    var selectedIndex by remember { mutableStateOf(0) }

    Scaffold(
        topBar = {
            ModalDrawerTopAppBar(openDrawer)
        },

        ) {
        ModalDrawer(
            drawerElevation = 24.dp,
            drawerShape = CutCornerShape(topEnd = 24.dp),
            drawerState = drawerState,
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
                Column(modifier = Modifier.fillMaxSize()) {
                    ModalContent(openDrawer)
                }

            }
        )
    }

}