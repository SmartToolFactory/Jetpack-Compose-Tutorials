package com.smarttoolfactory.tutorial2_1unit_testing.coroutines

import com.google.common.truth.Truth
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.RegisterExtension

// These are JUnit 5 tests
@OptIn(ExperimentalCoroutinesApi::class)
class CoroutinesTest2 {

    companion object {
        @JvmField
        @RegisterExtension
        val mainDispatcherExtension = MainDispatcherExtension()
    }

    @Test
    fun loadMessageTest() = runTest {
        val viewModel = CoroutinesViewModel()
        viewModel.loadMessage()
        Truth.assertThat(viewModel.message.value).isEqualTo("Greetings!")
    }

    @Test
    fun loadResultWithDelayTest() = runTest {
        // given
        val viewModel = CoroutinesViewModel()
        // when
        viewModel.loadResultWithDelay()
        val loading = viewModel.result
        advanceUntilIdle()
        val result = (viewModel.result as ResponseResult.Success).data
        // then
        Truth.assertThat(loading).isInstanceOf(ResponseResult.Loading::class.java)
        Truth.assertThat(result).isEqualTo("Result")
    }
}
