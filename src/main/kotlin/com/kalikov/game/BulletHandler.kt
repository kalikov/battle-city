package com.kalikov.game

import com.kalikov.engine.Event
import com.kalikov.engine.EventSubscriber
import kotlin.reflect.KClass

class BulletHandler(
    private val game: BattleCityGame,
    private val spriteContainer: SpriteContainer
) : EventSubscriber {
    override val identity: Int
        get() = TODO("Not yet implemented")

    private companion object {
        private val subscriptions = arrayOf<KClass<out Event>>(Tank.Shoot::class)
    }

    init {
        game.eventManager.addSubscriber(this, subscriptions)
    }

    override fun notify(event: Event) {
        if (event is Tank.Shoot) {
            if (event.bullet.tank is PlayerTank) {
                game.soundManager.bulletShot.play()
            }
            spriteContainer.addSprite(event.bullet)
        }
    }

    fun dispose() {
        game.eventManager.removeSubscriber(this, subscriptions)
    }
}