package com.smarttoolfactory.tutorial1_1basics

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.navigation.NavController
import com.smarttoolfactory.tutorial1_1basics.ui.ComposeTutorialsTheme

/**
 * This is the single and only Activity that contains Composable Tutorial list.
 *
 * * Tutorial navigation is done via [NavController] and ```composable``` extension function
 * for [NavGraphBuilder]
 */
@ExperimentalFoundationApi
@ExperimentalComposeUiApi
class MainActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            ComposeTutorialsTheme {
                MainScreen()
            }
        }
    }
}