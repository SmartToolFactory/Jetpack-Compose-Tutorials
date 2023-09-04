package com.smarttoolfactory.tutorial2_1unit_testing.coroutines

import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

@OptIn(ExperimentalCoroutinesApi::class)
class CoroutinesTest1 {

    @Test
    fun standardTest() = runTest {
        val userRepo = UserRepository()

        launch { userRepo.registerUsers("Alice") }
        launch { userRepo.registerUsers("Bob") }

        assertEquals(listOf("Alice", "Bob"), userRepo.getAllUsers()) // ❌ Fails
    }

    @Test
    fun standardTestWithJoin() = runTest {
        val userRepo = UserRepository()

        val job1 = launch { userRepo.registerUsers("Alice") }
        val job2 = launch { userRepo.registerUsers("Bob") }

        job1.join()
        job2.join()
        assertEquals(listOf("Alice", "Bob"), userRepo.getAllUsers()) // ✅ Passes
    }

    // 🔥 This test passes with registerUserAsync too
    @Test
    fun standardTest2() = runTest {
        val userRepo = UserRepository()

        launch { userRepo.registerUsers("Alice") }
        launch { userRepo.registerUsers("Bob") }
        advanceUntilIdle() // Yields to perform the registrations

        assertEquals(listOf("Alice", "Bob"), userRepo.getAllUsers()) // ✅ Passes
    }

    /*
        UnconfinedTestDispatcher
        When new coroutines are started on an UnconfinedTestDispatcher,
        they are started eagerly on the current thread. This means that they’ll
        start running immediately, without waiting for their coroutine builder to return.
        In many cases, this dispatching behavior results in simpler test code,
        as you don’t need to manually yield the test thread to let new coroutines run.

        However, this behavior is different from what you’ll see in production with
        non-test dispatchers. If your test focuses on concurrency,
        prefer using StandardTestDispatcher instead.
     */


    // 🔥 This test fails with registerUserAsync(delay)
    @Test
    fun unconfinedTest() = runTest(UnconfinedTestDispatcher()) {
        val userRepo = UserRepository()

        launch { userRepo.registerUsers("Alice") }
        launch { userRepo.registerUsers("Bob") }

        assertEquals(listOf("Alice", "Bob"), userRepo.getAllUsers()) // ✅ Passes
    }

    @Test
    fun unconfinedTest2() = runTest(UnconfinedTestDispatcher()) {
        val userRepo = UserRepository()

        launch { userRepo.registerUserAsync("Alice") }
        launch { userRepo.registerUserAsync("Bob") }
        // 🔥 Need to call for past to test
        advanceUntilIdle()

        assertEquals(listOf("Alice", "Bob"), userRepo.getAllUsers()) // ✅ Passes
    }

    /*
        Remember that UnconfinedTestDispatcher starts new coroutines eagerly,
        but this doesn’t mean that it’ll run them to completion eagerly as well.
        If the new coroutine suspends, other coroutines will resume executing.

        For example, the new coroutine launched within this test will register Alice,
        but then it suspends when delay is called. This lets the top-level coroutine
        proceed with the assertion, and the test fails as Bob is not registered yet:
      */
    @Test
    fun yieldingTest() = runTest(UnconfinedTestDispatcher()) {
        val userRepo = UserRepository()

        launch {
            userRepo.registerUsers("Alice")
            delay(10L)
            userRepo.registerUsers("Bob")
        }

        assertEquals(listOf("Alice", "Bob"), userRepo.getAllUsers()) // ❌ Fails
    }

    /*
        Like Dispatchers.Unconfined, this one does not provide guarantees about the execution
        order when several coroutines are queued in this dispatcher.
        However, we ensure that the launch and async blocks at the top level of
        runTest are entered eagerly. This allows launching child coroutines and not calling
        runCurrent for them to start executing.

     */
    @Test
    fun testEagerlyEnteringChildCoroutines() = runTest(UnconfinedTestDispatcher()) {
        println("🍏before launch")

        var entered = false
        val deferred = CompletableDeferred<Unit>()
        var completed = false

        launch {
            entered = true
            deferred.await()
            completed = true
            println("🚀 inside launch")
        }

        println("🍎 after launch")
        assertTrue(entered) // `entered = true` already executed.
        assertFalse(completed) // however, the child coroutine then suspended, so it is enqueued.
        deferred.complete(Unit) // resume the coroutine.
        assertTrue(completed) // now the child coroutine is immediately completed.
        // ✅ Passes
    }

}
