package com.kalikov.game

import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.never
import org.mockito.kotlin.verify
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
        tank = mockTank(eventManager, pauseManager, clock = clock)
        state = TankStateAppearing(eventManager, mock(), tank)
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
        eventManager = ConcurrentEventManager()
        val pauseListener = PauseListener(eventManager)
        pauseManager = pauseListener
        tank = mockTank(eventManager, pauseManager, clock = clock)
        state = TankStateAppearing(eventManager, mock(), tank)
        tank.state = state
        tank.update()

        val appearingEndSubscriber: EventSubscriber = mock()
        eventManager.addSubscriber(appearingEndSubscriber, setOf(TankStateAppearing.End::class))

        clock.tick(TankStateAppearing.DEFAULT_ANIMATION_DURATION - 1)
        tank.update()
        verify(appearingEndSubscriber, never()).notify(TankStateAppearing.End(tank))

        eventManager.fireEvent(PauseManager.Start)

        clock.tick(100)
        tank.update()
        verify(appearingEndSubscriber, never()).notify(TankStateAppearing.End(tank))

        eventManager.fireEvent(PauseManager.End)

        tank.update()
        verify(appearingEndSubscriber, never()).notify(TankStateAppearing.End(tank))

        clock.tick(1)
        tank.update()
        verify(appearingEndSubscriber).notify(TankStateAppearing.End(tank))
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