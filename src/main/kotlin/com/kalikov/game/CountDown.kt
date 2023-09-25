package com.kalikov.game

open class CountDown(val value: Int, private val callback: Runnable? = null) {
    private var tick = 0

    val stopped: Boolean
        get() = tick == 0

    fun restart() {
        tick = value
    }

    fun stop() {
        if (tick > 0) {
            tick = 0
            callback?.run()
        }
    }

    fun update() {
        if (tick > 0) {
            tick--
            if (tick == 0) {
                callback?.run()
            }
        }
    }
}