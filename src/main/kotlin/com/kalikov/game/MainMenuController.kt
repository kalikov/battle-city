package com.kalikov.game

class MainMenuController(private val eventManager: EventManager, private val menu: MainMenu) : EventSubscriber {
    private companion object {
        private val subscriptions = setOf(Keyboard.KeyPressed::class)
    }

    var isActive = false

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
        if (key == Keyboard.Key.SELECT) {
            menu.nextItem()
        } else if (key == Keyboard.Key.START) {
            menu.executeCurrentItem()
        }
    }

    fun dispose() {
        eventManager.removeSubscriber(this, subscriptions)

        LeaksDetector.remove(this)
    }
}
