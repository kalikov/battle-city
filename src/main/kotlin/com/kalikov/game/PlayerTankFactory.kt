package com.kalikov.game

import java.time.Clock

class PlayerTankFactory(
    private val game: Game,
    private val pauseManager: PauseManager,
    private val spriteContainer: SpriteContainer,
    private val appearPosition: Point,
    private val clock: Clock,
    val player: Player,
) : EventSubscriber {
    data class PlayerTankCreated(val tank: PlayerTank) : Event()

    private companion object {
        private val subscriptions = setOf(TankExplosion.Destroyed::class, Player.OutOfLives::class)
    }

    private var outOfLives = true

    var playerTank: PlayerTank? = null
        private set

    init {
        game.eventManager.addSubscriber(this, subscriptions)
    }

    fun init(upgradeLevel: Int) {
        outOfLives = false
        check(playerTank == null)
        val tank = create()
        for (i in 1..upgradeLevel) {
            tank.upgrade()
        }
        playerTank = tank
    }

    override fun notify(event: Event) {
        if (playerTankExplosionDestroyed(event)) {
            playerTank?.dispose()
            playerTank = null
            if (!outOfLives) {
                val tank = create()
                playerTank = tank
            }
        } else if (event is Player.OutOfLives && event.player === player) {
            outOfLives = true
        }
    }

    private fun create(): PlayerTank {
        val tank = PlayerTank.create(
            game,
            pauseManager,
            clock,
            appearPosition.x,
            appearPosition.y,
            player,
        )
        spriteContainer.addSprite(tank)
        tank.state = TankStateAppearing(game.eventManager, game.imageManager, tank, 48)
        game.eventManager.fireEvent(PlayerTankCreated(tank))
        return tank
    }

    private fun playerTankExplosionDestroyed(event: Event): Boolean {
        if (event !is TankExplosion.Destroyed) {
            return false
        }
        val tank = event.explosion.tank
        return tank is PlayerTank && tank.player === player
    }

    fun dispose() {
        playerTank?.dispose()

        game.eventManager.removeSubscriber(this, subscriptions)
    }
}