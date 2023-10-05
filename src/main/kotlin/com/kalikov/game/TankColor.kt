package com.kalikov.game

import java.time.Clock
import kotlin.math.min

class TankColor(clock: Clock) {
    companion object {
        const val FLASHING_INTERVAL = 16
    }

    var colors: Array<Pair<Int, Int>> = arrayOf(0 to 0)

    private val timer = BasicTimer(clock, FLASHING_INTERVAL, ::change)
    private var hit = 0

    var isAlternative = false
        private set

    fun getColor(): Int {
        val pair = colors[hit]
        return if (isAlternative) pair.second else pair.first
    }

    fun update() {
        if (timer.isStopped) {
            timer.restart()
        }
        timer.update()
    }

    private fun change(count: Int) {
        if (count % 2 != 0) {
            isAlternative = !isAlternative
        }
    }

    fun hit() {
        hit = min(hit + 1, colors.size - 1)
    }
}