package com.kalikov.game

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
    protected lateinit var game: Game

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

        tank.notify(Tank.Reload(tank))

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

        tank.notify(Tank.Reload(tank))

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
        tank.setPosition(PixelPoint(px(12), px(7)))
        tank.direction = Direction.UP
        assertEquals(px(16), tank.x)
        assertEquals(px(7), tank.y)
    }

    @Test
    fun `should smooth turn left-up rounding using direction`() {
        tank.direction = Direction.LEFT
        tank.setPosition(PixelPoint(px(12), px(7)))
        tank.direction = Direction.UP
        assertEquals(px(8), tank.x)
        assertEquals(px(7), tank.y)
    }

    @Test
    fun `should smooth turn right-up`() {
        tank.direction = Direction.RIGHT
        tank.setPosition(PixelPoint(px(6), px(7)))
        tank.direction = Direction.UP
        assertEquals(px(8), tank.x)
        assertEquals(px(7), tank.y)
    }

    @Test
    fun `should smooth turn right-down`() {
        tank.direction = Direction.RIGHT
        tank.setPosition(PixelPoint(px(5), px(0)))
        tank.direction = Direction.DOWN
        assertEquals(px(8), tank.x)
        assertEquals(px(0), tank.y)
    }

    @Test
    fun `should smooth turn left-down`() {
        tank.direction = Direction.LEFT
        tank.setPosition(PixelPoint(px(3), px(1)))
        tank.direction = Direction.DOWN
        assertEquals(px(0), tank.x)
        assertEquals(px(1), tank.y)
    }

    @Test
    fun `should smooth turn left-up`() {
        tank.direction = Direction.LEFT
        tank.setPosition(PixelPoint(px(6), px(2)))
        tank.direction = Direction.UP
        assertEquals(px(8), tank.x)
        assertEquals(px(2), tank.y)
    }

    @Test
    fun `should smooth turn down-right`() {
        tank.direction = Direction.DOWN
        tank.setPosition(PixelPoint(px(0), px(3)))
        tank.direction = Direction.RIGHT
        assertEquals(px(0), tank.x)
        assertEquals(px(0), tank.y)
    }

    @Test
    fun `should smooth turn down-left`() {
        tank.direction = Direction.DOWN
        tank.setPosition(PixelPoint(px(0), px(3)))
        tank.direction = Direction.LEFT
        assertEquals(px(0), tank.x)
        assertEquals(px(0), tank.y)
    }

    @Test
    fun `should smooth turn up-left`() {
        tank.direction = Direction.UP
        tank.setPosition(PixelPoint(px(3), px(3)))
        tank.direction = Direction.LEFT
        assertEquals(px(3), tank.x)
        assertEquals(px(0), tank.y)
    }

    @Test
    fun `should smooth turn up-right`() {
        tank.direction = Direction.UP
        tank.setPosition(PixelPoint(px(3), px(3)))
        tank.direction = Direction.RIGHT
        assertEquals(px(3), tank.x)
        assertEquals(px(0), tank.y)
    }

    @Test
    fun `should smooth turn left-right`() {
        tank.direction = Direction.LEFT
        tank.setPosition(PixelPoint(px(3), px(3)))
        tank.direction = Direction.RIGHT
        assertEquals(px(3), tank.x)
        assertEquals(px(3), tank.y)
    }

    @Test
    fun `should be in normal state when invincible state ends`() {
        tank.state = TankStateInvincible(game, tank)
        tank.notify(TankStateInvincible.End(tank))
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
    fun `should subscribe`() {
        verify(eventManager).addSubscriber(
            tank,
            setOf(
                Tank.Reload::class,
                TankStateAppearing.End::class,
                TankStateInvincible.End::class,
                TankStateFrozen.End::class,
            )
        )
    }


    @Test
    fun `should draw tank shooting right`() {
        shouldDrawShootingTank(
            PixelPoint(),
            Direction.RIGHT,
            PixelSize(t(3).toPixel(), t(2).toPixel()),
            "tank_shooting_right.png"
        )
    }

    @Test
    fun `should draw tank shooting left`() {
        shouldDrawShootingTank(
            PixelPoint(t(1).toPixel(), px(0)),
            Direction.LEFT,
            PixelSize(t(3).toPixel(), t(2).toPixel()),
            "tank_shooting_left.png"
        )
    }

    @Test
    fun `should draw tank shooting up`() {
        shouldDrawShootingTank(
            PixelPoint(px(0), t(1).toPixel()),
            Direction.UP,
            PixelSize(t(2).toPixel(), t(3).toPixel()),
            "tank_shooting_up.png"
        )
    }

    @Test
    fun `should draw tank shooting down`() {
        shouldDrawShootingTank(
            PixelPoint(),
            Direction.DOWN,
            PixelSize(t(2).toPixel(), t(3).toPixel()),
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
            PixelPoint(px(0), px(0)),
            Direction.RIGHT,
            PixelPoint(t(2).toPixel(), t(1).toPixel() - Bullet.SIZE / 2)
        )
    }

    @Test
    fun `should create bullet facing left direction`() {
        shouldCreateBulletWithCorrectDirection(
            PixelPoint(t(2).toPixel(), px(0)),
            Direction.LEFT,
            PixelPoint(
                t(2).toPixel() - Bullet.SIZE,
                t(1).toPixel() - Bullet.SIZE / 2
            )
        )
    }

    @Test
    fun `should create bullet facing up direction`() {
        shouldCreateBulletWithCorrectDirection(
            PixelPoint(px(0), t(2).toPixel()),
            Direction.UP,
            PixelPoint(
                t(1).toPixel() - Bullet.SIZE / 2,
                t(2).toPixel() - Bullet.SIZE
            )
        )
    }

    @Test
    fun `should create bullet facing down direction`() {
        shouldCreateBulletWithCorrectDirection(
            PixelPoint(px(0), px(0)),
            Direction.DOWN,
            PixelPoint(t(1).toPixel() - Bullet.SIZE / 2, t(2).toPixel())
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

        tank.notify(Tank.Reload(tank))

        for (i in 1 until Tank.LONG_COOLDOWN_INTERVAL) {
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

        tank.notify(Tank.Reload(tank))

        for (i in 1 until Tank.SHORT_COOLDOWN_INTERVAL) {
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

        tank.notify(Tank.Reload(tank))
        tank.notify(Tank.Reload(tank))

        for (i in Tank.SHORT_COOLDOWN_INTERVAL + 1 until Tank.LONG_COOLDOWN_INTERVAL) {
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