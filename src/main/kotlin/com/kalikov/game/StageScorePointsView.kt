package com.kalikov.game

import java.time.Clock

class StageScorePointsView(
    private val eventManager: EventManager,
    imageManager: ImageManager,
    private val enemyType: Tank.EnemyType,
    score: StageScore,
    private val listener: Script,
    clock: Clock
) : ScriptNode {
    private val count = score.getTanks(enemyType)
    private var counter = if (count > 0) 1 else 0
    private var isScoreVisible = false
    private val script = Script()

    private val enemyImage = imageManager.getImage("tank_enemy")
    private val arrowImage = imageManager.getImage("arrows")

    init {
        script.enqueue(Execute { isScoreVisible = true })
        if (count > 0) {
            script.enqueue(Execute {
                eventManager.fireEvent(SoundManager.Play("statistics_1"))
            })
            script.enqueue(Delay(script, 160, clock))
        }
        for (i in 1 until count) {
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
        if (isScoreVisible) {
            var str = "${counter * enemyType.score}".padStart(5, ' ')
            str += "     " + "$counter".padStart(2, ' ')
            surface.fillText(str, x, y, ARGB.WHITE, Globals.FONT_REGULAR)
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
    }
}