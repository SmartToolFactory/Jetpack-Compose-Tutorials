package com.smarttoolfactory.tutorial2_1unit_testing.mockk

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel

class CalculationViewModel : ViewModel() {
    var sum by mutableStateOf(0)
        private set

    fun increase() {
        sum++
    }

    fun sumWith(value: Int) {
        sum += value
    }
}
