package com.kalikov.game

import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import kotlin.test.assertSame

class PlayerTankControllerTest {
    private val controllerSubscriptions = setOf(
        PlayerTankFactory.PlayerTankCreated::class,
        BaseExplosion.Destroyed::class,
        Keyboard.KeyPressed::class,
        Keyboard.KeyReleased::class
    )

    private lateinit var eventManager: EventManager
    private lateinit var tank: PlayerTankHandle
    private lateinit var controller: PlayerTankController

    @BeforeEach
    fun beforeEach() {
        eventManager = mock()
        tank = mock()
        doReturn(Player(eventManager)).whenever(tank).player

        controller = PlayerTankController(eventManager, mock(), tank.player)
    }

    @Test
    fun `should subscribe`() {
        verify(eventManager).addSubscriber(controller, controllerSubscriptions)
    }

    @Test
    fun `should shoot when space key is pressed`() {
        controller.notify(PlayerTankFactory.PlayerTankCreated(tank))
        controller.notify(Keyboard.KeyPressed(Keyboard.Key.ACTION, 0))

        verify(tank).startShooting()
    }

    @Test
    fun `should create controller on player tank creation`() {
        val tank = stubPlayerTank(mockGame(eventManager = eventManager), player = tank.player)

        controller.notify(PlayerTankFactory.PlayerTankCreated(tank))

        assertSame(tank, controller.tank)
    }

    @Test
    fun `should recreate controller on subsequent player tank creation`() {
        val game = mockGame(eventManager = eventManager)
        val tank1 = stubPlayerTank(game, player = tank.player)
        controller.notify(PlayerTankFactory.PlayerTankCreated(tank1))

        val tank2 = stubPlayerTank(game, player = tank.player)

        controller.notify(PlayerTankFactory.PlayerTankCreated(tank2))

        assertSame(tank2, controller.tank)
    }
}