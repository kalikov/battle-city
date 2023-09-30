package com.kalikov.game

import java.time.Clock

class StageScorePointsView(
    private val eventManager: EventManager,
    private val value: Int,
    count: Int,
    private val listener: Script,
    clock: Clock
) : ScriptNode {
    private var counter = if (count > 0) 1 else 0
    private var visible = false
    private val script = Script()

    init {
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
        if (!visible) {
            return
        }
        var str = "${counter * value}".padStart(5, ' ')
        str += "     " + "$counter".padStart(2, ' ')
        surface.fillText(str, x, y, ARGB.WHITE, Globals.FONT_REGULAR)
    }

    fun show() {
        visible = true
    }
}