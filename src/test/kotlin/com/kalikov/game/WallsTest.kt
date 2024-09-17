package com.kalikov.game

import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.ArgumentMatchers.anyInt
import org.mockito.kotlin.whenever
import java.awt.image.BufferedImage
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class WallsTest {
    private lateinit var fonts: TestFonts
    private lateinit var game: Game
    private lateinit var walls: Walls
    private lateinit var bullet: Bullet

    @BeforeEach
    fun beforeMethod() {
        fonts = TestFonts()
        game = mockGame(imageManager = TestImageManager(fonts))
        whenever(game.screen.createSurface(px(anyInt()), px(anyInt()))).thenAnswer {
            val image = BufferedImage(it.getArgument(0), it.getArgument(1), BufferedImage.TYPE_INT_ARGB)
            AwtScreenSurface(fonts, image)
        }

        walls = Walls(game, px(0), px(0), WallsConfig())

        val tank = stubPlayerTank(game)
        bullet = stubBullet(game, tank)
    }

    @Test
    fun `should be hit by a bullet`() {
        walls.fillBrickTile(t(0), t(0))

        assertTrue(walls.hit(bullet))
    }

    @Test
    fun `should be hit left by a bullet`() {
        bullet.direction = Direction.RIGHT
        bullet.setPosition(t(1).toPixel() - Bullet.SIZE + 1, t(1).toPixel() - Bullet.SIZE / 2)

        walls.fillBrickTile(t(1), t(0))
        walls.fillBrickTile(t(1), t(1))
        assertTrue(walls.hit(bullet))

        assertEquals(
            setOf(
                BrickTile(t(1), t(0), 0b0110),
                BrickTile(t(1), t(1), 0b0110)
            ),
            walls.config.bricks,
        )
    }

    @Test
    fun `should be hit right by a bullet`() {
        bullet.direction = Direction.LEFT
        bullet.setPosition(t(1).toPixel() - 1, t(1).toPixel() - Bullet.SIZE / 2)

        walls.fillBrickTile(t(0), t(0))
        walls.fillBrickTile(t(0), t(1))
        assertTrue(walls.hit(bullet))

        assertEquals(
            setOf(
                BrickTile(t(0), t(0), 0b1001),
                BrickTile(t(0), t(1), 0b1001)
            ),
            walls.config.bricks,
        )
    }

    @Test
    fun `should be hit top by a bullet`() {
        bullet.direction = Direction.DOWN
        bullet.setPosition(t(1).toPixel() - Bullet.SIZE / 2, t(1).toPixel() - Bullet.SIZE + 1)

        walls.fillBrickTile(t(0), t(1))
        walls.fillBrickTile(t(1), t(1))
        assertTrue(walls.hit(bullet))

        assertEquals(
            setOf(
                BrickTile(t(0), t(1), 0b0011),
                BrickTile(t(1), t(1), 0b0011)
            ),
            walls.config.bricks,
        )
    }

    @Test
    fun `should be hit bottom by a bullet`() {
        bullet.direction = Direction.UP
        bullet.setPosition(t(1).toPixel() - Bullet.SIZE / 2, t(1).toPixel() - 1)

        walls.fillBrickTile(t(0), t(0))
        walls.fillBrickTile(t(1), t(0))
        assertTrue(walls.hit(bullet))

        assertEquals(
            setOf(
                BrickTile(t(0), t(0), 0b1100),
                BrickTile(t(1), t(0), 0b1100)
            ),
            walls.config.bricks,
        )
    }

    @Test
    fun `should be destroyed by an enhanced bullet`() {
        bullet.type = Bullet.Type.ENHANCED

        walls.fillBrickTile(t(0), t(0))
        assertTrue(walls.hit(bullet))

        assertEquals(emptySet(), walls.config.bricks)
    }

    @Test
    fun `should be destroyed by being hit left twice`() {
        bullet.direction = Direction.RIGHT
        bullet.setPosition(t(1).toPixel() - Bullet.SIZE + 1, t(1).toPixel() - Bullet.SIZE / 2)

        walls.fillBrickTile(t(1), t(0))
        walls.fillBrickTile(t(1), t(1))
        assertTrue(walls.hit(bullet))

        bullet.setPosition(t(1).toPixel() + 1, bullet.y)
        assertTrue(walls.hit(bullet))

        assertEquals(emptySet(), walls.config.bricks)
    }

    @Test
    fun `should be destroyed by being hit right twice`() {
        bullet.direction = Direction.LEFT
        bullet.setPosition(t(1).toPixel() - 1, t(1).toPixel() - Bullet.SIZE / 2)

        walls.fillBrickTile(t(0), t(0))
        walls.fillBrickTile(t(0), t(1))
        assertTrue(walls.hit(bullet))

        bullet.setPosition(t(1).toPixel() - Bullet.SIZE - 1, bullet.y)
        assertTrue(walls.hit(bullet))

        assertEquals(emptySet(), walls.config.bricks)
    }

    @Test
    fun `should be destroyed by being hit top twice`() {
        bullet.direction = Direction.DOWN
        bullet.setPosition(t(1).toPixel() - Bullet.SIZE / 2, t(1).toPixel() - Bullet.SIZE + 1)

        walls.fillBrickTile(t(0), t(1))
        walls.fillBrickTile(t(1), t(1))
        assertTrue(walls.hit(bullet))

        bullet.setPosition(bullet.x, t(1).toPixel() + 1)
        assertTrue(walls.hit(bullet))

        assertEquals(emptySet(), walls.config.bricks)
    }

    @Test
    fun `should be destroyed by being hit bottom twice`() {
        bullet.direction = Direction.UP
        bullet.setPosition(t(1).toPixel() - Bullet.SIZE / 2, t(1).toPixel() - 1)

        walls.fillBrickTile(t(0), t(0))
        walls.fillBrickTile(t(1), t(0))
        assertTrue(walls.hit(bullet))

        bullet.setPosition(bullet.x, t(1).toPixel() - Bullet.SIZE - 1)
        assertTrue(walls.hit(bullet))

        assertEquals(emptySet(), walls.config.bricks)
    }

    @Test
    fun `should be hit from different directions correctly`() {
        bullet.direction = Direction.DOWN
        bullet.setPosition(t(2).toPixel() - Bullet.SIZE / 2, t(1).toPixel() - Bullet.SIZE + 1)

        walls.fillBrickTile(t(1), t(1))
        assertTrue(walls.hit(bullet))

        bullet.direction = Direction.LEFT
        bullet.setPosition(t(2).toPixel() - 1, t(2).toPixel() - Bullet.SIZE / 2)
        assertTrue(walls.hit(bullet))

        assertEquals(
            setOf(
                BrickTile(t(1), t(1), 0b0001)
            ),
            walls.config.bricks,
        )

        bullet.direction = Direction.UP
        bullet.setPosition(t(1).toPixel() - Bullet.SIZE / 2, t(2).toPixel() - 1)
        assertTrue(walls.hit(bullet))

        assertEquals(emptySet(), walls.config.bricks)

    }

    @Test
    fun `should draw brick wall hit right`() {
        walls.fillBrickTile(t(0), t(0))

        bullet.direction = (Direction.LEFT)
        bullet.setPosition(t(1).toPixel() - 1, t(1).toPixel() - Bullet.SIZE / 2)
        assertTrue(walls.hit(bullet))

        val image = BufferedImage(t(1).toPixel().toInt(), t(1).toPixel().toInt(), BufferedImage.TYPE_INT_ARGB)
        walls.draw(AwtScreenSurface(fonts, image))

        assertImageEquals("brick_wall_hit_right.png", image)
    }

    @Test
    fun `should draw brick wall hit left`() {
        walls.fillBrickTile(t(0), t(0))

        bullet.direction = Direction.RIGHT
        bullet.setPosition(t(0).toPixel() - Bullet.SIZE + 1, t(1).toPixel() - Bullet.SIZE / 2)
        assertTrue(walls.hit(bullet))

        val image = BufferedImage(t(1).toPixel().toInt(), t(1).toPixel().toInt(), BufferedImage.TYPE_INT_ARGB)
        walls.draw(AwtScreenSurface(fonts, image))

        assertImageEquals("brick_wall_hit_left.png", image)
    }

    @Test
    fun `should draw brick wall hit top and right`() {
        walls.fillBrickTile(t(0), t(0))

        bullet.direction = Direction.LEFT
        bullet.setPosition(t(1).toPixel() - 1, t(1).toPixel() - Bullet.SIZE / 2)
        assertTrue(walls.hit(bullet))

        bullet.direction = Direction.DOWN
        bullet.setPosition(t(0).toPixel() - Bullet.SIZE / 2, t(0).toPixel() - Bullet.SIZE + 1)
        assertTrue(walls.hit(bullet))

        val image = BufferedImage(t(1).toPixel().toInt(), t(1).toPixel().toInt(), BufferedImage.TYPE_INT_ARGB)
        walls.draw(AwtScreenSurface(fonts, image))

        assertImageEquals("brick_wall_hit_top_right.png", image)
    }

    @Test
    fun `should draw brick wall hit top and left`() {
        walls.fillBrickTile(t(0), t(0))

        bullet.direction = Direction.RIGHT
        bullet.setPosition(t(0).toPixel() - Bullet.SIZE + 1, t(1).toPixel() - Bullet.SIZE / 2)
        assertTrue(walls.hit(bullet))

        bullet.direction = Direction.DOWN
        bullet.setPosition(t(1).toPixel() - Bullet.SIZE / 2, t(0).toPixel() - Bullet.SIZE + 1)
        assertTrue(walls.hit(bullet))

        val image = BufferedImage(t(1).toPixel().toInt(), t(1).toPixel().toInt(), BufferedImage.TYPE_INT_ARGB)
        walls.draw(AwtScreenSurface(fonts, image))

        assertImageEquals("brick_wall_hit_top_left.png", image)
    }

    @Test
    fun `steel should be invincible for normal bullets`() {
        walls.fillSteelTile(t(0), t(0))

        assertTrue(walls.hit(bullet))

        assertEquals(
            setOf(
                TilePoint(t(0), t(0)),
            ),
            walls.config.steel
        )
    }

    @Test
    fun `steel should be destroyed by enhanced bullet`() {
        walls.fillSteelTile(t(0), t(0))

        bullet.type = Bullet.Type.ENHANCED
        assertTrue(walls.hit(bullet))

        assertEquals(emptySet(), walls.config.steel)
    }
}