package com.smarttoolfactory.tutorial2_1unit_testing

import com.google.common.truth.Truth
import org.junit.jupiter.api.Test

class CalculationViewModelTest {

    private val calculationViewModel = CalculationViewModel()

    @Test
    fun `increase test`() {
        // GIVEN
        val initialValue = calculationViewModel.sum
        // WHEN
        calculationViewModel.increase()
        // THEN
        Truth.assertThat(calculationViewModel.sum).isEqualTo(initialValue + 1)
    }

    @Test
    fun `sumWith test`() {
        // GIVEN
        val initialValue = calculationViewModel.sum
        // WHEN
        calculationViewModel.sumWith(5)
        // THEN
        Truth.assertThat(calculationViewModel.sum).isEqualTo(initialValue + 5)
    }
}