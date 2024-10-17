package com.smarttoolfactory.tutorial3_1navigation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.smarttoolfactory.tutorial3_1navigation.ui.theme.ComposeTutorialsTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ComposeTutorialsTheme {
                Scaffold { innerPadding ->
                    Surface(
                        modifier = Modifier.padding(innerPadding)
                    ) {
//                        Tutorial1Screen()
                        Tutorial2Screen()
                    }
                }
            }
        }
    }
}

