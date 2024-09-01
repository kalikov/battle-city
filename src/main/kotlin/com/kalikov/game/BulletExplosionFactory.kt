package com.kalikov.game

import java.time.Clock

class BulletExplosionFactory(
    private val eventManager: EventManager,
    private val imageManager: ImageManager,
    private val spriteContainer: SpriteContainer,
    private val clock: Clock
) : EventSubscriber {
    private companion object {
        private val subscriptions = setOf(Bullet.Exploded::class)
    }

    init {
        eventManager.addSubscriber(this, subscriptions)
    }

    override fun notify(event: Event) {
        if (event is Bullet.Exploded) {
            spriteContainer.addSprite(create(event.bullet))
        }
    }

    private fun create(bullet: Bullet): BulletExplosion {
        val explosion = BulletExplosion(eventManager, imageManager, clock, bullet)
        explosion.setPosition(bullet.center.translate(-explosion.width / 2, -explosion.height / 2))
        return explosion
    }

    fun dispose() {
        eventManager.removeSubscriber(this, subscriptions)
    }
}