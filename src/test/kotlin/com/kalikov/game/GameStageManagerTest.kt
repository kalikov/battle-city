package com.kalikov.game

import com.kalikov.engine.EventManager
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.kotlin.mock
import kotlin.test.assertEquals

class GameStageManagerTest {
    private lateinit var eventManager: EventManager
    private lateinit var stageManager: StageManager

    @BeforeEach
    fun beforeEach() {
        eventManager = mock()
        stageManager = GameStageManager(mockGame(eventManager = eventManager))
    }

    @Test
    fun `should update high score`() {
        val stageMapConfig = StageMapConfig(
            base = TilePoint(),
            playerSpawnPoints = emptyList(),
            enemySpawnPoints = emptyList(),
        )
        stageManager.init(
            listOf(Stage(stageMapConfig, 0, emptyList())),
            Stage(stageMapConfig, 0, emptyList()),
            stageMapConfig
        )

        stageManager.players[0].score(40000)

        stageManager.reset()

        assertEquals(40000, stageManager.highScore)
        assertEquals(0, stageManager.players[0].score)
        assertEquals(40000, stageManager.players[0].previousScore)
    }
}