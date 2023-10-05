package com.kalikov.game

import java.time.Clock

class Animation private constructor(
    private val frames: FrameSequence,
    private val timer: Timer
) {
    companion object {
        fun basic(frames: FrameSequence, clock: Clock, interval: Int): Animation {
            return Animation(frames, BasicTimer(clock, interval, frames::advance))
        }

        fun pauseAware(eventManager: EventManager, frames: FrameSequence, clock: Clock, interval: Int): Animation {
            return Animation(frames, PauseAwareTimer(eventManager, clock, interval, frames::advance))
        }
    }

    val isCompleted get() = frames.isCompleted

    val isRunning get() = !timer.isStopped

    val frame get() = frames.frame

    fun restart() {
        frames.restart()
        timer.restart()
    }

    fun update() {
        if (isCompleted) {
            return
        }
        timer.update()
    }

    fun dispose() {
        timer.dispose()
    }
}