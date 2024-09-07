package com.kalikov.game

import java.time.Clock

class StageScoreScene(
    private val game: Game,
    private val stageManager: StageManager,
    private val entityFactory: EntityFactory,
    private val scores: List<StageScore>,
    private val gameOver: Boolean,
    clock: Clock
) : Scene {
    private companion object {
        const val BONUS_SCORE = 1000
    }

    val isComplete get() = script.isEmpty

    private val script = Script()

    private val basicTankPoints =
        StageScorePointsView(game.eventManager, game.imageManager, EnemyTank.EnemyType.BASIC, scores, script, clock)
    private val fastTankPoints =
        StageScorePointsView(game.eventManager, game.imageManager, EnemyTank.EnemyType.FAST, scores, script, clock)
    private val powerTankPoints =
        StageScorePointsView(game.eventManager, game.imageManager, EnemyTank.EnemyType.POWER, scores, script, clock)
    private val armorTankPoints =
        StageScorePointsView(game.eventManager, game.imageManager, EnemyTank.EnemyType.ARMOR, scores, script, clock)

    private var drawTotal = false
    private var drawBonusIndex = -1

    init {
        script.enqueue(Delay(script, 640, clock))
        script.enqueue(basicTankPoints)
        script.enqueue(fastTankPoints)
        script.enqueue(powerTankPoints)
        script.enqueue(armorTankPoints)
        script.enqueue(Execute { drawTotal = true })
        if (!gameOver && scores.size > 1) {
            var maxTanksCount = 0
            var maxTanksIndex = 0
            scores.forEachIndexed { index, it ->
                if (maxTanksCount < it.tanksCount) {
                    maxTanksCount = it.tanksCount
                    maxTanksIndex = index
                } else if (maxTanksCount == it.tanksCount) {
                    maxTanksIndex = -1
                }
            }
            if (maxTanksIndex >= 0) {
                script.enqueue(Delay(script, 320, clock))
                script.enqueue(Execute {
                    drawBonusIndex = maxTanksIndex
                    game.eventManager.fireEvent(Player.Score(stageManager.players[maxTanksIndex], BONUS_SCORE))
                })
            }
        }
        script.enqueue(Delay(script, 2000, clock))
        script.enqueue(Execute {
            if (gameOver) {
                game.eventManager.fireEvent(Scene.Start {
                    GameOverScene(game, stageManager, entityFactory, clock)
                })
            } else {
                stageManager.next()
                stageManager.resetConstruction()
                game.eventManager.fireEvent(Scene.Start {
                    StageScene(game, stageManager, entityFactory, clock)
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
        surface.draw(26, y - Globals.TILE_SIZE, game.imageManager.getImage("roman_one")) { dst, src, _, _ ->
            src.and(ARGB.rgb(0xe44437)).over(dst)
        }
        if (stageManager.players.size > 1) {
            surface.draw(170, y - Globals.TILE_SIZE, game.imageManager.getImage("roman_two")) { dst, src, _, _ ->
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
                surface.fillText(
                    "${scores[1].tanksCount}".padStart(2, ' '),
                    145,
                    y - 1,
                    ARGB.WHITE,
                    Globals.FONT_REGULAR
                )
            }
        }
        y += Globals.UNIT_SIZE
        if (drawBonusIndex == 0 || drawBonusIndex == 1) {
            val x = if (drawBonusIndex == 0) 25 else 169
            surface.fillText("BONUS", x, y - 1, ARGB.rgb(0xe44437), Globals.FONT_REGULAR)
            surface.draw(x + 42, y - Globals.TILE_SIZE, game.imageManager.getImage("exclamation")) { dst, src, _, _ ->
                src.and(ARGB.rgb(0xe44437)).over(dst)
            }
            y += Globals.TILE_SIZE
            surface.fillText("$BONUS_SCORE PTS", x, y - 1, ARGB.WHITE, Globals.FONT_REGULAR)
        }
    }

    override fun destroy() {
    }
}