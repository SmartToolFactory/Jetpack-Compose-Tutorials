package com.smarttoolfactory.tutorial1_1basics.chapter2_material_widgets

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.compose.ui.window.SecureFlagPolicy
import com.smarttoolfactory.tutorial1_1basics.R
import com.smarttoolfactory.tutorial1_1basics.ui.components.StyleableTutorialText
import com.smarttoolfactory.tutorial1_1basics.ui.components.TutorialHeader
import com.smarttoolfactory.tutorial1_1basics.ui.components.TutorialText2

@Composable
fun Tutorial2_12Screen() {
    TutorialContent()
}

@Composable
private fun TutorialContent() {

    // To show dialog we set one of the flags to true and set it back to false
    // in onDismissRequest function of dialog.
    var showAlertDialog by remember { mutableStateOf(false) }
    var showAlertDialogWithStyle by remember { mutableStateOf(false) }
    var showDialog by remember { mutableStateOf(false) }
    var showCustomDialog by remember { mutableStateOf(false) }
    var showCustomDialogWithResult by remember { mutableStateOf(false) }

    val context = LocalContext.current

    LazyColumn(
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {

        item {

            TutorialHeader(text = "AlertDialog")
            StyleableTutorialText(
                text = "1-) Alert dialogs interrupt users with urgent information, details, or actions."
            )

            OutlinedButton(
                modifier = Modifier.fillMaxSize(),
                onClick = {
                    showAlertDialog = !showAlertDialog
                }) {
                Text("AlertDialog")

                if (showAlertDialog) {
                    AlertDialogExample {
                        showAlertDialog = !showAlertDialog
                    }
                }
            }
        }

        item {
            OutlinedButton(
                modifier = Modifier.fillMaxSize(),
                onClick = {
                    showAlertDialogWithStyle = !showAlertDialogWithStyle
                }) {
                Text("AlertDialog with Style")

                if (showAlertDialogWithStyle) {
                    AlertDialogExample2 {
                        showAlertDialogWithStyle = !showAlertDialogWithStyle
                    }
                }
            }
        }

        item {

            TutorialHeader(text = "Dialog")
            StyleableTutorialText(
                text = "2-) Unlike **AlertDialog**, **Dialog** does not have slots fo " +
                        "**dismissButton, confirmButton, or buttons**. " +
                        "Allows customization of everything inside it."
            )
            OutlinedButton(
                modifier = Modifier.fillMaxSize(),
                onClick = {
                    showDialog = !showDialog
                }) {
                Text("Dialog")

                if (showDialog) {
                    DialogExample {
                        showDialog = !showDialog
                    }
                }
            }

            TutorialText2(text = "Custom Dialog")

            OutlinedButton(
                modifier = Modifier.fillMaxSize(),
                onClick = {
                    showCustomDialog = !showCustomDialog
                }) {

                Text("Custom Dialog")

                if (showCustomDialog) {
                    CustomDialogExample(
                        onDismiss = {
                            showCustomDialog = !showCustomDialog
                            Toast.makeText(context, "Dialog dismissed!", Toast.LENGTH_SHORT)
                                .show()
                        },
                        onNegativeClick = {
                            showCustomDialog = !showCustomDialog
                            Toast.makeText(context, "Negative Button Clicked!", Toast.LENGTH_SHORT)
                                .show()

                        },
                        onPositiveClick = {
                            showCustomDialog = !showCustomDialog
                            Toast.makeText(context, "Positive Button Clicked!", Toast.LENGTH_SHORT)
                                .show()
                        }
                    )
                }
            }
        }

        item {
            TutorialText2(text = "Custom Dialog with Result")
            OutlinedButton(
                modifier = Modifier.fillMaxSize(),
                onClick = {
                    showCustomDialogWithResult = !showCustomDialogWithResult
                }) {

                Text("Custom Dialog with Result")

                if (showCustomDialogWithResult) {
                    CustomDialogWithResultExample(
                        onDismiss = {
                            showCustomDialogWithResult = !showCustomDialogWithResult
                            Toast.makeText(context, "Dialog dismissed!", Toast.LENGTH_SHORT)
                                .show()
                        },
                        onNegativeClick = {
                            showCustomDialogWithResult = !showCustomDialogWithResult
                            Toast.makeText(context, "Negative Button Clicked!", Toast.LENGTH_SHORT)
                                .show()

                        },
                        onPositiveClick = { color ->
                            showCustomDialogWithResult = !showCustomDialogWithResult
                            Toast.makeText(context, "Selected color: $color", Toast.LENGTH_SHORT)
                                .show()
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun AlertDialogExample(onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        dismissButton = {
            TextButton(
                onClick = onDismiss,
                modifier = Modifier
                    .padding(8.dp)
            ) {
                Text(text = "Cancel")
            }
        },
        confirmButton = {
            TextButton(
                onClick = onDismiss,
                modifier = Modifier
                    .padding(8.dp)
            ) {
                Text(text = "OK")
            }
        },
        title = {
            Text(text = "AlertDialog Title", fontSize = 18.sp, fontWeight = FontWeight.Bold)
        },
        text = {
            Text(text = dialogText)
        }
    )
}

@Composable
private fun AlertDialogExample2(onDismiss: () -> Unit) {
    // This example uses button Composable to create buttons instead of confirmButton and dismissButton

    AlertDialog(
        onDismissRequest = onDismiss,
        // Properties used to customize the behavior of dialog
        properties = DialogProperties(
            dismissOnBackPress = true,
            dismissOnClickOutside = false,
            securePolicy = SecureFlagPolicy.Inherit
        ),
        title = {
            Text("AlertDialog with Style", fontWeight = FontWeight.Bold)
        },
        text = {
            Text(text = "This dialog has buttons with custom style and aligned vertically as in Column. Properties set custom behaviour of a dialog such as dismissing when back button pressed or pressed outside of dialog")
        },
        buttons = {
            OutlinedButton(
                shape = RoundedCornerShape(percent = 30),
                onClick = onDismiss,
                modifier = Modifier
                    .padding(8.dp)
                    .fillMaxWidth()
            ) {
                Text(text = "Cancel")
            }
            Spacer(modifier = Modifier.width(8.dp))
            OutlinedButton(
                shape = RoundedCornerShape(percent = 30),
                onClick = onDismiss,
                colors = ButtonDefaults.outlinedButtonColors(
                    backgroundColor = Color(0xff8BC34A),
                    contentColor = Color.White
                ),
                modifier = Modifier
                    .padding(8.dp)
                    .fillMaxWidth()
            ) {
                Text(text = "OK")
            }
        }
    )
}

@Composable
private fun DialogExample(onDismiss: () -> Unit) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties()
    ) {
        Surface(elevation = 8.dp, shape = RoundedCornerShape(12.dp)) {
            Column(
                modifier = Modifier
                    .width(400.dp)
                    .wrapContentHeight()
                    .background(Color.White)
                    .padding(8.dp)
            ) {

                Text(
                    text = "Dialog Title",
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp,
                    modifier = Modifier.padding(8.dp)
                )

                CompositionLocalProvider(LocalContentAlpha provides ContentAlpha.medium) {
                    Text(dialogText, modifier = Modifier.padding(8.dp))
                }

                Spacer(modifier = Modifier.height(8.dp))
                DialogButtons(onDismiss)
            }
        }
    }
}

@Composable
private fun CustomDialogExample(
    onDismiss: () -> Unit,
    onNegativeClick: () -> Unit,
    onPositiveClick: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        val color = Color(0xff4DB6AC)
        Card(elevation = 8.dp, shape = RoundedCornerShape(12.dp)) {

            Column {

                // Header
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(160.dp)
                        .background(color)
                ) {
                    Icon(
                        tint = Color.White,
                        painter = painterResource(id = R.drawable.ic_baseline_location_on_48),
                        contentDescription = null,
                        modifier = Modifier
                            .graphicsLayer(scaleX = 1.2f, scaleY = 1.2f)
                            .align(
                                Alignment.Center
                            )
                    )
                }

                Column(modifier = Modifier.padding(16.dp)) {
                    CompositionLocalProvider(LocalContentAlpha provides ContentAlpha.medium) {
                        Text("To send a nearby place or your location, allow access to your location.")
                    }
                    Row(
                        horizontalArrangement = Arrangement.End,
                        modifier = Modifier.fillMaxWidth()
                    ) {

                        Text(
                            text = "NOT NOW", color = color,
                            modifier = Modifier
                                .clickable(
                                    interactionSource = MutableInteractionSource(),
                                    indication = rememberRipple(color = Color.DarkGray),
                                    onClick = onNegativeClick
                                )
                                .padding(8.dp)
                        )

                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "CONTINUE", color = color,
                            modifier = Modifier
                                .clickable(
                                    interactionSource = MutableInteractionSource(),
                                    indication = rememberRipple(color = Color.DarkGray),
                                    onClick = onPositiveClick
                                )
                                .padding(8.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun CustomDialogWithResultExample(
    onDismiss: () -> Unit,
    onNegativeClick: () -> Unit,
    onPositiveClick: (Color) -> Unit
) {
    var red by remember { mutableStateOf(0f) }
    var green by remember { mutableStateOf(0f) }
    var blue by remember { mutableStateOf(0f) }

    val color = Color(
        red = red.toInt(),
        green = green.toInt(),
        blue = blue.toInt(),
        alpha = 255
    )

    Dialog(onDismissRequest = onDismiss) {

        Card(
            elevation = 8.dp,
            shape = RoundedCornerShape(12.dp)
        ) {

            Column(modifier = Modifier.padding(8.dp)) {

                Text(
                    text = "Select Color",
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp,
                    modifier = Modifier.padding(8.dp)
                )
                Spacer(modifier = Modifier.height(8.dp))

                // Color Selection
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {


                    Column {

                        Text(text = "Red ${red.toInt()}")
                        Slider(
                            value = red,
                            onValueChange = { red = it },
                            valueRange = 0f..255f,
                            onValueChangeFinished = {}
                        )
                        Spacer(modifier = Modifier.height(8.dp))

                        Text(text = "Green ${green.toInt()}")
                        Slider(
                            value = green,
                            onValueChange = { green = it },
                            valueRange = 0f..255f,
                            onValueChangeFinished = {}
                        )
                        Spacer(modifier = Modifier.height(8.dp))

                        Text(text = "Blue ${blue.toInt()}")
                        Slider(
                            value = blue,
                            onValueChange = { blue = it },
                            valueRange = 0f..255f,
                            onValueChangeFinished = {}
                        )

                        Spacer(modifier = Modifier.height(8.dp))
                        Surface(
                            border = BorderStroke(1.dp, Color.DarkGray),
                            color = color,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(40.dp)
                        ) {}
                    }
                }

                // Buttons
                Row(
                    horizontalArrangement = Arrangement.End,
                    modifier = Modifier.fillMaxWidth()
                ) {

                    TextButton(onClick = onNegativeClick) {
                        Text(text = "CANCEL")
                    }
                    Spacer(modifier = Modifier.width(4.dp))
                    TextButton(onClick = {
                        onPositiveClick(color)
                    }) {
                        Text(text = "OK")
                    }
                }
            }
        }
    }
}

@Composable
private fun DialogButtons(onDismiss: () -> Unit) {
    Row {
        TextButton(
            onClick = onDismiss,
            modifier = Modifier.padding(8.dp)
        ) {
            Text(text = "Cancel")
        }
        TextButton(
            onClick = onDismiss,
            modifier = Modifier.padding(8.dp)
        ) {
            Text(text = "OK")
        }
    }
}

val dialogText = """
    Lorem Ipsum is simply dummy text of the printing and typesetting industry. 
    Lorem Ipsum has been the industry's standard dummy text ever since the 1500s, when an unknown 
    printer took a galley of type and scrambled it to make a type specimen book.
""".trimIndent()