package com.kalikov.game

import com.kalikov.engine.AwtScreenSurface
import com.kalikov.engine.EventManager
import com.kalikov.engine.px
import com.kalikov.util.TestClock
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.argumentCaptor
import org.mockito.kotlin.clearInvocations
import org.mockito.kotlin.isA
import org.mockito.kotlin.mock
import org.mockito.kotlin.never
import org.mockito.kotlin.reset
import org.mockito.kotlin.times
import org.mockito.kotlin.verify
import java.awt.image.BufferedImage
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertIsNot
import kotlin.test.assertSame
import kotlin.test.assertTrue

abstract class TankTest<T : Tank> {
    protected lateinit var eventManager: EventManager
    protected lateinit var imageManager: ImageManager
    protected lateinit var clock: TestClock
    protected lateinit var game: BattleCityGame

    protected lateinit var tank: T

    @BeforeEach
    fun beforeEach() {
        eventManager = mock()
        imageManager = TestImageManager(mock())
        clock = TestClock()
        game = mockGame(eventManager = eventManager, imageManager = imageManager, clock = clock)
        tank = createTank()
    }

    protected abstract fun createTank(): T

    @Test
    fun `should fire event on shoot`() {
        tank.shoot()

        val shootCaptor = argumentCaptor<Tank.Shoot>()
        verify(eventManager).fireEvent(shootCaptor.capture())
        assertSame(tank, shootCaptor.firstValue.bullet.tank)
    }

    @Test
    fun `should shoot one bullet`() {
        tank.bulletsLimit = 1
        tank.shoot()
        reset(eventManager)

        clock.tick(Tank.LONG_COOLDOWN_INTERVAL)
        tank.update()
        tank.shoot()
        verify(eventManager, never()).fireEvent(any())

        tank.reload()

        clock.tick(Tank.LONG_COOLDOWN_INTERVAL)
        tank.update()
        tank.shoot()

        verify(eventManager).fireEvent(isA<Tank.Shoot>())
    }

    @Test
    fun `should shoot two bullets`() {
        tank.bulletsLimit = 2
        tank.shoot()
        reset(eventManager)

        clock.tick(Tank.LONG_COOLDOWN_INTERVAL)
        tank.update()
        tank.shoot()
        verify(eventManager).fireEvent(isA<Tank.Shoot>())

        reset(eventManager)

        clock.tick(Tank.LONG_COOLDOWN_INTERVAL)
        tank.update()
        tank.shoot()
        verify(eventManager, never()).fireEvent(any())

        tank.reload()

        clock.tick(Tank.LONG_COOLDOWN_INTERVAL)
        tank.update()
        tank.shoot()
        verify(eventManager).fireEvent(isA<Tank.Shoot>())
        verify(eventManager).fireEvent(isA<Tank.Shoot>())

        reset(eventManager)

        clock.tick(Tank.LONG_COOLDOWN_INTERVAL)
        tank.update()
        tank.shoot()
        verify(eventManager, never()).fireEvent(any())
    }

    @Test
    fun `should update state`() {
        val state: TankState = mock()

        tank.state = state
        tank.update()
        verify(state).update()
    }

    @Test
    fun `should smooth turn right-up rounding using direction`() {
        tank.direction = Direction.RIGHT
        tank.setPosition(PixelPoint(12.px, 7.px))
        tank.direction = Direction.UP
        assertEquals(16.px, tank.x)
        assertEquals(7.px, tank.y)
    }

    @Test
    fun `should smooth turn left-up rounding using direction`() {
        tank.direction = Direction.LEFT
        tank.setPosition(PixelPoint(12.px, 7.px))
        tank.direction = Direction.UP
        assertEquals(8.px, tank.x)
        assertEquals(7.px, tank.y)
    }

    @Test
    fun `should smooth turn right-up`() {
        tank.direction = Direction.RIGHT
        tank.setPosition(PixelPoint(6.px, 7.px))
        tank.direction = Direction.UP
        assertEquals(8.px, tank.x)
        assertEquals(7.px, tank.y)
    }

    @Test
    fun `should smooth turn right-down`() {
        tank.direction = Direction.RIGHT
        tank.setPosition(PixelPoint(5.px, 0.px))
        tank.direction = Direction.DOWN
        assertEquals(8.px, tank.x)
        assertEquals(0.px, tank.y)
    }

    @Test
    fun `should smooth turn left-down`() {
        tank.direction = Direction.LEFT
        tank.setPosition(PixelPoint(3.px, 1.px))
        tank.direction = Direction.DOWN
        assertEquals(0.px, tank.x)
        assertEquals(1.px, tank.y)
    }

