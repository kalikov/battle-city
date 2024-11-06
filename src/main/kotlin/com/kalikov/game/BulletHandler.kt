package com.kalikov.game

class BulletHandler(
    private val game: Game,
    private val spriteContainer: SpriteContainer
) : EventSubscriber {
    private companion object {
        private val subscriptions = setOf(Tank.Shoot::class)
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