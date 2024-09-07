package com.kalikov.game

import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.reset
import org.mockito.kotlin.verify
import org.mockito.kotlin.verifyNoMoreInteractions
import kotlin.test.assertNotNull
import kotlin.test.assertNotSame
import kotlin.test.assertSame

class PlayerTankControllerFactoryTest {
    private val controllerSubscriptions = setOf(
        BaseExplosion.Destroyed::class,
        Keyboard.KeyPressed::class,
        Keyboard.KeyReleased::class
    )

    private lateinit var game: Game
    private lateinit var eventManager: EventManager
    private lateinit var factory: PlayerTankControllerFactory

    @BeforeEach
    fun beforeEach() {
        eventManager = mock()
        game = mockGame(eventManager = eventManager)

        factory = PlayerTankControllerFactory(eventManager, mock(), Player(eventManager))
    }

    @Test
    fun `should subscribe`() {
        verify(eventManager).addSubscriber(factory, setOf(PlayerTankFactory.PlayerTankCreated::class))
    }

    @Test
    fun `should create controller on player tank creation`() {
        val tank = mockPlayerTank(game, player = factory.player)

        reset(eventManager)
        factory.notify(PlayerTankFactory.PlayerTankCreated(tank))

        val controller = factory.controller
        assertNotNull(controller)
        assertSame(tank, controller.tank)
        verify(eventManager).addSubscriber(controller, controllerSubscriptions)
        verifyNoMoreInteractions(eventManager)
    }

    @Test
    fun `should recreate controller on subsequent player tank creation`() {
        val tank1 = mockPlayerTank(game, player = factory.player)
        factory.notify(PlayerTankFactory.PlayerTankCreated(tank1))
        val controller1 = factory.controller
        assertNotNull(controller1)

        val tank2 = mockPlayerTank(game, player = factory.player)
        reset(eventManager)

        factory.notify(PlayerTankFactory.PlayerTankCreated(tank2))
        val controller2 = factory.controller
        assertNotNull(controller2)
        assertSame(tank2, controller2.tank)

        assertNotSame(controller1, controller2)

        verify(eventManager).removeSubscriber(controller1, controllerSubscriptions)
        verify(eventManager).addSubscriber(controller2, controllerSubscriptions)
        verifyNoMoreInteractions(eventManager)
    }
}