    @Test
    fun `should smooth turn left-up`() {
        tank.direction = Direction.LEFT
        tank.setPosition(PixelPoint(6.px, 2.px))
        tank.direction = Direction.UP
        assertEquals(8.px, tank.x)
        assertEquals(2.px, tank.y)
    }

    @Test
    fun `should smooth turn down-right`() {
        tank.direction = Direction.DOWN
        tank.setPosition(PixelPoint(0.px, 3.px))
        tank.direction = Direction.RIGHT
        assertEquals(0.px, tank.x)
        assertEquals(0.px, tank.y)
    }

    @Test
    fun `should smooth turn down-left`() {
        tank.direction = Direction.DOWN
        tank.setPosition(PixelPoint(0.px, 3.px))
        tank.direction = Direction.LEFT
        assertEquals(0.px, tank.x)
        assertEquals(0.px, tank.y)
    }

    @Test
    fun `should smooth turn up-left`() {
        tank.direction = Direction.UP
        tank.setPosition(PixelPoint(3.px, 3.px))
        tank.direction = Direction.LEFT
        assertEquals(3.px, tank.x)
        assertEquals(0.px, tank.y)
    }

    @Test
    fun `should smooth turn up-right`() {
        tank.direction = Direction.UP
        tank.setPosition(PixelPoint(3.px, 3.px))
        tank.direction = Direction.RIGHT
        assertEquals(3.px, tank.x)
        assertEquals(0.px, tank.y)
    }

    @Test
    fun `should smooth turn left-right`() {
        tank.direction = Direction.LEFT
        tank.setPosition(PixelPoint(3.px, 3.px))
        tank.direction = Direction.RIGHT
        assertEquals(3.px, tank.x)
        assertEquals(3.px, tank.y)
    }

    @Test
    fun `should be in normal state when invincible state ends`() {
        tank.state = TankStateInvincible(game, mock(), tank)
//        tank.notify(TankStateInvincible.End(tank))
        assertIs<TankStateNormal>(tank.state)
        assertIsNot<TankStateInvincible>(tank.state)
    }

    @Test
    fun `should fire event on destroy`() {
        tank.destroy()
        tank.update()
        verify(eventManager).fireEvent(Tank.Destroyed(tank))
    }

    @Test
    fun `should fire hit event on tank destroy`() {
        tank.hit(mock())
        assertTrue(tank.isDestroyed)
        verify(eventManager).fireEvent(Tank.Hit(tank))
    }

    @Test
    fun `should draw tank shooting right`() {
        shouldDrawShootingTank(
            PixelPoint(),
            Direction.RIGHT,
            PixelSize(3.tiles.toPixel(), 2.tiles.toPixel()),
            "tank_shooting_right.png"
        )
    }

    @Test
    fun `should draw tank shooting left`() {
        shouldDrawShootingTank(
            PixelPoint(1.tiles.toPixel(), 0.px),
            Direction.LEFT,
            PixelSize(3.tiles.toPixel(), 2.tiles.toPixel()),
            "tank_shooting_left.png"
        )
    }

    @Test
    fun `should draw tank shooting up`() {
        shouldDrawShootingTank(
            PixelPoint(0.px, 1.tiles.toPixel()),
            Direction.UP,
            PixelSize(2.tiles.toPixel(), 3.tiles.toPixel()),
            "tank_shooting_up.png"
        )
    }

    @Test
    fun `should draw tank shooting down`() {
        shouldDrawShootingTank(
            PixelPoint(),
            Direction.DOWN,
            PixelSize(2.tiles.toPixel(), 3.tiles.toPixel()),
            "tank_shooting_down.png"
        )
    }

    private fun shouldDrawShootingTank(
        tankPosition: PixelPoint,
        direction: Direction,
        imageSize: PixelSize,
        imageName: String
    ) {
        val fonts = TestFonts()
        val imageManager = TestImageManager(fonts)
        val game = mockGame(imageManager = imageManager)

        val tank = stubPlayerTank(game, x = tankPosition.x, y = tankPosition.y)
        tank.direction = direction

        clearInvocations(game.eventManager)
        tank.shoot()

        val captor = argumentCaptor<Tank.Shoot>()
        verify(game.eventManager).fireEvent(captor.capture())

        val bullet = captor.firstValue.bullet

        val image = BufferedImage(imageSize.width.toInt(), imageSize.height.toInt(), BufferedImage.TYPE_INT_ARGB)
        tank.draw(AwtScreenSurface(fonts, image))
        bullet.draw(AwtScreenSurface(fonts, image))

        assertImageEquals(imageName, image)
    }

