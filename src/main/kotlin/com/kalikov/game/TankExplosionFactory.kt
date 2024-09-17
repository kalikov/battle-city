package com.kalikov.game

class TankExplosionFactory(
    private val game: Game,
    private val spriteContainer: SpriteContainer
) : EventSubscriber {
    init {
        LeaksDetector.add(this)

        game.eventManager.addSubscriber(this, setOf(Tank.Destroyed::class))
    }

    override fun notify(event: Event) {
        if (event is Tank.Destroyed) {
            spriteContainer.addSprite(create(event.tank))
        }
    }

    private fun create(tank: Tank): TankExplosion {
        val explosion = TankExplosion(game, tank)
        explosion.setPosition(tank.center - explosion.width / 2, tank.middle - explosion.height / 2)

        game.eventManager.fireEvent(SoundManager.Play("explosion_1"))

        return explosion
    }

    fun dispose() {
        game.eventManager.removeSubscriber(this, setOf(Tank.Destroyed::class))

        LeaksDetector.remove(this)
    }
}