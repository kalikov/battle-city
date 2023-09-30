package com.kalikov.game

import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.argumentCaptor
import org.mockito.kotlin.clearInvocations
import org.mockito.kotlin.isA
import org.mockito.kotlin.mock
import org.mockito.kotlin.never
import org.mockito.kotlin.reset
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertIs
import kotlin.test.assertTrue

class EnemyFactoryTest {
    private lateinit var clock: TestClock
    private lateinit var eventManager: EventManager
    private lateinit var pauseManager: PauseManager
    private lateinit var spriteContainer: SpriteContainer

    @BeforeEach
    fun beforeEach() {
        clock = TestClock()
        eventManager = mock()
        pauseManager = mock()
        spriteContainer = mock()
    }

    @Test
    fun `should subscribe`() {
        val factory = createFactory(emptyList(), emptyList())
        verify(eventManager).addSubscriber(factory, setOf(Points.Destroyed::class, TankExplosion.Destroyed::class))
    }

    @Test
    fun `should return positions in a loop`() {
        val position1 = Point(0, 0)
        val position2 = Point(10, 20)
        val position3 = Point(40, 100)
        val factory = createFactory(
            listOf(position1, position2, position3),
            emptyList()
        )
        assertEquals(position1, factory.position)
        factory.nextPosition()
        assertEquals(position2, factory.position)
        factory.nextPosition()
        assertEquals(position3, factory.position)
        factory.nextPosition()
        assertEquals(position1, factory.position)
    }

    @Test
    fun `should iterate over enemies`() {
        val group1 = EnemyGroupConfig(Tank.EnemyType.BASIC, 1)
        val group2 = EnemyGroupConfig(Tank.EnemyType.FAST, 2)
        val group3 = EnemyGroupConfig(Tank.EnemyType.BASIC, 1)
        val factory = createFactory(
            emptyList(),
            listOf(group1, group2, group3)
        )
        assertEquals(group1.type, factory.enemy)
        factory.nextEnemy()
        assertEquals(group2.type, factory.enemy)
        factory.nextEnemy()
        assertEquals(group2.type, factory.enemy)
        factory.nextEnemy()
        assertEquals(group3.type, factory.enemy)
    }

    @Test
    fun `should spawn no more than limit respecting interval`() {
        val factory = createFactory(
            listOf(Point()),
            listOf(EnemyGroupConfig(Tank.EnemyType.BASIC, 4)),
            4,
        )
        factory.enemyCountLimit = 2

        factory.update()
        assertEquals(1, factory.enemyCount)
        assertFalse(factory.enemyCountLimitReached)

        clock.tick(1)
        factory.update()
        assertEquals(1, factory.enemyCount)
        assertFalse(factory.enemyCountLimitReached)

        clock.tick(1)
        factory.update()
        assertEquals(1, factory.enemyCount)
        assertFalse(factory.enemyCountLimitReached)

        clock.tick(1)
        factory.update()
        assertEquals(1, factory.enemyCount)
        assertFalse(factory.enemyCountLimitReached)

        clock.tick(1)
        factory.update()
        assertEquals(2, factory.enemyCount)
        assertTrue(factory.enemyCountLimitReached)

        clock.tick(1)
        factory.update()
        assertEquals(2, factory.enemyCount)
        assertTrue(factory.enemyCountLimitReached)

        clock.tick(1)
        factory.update()
        assertEquals(2, factory.enemyCount)
        assertTrue(factory.enemyCountLimitReached)

        clock.tick(1)
        factory.update()
        assertEquals(2, factory.enemyCount)
        assertTrue(factory.enemyCountLimitReached)

        clock.tick(1)
        factory.update()
        assertEquals(2, factory.enemyCount)
        assertTrue(factory.enemyCountLimitReached)
    }

