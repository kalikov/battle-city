package com.kalikov.game

class BulletFactory(
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
            spriteContainer.addSprite(createBullet(event.tank))
        }
    }

    private fun createBullet(tank: Tank): Bullet {
        val bullet = tank.createBullet()

        if (tank.isPlayer) {
            eventManager.fireEvent(SoundManager.Play("bullet_shot"))
        }

        return bullet
    }

    fun dispose() {
        eventManager.removeSubscriber(this, subscriptions)
    }
}