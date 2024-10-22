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

    private var shooting = false

    override val image = "tank_player${player.index + 1}"
    override val imageMod get() = upgradeLevel

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