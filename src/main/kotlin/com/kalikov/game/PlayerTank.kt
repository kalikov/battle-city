package com.kalikov.game

import java.time.Clock

class PlayerTank private constructor(
    eventManager: EventManager,
    pauseManager: PauseManager,
    imageManager: ImageManager,
    clock: Clock,
    x: Int,
    y: Int,
    override val player: Player,
) : Tank(
    eventManager,
    pauseManager,
    imageManager,
    clock,
    x,
    y
), PlayerTankHandle {
    companion object {
        fun create(
            eventManager: EventManager,
            pauseManager: PauseManager,
            imageManager: ImageManager,
            clock: Clock,
            x: Int,
            y: Int,
            player: Player,
        ) = init(PlayerTank(eventManager, pauseManager, imageManager, clock, x, y, player))
    }

    data class PlayerDestroyed(val tank: PlayerTank) : Event()

    var upgradeLevel = 0
        private set

    private var shooting = false

    override val image = "tank_player${player.index + 1}"
    override val imageMod get() = upgradeLevel

    override fun stateAppearingEnd() {
        state = TankStateInvincible(eventManager, imageManager, this)
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

        eventManager.fireEvent(PlayerDestroyed(this))
    }

    override fun hitHook(bullet: BulletHandle) {
        if (bullet.tank is PlayerTank) {
            if (state is TankStateFrozen) {
                (state as TankStateFrozen).restartTimer()
            } else {
                state = TankStateFrozen(eventManager, imageManager, this, clock)
                isIdle = true
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

        when (upgradeLevel) {
            1 -> bulletSpeed = Bullet.Speed.FAST
            2 -> bulletsLimit = 2
            3 -> bulletType = Bullet.Type.ENHANCED
        }
    }
}