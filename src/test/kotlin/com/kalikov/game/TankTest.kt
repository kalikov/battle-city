package com.kalikov.game

import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.mock
import org.mockito.kotlin.never
import org.mockito.kotlin.reset
import org.mockito.kotlin.verify
import java.awt.image.BufferedImage
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertIs
import kotlin.test.assertIsNot
import kotlin.test.assertTrue

class TankTest {
    private lateinit var eventManager: EventManager
    private lateinit var imageManager: ImageManager
    private lateinit var clock: TestClock
    private lateinit var tank: Tank

    @BeforeEach
    fun beforeEach() {
        eventManager = mock()
        imageManager = TestImageManager(mock())
        clock = TestClock()
        tank = Tank(eventManager, mock(), imageManager, clock, 0, 0)
    }

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
    fun `should be destroyed on hit`() {
        tank.hitLimit = 1

        tank.hit()
        assertTrue(tank.isDestroyed)
    }

    @Test
    fun `should be destroyed on multiple hits`() {
        tank.hitLimit = 4

        tank.hit()
        assertFalse(tank.isDestroyed)
        tank.hit()
        assertFalse(tank.isDestroyed)
        tank.hit()
        assertFalse(tank.isDestroyed)
        tank.hit()
        assertTrue(tank.isDestroyed)
    }

    @Test
    fun `should change color on hit`() {
        val color = TankColor(mock())
        color.colors = arrayOf(0 to 0, 1 to 1)
        tank.color = color

        tank.hit()
        assertEquals(1, color.getColor())
    }

    @Test
    fun `should be in invincible state when appearing state ends`() {
        tank.state = TankStateAppearing(mock(), mock(), tank)
        tank.notify(TankStateAppearing.End(tank))
        assertIs<TankStateInvincible>(tank.state)
    }

    @Test
    fun `should face up direction when appearing state ends`() {
        tank.state = TankStateAppearing(mock(), mock(), tank)
        tank.direction = Direction.DOWN
        tank.notify(TankStateAppearing.End(tank))
        assertEquals(Direction.UP, tank.direction)
    }

    @Test
    fun `enemy should be in normal state when appearing state ends`() {
        tank.state = TankStateAppearing(mock(), mock(), tank)
        tank.enemyType = Tank.EnemyType.BASIC
        tank.notify(TankStateAppearing.End(tank))
        assertIs<TankStateNormal>(tank.state)
        assertIsNot<TankStateInvincible>(tank.state)
    }

    @Test
    fun `enemy should down direction when appearing state ends`() {
        tank.state = TankStateAppearing(mock(), mock(), tank)
        tank.enemyType = Tank.EnemyType.BASIC
        tank.direction = Direction.UP
        tank.notify(TankStateAppearing.End(tank))
        assertEquals(Direction.DOWN, tank.direction)
    }

    @Test
    fun `should fire event on destroy`() {
        tank.destroy()
        tank.update()
        verify(eventManager).fireEvent(Tank.Destroyed(tank))
    }

    @Test
    fun `should fire player destroy event`() {
        tank.destroy()
        tank.update()
        verify(eventManager).fireEvent(Tank.PlayerDestroyed(tank))
    }

    @Test
    fun `should fire enemy destroy event`() {
        tank.enemyType = Tank.EnemyType.BASIC
        tank.destroy()
        tank.update()
        verify(eventManager).fireEvent(Tank.EnemyDestroyed(tank))
    }

    @Test
    fun `should fire flashing event on tank destroy`() {
        tank.isFlashing = true
        tank.hit()
        assertTrue(tank.isDestroyed)
        verify(eventManager).fireEvent(Tank.FlashingTankHit(tank))
    }

    @Test
    fun `should fire flashing event on armored tank hit`() {
        tank.hitLimit = 4
        tank.isFlashing = true
        tank.hit()
        assertFalse(tank.isDestroyed)
        verify(eventManager).fireEvent(Tank.FlashingTankHit(tank))
    }

