package com.smarttoolfactory.tutorial1_1basics.chapter4_state

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import com.smarttoolfactory.tutorial1_1basics.ui.components.StyleableTutorialText
import com.smarttoolfactory.tutorial1_1basics.ui.components.getRandomColor
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun Tutorial4_5_1Screen() {
    TutorialContent()
}

@Composable
private fun TutorialContent() {

    val scaffoldState: ScaffoldState = rememberScaffoldState()

    Scaffold(scaffoldState = scaffoldState) {

        /*
            LaunchedEffect and rememberCoroutineScope
         */
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp)
                .verticalScroll(rememberScrollState())
        ) {

            StyleableTutorialText(
                text = "When LaunchedEffect enters the Composition, it launches a coroutine " +
                        "with the block of code passed as a parameter. The coroutine will be " +
                        "cancelled if LaunchedEffect leaves the composition.",
                bullets = false
            )
            LaunchedEffectExample(scaffoldState)

            CoroutineScopeExample(scaffoldState)

            /*
                rememberUpdatedState
             */
            StyleableTutorialText(
                text = "rememberUpdatedState unlike remember updates calculation" +
                        " when a composable is recomposed.",
                bullets = false
            )
            UpdatedRememberExample()
            RememberUpdatedStateSample2()
            /*
                DisposableEffect samples
             */
            StyleableTutorialText(
                text = "DisposableEffect is good for cleaning things before " +
                        "a composable exits composition",
                bullets = false
            )
            DisposableEffectButton()
            DisposableEffectLifecycleButton()
        }
    }

}

/**
 * Adding this LaunchedEffect to composition when condition is true
 * Same goes for if this was remember(LaunchedEffect under the hood uses `remember(key){}`
 * when condition is met remember gets added to composition and it gets removed when it's not met
 */
@Composable
private fun LaunchedEffectExample(scaffoldState: ScaffoldState) {

    var counter by remember { mutableStateOf(0) }

    if (counter > 0 && counter % 3 == 0) {
        // `LaunchedEffect` will cancel and re-launch if
        // `scaffoldState.snackbarHostState` changes
        LaunchedEffect(scaffoldState.snackbarHostState) {
            // Show snackbar using a coroutine, when the coroutine is cancelled the
            // snackbar will automatically dismiss. This coroutine will cancel whenever
            // if statement is false, and only start when statement is true
            // (due to the above if-check), or if `scaffoldState.snackbarHostState` changes.
            scaffoldState.snackbarHostState.showSnackbar("LaunchedEffect snackbar")
        }
    }

    // This button increase counter that will trigger LaunchedEffect
    OutlinedButton(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        onClick = {
            counter++
        }
    ) {
        Text("LaunchedEffect Counter $counter")
    }
}

@Composable
private fun CoroutineScopeExample(
    scaffoldState: ScaffoldState
) {

    // Creates a CoroutineScope bound to TutorialContent
    val scope = rememberCoroutineScope()

    // This button increase counter that will trigger CoroutineScope
    var counter by remember { mutableStateOf(0) }

    OutlinedButton(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        onClick = {
            counter++
            if (counter > 0 && counter % 3 == 0) {
                scope.launch {
                    scaffoldState.snackbarHostState.showSnackbar("CoroutineScope snackbar")
                }
            }

        }
    ) {
        Text("CoroutineScope Counter $counter")
    }
}

@Composable
private fun UpdatedRememberExample() {
    var myInput by remember {
        mutableStateOf(0)
    }

    OutlinedButton(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        onClick = {
            myInput++

        }
    ) {
        Text("Increase rememberInput: $myInput")
    }
    Calculation(input = myInput)
}

/*
    rememberUpdateState
 */

/**
 * In this example calculation uses `rememberUpdatedState` and `remember` functions to
 * store values from previous composition.
 *
 * However `rememberUpdatedState` stores updates state if we recompose this function with
 * different input.
 *
 */
@Composable
private fun Calculation(input: Int) {
    val rememberUpdatedStateInput by rememberUpdatedState(input)
    val rememberedInput = remember { input }
    Text("updatedInput: $rememberUpdatedStateInput, rememberedInput: $rememberedInput")
}


/**
 * In this example we set a lambda to be invoked after a calculation that takes time to complete
 * while calculation running if our lambda gets updated `rememberUpdatedState` makes sure
 * that latest lambda is invoked
 */
