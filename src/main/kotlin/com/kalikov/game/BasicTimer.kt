package com.kalikov.game

import java.time.Clock

class BasicTimer(
    private val clock: Clock,
    interval: Int,
    private val callback: ((Int) -> Unit)? = null
) : Timer {
    constructor(
        clock: Clock,
        interval: Int,
        callback: () -> Unit
    ) : this(clock, interval, { _ -> callback() })

    var interval: Int = interval
        private set

    override val isStopped get() = start == 0L

    val isPaused get() = pauseStart != 0L

    private var start = 0L
    private var pauseStart = 0L

    override fun restart() {
        start = clock.millis()
        pauseStart = 0
    }

    fun restart(interval: Int) {
        start = clock.millis()
        pauseStart = 0
        this.interval = interval
    }

    fun stop() {
        start = 0
        pauseStart = 0
    }

    fun pause() {
        if (start > 0) {
            pauseStart = clock.millis()
        }
    }

    fun resume() {
        if (pauseStart > 0) {
            val pauseDuration = clock.millis() - pauseStart
            start += pauseDuration
            pauseStart = 0
        }
    }

    override fun update() {
        if (start > 0 && pauseStart == 0L) {
            val now = clock.millis()
            val count = ((now - start) / interval).toInt()
            start += interval * count
            if (count > 0) {
                callback?.invoke(count)
            }
        }
    }

    override fun dispose() {
        stop()
    }
}