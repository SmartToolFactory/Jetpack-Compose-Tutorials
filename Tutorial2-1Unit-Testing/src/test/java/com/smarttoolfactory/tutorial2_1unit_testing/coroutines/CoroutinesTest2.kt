package com.smarttoolfactory.tutorial2_1unit_testing.coroutines

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.common.truth.Truth
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
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

        val result = (viewModel.result as? ResponseResult.Success)?.data
        // then
        Truth.assertThat(loading).isInstanceOf(ResponseResult.Loading::class.java)
        Truth.assertThat(result).isEqualTo("Result")
    }

    @Test
    fun loadResultWithDelayToFlowTest() = runTest {
        // given
        val viewModel = CoroutinesViewModel()
        // when
        viewModel.loadResultWithDelayToStateFlow()
        val loading = viewModel.messageFlow.value
        advanceUntilIdle()
        val result = (viewModel.messageFlow.value as ResponseResult.Success).data
        // then
        Truth.assertThat(loading).isInstanceOf(ResponseResult.Loading::class.java)
        Truth.assertThat(result).isEqualTo("Result")
    }

    @Test
    fun statedFlowTest() = runTest {
        val flow = MutableStateFlow(0)
        // 🔥 This test does not complete, it times out
        flow.collect {}
    }

    @Test
    fun stateFlowTest2() = runTest {
        val flow = MutableStateFlow(0)

        val list = mutableListOf<Int>()

        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            flow.collect {
                list.add(it)
            }
        }

        flow.emit(1)
        flow.emit(3)
        flow.emit(4)

        Truth.assertThat(list).containsExactly(0, 1, 3, 4)
    }

    @Test
    fun sharedFlowTest() = runTest {
        val flow = MutableSharedFlow<Int>()
        // 🔥 This test does not complete, it times out
        flow.collect {}
    }

    @Test
    fun sharedFlowTest2() = runTest {
        val flow = MutableSharedFlow<Int>()

        val list = mutableListOf<Int>()

        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            flow.collect {
                list.add(it)
            }
        }

        flow.emit(1)
        flow.emit(3)
        flow.emit(4)

        Truth.assertThat(list).containsExactly(1, 3, 4)
    }

    @Test
    fun continuouslyCollect() = runTest {
        val dataSource = FakeDataSource()
        val repository = Repository(dataSource)

        val values = mutableListOf<Int>()
        /*
            A scope for background work.
            This scope is automatically cancelled when the test finishes. The coroutines in this
            scope are run as usual when using advanceTimeBy and runCurrent. advanceUntilIdle,
            on the other hand, will stop advancing the virtual time once only the coroutines
            in this scope are left unprocessed.

            Failures in coroutines in this scope do not terminate the test. Instead, they are
            reported at the end of the test. Likewise, failure in the TestScope itself will
            not affect its backgroundScope, because there's no parent-child
            relationship between them.
         */
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            repository.scores().toList(values)
        }

        // 🔥 This test fails with After waiting for 10s, the test coroutine is not completing,
        // there were active child jobs: ["coroutine#3":StandaloneCoroutine{Active}@2ea4aea1]
//        launch(UnconfinedTestDispatcher(testScheduler)) {
//            repository.scores().toList(values)
//        }

        assertEquals(0, values.size)

        dataSource.emit(1)
        assertEquals(10, values[0]) // Assert on the list contents

        dataSource.emit(2)
        dataSource.emit(3)
        assertEquals(30, values[2])

        assertEquals(3, values.size) // Assert the number of items collected
    }
}

private class Repository(private val dataSource: DataSource) {
    fun scores(): Flow<Int> {
        return dataSource.counts().map { it * 10 }
    }
}

private class FakeDataSource : DataSource {
    private val flow = MutableSharedFlow<Int>()
    suspend fun emit(value: Int) = flow.emit(value)
    override fun counts(): Flow<Int> = flow
}

interface DataSource {
    fun counts(): Flow<Int>
}

private class CoroutinesViewModel : ViewModel() {

    var result by mutableStateOf<ResponseResult>(ResponseResult.Idle)

    private val _message = MutableStateFlow("")
    val message: StateFlow<String> get() = _message

    private val _messageFlow = MutableStateFlow<ResponseResult>(ResponseResult.Idle)
    val messageFlow: StateFlow<ResponseResult> get() = _messageFlow

    fun loadMessage() {
        viewModelScope.launch {
            _message.value = "Greetings!"
        }
    }

    fun loadResultWithDelay() {
        viewModelScope.launch {
            result = ResponseResult.Loading
            val resultFromApi = getResult()
            result = resultFromApi
        }
    }

    fun loadResultWithDelayToStateFlow() {
        viewModelScope.launch {
            _messageFlow.value = ResponseResult.Loading
            val resultFromApi = getResult()
            _messageFlow.value = resultFromApi
        }
    }

    private suspend fun getResult(): ResponseResult {
        delay(100)
        return ResponseResult.Success("Result")
    }
}

internal sealed class ResponseResult {
    object Idle : ResponseResult()
    object Loading : ResponseResult()
    data class Success(val data: String) : ResponseResult()
}
