package com.smarttoolfactory.tutorial2_1unit_testing.mockk

import com.smarttoolfactory.tutorial2_1unit_testing.mockk.model_math_application.CalculatorService
import com.smarttoolfactory.tutorial2_1unit_testing.mockk.model_math_application.MathApplication
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class Test1WithMockK {


    private lateinit var mathApplication: MathApplication
    private lateinit var calcService: CalculatorService

    @BeforeEach
    fun setUp() {
        mathApplication = MathApplication()

        calcService = mockk<CalculatorService>()

        mathApplication.calcService = calcService
    }

    @Test
    fun `Add two numbers`() {

        // Given
        every { calcService.add(10.0, 20.0) } returns 30.0

        // When
        val expected = mathApplication.add(10.0, 20.0)

        // Then
        Assertions.assertEquals(expected, 30.0)
        // Verify that add method is called with 10.0 and 20.0 parameters
        verify(exactly = 1) { calcService.add(10.0, 20.0) }
    }
}
