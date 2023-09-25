package com.kalikov.game

import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.kotlin.mock
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class TankStateNormalTest {
    private lateinit var eventManager: EventManager
    private lateinit var pauseManager: PauseManager
    private lateinit var imageManager: ImageManager
    private lateinit var clock: TestClock
    private lateinit var tank: Tank
    private lateinit var state: TankStateNormal

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
        tank.isFlashing = true
        tank.update()
        assertTrue(state.isBright)

        clock.tick(100)
        tank.update()
        assertTrue(state.isBright)

        clock.tick(50)
        tank.update()
        assertTrue(state.isBright)

        clock.tick(50)
        tank.update()
        assertFalse(state.isBright)

        clock.tick(100)
        tank.update()
        assertFalse(state.isBright)

        clock.tick(50)
        tank.update()
        assertFalse(state.isBright)

        clock.tick(50)
        tank.update()
        assertTrue(state.isBright)
    }

    @Test
    fun `should not pause flashing`() {
        eventManager = ConcurrentEventManager()
        val pauseListener = PauseListener(eventManager)
        pauseManager = pauseListener
        tank = mockTank(eventManager, pauseManager, clock = clock)
        state = TankStateInvincible(eventManager, mock(), tank, 10)
        tank.state = state
        tank.update()

        assertTrue(state.isBright)

        eventManager.fireEvent(PauseManager.Start)

        clock.tick(200)
        tank.update()
        assertFalse(state.isBright)

        clock.tick(200)
        tank.update()
        assertTrue(state.isBright)
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