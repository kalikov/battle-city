package com.kalikov.game

import java.time.Clock
import kotlin.math.min

class StageScorePointsView(
    private val eventManager: EventManager,
    imageManager: ImageManager,
    private val enemyType: EnemyTank.EnemyType,
    private val scores: List<StageScore>,
    private val listener: Script,
    clock: Clock
) : ScriptNode {
    private val counterBound = scores.asSequence().take(2).map { it.getTanks(enemyType) }.max()
    private var counter = if (counterBound > 0) 1 else 0
    private var isScoreVisible = false
    private val script = Script()

    private val enemyImage = imageManager.getImage("tank_enemy")
    private val arrowImage = imageManager.getImage("arrows")

    init {
        script.enqueue(Execute { isScoreVisible = true })
        if (counterBound > 0) {
            script.enqueue(Execute {
                eventManager.fireEvent(SoundManager.Play("statistics_1"))
            })
            script.enqueue(Delay(script, 160, clock))
        }
        for (i in 1 until counterBound) {
            script.enqueue(Execute {
                counter++
                eventManager.fireEvent(SoundManager.Play("statistics_1"))
            })
            script.enqueue(Delay(script, 160, clock))
        }
        script.enqueue(Delay(script, 640, clock))
        script.enqueue(Execute { listener.actionCompleted() })
    }

    override val isDisposable get() = false

    override fun update() {
        script.update()
    }

    fun draw(surface: ScreenSurface, x: Int, y: Int) {
        surface.fillText("PTS", x + 6 * Globals.TILE_SIZE, y, ARGB.WHITE, Globals.FONT_REGULAR)
        if (scores.size > 1) {
            surface.fillText("PTS", x + 24 * Globals.TILE_SIZE, y, ARGB.WHITE, Globals.FONT_REGULAR)
        }
        if (isScoreVisible) {
            val countOne = min(counter, scores[0].getTanks(enemyType))
            val scoreOneString = "${countOne * enemyType.score}".padStart(5, ' ')
            val countOneString = "$countOne".padStart(2, ' ')
            surface.fillText(scoreOneString, x, y, ARGB.WHITE, Globals.FONT_REGULAR)
            surface.fillText(countOneString, x + 10 * Globals.TILE_SIZE, y, ARGB.WHITE, Globals.FONT_REGULAR)
            if (scores.size > 1) {
                val countTwo = min(counter, scores[1].getTanks(enemyType))
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
            0,
            2 * enemyType.index * Globals.UNIT_SIZE,
            Globals.UNIT_SIZE,
            Globals.UNIT_SIZE
        )
        surface.draw(x + 96, y - 7, arrowImage, 0, 0, 7, 7)
        if (scores.size > 1) {
            surface.draw(x + 120, y - 7, arrowImage, 7, 0, 7, 7)
        }
    }
}