package com.kalikov.game

import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.kotlin.doAnswer
import org.mockito.kotlin.isA
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class LevelTest {
    private lateinit var screen: Screen
    private lateinit var eventManager: EventManager
    private lateinit var imageManager: ImageManager
    private lateinit var stageManager: StageManager
    private lateinit var entityFactory: EntityFactory

    private lateinit var clock: TestClock

    private lateinit var level: Level

    @BeforeEach
    fun beforeEach() {
        screen = mock()
        whenever(screen.createSurface()).thenReturn(mock())

        eventManager = mock()
        imageManager = mock()

        stageManager = mock()
        val stage = Stage(
            StageMapConfig(
                emptyList(),
                Point(),
                Point(),
                listOf(Point())
            ),
            1,
            listOf(EnemyGroupConfig(Tank.EnemyType.BASIC, 1))
        )
        val player = Player(eventManager)
        whenever(stageManager.stage).thenReturn(stage)
        whenever(stageManager.player).thenReturn(player)

        entityFactory = mock()

        clock = TestClock()

        level = Level(screen, eventManager, imageManager, stageManager, entityFactory, clock)
    }

    @Test
    fun `should not game over when last enemy destroyed`() {
        var nextSceneCalled = false
        whenever(eventManager.fireEvent(isA<Scene.Start>())).doAnswer { nextSceneCalled = true }

        level.start()
        level.notify(EnemyFactory.LastEnemyDestroyed)

        while (!nextSceneCalled) {
            level.update()
            clock.tick(1000)
        }
        assertFalse(level.gameOver)
    }

    @Test
    fun `should game over when base explodes`() {
        var nextSceneCalled = false
        whenever(eventManager.fireEvent(isA<Scene.Start>())).doAnswer { nextSceneCalled = true }

        level.start()
        level.notify(BaseExplosion.Destroyed(mockBaseExplosion()))

        while (!nextSceneCalled) {
            level.update()
            clock.tick(1000)
        }
        assertTrue(level.gameOver)
    }

    @Test
    fun `should game over when base hit after win`() {
        var nextSceneCalled = false
        whenever(eventManager.fireEvent(isA<Scene.Start>())).doAnswer { nextSceneCalled = true }

        level.start()
        level.notify(EnemyFactory.LastEnemyDestroyed)
        clock.tick(100)
        level.update()
        level.notify(Base.Hit(mockBase()))

        while (!nextSceneCalled) {
            level.update()
            clock.tick(1000)
        }
        assertTrue(level.gameOver)
    }
}