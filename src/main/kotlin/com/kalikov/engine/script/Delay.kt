package com.kalikov.engine.script

import com.kalikov.engine.BasicTimer
import java.time.Clock

class Delay(private val script: ScriptCallback, interval: Int, clock: Clock) : ScriptNode {
    private val timer = BasicTimer(clock, interval, ::complete)

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

    private fun complete() {
        timer.stop()
        script.actionCompleted()
    }
}