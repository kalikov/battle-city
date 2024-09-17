package com.kalikov.game

import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify

class TankExplosionTest {
    private lateinit var eventManager: EventManager
    private lateinit var tank: Tank
    private lateinit var explosion: TankExplosion

    @BeforeEach
    fun beforeEach() {
        eventManager = mock()
        val game = mockGame(eventManager = eventManager)
        tank = stubPlayerTank(game)
        explosion = TankExplosion(game, tank)
    }

    @Test
    fun `should fire event on destroy`() {
        explosion.destroy()
        explosion.update()
        verify(eventManager).fireEvent(TankExplosion.Destroyed(explosion))
    }
}