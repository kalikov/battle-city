package com.kalikov.game

import org.junit.jupiter.api.Test
import org.mockito.kotlin.argumentCaptor
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import kotlin.test.assertIs

class ConstructionMenuItemTest {
    @Test
    fun `should start construction scene on execute`() {
        val game = mockGame()
        val stageManager: StageManager = mock()

        whenever(stageManager.constructionMap).thenReturn(
            StageMapConfig(
                emptyList(),
                Point(12, 24),
                listOf(Point(8, 24)),
                emptyList()
            )
        )

        val item = ConstructionMenuItem(game, stageManager, mock(), mock())
        item.execute()

        val captor = argumentCaptor<Scene.Start>()
        verify(game.eventManager).fireEvent(captor.capture())

        val event = captor.firstValue
        assertIs<ConstructionScene>(event.sceneFactory())
    }
}