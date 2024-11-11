package com.smarttoolfactory.tutorial3_1navigation

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavBackStackEntry
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.dialog
import androidx.navigation.compose.rememberNavController
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.serialization.Serializable
import javax.inject.Inject

@Preview
@Composable
fun Tutorial9_1Screen() {
    val navController = rememberNavController()


    NavHost(
        modifier = Modifier.fillMaxSize().systemBarsPadding(),
        navController = navController,
        startDestination = Home
    ) {

        composable<Home> { navBackStackEntry: NavBackStackEntry ->

            val entry = navController.getBackStackEntry(Home)
            val viewModel = viewModel<SharedDialogViewModel>(entry)
            ValueSetScreen(viewModel) {
                navController.navigate(DialogRoute)
            }

        }

        dialog<DialogRoute>(
            dialogProperties = DialogProperties()
        ) {
            val entry = navController.getBackStackEntry(Home)
            val viewModel = viewModel<SharedDialogViewModel>(entry)
            ValueSetDialog(viewModel)
        }
    }
}


@Composable
private fun ValueSetScreen(
    sharedDialogViewModel: SharedDialogViewModel,
    onClick: () -> Unit,
) {

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        val value = sharedDialogViewModel.counter

        Text(text = "Screen", fontSize = 36.sp)

        Text("Value: $value")
        Slider(
            value = value,
            onValueChange = {
                sharedDialogViewModel.counter = it
            },
            valueRange = 0f..100f
        )

        Button(
            modifier = Modifier.fillMaxWidth(),
            onClick = onClick
        ) {
            Text("Open dialog")
        }
    }
}

@Composable
private fun ValueSetDialog(sharedDialogViewModel: SharedDialogViewModel) {

    val context = LocalContext.current

    DisposableEffect(Unit) {

        onDispose {
            Toast.makeText(context, "Dialog is disposed", Toast.LENGTH_SHORT).show()
        }
    }

    Column(
        modifier = Modifier.background(Color.White, RoundedCornerShape(16.dp))
            .padding(horizontal = 16.dp, vertical = 32.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        val value = sharedDialogViewModel.counter



        Text(text = "Dialog", fontSize = 36.sp)
        Text("Value: $value")

        Slider(
            value = value,
            onValueChange = {
                sharedDialogViewModel.counter = it
            },
            valueRange = 0f..100f
        )
    }
}

@Serializable
data object DialogRoute

@HiltViewModel
class SharedDialogViewModel @Inject constructor() : ViewModel() {
    var counter by mutableFloatStateOf(0f)
}