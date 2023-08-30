package com.smarttoolfactory.tutorial2_1unit_testing.mockk

import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Test


/**
 * To verify concurrent operations you can use timeout = xxx:
 */
class Test14VerificationTimeout {

    @Test
    fun `Test timeout`() {

        mockk<MockCls> {

            every { sum(1, 2) } returns 4

            Thread {
                Thread.sleep(2000)
                sum(1, 2)
            }.start()

            verify(timeout = 3000) { sum(1, 2) }
        }

    }

    class MockCls {

        fun sum(num1: Int, num2: Int): Int {
            return num1 + num2
        }

    }

}