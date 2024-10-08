package com.kalikov.game

import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.kotlin.mock
import kotlin.test.assertTrue

class TankStateNormalTest {
    private lateinit var game: Game
    private lateinit var pauseManager: PauseManager
    private lateinit var tank: Tank
    private lateinit var state: TankStateNormal

    @BeforeEach
    fun beforeEach() {
        game = mockGame(clock = TestClock())
        pauseManager = mock()
        tank = stubPlayerTank(game, pauseManager)
        state = TankStateNormal(game.imageManager, tank)
        tank.state = state
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