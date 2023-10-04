package com.kalikov.game

import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify

class PlayerTankControllerTest {
    private lateinit var eventManager: EventManager
    private lateinit var tank: PlayerTankHandle
    private lateinit var controller: PlayerTankController

    @BeforeEach
    fun beforeEach() {
        eventManager = mock()
        tank = mock()

        controller = PlayerTankController(eventManager, mock(), tank)
    }

    @Test
    fun `should shoot when space key is pressed`() {
        controller.notify(Keyboard.KeyPressed(Keyboard.Key.ACTION))

        verify(tank).startShooting()
    }
}