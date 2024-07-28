package com.kalikov.game

import java.time.Clock

class EnemyTank private constructor(
    eventManager: EventManager,
    pauseManager: PauseManager,
    imageManager: ImageManager,
    clock: Clock,
    x: Int,
    y: Int,
    val enemyType: EnemyType,
) : Tank(
    eventManager,
    pauseManager,
    imageManager,
    clock,
    x,
    y
) {
    companion object {
        fun create(
            eventManager: EventManager,
            pauseManager: PauseManager,
            imageManager: ImageManager,
            clock: Clock,
            x: Int,
            y: Int,
            enemyType: EnemyType,
        ) = init(EnemyTank(eventManager, pauseManager, imageManager, clock, x, y, enemyType))
    }

    data class Score(val tank: EnemyTank, val player: Player) : Event()

    enum class EnemyType(val score: Int, val index: Int) {
        BASIC(100, 0),
        FAST(200, 1),
        POWER(300, 2),
        ARMOR(400, 3)
    }

    override val image = "tank_enemy"
    override val imageMod: Int
        get() {
            val typeOffset = 2 * enemyType.index
            return typeOffset + color.getColor()
        }

    private var isValued = true
    val value: Int get() = if (isValued) this.enemyType.score else 0

    var color = TankColor(clock)

    var hitLimit = 1
    private var hit = 0

    val isHit get() = hit > 0

    override fun stateAppearingEnd() {
        state = TankStateNormal(imageManager, this)
        direction = Direction.DOWN
    }

    override fun updateHook() {
        super.updateHook()

        if (state is TankStateNormal) {
            updateColor()
        }
    }

    override fun hitHook(bullet: BulletHandle) {
        hit++
        color.hit()
        if (hit >= hitLimit) {
            destroy()
            val bulletTank = bullet.tank
            if (bulletTank is PlayerTank) {
                eventManager.fireEvent(Player.Score(bulletTank.player, this.value))
                eventManager.fireEvent(Score(this, bulletTank.player))
            }
        }
    }

    private fun updateColor() {
        color.update()
    }

    fun devalue() {
        isValued = false
    }
}