package com.smarttoolfactory.tutorial2_1unit_testing

import com.smarttoolfactory.tutorial2_1unit_testing.car.Car
import com.smarttoolfactory.tutorial2_1unit_testing.car.Direction
import com.smarttoolfactory.tutorial2_1unit_testing.car.Outcome
import com.smarttoolfactory.tutorial2_1unit_testing.model_math_application.CalculatorService
import com.smarttoolfactory.tutorial2_1unit_testing.model_math_application.MathApplication
import io.mockk.MockKAnnotations.init
import io.mockk.confirmVerified
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

/**
 * You can mix both regular arguments and matchers
 */
class Test7PartialArgumentMatching {

    @InjectMockKs
    private lateinit var mathApplication: MathApplication
    @MockK
    private lateinit var calcService: CalculatorService

    @BeforeEach
    fun setUp() {
        init(this)
    }

    @Test
    fun `Add two numbers`() {

        // 🔥 Only one parameter of add function is mocked, This does not work with Mockito

        // Given
        every { calcService.add(any(), 20.0) } returns 30.0

        // When
        val expected = mathApplication.add(10.0, 20.0)

        // Then
        Assertions.assertEquals(expected, 30.0)
        // Verify that add method is called with 10.0 and 20.0 parameters
        verify(exactly = 1) { calcService.add(10.0, 20.0) }
    }


    /**
     * more(): matches if the value is more than the provided value via the compareTo function
     *
     *
     *
     */
    @Test
    fun `Partial Mocking with car`() {

        // Given
        val car = mockk<Car>()

        every {
            car.recordTelemetry(
                speed = more(50),
                direction = Direction.NORTH, // here eq() is used
                lat = any(),
                long = any()
            )
        } returns Outcome.RECORDED

        // When
        car.recordTelemetry(60, Direction.NORTH, 51.1377382, 17.0257142)

        // Then
        verify { car.recordTelemetry(60, Direction.NORTH, 51.1377382, 17.0257142) }
        confirmVerified(car)

    }

}