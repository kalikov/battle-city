package com.kalikov.game

import org.junit.jupiter.api.Test
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.isA
import org.mockito.kotlin.mock
import org.mockito.kotlin.never
import org.mockito.kotlin.verify
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertIs
import kotlin.test.assertIsNot
import kotlin.test.assertTrue

class EnemyTankTest : TankTest<EnemyTank>() {
    private val flashInterval = 128

    override fun createTank(): EnemyTank {
        return EnemyTank.create(
            game,
            mock(),
            0,
            0,
            EnemyTank.EnemyType.BASIC
        )
    }

    @Test
    fun `should face down direction when appearing state ends`() {
        tank.state = TankStateAppearing(game, tank)
        tank.direction = Direction.UP
        tank.notify(TankStateAppearing.End(tank))
        assertEquals(Direction.DOWN, tank.direction)
    }

    @Test
    fun `enemy should be in normal state when appearing state ends`() {
        tank.state = TankStateAppearing(game, tank)
        tank.notify(TankStateAppearing.End(tank))
        assertIs<TankStateNormal>(tank.state)
        assertIsNot<TankStateInvincible>(tank.state)
    }

    @Test
    fun `enemy should down direction when appearing state ends`() {
        tank.state = TankStateAppearing(game, tank)
        tank.direction = Direction.UP
        tank.notify(TankStateAppearing.End(tank))
        assertEquals(Direction.DOWN, tank.direction)
    }

    @Test
    fun `should not fire enemy score event on explicit destroy`() {
        tank.destroy()
        tank.update()
        verify(eventManager, never()).fireEvent(isA<EnemyTank.Score>())
    }

    @Test
    fun `should fire enemy score event on hit destroy`() {
        val playerTank = mockPlayerTank()
        val bullet = mock<BulletHandle> {
            on { tank } doReturn playerTank
        }
        tank.hit(bullet)
        tank.update()
        verify(eventManager).fireEvent(EnemyTank.Score(tank, playerTank.player))
    }

    @Test
    fun `should flash using flashing interval`() {
        tank.color.colors = arrayOf(EnemyFactory.FLASHING_COLORS)
        tank.update()
        assertEquals(0, tank.color.getColor())

        clock.tick(flashInterval / 2)
        tank.update()
        assertEquals(0, tank.color.getColor())

        clock.tick(flashInterval / 4)
        tank.update()
        assertEquals(0, tank.color.getColor())

        clock.tick(flashInterval / 4)
        tank.update()
        assertEquals(1, tank.color.getColor())

        clock.tick(flashInterval / 2)
        tank.update()
        assertEquals(1, tank.color.getColor())

        clock.tick(flashInterval / 4)
        tank.update()
        assertEquals(1, tank.color.getColor())

        clock.tick(flashInterval / 4)
        tank.update()
        assertEquals(0, tank.color.getColor())
    }

    @Test
    fun `should not pause flashing`() {
        eventManager = ConcurrentEventManager()
        val pauseListener = PauseListener(eventManager)
        game = mockGame(eventManager = eventManager, clock = clock)
        tank = mockEnemyTank(game, pauseListener)
        val state = TankStateInvincible(game, tank, 10)
        tank.state = state
        tank.color.colors = arrayOf(EnemyFactory.FLASHING_COLORS)
        tank.update()

        assertEquals(0, tank.color.getColor())

        eventManager.fireEvent(PauseManager.Start)

        clock.tick(flashInterval)
        tank.update()
        assertEquals(1, tank.color.getColor())

        clock.tick(flashInterval)
        tank.update()
        assertEquals(0, tank.color.getColor())
    }


    @Test
    fun `should be destroyed on hit`() {
        tank.hitLimit = 1

        tank.hit(mock())
        assertTrue(tank.isDestroyed)
    }

    @Test
    fun `should be destroyed on multiple hits`() {
        tank.hitLimit = 4

        tank.hit(mock())
        assertFalse(tank.isDestroyed)
        tank.hit(mock())
        assertFalse(tank.isDestroyed)
        tank.hit(mock())
        assertFalse(tank.isDestroyed)
        tank.hit(mock())
        assertTrue(tank.isDestroyed)
    }

    @Test
    fun `should change color on hit`() {
        val color = TankColor(mock())
        color.colors = arrayOf(intArrayOf(0), intArrayOf(1))
        tank.color = color

        tank.hit(mock())
        assertEquals(1, color.getColor())
    }

    @Test
    fun `should fire hit event on armored tank hit`() {
        tank.hitLimit = 4
        tank.hit(mock())
        assertFalse(tank.isDestroyed)
        verify(eventManager).fireEvent(Tank.Hit(tank))
    }

    @Test
    fun `should update color`() {
        val color = TankColor(clock)
        color.colors = arrayOf(intArrayOf(0, 1))
        tank.color = color
        tank.update()
        assertEquals(0, color.index)

        clock.tick(TankColor.FLASHING_INTERVAL)
        tank.update()
        assertEquals(1, color.index)
    }
}