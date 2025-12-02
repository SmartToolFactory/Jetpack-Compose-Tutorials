package com.smarttoolfactory.tutorial2_1unit_testing.mockk

import com.smarttoolfactory.tutorial2_1unit_testing.mockk.model_math_application.CalculatorService
import com.smarttoolfactory.tutorial2_1unit_testing.mockk.model_math_application.MathApplication
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.verify
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith


/**
 * A typical mocked object will throw MockKException if we try to call a method
 * where the return value hasn't been specified.
 *
 *If we don't want to describe the behavior of each method,
 * then we can use a relaxed mock. This kind of mock provides default values for each function.
 * For example, the String return type will return an empty String, Double 0, Boolean false,
 * an object type null
 */

@ExtendWith(MockKExtension::class)
class Test3RelaxedMockK {
    @Nested
    inner class RelaxedMockTest {

        @InjectMockKs
        private lateinit var mathApplication: MathApplication

        @MockK(relaxed = true)
        private lateinit var calcService: CalculatorService

        @Test
        fun `Add two numbers`() {

            // 🔥 If this was not a RelaxedMockk it would throw
            // io.mockk.MockKException: no answer found for: CalculatorService(#1).add(10.0, 20.0)
            // Given
//        every { calcService.add(10.0, 20.0) } returns 30.0

            // When
            val expected = mathApplication.add(10.0, 20.0)

            // Then
            Assertions.assertEquals(expected, 0.0)
            // Verify that add method is called with 10.0 and 20.0 parameters
            verify(exactly = 1) { calcService.add(10.0, 20.0) }
        }

    }

    /**
     * In case you would like Unit returning functions to be relaxed,
     * you can use relaxUnitFun = true as an argument to the mockk function,
     * @MockKannotation or MockKAnnotations.init function.
     */
    @Nested
    inner class RelaxedUnitFunTest {

        @InjectMockKs
        private lateinit var mathApplication: MathApplication

        @MockK(relaxUnitFun = true)
        private lateinit var calcService: CalculatorService

        @Test
        fun `Add two numbers`() {
            // 🔥 Since this only has relaxUnitFun = true methods return any value other than
            // Unit should be stubbed
            // io.mockk.MockKException: no answer found for: CalculatorService(#1).add(10.0, 20.0)
            // Given
            every { calcService.add(10.0, 20.0) } returns 30.0

            // When
            val expected = mathApplication.add(10.0, 20.0)

            // Then
            Assertions.assertEquals(expected, 30.0)
            // Verify that add method is called with 10.0 and 20.0 parameters
            verify(exactly = 1) { calcService.add(10.0, 20.0) }
        }

        @Test
        fun `Prints nothing with relaxed unit fun mock`() {
            // Given
            // 🔥 Method that returns unit is stubbed since it's  @MockK(relaxUnitFun = true)
            // When
            mathApplication.print("Hello")

            // Then
            verify { calcService.print("Hello") }
        }
    }

    @Nested
    inner class RelaxedUnitFunTest2 {
        @InjectMockKs
        private lateinit var useCase: BooleanUseCase

        @MockK(relaxed = true)
        private lateinit var service: BooleanService

        @Test
        fun `is active`() {
            val expected = useCase.isActive()
            Assertions.assertEquals(expected, false)

            verify { service.isActive() }
        }
    }
}

class BooleanUseCase(val booleanService: BooleanService) {
    fun isActive(): Boolean {
        return booleanService.isActive()
    }
}

interface BooleanService {
    fun isActive(): Boolean
    fun add(): Int
}
