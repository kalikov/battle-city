package com.kalikov.game

import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.kotlin.argumentCaptor
import org.mockito.kotlin.isA
import org.mockito.kotlin.mock
import org.mockito.kotlin.never
import org.mockito.kotlin.reset
import org.mockito.kotlin.verify
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertNotNull
import kotlin.test.assertNotSame
import kotlin.test.assertNull
import kotlin.test.assertSame

class PlayerTankFactoryTest {
    private lateinit var eventManager: EventManager
    private lateinit var spriteContainer: SpriteContainer
    private lateinit var factory: PlayerTankFactory

    @BeforeEach
    fun beforeEach() {
        eventManager = mock()
        spriteContainer = mock()
        factory = PlayerTankFactory(eventManager, mock(), mock(), spriteContainer, Point(10, 100), mock())
    }

    @Test
    fun `should subscribe`() {
        verify(eventManager).addSubscriber(factory, setOf(TankExplosion.Destroyed::class, Player.OutOfLives::class))
    }

    @Test
    fun `should create tank right away`() {
        val tank = factory.playerTank
        assertNotNull(tank)
        assertEquals(10, tank.x)
        assertEquals(100, tank.y)
        assertIs<TankStateAppearing>(tank.state)
        verify(eventManager).fireEvent(PlayerTankFactory.PlayerTankCreated(tank))
    }

    @Test
    fun `should create new tank after explosion`() {
        reset(eventManager)

        val explosion = mockTankExplosion(eventManager, tank = factory.playerTank!!)
        factory.notify(TankExplosion.Destroyed(explosion))

        verify(eventManager).fireEvent(isA<PlayerTankFactory.PlayerTankCreated>())
    }

    @Test
    fun `should add tank to the sprite container`() {
        reset(eventManager)
        val firstTank = factory.playerTank
        assertNotNull(firstTank)

        factory.notify(TankExplosion.Destroyed(mockTankExplosion(eventManager, tank = firstTank)))

        val captor = argumentCaptor<PlayerTankFactory.PlayerTankCreated>()
        verify(eventManager).fireEvent(captor.capture())

        val tank = captor.firstValue.tank
        assertNotSame(firstTank, tank)
        assertSame(tank, factory.playerTank)

        verify(spriteContainer).addSprite(tank)
        assertEquals(10, tank.x)
        assertEquals(100, tank.y)
        assertIs<TankStateAppearing>(tank.state)
    }

    @Test
    fun `should not create new tank when player is out of lives`() {
        reset(eventManager)

        factory.notify(Player.OutOfLives)
        val explosion = mockTankExplosion(eventManager, tank = factory.playerTank!!)
        factory.notify(TankExplosion.Destroyed(explosion))

        verify(eventManager, never()).fireEvent(isA<PlayerTankFactory.PlayerTankCreated>())
        assertNull(factory.playerTank)
    }
}