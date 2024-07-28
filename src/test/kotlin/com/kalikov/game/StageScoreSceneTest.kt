package com.kalikov.game

import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever
import java.awt.image.BufferedImage

class StageScoreSceneTest {
    private lateinit var fonts: TestFonts
    private lateinit var clock: TestClock

    @BeforeEach
    fun beforeEach() {
        fonts = TestFonts()
        clock = TestClock()
    }

    @Test
    fun `should draw scene correctly`() {
        val screen: Screen = mock()
        val eventManager: EventManager = mock()
        val imageManager = TestImageManager(fonts)
        val stageManager: StageManager = mock()
        whenever(stageManager.highScore).thenReturn(20000)
        whenever(stageManager.stageNumber).thenReturn(7)

        val score = StageScore()
        score.increment(createTank(EnemyTank.EnemyType.BASIC))
        score.increment(createTank(EnemyTank.EnemyType.FAST))

        val player = Player(eventManager, initialScore = 25200)
        whenever(stageManager.players).thenReturn(listOf(player))
        val scene =
            StageScoreScene(screen, eventManager, imageManager, stageManager, mock(), listOf(score), false, clock)

        val stage = Stage(
            StageMapConfig(
                emptyList(),
                Point(0, 0),
                emptyList(),
                listOf(Point(12, 0), Point(24, 0), Point(0, 0))
            ),
            1,
            emptyList()
        )

        whenever(stageManager.stage).thenReturn(stage)

        while (!scene.isComplete) {
            clock.tick(1)
            scene.update()
        }

        val image = BufferedImage(Globals.CANVAS_WIDTH, Globals.CANVAS_HEIGHT, BufferedImage.TYPE_INT_ARGB)
        scene.draw(AwtScreenSurface(fonts, image))

        assertImageEquals("stage_score.png", image)
    }

    @Test
    fun `should draw two players scene correctly`() {
        val screen: Screen = mock()
        val eventManager: EventManager = mock()
        val imageManager = TestImageManager(fonts)
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

        val playerOne = Player(eventManager, initialScore = 25200)
        val playerTwo = Player(eventManager, initialScore = 5600)
        whenever(stageManager.players).thenReturn(listOf(playerOne, playerTwo))
        val scene = StageScoreScene(
            screen,
            eventManager,
            imageManager,
            stageManager,
            mock(),
            listOf(scoreOne, scoreTwo),
            false,
            clock
        )

        val stage = Stage(
            StageMapConfig(
                emptyList(),
                Point(0, 0),
                emptyList(),
                listOf(Point(12, 0), Point(24, 0), Point(0, 0))
            ),
            1,
            emptyList()
        )

        whenever(stageManager.stage).thenReturn(stage)

        while (!scene.isComplete) {
            clock.tick(1)
            scene.update()
        }

        val image = BufferedImage(Globals.CANVAS_WIDTH, Globals.CANVAS_HEIGHT, BufferedImage.TYPE_INT_ARGB)
        scene.draw(AwtScreenSurface(fonts, image))

        assertImageEquals("stage_score_two_players.png", image)
    }

    private fun createTank(type: EnemyTank.EnemyType): EnemyTank {
        return mockEnemyTank(enemyType = type)
    }
}