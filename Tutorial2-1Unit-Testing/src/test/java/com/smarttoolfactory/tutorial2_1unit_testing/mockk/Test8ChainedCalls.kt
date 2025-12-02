package com.smarttoolfactory.tutorial2_1unit_testing.mockk

import com.smarttoolfactory.tutorial2_1unit_testing.mockk.car.Car
import com.smarttoolfactory.tutorial2_1unit_testing.mockk.car.DoorType
import com.smarttoolfactory.tutorial2_1unit_testing.mockk.car.WindowState
import io.mockk.confirmVerified
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotEquals
import org.junit.jupiter.api.Test


// TODO Add more tests with more practical examples
/**
 * Chained calls can be used to check both methods are invoked
 */
class Test8ChainedCalls {

    @Test
    fun `Test chained mock calls`() {

        // Given
        val car = mockk<Car>()
        every { car.door(DoorType.FRONT_LEFT).windowState() } returns WindowState.UP

        // When
        val expected = car.door(DoorType.FRONT_LEFT).windowState() // returns WindowState.UP

        // Then
        assertEquals(expected, WindowState.UP)
        verify { car.door(DoorType.FRONT_LEFT).windowState() }

    }

    @Test
    fun `Confirm that 2 stub method invocations occurred`() {

        // Given
        val car = mockk<Car>()
        every { car.door(DoorType.FRONT_LEFT).windowState() } returns WindowState.UP

        // When
        car.door(DoorType.FRONT_LEFT) // returns chained mock for Door
        car.door(DoorType.FRONT_LEFT).windowState() // returns WindowState.UP

        // Then
        verify { car.door(DoorType.FRONT_LEFT).windowState() }
        verify(exactly = 2) { car.door(any()) }

        confirmVerified(car)

    }

    @Test
    fun `Test works without chain mock that returns mock`() {

        // Given
        val car = mockk<Car>()
        every { car.door(DoorType.FRONT_LEFT) } returns car
//        every { car.door(DoorType.FRONT_LEFT).windowState() } returns WindowState.UP

        // When
        val expectedCar = car.door(DoorType.FRONT_LEFT) // returns chained mock for Door
//        val expectedWindowState =
//            car.door(DoorType.FRONT_LEFT).windowState() // returns WindowState.UP

        // Then
        assertEquals(expectedCar, car)
//        assertEquals(expectedWindowState, WindowState.UP)
//        verify { car.door(DoorType.FRONT_LEFT).windowState() }
    }

    @Test
    fun `Returns different mock when param with chain mock call`() {

        /*
         * Fails with if assertEquals is used
         * org.opentest4j.AssertionFailedError:
         * Expected :Car(child of #1#2)
         * Actual   :Car(#1)
         */

        // Given
        val car = mockk<Car>()
        every { car.door(DoorType.FRONT_LEFT) } returns car
        every { car.door(DoorType.FRONT_LEFT).windowState() } returns WindowState.UP

        // When
        val expectedCar = car.door(DoorType.FRONT_LEFT) // returns chained mock for Door
        val expectedWindowState =
            car.door(DoorType.FRONT_LEFT).windowState() // returns WindowState.UP

        // Then
        assertNotEquals(expectedCar, car)
        assertEquals(expectedWindowState, WindowState.UP)
        verify { car.door(DoorType.FRONT_LEFT).windowState() }

    }

    @Test
    fun `Returns same mock when param is different than chain mock call`() {

        // Given
        val car = mockk<Car>()
        //
        every { car.door(DoorType.FRONT_RIGHT) } returns car
        every { car.door(DoorType.FRONT_LEFT).windowState() } returns WindowState.UP

        // When
        val expectedCar = car.door(DoorType.FRONT_RIGHT) // returns chained mock for Door
        val expectedWindowState =
            car.door(DoorType.FRONT_LEFT).windowState() // returns WindowState.UP

        // Then
        assertEquals(expectedCar, car)
        assertEquals(expectedWindowState, WindowState.UP)
        verify { car.door(DoorType.FRONT_LEFT).windowState() }

    }
}
