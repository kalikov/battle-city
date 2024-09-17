package com.kalikov.game

import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.verifyNoMoreInteractions
import java.awt.image.BufferedImage

class BaseTest {
    private lateinit var fonts: TestFonts
    private lateinit var eventRouter: EventRouter
    private lateinit var base: Base

    @BeforeEach
    fun beforeEach() {
        eventRouter = mock()
        fonts = TestFonts()
        base = Base(eventRouter, TestImageManager(fonts))
    }

    @Test
    fun `should draw base`() {
        val image = BufferedImage(Base.SIZE.toInt(), Base.SIZE.toInt(), BufferedImage.TYPE_INT_ARGB)
        base.draw(AwtScreenSurface(fonts, image))

        assertImageEquals("base.png", image)
    }

    @Test
    fun `should draw destroyed base`() {
        val image = BufferedImage(Base.SIZE.toInt(), Base.SIZE.toInt(), BufferedImage.TYPE_INT_ARGB)
        base.hit()
        base.draw(AwtScreenSurface(fonts, image))

        assertImageEquals("base_destroyed.png", image)
    }

    @Test
    fun `should be hit only once`() {
        base.hit()
        verify(eventRouter).fireEvent(Base.Hit(base))

        base.hit()
        verifyNoMoreInteractions(eventRouter)
    }
}