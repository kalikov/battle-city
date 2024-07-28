package com.kalikov.game

import java.time.Clock

class StageScoreScene(
    private val screen: Screen,
    private val eventManager: EventManager,
    private val imageManager: ImageManager,
    private val stageManager: StageManager,
    private val entityFactory: EntityFactory,
    private val scores: List<StageScore>,
    private val gameOver: Boolean,
    clock: Clock
) : Scene {
    val isComplete get() = script.isEmpty

    private val script = Script()

    private val basicTankPoints =
        StageScorePointsView(eventManager, imageManager, EnemyTank.EnemyType.BASIC, scores, script, clock)
    private val fastTankPoints =
        StageScorePointsView(eventManager, imageManager, EnemyTank.EnemyType.FAST, scores, script, clock)
    private val powerTankPoints =
        StageScorePointsView(eventManager, imageManager, EnemyTank.EnemyType.POWER, scores, script, clock)
    private val armorTankPoints =
        StageScorePointsView(eventManager, imageManager, EnemyTank.EnemyType.ARMOR, scores, script, clock)

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

        var y = 2 * Globals.UNIT_SIZE
        surface.fillText("HI-SCORE", 65, y - 1, ARGB.rgb(0xe44437), Globals.FONT_REGULAR)
        surface.fillText(
            "${stageManager.highScore}".padStart(7, ' '),
            137,
            y - 1,
            ARGB.rgb(0xfeac4e),
            Globals.FONT_REGULAR
        )

        y += Globals.UNIT_SIZE
        val stage = "STAGE " + "${stageManager.stageNumber}".padStart(2, ' ')
        surface.fillText(stage, 97, y - 1, ARGB.WHITE, Globals.FONT_REGULAR)

        y += Globals.UNIT_SIZE
        surface.draw(26, y - Globals.TILE_SIZE, imageManager.getImage("roman_one")) { dst, src, _, _ ->
            src.and(ARGB.rgb(0xe44437)).over(dst)
        }
        if (stageManager.players.size > 1) {
            surface.draw(170, y - Globals.TILE_SIZE, imageManager.getImage("roman_two")) { dst, src, _, _ ->
                src.and(ARGB.rgb(0xe44437)).over(dst)
            }
        }
        surface.fillText("-PLAYER", 33, y - 1, ARGB.rgb(0xe44437), Globals.FONT_REGULAR)
        if (stageManager.players.size > 1) {
            surface.fillText("-PLAYER", 177, y - 1, ARGB.rgb(0xe44437), Globals.FONT_REGULAR)

        }

        y += Globals.UNIT_SIZE
        val playerOneScore = "${stageManager.players[0].score}".padStart(7, ' ')
        surface.fillText(playerOneScore, 33, y - 1, ARGB.rgb(0xfeac4e), Globals.FONT_REGULAR)

        if (stageManager.players.size > 1) {
            val playerTwoScore = "${stageManager.players[1].score}".padStart(7, ' ')
            surface.fillText(playerTwoScore, 177, y - 1, ARGB.rgb(0xfeac4e), Globals.FONT_REGULAR)
        }

        y += 2 * Globals.UNIT_SIZE
        basicTankPoints.draw(surface, 17, y - Globals.TILE_SIZE - 1)
        y += Globals.UNIT_SIZE
        fastTankPoints.draw(surface, 17, y - 1)
        y += 2 * Globals.UNIT_SIZE
        powerTankPoints.draw(surface, 17, y - Globals.TILE_SIZE - 1)
        y += Globals.UNIT_SIZE
        armorTankPoints.draw(surface, 17, y - 1)

        y += Globals.UNIT_SIZE
        surface.fillText("TOTAL", 49, y - 1, ARGB.WHITE, Globals.FONT_REGULAR)
        surface.fillRect(96, y - 3 - Globals.TILE_SIZE, 64, 2, ARGB.WHITE)

        if (drawTotal) {
            surface.fillText("${scores[0].tanksCount}".padStart(2, ' '), 97, y - 1, ARGB.WHITE, Globals.FONT_REGULAR)
            if (scores.size > 1) {
                surface.fillText("${scores[1].tanksCount}".padStart(2, ' '), 145, y - 1, ARGB.WHITE, Globals.FONT_REGULAR)
            }
        }
    }

    override fun destroy() {
    }
}