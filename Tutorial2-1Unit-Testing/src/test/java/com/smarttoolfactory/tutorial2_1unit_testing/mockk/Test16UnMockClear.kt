package com.smarttoolfactory.tutorial2_1unit_testing.mockk

import io.mockk.clearAllMocks
import io.mockk.clearMocks
import io.mockk.every
import io.mockk.junit5.MockKExtension
import io.mockk.mockk
import io.mockk.spyk
import io.mockk.unmockkAll
import org.junit.Test
import org.junit.jupiter.api.extension.ExtendWith

@ExtendWith(MockKExtension::class)
class Test16UnMockClear {

    private lateinit var viewModel: MyViewModel
    private val useCaseMock: MyUseCase = mockk()
    private val useCaseSpy: MyUseCase = spyk()

    @Test
    fun clearMocksTest1() {
        viewModel = MyViewModel(useCaseMock)
        every {
            useCaseMock.greet(any())
        } returns "Hello World"

        val result1 = viewModel.greet("Cat")
        println("result1: $result1")

        // 🔥 Clearing mock returns nothing since useCase is a mock with no implementation
        // without every{} returns and test fails
        clearMocks(useCaseMock)
        // This one does nothing
//        unmockkAll()

        val result2 = viewModel.greet("Dog")
        println("result2: $result2")
    }

    @Test
    fun clearMocksTest2() {
        viewModel = MyViewModel(useCaseSpy)
        every {
            useCaseSpy.greet(any())
        } returns "Hello World"

        val result1 = viewModel.greet("Cat")
        println("result1: $result1")

        // 🔥 Clearing mock returns this to original function
        clearAllMocks()
//        unmockkAll()

        val result2 = viewModel.greet("Dog")
        println("result2: $result2")
    }

}

private class MyViewModel(private val useCase: MyUseCase) {
    fun greet(name: String): String {
        return useCase.greet(name)
    }
}

class MyUseCase {
    fun greet(name: String): String {
        println("MyUseCase greet $name")
        return "Hello $name"
    }
}