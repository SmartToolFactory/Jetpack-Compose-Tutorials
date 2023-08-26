package com.smarttoolfactory.tutorial2_1unit_testing

import com.google.common.truth.Truth
import io.mockk.clearMocks
import io.mockk.every
import io.mockk.spyk
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class Test15PrivateMethodsAndProperties {

    private val levelManager = spyk(LevelManager(), recordPrivateCalls = true)

    //    private val levelManager: LevelManager = mockk()
    private lateinit var player: Player

    @BeforeEach
    fun setUp() {
        player = Player(levelManager)
    }

    @AfterEach
    fun tearDown() {
        clearMocks(levelManager)
    }

    @Test
    fun testStatAndLevel() {

        // Given
//        every { levelManager getProperty "stat" } returns 20
//        every { levelManager getProperty "level" } returns 20

        every { levelManager setProperty "stat" value (20) }
        every { levelManager setProperty "level" value (20) }

//        levelManager.level = 20
//        levelManager.stat = 20

        // When
        val expected = player.isAboveLevelAndStat()

        // Then
        Truth.assertThat(expected).isTrue()

    }

}

class Player(private val levelManager: LevelManager) {


    fun isAboveLevelAndStat(): Boolean {
        return levelManager.isAboveLevelAndStat()
    }

}

class LevelManager {

    var stat = 0
    var level = 0

    fun isAboveLevelAndStat(): Boolean {
        return stat > 10 && level > 10
    }

}