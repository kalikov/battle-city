package com.kalikov.game

import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.kotlin.mock
import kotlin.test.assertEquals

class DefaultStageManagerTest {
    private lateinit var eventManager: EventManager
    private lateinit var stageManager: StageManager

    @BeforeEach
    fun beforeEach() {
        eventManager = mock()
        stageManager = DefaultStageManager(eventManager)
    }

    @Test
    fun `should update high score`() {
        val stageMapConfig = StageMapConfig(emptyList(), Point(), Point(), emptyList())
        stageManager.init(listOf(Stage(stageMapConfig, 0, emptyList())), stageMapConfig)

        val points = mockPoints(value = 40000)
        stageManager.player.notify(PointsFactory.PointsCreated(points))

        stageManager.reset()

        assertEquals(40000, stageManager.highScore)
        assertEquals(0, stageManager.player.score)
        assertEquals(40000, stageManager.player.previousScore)
    }
}