package com.kalikov.game

import java.time.Clock

class CurtainFall(
    private val curtain: Curtain,
    private val script: ScriptCallback,
    clock: Clock
) : ScriptNode {
    private val timer = BasicTimer(clock, 2, this::fall)

    override val isDisposable get() = false

    override fun update() {
        if (timer.isStopped) {
            timer.restart()
        }
        timer.update()
    }

    private fun fall(count: Int) {
        for (i in 1..count) {
            curtain.fall()
            if (curtain.isFallen) {
                break
            }
        }
        if (curtain.isFallen) {
            timer.stop()
            script.actionCompleted()
        }
    }
}