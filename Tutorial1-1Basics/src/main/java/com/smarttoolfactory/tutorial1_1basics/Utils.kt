package com.smarttoolfactory.tutorial1_1basics

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.ui.platform.LocalInspectionMode

@Stable
val isInPreview @Composable get() = LocalInspectionMode.current