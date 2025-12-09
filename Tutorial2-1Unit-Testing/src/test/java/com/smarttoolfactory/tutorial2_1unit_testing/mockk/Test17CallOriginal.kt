package com.smarttoolfactory.tutorial2_1unit_testing.mockk

import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class Adder {
    fun addOne(num: Int) = num + 1
}

class Test17CallOriginal {

    val adder = mockk<Adder>()
    private val interfaceMock = mockk<SomeInterface>()
    private val interfaceImplMock = mockk<SomeInterfaceImpl>()

    @Test
    fun callOriginalTest() {
        every { adder.addOne(any()) } returns -1
        every { adder.addOne(3) } answers { callOriginal() }

        assertEquals(-1, adder.addOne(2))
        assertEquals(4, adder.addOne(3)) // original function is called
    }

    // This test fails because there is no implementation
    @Test
    fun interfaceCallOriginalTest() {
        every { interfaceMock.operation() } answers { callOriginal() }

        val result = interfaceMock.operation()
        println("Result: $result")
    }

    @Test
    fun interfaceImplOriginalTest() {
        every { interfaceImplMock.operation() } answers { callOriginal() }

        val expected = interfaceImplMock.operation()

        assertEquals(expected, 3)
    }
}

private class SomeInterfaceImpl() : SomeInterface {
    override fun operation(): Int {
        return 3
    }

}

private interface SomeInterface {
    fun operation(): Int
}