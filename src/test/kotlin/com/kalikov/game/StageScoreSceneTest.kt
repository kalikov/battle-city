package com.kalikov.game

import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.ArgumentMatchers.anyInt
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever
import java.awt.image.BufferedImage

class StageScoreSceneTest {
    private lateinit var fonts: TestFonts
    private lateinit var clock: TestClock
    private lateinit var game: Game
    private lateinit var image: BufferedImage

    @BeforeEach
    fun beforeEach() {
        fonts = TestFonts()
        clock = TestClock()

        game = mockGame(imageManager = TestImageManager(fonts), clock = clock)
        whenever(game.screen.createSurface(px(anyInt()), px(anyInt()))).thenAnswer {
            BufferedImage(it.getArgument(0), it.getArgument(1), BufferedImage.TYPE_INT_ARGB)
        }

        image = BufferedImage(Globals.CANVAS_WIDTH.toInt(), Globals.CANVAS_HEIGHT.toInt(), BufferedImage.TYPE_INT_ARGB)
    }

    @Test
    fun `should draw scene correctly`() {
        val stageManager: StageManager = mock()
        whenever(stageManager.highScore).thenReturn(20000)
        whenever(stageManager.stageNumber).thenReturn(7)

        val score = StageScore()
        score.increment(createTank(EnemyTank.EnemyType.BASIC))
        score.increment(createTank(EnemyTank.EnemyType.FAST))

        val player = Player(game.eventManager, initialScore = 25200)
        whenever(stageManager.players).thenReturn(listOf(player))
        val scene = StageScoreScene(
            game,
            stageManager,
            listOf(score),
            false,
        )

        val stage = Stage(
            StageMapConfig(
                base = TilePoint(),
                playerSpawnPoints = emptyList(),
                enemySpawnPoints = listOf(
                    TilePoint(t(12), t(0)),
                    TilePoint(t(24), t(0)),
                    TilePoint(t(0), t(0))
                ),
            ),
            1,
            emptyList()
        )

        whenever(stageManager.stage).thenReturn(stage)

        while (!scene.isComplete) {
            clock.tick(1)
            scene.update()
        }

        scene.draw(AwtScreenSurface(fonts, image))

        assertImageEquals("stage_score.png", image)
    }

    @Test
    fun `should draw two players scene correctly`() {
        val stageManager: StageManager = mock()
        whenever(stageManager.highScore).thenReturn(20000)
        whenever(stageManager.stageNumber).thenReturn(7)

        val scoreOne = StageScore()
        scoreOne.increment(createTank(EnemyTank.EnemyType.BASIC))
        scoreOne.increment(createTank(EnemyTank.EnemyType.FAST))

        val scoreTwo = StageScore()
        scoreTwo.increment(createTank(EnemyTank.EnemyType.BASIC))
        scoreTwo.increment(createTank(EnemyTank.EnemyType.BASIC))
        scoreTwo.increment(createTank(EnemyTank.EnemyType.BASIC))
        scoreTwo.increment(createTank(EnemyTank.EnemyType.FAST))
        scoreTwo.increment(createTank(EnemyTank.EnemyType.FAST))

        val playerOne = Player(game.eventManager, initialScore = 25200)
        val playerTwo = Player(game.eventManager, initialScore = 5600)
        whenever(stageManager.players).thenReturn(listOf(playerOne, playerTwo))
        val scene = StageScoreScene(
            game,
            stageManager,
            listOf(scoreOne, scoreTwo),
            false,
        )

        val stage = Stage(
            StageMapConfig(
                base = TilePoint(),
                playerSpawnPoints = emptyList(),
                enemySpawnPoints = listOf(
                    TilePoint(t(12), t(0)),
                    TilePoint(t(24), t(0)),
                    TilePoint(t(0), t(0))
                ),
            ),
            1,
            emptyList()
        )

        whenever(stageManager.stage).thenReturn(stage)

        while (!scene.isComplete) {
            clock.tick(1)
            scene.update()
        }

        scene.draw(AwtScreenSurface(fonts, image))

        assertImageEquals("stage_score_two_players.png", image)
    }

    @Test
    fun `should not draw two players bonus`() {
        val stageManager: StageManager = mock()
        whenever(stageManager.highScore).thenReturn(20000)
        whenever(stageManager.stageNumber).thenReturn(7)

        val scoreOne = StageScore()
        scoreOne.increment(createTank(EnemyTank.EnemyType.BASIC))
        scoreOne.increment(createTank(EnemyTank.EnemyType.FAST))
        scoreOne.increment(createTank(EnemyTank.EnemyType.POWER))
        scoreOne.increment(createTank(EnemyTank.EnemyType.POWER))
        scoreOne.increment(createTank(EnemyTank.EnemyType.ARMOR))

        val scoreTwo = StageScore()
        scoreTwo.increment(createTank(EnemyTank.EnemyType.BASIC))
        scoreTwo.increment(createTank(EnemyTank.EnemyType.BASIC))
        scoreTwo.increment(createTank(EnemyTank.EnemyType.BASIC))
        scoreTwo.increment(createTank(EnemyTank.EnemyType.FAST))
        scoreTwo.increment(createTank(EnemyTank.EnemyType.FAST))

        val playerOne = Player(game.eventManager, initialScore = 25200)
        val playerTwo = Player(game.eventManager, initialScore = 5600)
        whenever(stageManager.players).thenReturn(listOf(playerOne, playerTwo))
        val scene = StageScoreScene(
            game,
            stageManager,
            listOf(scoreOne, scoreTwo),
            false,
        )

        val stage = Stage(
            StageMapConfig(
                base = TilePoint(),
                playerSpawnPoints = emptyList(),
                enemySpawnPoints = listOf(
                    TilePoint(t(12), t(0)),
                    TilePoint(t(24), t(0)),
                    TilePoint(t(0), t(0))
                )
            ),
            1,
            emptyList()
        )

        whenever(stageManager.stage).thenReturn(stage)

        while (!scene.isComplete) {
            clock.tick(1)
            scene.update()
        }

        scene.draw(AwtScreenSurface(fonts, image))

        assertImageEquals("stage_score_no_bonus.png", image)
    }

    private fun createTank(type: EnemyTank.EnemyType): EnemyTank {
        return stubEnemyTank(enemyType = type)
    }
}