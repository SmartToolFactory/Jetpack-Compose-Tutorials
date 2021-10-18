package com.smarttoolfactory.tutorial1_1basics.model

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

data class TutorialSectionModel(
    val title: String,
    val action: @Composable (() -> Unit)? = null,
    val description: String,
    val tags: List<String> = listOf(),
    val tagColor: Color = Color(0xff00BCD4),
    var expanded: Boolean = false
)

