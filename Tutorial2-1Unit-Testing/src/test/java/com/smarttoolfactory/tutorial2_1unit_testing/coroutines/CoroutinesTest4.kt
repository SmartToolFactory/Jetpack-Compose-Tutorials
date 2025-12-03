package com.smarttoolfactory.tutorial2_1unit_testing.coroutines

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.common.truth.Truth
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.RegisterExtension

class FlowUseCase(val dispatcher: CoroutineDispatcher = Dispatchers.Default) {

    fun getResultFlow(): Flow<List<String>> = flow {
        val result = getResult()
        emit(result)
    }
        .map { res: List<Int> ->
            res.map {
                "Item $it"
            }
        }
        .flowOn(dispatcher)


    fun getResultFlowWithDelay() = flow {
        println("FlowUseCase getResultFlowWithDelay() map in ${Thread.currentThread().name}")
        val result = getResultWithDelay()
        emit(result)
    }
        .map { res: List<Int> ->
            res.map {
                "Item With delay $it"
            }
        }
        .flowOn(dispatcher)

    private fun getResult(): List<Int> {
        return listOf(1, 2, 3)
    }

    private suspend fun getResultWithDelay(): List<Int> {
        delay(100)
        return listOf(1, 2, 3)
    }
}

class FlowViewModel(private val flowUseCase: FlowUseCase) : ViewModel() {
    private val _resultFlow = MutableStateFlow<List<String>>(listOf())
    val resultFlow: StateFlow<List<String>> get() = _resultFlow

    fun getResultFlow() {
        viewModelScope.launch {
            flowUseCase.getResultFlow().collect {
                _resultFlow.value = it
            }
        }
    }

    fun getResultFlowWitDelay() {
        viewModelScope.launch {
            flowUseCase.getResultFlowWithDelay().collect {
                _resultFlow.value = it
            }
        }
    }

}

@OptIn(ExperimentalCoroutinesApi::class)
class FlowTest {

    companion object {
        @JvmField
        @RegisterExtension
        val mainDispatcherExtension = MainDispatcherExtension()
    }

    @Test
    fun useCaseTest1() = runTest {
        val result = mutableListOf<String>()
        val useCase = FlowUseCase()

        println("Start")
        useCase.getResultFlow().collect {
            result.addAll(it)
        }

        // ✅ Passes
        Truth.assertThat(result).hasSize(3)
    }

    @Test
    fun useCaseWithDelayTest1() = runTest {
        val result = mutableListOf<String>()

        val useCase = FlowUseCase()

        println("Start")
        launch(UnconfinedTestDispatcher(testScheduler)) {
            useCase.getResultFlowWithDelay().collect {
                result.addAll(it)
            }
        }

        advanceUntilIdle()

        // ❌ Fails
        Truth.assertThat(result).hasSize(3)
    }

    @Test
    fun useCaseWithDelayTest2() = runTest {
        val result = mutableListOf<String>()
        val useCase = FlowUseCase(mainDispatcherExtension.testDispatcher)

        println("Start")
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            useCase.getResultFlowWithDelay().collect {
                result.addAll(it)
            }
        }

        advanceUntilIdle()

        // ❌ Fails
        Truth.assertThat(result).hasSize(3)
    }

    @Test
    fun useCaseWithDelayTest3() = runTest {
        val result = mutableListOf<String>()

        val useCase = FlowUseCase()

        println("Start")
        val job = launch {
            useCase.getResultFlowWithDelay().collect {
                result.addAll(it)
            }
        }

        job.join()

        // ✅ Passes
        Truth.assertThat(result).hasSize(3)
    }

    /*
         runTest(UnconfinedTestDispatcher()) and backgroundScope.launch
         for ViewModel to collect in viewModelScope
     */
    @Test
    fun viewModelWitFlowCollectTest1() = runTest(UnconfinedTestDispatcher()) {
        val viewModel = FlowViewModel(FlowUseCase())
        backgroundScope.launch {
            viewModel.getResultFlow()
        }

        val expected = viewModel.resultFlow.value

        // ✅ Passes
        Truth.assertThat(expected).hasSize(3)
    }

    /*
        Using mainDispatcherExtension.testDispatcher as FlowCase param results success
     */
    @Test
    fun viewModelWitFlowCollectTest2() = runTest(UnconfinedTestDispatcher()) {
        val viewModel = FlowViewModel(FlowUseCase(mainDispatcherExtension.testDispatcher))
        viewModel.getResultFlow()

        val expected = viewModel.resultFlow.value

        // ✅ Passes
        Truth.assertThat(expected).hasSize(3)
    }

    /*
        This tes passes if mainDispatcherExtension.testDispatcher and advanceUntilIdle are used
     */
    @Test
    fun viewModelWitFlowWithDelayCollectTest1() = runTest {
        val viewModel = FlowViewModel(FlowUseCase(mainDispatcherExtension.testDispatcher))
        viewModel.getResultFlowWitDelay()

        advanceUntilIdle()

        val expected = viewModel.resultFlow.value

        // ✅ Passes
        Truth.assertThat(expected).hasSize(3)
    }

    @Test
    fun viewModelWitFlowWithDelayCollectTest2() = runTest(UnconfinedTestDispatcher()) {
        val viewModel = FlowViewModel(FlowUseCase())
        val job = backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            viewModel.getResultFlowWitDelay()
        }

        advanceUntilIdle()
        job.join()

        val expected = viewModel.resultFlow.value

        // ❌ Fails
        Truth.assertThat(expected).hasSize(3)
    }
}
