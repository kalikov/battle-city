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
        StageScorePointsView(eventManager, 100, score.getTanks(Tank.EnemyType.BASIC), script, clock)
    private val fastTankPoints =
        StageScorePointsView(eventManager, 200, score.getTanks(Tank.EnemyType.FAST), script, clock)
    private val powerTankPoints =
        StageScorePointsView(eventManager, 300, score.getTanks(Tank.EnemyType.POWER), script, clock)
    private val armorTankPoints =
        StageScorePointsView(eventManager, 400, score.getTanks(Tank.EnemyType.ARMOR), script, clock)

    private val enemyImage = imageManager.getImage("tank_enemy")
    private val arrowImage = imageManager.getImage("arrows")

    private var drawTotal = false

    init {
        script.enqueue(Delay(script, 640, clock))
        script.enqueue(Execute { basicTankPoints.show() })
        script.enqueue(basicTankPoints)
        script.enqueue(Execute { fastTankPoints.show() })
        script.enqueue(fastTankPoints)
        script.enqueue(Execute { powerTankPoints.show() })
        script.enqueue(powerTankPoints)
        script.enqueue(Execute { armorTankPoints.show() })
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
        surface.fillText("20000", 153, 31, ARGB.rgb(0xfeac4e), Globals.FONT_REGULAR)

        val stage = "STAGE " + "${stageManager.stageNumber}".padStart(2, ' ')
        surface.fillText(stage, 97, 47, ARGB.WHITE, Globals.FONT_REGULAR)

        surface.draw(26, 56, imageManager.getImage("roman_one")) { dst, src, _, _ ->
            src.and(ARGB.rgb(0xe44437)).over(dst)
        }
        surface.fillText("-PLAYER", 33, 63, ARGB.rgb(0xe44437), Globals.FONT_REGULAR)

        val playerScore = "${stageManager.player.score}".padStart(7, ' ')
        surface.fillText(playerScore, 33, 79, ARGB.rgb(0xfeac4e), Globals.FONT_REGULAR)

        surface.fillText("PTS", 65, 103, ARGB.WHITE, Globals.FONT_REGULAR)
        basicTankPoints.draw(surface, 17, 103)
        surface.draw(121, 93, enemyImage, 0, 0, Globals.UNIT_SIZE, Globals.UNIT_SIZE)
        surface.draw(113, 96, arrowImage, 0, 0, 7, 7)

        surface.fillText("PTS", 65, 127, ARGB.WHITE, Globals.FONT_REGULAR)
        fastTankPoints.draw(surface, 17, 127)
        surface.draw(121, 117, enemyImage, 0, 2 * Globals.UNIT_SIZE, Globals.UNIT_SIZE, Globals.UNIT_SIZE)
        surface.draw(113, 120, arrowImage, 0, 0, 7, 7)

        surface.fillText("PTS", 65, 151, ARGB.WHITE, Globals.FONT_REGULAR)
        powerTankPoints.draw(surface, 17, 151)
        surface.draw(121, 141, enemyImage, 0, 4 * Globals.UNIT_SIZE, Globals.UNIT_SIZE, Globals.UNIT_SIZE)
        surface.draw(113, 144, arrowImage, 0, 0, 7, 7)

        surface.fillText("PTS", 65, 175, ARGB.WHITE, Globals.FONT_REGULAR)
        armorTankPoints.draw(surface, 17, 175)
        surface.draw(121, 165, enemyImage, 0, 6 * Globals.UNIT_SIZE, Globals.UNIT_SIZE, Globals.UNIT_SIZE)
        surface.draw(113, 168, arrowImage, 0, 0, 7, 7)

        surface.fillText("TOTAL", 49, 191, ARGB.WHITE, Globals.FONT_REGULAR)
        surface.fillRect(96, 181, 64, 2, ARGB.WHITE)

        if (drawTotal) {
            surface.fillText("${score.tanksCount}".padStart(2, ' '), 97, 191, ARGB.WHITE, Globals.FONT_REGULAR)
        }
    }

    override fun destroy() {
    }
}