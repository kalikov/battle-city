package com.kalikov.game

import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.kotlin.argumentCaptor
import org.mockito.kotlin.isA
import org.mockito.kotlin.mock
import org.mockito.kotlin.never
import org.mockito.kotlin.verify
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class PointsFactoryTest {
    private lateinit var eventManager: EventManager
    private lateinit var spriteContainer: SpriteContainer
    private lateinit var factory: PointsFactory

    @BeforeEach
    fun beforeEach() {
        eventManager = mock()
        spriteContainer = mock()
        factory = PointsFactory(eventManager, mock(), spriteContainer, mock())
    }

    @Test
    fun `should subscribe`() {
        verify(eventManager).addSubscriber(factory, setOf(TankExplosion.Destroyed::class, PowerUp.Pick::class))
    }

    @Test
    fun `should create points when enemy tank is destroyed`() {
        val tank = mockEnemyTank(eventManager)

        val explosion = mockTankExplosion(eventManager, tank = tank)
        factory.notify(TankExplosion.Destroyed(explosion))

        val captor = argumentCaptor<Points>()
        verify(spriteContainer).addSprite(captor.capture())
        val points = captor.firstValue

        assertNotNull(points)
        assertEquals(explosion.center.x, points.x + points.width / 2)
        assertEquals(explosion.center.y, points.y + points.height / 2)
        assertEquals(tank.value, points.value)
    }

    @Test
    fun `should not create points when enemy tank with zero value is destroyed`() {
        val tank = mockEnemyTank(eventManager)
        tank.devalue()

        val explosion = mockTankExplosion(eventManager, tank = tank)
        factory.notify(TankExplosion.Destroyed(explosion))

        verify(spriteContainer, never()).addSprite(isA<Points>())
    }

    @Test
    fun `should not create points when player tank is destroyed`() {
        val tank = mockPlayerTank(eventManager)

        val explosion = mockTankExplosion(eventManager, tank = tank)
        factory.notify(TankExplosion.Destroyed(explosion))

        verify(spriteContainer, never()).addSprite(isA<Points>())
    }

    @Test
    fun `should create points when power up is picked`() {
        val powerUp = mockPowerUp(eventManager, position = Point(1, 2))
        powerUp.value = 200

        val tank = mockPlayerTank(eventManager)

        factory.notify(PowerUp.Pick(powerUp, tank))

        val captor = argumentCaptor<Points>()
        verify(spriteContainer).addSprite(captor.capture())
        val points = captor.firstValue

        assertNotNull(points)
        assertEquals(powerUp.center.x, points.x + points.width / 2)
        assertEquals(powerUp.center.y, points.y + points.height / 2)
        assertEquals(powerUp.value, points.value)
    }
}