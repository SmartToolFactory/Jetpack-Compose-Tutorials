package com.smarttoolfactory.tutorial1_1basics.chapter2_material_widgets

import android.widget.Toast
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredWidth
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.shape.CutCornerShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Checkbox
import androidx.compose.material.CheckboxDefaults
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.LinearProgressIndicator
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedButton
import androidx.compose.material.ProgressIndicatorDefaults
import androidx.compose.material.RadioButton
import androidx.compose.material.RadioButtonDefaults
import androidx.compose.material.RangeSlider
import androidx.compose.material.Slider
import androidx.compose.material.SliderDefaults
import androidx.compose.material.Snackbar
import androidx.compose.material.Switch
import androidx.compose.material.SwitchDefaults
import androidx.compose.material.Text
import androidx.compose.material.TriStateCheckbox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.state.ToggleableState
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.smarttoolfactory.tutorial1_1basics.isInPreview
import com.smarttoolfactory.tutorial1_1basics.ui.components.StyleableTutorialText
import com.smarttoolfactory.tutorial1_1basics.ui.components.TutorialHeader
import com.smarttoolfactory.tutorial1_1basics.ui.components.TutorialText2
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.flow

@ExperimentalMaterialApi
@Preview(showBackground = true)
@Composable
fun Tutorial2_11Screen() {
    TutorialContent()
}

@ExperimentalMaterialApi
@Composable
private fun TutorialContent() {

    val scrollState = rememberScrollState()
    Column(
        modifier = Modifier
            .padding(start = 8.dp, top = 8.dp, end = 8.dp, bottom = 16.dp)
            .verticalScroll(scrollState),
        verticalArrangement = Arrangement.Center
    ) {
        SnackBarExample()
        ProgressIndicatorExample()
        CheckboxExample()
        SwitchExample()
        RadioButtonExample()
        SliderExample()
    }
}

@Composable
private fun SnackBarExample() {
    TutorialHeader(text = "SnackBar")
    StyleableTutorialText(
        text = "1-) **Snackbar** provides brief messages about app processes at the bottom of the screen."
    )

    TutorialText2(text = "Basic SnackBar")
    Snackbar(modifier = Modifier.padding(4.dp)) {
        Text("Basic Snackbar")
    }

    TutorialText2(text = "Action SnackBar")
    val context = LocalContext.current
    val isInPreview = isInPreview
    Snackbar(modifier = Modifier.padding(4.dp),
        action = {
            Text(
                text = "Action",
                modifier = Modifier.clickable {
                    if (!isInPreview) {
                        Toast.makeText(context, "Action is clicked", Toast.LENGTH_SHORT).show()
                    }
                })
        }) {
        Text("Action Snackbar")
    }

    TutorialText2(text = "actionOnNewLine SnackBar")
    Snackbar(modifier = Modifier.padding(4.dp),
        actionOnNewLine = true,
        action = {
            Text(text = "Action",
                color = Color(0xffCE93D8),
                modifier = Modifier.clickable {
                    if (!isInPreview) {
                        Toast.makeText(context, "Action is clicked", Toast.LENGTH_SHORT).show()
                    }
                }
            )
        }) {
        Text("Action on new line Snackbar")
    }

    TutorialText2(text = "Snackbar Style")
    Snackbar(modifier = Modifier.padding(4.dp),
        shape = CutCornerShape(topStart = 8.dp),
        elevation = 2.dp,
//            backgroundColor = SnackbarDefaults.backgroundColor,
        backgroundColor = Color(0xffFFC107),
//            contentColor = MaterialTheme.colors.surface,
        contentColor = Color(0xffEC407A),
        action = {
            Text(
                text = "Action",
                modifier = Modifier.clickable {
                    if (!isInPreview) {
                        Toast.makeText(context, "Action is clicked", Toast.LENGTH_SHORT).show()
                    }
                })
        }) {
        Text("Snackbar with custom shape and colors")
    }

    Snackbar(modifier = Modifier.padding(4.dp),
        shape = RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp),
        elevation = 1.dp,
        backgroundColor = Color(0xff4CAF50),
        contentColor = Color(0xffFFFF00),
        action = {
            Text(
                text = "Action",
                color = Color(0xffD32F2F),
                fontWeight = FontWeight.Bold,
                modifier = Modifier.clickable {
                    if (!isInPreview) {
                        Toast.makeText(context, "Action is clicked", Toast.LENGTH_SHORT).show()
                    }
                })
        }) {
        Text("Snackbar with custom shape and colors")
    }
}

