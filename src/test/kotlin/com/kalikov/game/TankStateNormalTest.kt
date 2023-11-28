package com.kalikov.game

import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.kotlin.mock
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class TankStateNormalTest {
    private lateinit var eventManager: EventManager
    private lateinit var pauseManager: PauseManager
    private lateinit var imageManager: ImageManager
    private lateinit var clock: TestClock
    private lateinit var tank: Tank
    private lateinit var state: TankStateNormal

    private val flashInterval = 128

    @BeforeEach
    fun beforeEach() {
        eventManager = mock()
        pauseManager = mock()
        imageManager = mock()
        clock = TestClock()
        tank = mockTank(eventManager, pauseManager, imageManager, clock)
        state = TankStateNormal(imageManager, tank)
        tank.state = state
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
        pauseManager = pauseListener
        tank = mockTank(eventManager, pauseManager, clock = clock)
        state = TankStateInvincible(eventManager, mock(), tank, 10)
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
    fun `should allow movement`() {
        assertTrue(state.canMove)
    }

    @Test
    fun `should allow shooting`() {
        assertTrue(state.canShoot)
    }
}