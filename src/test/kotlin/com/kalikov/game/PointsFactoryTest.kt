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
    private lateinit var game: Game
    private lateinit var spriteContainer: SpriteContainer
    private lateinit var factory: PointsFactory

    @BeforeEach
    fun beforeEach() {
        game = mockGame()
        spriteContainer = mock()
        factory = PointsFactory(game, spriteContainer)
    }

    @Test
    fun `should subscribe`() {
        verify(game.eventManager).addSubscriber(factory, setOf(TankExplosion.Destroyed::class, PowerUp.Pick::class))
    }

    @Test
    fun `should create points when enemy tank is destroyed`() {
        val tank = stubEnemyTank(game)

        val explosion = stubTankExplosion(game, tank = tank)
        factory.notify(TankExplosion.Destroyed(explosion))

        val captor = argumentCaptor<Points>()
        verify(spriteContainer).addSprite(captor.capture())
        val points = captor.firstValue

        assertNotNull(points)
        assertEquals(explosion.center, points.x + points.width / 2)
        assertEquals(explosion.middle, points.y + points.height / 2)
        assertEquals(tank.value, points.value)
    }

    @Test
    fun `should not create points when enemy tank with zero value is destroyed`() {
        val tank = stubEnemyTank(game)
        tank.devalue()

        val explosion = stubTankExplosion(game, tank = tank)
        factory.notify(TankExplosion.Destroyed(explosion))

        verify(spriteContainer, never()).addSprite(isA<Points>())
    }

    @Test
    fun `should not create points when player tank is destroyed`() {
        val tank = stubPlayerTank(game)

        val explosion = stubTankExplosion(game, tank = tank)
        factory.notify(TankExplosion.Destroyed(explosion))

        verify(spriteContainer, never()).addSprite(isA<Points>())
    }

    @Test
    fun `should create points when power up is picked`() {
        val powerUp = stubPowerUp(game, PixelPoint(px(1), px(2)))
        powerUp.value = 200

        val tank = stubPlayerTank(game)

        factory.notify(PowerUp.Pick(powerUp, tank))

        val captor = argumentCaptor<Points>()
        verify(spriteContainer).addSprite(captor.capture())
        val points = captor.firstValue

        assertNotNull(points)
        assertEquals(powerUp.center, points.center)
        assertEquals(powerUp.middle, points.middle)
        assertEquals(powerUp.value, points.value)
    }
}