@Composable
private fun ProgressIndicatorExample() {
    TutorialHeader(text = "ProgressIndicator")
    StyleableTutorialText(
        text = "2-) Progress indicators express an unspecified wait time or display the length of a process."
    )

    TutorialText2("Indeterminate progress")
    CircularProgressIndicator()
    Spacer(modifier = Modifier.height(8.dp))
    LinearProgressIndicator()
    Spacer(modifier = Modifier.height(8.dp))



    TutorialText2("Determinate progress")
    val progress: Int by progressFlow.collectAsState(initial = 0)
    CircularProgressIndicator(
        progress = progress / 100f,
        strokeWidth = 4.dp,
        color = Color(0xffF44336)
    )
    Spacer(modifier = Modifier.height(8.dp))
    LinearProgressIndicator(
        modifier = Modifier
            .fillMaxWidth()
            .height(8.dp),
        progress = progress / 100f,
        backgroundColor = Color(0xff2196F3)
    )

    TutorialText2("Animated Progress")
    var progressAnimated by remember { mutableStateOf(0.1f) }

    val animatedProgress by animateFloatAsState(
        targetValue = progressAnimated,
        animationSpec = ProgressIndicatorDefaults.ProgressAnimationSpec
    )
    Row(verticalAlignment = Alignment.CenterVertically) {
        OutlinedButton(
            onClick = {
                if (progressAnimated < 1f) progressAnimated += 0.1f
            }
        ) {
            Text("Increase")
        }
        Spacer(Modifier.requiredWidth(30.dp))
        LinearProgressIndicator(progress = animatedProgress)
    }
}

@Composable
private fun CheckboxExample() {
    TutorialHeader(text = "Checkbox")
    StyleableTutorialText(
        text = "2-) Progress indicators express an unspecified wait time or display the " +
                "length of a process. **TriStateCheckbox** can be used to set child checkboxes."
    )

    TutorialText2("Checkbox")
    var checkBoxState by remember { mutableStateOf(false) }
    Checkbox(
        modifier = Modifier.padding(8.dp),
        checked = checkBoxState,
        onCheckedChange = {
            checkBoxState = it
        })
    Spacer(modifier = Modifier.height(8.dp))

    var checkBoxState2 by remember { mutableStateOf(false) }

    CheckBoxWithText("Checkbox with Text", checkBoxState2) {
        checkBoxState2 = it
    }

    var checkBoxState3 by remember { mutableStateOf(false) }

    CheckBoxWithTextRippleFullRow("Checkbox with Text and ripple", checkBoxState3) {
        checkBoxState3 = it
    }

    TutorialText2("TriStateCheckbox")

    // Parent and children checkboxes with TriStateCheckbox
    Column(modifier = Modifier.padding(8.dp)) {
        // define dependent checkboxes states
        val (state, onStateChange) = remember { mutableStateOf(true) }
        val (state2, onStateChange2) = remember { mutableStateOf(true) }

        // TriStateCheckbox state reflects state of dependent checkboxes
        val parentState = remember(state, state2) {
            if (state && state2) ToggleableState.On
            else if (!state && !state2) ToggleableState.Off
            else ToggleableState.Indeterminate
        }
        // click on TriStateCheckbox can set state for dependent checkboxes
        val onParentClick = {
            val s = parentState != ToggleableState.On
            onStateChange(s)
            onStateChange2(s)
        }

        Spacer(modifier = Modifier.width(16.dp))
        Row {
            // 🔥 Tri state
            TriStateCheckbox(
                state = parentState,
                onClick = onParentClick,
                colors = CheckboxDefaults.colors(
                    checkedColor = MaterialTheme.colors.primary
                )
            )

            Spacer(modifier = Modifier.width(8.dp))
            Text(text = "Additions")
        }
        Spacer(Modifier.height(8.dp))
        Column(Modifier.padding(10.dp, 0.dp, 0.dp, 0.dp)) {
            CheckBoxWithText(label = "Pickles", state = state, onStateChange = onStateChange)
            Spacer(Modifier.height(8.dp))
            CheckBoxWithText(label = "Tomato", state = state2, onStateChange = onStateChange2)
        }
    }
}

