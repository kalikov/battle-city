package com.kalikov.game

class BaseExplosionFactory(
    private val game: Game,
    private val spriteContainer: SpriteContainer,
) : EventSubscriber {
    private companion object {
        private val subscriptions = setOf(Base.Hit::class)
    }

    init {
        game.eventManager.addSubscriber(this, subscriptions)
    }

    override fun notify(event: Event) {
        if (event is Base.Hit) {
            spriteContainer.addSprite(create(event.base))
        }
    }

    private fun create(base: Base): BaseExplosion {
        val explosion = BaseExplosion(game.eventManager, game.imageManager, game.clock)
        explosion.setPosition(base.center.translate(-explosion.width / 2, -explosion.height / 2))

        game.eventManager.fireEvent(SoundManager.Play("explosion_2"))

        return explosion
    }

    fun dispose() {
        game.eventManager.removeSubscriber(this, subscriptions)
    }
}