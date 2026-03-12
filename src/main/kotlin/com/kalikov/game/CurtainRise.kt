package com.kalikov.game

import com.kalikov.engine.BasicTimer
import com.kalikov.engine.script.ScriptCallback
import com.kalikov.engine.script.ScriptNode
import java.time.Clock

class CurtainRise(
    private val curtain: Curtain,
    private val script: ScriptCallback,
    clock: Clock
) : ScriptNode {
    private val timer = BasicTimer(clock, 2, this::rise)

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

    private fun rise(count: Int) {
        for (i in 1..count) {
            curtain.rise()
            if (curtain.isRisen) {
                break
            }
        }
        if (curtain.isRisen) {
            timer.stop()
            script.actionCompleted()
        }
    }
}