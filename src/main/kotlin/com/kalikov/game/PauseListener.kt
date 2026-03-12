package com.kalikov.game

import com.kalikov.engine.Event
import com.kalikov.engine.EventSubscriber
import com.kalikov.engine.Keyboard
import com.kalikov.engine.LeaksDetector
import kotlin.reflect.KClass

class PauseListener(private val game: BattleCityGame) : EventSubscriber, PauseManager {
    private companion object {
        private val subscriptions = arrayOf<KClass<out Event>>(Keyboard.KeyPressed::class)
    }

    override val identity get() = Globals.IDENTITY_PAUSE_MANAGER

    override var isPaused = false
        private set

    var isEnabled = true

    fun activate() {
        LeaksDetector.add(this)

        game.eventManager.addSubscriber(this, subscriptions)
    }

    fun deactivate() {
        game.eventManager.removeSubscriber(this, subscriptions)

        LeaksDetector.remove(this)
    }

    override fun notify(event: Event) {
        if (event is Keyboard.KeyPressed) {
            keyPressed(event.key)
        }
    }

    private fun keyPressed(key: Keyboard.Key) {
        if (!isEnabled) {
            return
        }
        if (key == Keyboard.Key.START) {
            isPaused = !isPaused

            if (isPaused) {
                game.soundManager.pause()
                game.soundManager.pause.play()
            } else {
                game.soundManager.resume()
            }
        }
    }
}