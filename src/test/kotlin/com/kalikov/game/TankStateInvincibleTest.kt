package com.kalikov.game

import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.never
import org.mockito.kotlin.verify

class TankStateInvincibleTest {
    private lateinit var eventManager: EventManager
    private lateinit var pauseManager: PauseManager
    private lateinit var clock: TestClock
    private lateinit var tank: Tank
    private lateinit var state: TankStateInvincible

    @BeforeEach
    fun beforeEach() {
        eventManager = mock()
        pauseManager = mock()
        clock = TestClock()
        tank = mockPlayerTank(mockGame(eventManager = eventManager), pauseManager, clock = clock)
        state = TankStateInvincible(eventManager, mock(), tank, 4)
        tank.state = state
    }

    @Test
    fun `should fire event on duration end`() {
        tank.update()

        clock.tick(1)
        tank.update()
        verify(eventManager, never()).fireEvent(TankStateInvincible.End(tank))

        clock.tick(1)
        tank.update()
        verify(eventManager, never()).fireEvent(TankStateInvincible.End(tank))

        clock.tick(1)
        tank.update()
        verify(eventManager, never()).fireEvent(TankStateInvincible.End(tank))

        clock.tick(1)
        tank.update()
        verify(eventManager).fireEvent(TankStateInvincible.End(tank))
    }

    @Test
    fun `should pause invincible state duration`() {
        eventManager = ConcurrentEventManager()
        val pauseListener = PauseListener(eventManager)
        pauseManager = pauseListener
        tank = mockPlayerTank(mockGame(eventManager = eventManager), pauseManager, clock = clock)
        state = TankStateInvincible(eventManager, mock(), tank, 10)
        tank.state = state
        tank.update()

        val invincibleEndSubscriber: EventSubscriber = mock()
        eventManager.addSubscriber(invincibleEndSubscriber, setOf(TankStateInvincible.End::class))

        clock.tick(5)
        tank.update()
        verify(invincibleEndSubscriber, never()).notify(TankStateInvincible.End(tank))

        eventManager.fireEvent(PauseManager.Start)

        clock.tick(100)
        tank.update()
        verify(invincibleEndSubscriber, never()).notify(TankStateInvincible.End(tank))

        eventManager.fireEvent(PauseManager.End)

        clock.tick(4)
        tank.update()
        verify(invincibleEndSubscriber, never()).notify(TankStateInvincible.End(tank))

        clock.tick(1)
        state.update()
        verify(invincibleEndSubscriber).notify(TankStateInvincible.End(tank))
    }
}