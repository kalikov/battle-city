package com.kalikov.game

import java.time.Clock

class Delay(private val script: ScriptCallback, interval: Int, clock: Clock) : ScriptNode {
    private val timer = BasicTimer(clock, interval, ::complete)

    override val isDisposable: Boolean
        get() = false

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