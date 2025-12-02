package com.smarttoolfactory.tutorial2_1unit_testing.mockk

import com.smarttoolfactory.tutorial2_1unit_testing.mockk.car.Car
import com.smarttoolfactory.tutorial2_1unit_testing.mockk.car.Direction
import com.smarttoolfactory.tutorial2_1unit_testing.mockk.car.Outcome
import io.mockk.confirmVerified
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Test


/**
 * To double check that all calls were verified by verify... constructs you can use confirmVerified:
 * confirmVerified(mock1, mock2)
 * There is no big sense to use it for verifySequence and verifyAll
 * as this verification methods already exhaustively cover all calls with verification.
 *
 * It will throw exception in case some calls left without verification.
 */
class Test12VerificationConfirmation {


    /**
     * ConfirmVerified check WHEN, and THEN sections of the test.
     * Not mocking with same params we used in verify does not have to be same.
     *
     */
    @Test
    fun `Confirm verifications are done`() {

        // Given
        val car = mockk<Car>()

        every { car.drive(Direction.NORTH) } returns Outcome.OK
        every { car.drive(Direction.SOUTH) } returns Outcome.OK

        // WHEN
        car.drive(Direction.NORTH) // returns OK
        car.drive(Direction.SOUTH) // returns OK

        // THEN
        verify {
            car.drive(Direction.SOUTH)
            car.drive(Direction.NORTH)
        }

        //
        confirmVerified(car) // makes sure all calls were covered with verification

    }

    @Test
    fun `Confirm verifications are done2`() {

        //Given
        val car = mockk<Car>()

        every { car.drive(Direction.NORTH) } returns Outcome.OK
        every { car.drive(Direction.SOUTH) } returns Outcome.OK
        every { car.drive(Direction.EAST) } returns Outcome.OK

        //When
        car.drive(Direction.NORTH) // returns OK
        car.drive(Direction.SOUTH) // returns OK

        // Then
        verify {
            car.drive(Direction.SOUTH)
            car.drive(Direction.NORTH)
        }

        confirmVerified(car) // makes sure all calls were covered with verification

    }


    @Test
    fun `Confirm verifications are done3`() {

        // Given
        val car = mockk<Car>()
        every { car.drive(any()) } returns Outcome.OK

        //When
        car.drive(Direction.NORTH) // returns OK
        car.drive(Direction.SOUTH) // returns OK

        // Then
        verify {
            car.drive(Direction.SOUTH)
            car.drive(Direction.NORTH)
        }

        // Confirms that mocked methods match to verifies on then
        confirmVerified(car) // makes sure all calls were covered with verification

    }
}
