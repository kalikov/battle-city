package com.kalikov.game

import com.kalikov.engine.ARGB
import com.kalikov.engine.Pixel
import com.kalikov.engine.ScreenSurface
import com.kalikov.engine.px
import com.kalikov.engine.script.Delay
import com.kalikov.engine.script.Execute
import com.kalikov.engine.script.Script
import com.kalikov.engine.script.ScriptNode
import com.kalikov.engine.times
import kotlin.math.min

class StageScorePointsView(
    private val game: BattleCityGame,
    private val enemyType: EnemyTank.EnemyType,
    private val listener: Script,
) : ScriptNode {
    private val script = Script()
    private var counterBound = 0
    private var counter = 0
    private var isScoreVisible = false

    private val enemyImage = game.imageManager.getImage("tank_enemy")
    private val arrowImage = game.imageManager.getImage("arrows")

    fun reset() {
        isScoreVisible = false

        counterBound = game.stageManager.players.maxOf { it.stageScore.getTanks(enemyType) }
        counter = if (counterBound > 0) 1 else 0

        script.clear()
        script.enqueue(Execute { isScoreVisible = true })
        if (counterBound > 0) {
            script.enqueue(Execute {
                game.soundManager.statistics.play()
            })
            script.enqueue(Delay(script, 160, game.clock))
        }
        repeat(counterBound - 1) {
            script.enqueue(Execute {
                counter++
                game.soundManager.statistics.play()
            })
            script.enqueue(Delay(script, 160, game.clock))
        }
        script.enqueue(Delay(script, 640, game.clock))
        script.enqueue(Execute { listener.actionCompleted() })
    }

    override val isDisposable get() = false

    override fun update() {
        script.update()
    }

    fun draw(surface: ScreenSurface, x: Pixel, y: Pixel) {
        surface.fillText("PTS", x + 6 * Globals.TILE_SIZE, y, ARGB.WHITE, Globals.FONT_REGULAR)
        if (game.stageManager.players.size > 1) {
            surface.fillText("PTS", x + 24 * Globals.TILE_SIZE, y, ARGB.WHITE, Globals.FONT_REGULAR)
        }
        if (isScoreVisible) {
            val countOne = min(counter, game.stageManager.players[0].stageScore.getTanks(enemyType))
            val scoreOneString = "${countOne * enemyType.score}".padStart(5, ' ')
            val countOneString = "$countOne".padStart(2, ' ')
            surface.fillText(scoreOneString, x, y, ARGB.WHITE, Globals.FONT_REGULAR)
            surface.fillText(countOneString, x + 10 * Globals.TILE_SIZE, y, ARGB.WHITE, Globals.FONT_REGULAR)
            if (game.stageManager.players.size > 1) {
                val countTwo = min(counter, game.stageManager.players[1].stageScore.getTanks(enemyType))
                val scoreTwoString = "${countTwo * enemyType.score}".padStart(5, ' ')
                val countTwoString = "$countTwo".padStart(2, ' ')
                surface.fillText(scoreTwoString, x + 18 * Globals.TILE_SIZE, y, ARGB.WHITE, Globals.FONT_REGULAR)
                surface.fillText(countTwoString, x + 16 * Globals.TILE_SIZE, y, ARGB.WHITE, Globals.FONT_REGULAR)
            }
        }
        surface.draw(
            x + 104,
            y - 10,
            enemyImage,
            0.px,
            2 * enemyType.index * Tank.SIZE,
            Tank.SIZE,
            Tank.SIZE
        )
        surface.draw(x + 96, y - 7, arrowImage, 0.px, 0.px, 7.px, 7.px)
        if (game.stageManager.players.size > 1) {
            surface.draw(x + 120, y - 7, arrowImage, 7.px, 0.px, 7.px, 7.px)
        }
    }
}