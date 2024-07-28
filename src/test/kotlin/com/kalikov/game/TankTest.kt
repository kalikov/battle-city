package com.kalikov.game

import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.mock
import org.mockito.kotlin.never
import org.mockito.kotlin.reset
import org.mockito.kotlin.verify
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertIsNot
import kotlin.test.assertTrue

abstract class TankTest<T : Tank> {
    protected lateinit var eventManager: EventManager
    protected lateinit var imageManager: ImageManager
    protected lateinit var clock: TestClock
    protected lateinit var tank: T

    @BeforeEach
    fun beforeEach() {
        eventManager = mock()
        imageManager = TestImageManager(mock())
        clock = TestClock()
        tank = createTank()
    }

    protected abstract fun createTank(): T

    @Test
    fun `should fire event on shoot`() {
        tank.shoot()
        verify(eventManager).fireEvent(Tank.Shoot(tank))
    }

    @Test
    fun `should shoot one bullet`() {
        tank.bulletsLimit = 1
        tank.shoot()
        reset(eventManager)

        clock.tick(Tank.COOLDOWN_INTERVAL)
        tank.update()
        tank.shoot()
        verify(eventManager, never()).fireEvent(any())

        val bullet = tank.createBullet()
        tank.notify(Bullet.Destroyed(bullet))

        clock.tick(Tank.COOLDOWN_INTERVAL)
        tank.update()
        tank.shoot()
        verify(eventManager).fireEvent(Tank.Shoot(tank))
    }

    @Test
    fun `should shoot two bullets`() {
        tank.bulletsLimit = 2
        tank.shoot()
        reset(eventManager)

        clock.tick(Tank.COOLDOWN_INTERVAL)
        tank.update()
        tank.shoot()
        verify(eventManager).fireEvent(Tank.Shoot(tank))

        reset(eventManager)

        clock.tick(Tank.COOLDOWN_INTERVAL)
        tank.update()
        tank.shoot()
        verify(eventManager, never()).fireEvent(any())

        val bullet = tank.createBullet()
        tank.notify(Bullet.Destroyed(bullet))

        clock.tick(Tank.COOLDOWN_INTERVAL)
        tank.update()
        tank.shoot()
        verify(eventManager).fireEvent(Tank.Shoot(tank))

        reset(eventManager)

        clock.tick(Tank.COOLDOWN_INTERVAL)
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
        tank.setPosition(Point(12, 7))
        tank.direction = Direction.UP
        assertEquals(16, tank.x)
        assertEquals(7, tank.y)
    }

    @Test
    fun `should smooth turn left-up rounding using direction`() {
        tank.direction = Direction.LEFT
        tank.setPosition(Point(12, 7))
        tank.direction = Direction.UP
        assertEquals(8, tank.x)
        assertEquals(7, tank.y)
    }

    @Test
    fun `should smooth turn right-up`() {
        tank.direction = Direction.RIGHT
        tank.setPosition(Point(6, 7))
        tank.direction = Direction.UP
        assertEquals(8, tank.x)
        assertEquals(7, tank.y)
    }

    @Test
    fun `should smooth turn right-down`() {
        tank.direction = Direction.RIGHT
        tank.setPosition(Point(5, 0))
        tank.direction = Direction.DOWN
        assertEquals(8, tank.x)
        assertEquals(0, tank.y)
    }

    @Test
    fun `should smooth turn left-down`() {
        tank.direction = Direction.LEFT
        tank.setPosition(Point(3, 1))
        tank.direction = Direction.DOWN
        assertEquals(0, tank.x)
        assertEquals(1, tank.y)
    }

    @Test
    fun `should smooth turn left-up`() {
        tank.direction = Direction.LEFT
        tank.setPosition(Point(6, 2))
        tank.direction = Direction.UP
        assertEquals(8, tank.x)
        assertEquals(2, tank.y)
    }

    @Test
    fun `should smooth turn down-right`() {
        tank.direction = Direction.DOWN
        tank.setPosition(Point(0, 3))
        tank.direction = Direction.RIGHT
        assertEquals(0, tank.x)
        assertEquals(0, tank.y)
    }

    @Test
    fun `should smooth turn down-left`() {
        tank.direction = Direction.DOWN
        tank.setPosition(Point(0, 3))
        tank.direction = Direction.LEFT
        assertEquals(0, tank.x)
        assertEquals(0, tank.y)
    }

    @Test
    fun `should smooth turn up-left`() {
        tank.direction = Direction.UP
        tank.setPosition(Point(3, 3))
        tank.direction = Direction.LEFT
        assertEquals(3, tank.x)
        assertEquals(0, tank.y)
    }

    @Test
    fun `should smooth turn up-right`() {
        tank.direction = Direction.UP
        tank.setPosition(Point(3, 3))
        tank.direction = Direction.RIGHT
        assertEquals(3, tank.x)
        assertEquals(0, tank.y)
    }

    @Test
    fun `should smooth turn left-right`() {
        tank.direction = Direction.LEFT
        tank.setPosition(Point(3, 3))
        tank.direction = Direction.RIGHT
        assertEquals(3, tank.x)
        assertEquals(3, tank.y)
    }

    @Test
    fun `should be in normal state when invincible state ends`() {
        tank.state = TankStateInvincible(mock(), mock(), tank)
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
                Bullet.Destroyed::class,
                TankStateAppearing.End::class,
                TankStateInvincible.End::class,
                TankStateFrozen.End::class,
            )
        )
    }
}