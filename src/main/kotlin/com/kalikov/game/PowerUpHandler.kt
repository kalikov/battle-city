package com.kalikov.game

class PowerUpHandler(
    private val game: Game,
) : EventSubscriber {
    data class Life(val player: Player) : Event()
    data object Freeze : Event()
    data object ShovelStart : Event()

    private companion object {
        const val HELMET_DURATION = 9000

        private val subscriptions = setOf(
            PowerUp.Pick::class,
            EnemyFactory.EnemyCreated::class,
            Tank.Destroyed::class,
        )
    }

    private val enemies = HashSet<EnemyTank>()

    init {
        game.eventManager.addSubscriber(this, subscriptions)
    }

    override fun notify(event: Event) {
        if (event is PowerUp.Pick) {
            handle(event.powerUp, event.tank)
        } else if (event is EnemyFactory.EnemyCreated) {
            enemies.add(event.enemy)
        } else if (event is Tank.Destroyed && event.tank is EnemyTank) {
            enemies.remove(event.tank)
        }
    }

    private fun handle(powerUp: PowerUp, playerTank: PlayerTank) {
        game.soundManager.powerUpPick.play()

        when (powerUp.type) {
            PowerUp.Type.GRENADE -> handleGrenade()
            PowerUp.Type.HELMET -> handleHelmet(playerTank)
            PowerUp.Type.TIMER -> handleTimer()
            PowerUp.Type.SHOVEL -> handleShovel()
            PowerUp.Type.STAR -> handleStar(playerTank)
            PowerUp.Type.TANK -> handleTank(playerTank)
        }
    }

    private fun handleGrenade() {
        enemies.forEach { tank ->
            if (tank.canBeDestroyed && !tank.isDestroyed) {
                tank.devalue()
                tank.destroy()
            }
        }
        game.soundManager.enemyExplosion.play()
    }

    private fun handleHelmet(playerTank: Tank) {
        val state = TankStateInvincible(game, playerTank, HELMET_DURATION)
        playerTank.state = state
    }

    private fun handleTimer() {
        game.eventManager.fireEvent(Freeze)
    }

    private fun handleShovel() {
        game.eventManager.fireEvent(ShovelStart)
    }

    private fun handleStar(playerTank: PlayerTank) {
        playerTank.upgrade()
    }

    private fun handleTank(playerTank: PlayerTank) {
        game.eventManager.fireEvent(Life(playerTank.player))
    }

    fun dispose() {
        game.eventManager.removeSubscriber(this, subscriptions)
    }
}