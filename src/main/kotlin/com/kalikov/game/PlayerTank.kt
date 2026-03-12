package com.kalikov.game

import com.kalikov.engine.CountDown
import com.kalikov.engine.Event
import com.kalikov.engine.Pixel
import java.util.EnumSet
import kotlin.math.max
import kotlin.math.min

class PlayerTank private constructor(
    game: BattleCityGame,
    pauseManager: PauseManager,
    x: Pixel,
    y: Pixel,
    override val player: Player,
    private val options: EnumSet<PlayerTankOption> = EnumSet.noneOf(PlayerTankOption::class.java)
) : Tank(
    game,
    pauseManager,
    x,
    y
), PlayerTankHandle {
    companion object {
        fun create(
            game: BattleCityGame,
            pauseManager: PauseManager,
            x: Pixel,
            y: Pixel,
            player: Player,
            options: EnumSet<PlayerTankOption> = EnumSet.noneOf(PlayerTankOption::class.java)
        ) = init(PlayerTank(game, pauseManager, x, y, player, options))
    }

    var upgradeLevel = 0
        set(value) {
            bulletSpeed = if (value >= 1) Bullet.Speed.FAST else Bullet.Speed.NORMAL
            bulletsLimit = if (value >= 2) 2 else 1
            bulletType = if (value >= 3) Bullet.Type.ENHANCED else Bullet.Type.REGULAR
            field = max(0, min(3, value))
        }

    val isSlipping get() = !slipCountDown.isStopped

    var slipDuration = 28
        set(value) {
            field = value
            slipCountDown = CountDown(value)
        }

    private var shooting = false

    override val image = "tank_player${player.index + 1}"
    override val imageMod get() = upgradeLevel

    private var slipCountDown = CountDown(slipDuration)

    private var slipped = false

    fun startSlipping() {
        if (slipCountDown.isStopped) {
            slipped = false
            slipCountDown.restart()
        }
    }

    fun stopSlipping() {
        slipCountDown.stop()
    }

    override fun canChangeDirection(target: Direction): Boolean {
        return (slipCountDown.isStopped || !isSmoothTurnRequired(target)) && super.canChangeDirection(target)
    }

    override fun moveHook(moved: Boolean) {
        if (!moved) {
            slipCountDown.stop()
        } else {
            slipCountDown.update()
            if (!slipCountDown.isStopped && !slipped && isIdle) {
                game.soundManager.slip.play()
                slipped = true
            }
        }
    }

    private fun isSmoothTurnRequired(newDirection: Direction): Boolean {
        val prevDirection = direction
        return if (newDirection.isVertical) {
            prevDirection.isHorizontal && (x % turnRoundTo) > 0
        } else {
            prevDirection.isVertical && (y % turnRoundTo) > 0
        }
    }

    override fun updateHook() {
        super.updateHook()
        if (shooting) {
            shoot()
        }
    }

    override fun hitHook(bullet: BulletHandle) {
        if (bullet.tank is PlayerTank) {
            if (!options.contains(PlayerTankOption.FRIENDLY_FIRE_INVINCIBLE)) {
                if (state is TankStateFrozen) {
                    (state as TankStateFrozen).restartTimer()
                } else {
                    state = TankStateFrozen(game.eventManager, pauseManager, game.imageManager, this, game.clock)
                    isIdle = true
                }
            }
        } else {
            destroy()
        }
    }

    override fun startShooting() {
        shooting = true
        shoot()
    }

    override fun stopShooting() {
        shooting = false
    }

    fun upgrade() {
        if (upgradeLevel == 3) {
            return
        }
        upgradeLevel++
    }
}