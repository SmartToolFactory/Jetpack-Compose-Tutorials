package com.smarttoolfactory.tutorial1_1basics.model

import androidx.compose.runtime.Composable

data class TutorialSectionModel(
    val title: String,
    val action: @Composable (() -> Unit)? = null,
    val description: String,
    val tags: List<String> = listOf()
)

// Basics
val TAG_COMPOSE = "Compose"
val TAG_COMPOSE_MODIFIER = "Modifier"
val TAG_COMPOSE_COLUMN = "Column"
val TAG_COMPOSE_ROW = "Row"
val TAG_COMPOSE_BOX = "Box"
val TAG_COMPOSE_SURFACE = "Surface"
val TAG_COMPOSE_SHAPE = "Shape"
val TAG_COMPOSE_CLICKABLE = "Clickable"

// TEXT
val TAG_TEXT = "Text"

// BUTTON
val TAG_BUTTON = "Button"
val TAG_IMAGE_BUTTON = "Image Button"
val TAG_FAB_BUTTON = "Floating Action Button"
val TAG_CHIP = "Chip"


// TEXT FIELD

// IMAGE

// CARD

// SCAFFOLD

// TOOLBAR

// BOTTOM NAVIGATION

// BOTTOM BAR

// ALERT DIALOG

// CHECK BOX

// RADIO

// Navigation
val TAG_COMPOSE_NAVIGATION = "Compose Navigation"