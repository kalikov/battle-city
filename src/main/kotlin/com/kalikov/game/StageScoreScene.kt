package com.kalikov.game

import com.kalikov.engine.ARGB
import com.kalikov.engine.Blending
import com.kalikov.engine.Scene
import com.kalikov.engine.ScreenSurface
import com.kalikov.engine.px
import com.kalikov.engine.script.Delay
import com.kalikov.engine.script.Execute
import com.kalikov.engine.script.Script

class StageScoreScene(
    private val game: BattleCityGame,
    private val sceneProvider: SceneProvider,
) : Scene {
    private companion object {
        const val BONUS_SCORE = 1000

        private val blending = Blending { dst, src, _, _ -> src.and(ARGB.rgb(0xe44437)).over(dst) }
    }

    val isComplete get() = script.isDone

    private val script = Script()

    private val points = Array(EnemyTank.EnemyType.entries.size) {
        StageScorePointsView(game, EnemyTank.EnemyType.entries[it], script)
    }

    private var drawTotal = false
    private var drawBonusIndex = -1

    override fun activate() {
        drawTotal = false
        drawBonusIndex = -1

        points.forEach {
            it.reset()
        }

        script.clear()
        script.enqueue(Delay(script, 640, game.clock))
        points.forEach {
            script.enqueue(it)
        }
        script.enqueue(Execute { drawTotal = true })
        if (!game.stageManager.isGameOver && game.stageManager.players.size > 1) {
            var maxTanksCount = 0
            var maxTanksIndex = 0
            game.stageManager.players.forEachIndexed { index, it ->
                if (maxTanksCount < it.stageScore.tanksCount) {
                    maxTanksCount = it.stageScore.tanksCount
                    maxTanksIndex = index
                } else if (maxTanksCount == it.stageScore.tanksCount) {
                    maxTanksIndex = -1
                }
            }
            if (maxTanksIndex >= 0) {
                script.enqueue(Delay(script, 320, game.clock))
                script.enqueue(Execute {
                    drawBonusIndex = maxTanksIndex
                    game.stageManager.players[maxTanksIndex].score(BONUS_SCORE)
                    game.soundManager.bonus.play()
                })
            }
        }
        script.enqueue(Delay(script, 2000, game.clock))
        script.enqueue(Execute {
            if (game.stageManager.isGameOver) {
                game.sceneManager.setNextScene(sceneProvider.gameOverScene)
            } else {
                game.stageManager.next()
                game.stageManager.resetConstruction()
                game.sceneManager.setNextScene(sceneProvider.stageScene)
            }
        })
    }

    override fun deactivate() {
    }

    override fun update() {
        script.update()
    }

    override fun draw(surface: ScreenSurface) {
        surface.clear(ARGB.BLACK)

        var y = 4.tiles.toPixel()
        surface.fillText("HI-SCORE", 65.px, y - 1, ARGB.rgb(0xe44437), Globals.FONT_REGULAR)
        surface.fillText(
            "${game.stageManager.highScore}".padStart(7, ' '),
            137.px,
            y - 1,
            ARGB.rgb(0xfeac4e),
            Globals.FONT_REGULAR
        )

        y += 2.tiles.toPixel()
        val stage = "STAGE " + "${game.stageManager.stageNumber}".padStart(2, ' ')
        surface.fillText(stage, 97.px, y - 1, ARGB.WHITE, Globals.FONT_REGULAR)

        y += 2.tiles.toPixel()
        surface.draw(26.px, y - Globals.TILE_SIZE, game.imageManager.romanOne, blending)
        if (game.stageManager.players.size > 1) {
            surface.draw(170.px, y - Globals.TILE_SIZE, game.imageManager.romanTwo, blending)
        }
        surface.fillText("-PLAYER", 33.px, y - 1, ARGB.rgb(0xe44437), Globals.FONT_REGULAR)
        if (game.stageManager.players.size > 1) {
            surface.fillText("-PLAYER", 177.px, y - 1, ARGB.rgb(0xe44437), Globals.FONT_REGULAR)

        }

        y += 2.tiles.toPixel()
        val playerOneScore = "${game.stageManager.players[0].score}".padStart(7, ' ')
        surface.fillText(playerOneScore, 33.px, y - 1, ARGB.rgb(0xfeac4e), Globals.FONT_REGULAR)

        if (game.stageManager.players.size > 1) {
            val playerTwoScore = "${game.stageManager.players[1].score}".padStart(7, ' ')
            surface.fillText(playerTwoScore, 177.px, y - 1, ARGB.rgb(0xfeac4e), Globals.FONT_REGULAR)
        }

        points.forEach {
            y += 3.tiles.toPixel()
            it.draw(surface, 17.px, y - 1)
        }

        y += 2.tiles.toPixel()
        surface.fillText("TOTAL", 49.px, y - 1, ARGB.WHITE, Globals.FONT_REGULAR)
        surface.fillRect(96.px, y - 3 - Globals.TILE_SIZE, 64.px, 2.px, ARGB.WHITE)

        if (drawTotal) {
            surface.fillText(
                "${game.stageManager.players[0].stageScore.tanksCount}".padStart(2, ' '),
                97.px,
                y - 1,
                ARGB.WHITE,
                Globals.FONT_REGULAR
            )
            if (game.stageManager.players.size > 1) {
                surface.fillText(
                    "${game.stageManager.players[1].stageScore.tanksCount}".padStart(2, ' '),
                    145.px,
                    y - 1,
                    ARGB.WHITE,
                    Globals.FONT_REGULAR
                )
            }
        }
        y += 2.tiles.toPixel()
        if (drawBonusIndex == 0 || drawBonusIndex == 1) {
            val x = if (drawBonusIndex == 0) 25.px else 169.px
            surface.fillText("BONUS", x, y - 1, ARGB.rgb(0xe44437), Globals.FONT_REGULAR)
            surface.draw(x + 42, y - Globals.TILE_SIZE, game.imageManager.getImage("exclamation"), blending)
            y += 1.tiles.toPixel()
            surface.fillText("$BONUS_SCORE PTS", x, y - 1, ARGB.WHITE, Globals.FONT_REGULAR)
        }
    }

    override fun destroy() {
    }
}