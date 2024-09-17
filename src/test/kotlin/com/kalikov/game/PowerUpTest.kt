package com.kalikov.game

import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.kotlin.verify
import kotlin.test.assertTrue

class PowerUpTest {
    private lateinit var game: Game
    private lateinit var powerUp: PowerUp

    @BeforeEach
    fun beforeEach() {
        game = mockGame()
        powerUp = PowerUp(game, PixelPoint())
    }

    @Test
    fun `should destroy on pick`() {
        val tank = stubPlayerTank(game)
        powerUp.pick(tank)

        assertTrue(powerUp.isDestroyed)
        verify(game.eventManager).fireEvent(PowerUp.Pick(powerUp, tank))
    }
}