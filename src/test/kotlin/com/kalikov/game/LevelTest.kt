package com.kalikov.game

import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.ArgumentMatchers.anyInt
import org.mockito.kotlin.any
import org.mockito.kotlin.argumentCaptor
import org.mockito.kotlin.doAnswer
import org.mockito.kotlin.isA
import org.mockito.kotlin.mock
import org.mockito.kotlin.reset
import org.mockito.kotlin.times
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertIs
import kotlin.test.assertSame
import kotlin.test.assertTrue

class LevelTest {
    private lateinit var game: Game
    private lateinit var eventManager: EventManager
    private lateinit var stageManager: StageManager

    private lateinit var clock: TestClock

    private lateinit var level: Level

    @BeforeEach
    fun beforeEach() {
        clock = TestClock()
        game = mockGame(clock = clock)

        whenever(game.screen.createSurface()).thenReturn(mock())
        whenever(game.screen.createSurface(px(anyInt()), px(anyInt()))).thenReturn(mock())

        whenever(game.imageManager.getImage(any())).thenReturn(mock())

        eventManager = game.eventManager

        stageManager = mock()
        val stage = Stage(
            StageMapConfig(
                base = TilePoint(),
                playerSpawnPoints = listOf(TilePoint()),
                enemySpawnPoints = listOf(TilePoint()),
            ),
            1,
            listOf(EnemyGroupConfig(EnemyTank.EnemyType.BASIC, 1))
        )
        val player = Player(game)
        whenever(stageManager.stage).thenReturn(stage)
        whenever(stageManager.players).thenReturn(listOf(player))

        level = Level(game, stageManager)
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
        level.notify(BaseExplosion.Destroyed(stubBaseExplosion()))

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
        level.notify(Base.Hit(mock()))

        while (!nextSceneCalled) {
            level.update()
            clock.tick(1000)
        }
        assertTrue(level.gameOver)
    }

    @Test
    fun `should not create players with zero lives`() {
        val stage = Stage(
            StageMapConfig(
                base = TilePoint(),
                playerSpawnPoints = listOf(TilePoint(), TilePoint()),
                enemySpawnPoints = listOf(TilePoint()),
            ),
            1,
            listOf(EnemyGroupConfig(EnemyTank.EnemyType.BASIC, 1))
        )
        val playerOne = Player(game)

        val playerTwo = Player(game)
        playerTwo.notify(PlayerTank.PlayerDestroyed(stubPlayerTank(player = playerTwo)))
        playerTwo.notify(PlayerTank.PlayerDestroyed(stubPlayerTank(player = playerTwo)))
        playerTwo.notify(PlayerTank.PlayerDestroyed(stubPlayerTank(player = playerTwo)))
        verify(eventManager).fireEvent(Player.OutOfLives(playerTwo))

        whenever(stageManager.stage).thenReturn(stage)
        whenever(stageManager.players).thenReturn(listOf(playerOne, playerTwo))
        level = Level(game, stageManager)

        reset(eventManager)
        level.start()

        val captor = argumentCaptor<Event>()
        verify(eventManager, times(2)).fireEvent(captor.capture())
        val values = captor.allValues
        assertEquals(2, values.size)
        values[0].let {
            assertIs<SpriteContainer.Added>(it)
            assertIs<Tank>(it.sprite)
        }
        values[1].let {
            assertIs<PlayerTankFactory.PlayerTankCreated>(it)
            assertSame(playerOne, it.tank.player)
        }
    }
}