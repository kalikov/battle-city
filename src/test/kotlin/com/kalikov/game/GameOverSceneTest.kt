package com.kalikov.game

import com.kalikov.engine.AwtScreenSurface
import com.kalikov.engine.Screen
import com.kalikov.engine.px
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.ArgumentMatchers.anyInt
import org.mockito.kotlin.doAnswer
import org.mockito.kotlin.mock
import java.awt.image.BufferedImage

class GameOverSceneTest {
    private lateinit var fonts: TestFonts
    private lateinit var game: BattleCityGame

    @BeforeEach
    fun beforeEach() {
        fonts = TestFonts()

        val screen: Screen = mock {
            on { createSurface(anyInt().px, anyInt().px) } doAnswer {
                val image = BufferedImage(it.getArgument(0), it.getArgument(1), BufferedImage.TYPE_INT_ARGB)
                AwtScreenSurface(fonts, image)
            }
        }
        game = mockGame(screen = screen, imageManager = TestImageManager(fonts))
    }

    @Test
    fun `should draw game over scene`() {
        val scene = GameOverScene(game, mock())

        val image = BufferedImage(
            Globals.CANVAS_WIDTH.toInt(),
            Globals.CANVAS_HEIGHT.toInt(),
            BufferedImage.TYPE_INT_RGB
        )
        scene.draw(AwtScreenSurface(fonts, image))

        assertImageEquals("game_over.png", image)
    }
}