    @Test
    fun `should not spawn when paused`() {
        whenever(pauseManager.isPaused).thenReturn(true)

        val factory = createFactory(
            listOf(Point()),
            listOf(EnemyGroupConfig(Tank.EnemyType.BASIC, 4)),
            1
        )

        factory.update()
        assertEquals(0, factory.enemyCount)

        clock.tick(1)
        factory.update()
        assertEquals(0, factory.enemyCount)

        clock.tick(1)
        factory.update()
        assertEquals(0, factory.enemyCount)
    }

    @Test
    fun `should stop creation when no more enemies left`() {
        val positions = listOf(Point(0, 0), Point(10, 20), Point(40, 100))
        val group1 = EnemyGroupConfig(Tank.EnemyType.BASIC, 1)
        val group2 = EnemyGroupConfig(Tank.EnemyType.FAST, 2)
        val group3 = EnemyGroupConfig(Tank.EnemyType.BASIC, 1)
        val factory = createFactory(
            positions,
            listOf(group1, group2, group3),
            1
        )
        factory.flashingTanks = emptySet()

        factory.update()
        verifyEnemyCreated(group1.type, positions[0])
        reset(eventManager, spriteContainer)

        clock.tick(1)
        factory.update()
        verifyEnemyCreated(group2.type, positions[1])
        reset(eventManager, spriteContainer)

        clock.tick(1)
        factory.update()
        verifyEnemyCreated(group2.type, positions[2])
        reset(eventManager, spriteContainer)

        clock.tick(1)
        factory.update()
        verifyEnemyCreated(group3.type, positions[0])
        reset(eventManager, spriteContainer)

        clock.tick(1)
        factory.update()
        verify(spriteContainer, never()).addSprite(any())
    }

    @Test
    fun `should create enemy`() {
        val position = Point(1, 2)
        val factory = createFactory(
            listOf(position),
            listOf(EnemyGroupConfig(Tank.EnemyType.BASIC, 4))
        )

        assertEquals(0, factory.enemyCount)

        factory.update()
        verifyEnemyCreated(Tank.EnemyType.BASIC, position)

        assertEquals(1, factory.enemyCount)
    }

    @Test
    fun `should create flashing enemies in order`() {
        val position = Point()
        val factory = createFactory(
            listOf(position),
            listOf(EnemyGroupConfig(Tank.EnemyType.BASIC, 7)),
            1
        )
        factory.flashingTanks = setOf(3, 5, 6)
        factory.enemyCountLimit = 7

        factory.update()
        verifyEnemyCreated(Tank.EnemyType.BASIC, position)
        reset(eventManager)

        clock.tick(1)
        factory.update()
        verifyEnemyCreated(Tank.EnemyType.BASIC, position)
        reset(eventManager)

        clock.tick(1)
        factory.update()
        verifyEnemyCreated(Tank.EnemyType.BASIC, position, true)
        reset(eventManager)

        clock.tick(1)
        factory.update()
        verifyEnemyCreated(Tank.EnemyType.BASIC, position)
        reset(eventManager)

        clock.tick(1)
        factory.update()
        verifyEnemyCreated(Tank.EnemyType.BASIC, position, true)
        reset(eventManager)

        clock.tick(1)
        factory.update()
        verifyEnemyCreated(Tank.EnemyType.BASIC, position, true)
        reset(eventManager)

        clock.tick(1)
        factory.update()
        verifyEnemyCreated(Tank.EnemyType.BASIC, position)
    }

    @Test
    fun `should track destroyed enemies`() {
        val factory = createFactory(
            listOf(Point(1, 2)),
            listOf(EnemyGroupConfig(Tank.EnemyType.BASIC, 1))
        )

        factory.update()
        assertEquals(1, factory.enemyCount)

        val tank = verifyEnemyCreated()

        val explosion = mockTankExplosion(eventManager, tank = tank)
        factory.notify(TankExplosion.Destroyed(explosion))

        assertEquals(0, factory.enemyCount)
    }

