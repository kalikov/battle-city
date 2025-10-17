package com.kalikov.game

import com.kalikov.engine.BasicTimer
import com.kalikov.engine.Timer
import java.time.Clock

class PauseAwareTimer private constructor(
    private val pauseManager: PauseManager,
    private val timer: BasicTimer
) : Timer {
    constructor(
        pauseManager: PauseManager,
        clock: Clock,
        interval: Int,
        callback: ((Int) -> Unit)? = null
    ) : this(pauseManager, BasicTimer(clock, interval, callback))

    constructor(
        pauseManager: PauseManager,
        clock: Clock,
        interval: Int,
        callback: () -> Unit
    ) : this(pauseManager, BasicTimer(clock, interval, callback))

    override val isStopped get() = timer.isStopped

    override fun restart() {
        timer.restart()
        if (pauseManager.isPaused) {
            timer.pause()
        }
    }

    override fun stop() = timer.stop()

    override fun update() {
        if (pauseManager.isPaused && !timer.isStopped && !timer.isPaused) {
            timer.pause()
        } else if (!pauseManager.isPaused && timer.isPaused) {
            timer.resume()
        }
        timer.update()
    }
}