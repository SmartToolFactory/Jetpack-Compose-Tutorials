package com.smarttoolfactory.tutorial1_1basics.chapter2_material_widgets

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.ContentAlpha
import androidx.compose.material.Divider
import androidx.compose.material.DrawerValue
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.ListItem
import androidx.compose.material.LocalContentAlpha
import androidx.compose.material.ModalDrawer
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.CloudUpload
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.OfflineShare
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Upload
import androidx.compose.material.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.smarttoolfactory.tutorial1_1basics.R
import com.smarttoolfactory.tutorial1_1basics.ui.components.DrawerButton
import kotlinx.coroutines.launch

@ExperimentalMaterialApi
@Preview(showBackground = true)
@Composable
fun Tutorial2_9Screen2() {
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

    ModalDrawer(
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
                ModalDrawerTopAppBar(openDrawer)
                ModalContent(openDrawer)
            }
        }
    )
}

@Composable
fun ModalDrawerTopAppBar(openDrawer: () -> Unit) {
    TopAppBar(
        title = {
            Text("ModalDrawer")
        },
        navigationIcon = {
            IconButton(onClick = openDrawer) {
                Icon(
                    imageVector = Icons.Filled.Menu,
                    contentDescription = null
                )
            }
        },
        actions = {}
    )
}

@Composable
fun ModalDrawerContentHeader() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .height(180.dp)
            .padding(20.dp)
    ) {

        Image(
            modifier = Modifier
                .size(60.dp)
                .clip(CircleShape),
            painter = painterResource(id = R.drawable.avatar_1_raster),
            contentDescription = null
        )

        Spacer(modifier = Modifier.weight(1f))
        Text(text = "Smart Tool Factory", fontWeight = FontWeight.Bold, fontSize = 22.sp)
        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            CompositionLocalProvider(LocalContentAlpha provides ContentAlpha.medium) {
                Text(text = "smarttoolfactory@icloud.com")
                Spacer(modifier = Modifier.weight(1f))
                Icon(imageVector = Icons.Filled.ArrowDropDown, contentDescription = null)
            }
        }
    }
}


@Composable
fun ModelDrawerContentBody(
    selectedIndex: Int,
    onSelected: (Int) -> Unit,
    closeDrawer: () -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        modalDrawerList.forEachIndexed { index, pair ->

            val label = pair.first
            val imageVector = pair.second
            DrawerButton(
                icon = imageVector,
                label = label,
                isSelected = selectedIndex == index,
                action = {
                    onSelected(index)
                }
            )
        }
    }
}

@ExperimentalMaterialApi
@Composable
fun ModalContent(openDrawer: () -> Unit) {
    LazyColumn {

        items(userList) { item: String ->
            ListItem(
                modifier = Modifier.clickable {
                    openDrawer()
                },
                icon = {
                    Image(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape),
                        painter = painterResource(id = R.drawable.avatar_1_raster),
                        contentDescription = null
                    )
                },
                secondaryText = {
                    Text(text = "Secondary text")
                }
            ) {
                Text(text = item, fontSize = 18.sp)
            }
        }
    }
}

val modalDrawerList = listOf(
    Pair("My Files", Icons.Filled.Folder),
    Pair("Shared with Me", Icons.Filled.People),
    Pair("Starred", Icons.Filled.Star),
    Pair("Recent", Icons.Filled.AccessTime),
    Pair("Offline", Icons.Filled.OfflineShare),
    Pair("Uploads", Icons.Filled.Upload),
    Pair("Backups", Icons.Filled.CloudUpload),
)