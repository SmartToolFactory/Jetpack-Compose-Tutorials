package com.smarttoolfactory.tutorial1_1basics.chapter2_material_widgets

import android.widget.EditText
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Email
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.SoftwareKeyboardController
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.*
import androidx.compose.ui.unit.dp
import com.smarttoolfactory.tutorial1_1basics.components.TutorialHeader
import com.smarttoolfactory.tutorial1_1basics.components.TutorialText
import com.smarttoolfactory.tutorial1_1basics.components.TutorialText2

// TODO Add Filtering and Regex
/**
 * TextField is composable that replaces [EditText] in classic Android Views.
 *
 * It's input component for entering text.
 *
 * ### Note
 * As of 1.0.0-alpha-09 it does not have Assitive Text, Error Text, Character Counter,
 * Prefix, and Suffix.
 */
@ExperimentalComposeUiApi
@Composable
fun Tutorial2_3Screen() {
    TutorialContent()
}

@ExperimentalComposeUiApi
@Composable
private fun TutorialContent() {

    LazyColumn(Modifier.fillMaxSize()) {

        item {


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

            TutorialText2(text = "Error")

            val errorText = remember { mutableStateOf(TextFieldValue("Don't leave blank")) }

            TextField(
                modifier = fullWidthModifier,
                value = errorText.value,
                onValueChange = { newValue ->
                    errorText.value = newValue
                },
                label = {
                    Text(text = "")
                },
                placeholder = { Text("Placeholder") },
                isError = errorText.value.text.isEmpty(),
            )


            TutorialText2(text = "Colors")

            TextField(
                modifier = fullWidthModifier,
                value = textFieldValue.value,
                onValueChange = { newValue ->
                    textFieldValue.value = newValue
                },
                label = { Text("Label") },
                placeholder = { Text("Placeholder") },
                colors = TextFieldDefaults.textFieldColors(
                    backgroundColor = Color(0xffFFD54F),
                    disabledTextColor = Color(0xff42A5F5),
                    errorLabelColor = Color(0xff2E7D32),
                    disabledLabelColor = Color(0xff42A5F5)
                )
            )

            TutorialText2(text = "Colors & Text Style")
            TextField(
                modifier = fullWidthModifier,
                value = textFieldValue.value,
                label = { Text("Label") },
                placeholder = { Text("Placeholder") },
                onValueChange = { newValue ->
                    textFieldValue.value = newValue
                },
                colors = TextFieldDefaults.textFieldColors(

                    backgroundColor = Color(0xff039BE5),
                    disabledTextColor = Color(0xff42A5F5),
                    errorLabelColor = Color(0xff2E7D32),
                    focusedLabelColor = Color(0xffAEEA00),
                    placeholderColor =  Color(0xffFFE082),
                ),

                textStyle = TextStyle(
                    color = Color.White,
                    fontStyle = FontStyle.Italic,
                    fontWeight = FontWeight.Bold
                )
            )

            TutorialText2(text = "Shape")

            Surface(
                modifier = fullWidthModifier,
                shape = RoundedCornerShape(25),
                elevation = 2.dp,
                border = BorderStroke(1.dp, Color.LightGray)
            ) {
                TextField(
                    value = textFieldValue.value,
                    onValueChange = { newValue ->
                        textFieldValue.value = newValue
                    },
                    placeholder = { Text("Search") },
                    colors = TextFieldDefaults.textFieldColors(
                        backgroundColor = Color.White
                    )
                )
            }

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
                onValueChange = { newValue ->
                    textFieldValue.value = newValue
                },
                label = { Text("Label") },
                placeholder = { Text("Placeholder") },

                colors = TextFieldDefaults.textFieldColors(
                textColor = Color.Blue,
                    backgroundColor = Color.Yellow,
                    placeholderColor = Color(0xffFFF176),
                    unfocusedLabelColor = Color(0xff43A047),
                    focusedLabelColor = Color(0xff66BB6A),
                    errorLabelColor = Color(0xffFFEB3B),
                    unfocusedIndicatorColor = Color(0xffFF5722),
                    focusedIndicatorColor = Color(0xff1976D2)
                )
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
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Email,
                        contentDescription = null
                    )
                }
            )

            OutlinedTextField(
                modifier = fullWidthModifier,
                value = emailText.value,
                label = { Text("Email") },
                placeholder = { Text(text = "") },
                onValueChange = { newValue ->
                    emailText.value = newValue
                },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Email,
                        contentDescription = null
                    )
                },
                trailingIcon = {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = null
                    )
                }
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

            val context = LocalContext.current

            lateinit var softwareKeyboardController: SoftwareKeyboardController

//            OutlinedTextField(
//                modifier = fullWidthModifier,
//                value = searchText.value,
//                onValueChange = { newValue ->
//                    searchText.value = newValue
//                },
//                label = { Text("Search onImeActionPerformed") },
//                placeholder = { Text(text = "") },
//
//                onTextInputStarted = { softwareKeyboardController = it },
//                singleLine = true,
//                keyboardOptions = KeyboardOptions(
//                    capitalization = KeyboardCapitalization.Characters,
//                    imeAction = ImeAction.Go,
//                ),
//
//                keyboardActions = KeyboardActions(onGo = {
//
//                    softwareKeyboardController.hideSoftwareKeyboard()
//
//                    Toast.makeText(
//                        context,
//                        "ImeAction performed onGo " +
//                                ", and softwareKeyboardController.hideSoftwareKeyboard()",
//                        Toast.LENGTH_SHORT
//                    ).show()
//                })
//            )


            TutorialText(
                text = "5-) With VisualTransformation and Regex it's possible to " +
                        "transform text based on a format such as masked chars, phone " +
                        "or currency."
            )

            val maskText = remember { mutableStateOf(TextFieldValue("")) }
            OutlinedTextField(
                modifier = fullWidthModifier,
                value = maskText.value,
                label = { Text("Mask Chars") },
                placeholder = { Text(text = "") },
                onValueChange = { newValue ->
                    maskText.value = newValue
                },
                singleLine = true,
                visualTransformation = PasswordMaskTransformation()
            )

            Spacer(modifier = Modifier.padding(bottom = 32.dp))
        }
    }
}

/**
 * VisualTransformation transforms [AnnotatedString] to required format.
 */
class PasswordMaskTransformation : VisualTransformation {

    override fun filter(text: AnnotatedString): TransformedText {
        return TransformedText(
            AnnotatedString(text.text.replace(".".toRegex(), "!")),
            maskOffsetMap
        )
    }

    private val maskOffsetMap = object : OffsetMapping {
        override fun originalToTransformed(offset: Int) = offset
        override fun transformedToOriginal(offset: Int) = offset
    }
}



