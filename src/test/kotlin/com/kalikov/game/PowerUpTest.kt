package com.kalikov.game

import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import kotlin.test.assertTrue

class PowerUpTest {
    private lateinit var eventManager: EventManager
    private lateinit var powerUp: PowerUp

    @BeforeEach
    fun beforeEach() {
        eventManager = mock()
        powerUp = PowerUp(eventManager, mock(), Point(), mock())
    }

    @Test
    fun `should destroy on pick`() {
        val tank = mockPlayerTank(mockGame(eventManager = eventManager))
        powerUp.pick(tank)

        assertTrue(powerUp.isDestroyed)
        verify(eventManager).fireEvent(PowerUp.Pick(powerUp, tank))
    }
}