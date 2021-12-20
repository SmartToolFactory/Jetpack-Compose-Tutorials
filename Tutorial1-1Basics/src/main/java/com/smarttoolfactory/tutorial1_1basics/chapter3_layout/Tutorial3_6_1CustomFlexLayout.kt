package com.smarttoolfactory.tutorial1_1basics.chapter3_layout

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.smarttoolfactory.tutorial1_1basics.chapter2_material_widgets.ChatAppbar
import com.smarttoolfactory.tutorial1_1basics.ui.Blue400
import com.smarttoolfactory.tutorial1_1basics.ui.Green400
import com.smarttoolfactory.tutorial1_1basics.ui.Orange400
import com.smarttoolfactory.tutorial1_1basics.ui.Pink400
import java.text.SimpleDateFormat
import java.util.*
import kotlin.random.Random

@Composable
fun Tutorial3_6Screen1() {

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

    val messages = remember { mutableStateListOf<ChatMessage>() }
    val sdf = remember { SimpleDateFormat("hh:mm a", Locale.ROOT) }
    val context = LocalContext.current

    println("🎃 Tutorial3_6Screen messages: $messages, sdf: $sdf")

    val description = "Flexible chat rows that position message and status " +
            "based on text width, line, last width line and parent width.\n" +
            "Create and post messages with varying in size and line numbers."
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

        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            contentPadding = PaddingValues(top = 8.dp, bottom = 8.dp)
        ) {
            items(messages) { message: ChatMessage ->

                // Remember random stats icon to not create at each recomposition
                val messageStatus = remember { MessageStatus.values()[Random.nextInt(3)] }


                Column(
                    horizontalAlignment = Alignment.End,
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentHeight()
                        .padding(top = 2.dp, bottom = 2.dp)
                        .background(Color.LightGray)
                        .padding(start = 60.dp, end = 8.dp, top = 2.dp, bottom = 2.dp)

                ) {
                    MessageRow(
                        text = message.message,
                        messageTime = sdf.format(message.date),
                        messageStatus = messageStatus
                    )
                }

            }
        }

        ChatInput(onMessageChange = { messageContent ->
            messages.add(
                ChatMessage(
                    (messages.size + 1).toLong(),
                    messageContent,
                    System.currentTimeMillis()
                )
            )
        })
    }
}


@Composable
private fun MessageRow(
    text: String,
    messageTime: String,
    messageStatus: MessageStatus
) {
    var color by remember {
        mutableStateOf(Blue400)
    }

    ChatFlexBoxLayout(
        modifier = Modifier
            .background(color, shape = RoundedCornerShape(8.dp))
            .padding(start = 2.dp, top = 2.dp, end = 4.dp, bottom = 2.dp),
        text = text,
        messageStat = {
            MessageTimeText(
                modifier = Modifier
                    .wrapContentSize()
                    .background(Color.Yellow)
                    .padding(end = 6.dp),
                messageTime = messageTime,
                messageStatus = messageStatus
            )
        },
        onMeasure = { chatRowData ->
            color = when (chatRowData.measuredType) {
                0 -> Orange400
                1 -> Pink400
                2 -> Green400
                else -> Blue400
            }
        }
    )
}
