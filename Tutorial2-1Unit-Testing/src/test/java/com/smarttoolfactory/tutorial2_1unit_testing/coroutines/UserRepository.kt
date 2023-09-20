package com.smarttoolfactory.tutorial2_1unit_testing.coroutines

import kotlinx.coroutines.delay

class UserRepository {
    private val users = mutableListOf<String>()

    fun registerUsers(user: String) {
        users.add(user)
    }

    suspend fun registerUserWithDelay(user: String) {
        delay(100)
        users.add(user)
    }

    fun getAllUsers(): MutableList<String> {
        return users
    }
}