    @Test
    fun `should create bullet facing right direction`() {
        shouldCreateBulletWithCorrectDirection(
            PixelPoint(0.px, 0.px),
            Direction.RIGHT,
            PixelPoint(2.tiles.toPixel(), 1.tiles.toPixel() - Bullet.SIZE / 2)
        )
    }

    @Test
    fun `should create bullet facing left direction`() {
        shouldCreateBulletWithCorrectDirection(
            PixelPoint(2.tiles.toPixel(), 0.px),
            Direction.LEFT,
            PixelPoint(
                2.tiles.toPixel() - Bullet.SIZE,
                1.tiles.toPixel() - Bullet.SIZE / 2
            )
        )
    }

    @Test
    fun `should create bullet facing up direction`() {
        shouldCreateBulletWithCorrectDirection(
            PixelPoint(0.px, 2.tiles.toPixel()),
            Direction.UP,
            PixelPoint(
                1.tiles.toPixel() - Bullet.SIZE / 2,
                2.tiles.toPixel() - Bullet.SIZE
            )
        )
    }

    @Test
    fun `should create bullet facing down direction`() {
        shouldCreateBulletWithCorrectDirection(
            PixelPoint(0.px, 0.px),
            Direction.DOWN,
            PixelPoint(1.tiles.toPixel() - Bullet.SIZE / 2, 2.tiles.toPixel())
        )
    }

    private fun shouldCreateBulletWithCorrectDirection(
        tankPosition: PixelPoint,
        direction: Direction,
        bulletPosition: PixelPoint
    ) {
        val tank = stubPlayerTank(game)
        tank.setPosition(tankPosition)
        tank.direction = direction

        clearInvocations(game.eventManager)
        tank.shoot()

        val captor = argumentCaptor<Tank.Shoot>()
        verify(game.eventManager).fireEvent(captor.capture())

        val event = captor.firstValue
        val bullet = event.bullet
        assertEquals(bulletPosition, PixelPoint(bullet.x, bullet.y))
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

    @Test
    fun `should not shoot during long cooldown`() {
        val tank = stubPlayerTank(game)
        tank.shoot()

        verify(game.eventManager).fireEvent(isA<Tank.Shoot>())
        clearInvocations(game.eventManager)

        tank.reload()

        repeat(Tank.LONG_COOLDOWN_INTERVAL - 1) {
            clock.tick(1)
            tank.update()
            tank.shoot()
            verify(game.eventManager, never()).fireEvent(isA<Tank.Shoot>())
        }

        clock.tick(1)
        tank.update()
        tank.shoot()
        verify(game.eventManager).fireEvent(isA<Tank.Shoot>())
    }

    @Test
    fun `should not shoot during short cooldown`() {
        val tank = stubPlayerTank(game)
        tank.bulletsLimit = 2
        tank.shoot()

        verify(game.eventManager).fireEvent(isA<Tank.Shoot>())
        clearInvocations(game.eventManager)

        tank.reload()

        repeat (Tank.SHORT_COOLDOWN_INTERVAL - 1) {
            clock.tick(1)
            tank.update()
            tank.shoot()
            verify(game.eventManager, never()).fireEvent(isA<Tank.Shoot>())
        }

        clock.tick(1)
        tank.update()
        tank.shoot()
        verify(game.eventManager).fireEvent(isA<Tank.Shoot>())
    }

    @Test
    fun `should not shoot next first bullet during long cooldown`() {
        val tank = stubPlayerTank(game)
        tank.bulletsLimit = 2
        tank.shoot()

        clock.tick(Tank.SHORT_COOLDOWN_INTERVAL)
        tank.update()
        tank.shoot()

        verify(game.eventManager, times(2)).fireEvent(isA<Tank.Shoot>())
        clearInvocations(game.eventManager)

        tank.reload()
        tank.reload()

        repeat(Tank.LONG_COOLDOWN_INTERVAL - Tank.SHORT_COOLDOWN_INTERVAL - 1) {
            clock.tick(1)
            tank.update()
            tank.shoot()
            verify(game.eventManager, never()).fireEvent(isA<Tank.Shoot>())
        }

        clock.tick(1)
        tank.update()
        tank.shoot()
        verify(game.eventManager).fireEvent(isA<Tank.Shoot>())
    }

    private fun shouldCreateBulletWithCorrectType(type: Bullet.Type) {
        val tank = stubPlayerTank(game)
        tank.bulletType = type

        tank.shoot()

        val captor = argumentCaptor<Tank.Shoot>()
        verify(game.eventManager).fireEvent(captor.capture())

        val event = captor.firstValue
        val bullet = event.bullet
        assertEquals(type, bullet.type)
    }
}