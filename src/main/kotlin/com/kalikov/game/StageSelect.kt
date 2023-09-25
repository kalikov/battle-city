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
        if (key == Keyboard.Key.START) {
            eventManager.removeSubscriber(this, subscriptions)
            subscribed = false
            scriptCallback.actionCompleted()
        } else if (key == Keyboard.Key.UP || key == Keyboard.Key.RIGHT || key == Keyboard.Key.SPACE) {
            stageManager.next(false)
        } else if (key == Keyboard.Key.DOWN || key == Keyboard.Key.LEFT) {
            stageManager.prev(false)
        }
    }

    fun dispose() {
        eventManager.removeSubscriber(this, subscriptions)
    }
}