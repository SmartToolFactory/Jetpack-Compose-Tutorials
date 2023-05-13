package com.smarttoolfactory.tutorial1_1basics.chapter3_layout

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.smarttoolfactory.tutorial1_1basics.R
import com.smarttoolfactory.tutorial1_1basics.chapter2_material_widgets.ChatAppbar
import com.smarttoolfactory.tutorial1_1basics.chapter3_layout.chat.ChatInput
import com.smarttoolfactory.tutorial1_1basics.chapter3_layout.chat.ChatMessage
import com.smarttoolfactory.tutorial1_1basics.chapter3_layout.chat.MessageStatus
import com.smarttoolfactory.tutorial1_1basics.chapter3_layout.chat.ReceivedMessageRowAlt
import com.smarttoolfactory.tutorial1_1basics.chapter3_layout.chat.SentMessageRowAlt
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Locale
import kotlin.random.Random

@Preview
@Composable
fun Tutorial3_6Screen2() {

    val systemUiController = rememberSystemUiController()

    // Check out Tutorial4_5_1 for DisposableEffect
    DisposableEffect(key1 = true, effect = {

        systemUiController.setStatusBarColor(
            color = Color(0xff00897B)
        )
        onDispose {
            systemUiController.setStatusBarColor(
                color = Color.Transparent
            )
        }
    })

    TutorialContent()
}

@Composable
private fun TutorialContent() {

    val description = "Flexible chat rows that position message and status " +
            "based on text width, line, last width line and parent width.\n" +
            "Create messages with varying in size and line numbers to see how they are displayed."
    val context = LocalContext.current

    val messages = remember { mutableStateListOf<ChatMessage>() }
    val sdf = remember { SimpleDateFormat("hh:mm", Locale.ROOT) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xffFBE9E7))
    ) {

        ChatAppbar(
            title = "Flexible ChatRows",
            description = description,
            onClick = {
                Toast.makeText(context, description, Toast.LENGTH_SHORT).show()
            }
        )
        val scrollState = rememberLazyListState()
        val coroutineScope = rememberCoroutineScope()

        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            state = scrollState,
            contentPadding = PaddingValues(top = 8.dp, bottom = 8.dp)
        ) {
            items(messages) { message: ChatMessage ->

                // Remember random stats icon to not create in every recomposition
                val messageStatus = remember { MessageStatus.values()[Random.nextInt(3)] }

                // Toggle between sent and received message
                when (message.id.toInt() % 4) {
                    1 -> {
                        SentMessageRowAlt(
                            text = message.message,
                            quotedMessage = "Quote message",
                            messageTime = sdf.format(System.currentTimeMillis()),
                            messageStatus = messageStatus
                        )

                    }
                    2 -> {
                        ReceivedMessageRowAlt(
                            text = message.message,
                            quotedMessage = "Quote",
                            messageTime = sdf.format(System.currentTimeMillis()),
                        )

                    }
                    3 -> {
                        SentMessageRowAlt(
                            text = message.message,
                            quotedImage = R.drawable.landscape1,
                            messageTime = sdf.format(System.currentTimeMillis()),
                            messageStatus = messageStatus
                        )

                    }
                    else -> {
                        ReceivedMessageRowAlt(
                            text = message.message,
                            quotedImage = R.drawable.landscape2,
                            messageTime = sdf.format(System.currentTimeMillis()),
                        )
                    }
                }
            }
        }

        ChatInput(
            modifier=Modifier.imePadding(),
            onMessageChange = { messageContent ->
                messages.add(
                    ChatMessage(
                        (messages.size + 1).toLong(),
                        messageContent,
                        System.currentTimeMillis()
                    )
                )

                coroutineScope.launch {
                    scrollState.animateScrollToItem(messages.size - 1)
                }

            }
        )
    }
}
