package com.kalikov.game

class BulletExplosionFactory(
    private val game: Game,
    private val spriteContainer: SpriteContainer,
) : EventSubscriber {
    private companion object {
        private val subscriptions = setOf(Bullet.Exploded::class)
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
        return BulletExplosion(game, bullet)
    }

    fun dispose() {
        game.eventManager.removeSubscriber(this, subscriptions)
    }
}