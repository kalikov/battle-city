package com.kalikov.game

import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.kotlin.argumentCaptor
import org.mockito.kotlin.isA
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class PowerUpFactoryTest {
    private lateinit var eventManager: EventManager
    private lateinit var spriteContainer: SpriteContainer
    private lateinit var factory: PowerUpFactory

    @BeforeEach
    fun beforeEach() {
        eventManager = mock()
        spriteContainer = mock()
        factory = PowerUpFactory(eventManager, mock(), spriteContainer, mock(), Rect(), mock())
    }

    @Test
    fun `should subscribe`() {
        verify(eventManager).addSubscriber(
            factory,
            setOf(EnemyFactory.FlashingTankHit::class, EnemyFactory.EnemyCreated::class)
        )
    }

    @Test
    fun `should unsubscribe`() {
        factory.dispose()
        verify(eventManager).removeSubscriber(
            factory,
            setOf(EnemyFactory.FlashingTankHit::class, EnemyFactory.EnemyCreated::class)
        )
    }

    @Test
    fun `should create power-up when flashing tank is destroyed`() {
        factory.notify(EnemyFactory.FlashingTankHit)

        verify(spriteContainer).addSprite(isA<PowerUp>())
    }

    @Test
    fun `should destroy power up when new flashing type appears`() {
        factory.notify(EnemyFactory.FlashingTankHit)

        val captor = argumentCaptor<PowerUp>()
        verify(spriteContainer).addSprite(captor.capture())

        val powerUp = captor.firstValue
        assertNotNull(powerUp)
        assertFalse(powerUp.isDestroyed)

        val newTank = mockEnemyTank(mockGame(eventManager = eventManager))
        factory.notify(EnemyFactory.EnemyCreated(newTank, true))

        assertTrue(powerUp.isDestroyed)
    }

    @Test
    fun `should destroy power up when new flashing tank is destroyed`() {
        factory.notify(EnemyFactory.FlashingTankHit)

        val captor = argumentCaptor<PowerUp>()
        verify(spriteContainer).addSprite(captor.capture())

        val powerUp = captor.firstValue
        assertNotNull(powerUp)
        assertFalse(powerUp.isDestroyed)

        factory.notify(EnemyFactory.FlashingTankHit)

        assertTrue(powerUp.isDestroyed)
    }
}