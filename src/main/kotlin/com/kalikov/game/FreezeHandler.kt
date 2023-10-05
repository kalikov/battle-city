package com.kalikov.game

import java.time.Clock

class FreezeHandler(
    private val eventManager: EventManager,
    clock: Clock,
    duration: Int = 9000
) : EventSubscriber {
    data object Unfreeze : Event()

    private companion object {
        private val subscriptions = setOf(PowerUpHandler.Freeze::class)
    }

    private val timer = PauseAwareTimer(eventManager, clock, duration, ::unfreeze)

    init {
        eventManager.addSubscriber(this, subscriptions)
    }

    val isActive get() = !timer.isStopped

    override fun notify(event: Event) {
        if (event is PowerUpHandler.Freeze) {
            restart()
        }
    }

    private fun restart() {
        timer.restart()
    }

    private fun unfreeze() {
        eventManager.fireEvent(Unfreeze)
        timer.stop()
    }

    fun update() {
        timer.update()
    }

    fun dispose() {
        eventManager.removeSubscriber(this, subscriptions)

        timer.dispose()
    }
}