@Composable
private fun RememberUpdatedStateSample2() {

    val context = LocalContext.current

    var showCalculation by remember { mutableStateOf(true) }
    val radioOptions = listOf("Option🍒", "Option🍏", "Option🎃")

    val (selectedOption: String, onOptionsSelected: (String) -> Unit) = remember {
        mutableStateOf(radioOptions[0])
    }

    Column {

        radioOptions.forEach { text ->
            Column(
                modifier = Modifier.selectableGroup()
            ) {
                Row(
                    Modifier
                        .selectable(
                            selected = (text == selectedOption),
                            onClick = {
                                if (!showCalculation) {
                                    showCalculation = true
                                }
                                onOptionsSelected(text)
                            },
                            role = Role.RadioButton
                        )
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                ) {
                    RadioButton(selected = (text == selectedOption), onClick = null)
                    Spacer(modifier = Modifier.width(16.dp))
                    Text(text = text)
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        if (showCalculation) {

            println("📝 Invoking calculation2 with option: $selectedOption")

            Calculation2 {
                showCalculation = false
                Toast.makeText(
                    context,
                    "Calculation2 $it result: $selectedOption",
                    Toast.LENGTH_SHORT
                )
                    .show()
            }
        }
    }
}

/**
 * LaunchedEffect restarts when one of the key parameters changes.
 * However, in some situations you might want to capture a value in your effect that,
 * if it changes, you do not want the effect to restart.
 * In order to do this, it is required to use rememberUpdatedState to create a reference
 * to this value which can be captured and updated. This approach is helpful for effects that
 * contain long-lived operations that may be expensive or prohibitive to recreate and restart.
 */
@Composable
private fun Calculation2(operation: (String) -> Unit) {

    println("🤔 Calculation2(): operation: $operation")
    // This returns the updated operation if we recompose with new operation
    val currentOperation by rememberUpdatedState(newValue = operation)
    // This one returns the initial operation this composable enters composition
    val rememberedOperation = remember { operation }

    // 🔥 This LaunchedEffect block only gets called once, not called on each recomposition
    LaunchedEffect(key1 = true, block = {
        delay(4000)
        currentOperation("rememberUpdatedState")
        rememberedOperation("remember")
    })

    Row(verticalAlignment = Alignment.CenterVertically) {
        CircularProgressIndicator(color = getRandomColor())
    }
}


/*
    DisposableEffect
 */
@Composable
private fun DisposableEffectButton() {
    var showDisposableEffectSample by remember { mutableStateOf(false) }
    OutlinedButton(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        onClick = {
            showDisposableEffectSample = !showDisposableEffectSample

        }
    ) {
        Text("Display DisposableEffect sample")
    }

    if (showDisposableEffectSample) {
        DisposableEffectExample()
    }
}

/**
 * When a composable functions enters composition side-effect function, `DisposableEffect`
 * is called, when the same composable function is about to exit composition `onDispose`
 * function of `DisposableEffect` is called
 */
@Composable
private fun DisposableEffectExample() {

    val context = LocalContext.current

    DisposableEffect(
        key1 = Unit,
        effect = {

            Toast.makeText(
                context,
                "DisposableEffectSample composition ENTER",
                Toast.LENGTH_SHORT
            ).show()

            // 🔥 Called just before exiting composition
            onDispose {
                Toast.makeText(
                    context,
                    "DisposableEffectSample composition EXIT",
                    Toast.LENGTH_SHORT
                )
                    .show()

            }
        }
    )

    Column(modifier = Modifier.background(Color(0xffFFB300))) {
        Text(
            text = "Disposable Effect Enter/Exit sample",
            color = Color.White,
            modifier = Modifier
                .padding(8.dp)
                .fillMaxWidth()
        )
    }

}

@Composable
private fun DisposableEffectLifecycleButton() {
    var showDisposableEffectLifeCycle by remember { mutableStateOf(false) }
    OutlinedButton(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        onClick = {
            showDisposableEffectLifeCycle = !showDisposableEffectLifeCycle

        }
    ) {
        Text("Display DisposableEffect with LifeCycle")
    }

    if (showDisposableEffectLifeCycle) {

        val context = LocalContext.current

        DisposableEffectWithLifeCycle(
            onResume = {
                Toast.makeText(
                    context,
                    "DisposableEffectWithLifeCycle onResume()",
                    Toast.LENGTH_SHORT
                )
                    .show()
            },
            onPause = {
                Toast.makeText(
                    context,
                    "DisposableEffectWithLifeCycle onPause()",
                    Toast.LENGTH_SHORT
                )
                    .show()
            }

        )
    }
}

@Composable
private fun DisposableEffectWithLifeCycle(
    onResume: () -> Unit,
    onPause: () -> Unit,
) {

    val context = LocalContext.current

    // Safely update the current lambdas when a new one is provided
    val lifecycleOwner: LifecycleOwner = LocalLifecycleOwner.current

    Toast.makeText(
        context,
        "DisposableEffectWithLifeCycle composition ENTER",
        Toast.LENGTH_SHORT
    ).show()

    val currentOnResume by rememberUpdatedState(onResume)
    val currentOnPause by rememberUpdatedState(onPause)

    // If `lifecycleOwner` changes, dispose and reset the effect
    DisposableEffect(lifecycleOwner) {
        // Create an observer that triggers our remembered callbacks
        // for lifecycle events
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_CREATE -> {
                    Toast.makeText(
                        context,
                        "DisposableEffectWithLifeCycle ON_CREATE",
                        Toast.LENGTH_SHORT
                    ).show()
                }
                Lifecycle.Event.ON_START -> {
                    Toast.makeText(
                        context,
                        "DisposableEffectWithLifeCycle ON_START",
                        Toast.LENGTH_SHORT
                    ).show()
                }
                Lifecycle.Event.ON_RESUME -> {
                    currentOnResume()
                }
                Lifecycle.Event.ON_PAUSE -> {
                    currentOnPause()
                }
                Lifecycle.Event.ON_STOP -> {
                    Toast.makeText(
                        context,
                        "DisposableEffectWithLifeCycle ON_STOP",
                        Toast.LENGTH_SHORT
                    ).show()
                }
                Lifecycle.Event.ON_DESTROY -> {
                    Toast.makeText(
                        context,
                        "DisposableEffectWithLifeCycle ON_DESTROY",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }

        // Add the observer to the lifecycle
        lifecycleOwner.lifecycle.addObserver(observer)

        // When the effect leaves the Composition, remove the observer
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)

            Toast.makeText(
                context,
                "DisposableEffectWithLifeCycle composition EXIT",
                Toast.LENGTH_SHORT
            )
                .show()
        }
    }

    Column(modifier = Modifier.background(Color(0xff03A9F4))) {
        Text(
            text = "Disposable Effect with lifecycle",
            color = Color.White,
            modifier = Modifier
                .padding(8.dp)
                .fillMaxWidth()
        )
    }
}

