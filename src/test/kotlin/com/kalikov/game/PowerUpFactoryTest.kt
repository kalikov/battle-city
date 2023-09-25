package com.kalikov.game

import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.kotlin.isA
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify

class PowerUpFactoryTest {
    private lateinit var eventManager: EventManager
    private lateinit var spriteContainer: SpriteContainer
    private lateinit var factory: PowerUpFactory

    @BeforeEach
    fun beforeEach() {
        eventManager = mock()
        spriteContainer = mock()
        factory = PowerUpFactory(eventManager, mock(), spriteContainer, mock())
    }

    @Test
    fun `should subscribe`() {
        verify(eventManager).addSubscriber(factory, setOf(Tank.FlashingTankHit::class))
    }

    @Test
    fun `should unsubscribe`() {
        factory.dispose()
        verify(eventManager).removeSubscriber(factory, setOf(Tank.FlashingTankHit::class))
    }

    @Test
    fun `should create power-up when flashing tank is destroyed`() {
        val tank = mockTank(eventManager)
        tank.isFlashing = true

        factory.notify(Tank.FlashingTankHit(tank))

        verify(spriteContainer).addSprite(isA<PowerUp>())
    }
}