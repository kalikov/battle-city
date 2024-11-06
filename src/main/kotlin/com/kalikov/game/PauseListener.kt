package com.kalikov.game

class PauseListener(private val game: Game) : EventSubscriber, PauseManager {
    private companion object {
        private val subscriptions = setOf(Keyboard.KeyPressed::class)
    }

    override var isPaused = false
        private set

    var isActive = true

    init {
        LeaksDetector.add(this)

        game.eventManager.addSubscriber(this, subscriptions)
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
                game.eventManager.fireEvent(PauseManager.Start)
                game.soundManager.pause()
                game.soundManager.pause.play()
            } else {
                game.eventManager.fireEvent(PauseManager.End)
                game.soundManager.resume()
            }
        }
    }

    fun dispose() {
        game.eventManager.removeSubscriber(this, subscriptions)

        LeaksDetector.remove(this)
    }
}