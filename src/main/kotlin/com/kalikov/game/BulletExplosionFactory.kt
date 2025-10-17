package com.kalikov.game

import com.kalikov.engine.Event
import com.kalikov.engine.EventSubscriber
import kotlin.reflect.KClass

class BulletExplosionFactory(
    private val game: BattleCityGame,
    private val pauseManager: PauseManager,
    private val spriteContainer: SpriteContainer,
) : EventSubscriber {
    override val identity: Int
        get() = TODO("Not yet implemented")

    private companion object {
        private val subscriptions = arrayOf<KClass<out Event>>(Bullet.Exploded::class)
    }

    init {
        game.eventManager.addSubscriber(this, subscriptions)
    }

    override fun notify(event: Event) {
        if (event is Bullet.Exploded) {
            spriteContainer.addSprite(create(event.bullet))
        }
    }

    private fun create(bullet: BulletHandle): BulletExplosion {
        return BulletExplosion(game, pauseManager, bullet)
    }

    fun dispose() {
        game.eventManager.removeSubscriber(this, subscriptions)
    }
}