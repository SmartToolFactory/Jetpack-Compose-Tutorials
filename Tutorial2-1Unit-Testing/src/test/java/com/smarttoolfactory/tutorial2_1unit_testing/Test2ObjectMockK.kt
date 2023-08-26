package com.smarttoolfactory.tutorial2_1unit_testing

import io.mockk.every
import io.mockk.mockkObject
import io.mockk.unmockkAll
import io.mockk.verify
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test


class Test2ObjectMockK {

    @BeforeEach
    fun setUp() {
        mockkObject(CalculusObject)
        mockkObject(MockObj)
        every { MockObj.add(1, 2) } returns 55
    }

    @Test
    fun willUseMockBehaviour() {
        assertEquals(55, MockObj.add(1, 2))
    }

    @AfterEach
    fun afterTests() {
        unmockkAll()
        // or unmockkObject(MockObj)
    }


    @Test
    fun `Add two numbers with mock Object `() {

        // CalculusObject returns real value of it's method
        assertEquals(3, CalculusObject.add(1, 2))

        // Mocked method here
        every { CalculusObject.add(1, 2) } returns 4

        assertEquals(4, CalculusObject.add(1, 2))

        verify(exactly = 2) { CalculusObject.add(1, 2) }

    }

}


object CalculusObject {

    fun add(input1: Int, input2: Int): Int {
        return input1 + input2
    }
}

object MockObj {
    fun add(a: Int, b: Int) = a + b
}