@Composable
private fun SwitchExample() {

    TutorialHeader(text = "Switch")
    StyleableTutorialText(
        text = "3-) **Switch** toggles the state of a single item on or off. " +
                "**enabled** flag set to false on the ones in right half."
    )

    val switchColors = SwitchDefaults.colors(
        checkedThumbColor = Color(0xffF44336),
        checkedTrackColor = Color(0xff76FF03),
        checkedTrackAlpha = 0.54f,
        uncheckedThumbColor = Color(0xff9C27B0),
        uncheckedTrackColor = Color(0xff3F51B5),
        uncheckedTrackAlpha = 0.38f,
        disabledCheckedThumbColor = Color(0xff212121),
        disabledCheckedTrackColor = Color(0xff616161),
        disabledUncheckedThumbColor = Color(0xff607D8B),
        disabledUncheckedTrackColor = Color(0xff795548)
    )

    var isSwitched by remember { mutableStateOf(true) }
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
        Switch(checked = isSwitched, onCheckedChange = { isSwitched = it })
        Switch(
            checked = isSwitched,
            onCheckedChange = { isSwitched = it },
            colors = switchColors
        )
        Switch(
            enabled = false,
            colors = switchColors,
            checked = false,
            onCheckedChange = { isSwitched = it },
        )

        Switch(
            enabled = false,
            colors = switchColors,
            checked = true,
            onCheckedChange = { isSwitched = it },
        )
    }
}

