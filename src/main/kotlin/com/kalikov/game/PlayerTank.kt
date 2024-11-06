package com.kalikov.game

import java.util.EnumSet

class PlayerTank private constructor(
    game: Game,
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
            game: Game,
            pauseManager: PauseManager,
            x: Pixel,
            y: Pixel,
            player: Player,
            options: EnumSet<PlayerTankOption> = EnumSet.noneOf(PlayerTankOption::class.java)
        ) = init(PlayerTank(game, pauseManager, x, y, player, options))
    }

    data class PlayerDestroyed(val tank: PlayerTank) : Event()
    data class PlayerMoved(val tank: PlayerTank) : Event()

    var upgradeLevel = 0
        private set

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

    override fun stateAppearingEnd() {
        state = TankStateInvincible(game, this)
        direction = Direction.UP
    }

    override fun updateHook() {
        super.updateHook()
        if (shooting) {
            shoot()
        }
    }

    override fun destroyHook() {
        super.destroyHook()

        game.eventManager.fireEvent(PlayerDestroyed(this))

        game.soundManager.playerExplosion.play()
    }

    override fun hitHook(bullet: BulletHandle) {
        if (bullet.tank is PlayerTank) {
            if (!options.contains(PlayerTankOption.FRIENDLY_FIRE_INVINCIBLE)) {
                if (state is TankStateFrozen) {
                    (state as TankStateFrozen).restartTimer()
                } else {
                    state = TankStateFrozen(game.eventManager, game.imageManager, this, game.clock)
                    isIdle = true
                }
            }
        } else {
            destroy()
        }
    }

    override fun hitRectHook() {
        super.hitRectHook()

        game.eventManager.fireEvent(PlayerMoved(this))
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

        when (upgradeLevel) {
            1 -> bulletSpeed = Bullet.Speed.FAST
            2 -> bulletsLimit = 2
            3 -> bulletType = Bullet.Type.ENHANCED
        }
    }
}