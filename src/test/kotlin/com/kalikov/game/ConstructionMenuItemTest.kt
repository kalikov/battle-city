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
        val stageManager: StageManager = mock()
        val eventManager: EventManager = mock()

        whenever(stageManager.constructionMap).thenReturn(
            StageMapConfig(
                emptyList(),
                Point(12, 24),
                Point(8, 24),
                emptyList()
            )
        )

        val item = ConstructionMenuItem(mock(), eventManager, mock(), stageManager, mock(), mock())
        item.execute()

        val captor = argumentCaptor<Scene.Start>()
        verify(eventManager).fireEvent(captor.capture())

        val event = captor.firstValue
        assertIs<ConstructionScene>(event.sceneFactory())
    }
}