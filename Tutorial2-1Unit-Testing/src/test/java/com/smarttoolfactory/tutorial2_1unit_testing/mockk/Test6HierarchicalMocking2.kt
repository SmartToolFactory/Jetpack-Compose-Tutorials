package com.smarttoolfactory.tutorial2_1unit_testing.mockk

import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.Test

class Test5HierarchicalMocking2 {

    @Test
    fun `test hierarchical structure of Address book`() {

        val addressBook = mockk<AddressBook> {

            every { contacts } returns listOf(

                mockk {
                    every { name } returns "John"
                    every { telephone } returns "123-456-789"
                    // 🔥 Fields of Address object is mocked
                    every { address.city } returns "New-York"
                    every { address.zip } returns "123-45"
                },

                mockk {
                    every { name } returns "Alex"
                    every { telephone } returns "789-456-123"
                    // 🔥 Address object is mocked
                    every { address } returns mockk {
                        every { city } returns "Wroclaw"
                        every { zip } returns "543-21"
                    }
                }

            )
        }

    }

}

interface AddressBook {
    val contacts: List<Contact>
}

interface Contact {
    val name: String
    val telephone: String
    val address: Address
}

interface Address {
    val city: String
    val zip: String
}
