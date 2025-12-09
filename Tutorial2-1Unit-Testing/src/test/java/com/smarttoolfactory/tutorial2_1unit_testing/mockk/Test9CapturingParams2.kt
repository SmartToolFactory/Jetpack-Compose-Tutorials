package com.smarttoolfactory.tutorial2_1unit_testing.mockk

import com.google.common.truth.Truth
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.slot
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class CaptureTest {

    @InjectMockKs
    private lateinit var useCase: UseCase

    @MockK
    private lateinit var repository: Repository

    @BeforeEach
    fun setUp() {
        // 🔥🔥 Initializes mock objects annotated with MockKAnnotations
        MockKAnnotations.init(this)
    }

    @Test
    fun `Returns captured parameter with slot`() {

        // GIVEN
        val size = 5
        val slot = slot<Int>()
        every { repository.getItems(capture(slot)) } returns List(5) {
            Item("Row $it")
        }
        // WHEN
        useCase.getItems(size = size)
        // THEN
        Truth.assertThat(slot.captured).isEqualTo(10)

    }
}

class UseCase(private val repository: Repository) {
    fun getItems(size: Int): List<Item> {
        return repository.getItems(size * 2)
    }
}

class Repository {
    fun getItems(size: Int) = List(size) {
        Item("Row $it")
    }
}

data class Item(val text: String)
