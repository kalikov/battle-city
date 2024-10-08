package com.kalikov.game

import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever
import java.awt.image.BufferedImage

class HighScoreSceneTest {
    private lateinit var fonts: TestFonts

    @BeforeEach
    fun beforeEach() {
        fonts = TestFonts()
    }

    @Test
    fun `should draw high score scene`() {
        val stageManager: StageManager = mock()
        val game = mockGame(imageManager = TestImageManager(fonts))
        val scene = HighScoreScene(game, stageManager, mock())

        whenever(stageManager.highScore).thenReturn(29100)

        val image = BufferedImage(
            Globals.CANVAS_WIDTH.toInt(),
            Globals.CANVAS_HEIGHT.toInt(),
            BufferedImage.TYPE_INT_RGB
        )
        scene.draw(AwtScreenSurface(fonts, image))

        assertImageEquals("high_score.png", image)
    }
}