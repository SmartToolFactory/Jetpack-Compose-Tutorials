package com.smarttoolfactory.tutorial2_1unit_testing.mockk

import com.smarttoolfactory.tutorial2_1unit_testing.mockk.car.Car
import com.smarttoolfactory.tutorial2_1unit_testing.mockk.car.Direction
import com.smarttoolfactory.tutorial2_1unit_testing.mockk.car.Outcome
import com.smarttoolfactory.tutorial2_1unit_testing.mockk.model_math_application.CalculatorService
import com.smarttoolfactory.tutorial2_1unit_testing.mockk.model_math_application.MathApplication
import io.mockk.MockKAnnotations
import io.mockk.confirmVerified
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.mockk
import io.mockk.slot
import io.mockk.verify
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

/**
 * If we need to capture the parameters passed to a method,
 * then we can use CapturingSlot or MutableList.
 * It is useful when we want to have some custom logic
 * in an answer block or we just need to verify the value of the arguments passed.
 */
class Test9CapturingParams {

    @InjectMockKs
    private lateinit var mathApplication: MathApplication

    @MockK
    private lateinit var calcService: CalculatorService

    @Test
    fun `Returns captured parameter with slot`() {

        // given
        val slot = slot<String>()
        every { calcService.log(capture(slot)) } returns "Expected Output"

        // when
        mathApplication.log("Expected Param")

        // then
        assertEquals("Expected Param", slot.captured)
    }

    @Test
    fun `Returns captured parameter with list`() {

        // given
        val list = mutableListOf<String>()
        every { calcService.log(capture(list)) } returns "Expected Output"

        // when
        mathApplication.log("Expected Param")

        // then
        assertEquals("Expected Param", list[0])
    }

    @Test
    fun `Returns captured multiple parameters with slot`() {

        // given
        val slot1 = slot<Double>()
        val slot2 = slot<Double>()

        every {
            calcService.add(capture(slot1), capture(slot2))
        } answers {
            // 🔥 returns 40.0(random) to test assertion output
            println("answers arg1: ${this.args[0]}, arg2: ${this.args[1]}")
            40.0
        }

        // when
        val expectedOutput = mathApplication.add(10.0, 20.0)

        // then
        assertEquals(10.0, slot1.captured)
        assertEquals(20.0, slot2.captured)
        assertEquals(expectedOutput, 40.0)
    }

    @Test
    fun `Capturing parameters with car example`() {

        // Given
        val car = mockk<Car>()

        val slot = slot<Int>()
        val list = mutableListOf<Int>()

        every {
            car.recordTelemetry(
                speed = capture(slot), // makes mock match call with any value for `speed` and record it in a slot
                direction = Direction.NORTH // makes mock and capturing only match calls with specific `direction`. Use `any()` to match calls with any `direction`
            )
        } answers {
            println(slot.captured)

            Outcome.RECORDED
        }

        every {
            car.recordTelemetry(
                speed = capture(list),
                direction = Direction.SOUTH
            )
        } answers {
            Outcome.RECORDED
        }

        // When
        car.recordTelemetry(speed = 15, direction = Direction.NORTH) // prints 15
        car.recordTelemetry(speed = 16, direction = Direction.SOUTH) // prints 16

        // Then
        // 🔥 or() is used for verifying method is called twice with either 15 or 16
        verify(exactly = 2) {
            car.recordTelemetry(
                speed = or(15, 16),
                direction = any()
            )
        }

        confirmVerified(car)
    }

    @BeforeEach
    fun setUp() {
        MockKAnnotations.init(this)
    }
}