package com.kalikov.game

import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.never
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class AITankControllerContainerTest {
    private lateinit var eventManager: EventManager
    private lateinit var container: AITankControllerContainer
    private lateinit var clock: TestClock

    @BeforeEach
    fun beforeEach() {
        eventManager = mock()
        clock = TestClock()
        container = AITankControllerContainer(eventManager, mock(), Point())
    }

    @Test
    fun `should subscribe`() {
        verify(eventManager).addSubscriber(
            container,
            setOf(
                Tank.Destroyed::class,
                EnemyFactory.EnemyCreated::class,
                PowerUpHandler.Freeze::class,
                FreezeHandler.Unfreeze::class
            )
        )
    }

    @Test
    fun `should create controller on enemy creation`() {
        val tank = mockTank(eventManager)
        container.notify(EnemyFactory.EnemyCreated(tank))

        assertTrue(container.hasController(tank))
    }

    @Test
    fun `should become frozen on freeze power-up`() {
        container.notify(PowerUpHandler.Freeze)
        assertTrue(container.isFrozen)
    }

    @Test
    fun `should become unfrozen on freeze timer ending`() {
        container.notify(PowerUpHandler.Freeze)
        container.notify(FreezeHandler.Unfreeze)
        assertFalse(container.isFrozen)
    }

    @Test
    fun `should create tanks with normal speed when not frozen`() {
        val tank = mockTank(eventManager)
        container.notify(EnemyFactory.EnemyCreated(tank))

        assertFalse(tank.isIdle)
    }

    @Test
    fun `should create tanks stopped when frozen`() {
        container.notify(PowerUpHandler.Freeze)

        val tank = mockTank(eventManager)
        container.notify(EnemyFactory.EnemyCreated(tank))

        assertTrue(tank.isIdle)
    }

    @Test
    fun `should create tanks with normal speed when unfrozen`() {
        container.notify(PowerUpHandler.Freeze)
        container.notify(FreezeHandler.Unfreeze)

        val tank = mockTank(eventManager)
        container.notify(EnemyFactory.EnemyCreated(tank))

        assertFalse(tank.isIdle)
    }

    @Test
    fun `should stop existing tanks on freeze`() {
        val tank = mockTank(eventManager)
        container.notify(EnemyFactory.EnemyCreated(tank))

        container.notify(PowerUpHandler.Freeze)

        assertTrue(tank.isIdle)
    }

    @Test
    fun `should restore tank speed when unfrozen`() {
        container.notify(PowerUpHandler.Freeze)

        val tank = mockTank(eventManager)
        container.notify(EnemyFactory.EnemyCreated(tank))

        container.notify(FreezeHandler.Unfreeze)

        assertFalse(tank.isIdle)
    }

    @Test
    fun `should remove controller when tank is destroyed`() {
        val tank = mockTank(eventManager)
        container.notify(EnemyFactory.EnemyCreated(tank))

        assertTrue(container.hasController(tank))

        container.notify(Tank.Destroyed(tank))

        assertFalse(container.hasController(tank))
    }

    @Test
    fun `should update controllers and shoot`() {
        container = AITankControllerContainer(
            eventManager,
            mock(),
            Point(),
            mock(),
            AITankControllerParams(clock = clock, shootInterval = 1, shootProbability = 1.0)
        )
        val tank1 = mockTank(eventManager)
        container.notify(EnemyFactory.EnemyCreated(tank1))

        val tank2 = mockTank(eventManager)
        container.notify(EnemyFactory.EnemyCreated(tank2))

        container.update()
        clock.tick(1)
        container.update()

        verify(eventManager).fireEvent(Tank.Shoot(tank1))
        verify(eventManager).fireEvent(Tank.Shoot(tank2))
    }

    @Test
    fun `should update controllers and update tank direction`() {
        container = AITankControllerContainer(
            eventManager,
            mock(),
            Point(),
            mock(),
            AITankControllerParams(
                clock = clock,
                directionUpdateInterval = 1,
                directionUpdateProbability = 1.0,
                directionRetreatProbability = 0.0
            )
        )

        val tank = mockTank(eventManager)
        container.notify(EnemyFactory.EnemyCreated(tank))

        container.update()
        clock.tick(1)
        container.update()

        assertEquals(Direction.DOWN, tank.direction)
    }

    @Test
    fun `should not update controllers when paused`() {
        val pauseManager: PauseManager = mock()
        whenever(pauseManager.isPaused).thenReturn(true)

        container = AITankControllerContainer(
            eventManager,
            pauseManager,
            Point(),
            mock(),
            AITankControllerParams(
                clock = clock,
                shootInterval = 1,
                shootProbability = 1.0
            )
        )
        val tank = mockTank(eventManager)
        container.notify(EnemyFactory.EnemyCreated(tank))

        container.update()
        clock.tick(1)
        container.update()

        verify(eventManager, never()).fireEvent(Tank.Shoot(tank))
    }

    @Test
    fun `should not update controllers when frozen`() {
        container = AITankControllerContainer(
            eventManager,
            mock(),
            Point(),
            mock(),
            AITankControllerParams(
                clock = clock,
                shootInterval = 1,
                shootProbability = 1.0
            )
        )
        container.notify(PowerUpHandler.Freeze)

        val tank = mockTank(eventManager)
        container.notify(EnemyFactory.EnemyCreated(tank))

        container.update()
        clock.tick(1)
        container.update()

        verify(eventManager, never()).fireEvent(Tank.Shoot(tank))
    }

    @Test
    fun `should remove subscriptions on dispose`() {
        container.dispose()

        verify(eventManager).removeSubscriber(
            container,
            setOf(
                Tank.Destroyed::class,
                EnemyFactory.EnemyCreated::class,
                PowerUpHandler.Freeze::class,
                FreezeHandler.Unfreeze::class
            )
        )
    }

    @Test
    fun `should remove controllers on dispose`() {
        val tank = mockTank(eventManager)
        container.notify(EnemyFactory.EnemyCreated(tank))

        container.dispose()

        assertFalse(container.hasController(tank))
    }
}