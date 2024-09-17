package com.kalikov.game

import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.kotlin.argumentCaptor
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import kotlin.test.assertEquals

class BulletExplosionFactoryTest {
    private lateinit var game: Game
    private lateinit var spriteContainer: SpriteContainer
    private lateinit var explosionFactory: BulletExplosionFactory
    private lateinit var bullet: BulletHandle

    @BeforeEach
    fun beforeEach() {
        game = mockGame()
        spriteContainer = mock()
        explosionFactory = BulletExplosionFactory(game, spriteContainer)

        bullet = mock {
            on { center } doReturn px(0)
            on { middle } doReturn px(0)
        }
    }

    @Test
    fun `should subscribe`() {
        verify(game.eventManager).addSubscriber(explosionFactory, setOf(Bullet.Exploded::class))
    }

    @Test
    fun `should unsubscribe`() {
        explosionFactory.dispose()
        verify(game.eventManager).removeSubscriber(explosionFactory, setOf(Bullet.Exploded::class))
    }

    @Test
    fun `should place explosion correctly`() {
        explosionFactory.notify(Bullet.Exploded(bullet))

        val captor = argumentCaptor<BulletExplosion>()
        verify(spriteContainer).addSprite(captor.capture())
        val explosion = captor.firstValue

        assertEquals(
            PixelRect(
                bullet.center - BulletExplosion.SIZE / 2,
                bullet.middle - BulletExplosion.SIZE / 2,
                BulletExplosion.SIZE,
                BulletExplosion.SIZE,
            ),
            explosion.bounds
        )
    }
}