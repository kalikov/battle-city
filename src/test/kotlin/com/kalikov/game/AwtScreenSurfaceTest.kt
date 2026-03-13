package com.kalikov.game

import com.kalikov.engine.ARGB
import com.kalikov.engine.AwtScreenSurface
import com.kalikov.engine.px
import org.junit.jupiter.api.Test
import java.awt.image.BufferedImage

class AwtScreenSurfaceTest {
    @Test
    fun test() {
        val fonts = TestFonts()
        val image = BufferedImage(100, 100, BufferedImage.TYPE_INT_ARGB)
        val surface = AwtScreenSurface(fonts, image)

        surface.fillText("test", 0.px, 50.px, ARGB.RED, Globals.FONT_BIG) { _, _, _, _ ->
            ARGB.WHITE
        }
    }
}