package com.kalikov.game

class StageSelect(
    private val eventManager: EventManager,
    private val stageManager: StageManager,
    private val scriptCallback: ScriptCallback,
) : ScriptNode, EventSubscriber {
    private companion object {
        private val subscriptions = setOf(Keyboard.KeyPressed::class)
    }

    override val isDisposable get() = false

    private var subscribed = false

    override fun update() {
        if (!subscribed) {
            eventManager.addSubscriber(this, subscriptions)
            subscribed = true
        }
    }

    override fun notify(event: Event) {
        if (event is Keyboard.KeyPressed) {
            keyPressed(event.key)
        }
    }

    private fun keyPressed(key: Keyboard.Key) {
        when (key) {
            Keyboard.Key.START -> {
                eventManager.removeSubscriber(this, subscriptions)
                subscribed = false
                scriptCallback.actionCompleted()
            }

            Keyboard.Key.UP, Keyboard.Key.RIGHT, Keyboard.Key.ACTION -> {
                stageManager.next(false)
            }

            Keyboard.Key.DOWN, Keyboard.Key.LEFT -> {
                stageManager.prev(false)
            }

            else -> Unit
        }
    }

    fun dispose() {
        eventManager.removeSubscriber(this, subscriptions)
    }
}