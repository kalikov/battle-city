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
        whenever(stageManager.stageNumber).thenReturn(7)

        val score = StageScore()
        score.increment(createTank(Tank.EnemyType.BASIC))
        score.increment(createTank(Tank.EnemyType.FAST))

        val player = Player(eventManager)
        player.score = 25200
        whenever(stageManager.player).thenReturn(player)
        val scene = StageScoreScene(screen, eventManager, imageManager, stageManager, mock(), score, false, clock)

        val stage = Stage(
            StageMapConfig(
                emptyList(),
                Point(0, 0),
                Point(8, 24),
                listOf(Point(12, 0), Point(24, 0), Point(0, 0))
            ),
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

    private fun createTank(type: Tank.EnemyType): Tank {
        val tank = mockTank()
        tank.enemyType = type
        tank.value = type.score
        return tank
    }
}