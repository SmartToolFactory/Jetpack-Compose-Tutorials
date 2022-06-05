package com.smarttoolfactory.tutorial1_1basics.chapter5_gesture

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Indication
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CutCornerShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.smarttoolfactory.tutorial1_1basics.ui.Blue400
import com.smarttoolfactory.tutorial1_1basics.ui.Orange400
import com.smarttoolfactory.tutorial1_1basics.ui.components.StyleableTutorialText
import com.smarttoolfactory.tutorial1_1basics.ui.components.TutorialText2
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

/**
 * https://developer.android.com/jetpack/compose/handling-interaction
 */
@Composable
fun Tutorial5_1Screen2() {
    TutorialContent()
}

@Composable
private fun TutorialContent() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        StyleableTutorialText(
            text = "When a user interacts with a UI component, " +
                    "the system represents their behavior by generating " +
                    "a number of Interaction events. " +
                    "For example, if a user touches a button, " +
                    "the button generates PressInteraction." +
                    "Press. If the user lifts their finger inside the button, it generates a " +
                    "PressInteraction.Release, letting the button know that the " +
                    "click was finished.",
            bullets = false
        )
        TutorialText2(text = "collectIsPressedAsState")
        CollectPressedStateExample()
        TutorialText2(text = "Collect interactionSource.interactions")
        InteractionFlowExample()
        TutorialText2(text = "Sample with collectIsPressedAsState")
        InteractionButtonSample()
        TutorialText2(text = "Scale based on collectIsPressedAsState")
        ElasticComposableSample()
    }

}

@Composable
private fun CollectPressedStateExample() {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    Button(
        modifier = Modifier.fillMaxWidth(),
        onClick = { /* do something */ },
        interactionSource = interactionSource
    ) {
        Text(if (isPressed) "Pressed!" else "Not pressed", modifier = Modifier.padding(8.dp))
    }
}


@Composable
private fun CollectStatesExample() {

    val interactionSource = remember { MutableInteractionSource() }
    val interactions = remember { mutableStateListOf<Interaction>() }

    LaunchedEffect(interactionSource) {
        interactionSource.interactions.collect { interaction ->
            when (interaction) {
                is PressInteraction.Press -> {
                    interactions.add(interaction)
                }
                is PressInteraction.Release -> {
                    interactions.remove(interaction.press)
                }
                is PressInteraction.Cancel -> {
                    interactions.remove(interaction.press)
                }
                is DragInteraction.Start -> {
                    interactions.add(interaction)
                }
                is DragInteraction.Stop -> {
                    interactions.remove(interaction.start)
                }
                is DragInteraction.Cancel -> {
                    interactions.remove(interaction.start)
                }

            }
        }
    }


    val lastInteraction = when (interactions.lastOrNull()) {
        is DragInteraction.Start -> "Drag Start"
        is DragInteraction.Stop -> "Drag Stop"
        is DragInteraction.Cancel -> "Drag Cancel"
        is PressInteraction.Press -> "Pressed"
        is PressInteraction.Release -> "Press Release"
        is PressInteraction.Cancel -> "Press Cancel"
        else -> "No state"
    }

    Box(
        Modifier
            .fillMaxWidth()
            .border(2.dp, Blue400)
            .clickable(
                interactionSource, rememberRipple()
            ) {}

            .padding(8.dp)
    ) {
        Text(lastInteraction, modifier = Modifier.padding(8.dp))
    }
}

@Composable
private fun InteractionButtonSample() {
    PressIconButton(
        onClick = {},
        icon = { Icon(Icons.Filled.ShoppingCart, contentDescription = null) },
        text = { Text("Add to cart") }
    )
}

@Composable
private fun InteractionFlowExample() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
    ) {

        val interactionSource = MutableInteractionSource()
        val context = LocalContext.current

        LaunchedEffect(interactionSource) {
            interactionSource.interactions
                .onEach { interaction: Interaction ->
                    Toast.makeText(
                        context,
                        "Interaction: $interaction",
                        Toast.LENGTH_SHORT
                    ).show()
                }
                .launchIn(this)
        }

        Box(
            modifierWithClip
                .clickable(
                    interactionSource = interactionSource,
                    indication = rememberRipple(),
                    onClick = {}
                ),
            contentAlignment = Alignment.Center) {
            Text(
                text = "Collect Interactions",
                color = Color.White
            )
        }
    }
}

@Composable
fun PressIconButton(
    onClick: () -> Unit,
    icon: @Composable () -> Unit,
    text: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
) {
    val isPressed by interactionSource.collectIsPressedAsState()
    Button(
        onClick = onClick, modifier = modifier,
        interactionSource = interactionSource
    ) {
        AnimatedVisibility(visible = isPressed) {
            if (isPressed) {
                Row {
                    icon()
                    Spacer(Modifier.size(ButtonDefaults.IconSpacing))
                }
            }
        }
        text()
    }
}

@Composable
private fun ElasticComposableSample() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {

        ElasticComposable(
            Modifier
                .shadow(8.dp, RoundedCornerShape(10.dp)),
            onClick = {}) {
            Surface(
                shape = RoundedCornerShape(10.dp),
                color = Blue400
            ) {
                Text(
                    text = "Elastic Composable",
                    fontSize = 14.sp,
                    modifier = Modifier
                        .background(Orange400)
                        .padding(14.dp),
                    color = Color.White
                )
            }
        }

        ElasticComposable(
            Modifier
                .shadow(1.dp, CutCornerShape(topEndPercent = 25)),
            onClick = {}) {
            Surface(
                shape = RoundedCornerShape(topEndPercent = 25),
                color = Blue400
            ) {
                Text(
                    text = "Elastic Composable",
                    fontSize = 14.sp,
                    modifier = Modifier
                        .background(Blue400)
                        .padding(14.dp),
                    color = Color.White
                )
            }
        }
    }
}

@Composable
private fun ElasticComposable(
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
    indication: Indication? = null,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    content: @Composable () -> Unit
) {

    val isPressed by interactionSource.collectIsPressedAsState()

    Box(modifier = Modifier
        .graphicsLayer {
            scaleX = if (isPressed) 0.95f else 1f
            scaleY = if (isPressed) 0.95f else 1f
        }
        .then(
            modifier.clickable(
                interactionSource = interactionSource,
                indication = indication
            ) {
                onClick()
            }
        )
    ) {
        content()
    }
}
