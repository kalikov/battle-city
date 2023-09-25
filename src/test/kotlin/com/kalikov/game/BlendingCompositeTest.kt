package com.kalikov.game

import org.junit.jupiter.api.Test
import java.awt.Color
import java.awt.image.BufferedImage

class BlendingCompositeTest {
    @Test
    fun `should blend using IntContext`() {
        val image = BufferedImage(16, 16, BufferedImage.TYPE_INT_ARGB)
        image.createGraphics().use {
            it.composite = BlendingComposite { _, _, _, _ -> ARGB.rgb(0xFF0000) }
            it.color = Color.WHITE
            it.fillRect(4, 4, 8, 8)
        }
        assertImageEquals("blending_argb.png", image)
    }

    @Test
    fun `should blend using GeneralContext`() {
        val image = BufferedImage(16, 16, BufferedImage.TYPE_BYTE_INDEXED)
        image.createGraphics().use {
            it.composite = BlendingComposite { _, _, _, _ -> ARGB.rgb(0xFF0000) }
            it.color = Color.WHITE
            it.fillRect(4, 4, 8, 8)
        }
        assertImageEquals("blending_rgb.png", image)
    }
}