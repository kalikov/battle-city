package com.kalikov.game

import com.kalikov.engine.AwtScreenSurface
import com.kalikov.engine.px
import com.kalikov.util.TestClock
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.ArgumentMatchers.anyInt
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever
import java.awt.image.BufferedImage

class StageScoreSceneTest {
    private lateinit var fonts: TestFonts
    private lateinit var clock: TestClock
    private lateinit var game: BattleCityGame
    private lateinit var image: BufferedImage

    @BeforeEach
    fun beforeEach() {
        fonts = TestFonts()
        clock = TestClock()

        game = mockGame(imageManager = TestImageManager(fonts), clock = clock)
        whenever(game.screen.createSurface(anyInt().px, anyInt().px)).thenAnswer {
            BufferedImage(it.getArgument(0), it.getArgument(1), BufferedImage.TYPE_INT_ARGB)
        }

        image = BufferedImage(Globals.CANVAS_WIDTH.toInt(), Globals.CANVAS_HEIGHT.toInt(), BufferedImage.TYPE_INT_ARGB)
    }

    @Test
    fun `should draw scene correctly`() {
        whenever(game.stageManager.highScore).thenReturn(20000)
        whenever(game.stageManager.stageNumber).thenReturn(7)

        val player = Player(game, initialScore = 25200)
        whenever(game.stageManager.players).thenReturn(listOf(player))
        player.stageScore.increment(stubEnemyTank(game, enemyType = EnemyTank.EnemyType.BASIC))
        player.stageScore.increment(stubEnemyTank(game, enemyType = EnemyTank.EnemyType.FAST))
        val scene = StageScoreScene(
            game,
            mock(),
        )
        scene.activate()

        while (!scene.isComplete) {
            clock.tick(1)
            scene.update()
        }

        scene.draw(AwtScreenSurface(fonts, image))

        assertImageEquals("stage_score.png", image)
    }

    @Test
    fun `should draw two players scene correctly`() {
        whenever(game.stageManager.highScore).thenReturn(20000)
        whenever(game.stageManager.stageNumber).thenReturn(7)

        val playerOne = Player(game, initialScore = 25200, index = 0)
        playerOne.stageScore.increment(createTank(EnemyTank.EnemyType.BASIC))
        playerOne.stageScore.increment(createTank(EnemyTank.EnemyType.FAST))

        val playerTwo = Player(game, initialScore = 4600, index = 1)
        playerTwo.stageScore.increment(createTank(EnemyTank.EnemyType.BASIC))
        playerTwo.stageScore.increment(createTank(EnemyTank.EnemyType.BASIC))
        playerTwo.stageScore.increment(createTank(EnemyTank.EnemyType.BASIC))
        playerTwo.stageScore.increment(createTank(EnemyTank.EnemyType.FAST))
        playerTwo.stageScore.increment(createTank(EnemyTank.EnemyType.FAST))

        whenever(game.stageManager.players).thenReturn(listOf(playerOne, playerTwo))
        val scene = StageScoreScene(
            game,
            mock(),
        )
        scene.activate()

        while (!scene.isComplete) {
            clock.tick(1)
            scene.update()
        }

        scene.draw(AwtScreenSurface(fonts, image))

        assertImageEquals("stage_score_two_players.png", image)
    }

    @Test
    fun `should not draw two players bonus`() {
        whenever(game.stageManager.highScore).thenReturn(20000)
        whenever(game.stageManager.stageNumber).thenReturn(7)

        val playerOne = Player(game, initialScore = 25200, index = 0)
        playerOne.stageScore.increment(createTank(EnemyTank.EnemyType.BASIC))
        playerOne.stageScore.increment(createTank(EnemyTank.EnemyType.FAST))
        playerOne.stageScore.increment(createTank(EnemyTank.EnemyType.POWER))
        playerOne.stageScore.increment(createTank(EnemyTank.EnemyType.POWER))
        playerOne.stageScore.increment(createTank(EnemyTank.EnemyType.ARMOR))

        val playerTwo = Player(game, initialScore = 5600, index = 1)
        playerTwo.stageScore.increment(createTank(EnemyTank.EnemyType.BASIC))
        playerTwo.stageScore.increment(createTank(EnemyTank.EnemyType.BASIC))
        playerTwo.stageScore.increment(createTank(EnemyTank.EnemyType.BASIC))
        playerTwo.stageScore.increment(createTank(EnemyTank.EnemyType.FAST))
        playerTwo.stageScore.increment(createTank(EnemyTank.EnemyType.FAST))

        whenever(game.stageManager.players).thenReturn(listOf(playerOne, playerTwo))
        val scene = StageScoreScene(
            game,
            mock(),
        )
        scene.activate()

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