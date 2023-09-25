package com.kalikov.game

import java.time.Clock
import kotlin.math.max

class CurtainRise(
    private val curtain: Curtain,
    private val script: ScriptCallback,
    clock: Clock
) : ScriptNode {
    private val timer = BasicTimer(clock, max(1, 32 / Globals.TILE_SIZE), this::rise)

    override val isDisposable get() = false

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