//package com.kalikov.game
//
//import com.kalikov.engine.EventManager
//import com.kalikov.engine.px
//import com.kalikov.util.TestClock
//import org.junit.jupiter.api.BeforeEach
//import org.junit.jupiter.api.Test
//import org.mockito.kotlin.any
//import org.mockito.kotlin.argumentCaptor
//import org.mockito.kotlin.clearInvocations
//import org.mockito.kotlin.isA
//import org.mockito.kotlin.mock
//import org.mockito.kotlin.never
//import org.mockito.kotlin.reset
//import org.mockito.kotlin.verify
//import org.mockito.kotlin.whenever
//import kotlin.test.assertEquals
//import kotlin.test.assertFalse
//import kotlin.test.assertIs
//import kotlin.test.assertTrue
//
//class EnemyFactoryTest {
//    private lateinit var clock: TestClock
//    private lateinit var eventManager: EventManager
//    private lateinit var game: BattleCityGame
//    private lateinit var pauseManager: PauseManager
//
//    @BeforeEach
//    fun beforeEach() {
//        clock = TestClock()
//        eventManager = mock()
//        game = mockGame(eventManager = eventManager, clock = clock)
//        pauseManager = mock()
//    }
//
//    @Test
//    fun `should subscribe`() {
//        val factory = createFactory(emptyList(), emptyList())
//        verify(eventManager).addSubscriber(factory, arrayOf(TankExplosion.Destroyed::class, Tank.Hit::class))
//    }
//
//    @Test
//    fun `should iterate over positions in a loop`() {
//        val position1 = PixelPoint(0.px, 0.px)
//        val position2 = PixelPoint(10.px, 20.px)
//        val position3 = PixelPoint(40.px, 100.px)
//        val factory = createFactory(
//            listOf(position1, position2, position3),
//            emptyList()
//        )
////        assertEquals(position1, factory.nextPosition())
////        assertEquals(position2, factory.nextPosition())
////        assertEquals(position3, factory.nextPosition())
////        assertEquals(position1, factory.nextPosition())
//    }
//
//    @Test
//    fun `should iterate over enemies in a loop`() {
//        val group1 = EnemyGroupConfig(EnemyTank.EnemyType.BASIC, 1)
//        val group2 = EnemyGroupConfig(EnemyTank.EnemyType.FAST, 2)
//        val group3 = EnemyGroupConfig(EnemyTank.EnemyType.BASIC, 1)
//        val factory = createFactory(
//            emptyList(),
//            listOf(group1, group2, group3)
//        )
//        assertEquals(group1.type, factory.nextEnemy())
//        assertEquals(group2.type, factory.nextEnemy())
//        assertEquals(group2.type, factory.nextEnemy())
//        assertEquals(group3.type, factory.nextEnemy())
//        assertEquals(group1.type, factory.nextEnemy())
//    }
//
//    @Test
//    fun `should spawn no more than limit respecting interval`() {
//        val factory = createFactory(
//            listOf(PixelPoint()),
//            listOf(EnemyGroupConfig(EnemyTank.EnemyType.BASIC, 4)),
//            4,
//        )
//        factory.enemyCountLimit = 2
//
//        factory.update()
//        assertEquals(1, factory.enemyCount)
//        assertFalse(factory.enemyCountLimitReached)
//
//        clock.tick(1)
//        factory.update()
//        assertEquals(1, factory.enemyCount)
//        assertFalse(factory.enemyCountLimitReached)
//
//        clock.tick(1)
//        factory.update()
//        assertEquals(1, factory.enemyCount)
//        assertFalse(factory.enemyCountLimitReached)
//
//        clock.tick(1)
//        factory.update()
//        assertEquals(1, factory.enemyCount)
//        assertFalse(factory.enemyCountLimitReached)
//
//        clock.tick(1)
//        factory.update()
//        assertEquals(2, factory.enemyCount)
//        assertTrue(factory.enemyCountLimitReached)
//
//        clock.tick(1)
//        factory.update()
//        assertEquals(2, factory.enemyCount)
//        assertTrue(factory.enemyCountLimitReached)
//
//        clock.tick(1)
//        factory.update()
//        assertEquals(2, factory.enemyCount)
//        assertTrue(factory.enemyCountLimitReached)
//
//        clock.tick(1)
//        factory.update()
//        assertEquals(2, factory.enemyCount)
//        assertTrue(factory.enemyCountLimitReached)
//
//        clock.tick(1)
//        factory.update()
//        assertEquals(2, factory.enemyCount)
//        assertTrue(factory.enemyCountLimitReached)
//    }
//
//    @Test
//    fun `should not spawn when paused`() {
//        whenever(pauseManager.isPaused).thenReturn(true)
//
//        val factory = createFactory(
//            listOf(PixelPoint()),
//            listOf(EnemyGroupConfig(EnemyTank.EnemyType.BASIC, 4)),
//            1
//        )
//
//        factory.update()
//        assertEquals(0, factory.enemyCount)
//
//        clock.tick(1)
//        factory.update()
//        assertEquals(0, factory.enemyCount)
//
//        clock.tick(1)
//        factory.update()
//        assertEquals(0, factory.enemyCount)
//    }
//
//    @Test
//    fun `should stop creation when no more enemies left`() {
//        val positions = listOf(PixelPoint(0.px, 0.px), PixelPoint(10.px, 20.px), PixelPoint(40.px, 100.px))
//        val group1 = EnemyGroupConfig(EnemyTank.EnemyType.BASIC, 1)
//        val group2 = EnemyGroupConfig(EnemyTank.EnemyType.FAST, 2)
//        val group3 = EnemyGroupConfig(EnemyTank.EnemyType.BASIC, 1)
//        val factory = createFactory(
//            positions,
//            listOf(group1, group2, group3),
//            1
//        )
//        factory.flashingIndices = emptySet()
//
//        factory.update()
//        verifyEnemyCreated(group1.type, positions[0])
//        reset(eventManager, spriteContainer)
//
//        clock.tick(1)
//        factory.update()
//        verifyEnemyCreated(group2.type, positions[1])
//        reset(eventManager, spriteContainer)
//
//        clock.tick(1)
//        factory.update()
//        verifyEnemyCreated(group2.type, positions[2])
//        reset(eventManager, spriteContainer)
//
//        clock.tick(1)
//        factory.update()
//        verifyEnemyCreated(group3.type, positions[0])
//        reset(eventManager, spriteContainer)
//
//        clock.tick(1)
//        factory.update()
//        verify(spriteContainer, never()).addSprite(any())
//    }
//
//    @Test
//    fun `should create enemy`() {
//        val position = PixelPoint(1.px, 2.px)
//        val factory = createFactory(
//            listOf(position),
//            listOf(EnemyGroupConfig(EnemyTank.EnemyType.BASIC, 4))
//        )
//
//        assertEquals(0, factory.enemyCount)
//
//        factory.update()
//        verifyEnemyCreated(EnemyTank.EnemyType.BASIC, position)
//
//        assertEquals(1, factory.enemyCount)
//    }
//
//    @Test
//    fun `should create flashing enemies in order`() {
//        val position = PixelPoint()
//        val factory = createFactory(
//            listOf(position),
//            listOf(EnemyGroupConfig(EnemyTank.EnemyType.BASIC, 7)),
//            1
//        )
//        factory.flashingIndices = setOf(3, 5, 6)
//        factory.enemyCountLimit = 7
//
//        factory.update()
//        verifyEnemyCreated(EnemyTank.EnemyType.BASIC, position)
//        reset(eventManager)
//
//        clock.tick(1)
//        factory.update()
//        verifyEnemyCreated(EnemyTank.EnemyType.BASIC, position)
//        reset(eventManager)
//
//        clock.tick(1)
//        factory.update()
//        verifyEnemyCreated(EnemyTank.EnemyType.BASIC, position, true)
//        reset(eventManager)
//
//        clock.tick(1)
//        factory.update()
//        verifyEnemyCreated(EnemyTank.EnemyType.BASIC, position)
//        reset(eventManager)
//
//        clock.tick(1)
//        factory.update()
//        verifyEnemyCreated(EnemyTank.EnemyType.BASIC, position, true)
//        reset(eventManager)
//
//        clock.tick(1)
//        factory.update()
//        verifyEnemyCreated(EnemyTank.EnemyType.BASIC, position, true)
//        reset(eventManager)
//
//        clock.tick(1)
//        factory.update()
//        verifyEnemyCreated(EnemyTank.EnemyType.BASIC, position)
//    }
//
//    @Test
//    fun `should track destroyed enemies`() {
//        val factory = createFactory(
//            listOf(PixelPoint(1.px, 2.px)),
//            listOf(EnemyGroupConfig(EnemyTank.EnemyType.BASIC, 1))
//        )
//
//        factory.update()
//        assertEquals(1, factory.enemyCount)
//
//        val tank = verifyEnemyCreated()
//
//        val explosion = stubTankExplosion(game, tank = tank)
//        factory.notify(TankExplosion.Destroyed(explosion))
//
//        assertEquals(0, factory.enemyCount)
//    }
//
//    @Test
//    fun `should notify when last enemy is destroyed`() {
//        val factory = createFactory(
//            listOf(PixelPoint()),
//            listOf(EnemyGroupConfig(EnemyTank.EnemyType.BASIC, 1))
//        )
//
//        factory.update()
//        assertEquals(1, factory.enemyCount)
//        val tank = verifyEnemyCreated()
//
//        val explosion = stubTankExplosion(game, tank = tank)
//        factory.notify(TankExplosion.Destroyed(explosion))
//
//        verify(eventManager).fireEvent(EnemyTanksManager.LastEnemyDestroyed)
//    }
//
//    @Test
//    fun `should not notify when destroyed enemy is not the last due to more to create`() {
//        val factory = createFactory(
//            listOf(PixelPoint()),
//            listOf(EnemyGroupConfig(EnemyTank.EnemyType.BASIC, 2))
//        )
//
//        factory.update()
//        val tank = verifyEnemyCreated()
//
//        val explosion = stubTankExplosion(game, tank = tank)
//        factory.notify(TankExplosion.Destroyed(explosion))
//
//        verify(eventManager, never()).fireEvent(EnemyTanksManager.LastEnemyDestroyed)
//    }
//
//    @Test
//    fun `should not notify when destroyed enemy is not the last due to left on the field`() {
//        val factory = createFactory(
//            listOf(PixelPoint()),
//            listOf(EnemyGroupConfig(EnemyTank.EnemyType.BASIC, 2)),
//            1
//        )
//
//        factory.update()
//        val tank = verifyEnemyCreated()
//
//        clock.tick(1)
//        factory.update()
//
//        assertEquals(0, factory.enemiesToCreateCount)
//
//        val explosion = stubTankExplosion(game, tank = tank)
//        factory.notify(TankExplosion.Destroyed(explosion))
//
//        verify(eventManager, never()).fireEvent(EnemyTanksManager.LastEnemyDestroyed)
//    }
//
//    @Test
//    fun `should return enemiesToCreateCount correctly`() {
//        val factory = createFactory(
//            listOf(PixelPoint()),
//            listOf(EnemyGroupConfig(EnemyTank.EnemyType.BASIC, 3)),
//            1
//        )
//
//        assertEquals(3, factory.enemiesToCreateCount)
//
//        factory.update()
//        assertEquals(2, factory.enemiesToCreateCount)
//
//        clock.tick(1)
//        factory.update()
//        assertEquals(1, factory.enemiesToCreateCount)
//
//        clock.tick(1)
//        factory.update()
//        assertEquals(0, factory.enemiesToCreateCount)
//    }
//
//    @Test
//    fun `should spawn new tank immediately after tank is destroyed`() {
//        val factory = createFactory(
//            listOf(PixelPoint()),
//            listOf(EnemyGroupConfig(EnemyTank.EnemyType.BASIC, 3)),
//            10000
//        )
//        factory.enemyCountLimit = 1
//
//        factory.update()
//        val tank = verifyEnemyCreated()
//        clearInvocations(eventManager)
//
//        clock.tick(10000)
//        factory.update()
//        verify(eventManager, never()).fireEvent(isA<EnemyTanksManager.EnemyCreated>())
//
//        clock.tick(5000)
//        factory.update()
//
//        val explosion = stubTankExplosion(game, tank = tank)
//        factory.notify(TankExplosion.Destroyed(explosion))
//
//        factory.update()
//        verifyEnemyCreated()
//    }
//
//    private fun verifyEnemyCreated(
//        type: EnemyTank.EnemyType,
//        position: PixelPoint,
//        flashing: Boolean = false
//    ): Tank {
//        val event = captureEnemyCreated()
//        val tank = event.enemy
//        assertEquals(type, tank.enemyType)
//        assertEquals(position.x, tank.x)
//        assertEquals(position.y, tank.y)
//        assertIs<TankStateAppearing>(tank.state)
//        assertEquals(flashing, event.isFlashing)
//        return tank
//    }
//
//    private fun verifyEnemyCreated(): Tank {
//        return captureEnemyCreated().enemy
//    }
//
//    private fun captureEnemyCreated(): GameEnemyTanksManager.EnemyCreated {
//        val captor = argumentCaptor<EnemyTanksManager.EnemyCreated>()
//        verify(eventManager).fireEvent(captor.capture())
//
//        return captor.firstValue
//    }
//
//    private fun createFactory(
//        positions: List<PixelPoint>,
//        enemies: List<EnemyGroupConfig>,
//        interval: Int = 3000
//    ): EnemyTanksManager {
//        return GameEnemyTanksManager(
//            game,
//            pauseManager,
////            spriteContainer,
////            positions,
////            enemies,
////            interval
//        )
//    }
//}