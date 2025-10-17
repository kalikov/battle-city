package com.kalikov.game

import com.kalikov.engine.EventManager
import com.kalikov.util.TestClock
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.never
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

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
        val game = mockGame(eventManager = eventManager, clock = clock)
        tank = stubPlayerTank(game, pauseManager)
        state = TankStateInvincible(game, pauseManager, tank, 10)
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

        clock.tick(7)
        tank.update()
        verify(eventManager).fireEvent(TankStateInvincible.End(tank))
    }

    @Test
    fun `should pause invincible state duration`() {
        tank.update()

        clock.tick(5)
        tank.update()
        verify(eventManager, never()).fireEvent(TankStateInvincible.End(tank))

        whenever(pauseManager.isPaused).thenReturn(true)
        tank.update()

        clock.tick(100)
        tank.update()
        verify(eventManager, never()).fireEvent(TankStateInvincible.End(tank))

        whenever(pauseManager.isPaused).thenReturn(false)
        tank.update()

        clock.tick(4)
        tank.update()
        verify(eventManager, never()).fireEvent(TankStateInvincible.End(tank))

        clock.tick(1)
        state.update()
        verify(eventManager).fireEvent(TankStateInvincible.End(tank))
    }
}