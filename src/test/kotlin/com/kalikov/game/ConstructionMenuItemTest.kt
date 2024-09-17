package com.kalikov.game

import org.junit.jupiter.api.Test
import org.mockito.ArgumentMatchers.anyInt
import org.mockito.kotlin.argumentCaptor
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import java.awt.image.BufferedImage
import kotlin.test.assertIs

class ConstructionMenuItemTest {
    @Test
    fun `should start construction scene on execute`() {
        val fonts = TestFonts()
        val game = mockGame(imageManager = TestImageManager(fonts))
        whenever(game.screen.createSurface(px(anyInt()), px(anyInt()))).thenAnswer {
            val image = BufferedImage(it.getArgument(0), it.getArgument(1), BufferedImage.TYPE_INT_ARGB)
            AwtScreenSurface(fonts, image)
        }
        val stageManager: StageManager = mock()

        whenever(stageManager.constructionMap).thenReturn(
            StageMapConfig(
                base = TilePoint(t(12), t(24)),
                playerSpawnPoints = listOf(TilePoint(t(8), t(24))),
                enemySpawnPoints = emptyList(),
            )
        )

        val item = ConstructionMenuItem(game, stageManager)
        item.execute()

        val captor = argumentCaptor<Scene.Start>()
        verify(game.eventManager).fireEvent(captor.capture())

        val event = captor.firstValue
        assertIs<ConstructionScene>(event.sceneFactory())
    }
}