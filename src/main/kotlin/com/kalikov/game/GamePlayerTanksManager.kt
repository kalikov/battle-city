package com.kalikov.game

import com.kalikov.engine.Event
import com.kalikov.engine.EventSubscriber
import com.kalikov.engine.ScreenSurface
import com.kalikov.game.Tank.Destroyed
import java.util.EnumSet
import kotlin.reflect.KClass

class GamePlayerTanksManager(
    private val game: BattleCityGame,
    private val pauseManager: PauseManager,
    private val players: List<Player>,
    private val gameFieldBounds: PixelRect,
    private val options: EnumSet<PlayerTankOption> = EnumSet.noneOf(PlayerTankOption::class.java)
) : PlayerTanksManager, EventSubscriber {
    data class PlayerTankCreated(val tank: PlayerTank) : Event()

    private companion object {
        private val subscriptions = arrayOf<KClass<out Event>>(
            TankExplosion.Destroyed::class,
            TankStateAppearing.End::class,
            TankStateInvincible.End::class,
            TankStateFrozen.End::class,
        )
    }

    override val identity get() = Globals.IDENTITY_PLAYERS_MANAGER

    private val entries = Array<Entry>(players.size) { Entry() }

    fun activate() {
        game.eventManager.addSubscriber(this, subscriptions)

        players.forEach { player ->
            if (player.lives > 0) {
                val entry = entries[player.index]
                entry.playerTank = entry.playerTank?.let { tank ->
                    tank.reset()
                    val position = game.stageManager.stageMap.playerSpawnPoints[player.index]
                    tank.setPosition(position.x.toPixel() + gameFieldBounds.x, position.y.toPixel() + gameFieldBounds.y)
                    tank.state = TankStateAppearing(game, pauseManager, tank, 48)
                    tank.upgradeLevel = player.upgradeLevel
                    tank
                } ?: run {
                    val tank = create(player)
                    tank.upgradeLevel = player.upgradeLevel
                    tank
                }
            }
        }
    }

    fun deactivate() {
        game.eventManager.removeSubscriber(this, subscriptions)
    }

    override fun update() {
        entries.forEach { entry ->
            entry.playerTank?.let {
                if (it.isDestroyed) {
                    game.eventManager.fireEvent(Destroyed(it))

                    it.player.die()

                    game.soundManager.playerExplosion.play()
                }
                if (it.update()) {
                    entry.playerTank = null
                }
            }
        }
    }

    override fun draw(surface: ScreenSurface) {
        entries.forEach { entry ->
            entry.playerTank?.let {
                if (!it.isDestroyed) {
                    it.draw(surface)
                }
            }
        }
    }

    override fun forEach(action: (PlayerTank) -> Unit) {
        for (entry in entries) {
            entry.playerTank?.let { action(it) }
        }
    }

    override fun iterateWhile(predicate: (PlayerTank) -> Boolean): Boolean {
        for (entry in entries) {
            entry.playerTank?.let {
                if (!predicate(it)) {
                    return false
                }
            }
        }
        return true
    }

    override fun getTank(player: Player): PlayerTankHandle? {
        return entries[player.index].playerTank
    }

    override fun notify(event: Event) {
        if (event is TankExplosion.Destroyed) {
            val tank = event.explosion.tank
            if (tank is PlayerTank) {
                val entry = entries[tank.player.index]
                entry.playerTank?.dispose()
                entry.playerTank = null
                if (tank.player.lives > 0) {
                    val tank = create(tank.player)
                    entry.playerTank = tank
                }
            }
        } else if (event is TankStateAppearing.End && event.tank is PlayerTank) {
            event.tank.state = TankStateInvincible(game, pauseManager, event.tank)
            event.tank.direction = Direction.UP
        } else if (event is TankStateInvincible.End && event.tank is PlayerTank) {
            event.tank.state = TankStateNormal(game.imageManager, event.tank)
        } else if (event is TankStateFrozen.End && event.tank is PlayerTank) {
            event.tank.state = TankStateNormal(game.imageManager, event.tank)
        }
    }

    private fun create(player: Player): PlayerTank {
        val position = game.stageManager.stageMap.playerSpawnPoints[player.index]
        val tank = PlayerTank.create(
            game,
            pauseManager,
            position.x.toPixel() + gameFieldBounds.x,
            position.y.toPixel() + gameFieldBounds.y,
            player,
            options,
        )
        tank.state = TankStateAppearing(game, pauseManager, tank, 48)
        game.eventManager.fireEvent(PlayerTankCreated(tank))
        return tank
    }

    fun dispose() {
        entries.forEach {
            it.playerTank?.dispose()
            it.playerTank = null
        }
    }

    private data class Entry(
        var playerTank: PlayerTank? = null,
    )
}