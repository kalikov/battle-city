package com.kalikov.game

import java.util.EnumSet

class PlayerTankFactory(
    private val game: Game,
    private val pauseManager: PauseManager,
    private val spriteContainer: SpriteContainer,
    val appearPosition: PixelPoint,
    val player: Player,
    private val options: EnumSet<PlayerTankOption> = EnumSet.noneOf(PlayerTankOption::class.java)
) : EventSubscriber {
    data class PlayerTankCreated(val tank: PlayerTankHandle) : Event()

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
            appearPosition.x,
            appearPosition.y,
            player,
            options,
        )
        spriteContainer.addSprite(tank)
        tank.state = TankStateAppearing(game, tank, 48)
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