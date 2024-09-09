package com.kalikov.game

import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.kotlin.mock
import java.awt.image.BufferedImage

class GameOverSceneTest {
    private lateinit var fonts: TestFonts

    @BeforeEach
    fun beforeEach() {
        fonts = TestFonts()
    }

    @Test
    fun `should draw game over scene`() {
        val scene = GameOverScene(mockGame(imageManager = TestImageManager(fonts)), mock(), mock())

        val image = BufferedImage(Globals.CANVAS_WIDTH, Globals.CANVAS_HEIGHT, BufferedImage.TYPE_INT_RGB)
        scene.draw(AwtScreenSurface(fonts, image))

        assertImageEquals("game_over.png", image)
    }
}