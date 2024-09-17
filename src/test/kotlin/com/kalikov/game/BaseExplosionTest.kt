package com.kalikov.game

import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.kotlin.verify
import java.awt.image.BufferedImage

class BaseExplosionTest {
    private lateinit var fonts: TestFonts
    private lateinit var clock: TestClock
    private lateinit var game: Game
    private lateinit var explosion: BaseExplosion

    @BeforeEach
    fun beforeEach() {
        clock = TestClock()
        fonts = TestFonts()
        game = mockGame(imageManager =  TestImageManager(fonts), clock = clock)

        explosion = BaseExplosion(game)
    }

    @Test
    fun `should fire event on destroy`() {
        explosion.destroy()
        explosion.update()
        verify(game.eventManager).fireEvent(BaseExplosion.Destroyed(explosion))
    }

    @Test
    fun `should draw explosion frame 1`() {
        val image = BufferedImage(BaseExplosion.SIZE.toInt(), BaseExplosion.SIZE.toInt(), BufferedImage.TYPE_INT_ARGB)
        explosion.draw(AwtScreenSurface(fonts, image))

        assertImageEquals("bullet_explosion1.png", image)
    }

    @Test
    fun `should draw explosion frame 2`() {
        val image = BufferedImage(BaseExplosion.SIZE.toInt(), BaseExplosion.SIZE.toInt(), BufferedImage.TYPE_INT_ARGB)
        explosion.update()
        clock.tick(BaseExplosion.ANIMATION_INTERVAL)
        explosion.update()
        explosion.draw(AwtScreenSurface(fonts, image))

        assertImageEquals("bullet_explosion2.png", image)
    }

    @Test
    fun `should draw explosion frame 3`() {
        val image = BufferedImage(BaseExplosion.SIZE.toInt(), BaseExplosion.SIZE.toInt(), BufferedImage.TYPE_INT_ARGB)
        explosion.update()
        clock.tick(2 * BaseExplosion.ANIMATION_INTERVAL)
        explosion.update()
        explosion.draw(AwtScreenSurface(fonts, image))

        assertImageEquals("bullet_explosion3.png", image)
    }

    @Test
    fun `should draw explosion frame 4`() {
        val image = BufferedImage(BaseExplosion.SIZE.toInt(), BaseExplosion.SIZE.toInt(), BufferedImage.TYPE_INT_ARGB)
        explosion.update()
        clock.tick(3 * BaseExplosion.ANIMATION_INTERVAL)
        explosion.update()
        explosion.draw(AwtScreenSurface(fonts, image))

        assertImageEquals("big_explosion1.png", image)
    }

    @Test
    fun `should draw explosion frame 5`() {
        val image = BufferedImage(BaseExplosion.SIZE.toInt(), BaseExplosion.SIZE.toInt(), BufferedImage.TYPE_INT_ARGB)
        explosion.update()
        clock.tick(4 * BaseExplosion.ANIMATION_INTERVAL)
        explosion.update()
        explosion.draw(AwtScreenSurface(fonts, image))

        assertImageEquals("big_explosion2.png", image)
    }
}