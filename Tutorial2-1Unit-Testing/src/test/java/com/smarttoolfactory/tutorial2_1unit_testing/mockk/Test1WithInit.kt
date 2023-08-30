package com.smarttoolfactory.tutorial2_1unit_testing.mockk

import com.smarttoolfactory.tutorial2_1unit_testing.mockk.model_math_application.CalculatorService
import com.smarttoolfactory.tutorial2_1unit_testing.mockk.model_math_application.MathApplication
import io.mockk.MockKAnnotations.init
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.verify
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class Test1WithInit {

    //@InjectMockKs annotation is used to create and inject the mock object
    @InjectMockKs
    private lateinit var mathApplication: MathApplication
    //@MockK annotation is used to create the mock object to be injected
    @MockK
    private lateinit var calcService: CalculatorService

    @BeforeEach
    fun setUp() {
        // 🔥🔥 Initializes mock objects annotated with MockKAnnotations
        init(this)
    }


    @Test
    fun `Add two numbers`() {

        // Given
//        every { calcService.add(10.0, 20.0) } returns 30.0

        // When
        val expected = mathApplication.add(10.0, 20.0)

        // Then
        Assertions.assertEquals(expected, 30.0)
        // Verify that add method is called with 10.0 and 20.0 parameters
        verify(exactly = 1) { calcService.add(10.0, 20.0) }
    }
}