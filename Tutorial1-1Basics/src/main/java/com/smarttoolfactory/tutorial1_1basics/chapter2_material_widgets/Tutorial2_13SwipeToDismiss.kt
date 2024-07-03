package com.smarttoolfactory.tutorial1_1basics.chapter2_material_widgets

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Button
import androidx.compose.material.Card
import androidx.compose.material.DismissDirection.EndToStart
import androidx.compose.material.DismissDirection.StartToEnd
import androidx.compose.material.DismissValue.Default
import androidx.compose.material.DismissValue.DismissedToEnd
import androidx.compose.material.DismissValue.DismissedToStart
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.FractionalThreshold
import androidx.compose.material.Icon
import androidx.compose.material.ListItem
import androidx.compose.material.SwipeToDismiss
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.rememberDismissState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.coroutines.flow.MutableStateFlow


@ExperimentalMaterialApi
@Preview
@Composable
fun Tutorial2_13Screen() {
    val viewModel = MyViewModel()
    TutorialContent(viewModel)
}

@ExperimentalMaterialApi
@Composable
private fun TutorialContent(viewModel: MyViewModel) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        // Button  to generate list. viewmodel is used to generate list which is close to real world scenario
        Button(onClick = {
            viewModel.newList()
        }) {
            Text(text = "Generate User List")
        }

        // safely collecting flows
        val usersList = viewModel.listFlow.collectAsStateWithLifecycle()
        // This is an example of a list of dismissible items, similar to what you would see in an
// email app. Swiping left reveals a 'delete' icon and swiping right reveals a 'done' icon.
// The background will start as grey, but once the dismiss threshold is reached, the colour
// will animate to red if you're swiping left or green if you're swiping right. When you let
// go, the item will animate out of the way if you're swiping left (like deleting an email) or
// back to its default position if you're swiping right (like marking an email as read/unread).
        LazyColumn {
            // deleting items will change row positions so it is required to add key which is unique
            // for demo user id is used as key
            items(items = usersList.value, key = { user -> user.id }) { user ->

                // https://stackoverflow.com/questions/75040603/is-composes-swipe-to-dismiss-state-always-remember-the-old-item-based-on-id-ev
                // This is required as explained in the link stackoverflow link
                val currentItem by rememberUpdatedState(user)

                val dismissState = rememberDismissState(
                    confirmStateChange = { dismissValue  ->
                        when (dismissValue) {
                            // do something when right to left swipe
                            DismissedToEnd -> {
                                viewModel.removeItem(currentItem)
                                true
                            }
                            // do something when left to right swipe
                            DismissedToStart -> {
                                viewModel.removeItem(currentItem)
                                true
                            }
                            else -> { false }
                        }
                    }
                )
                SwipeToDismiss(
                    state = dismissState,
                    modifier = Modifier.padding(vertical = 4.dp),
                    directions = setOf(StartToEnd, EndToStart),
                    dismissThresholds = { direction ->
                        FractionalThreshold(if (direction == StartToEnd) 0.25f else 0.5f)
                    },
                    background = {

                        val direction = dismissState.dismissDirection ?: return@SwipeToDismiss

                        val color by animateColorAsState(

                            when (dismissState.targetValue) {
                                Default -> Color.LightGray
                                DismissedToEnd -> Color.Green
                                DismissedToStart -> Color.Red
                            }
                        )
                        val alignment = when (direction) {
                            StartToEnd -> Alignment.CenterStart
                            EndToStart -> Alignment.CenterEnd
                        }
                        val icon = when (direction) {
                            StartToEnd -> Icons.Default.Done
                            EndToStart -> Icons.Default.Delete
                        }
                        val scale by animateFloatAsState(
                            if (dismissState.targetValue == Default) 0.75f else 1f
                        )

                        Box(
                            Modifier
                                .fillMaxSize()
                                .background(color)
                                .padding(horizontal = 20.dp),
                            contentAlignment = alignment
                        ) {
                            Icon(
                                icon,
                                contentDescription = "Localized description",
                                modifier = Modifier.scale(scale)
                            )
                        }
                    },
                    dismissContent = {
                        Card(
                            elevation = animateDpAsState(
                                if (dismissState.dismissDirection != null) 4.dp else 0.dp
                            ).value
                        ) {
                            ListItem(
                                text = {
                                    Text(user.name, fontWeight = FontWeight.Bold)
                                },
                                secondaryText = { Text("Swipe me left or right!") }
                            )
                        }
                    }
                )
            }
        }
    }
}

// ViewModel to generate list and expose the data as flow to ui
class MyViewModel : ViewModel() {
    private var userList = mutableStateListOf<User>()
    val listFlow = MutableStateFlow(userList)

    fun newList() {
        val mutableList = mutableStateListOf<User>()
        for (i in 0..10) {
            mutableList.add(User(i, "User$i"))
        }

        userList = mutableList
        listFlow.value = mutableList
    }

    fun removeItem(item: User) {
        val index = userList.indexOf(item)
        userList.remove(userList[index])
    }

}

data class User(val id: Int, val name: String)