@Composable
private fun RadioButtonExample() {


    TutorialHeader(text = "RadioButton")
    StyleableTutorialText(
        text = "4-) **RadioButton** allow users to select one option from a set."
    )

    var isRadioSelected by remember { mutableStateOf(true) }

    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {

        // Enabled RadioButtons

        RadioButton(selected = isRadioSelected, onClick = { isRadioSelected = !isRadioSelected })
        RadioButton(
            selected = isRadioSelected,
            onClick = { isRadioSelected = !isRadioSelected },
            colors = RadioButtonDefaults.colors(
                selectedColor = Color(0xffE91E63),
                unselectedColor = Color(0xffFFEB3B),
                disabledColor = Color(0xff607D8B)
            )
        )

        // Disabled RadioButtons

        RadioButton(
            enabled = false,
            selected = false,
            onClick = {},
            colors = RadioButtonDefaults.colors(
                disabledColor = Color(0xff607D8B)
            )
        )

        RadioButton(
            enabled = false,
            selected = true,
            onClick = {},
            colors = RadioButtonDefaults.colors(
                disabledColor = Color(0xff607D8B)
            )
        )
    }

    TutorialText2("Selectable group")

    Spacer(Modifier.height(8.dp))

    // We have two radio buttons and only one can be selected
    var state by remember { mutableStateOf(true) }
    // Note that Modifier.selectableGroup() is essential to ensure correct accessibility behavior
    Row(
        Modifier
            .selectableGroup()
            .padding(8.dp)
    ) {
        RadioButton(
            selected = state,
            onClick = { state = true }
        )
        Spacer(modifier = Modifier.width(24.dp))
        RadioButton(
            selected = !state,
            onClick = { state = false }
        )
    }

    TutorialText2("Selectable group with text")

    val radioOptions = listOf("Calls", "Missed", "Friends")

    val (selectedOption: String, onOptionSelected: (String) -> Unit) = remember {
        mutableStateOf(
            radioOptions[0]
        )
    }
// Note that Modifier.selectableGroup() is essential to ensure correct accessibility behavior
    Column(Modifier.selectableGroup()) {
        radioOptions.forEach { text ->
            Row(
                Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .selectable(
                        selected = (text == selectedOption),
                        onClick = { onOptionSelected(text) },
                        role = Role.RadioButton
                    )
                    .padding(horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                RadioButton(
                    selected = (text == selectedOption),
                    onClick = null // null recommended for accessibility with screenreaders
                )
                Text(
                    text = text,
                    style = MaterialTheme.typography.body1.merge(),
                    modifier = Modifier.padding(start = 16.dp)
                )
            }
        }
    }
}

@ExperimentalMaterialApi
@Composable
private fun SliderExample() {

    TutorialHeader(text = "Slider")
    StyleableTutorialText(
        text = "5-) **Slider** reflects a range of values along a bar, " +
                "from which users may select a single value. They are ideal for adjusting " +
                "settings such as volume, brightness, or applying image filters."
    )
    TutorialText2("Slider")

    val colors = SliderDefaults.colors(
        thumbColor = Color(0xffF44336),
        disabledThumbColor = Color(0xff795548),
        activeTrackColor = Color(0xff009688),
        inactiveTrackColor = Color(0xffFFEA00),
        disabledActiveTrackColor = Color(0xffFF9800),
        disabledInactiveTrackColor = Color(0xff616161),
        activeTickColor = Color(0xff673AB7),
        inactiveTickColor = Color(0xff2196F3),
        disabledActiveTickColor = Color(0xffE0E0E0),
        disabledInactiveTickColor = Color(0xff607D8B)
    )

    var sliderPosition by remember { mutableStateOf(0f) }
    Spacer(Modifier.height(8.dp))
    Slider(value = sliderPosition, onValueChange = { sliderPosition = it })
    Spacer(Modifier.height(8.dp))

    var sliderPosition2 by remember { mutableStateOf(.3f) }
    Slider(value = sliderPosition2, onValueChange = { sliderPosition2 = it }, colors = colors)
    Spacer(Modifier.height(8.dp))

    var sliderPosition3 by remember { mutableStateOf(.4f) }
    Slider(
        value = sliderPosition3,
        onValueChange = { sliderPosition3 = it },
        enabled = false,
        colors = colors
    )
    Spacer(Modifier.height(8.dp))

    var sliderPosition4 by remember { mutableStateOf(26f) }
    Text(text = sliderPosition4.toString())
    Slider(
        value = sliderPosition4,
        onValueChange = { sliderPosition4 = it },
        valueRange = 0f..100f,
        onValueChangeFinished = {

        },
        steps = 10,
        colors = SliderDefaults.colors(
            thumbColor = MaterialTheme.colors.secondary,
            activeTrackColor = MaterialTheme.colors.secondary
        )
    )

    TutorialText2("RangeSlider")
    var sliderPosition5 by remember { mutableStateOf(.1f..(.3f)) }

    RangeSlider(
        value = sliderPosition5,
        onValueChange = {
            sliderPosition5 = it
        },
        colors = colors
    )
}

/**
 * Composable that shows a title as initial letter, title color and a Slider to pick color
 */
@Composable
fun ColorSlider(
    modifier: Modifier,
    title: String,
    titleColor: Color,
    valueRange:  ClosedFloatingPointRange<Float> = 0f..255f,
    rgb: Float,
    onColorChanged: (Float) -> Unit
) {
    Row(modifier, verticalAlignment = Alignment.CenterVertically) {

        Text(text = title.substring(0,1), color = titleColor, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.width(8.dp))
        Slider(
            modifier = Modifier.weight(1f),
            value = rgb,
            onValueChange = { onColorChanged(it) },
            valueRange = valueRange,
            onValueChangeFinished = {}
        )

        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = rgb.toInt().toString(),
            color = Color.LightGray,
            fontSize = 12.sp,
            modifier = Modifier.width(30.dp)
        )

    }
}

@Composable
private fun CheckBoxWithText(label: String, state: Boolean, onStateChange: (Boolean) -> Unit) {

    // Checkbox with text on right side
    val interactionSource = remember { MutableInteractionSource() }
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(40.dp)
            .clickable(
                interactionSource = interactionSource,
                // This is for removing ripple when Row is clicked
                indication = null,
                role = Role.Checkbox,
                onClick = {
                    onStateChange(!state)
                }
            )
            .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically

    ) {
        Checkbox(
            checked = state,
            onCheckedChange = null
        )
        Spacer(modifier = Modifier.padding(start = 8.dp))
        Text(text = label)
    }
}

@Composable
 fun CheckBoxWithTextRippleFullRow(
    label: String,
    state: Boolean,
    onStateChange: (Boolean) -> Unit
) {

    // Checkbox with text on right side
    Row(modifier = Modifier
        .fillMaxWidth()
        .height(40.dp)
        .clickable(
            role = Role.Checkbox,
            onClick = {
                onStateChange(!state)
            }
        )
        .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Checkbox(
            checked = state,
            onCheckedChange = null
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(text = label)
    }
}

private val progressFlow by lazy {
    flow {
        repeat(100) {
            emit(it + 1)
            delay(50)
        }
    }
}