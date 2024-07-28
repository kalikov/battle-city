package com.kalikov.game

import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.kotlin.mock
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
        tank = mockPlayerTank(eventManager, pauseManager, imageManager, clock)
        state = TankStateNormal(imageManager, tank)
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