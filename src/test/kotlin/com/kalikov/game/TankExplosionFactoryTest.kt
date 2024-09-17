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
        val tank = stubPlayerTank(game)
        factory.notify(Tank.Destroyed(tank))

        verify(spriteContainer).addSprite(isA<TankExplosion>())
    }

    @Test
    fun `should correctly place created explosion`() {
        val tank = stubPlayerTank(game, x = px(5), y = px(6))
        factory.notify(Tank.Destroyed(tank))

        val captor = argumentCaptor<TankExplosion>()
        verify(spriteContainer).addSprite(captor.capture())

        val explosion = captor.firstValue
        assertEquals(
            PixelRect(
                px(5) - t(1).toPixel(),
                px(6) - t(1).toPixel(),
                t(4).toPixel(),
                t(4).toPixel(),
            ),
            explosion.bounds
        )
        assertSame(tank, explosion.tank)
    }
}