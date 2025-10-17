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

    override var isPaused = false
        private set

    var isActive = true
    override val identity: Int
        get() = TODO("Not yet implemented")

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