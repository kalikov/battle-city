package com.kalikov.game

import java.time.Clock
import kotlin.math.min

class TankColor(clock: Clock) {
    companion object {
        const val FLASHING_INTERVAL = 16
    }

    var colors: Array<IntArray> = arrayOf(intArrayOf(0))
        set(value) {
            require(value.isNotEmpty())
            field = value
        }

    private val timer = BasicTimer(clock, FLASHING_INTERVAL, ::change)
    private var hit = 0

    var index = 0
        private set

    fun getColor(): Int {
        val sequence = colors[hit]
        return sequence[index]
    }

    fun update() {
        if (timer.isStopped) {
            timer.restart()
        }
        timer.update()
    }

    private fun change(count: Int) {
        val size = colors[hit].size
        if (size > 1) {
            index = (index + count) % size
        }
    }

    fun hit() {
        hit = min(hit + 1, colors.size - 1)
        index = 0
    }
}