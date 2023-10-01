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
    fun `should fire event on destroy`() {
        powerUp.destroy()
        powerUp.update()
        verify(eventManager).fireEvent(PowerUp.Destroyed(powerUp))
    }
}