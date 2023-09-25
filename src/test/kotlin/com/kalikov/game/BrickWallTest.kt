package com.kalikov.game

import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.kotlin.mock
import java.awt.image.BufferedImage
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class BrickWallTest {
    private lateinit var fonts: TestFonts
    private lateinit var eventManager: EventManager
    private lateinit var wall: BrickWall
    private lateinit var bullet: Bullet

    @BeforeEach
    fun beforeMethod() {
        fonts = TestFonts()
        eventManager = mock()
        wall = BrickWall(eventManager, TestImageManager(fonts), 0, 0)

        val tank = mockTank(eventManager)
        bullet = tank.createBullet()
    }

    @Test
    fun `should be initialized correctly`() {
        assertEquals(Globals.TILE_SIZE, wall.width)
        assertEquals(Globals.TILE_SIZE, wall.height)

        assertFalse(wall.isHitLeft)
        assertFalse(wall.isHitTop)
        assertFalse(wall.isHitRight)
        assertFalse(wall.isHitBottom)

        assertFalse(wall.isDestroyed)

        assertEquals(wall.bounds, wall.hitRect)
    }

    @Test
    fun `should be hit by a bullet`() {
        wall.hit(bullet)
        assertTrue(wall.isHit)
        assertFalse(wall.isDestroyed)
    }

    @Test
    fun `should be hit left by a bullet`() {
        bullet.direction = Direction.RIGHT
        wall.hit(bullet)
        assertTrue(wall.isHitLeft)
        assertFalse(wall.isDestroyed)
        assertEquals(Rect(wall.x + wall.width / 2, wall.y, wall.width / 2, wall.height), wall.hitRect)
    }

    @Test
    fun `should be hit right by a bullet`() {
        bullet.direction = Direction.LEFT
        wall.hit(bullet)
        assertTrue(wall.isHitRight)
        assertFalse(wall.isDestroyed)
        assertEquals(Rect(wall.x, wall.y, wall.width / 2, wall.height), wall.hitRect)
    }

    @Test
    fun `should be hit top by a bullet`() {
        bullet.direction = Direction.DOWN
        wall.hit(bullet)
        assertTrue(wall.isHitTop)
        assertFalse(wall.isDestroyed)
        assertEquals(Rect(wall.x, wall.y + wall.height / 2, wall.width, wall.height / 2), wall.hitRect)
    }

    @Test
    fun `should be hit bottom by a bullet`() {
        bullet.direction = Direction.UP
        wall.hit(bullet)
        assertTrue(wall.isHitBottom)
        assertFalse(wall.isDestroyed)
        assertEquals(Rect(wall.x, wall.y, wall.width, wall.height / 2), wall.hitRect)
    }

    @Test
    fun `should be destroyed by an enhanced bullet`() {
        bullet.type = Bullet.Type.ENHANCED
        wall.hit(bullet)
        assertTrue(wall.isDestroyed)
    }

    @Test
    fun `should be destroyed by being hit left twice`() {
        bullet.direction = Direction.RIGHT
        wall.hit(bullet)
        wall.hit(bullet)
        assertTrue(wall.isDestroyed)
    }

    @Test
    fun `should be destroyed by being hit right twice`() {
        bullet.direction = Direction.LEFT
        wall.hit(bullet)
        wall.hit(bullet)
        assertTrue(wall.isDestroyed)
    }

    @Test
    fun `should be destroyed by being hit top twice`() {
        bullet.direction = Direction.DOWN
        wall.hit(bullet)
        wall.hit(bullet)
        assertTrue(wall.isDestroyed)
    }

    @Test
    fun `should be destroyed by being hit down twice`() {
        bullet.direction = Direction.UP
        wall.hit(bullet)
        wall.hit(bullet)
        assertTrue(wall.isDestroyed)
    }

    @Test
    fun `should be hit from different directions correctly`() {
        bullet.direction = Direction.DOWN
        wall.hit(bullet)

        bullet.direction = Direction.LEFT
        wall.hit(bullet)

        assertTrue(wall.isHitTop)
        assertTrue(wall.isHitRight)
        assertFalse(wall.isDestroyed)
        assertEquals(Rect(wall.x, wall.y + wall.height / 2, wall.width / 2, wall.height / 2), wall.hitRect)

        bullet.direction = Direction.UP
        wall.hit(bullet)
        assertTrue(wall.isDestroyed)
    }

    @Test
    fun `should draw brick wall hit right`() {
        bullet.direction = (Direction.LEFT)
        wall.hit(bullet)

        val image = BufferedImage(wall.width, wall.height, BufferedImage.TYPE_INT_ARGB)
        wall.draw(AwtScreenSurface(fonts, image))

        assertImageEquals("brick_wall_hit_right.png", image)
    }

    @Test
    fun `should draw brick wall hit left`() {
        bullet.direction = Direction.RIGHT
        wall.hit(bullet)

        val image = BufferedImage(wall.width, wall.height, BufferedImage.TYPE_INT_ARGB)
        wall.draw(AwtScreenSurface(fonts, image))

        assertImageEquals("brick_wall_hit_left.png", image)
    }

    @Test
    fun `should draw brick wall hit top and right`() {
        bullet.direction = Direction.LEFT
        wall.hit(bullet)
        bullet.direction = Direction.DOWN
        wall.hit(bullet)

        val image = BufferedImage(wall.width, wall.height, BufferedImage.TYPE_INT_ARGB)
        wall.draw(AwtScreenSurface(fonts, image))

        assertImageEquals("brick_wall_hit_top_right.png", image)
    }

    @Test
    fun `should draw brick wall hit top and left`() {
        bullet.direction = (Direction.RIGHT)
        wall.hit(bullet)
        bullet.direction = (Direction.DOWN)
        wall.hit(bullet)

        val image = BufferedImage(wall.width, wall.height, BufferedImage.TYPE_INT_ARGB)
        wall.draw(AwtScreenSurface(fonts, image))

        assertImageEquals("brick_wall_hit_top_left.png", image)
    }
}