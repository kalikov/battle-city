package com.kalikov.game

class TankExplosionFactory(
    private val eventManager: EventManager,
    private val imageManager: ImageManager,
    private val spriteContainer: SpriteContainer
) : EventSubscriber {
    init {
        LeaksDetector.add(this)

        eventManager.addSubscriber(this, setOf(Tank.Destroyed::class))
    }

    override fun notify(event: Event) {
        if (event is Tank.Destroyed) {
            spriteContainer.addSprite(create(event.tank))
        }
    }

    private fun create(tank: Tank): TankExplosion {
        val explosion = TankExplosion(eventManager, imageManager, tank)
        explosion.setPosition(tank.center.translate(-explosion.width / 2, -explosion.height / 2))

        eventManager.fireEvent(SoundManager.Play("explosion_1"))

        return explosion
    }

    fun dispose() {
        eventManager.removeSubscriber(this, setOf(Tank.Destroyed::class))

        LeaksDetector.remove(this)
    }
}