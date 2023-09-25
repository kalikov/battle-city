package com.kalikov.game

import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import java.awt.image.BufferedImage
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class BulletTest {
    private lateinit var fonts: TestFonts
    private lateinit var eventManager: EventManager
    private lateinit var imageManager: ImageManager
    private lateinit var pauseManager: PauseManager
    private lateinit var bullet: Bullet

    @BeforeEach
    fun beforeEach() {
        fonts = TestFonts()
        imageManager = TestImageManager(fonts)
        eventManager = mock()
        pauseManager = mock()
        val tank = mockTank(eventManager, pauseManager, imageManager)
        bullet = tank.createBullet()
    }

    @Test
    fun `should fire event on destroy`() {
        bullet.destroy()
        bullet.update()
        verify(eventManager).fireEvent(Bullet.Destroyed(bullet))
    }

    @Test
    fun `should be destroyed when goes out of bounds`() {
        bullet.outOfBounds()
        assertTrue(bullet.isDestroyed)
    }

    @Test
    fun `should be destroyed when hit without explosion`() {
        bullet.hit(false)
        assertFalse(bullet.shouldExplode)
        assertTrue(bullet.isDestroyed)
    }

    @Test
    fun `should be destroyed when hit with explosion`() {
        bullet.hit(true)
        assertTrue(bullet.shouldExplode)
        assertTrue(bullet.isDestroyed)
    }

    @Test
    fun `should draw bullet with right direction`() {
        shouldDrawBullet(Direction.RIGHT, "bullet_right")
    }

    @Test
    fun `should draw bullet with up direction`() {
        shouldDrawBullet(Direction.UP, "bullet_up")
    }

    @Test
    fun `should draw bullet with down direction`() {
        shouldDrawBullet(Direction.DOWN, "bullet_down")
    }

    @Test
    fun `should draw bullet with left direction`() {
        shouldDrawBullet(Direction.LEFT, "bullet_left")
    }

    private fun shouldDrawBullet(direction: Direction, imageName: String) {
        val image = BufferedImage(Globals.TILE_SIZE, Globals.TILE_SIZE, BufferedImage.TYPE_INT_ARGB)
        bullet.direction = direction
        bullet.setPosition((Globals.TILE_SIZE - bullet.width) / 2, (Globals.TILE_SIZE - bullet.height) / 2)
        bullet.draw(AwtScreenSurface(fonts, image))

        assertImageEquals("$imageName.png", image)
    }
}