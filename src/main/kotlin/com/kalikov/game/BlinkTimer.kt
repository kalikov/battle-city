package com.kalikov.game

import java.time.Clock

class BlinkTimer(clock: Clock, interval: Int) {
    private val timer = BasicTimer(clock, interval, ::toggle)

    var isOpaque = true
        private set

    val isStopped get() = timer.isStopped

    fun restart() = timer.restart()

    fun update() = timer.update()

    fun stop() = timer.stop()

    private fun toggle(count: Int) {
        if (count % 2 != 0) {
            isOpaque = !isOpaque
        }
    }
}