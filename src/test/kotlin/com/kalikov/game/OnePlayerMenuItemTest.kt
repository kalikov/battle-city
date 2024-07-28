package com.kalikov.game

import org.junit.jupiter.api.Test
import org.mockito.kotlin.argumentCaptor
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import kotlin.test.assertIs

class OnePlayerMenuItemTest {
    @Test
    fun `should start stage scene on execute`() {
        val eventManager: EventManager = mock()
        val stageManager: StageManager = mock()

        whenever(stageManager.stage).thenReturn(
            Stage(
                StageMapConfig(
                    emptyList(),
                    Point(12, 24),
                    emptyList(),
                    emptyList()
                ),
                1,
                emptyList()
            )
        )
        val player = Player(eventManager)
        whenever(stageManager.players).thenReturn(listOf(player))

        val item = OnePlayerMenuItem(mock(), eventManager, mock(), stageManager, mock(), mock())
        item.execute()

        val captor = argumentCaptor<Scene.Start>()
        verify(eventManager).fireEvent(captor.capture())

        val event = captor.firstValue
        assertIs<StageScene>(event.sceneFactory())
    }
}