package com.smarttoolfactory.tutorial2_1unit_testing.mockk

import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class Test5HierarchicalMocking {

    @Test
    fun ` Hierarchical Class mocked only field of property object`() {

        // given
        val foo = mockk<Foo> {
            every { bar } returns mockk {
                every { nickname } returns "Tomato"
            }
        }

        // when
        val nickname = foo.bar.nickname

        // then
        assertEquals("Tomato", nickname)
    }

    @Test
    fun `given Hierarchical Class when Mocking It then Return Proper Value`() {

        // given
        val foo = mockk<Foo> {
            every { name } returns "Karol"
            every { bar } returns mockk {
                every { nickname } returns "Tomato"

                // 🔥Method of Bar is stubbed here
                every { add(1, 2) } returns 5
            }
        }

        // when
        val name = foo.name
        val nickname = foo.bar.nickname
        val expected = foo.bar.add(1, 2)

        // then
        assertEquals("Karol", name)
        assertEquals("Tomato", nickname)

        assertEquals(expected, 5)

    }

}

class Foo {
    lateinit var name: String
    lateinit var bar: Bar
}

class Bar {
    lateinit var nickname: String

    fun add(num1: Int, num2: Int) = num1 + num2
}