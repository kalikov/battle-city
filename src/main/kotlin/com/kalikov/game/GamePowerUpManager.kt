package com.kalikov.game

import com.kalikov.engine.Event
import com.kalikov.engine.EventSubscriber
import com.kalikov.engine.ScreenSurface
import kotlin.random.Random

class GamePowerUpManager(
    private val game: BattleCityGame,
    private val pauseManager: PauseManager,
    private val enemyTanksManager: EnemyTanksManager,
    private val bounds: PixelRect,
    private val random: Random = Random.Default,
) : PowerUpManager, EventSubscriber {
    private companion object {
        const val HELMET_DURATION = 9000

        private val subscriptions = arrayOf(
            Tank.Hit::class,
            GameEnemyTanksManager.EnemyCreated::class,
            PowerUp.Pick::class,
        )

        private val appearTable = arrayOf(
            PowerUp.Type.HELMET,
            PowerUp.Type.TIMER,
            PowerUp.Type.SHOVEL,
            PowerUp.Type.STAR,
            PowerUp.Type.GRENADE,
            PowerUp.Type.TANK,
            PowerUp.Type.GRENADE,
            PowerUp.Type.STAR
        )
    }

    override val identity get() = Globals.IDENTITY_POWER_UP_MANAGER

    override var powerUp: PowerUp? = null
        private set

    override fun activate() {
        game.eventManager.addSubscriber(this, subscriptions)
    }

    override fun deactivate() {
        powerUp?.dispose()
        powerUp = null

        game.eventManager.removeSubscriber(this, subscriptions)
    }

    fun update() {
        powerUp = powerUp?.let {
            if (it.isDestroyed) {
                game.eventManager.fireEvent(PowerUpManager.PowerUpDestroyed(it))
            }
            if (it.update()) null else it
        }
    }

    override fun notify(event: Event) {
        when (event) {
            is Tank.Hit -> {
                if (event.tank is EnemyTank && event.tank.isFlashing && !event.tank.isHit) {
                    destroyPowerUp()
                    val newPowerUp = create()
                    powerUp = newPowerUp
                    game.eventManager.fireEvent(PowerUpManager.PowerUpCreated(newPowerUp))
                }
            }

            is GameEnemyTanksManager.EnemyCreated -> {
                if (event.enemy.isFlashing) {
                    destroyPowerUp()
                }
            }

            is PowerUp.Pick -> {
                if (event.powerUp === powerUp) {
                    destroyPowerUp()
                    handle(event.powerUp.type, event.tank)
                }
            }
        }
    }

    private fun destroyPowerUp() {
        powerUp?.let {
            it.destroy()
            game.eventManager.fireEvent(PowerUpManager.PowerUpDestroyed(it))
            it.dispose()
        }
        powerUp = null
    }

    private fun create(): PowerUp {
        val position = PixelPoint(
            bounds.x + Globals.TILE_SIZE * random.nextInt((GameField.SIZE_IN_TILES - PowerUp.SIZE_IN_TILES).toInt()),
            bounds.y + Globals.TILE_SIZE * random.nextInt((GameField.SIZE_IN_TILES - PowerUp.SIZE_IN_TILES).toInt())
        )
        val powerUp = PowerUp(game, position)
        powerUp.type = appearTable.random(random)

        game.soundManager.powerUpAppear.play()

        return powerUp
    }

    override fun dispose() {
        powerUp?.dispose()
        powerUp = null
    }

    override fun draw(surface: ScreenSurface) {
        powerUp?.let {
            if (!it.isDestroyed) {
                it.draw(surface)
            }
        }
    }

    private fun handle(powerUpType: PowerUp.Type, playerTank: PlayerTank) {
        game.soundManager.powerUpPick.play()

        when (powerUpType) {
            PowerUp.Type.GRENADE -> handleGrenade()
            PowerUp.Type.HELMET -> handleHelmet(playerTank)
            PowerUp.Type.TIMER -> handleTimer()
            PowerUp.Type.SHOVEL -> handleShovel()
            PowerUp.Type.STAR -> handleStar(playerTank)
            PowerUp.Type.TANK -> handleTank(playerTank)
        }
    }

    private fun handleGrenade() {
        enemyTanksManager.forEach { tank ->
            if (tank.canBeDestroyed && !tank.isDestroyed) {
                tank.devalue()
                tank.destroy()
            }
        }
        game.soundManager.enemyExplosion.play()
    }

    private fun handleHelmet(playerTank: Tank) {
        val state = TankStateInvincible(game, pauseManager, playerTank, HELMET_DURATION)
        playerTank.state = state
    }

    private fun handleTimer() {
        game.eventManager.fireEvent(PowerUpManager.Freeze)
    }

    private fun handleShovel() {
        game.eventManager.fireEvent(PowerUpManager.ShovelStart)
    }

    private fun handleStar(playerTank: PlayerTank) {
        playerTank.upgrade()
    }

    private fun handleTank(playerTank: PlayerTank) {
        playerTank.player.incrementLife()
    }
}