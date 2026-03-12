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
        private val subscriptions = arrayOf<KClass<out Event>>(PowerUpManager.Freeze::class)
    }

    override val identity get() = Globals.IDENTITY_FREEZE_HANDLER

    private val timer = PauseAwareTimer(pauseManager, clock, duration, ::unfreeze)

    val isActive get() = !timer.isStopped

    fun activate() {
        eventManager.addSubscriber(this, subscriptions)
    }

    fun deactivate() {
        eventManager.removeSubscriber(this, subscriptions)

        timer.stop()
    }

    override fun notify(event: Event) {
        if (event is PowerUpManager.Freeze) {
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
}