package com.kalikov.game

import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertIs
import kotlin.test.assertTrue

class PowerUpHandlerTest {
    private lateinit var eventManager: EventManager
    private lateinit var powerUp: PowerUp
    private lateinit var handler: PowerUpHandler
    private lateinit var tank: Tank

    @BeforeEach
    fun beforeEach() {
        eventManager = mock()
        powerUp = mockPowerUp(eventManager)
        handler = PowerUpHandler(eventManager, mock())

        tank = mockTank(eventManager)
    }

    @Test
    fun `should subscribe`() {
        verify(eventManager).addSubscriber(
            handler,
            setOf(
                PowerUp.Pick::class,
                EnemyFactory.EnemyCreated::class,
                Tank.Destroyed::class
            )
        )
    }

    @Test
    fun `should unsubscribe`() {
        handler.dispose()
        verify(eventManager).removeSubscriber(
            handler,
            setOf(
                PowerUp.Pick::class,
                EnemyFactory.EnemyCreated::class,
                Tank.Destroyed::class
            )
        )
    }

    @Test
    fun `should handle grenade`() {
        val enemy = mockTank(eventManager)
        enemy.enemyType = Tank.EnemyType.BASIC
        handler.notify(EnemyFactory.EnemyCreated(enemy, false))

        powerUp.type = PowerUp.Type.GRENADE
        handler.notify(PowerUp.Pick(powerUp, tank))

        assertEquals(0, enemy.value)
        assertTrue(enemy.isDestroyed)
    }

    @Test
    fun `should not explode appearing enemies with grenade`() {
        val enemy = mockTank(eventManager)
        enemy.enemyType = Tank.EnemyType.BASIC
        enemy.state = TankStateAppearing(eventManager, mock(), enemy)
        handler.notify(EnemyFactory.EnemyCreated(enemy, false))

        powerUp.type = PowerUp.Type.GRENADE
        handler.notify(PowerUp.Pick(powerUp, tank))

        assertEquals(100, enemy.value)
        assertFalse(enemy.isDestroyed)
    }

    @Test
    fun `should handle helmet`() {
        powerUp.type = PowerUp.Type.HELMET
        handler.notify(PowerUp.Pick(powerUp, tank))

        assertIs<TankStateInvincible>(tank.state)
    }

    @Test
    fun `should handle timer`() {
        powerUp.type = PowerUp.Type.TIMER
        handler.notify(PowerUp.Pick(powerUp, tank))

        verify(eventManager).fireEvent(PowerUpHandler.Freeze)
    }

    @Test
    fun `should handle shovel`() {
        powerUp.type = PowerUp.Type.SHOVEL
        handler.notify(PowerUp.Pick(powerUp, tank))

        verify(eventManager).fireEvent(PowerUpHandler.ShovelStart)
    }

    @Test
    fun `should handle star`() {
        powerUp.type = PowerUp.Type.STAR
        handler.notify(PowerUp.Pick(powerUp, tank))

        assertEquals(1, tank.upgradeLevel)
    }

    @Test
    fun `should handle tank`() {
        powerUp.type = PowerUp.Type.TANK
        handler.notify(PowerUp.Pick(powerUp, tank))

        verify(eventManager).fireEvent(PowerUpHandler.Life)
    }
}