package com.kalikov.game

import java.time.Clock
import kotlin.math.max

class CurtainFall(
    private val curtain: Curtain,
    private val script: ScriptCallback,
    clock: Clock
) : ScriptNode {
    private val timer = BasicTimer(clock, max(1, 32 / Globals.TILE_SIZE), this::fall)

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