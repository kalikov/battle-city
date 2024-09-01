package com.kalikov.game

import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.kotlin.argumentCaptor
import org.mockito.kotlin.isA
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import kotlin.test.assertEquals

class BulletExplosionFactoryTest {
    private lateinit var eventManager: EventManager
    private lateinit var spriteContainer: SpriteContainer
    private lateinit var explosionFactory: BulletExplosionFactory
    private lateinit var bullet: Bullet

    @BeforeEach
    fun beforeEach() {
        eventManager = mock()
        spriteContainer = mock()
        explosionFactory = BulletExplosionFactory(eventManager, mock(), spriteContainer, mock())

        val tank = mockPlayerTank(eventManager)
        bullet = tank.createBullet()
    }

    @Test
    fun `should subscribe`() {
        verify(eventManager).addSubscriber(explosionFactory, setOf(Bullet.Exploded::class))
    }

    @Test
    fun `should unsubscribe`() {
        explosionFactory.dispose()
        verify(eventManager).removeSubscriber(explosionFactory, setOf(Bullet.Exploded::class))
    }

    @Test
    fun `should place explosion correctly`() {
        explosionFactory.notify(Bullet.Exploded(bullet))

        val captor = argumentCaptor<BulletExplosion>()
        verify(spriteContainer).addSprite(captor.capture())
        val explosion = captor.firstValue

        assertEquals(
            Rect(
                bullet.center.x - Globals.UNIT_SIZE / 2,
                bullet.center.y - Globals.UNIT_SIZE / 2,
                Globals.UNIT_SIZE,
                Globals.UNIT_SIZE
            ),
            explosion.bounds
        )
    }

    @Test
    fun `should create explosion when bullet exploded`() {
        bullet.hit(true)
        explosionFactory.notify(Bullet.Exploded(bullet))

        verify(spriteContainer).addSprite(isA<BulletExplosion>())
    }
}