package com.kalikov.game

import com.kalikov.engine.BasicTimer
import com.kalikov.engine.script.ScriptCallback
import com.kalikov.engine.script.ScriptNode
import java.time.Clock

class CurtainFall(
    private val curtain: Curtain,
    private val script: ScriptCallback,
    clock: Clock
) : ScriptNode {
    private val timer = BasicTimer(clock, 2, this::fall)

    override val isDisposable get() = false

    fun reset() {
        timer.stop()
    }

    override fun update() {
        if (timer.isStopped) {
            timer.restart()
        }
        timer.update()
    }

    private fun fall(count: Int) {
        repeat(count) {
            curtain.fall()
            if (curtain.isFallen) {
                return@repeat
            }
        }
        if (curtain.isFallen) {
            timer.stop()
            script.actionCompleted()
        }
    }
}