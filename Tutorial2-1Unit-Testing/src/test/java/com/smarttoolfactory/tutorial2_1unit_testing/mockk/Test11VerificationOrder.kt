package com.smarttoolfactory.tutorial2_1unit_testing.mockk

import io.mockk.Called
import io.mockk.confirmVerified
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import io.mockk.verify
import io.mockk.verifyAll
import io.mockk.verifyOrder
import io.mockk.verifySequence
import org.junit.jupiter.api.Test


/**
 * verifyAll verifies that all calls happened without checking their order.
 * verifySequence verifies that that the exact sequence happened .
 * verifyOrder verifies that calls happened in a specific order.
 * wasNot Called verifies that the mock or the list of mocks was not called at all.
 *
 */
class Test11VerificationOrder {

    private val obj = mockk<MockedClass>()
    private val slot = slot<Int>()

    @Test
    fun test() {

        every {
            obj.sum(any(), capture(slot))
        } answers {
            // 🔥 firstArg<Int>() is the first argument of mock
            1 + firstArg<Int>() + slot.captured
        }

        obj.sum(1, 2) // returns 4
        obj.sum(1, 3) // returns 5
        obj.sum(2, 2) // returns 5

        // Verifies that each function with params is called
        verifyAll {
            obj.sum(1, 3)
            obj.sum(1, 2)
            obj.sum(2, 2)
        }

        // Verifies that all sequence is called, all mocked function calls should be verified
        verifySequence {
            obj.sum(1, 2)
            obj.sum(1, 3)
            obj.sum(2, 2)
        }

        // Only looks for order of calls, it's not necessary to call every function
        verifyOrder {
            obj.sum(1, 2)
            obj.sum(2, 2)
        }

        val obj2 = mockk<MockedClass>()
        val obj3 = mockk<MockedClass>()
        verify {
            listOf(obj2, obj3) wasNot Called
        }

        confirmVerified(obj)

    }

}


class MockedClass {
    fun sum(a: Int, b: Int) = a + b
}