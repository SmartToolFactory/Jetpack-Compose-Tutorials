package com.smarttoolfactory.tutorial1_1basics.chapter2_material_widgets

import android.widget.Toast
import androidx.compose.foundation.ScrollableColumn
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Icon
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Email
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.AmbientContext
import androidx.compose.ui.text.SoftwareKeyboardController
import androidx.compose.ui.text.input.*
import androidx.compose.ui.unit.dp
import com.smarttoolfactory.tutorial1_1basics.components.TutorialHeader
import com.smarttoolfactory.tutorial1_1basics.components.TutorialText
import com.smarttoolfactory.tutorial1_1basics.components.TutorialText2
import java.util.regex.Pattern

// TODO Add Filtering and Regex
@Composable
fun Tutorial2_3Screen() {
    TutorialContent()
}

@Composable
private fun TutorialContent() {

    ScrollableColumn(modifier = Modifier.fillMaxSize()) {

        val fullWidthModifier =
            Modifier
                .fillMaxWidth()
                .padding(8.dp)

        TutorialHeader(text = "TextField")

        TutorialText(
            text = "1-) Text fields let users enter and edit text. remember is " +
                    "used with MutableState to store state of text or TextFieldValue"
        )

        val textFieldValue = remember { mutableStateOf(TextFieldValue("")) }
        TextField(
            modifier = fullWidthModifier,
            value = textFieldValue.value,
            label = { Text("Label") },
            placeholder = { Text("Placeholder") },
            onValueChange = { newValue ->
                textFieldValue.value = newValue
            }
        )

        TutorialText2(text = "Colors")

        TextField(
            modifier = fullWidthModifier,
            value = textFieldValue.value,
            label = { Text("Label") },
            placeholder = { Text("Placeholder") },
            onValueChange = { newValue ->
                textFieldValue.value = newValue
            },
            activeColor = Color(0xffFF8F00),
            backgroundColor = Color(0xffFFD54F),
            inactiveColor = Color(0xff42A5F5),
            errorColor = Color(0xff2E7D32)
        )


        TutorialText2(text = "Outlined")

        OutlinedTextField(
            modifier = fullWidthModifier,
            value = textFieldValue.value,
            label = { Text("Label") },
            placeholder = { Text("Placeholder") },
            onValueChange = { newValue ->
                textFieldValue.value = newValue
            }
        )

        OutlinedTextField(
            modifier = fullWidthModifier,
            value = textFieldValue.value,
            label = { Text("Label") },
            placeholder = { Text("Placeholder") },
            onValueChange = { newValue ->
                textFieldValue.value = newValue
            },
            activeColor = Color(0xff7B1FA2),
            inactiveColor = Color(0xff66BB6A),
            errorColor = Color(0xffFFEB3B)
        )

        TutorialText2(text = "Single Line and Line Height")

        OutlinedTextField(
            modifier = fullWidthModifier,
            value = textFieldValue.value,
            label = { Text("Single Line") },
            placeholder = { Text("Placeholder") },
            onValueChange = { newValue ->
                textFieldValue.value = newValue
            },
            singleLine = true
        )

        OutlinedTextField(
            modifier = fullWidthModifier,
            value = textFieldValue.value,
            label = { Text("Max Lines 2") },
            placeholder = { Text("Placeholder") },
            onValueChange = { newValue ->
                textFieldValue.value = newValue
            },
            maxLines = 2
        )

        TutorialText(
            text = "2-) Keyboard options change the type of TextField. For instance " +
                    "PasswordVisualTransformation" +
                    "transforms that TextField to password input area"
        )
        TutorialText2(text = "keyboardOptions")

        OutlinedTextField(
            modifier = fullWidthModifier,
            value = textFieldValue.value,
            label = { Text("KeyboardType.Password") },
            placeholder = { Text(text = "123456789") },
            onValueChange = { newValue ->
                textFieldValue.value = newValue
            },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            visualTransformation = PasswordVisualTransformation()
        )

        val phoneNumberText = remember { mutableStateOf(TextFieldValue("")) }

        OutlinedTextField(
            modifier = fullWidthModifier,
            value = phoneNumberText.value,
            label = { Text("KeyboardType.Phone") },
            placeholder = { Text(text = "555-555-5555") },
            onValueChange = { newValue ->
                phoneNumberText.value = newValue
            },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone)
        )

        TutorialText(text = "3-) TextField can have leading and trailing icons.")
        TutorialText2(text = "Leading and Trailing Icons")

        val emailText = remember { mutableStateOf(TextFieldValue("")) }
        OutlinedTextField(
            modifier = fullWidthModifier,
            value = emailText.value,
            label = { Text("Email") },
            placeholder = { Text(text = "") },
            onValueChange = { newValue ->
                emailText.value = newValue
            },
            leadingIcon = { Icon(imageVector = Icons.Default.Email) }
        )

        OutlinedTextField(
            modifier = fullWidthModifier,
            value = emailText.value,
            label = { Text("Email") },
            placeholder = { Text(text = "") },
            onValueChange = { newValue ->
                emailText.value = newValue
            },
            leadingIcon = { Icon(imageVector = Icons.Default.Email) },
            trailingIcon = { Icon(imageVector = Icons.Default.Edit) }
        )

        TutorialText(
            text = "4-) Changing IME action changes icon/text at bottom right, " +
                    "action to be performed when that button is clicked"
        )
        TutorialText2(text = "IME Icons and Actions")

        val searchText = remember { mutableStateOf(TextFieldValue("")) }
        OutlinedTextField(
            modifier = fullWidthModifier,
            value = searchText.value,
            label = { Text("Search") },
            placeholder = { Text(text = "") },
            onValueChange = { newValue ->
                searchText.value = newValue
            },
            singleLine = true,
            keyboardOptions = KeyboardOptions(
                capitalization = KeyboardCapitalization.Words,
                autoCorrect = true,
                imeAction = ImeAction.Search
            )
        )

        val context = AmbientContext.current
        OutlinedTextField(
            modifier = fullWidthModifier,
            value = searchText.value,
            label = { Text("Search onImeActionPerformed") },
            placeholder = { Text(text = "") },
            onValueChange = { newValue ->
                searchText.value = newValue
            },
            singleLine = true,
            keyboardOptions = KeyboardOptions(
                capitalization = KeyboardCapitalization.Characters,
                imeAction = ImeAction.Go
            ),
            onImeActionPerformed = { imeAction: ImeAction,
                                     softwareKeyboardController: SoftwareKeyboardController? ->

                softwareKeyboardController?.hideSoftwareKeyboard()
                Toast.makeText(
                    context,
                    "ImeAction performed: $imeAction" +
                            ", and softwareKeyboardController.hideSoftwareKeyboard()",
                    Toast.LENGTH_SHORT
                ).show()
            }
        )
    }
}