    @Test
    fun `should upgrade to first level`() {
        assertEquals(0, tank.upgradeLevel)
        assertEquals(Bullet.Speed.NORMAL, tank.bulletSpeed)

        tank.upgrade()

        assertEquals(1, tank.upgradeLevel)
        assertEquals(Bullet.Speed.FAST, tank.bulletSpeed)
    }

    @Test
    fun `should upgrade to second level`() {
        tank.upgrade()

        assertEquals(1, tank.upgradeLevel)
        assertEquals(1, tank.bulletsLimit)

        tank.upgrade()

        assertEquals(2, tank.upgradeLevel)
        assertEquals(2, tank.bulletsLimit)
    }

    @Test
    fun `should upgrade to third level`() {
        tank.upgrade()
        tank.upgrade()

        assertEquals(2, tank.upgradeLevel)
        assertEquals(Bullet.Type.REGULAR, tank.bulletType)

        tank.upgrade()

        assertEquals(3, tank.upgradeLevel)
        assertEquals(Bullet.Type.ENHANCED, tank.bulletType)
    }

    @Test
    fun `should remain in third level and not upgrade to fourth level`() {
        tank.upgrade()
        tank.upgrade()
        tank.upgrade()

        assertEquals(3, tank.upgradeLevel)

        tank.upgrade()

        assertEquals(3, tank.upgradeLevel)
    }

    @Test
    fun `should update color`() {
        val color = TankColor(clock)
        tank.color = color
        tank.update()
        assertFalse(color.alternative)

        clock.tick(TankColor.FLASHING_INTERVAL)
        tank.updateColor()
        assertTrue(color.alternative)
    }

    @Test
    fun `should subscribe`() {
        verify(eventManager).addSubscriber(
            tank,
            setOf(
                Bullet.Destroyed::class,
                TankStateAppearing.End::class,
                TankStateInvincible.End::class
            )
        )
    }

    @Test
    @DisplayName("should draw tank in normal state with right direction")
    fun shouldDrawNormalRight() {
        shouldDrawTank(Direction.RIGHT, "tank_player_right_c0_t1")
    }

    @Test
    @DisplayName("should draw tank in normal state with up direction")
    fun shouldDrawNormalUp() {
        shouldDrawTank(Direction.UP, "tank_player_up_c0_t1")
    }

    @Test
    @DisplayName("should draw tank in normal state with down direction")
    fun shouldDrawNormalDown() {
        shouldDrawTank(Direction.DOWN, "tank_player_down_c0_t1")
    }

    @Test
    @DisplayName("should draw tank in normal state with left direction")
    fun shouldDrawNormalLeft() {
        shouldDrawTank(Direction.LEFT, "tank_player_left_c0_t1")
    }

    @Test
    @DisplayName("should draw tank in invincible state with right direction")
    fun shouldDrawInvincibleRight() {
        tank.state = TankStateInvincible(eventManager, imageManager, tank)
        shouldDrawTank(Direction.RIGHT, "tank_player_right_c0_t1_i")
    }

    @Test
    @DisplayName("should draw tank in invincible state with up direction")
    fun shouldDrawInvincibleUp() {
        tank.state = TankStateInvincible(eventManager, imageManager, tank)
        shouldDrawTank(Direction.UP, "tank_player_up_c0_t1_i")
    }

    @Test
    @DisplayName("should draw tank in invincible state with down direction")
    fun shouldDrawInvincibleDown() {
        tank.state = TankStateInvincible(eventManager, imageManager, tank)
        shouldDrawTank(Direction.DOWN, "tank_player_down_c0_t1_i")
    }

    @Test
    @DisplayName("should draw tank in invincible state with left direction")
    fun shouldDrawInvincibleLeft() {
        tank.state = TankStateInvincible(eventManager, imageManager, tank)
        shouldDrawTank(Direction.LEFT, "tank_player_left_c0_t1_i")
    }

    private fun shouldDrawTank(direction: Direction, imageName: String) {
        val image = BufferedImage(Globals.UNIT_SIZE, Globals.UNIT_SIZE, BufferedImage.TYPE_INT_ARGB)
        tank.direction = direction
        tank.draw(AwtScreenSurface(mock(), image))

        assertImageEquals("$imageName.png", image)
    }
}