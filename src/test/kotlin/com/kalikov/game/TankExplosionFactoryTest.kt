package com.kalikov.game

import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.kotlin.argumentCaptor
import org.mockito.kotlin.isA
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import kotlin.test.assertEquals
import kotlin.test.assertSame

class TankExplosionFactoryTest {
    private lateinit var game: Game
    private lateinit var spriteContainer: SpriteContainer
    private lateinit var factory: TankExplosionFactory

    @BeforeEach
    fun beforeEach() {
        game = mockGame()
        spriteContainer = mock()
        factory = TankExplosionFactory(game, spriteContainer)
    }

    @Test
    fun `should subscribe`() {
        verify(game.eventManager).addSubscriber(factory, setOf(Tank.Destroyed::class))
    }

    @Test
    fun `should unsubscribe`() {
        factory.dispose()
        verify(game.eventManager).removeSubscriber(factory, setOf(Tank.Destroyed::class))
    }

    @Test
    fun `should create explosion when tank is destroyed`() {
        val tank = mockPlayerTank(game)
        factory.notify(Tank.Destroyed(tank))

        verify(spriteContainer).addSprite(isA<TankExplosion>())
    }

    @Test
    fun `should correctly place created explosion`() {
        val tank = mockPlayerTank(game, x = 5, y = 6)
        factory.notify(Tank.Destroyed(tank))

        val captor = argumentCaptor<TankExplosion>()
        verify(spriteContainer).addSprite(captor.capture())

        val explosion = captor.firstValue
        assertEquals(
            Rect(
                5 - Globals.UNIT_SIZE / 2,
                6 - Globals.UNIT_SIZE / 2,
                2 * Globals.UNIT_SIZE,
                2 * Globals.UNIT_SIZE
            ),
            explosion.bounds
        )
        assertSame(tank, explosion.tank)
    }
}