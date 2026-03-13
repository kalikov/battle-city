package com.kalikov.game

import com.kalikov.engine.Event
import com.kalikov.engine.EventManager
import com.kalikov.engine.px
import com.kalikov.util.TestClock
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.ArgumentMatchers.anyInt
import org.mockito.Mockito.times
import org.mockito.kotlin.any
import org.mockito.kotlin.argumentCaptor
import org.mockito.kotlin.doAnswer
import org.mockito.kotlin.mock
import org.mockito.kotlin.never
import org.mockito.kotlin.reset
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertSame

class LevelTest {
    private lateinit var game: BattleCityGame
    private lateinit var eventManager: EventManager
    private lateinit var stageManager: StageManager

    private lateinit var clock: TestClock

    private lateinit var level: Level

    @BeforeEach
    fun beforeEach() {
        clock = TestClock()
        game = mockGame(clock = clock)

        whenever(game.screen.createSurface()).thenReturn(mock())
        whenever(game.screen.createSurface(anyInt().px, anyInt().px)).thenReturn(mock())

        whenever(game.imageManager.getImage(any())).thenReturn(mock())

        eventManager = game.eventManager

        stageManager = game.stageManager
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
        whenever(stageManager.stageMap).thenReturn(stage.map)
        whenever(stageManager.stageEnemies).thenReturn(stage.enemies)
        whenever(stageManager.stageEnemySpawnDelay).thenReturn(stage.enemySpawnDelay)
        whenever(stageManager.players).thenReturn(listOf(player))

        val sceneProvider: SceneProvider = mock()
        whenever(sceneProvider.stageScoreScene).thenReturn(mock())
        level = Level(game, sceneProvider)
    }

    @Test
    fun `should not game over when last enemy destroyed`() {
        var nextSceneCalled = false
        whenever(game.sceneManager.setNextScene(any())).doAnswer { nextSceneCalled = true }

        level.activate()
        level.start()
        level.notify(GameEnemyTanksManager.LastEnemyDestroyed)

        while (!nextSceneCalled) {
            level.update()
            clock.tick(1000)
        }
        verify(game.stageManager, never()).isGameOver = true
    }

    @Test
    fun `should game over when base explodes`() {
        var nextSceneCalled = false
        whenever(game.sceneManager.setNextScene(any())).doAnswer { nextSceneCalled = true }

        level.activate()
        level.start()
        level.notify(BaseExplosion.Destroyed(stubBaseExplosion()))

        while (!nextSceneCalled) {
            level.update()
            clock.tick(1000)
        }
        verify(game.stageManager).isGameOver = true
    }

    @Test
    fun `should game over when base hit after win`() {
        var nextSceneCalled = false
        whenever(game.sceneManager.setNextScene(any())).doAnswer { nextSceneCalled = true }

        level.activate()
        level.start()
        level.notify(GameEnemyTanksManager.LastEnemyDestroyed)
        clock.tick(100)
        level.update()
        level.notify(Base.Hit(mock()))

        while (!nextSceneCalled) {
            level.update()
            clock.tick(1000)
        }
        verify(game.stageManager).isGameOver = true
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
        playerTwo.die()
        playerTwo.die()
        playerTwo.die()
        verify(eventManager).fireEvent(Player.OutOfLives(playerTwo))

        whenever(stageManager.stageMap).thenReturn(stage.map)
        whenever(stageManager.stageEnemies).thenReturn(stage.enemies)
        whenever(stageManager.stageEnemySpawnDelay).thenReturn(stage.enemySpawnDelay)
        whenever(stageManager.players).thenReturn(listOf(playerOne, playerTwo))
        level = Level(game, mock())

        reset(eventManager)
        level.activate()
        level.start()
        level.update()

        val captor = argumentCaptor<Event>()
        verify(eventManager, times(2)).fireEvent(captor.capture())
        val values = captor.allValues
        assertEquals(2, values.size)
        values[0].let {
            assertIs<GamePlayerTanksManager.PlayerTankCreated>(it)
            assertSame(playerOne, it.tank.player)
        }
        assertIs<GameEnemyTanksManager.EnemyCreated>(values[1])
    }
}