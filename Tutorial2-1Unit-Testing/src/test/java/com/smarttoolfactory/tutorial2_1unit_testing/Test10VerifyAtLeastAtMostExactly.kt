package com.smarttoolfactory.tutorial2_1unit_testing

import com.smarttoolfactory.tutorial2_1unit_testing.car.Car
import io.mockk.confirmVerified
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Test

class Test10VerifyAtLeastAtMostExactly {

    @Test
    fun `Verify atLeast, atMost and exactly`() {

        val car = mockk<Car>(relaxed = true)

        car.accelerate(fromSpeed = 10, toSpeed = 20)
        car.accelerate(fromSpeed = 10, toSpeed = 30)
        car.accelerate(fromSpeed = 20, toSpeed = 30)

        // all pass
        verify(atLeast = 3) { car.accelerate(allAny(), allAny()) }
        verify(atMost = 2) { car.accelerate(fromSpeed = 10, toSpeed = or(20, 30)) }
        verify(exactly = 1) { car.accelerate(fromSpeed = 10, toSpeed = 20) }
//        // means no calls were performed
        verify(exactly = 0) { car.accelerate(fromSpeed = 30, toSpeed = 10) }

        confirmVerified(car)

    }
}