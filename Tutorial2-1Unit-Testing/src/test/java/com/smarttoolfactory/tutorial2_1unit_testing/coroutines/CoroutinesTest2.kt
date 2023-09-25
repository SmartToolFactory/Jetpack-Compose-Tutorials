package com.smarttoolfactory.tutorial2_1unit_testing.coroutines

import com.google.common.truth.Truth
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
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
        val result = (viewModel.result as ResponseResult.Success).data
        // then
        Truth.assertThat(loading).isInstanceOf(ResponseResult.Loading::class.java)
        Truth.assertThat(result).isEqualTo("Result")
    }

    @Test
    fun sharedFlowTest() = runTest {
        val flow = MutableSharedFlow<Int>()
        // 🔥 This test does not complete, it times out
        flow.collect {

        }
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

class Repository(private val dataSource: DataSource) {
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