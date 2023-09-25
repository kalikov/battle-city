package com.kalikov.game

import java.time.Clock

class PlayerTankFactory(
    private val eventManager: EventManager,
    private val imageManager: ImageManager,
    private val pauseManager: PauseManager,
    private val spriteContainer: SpriteContainer,
    private val appearPosition: Point,
    private val clock: Clock
) : EventSubscriber {
    data class PlayerTankCreated(val tank: Tank) : Event()

    private companion object {
        private val subscriptions = setOf(TankExplosion.Destroyed::class, Player.OutOfLives::class)
    }

    private var gameOver = false

    var playerTank: Tank? = null
        private set

    init {
        eventManager.addSubscriber(this, subscriptions)

        playerTank = create()
    }

    override fun notify(event: Event) {
        if (playerTankExplosionDestroyed(event)) {
            playerTank?.dispose()
            playerTank = null
            if (!gameOver) {
                val tank = create()
                playerTank = tank
            }
        } else if (event is Player.OutOfLives) {
            gameOver = true
        }
    }

    private fun create(): Tank {
        val tank = Tank(eventManager, pauseManager, imageManager, clock, appearPosition.x, appearPosition.y)
        spriteContainer.addSprite(tank)
        tank.state = TankStateAppearing(eventManager, imageManager, tank)
        eventManager.fireEvent(PlayerTankCreated(tank))
        return tank
    }

    private fun playerTankExplosionDestroyed(event: Event): Boolean {
        if (event !is TankExplosion.Destroyed) {
            return false
        }
        val tank = event.explosion.tank
        return tank.isPlayer()
    }

    fun dispose() {
        playerTank?.dispose()

        eventManager.removeSubscriber(this, subscriptions)
    }
}