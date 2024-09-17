package com.kalikov.game

import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.kotlin.argumentCaptor
import org.mockito.kotlin.isA
import org.mockito.kotlin.mock
import org.mockito.kotlin.never
import org.mockito.kotlin.times
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertSame
import kotlin.test.assertTrue

class AITankControllerContainerTest {
    private lateinit var game: Game
    private lateinit var eventManager: EventManager
    private lateinit var container: AITankControllerContainer
    private lateinit var clock: TestClock

    @BeforeEach
    fun beforeEach() {
        clock = TestClock()
        game = mockGame(clock = clock)
        eventManager = game.eventManager
        container = AITankControllerContainer(eventManager, mock(), PixelPoint())
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
        val tank = stubEnemyTank(game)
        container.notify(EnemyFactory.EnemyCreated(tank, false))

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
        val tank = stubEnemyTank(game)
        container.notify(EnemyFactory.EnemyCreated(tank, false))

        assertFalse(tank.isIdle)
    }

    @Test
    fun `should create tanks stopped when frozen`() {
        container.notify(PowerUpHandler.Freeze)

        val tank = stubEnemyTank(game)
        container.notify(EnemyFactory.EnemyCreated(tank, false))

        assertTrue(tank.isIdle)
    }

    @Test
    fun `should create tanks with normal speed when unfrozen`() {
        container.notify(PowerUpHandler.Freeze)
        container.notify(FreezeHandler.Unfreeze)

        val tank = stubEnemyTank(game)
        container.notify(EnemyFactory.EnemyCreated(tank, false))

        assertFalse(tank.isIdle)
    }

    @Test
    fun `should stop existing tanks on freeze`() {
        val tank = stubEnemyTank(game)
        container.notify(EnemyFactory.EnemyCreated(tank, false))

        container.notify(PowerUpHandler.Freeze)

        assertTrue(tank.isIdle)
    }

    @Test
    fun `should restore tank speed when unfrozen`() {
        container.notify(PowerUpHandler.Freeze)

        val tank = stubEnemyTank(game)
        container.notify(EnemyFactory.EnemyCreated(tank, false))

        container.notify(FreezeHandler.Unfreeze)

        assertFalse(tank.isIdle)
    }

    @Test
    fun `should remove controller when tank is destroyed`() {
        val tank = stubEnemyTank(game)
        container.notify(EnemyFactory.EnemyCreated(tank, false))

        assertTrue(container.hasController(tank))

        container.notify(Tank.Destroyed(tank))

        assertFalse(container.hasController(tank))
    }

    @Test
    fun `should update controllers and shoot`() {
        container = AITankControllerContainer(
            eventManager,
            mock(),
            PixelPoint(),
            mock(),
            AITankControllerParams(clock = clock, shootInterval = 1, shootProbability = 1.0)
        )
        val tank1 = stubEnemyTank(game)
        container.notify(EnemyFactory.EnemyCreated(tank1, false))

        val tank2 = stubEnemyTank(game)
        container.notify(EnemyFactory.EnemyCreated(tank2, false))

        container.update()
        clock.tick(1)
        container.update()

        val captor = argumentCaptor<Tank.Shoot>()
        verify(eventManager, times(2)).fireEvent(captor.capture())
        assertSame(captor.firstValue.bullet.tank, tank1)
        assertSame(captor.secondValue.bullet.tank, tank2)
    }

    @Test
    fun `should update controllers and update tank direction`() {
        container = AITankControllerContainer(
            eventManager,
            mock(),
            PixelPoint(),
            mock(),
            AITankControllerParams(
                clock = clock,
                directionUpdateInterval = 1,
                directionUpdateProbability = 1.0,
                directionRetreatProbability = 0.0
            )
        )

        val tank = stubEnemyTank(game)
        container.notify(EnemyFactory.EnemyCreated(tank, false))

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
            PixelPoint(),
            mock(),
            AITankControllerParams(
                clock = clock,
                shootInterval = 1,
                shootProbability = 1.0
            )
        )
        val tank = stubEnemyTank(game)
        container.notify(EnemyFactory.EnemyCreated(tank, false))

        container.update()
        clock.tick(1)
        container.update()

        verify(eventManager, never()).fireEvent(isA<Tank.Shoot>())
    }

    @Test
    fun `should not update controllers when frozen`() {
        container = AITankControllerContainer(
            eventManager,
            mock(),
            PixelPoint(),
            mock(),
            AITankControllerParams(
                clock = clock,
                shootInterval = 1,
                shootProbability = 1.0
            )
        )
        container.notify(PowerUpHandler.Freeze)

        val tank = stubEnemyTank(game)
        container.notify(EnemyFactory.EnemyCreated(tank, false))

        container.update()
        clock.tick(1)
        container.update()

        verify(eventManager, never()).fireEvent(isA<Tank.Shoot>())
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
        val tank = stubEnemyTank(game)
        container.notify(EnemyFactory.EnemyCreated(tank, false))

        container.dispose()

        assertFalse(container.hasController(tank))
    }
}