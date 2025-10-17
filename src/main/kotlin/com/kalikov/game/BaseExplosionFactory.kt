package com.kalikov.game

import com.kalikov.engine.Event
import com.kalikov.engine.EventSubscriber
import kotlin.reflect.KClass

class BaseExplosionFactory(
    private val game: BattleCityGame,
    private val pauseManager: PauseManager,
    private val spriteContainer: SpriteContainer,
) : EventSubscriber {
    override val identity: Int
        get() = TODO("Not yet implemented")

    private companion object {
        private val subscriptions = arrayOf<KClass<out Event>>(Base.Hit::class)
    }

    init {
        game.eventManager.addSubscriber(this, subscriptions)
    }

    override fun notify(event: Event) {
        if (event is Base.Hit) {
            spriteContainer.addSprite(create(event.base))
        }
    }

    private fun create(base: BaseHandle): BaseExplosion {
        val explosion = BaseExplosion(
            game,
            pauseManager,
            base.x + Base.SIZE / 2 - BaseExplosion.SIZE / 2,
            base.y + Base.SIZE / 2 - BaseExplosion.SIZE / 2,
        )
        game.soundManager.playerExplosion.play()

        return explosion
    }

    fun dispose() {
        game.eventManager.removeSubscriber(this, subscriptions)
    }
}