package com.kalikov.game

import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.ArgumentMatchers.anyInt
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import java.awt.image.BufferedImage
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class ConstructionSceneTest {
    private lateinit var game: Game
    private lateinit var stageManager: StageManager

    private lateinit var constructionScene: ConstructionScene

    @BeforeEach
    fun beforeEach() {
        val fonts = TestFonts()
        game = mockGame(imageManager = TestImageManager(fonts))
        whenever(game.screen.createSurface(px(anyInt()), px(anyInt()))).thenAnswer {
            val image = BufferedImage(it.getArgument(0), it.getArgument(1), BufferedImage.TYPE_INT_ARGB)
            AwtScreenSurface(fonts, image)
        }
        stageManager = mock()
        whenever(stageManager.constructionMap).thenReturn(
            StageMapConfig(
                base = TilePoint(),
                playerSpawnPoints = emptyList(),
                enemySpawnPoints = emptyList(),
            ),
        )

        constructionScene = ConstructionScene(game, stageManager)
    }

    @Test
    fun `should subscribe`() {
        verify(game.eventManager).addSubscriber(
            constructionScene,
            setOf(Keyboard.KeyPressed::class)
        )
    }
}