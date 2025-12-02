package com.smarttoolfactory.tutorial2_1unit_testing.mockk

import com.smarttoolfactory.tutorial2_1unit_testing.mockk.model_math_application.CalculatorService
import com.smarttoolfactory.tutorial2_1unit_testing.mockk.model_math_application.MathApplication
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.verify
import org.junit.jupiter.api.*
import org.junit.jupiter.api.extension.ExtendWith


/**
 * Methods with @BeforeAll and @AfterAll annotations which are static methods should be inside
 * Companion object.
 *
 * Mockk is either used with @ExtendWith(MockKExtension::class) or
 *  MockKAnnotations.init(this) is called in method with @BeforeEach annotation
 *
 */
@ExtendWith(MockKExtension::class)
internal class Test1ExtendWith {

    @InjectMockKs
    private lateinit var mathApplication: MathApplication

    @MockK
    private lateinit var calcService: CalculatorService

    companion object {

        @BeforeAll
        @JvmStatic
        fun beforeAllTestCases() {
            println("Runs once before all test cases.")
        }

        @AfterAll
        @JvmStatic
        fun afterAllTestCases() {
            println("Runs once after all test cases.")
        }
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
        verify(exactly =  1) { calcService.add(10.0, 20.0) }
    }
}