    @Test
    fun `should notify when last enemy is destroyed`() {
        val factory = createFactory(
            listOf(Point()),
            listOf(EnemyGroupConfig(Tank.EnemyType.BASIC, 1))
        )

        factory.update()
        assertEquals(1, factory.enemyCount)
        val tank = verifyEnemyCreated()

        val explosion = mockTankExplosion(eventManager, tank = tank)
        factory.notify(TankExplosion.Destroyed(explosion))

        verify(eventManager).fireEvent(EnemyFactory.LastEnemyDestroyed)
    }

    @Test
    fun `should not notify when destroyed enemy is not the last due to more to create`() {
        val factory = createFactory(
            listOf(Point()),
            listOf(EnemyGroupConfig(Tank.EnemyType.BASIC, 2))
        )

        factory.update()
        val tank = verifyEnemyCreated()

        val explosion = mockTankExplosion(eventManager, tank = tank)
        factory.notify(TankExplosion.Destroyed(explosion))

        verify(eventManager, never()).fireEvent(EnemyFactory.LastEnemyDestroyed)
    }

    @Test
    fun `should not notify when destroyed enemy is not the last due to left on the field`() {
        val factory = createFactory(
            listOf(Point()),
            listOf(EnemyGroupConfig(Tank.EnemyType.BASIC, 2)),
            1
        )

        factory.update()
        val tank = verifyEnemyCreated()

        clock.tick(1)
        factory.update()

        assertEquals(0, factory.enemiesToCreateCount)

        val explosion = mockTankExplosion(eventManager, tank = tank)
        factory.notify(TankExplosion.Destroyed(explosion))

        verify(eventManager, never()).fireEvent(EnemyFactory.LastEnemyDestroyed)
    }

    @Test
    fun `should return enemiesToCreateCount correctly`() {
        val factory = createFactory(
            listOf(Point()),
            listOf(EnemyGroupConfig(Tank.EnemyType.BASIC, 3)),
            1
        )

        assertEquals(3, factory.enemiesToCreateCount)

        factory.update()
        assertEquals(2, factory.enemiesToCreateCount)

        clock.tick(1)
        factory.update()
        assertEquals(1, factory.enemiesToCreateCount)

        clock.tick(1)
        factory.update()
        assertEquals(0, factory.enemiesToCreateCount)
    }

    @Test
    fun `should spawn new tank immediately after tank is destroyed`() {
        val factory = createFactory(
            listOf(Point()),
            listOf(EnemyGroupConfig(Tank.EnemyType.BASIC, 3)),
            10000
        )
        factory.enemyCountLimit = 1

        factory.update()
        val tank = verifyEnemyCreated()
        clearInvocations(eventManager)

        clock.tick(10000)
        factory.update()
        verify(eventManager, never()).fireEvent(isA<EnemyFactory.EnemyCreated>())

        clock.tick(5000)
        factory.update()

        val explosion = mockTankExplosion(eventManager, tank = tank)
        factory.notify(TankExplosion.Destroyed(explosion))

        factory.update()
        verifyEnemyCreated()
    }

    private fun verifyEnemyCreated(
        type: Tank.EnemyType,
        position: Point,
        flashing: Boolean = false
    ): Tank {
        val tank = verifyEnemyCreated()
        assertEquals(type, tank.enemyType)
        assertEquals(position, tank.position)
        assertIs<TankStateAppearing>(tank.state)
        assertFalse(tank.isPlayer)
        assertEquals(flashing, tank.isFlashing)
        return tank
    }

    private fun verifyEnemyCreated(): Tank {
        val captor = argumentCaptor<EnemyFactory.EnemyCreated>()
        verify(eventManager).fireEvent(captor.capture())

        return captor.firstValue.enemy
    }

    private fun createFactory(
        positions: List<Point>,
        enemies: List<EnemyGroupConfig>,
        interval: Int = 3000
    ): EnemyFactory {
        return EnemyFactory(
            eventManager,
            mock(),
            pauseManager,
            spriteContainer,
            positions,
            clock,
            enemies,
            interval
        )
    }
}