package com.kalikov.game

import com.kalikov.engine.Event
import com.kalikov.engine.EventManager
import com.kalikov.engine.EventSubscriber
import java.time.Clock
import kotlin.reflect.KClass

class FreezeHandler(
    private val eventManager: EventManager,
    pauseManager: PauseManager,
    clock: Clock,
    duration: Int = 9000
) : EventSubscriber {
    data object Unfreeze : Event()

    private companion object {
        private val subscriptions = arrayOf<KClass<out Event>>(PowerUpHandler.Freeze::class)
    }

    private val timer = PauseAwareTimer(pauseManager, clock, duration, ::unfreeze)

    init {
        eventManager.addSubscriber(this, subscriptions)
    }

    val isActive get() = !timer.isStopped
    override val identity: Int
        get() = TODO("Not yet implemented")

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

        timer.stop()
    }
}