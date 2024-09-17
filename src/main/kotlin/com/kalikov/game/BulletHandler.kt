package com.kalikov.game

class BulletHandler(
    private val eventManager: EventManager,
    private val spriteContainer: SpriteContainer
) : EventSubscriber {
    private companion object {
        private val subscriptions = setOf(Tank.Shoot::class)
    }

    init {
        eventManager.addSubscriber(this, subscriptions)
    }

    override fun notify(event: Event) {
        if (event is Tank.Shoot) {
            if (event.bullet.tank is PlayerTank) {
                eventManager.fireEvent(SoundManager.Play("bullet_shot"))
            }
            spriteContainer.addSprite(event.bullet)
        }
    }

    fun dispose() {
        eventManager.removeSubscriber(this, subscriptions)
    }
}