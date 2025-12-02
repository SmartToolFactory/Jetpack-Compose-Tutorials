package com.smarttoolfactory.tutorial2_1unit_testing.mockk

import com.google.common.truth.Truth
import com.smarttoolfactory.tutorial2_1unit_testing.mockk.car.Car
import com.smarttoolfactory.tutorial2_1unit_testing.mockk.car.Direction
import com.smarttoolfactory.tutorial2_1unit_testing.mockk.car.Outcome
import io.mockk.confirmVerified
import io.mockk.every
import io.mockk.mockk
import io.mockk.spyk
import io.mockk.unmockkAll
import io.mockk.verify
import org.junit.jupiter.api.Test

class Test5Spy {
    private val carSpy = spyk(Car()) // or spyk<Car>() to call the default constructor
    private val carMock = mockk<Car>()

    @Test
    fun spyTest() {

        // WHEN
        val result =
            carSpy.drive(Direction.NORTH) // returns whatever the real function of Car returns

        // THEN
        Truth.assertThat(result).isEqualTo(Outcome.OK)
        verify { carSpy.drive(Direction.NORTH) }
        confirmVerified(carSpy)
    }

    @Test
    fun mockTest() {

        // GIVEN
        every {
            carMock.drive(any())
        } returns Outcome.RECORDED

        // WHEN
        val result = carMock.drive(Direction.NORTH) // mocked this to return Outcome.RECORDED

        // THEN
        Truth.assertThat(result).isEqualTo(Outcome.RECORDED)
        verify { carMock.drive(Direction.NORTH) }
        confirmVerified(carMock)
    }
}
