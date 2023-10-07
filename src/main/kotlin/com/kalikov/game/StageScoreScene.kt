package com.kalikov.game

import java.time.Clock

class StageScoreScene(
    private val screen: Screen,
    private val eventManager: EventManager,
    private val imageManager: ImageManager,
    private val stageManager: StageManager,
    private val entityFactory: EntityFactory,
    private val score: StageScore,
    private val gameOver: Boolean,
    clock: Clock
) : Scene {
    val isComplete get() = script.isEmpty

    private val script = Script()

    private val basicTankPoints =
        StageScorePointsView(eventManager, imageManager, Tank.EnemyType.BASIC, score, script, clock)
    private val fastTankPoints =
        StageScorePointsView(eventManager, imageManager, Tank.EnemyType.FAST, score, script, clock)
    private val powerTankPoints =
        StageScorePointsView(eventManager, imageManager, Tank.EnemyType.POWER, score, script, clock)
    private val armorTankPoints =
        StageScorePointsView(eventManager, imageManager, Tank.EnemyType.ARMOR, score, script, clock)

    private var drawTotal = false

    init {
        script.enqueue(Delay(script, 640, clock))
        script.enqueue(basicTankPoints)
        script.enqueue(fastTankPoints)
        script.enqueue(powerTankPoints)
        script.enqueue(armorTankPoints)
        script.enqueue(Execute { drawTotal = true })
        script.enqueue(Delay(script, 2000, clock))
        script.enqueue(Execute {
            if (gameOver) {
                stageManager.reset()
                eventManager.fireEvent(Scene.Start {
                    GameOverScene(screen, eventManager, imageManager, stageManager, entityFactory, clock)
                })
            } else {
                stageManager.next()
                stageManager.resetConstruction()
                eventManager.fireEvent(Scene.Start {
                    StageScene(screen, eventManager, imageManager, stageManager, entityFactory, clock)
                })
            }
        })
    }

    override fun update() {
        script.update()
    }

    override fun draw(surface: ScreenSurface) {
        surface.clear(ARGB.BLACK)

        surface.fillText("HI-SCORE", 65, 31, ARGB.rgb(0xe44437), Globals.FONT_REGULAR)
        surface.fillText(
            "${stageManager.highScore}".padStart(7, ' '),
            137,
            31,
            ARGB.rgb(0xfeac4e),
            Globals.FONT_REGULAR
        )

        val stage = "STAGE " + "${stageManager.stageNumber}".padStart(2, ' ')
        surface.fillText(stage, 97, 47, ARGB.WHITE, Globals.FONT_REGULAR)

        surface.draw(26, 56, imageManager.getImage("roman_one")) { dst, src, _, _ ->
            src.and(ARGB.rgb(0xe44437)).over(dst)
        }
        surface.fillText("-PLAYER", 33, 63, ARGB.rgb(0xe44437), Globals.FONT_REGULAR)

        val playerScore = "${stageManager.player.score}".padStart(7, ' ')
        surface.fillText(playerScore, 33, 79, ARGB.rgb(0xfeac4e), Globals.FONT_REGULAR)

        basicTankPoints.draw(surface, 17, 103)
        fastTankPoints.draw(surface, 17, 127)
        powerTankPoints.draw(surface, 17, 151)
        armorTankPoints.draw(surface, 17, 175)

        surface.fillText("TOTAL", 49, 191, ARGB.WHITE, Globals.FONT_REGULAR)
        surface.fillRect(96, 181, 64, 2, ARGB.WHITE)

        if (drawTotal) {
            surface.fillText("${score.tanksCount}".padStart(2, ' '), 97, 191, ARGB.WHITE, Globals.FONT_REGULAR)
        }
    }

    override fun destroy() {
    }
}