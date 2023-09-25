package com.kalikov.game

class PauseListener(private val eventManager: EventManager) : EventSubscriber, PauseManager {
    private companion object {
        private val subscriptions = setOf(Keyboard.KeyPressed::class)
    }

    override var isPaused = false
        private set

    var isActive = true

    init {
        LeaksDetector.add(this)

        eventManager.addSubscriber(this, subscriptions)
    }

    override fun notify(event: Event) {
        if (event is Keyboard.KeyPressed) {
            keyPressed(event.key)
        }
    }

    private fun keyPressed(key: Keyboard.Key) {
        if (!isActive) {
            return
        }
        if (key == Keyboard.Key.START) {
            isPaused = !isPaused

            if (isPaused) {
                eventManager.fireEvent(SoundManager.Play("pause"))
                eventManager.fireEvent(PauseManager.Start)
            } else {
                eventManager.fireEvent(PauseManager.End)
            }
        }
    }

    fun dispose() {
        eventManager.removeSubscriber(this, subscriptions)

        LeaksDetector.remove(this)
    }
}