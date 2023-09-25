package com.kalikov.game

import java.time.Clock

class BaseExplosionFactory(
    private val eventManager: EventManager,
    private val imageManager: ImageManager,
    private val spriteContainer: SpriteContainer,
    private val clock: Clock,
) : EventSubscriber {
    private companion object {
        private val subscriptions = setOf(Base.Hit::class)
    }

    init {
        eventManager.addSubscriber(this, subscriptions)
    }

    override fun notify(event: Event) {
        if (event is Base.Hit) {
            spriteContainer.addSprite(create(event.base))
        }
    }

    private fun create(base: Base): BaseExplosion {
        val explosion = BaseExplosion(eventManager, imageManager, clock)
        explosion.setPosition(base.center.translate(-explosion.width / 2, -explosion.height / 2))

        eventManager.fireEvent(SoundManager.Play("explosion_2"))

        return explosion
    }

    fun dispose() {
        eventManager.removeSubscriber(this, subscriptions)
    }
}