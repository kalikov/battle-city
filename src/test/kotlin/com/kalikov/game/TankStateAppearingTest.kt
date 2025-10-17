package com.kalikov.game

import com.kalikov.engine.EventManager
import com.kalikov.util.TestClock
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.never
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import kotlin.test.assertFalse

class TankStateAppearingTest {
    private lateinit var eventManager: EventManager
    private lateinit var pauseManager: PauseManager
    private lateinit var clock: TestClock
    private lateinit var tank: Tank
    private lateinit var state: TankStateAppearing

    @BeforeEach
    fun beforeEach() {
        eventManager = mock()
        pauseManager = mock()
        clock = TestClock()
        val game = mockGame(eventManager = eventManager, clock = clock)
        tank = stubPlayerTank(game, pauseManager)
        state = TankStateAppearing(game, pauseManager, tank)
        tank.state = state
    }

    @Test
    fun `should fire event on animation end`() {
        tank.update()

        clock.tick(TankStateAppearing.DEFAULT_ANIMATION_DURATION - 1)
        tank.update()
        verify(eventManager, never()).fireEvent(TankStateAppearing.End(tank))

        clock.tick(1)
        tank.update()
        verify(eventManager).fireEvent(TankStateAppearing.End(tank))
    }

    @Test
    fun `should pause tank appearing animation`() {
        tank.update()

        clock.tick(TankStateAppearing.DEFAULT_ANIMATION_DURATION - 1)
        tank.update()
        verify(eventManager, never()).fireEvent(TankStateAppearing.End(tank))

        whenever(pauseManager.isPaused).thenReturn(true)
        tank.update()

        clock.tick(100)
        tank.update()
        verify(eventManager, never()).fireEvent(TankStateAppearing.End(tank))

        whenever(pauseManager.isPaused).thenReturn(false)
        tank.update()

        tank.update()
        verify(eventManager, never()).fireEvent(TankStateAppearing.End(tank))

        clock.tick(1)
        tank.update()
        verify(eventManager).fireEvent(TankStateAppearing.End(tank))
    }

    @Test
    fun `should not move in appearing state`() {
        assertFalse(state.canMove)
    }

    @Test
    fun `should not shoot in appearing state`() {
        assertFalse(state.canShoot)
    }
}