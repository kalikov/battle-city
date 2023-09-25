package com.kalikov.game

import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever
import java.awt.image.BufferedImage
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class BulletExplosionTest {
    private lateinit var clock: TestClock
    private lateinit var fonts: TestFonts
    private lateinit var pauseManager: PauseManager
    private lateinit var explosion: BulletExplosion

    @BeforeEach
    fun beforeEach() {
        clock = TestClock()
        pauseManager = mock()
        fonts = TestFonts()
        explosion = BulletExplosion(mock(), TestImageManager(fonts), clock)
    }

    @Test
    fun `should destroy explosion in four ticks`() {
        explosion.update()

        clock.tick(BulletExplosion.ANIMATION_INTERVAL)
        explosion.update()

        clock.tick(BulletExplosion.ANIMATION_INTERVAL)
        explosion.update()
        assertFalse(explosion.isDestroyed)

        clock.tick(BulletExplosion.ANIMATION_INTERVAL)
        explosion.update()
        assertTrue(explosion.isDestroyed)
    }

    @Test
    fun `should not destroy explosion when paused`() {
        whenever(pauseManager.isPaused).thenReturn(true)

        for (i in 1..4) {
            explosion.update()
        }
        assertFalse(explosion.isDestroyed)
    }

    @Test
    fun `should draw explosion frame 1`() {
        val image = BufferedImage(2 * Globals.UNIT_SIZE, 2 * Globals.UNIT_SIZE, BufferedImage.TYPE_INT_ARGB)
        explosion.setPosition(Globals.UNIT_SIZE - explosion.width / 2, Globals.UNIT_SIZE - explosion.height / 2)
        explosion.draw(AwtScreenSurface(fonts, image))

        assertImageEquals("bullet_explosion1.png", image)
    }

    @Test
    fun `should draw explosion frame 2`() {
        val image = BufferedImage(2 * Globals.UNIT_SIZE, 2 * Globals.UNIT_SIZE, BufferedImage.TYPE_INT_ARGB)
        explosion.setPosition(Globals.UNIT_SIZE - explosion.width / 2, Globals.UNIT_SIZE - explosion.height / 2)
        explosion.update()

        clock.tick(BulletExplosion.ANIMATION_INTERVAL)
        explosion.update()
        explosion.draw(AwtScreenSurface(fonts, image))

        assertImageEquals("bullet_explosion2.png", image)
    }

    @Test
    fun `should draw explosion frame 3`() {
        val image = BufferedImage(2 * Globals.UNIT_SIZE, 2 * Globals.UNIT_SIZE, BufferedImage.TYPE_INT_ARGB)
        explosion.setPosition(Globals.UNIT_SIZE - explosion.width / 2, Globals.UNIT_SIZE - explosion.height / 2)
        explosion.update()

        clock.tick(2 * BulletExplosion.ANIMATION_INTERVAL)
        explosion.update()
        explosion.draw(AwtScreenSurface(fonts, image))

        assertImageEquals("bullet_explosion3.png", image)
    }
}