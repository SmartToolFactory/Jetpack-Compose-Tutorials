package com.smarttoolfactory.tutorial1_1basics.chapter2_material_widgets

import android.widget.EditText
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CutCornerShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.smarttoolfactory.tutorial1_1basics.ui.components.StyleableTutorialText
import com.smarttoolfactory.tutorial1_1basics.ui.components.TutorialHeader
import com.smarttoolfactory.tutorial1_1basics.ui.components.TutorialText2

/**
 * TextField is composable that replaces [EditText] in classic Android Views.
 *
 * It's input component for entering text.
 *
 * ### Note
 * As of 1.0.1 it does not have Assistive Text, Error Text, Character Counter,
 * Prefix, and Suffix.
 */
@Preview
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

            StyleableTutorialText(
                text = "1-) **TextField** let users enter and edit text. remember is " +
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
                    placeholderColor = Color(0xffFFE082),
                ),

                textStyle = TextStyle(
                    color = Color.White,
                    fontStyle = FontStyle.Italic,
                    fontWeight = FontWeight.Bold
                )
            )

            TutorialText2(text = "Shape")
            Surface(
                // This sets text color for Text if not set in textFieldColors
                contentColor = Color.White,
                color = Color(0xffEC407A),
                modifier = fullWidthModifier,
                shape = RoundedCornerShape(topStart = 16.dp, bottomEnd = 16.dp)
            ) {
                TextField(
                    value = textFieldValue.value,
                    onValueChange = { newValue ->
                        textFieldValue.value = newValue
                    },
                    placeholder = { Text("Search") },
                    colors = TextFieldDefaults.textFieldColors(
                        // 🔥 Removes bottom indicator line
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                        disabledIndicatorColor = Color.Transparent
                    )
                )
            }

            TextField(
                modifier = fullWidthModifier.clip(CutCornerShape(8.dp)),
                value = textFieldValue.value,
                label = { Text("Label") },
                placeholder = { Text("No Bottom Line") },
                onValueChange = { newValue ->
                    textFieldValue.value = newValue
                },
                colors = TextFieldDefaults.textFieldColors(
                    backgroundColor = Color(0xff607D8B),
                    placeholderColor = Color(0xffFFEA00),
                    unfocusedLabelColor = Color(0xff795548),
                    focusedLabelColor = Color(0xff66BB6A),
                    cursorColor = Color(0xffE1F5FE),
                    // 🔥 Removes bottom indicator line
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    disabledIndicatorColor = Color.Transparent,
                ),
                textStyle = TextStyle(
                    color = Color.White
                )
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
                    unfocusedLabelColor = Color(0xff795548),
                    focusedLabelColor = Color(0xff66BB6A),
                    errorLabelColor = Color(0xffFF1744),
                    unfocusedIndicatorColor = Color(0xffEF9A9A),
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

            StyleableTutorialText(
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

            StyleableTutorialText(text = "3-) TextField can have leading and trailing icons.")
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

            var searchQuery by remember { mutableStateOf("") }

            Surface(
                modifier = fullWidthModifier,
                shape = RoundedCornerShape(percent = 40),
                border = BorderStroke(1.dp, Color(0xff90A4AE)),
            ) {
                TextField(
                    value = searchQuery,
                    placeholder = { Text("Search") },
                    leadingIcon = {
                        if (searchQuery.isEmpty()) {
                            Icon(
                                imageVector = Icons.Default.Search,
                                contentDescription = null
                            )
                        }
                    },
                    onValueChange = { newValue ->
                        searchQuery = newValue
                    },
                    colors = TextFieldDefaults.textFieldColors(
                        backgroundColor = Color(0xffF5F5F5),
                        // 🔥 Removes bottom indicator line
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                        disabledIndicatorColor = Color.Transparent,
                    ),
                    singleLine = true,
                )
            }

            val emailText2 = remember { mutableStateOf(TextFieldValue("")) }

            OutlinedTextField(
                modifier = fullWidthModifier,
                value = emailText2.value,
                label = { Text("Email") },
                placeholder = { Text(text = "") },
                onValueChange = { newValue ->
                    emailText2.value = newValue
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

            StyleableTutorialText(
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

            val keyboardController = LocalSoftwareKeyboardController.current

            OutlinedTextField(
                modifier = fullWidthModifier,
                value = searchText.value,
                onValueChange = { newValue ->
                    searchText.value = newValue
                },
                label = { Text("Search onImeActionPerformed") },
                placeholder = { Text(text = "") },

                singleLine = true,
                keyboardOptions = KeyboardOptions(
                    capitalization = KeyboardCapitalization.Characters,
                    imeAction = ImeAction.Go,
                ),

                keyboardActions = KeyboardActions(onGo = {

                    keyboardController?.hide()

                    Toast.makeText(
                        context,
                        "ImeAction performed onGo " +
                                ", and keyboardController?.hide()",
                        Toast.LENGTH_SHORT
                    ).show()
                })
            )


            StyleableTutorialText(
                text = "5-) With **VisualTransformation** and Regex it's possible to " +
                        "transform text based on a format such as masked chars, phone " +
                        ", currency or credit card."
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


            val phoneText = remember { mutableStateOf(TextFieldValue("")) }
            val maxChar = 10

            OutlinedTextField(
                modifier = fullWidthModifier,
                value = phoneText.value,
                label = { Text("Phone") },
                placeholder = { Text(text = "") },
                onValueChange = { newValue ->
                    if (newValue.text.length <= maxChar) phoneText.value = newValue
                },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                maxLines = 1,
                visualTransformation = PhoneVisualTransformation()
            )

            val creditCardText = remember { mutableStateOf(TextFieldValue("")) }
            val maxCharCreditCard = 16
            // this for entering number only
            val numberRegex = "^[0-9]+\$".toRegex()

            OutlinedTextField(
                modifier = fullWidthModifier,
                value = creditCardText.value,
                label = { Text("Credit Card") },
                placeholder = { Text(text = "") },
                onValueChange = { newValue ->
                    val text = newValue.text
                    if (text.length <= maxCharCreditCard && numberRegex.matches(text)) {
                        creditCardText.value = newValue
                    }
                },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                maxLines = 1,
                visualTransformation = CreditCardVisualTransformation()
            )

            StyleableTutorialText(
                text = "6-) Basic composable that enables users to edit text via hardware " +
                        "or software keyboard, but provides no decorations like hint or placeholder."
            )

            Surface(
                modifier = fullWidthModifier.padding(8.dp),
                shape = MaterialTheme.shapes.small,
                color = Color(0xff90A4AE)
            ) {
                var basicText by remember { mutableStateOf("BasicTextField") }
                BasicTextField(
                    modifier = Modifier.padding(8.dp),
                    value = basicText,
                    onValueChange = { newValue ->
                        basicText = newValue
                    })

            }
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

/**
 * VisualTransformation that transforms user input into in appearance, actual text is not
 * as displayed.
 * ```
 * XXX-XXX-XXXX
 * ```
 */
class PhoneVisualTransformation : VisualTransformation {

    override fun filter(text: AnnotatedString): TransformedText {
        //
        // Making XXX-XXX-XXXX
        val trimmed = if (text.text.length >= 10) text.text.substring(0..9) else text.text

        var output = ""
        for (i in trimmed.indices) {
            output += trimmed[i]
            if (i % 3 == 2 && i != 8) output += "-"
        }

        println("PhoneVisualTransformation text: $text, trimmed: $trimmed, output: $output")


        return TransformedText(AnnotatedString(output), phoneOffsetMap)
    }

    private val phoneOffsetMap = object : OffsetMapping {

        override fun originalToTransformed(offset: Int): Int {

            // XXX
            if (offset <= 2) return offset
            // XXXXXX(5th) is transformed to XXX-XXX
            if (offset <= 5) return offset + 1
            // XXXXXXXXXX(5th to 9th) is transformed to XXX-XXX
            if (offset <= 9) return offset + 2

            // Number of chars in XXX-XXX-XXXX
            return 12
        }

        override fun transformedToOriginal(offset: Int): Int {

            println("🔥 transformedToOriginal() offset: $offset")
            // indexes of -
            // XXX
            if (offset <= 2) return offset
            // XXX-XXX
            if (offset <= 6) return offset - 1
            // XXX-XXX-XXXX
            if (offset <= 11) return offset - 2
            return 10
        }

    }
}

/**
 * VisualTransformation that transforms user input into in appearance, actual text is not
 * as displayed.
 * ```
 * XXXX-XXXX-XXXX
 * ```
 */
class CreditCardVisualTransformation : VisualTransformation {
    override fun filter(text: AnnotatedString): TransformedText {
        return creditCardFilter(text)
    }
}

fun creditCardFilter(text: AnnotatedString): TransformedText {

    // Making XXXX-XXXX-XXXX-XXXX string.
    val trimmed = if (text.text.length >= 16) text.text.substring(0..15) else text.text
    var out = ""
    for (i in trimmed.indices) {
        out += trimmed[i]
        if (i % 4 == 3 && i < 15) out += "-"
    }

    /**
     * The offset translator should ignore the hyphen characters, so conversion from
     *  original offset to transformed text works like
     *  - The 4th char of the original text is 5th char in the transformed text.
     *  - The 13th char of the original text is 15th char in the transformed text.
     *  Similarly, the reverse conversion works like
     *  - The 5th char of the transformed text is 4th char in the original text.
     *  - The 12th char of the transformed text is 10th char in the original text.
     */
    val creditCardOffsetTranslator = object : OffsetMapping {
        override fun originalToTransformed(offset: Int): Int {
            if (offset <= 3) return offset
            if (offset <= 7) return offset + 1
            if (offset <= 11) return offset + 2
            if (offset <= 16) return offset + 3
            return 19
        }

        override fun transformedToOriginal(offset: Int): Int {
            if (offset <= 4) return offset
            if (offset <= 9) return offset - 1
            if (offset <= 14) return offset - 2
            if (offset <= 19) return offset - 3
            return 16
        }
    }

    return TransformedText(AnnotatedString(out), creditCardOffsetTranslator)
}

/**
 * A sample for displaying label, placeholder and clickability with enabled or and readOnly
 * param variations
 */
@Preview
@Composable
private fun TextFieldDisableTest() {
    var value by remember {
        mutableStateOf("")
    }

    val context = LocalContext.current

    Column(modifier = Modifier.fillMaxSize()) {
        TextField(
            modifier = Modifier
                .fillMaxWidth()
                .clickable {
                    Toast
                        .makeText(
                            context,
                            "Clicked",
                            Toast.LENGTH_SHORT
                        )
                        .show()
                },
            value = value,
            onValueChange = { value = it },
            placeholder = {
                Text("Placeholder", modifier = Modifier.border(1.dp, Color.Red))
            },
            label = {
                Text("Label", Modifier.border(1.dp, Color.Yellow))
            }
        )
        Spacer(modifier = Modifier.height(20.dp))

        OutlinedTextField(
            modifier = Modifier
                .fillMaxWidth()
                .clickable {
                    Toast
                        .makeText(
                            context,
                            "Clicked",
                            Toast.LENGTH_SHORT
                        )
                        .show()
                },
            value = value,
            onValueChange = { value = it },
            placeholder = {
                Text("Placeholder", modifier = Modifier.border(1.dp, Color.Red))
            },
            label = {
                Text("Label", Modifier.border(1.dp, Color.Yellow))
            }
        )
        Spacer(modifier = Modifier.height(20.dp))
        OutlinedTextField(
            modifier = Modifier
                .fillMaxWidth()
                .clickable {
                    Toast
                        .makeText(
                            context,
                            "Clicked",
                            Toast.LENGTH_SHORT
                        )
                        .show()
                },
            value = value,
            onValueChange = {},
            label = {
                Text("Label", Modifier.border(1.dp, Color.Yellow))
            },
            placeholder = {
                Text("Placeholder", modifier = Modifier.border(1.dp, Color.Red))
            },
            readOnly = true
        )

        Spacer(modifier = Modifier.height(20.dp))

        OutlinedTextField(
            modifier = Modifier
                .fillMaxWidth()
                .clickable {
                    Toast
                        .makeText(
                            context,
                            "Clicked",
                            Toast.LENGTH_SHORT
                        )
                        .show()
                },
            value = value,
            onValueChange = {},
            label = {
                Text("Label", Modifier.border(1.dp, Color.Yellow))
            },
            placeholder = {
                Text("Placeholder", modifier = Modifier.border(1.dp, Color.Red))
            },
            enabled = false
        )

        Spacer(modifier = Modifier.height(20.dp))

        OutlinedTextField(
            modifier = Modifier
                .fillMaxWidth()
                .clickable {
                    Toast
                        .makeText(
                            context,
                            "Clicked",
                            Toast.LENGTH_SHORT
                        )
                        .show()
                },
            value = value,
            onValueChange = {},
            label = {
                Text("Label", Modifier.border(1.dp, Color.Yellow))
            },
            placeholder = {
                Text("Placeholder", modifier = Modifier.border(1.dp, Color.Red))
            },
            enabled = false,
            readOnly = true
        )
    }
}