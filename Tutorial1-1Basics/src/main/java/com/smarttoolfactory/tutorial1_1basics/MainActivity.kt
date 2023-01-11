package com.smarttoolfactory.tutorial1_1basics

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.core.view.WindowCompat
import androidx.navigation.NavController
import com.smarttoolfactory.tutorial1_1basics.ui.ComposeTutorialsTheme

/**
 * This is the single and only Activity that contains Composable Tutorial list.
 *
 * * Tutorial navigation is done via [NavController] and ```composable``` extension function
 * for [NavGraphBuilder]
 */
@ExperimentalMaterialApi
@ExperimentalAnimationApi
@ExperimentalFoundationApi
@ExperimentalComposeUiApi
class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        WindowCompat.setDecorFitsSystemWindows(window, false)

        setContent {
            ComposeTutorialsTheme {
                MainScreen()
            }
        }
    }
}
