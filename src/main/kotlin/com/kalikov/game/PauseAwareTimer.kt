package com.kalikov.game

import java.time.Clock
import kotlin.Unit

class PauseAwareTimer private constructor(
    private val eventManager: EventManager,
    private val timer: BasicTimer
) : Timer, EventSubscriber {
    constructor(
        eventManager: EventManager,
        clock: Clock,
        interval: Int,
        callback: ((Int) -> Unit)? = null
    ) : this(eventManager, BasicTimer(clock, interval, callback))

    constructor(
        eventManager: EventManager,
        clock: Clock,
        interval: Int,
        callback: () -> Unit
    ) : this(eventManager, BasicTimer(clock, interval, callback))

    private companion object {
        private val subscriptions = setOf(PauseManager.Start::class, PauseManager.End::class)
    }

    override val isStopped get() = timer.isStopped

    private var isPaused = false

    init {
        eventManager.addSubscriber(this, subscriptions)
    }

    override fun restart() {
        timer.restart()
        if (isPaused) {
            timer.pause()
        }
    }

    fun stop() = timer.stop()

    override fun update() = timer.update()

    override fun notify(event: Event) {
        if (event is PauseManager.Start) {
            isPaused = true
            timer.pause()
        } else if (event is PauseManager.End) {
            timer.resume()
            isPaused = false
        }
    }

    override fun dispose() {
        timer.stop()
        eventManager.removeSubscriber(this, subscriptions)
    }
}