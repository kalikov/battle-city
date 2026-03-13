package com.kalikov.game

import com.kalikov.engine.AwtScreenSurface
import com.kalikov.engine.DefaultEventManager
import com.kalikov.engine.EventManager
import com.kalikov.engine.px
import com.kalikov.util.TestClock
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever
import java.awt.image.BufferedImage
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class BulletExplosionTest {
    private lateinit var eventManager: EventManager
    private lateinit var pauseManager: PauseManager
    private lateinit var clock: TestClock
    private lateinit var fonts: TestFonts
    private lateinit var explosion: BulletExplosion

    @BeforeEach
    fun beforeEach() {
        clock = TestClock()
        fonts = TestFonts()
        eventManager = DefaultEventManager()
        pauseManager = mock()
        val bullet: BulletHandle = mock {
            on { center } doReturn 0.px
            on { middle } doReturn 0.px
        }
        explosion = BulletExplosion(
            mockGame(
                eventManager = eventManager,
                imageManager = TestImageManager(fonts),
                clock = clock
            ),
            pauseManager,
            bullet
        )
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
        whenever(pauseManager.isPaused).doReturn(true)

        repeat(BulletExplosion.ANIMATION_FRAMES + 1) {
            clock.tick(BulletExplosion.ANIMATION_INTERVAL)
            explosion.update()
        }
        assertFalse(explosion.isDestroyed)
    }

    @Test
    fun `should draw explosion frame 1`() {
        val image = BufferedImage(4.tiles.toPixel().toInt(), 4.tiles.toPixel().toInt(), BufferedImage.TYPE_INT_ARGB)
        explosion.setPosition(2.tiles.toPixel() - explosion.width / 2, 2.tiles.toPixel() - explosion.height / 2)
        explosion.draw(AwtScreenSurface(fonts, image))

        assertImageEquals("bullet_explosion1.png", image)
    }

    @Test
    fun `should draw explosion frame 2`() {
        val image = BufferedImage(4.tiles.toPixel().toInt(), 4.tiles.toPixel().toInt(), BufferedImage.TYPE_INT_ARGB)
        explosion.setPosition(2.tiles.toPixel() - explosion.width / 2, 2.tiles.toPixel() - explosion.height / 2)
        explosion.update()

        clock.tick(BulletExplosion.ANIMATION_INTERVAL)
        explosion.update()
        explosion.draw(AwtScreenSurface(fonts, image))

        assertImageEquals("bullet_explosion2.png", image)
    }

    @Test
    fun `should draw explosion frame 3`() {
        val image = BufferedImage(4.tiles.toPixel().toInt(), 4.tiles.toPixel().toInt(), BufferedImage.TYPE_INT_ARGB)
        explosion.setPosition(2.tiles.toPixel() - explosion.width / 2, 2.tiles.toPixel() - explosion.height / 2)
        explosion.update()

        clock.tick(2 * BulletExplosion.ANIMATION_INTERVAL)
        explosion.update()
        explosion.draw(AwtScreenSurface(fonts, image))

        assertImageEquals("bullet_explosion3.png", image)
    }
}