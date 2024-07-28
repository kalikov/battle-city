package com.kalikov.game

import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.kotlin.argumentCaptor
import org.mockito.kotlin.isA
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import java.awt.image.BufferedImage
import kotlin.test.assertEquals

class BulletFactoryTest {
    private lateinit var fonts: TestFonts

    @BeforeEach
    fun beforeEach() {
        fonts = TestFonts()
    }

    @Test
    fun `should subscribe`() {
        val eventManager: EventManager = mock()

        val factory = BulletFactory(eventManager, mock())
        verify(eventManager).addSubscriber(factory, setOf(Tank.Shoot::class))
    }

    @Test
    fun `should unsubscribe`() {
        val eventManager: EventManager = mock()

        val factory = BulletFactory(eventManager, mock())
        factory.dispose()
        verify(eventManager).removeSubscriber(factory, setOf(Tank.Shoot::class))
    }

    @Test
    fun `should draw tank shooting right`() {
        shouldDrawShootingTank(
            Point(0, 0),
            Direction.RIGHT,
            Size(Globals.UNIT_SIZE + Globals.TILE_SIZE, Globals.UNIT_SIZE),
            "tank_shooting_right.png"
        )
    }

    @Test
    fun `should draw tank shooting left`() {
        shouldDrawShootingTank(
            Point(Globals.TILE_SIZE, 0),
            Direction.LEFT,
            Size(Globals.UNIT_SIZE + Globals.TILE_SIZE, Globals.UNIT_SIZE),
            "tank_shooting_left.png"
        )
    }

    @Test
    fun `should draw tank shooting up`() {
        shouldDrawShootingTank(
            Point(0, Globals.TILE_SIZE),
            Direction.UP,
            Size(Globals.UNIT_SIZE, Globals.UNIT_SIZE + Globals.TILE_SIZE),
            "tank_shooting_up.png"
        )
    }

    @Test
    fun `should draw tank shooting down`() {
        shouldDrawShootingTank(
            Point(0, 0),
            Direction.DOWN,
            Size(Globals.UNIT_SIZE, Globals.UNIT_SIZE + Globals.TILE_SIZE),
            "tank_shooting_down.png"
        )
    }

    private fun shouldDrawShootingTank(tankPosition: Point, direction: Direction, imageSize: Size, imageName: String) {
        val spriteContainer: SpriteContainer = mock()
        val imageManager = TestImageManager(fonts)
        val factory = BulletFactory(mock(), spriteContainer)

        val tank = mockPlayerTank(imageManager = imageManager, x = tankPosition.x, y = tankPosition.y)
        tank.direction = direction

        factory.notify(Tank.Shoot(tank))

        val captor = argumentCaptor<Bullet>()
        verify(spriteContainer).addSprite(captor.capture())

        val bullet = captor.firstValue

        val image = BufferedImage(imageSize.width, imageSize.height, BufferedImage.TYPE_INT_ARGB)
        tank.draw(AwtScreenSurface(fonts, image))
        bullet.draw(AwtScreenSurface(fonts, image))

        assertImageEquals(imageName, image)
    }

    @Test
    fun `should create bullet facing right direction`() {
        shouldCreateBulletWithCorrectDirection(
            Point(0, 0),
            Direction.RIGHT,
            Point(Globals.UNIT_SIZE, Globals.UNIT_SIZE / 2 - Bullet.SIZE / 2)
        )
    }

    @Test
    fun `should create bullet facing left direction`() {
        shouldCreateBulletWithCorrectDirection(
            Point(Globals.UNIT_SIZE, 0),
            Direction.LEFT,
            Point(Globals.UNIT_SIZE - Bullet.SIZE, Globals.UNIT_SIZE / 2 - Bullet.SIZE / 2)
        )
    }

    @Test
    fun `should create bullet facing up direction`() {
        shouldCreateBulletWithCorrectDirection(
            Point(0, Globals.UNIT_SIZE),
            Direction.UP,
            Point(Globals.UNIT_SIZE / 2 - Bullet.SIZE / 2, Globals.UNIT_SIZE - Bullet.SIZE)
        )
    }

    @Test
    fun `should create bullet facing down direction`() {
        shouldCreateBulletWithCorrectDirection(
            Point(0, 0),
            Direction.DOWN,
            Point(Globals.UNIT_SIZE / 2 - Bullet.SIZE / 2, Globals.UNIT_SIZE)
        )
    }

    private fun shouldCreateBulletWithCorrectDirection(
        tankPosition: Point,
        direction: Direction,
        bulletPosition: Point
    ) {
        val spriteContainer: SpriteContainer = mock()
        val factory = BulletFactory(mock(), spriteContainer)

        val tank = mockPlayerTank()
        tank.setPosition(tankPosition)
        tank.direction = direction

        factory.notify(Tank.Shoot(tank))

        val captor = argumentCaptor<Bullet>()
        verify(spriteContainer).addSprite(captor.capture())

        val bullet = captor.firstValue
        assertEquals(bulletPosition, Point(bullet.x, bullet.y))
        assertEquals(direction, bullet.direction)
        assertEquals(tank.bulletSpeed, bullet.speed)
    }

    @Test
    fun `should create regular bullet`() {
        shouldCreateBulletWithCorrectType(Bullet.Type.REGULAR)
    }

    @Test
    fun `should create enhanced bullet`() {
        shouldCreateBulletWithCorrectType(Bullet.Type.ENHANCED)
    }

    private fun shouldCreateBulletWithCorrectType(type: Bullet.Type) {
        val spriteContainer: SpriteContainer = mock()
        val factory = BulletFactory(mock(), spriteContainer)

        val tank = mockPlayerTank()
        tank.bulletType = type

        factory.notify(Tank.Shoot(tank))

        val captor = argumentCaptor<Bullet>()
        verify(spriteContainer).addSprite(captor.capture())

        val bullet = captor.firstValue
        assertEquals(type, bullet.type)
    }

    @Test
    fun `should create a bullet when tank shoots`() {
        val spriteContainer: SpriteContainer = mock()
        val factory = BulletFactory(mock(), spriteContainer)

        val tank = mockPlayerTank()
        factory.notify(Tank.Shoot(tank))
        verify(spriteContainer).addSprite(isA<Bullet>())
    }
}