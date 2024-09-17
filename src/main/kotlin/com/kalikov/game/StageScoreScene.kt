package com.kalikov.game

class StageScoreScene(
    private val game: Game,
    private val stageManager: StageManager,
    private val scores: List<StageScore>,
    private val gameOver: Boolean,
) : Scene {
    private companion object {
        const val BONUS_SCORE = 1000
    }

    val isComplete get() = script.isEmpty

    private val script = Script()

    private val basicTankPoints = StageScorePointsView(game, EnemyTank.EnemyType.BASIC, scores, script)
    private val fastTankPoints = StageScorePointsView(game, EnemyTank.EnemyType.FAST, scores, script)
    private val powerTankPoints = StageScorePointsView(game, EnemyTank.EnemyType.POWER, scores, script)
    private val armorTankPoints = StageScorePointsView(game, EnemyTank.EnemyType.ARMOR, scores, script)

    private var drawTotal = false
    private var drawBonusIndex = -1

    init {
        script.enqueue(Delay(script, 640, game.clock))
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
                script.enqueue(Delay(script, 320, game.clock))
                script.enqueue(Execute {
                    drawBonusIndex = maxTanksIndex
                    game.eventManager.fireEvent(Player.Score(stageManager.players[maxTanksIndex], BONUS_SCORE))
                })
            }
        }
        script.enqueue(Delay(script, 2000, game.clock))
        script.enqueue(Execute {
            if (gameOver) {
                game.eventManager.fireEvent(Scene.Start {
                    GameOverScene(game, stageManager)
                })
            } else {
                stageManager.next()
                stageManager.resetConstruction()
                game.eventManager.fireEvent(Scene.Start {
                    StageScene(game, stageManager)
                })
            }
        })
    }

    override fun update() {
        script.update()
    }

    override fun draw(surface: ScreenSurface) {
        surface.clear(ARGB.BLACK)

        var y = t(4).toPixel()
        surface.fillText("HI-SCORE", px(65), y - 1, ARGB.rgb(0xe44437), Globals.FONT_REGULAR)
        surface.fillText(
            "${stageManager.highScore}".padStart(7, ' '),
            px(137),
            y - 1,
            ARGB.rgb(0xfeac4e),
            Globals.FONT_REGULAR
        )

        y += t(2).toPixel()
        val stage = "STAGE " + "${stageManager.stageNumber}".padStart(2, ' ')
        surface.fillText(stage, px(97), y - 1, ARGB.WHITE, Globals.FONT_REGULAR)

        y += t(2).toPixel()
        surface.draw(px(26), y - Globals.TILE_SIZE, game.imageManager.getImage("roman_one")) { dst, src, _, _ ->
            src.and(ARGB.rgb(0xe44437)).over(dst)
        }
        if (stageManager.players.size > 1) {
            surface.draw(px(170), y - Globals.TILE_SIZE, game.imageManager.getImage("roman_two")) { dst, src, _, _ ->
                src.and(ARGB.rgb(0xe44437)).over(dst)
            }
        }
        surface.fillText("-PLAYER", px(33), y - 1, ARGB.rgb(0xe44437), Globals.FONT_REGULAR)
        if (stageManager.players.size > 1) {
            surface.fillText("-PLAYER", px(177), y - 1, ARGB.rgb(0xe44437), Globals.FONT_REGULAR)

        }

        y += t(2).toPixel()
        val playerOneScore = "${stageManager.players[0].score}".padStart(7, ' ')
        surface.fillText(playerOneScore, px(33), y - 1, ARGB.rgb(0xfeac4e), Globals.FONT_REGULAR)

        if (stageManager.players.size > 1) {
            val playerTwoScore = "${stageManager.players[1].score}".padStart(7, ' ')
            surface.fillText(playerTwoScore, px(177), y - 1, ARGB.rgb(0xfeac4e), Globals.FONT_REGULAR)
        }

        y += t(4).toPixel()
        basicTankPoints.draw(surface, px(17), y - Globals.TILE_SIZE - 1)
        y += t(2).toPixel()
        fastTankPoints.draw(surface, px(17), y - 1)
        y += t(4).toPixel()
        powerTankPoints.draw(surface, px(17), y - Globals.TILE_SIZE - 1)
        y += t(2).toPixel()
        armorTankPoints.draw(surface, px(17), y - 1)

        y += t(2).toPixel()
        surface.fillText("TOTAL", px(49), y - 1, ARGB.WHITE, Globals.FONT_REGULAR)
        surface.fillRect(px(96), y - 3 - Globals.TILE_SIZE, px(64), px(2), ARGB.WHITE)

        if (drawTotal) {
            surface.fillText(
                "${scores[0].tanksCount}".padStart(2, ' '),
                px(97),
                y - 1,
                ARGB.WHITE,
                Globals.FONT_REGULAR
            )
            if (scores.size > 1) {
                surface.fillText(
                    "${scores[1].tanksCount}".padStart(2, ' '),
                    px(145),
                    y - 1,
                    ARGB.WHITE,
                    Globals.FONT_REGULAR
                )
            }
        }
        y += t(2).toPixel()
        if (drawBonusIndex == 0 || drawBonusIndex == 1) {
            val x = px(if (drawBonusIndex == 0) 25 else 169)
            surface.fillText("BONUS", x, y - 1, ARGB.rgb(0xe44437), Globals.FONT_REGULAR)
            surface.draw(x + 42, y - Globals.TILE_SIZE, game.imageManager.getImage("exclamation")) { dst, src, _, _ ->
                src.and(ARGB.rgb(0xe44437)).over(dst)
            }
            y += t(1).toPixel()
            surface.fillText("$BONUS_SCORE PTS", x, y - 1, ARGB.WHITE, Globals.FONT_REGULAR)
        }
    }

    override fun destroy() {
    }
}