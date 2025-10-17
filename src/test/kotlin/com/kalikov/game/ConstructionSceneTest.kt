package com.kalikov.game

import com.kalikov.engine.AwtScreenSurface
import com.kalikov.engine.Keyboard
import com.kalikov.engine.px
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.ArgumentMatchers.anyInt
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import java.awt.image.BufferedImage

class ConstructionSceneTest {
    private lateinit var game: BattleCityGame

    private lateinit var constructionScene: ConstructionScene

    @BeforeEach
    fun beforeEach() {
        val fonts = TestFonts()
        game = mockGame(imageManager = TestImageManager(fonts))
        whenever(game.screen.createSurface(px(anyInt()), px(anyInt()))).thenAnswer {
            val image = BufferedImage(it.getArgument(0), it.getArgument(1), BufferedImage.TYPE_INT_ARGB)
            AwtScreenSurface(fonts, image)
        }
        whenever(game.stageManager.constructionMap).thenReturn(
            StageMapConfig(
                base = TilePoint(),
                playerSpawnPoints = emptyList(),
                enemySpawnPoints = emptyList(),
            ),
        )

        constructionScene = ConstructionScene(game, mock())
    }

    @Test
    fun `should subscribe on activate`() {
        constructionScene.activate()

        verify(game.eventManager).addSubscriber(
            constructionScene,
            arrayOf(Keyboard.KeyPressed::class)
        )
    }
}