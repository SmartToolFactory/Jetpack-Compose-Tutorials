package com.smarttoolfactory.tutorial2_1unit_testing.coroutines

import com.google.common.truth.Truth
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Test
import org.junit.jupiter.api.extension.RegisterExtension

@OptIn(ExperimentalCoroutinesApi::class)
class SharedFlowTest {

    companion object {
        @JvmField
        @RegisterExtension
        val mainDispatcherExtension = MainDispatcherExtension()
    }

    @Test
    fun repositoryTest() = runTest {
        val fakeRepository = SensoryRepositoryImpl()
        val values = mutableListOf<Int>()

        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            fakeRepository.getSensorFlow().collect {
                values.add(it)
            }
        }

        fakeRepository.init()

        // ✅ Passes
        Truth.assertThat(values).hasSize(5)
    }

    @Test
    fun repositoryTest2() = runTest {
        val fakeRepository = SensoryRepositoryImpl()
        val values = mutableListOf<Int>()

        val job = launch(UnconfinedTestDispatcher(testScheduler)) {
            fakeRepository.getSensorFlow().collect {
                values.add(it)
            }
        }

        fakeRepository.init()
        job.cancel()

        // ✅ Passes
        Truth.assertThat(values).hasSize(5)
    }

    @Test
    fun repositoryTest3() = runTest {
        val fakeRepository = SensoryRepositoryImpl()
        val values = mutableListOf<Int>()

//        fakeRepository.getCacheFlow().collect {
//            values.add(it)
//        }

//        val job = launch(UnconfinedTestDispatcher(testScheduler)) {
//            fakeRepository.getCacheFlow().collect {
//                values.add(it)
//            }
//        }
//
//        job.join()

        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            fakeRepository.getCacheFlow().collect {
                values.add(it)
            }
        }

        advanceUntilIdle()

        // ✅ Passes
        Truth.assertThat(values).hasSize(3)
    }
}

private class SensoryRepositoryImpl : SensorRepository {
    private val flow = MutableSharedFlow<Int>()

    private var counter: Int = 0

    override suspend fun init() {
        while (counter < 5) {
            counter++
            flow.emit(counter)
            delay(30)
        }
    }

    override fun getSensorFlow(): Flow<Int> {
        return flow
    }

    override fun getCacheFlow(): Flow<Int> = flow {
        delay(100)
        emit(1)
        delay(100)
        emit(2)
        delay(100)
        emit(3)
    }
}

interface SensorRepository {
    suspend fun init(): Unit
    fun getSensorFlow(): Flow<Int>
    fun getCacheFlow(): Flow<Int>
}