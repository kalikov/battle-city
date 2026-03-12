package com.kalikov.game

import com.kalikov.engine.Pixel

class EnemyTank private constructor(
    game: BattleCityGame,
    pauseManager: PauseManager,
    x: Pixel,
    y: Pixel,
    val enemyType: EnemyType,
    val isFlashing: Boolean = false,
) : Tank(
    game,
    pauseManager,
    x,
    y
) {
    companion object {
        fun create(
            game: BattleCityGame,
            pauseManager: PauseManager,
            x: Pixel,
            y: Pixel,
            enemyType: EnemyType,
            isFlashing: Boolean = false,
        ) = init(EnemyTank(game, pauseManager, x, y, enemyType, isFlashing))

        private val FLASHING_COLORS = intArrayOf(0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 1, 1, 1, 1, 1)
        private val ARMOR_COLORS = arrayOf(intArrayOf(0, 2), intArrayOf(0, 3), intArrayOf(2, 3), intArrayOf(0))
        private val FLASHING_ARMOR_COLORS = ARMOR_COLORS.copyOf()

        init {
            FLASHING_ARMOR_COLORS[0] = FLASHING_COLORS
        }
    }

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

    var isValued = true
        private set
    val value: Int get() = if (isValued) this.enemyType.score else 0

    var hitLimit = 1

    private var hit = 0
    val isHit get() = hit > 0

    private val color = TankColor(game.clock)

    init {
        if (enemyType == EnemyType.ARMOR) {
            color.colors = if (isFlashing) FLASHING_ARMOR_COLORS else ARMOR_COLORS
        } else if (isFlashing) {
            color.colors[0] = FLASHING_COLORS
        }
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
        if (hit < hitLimit) {
            game.soundManager.bulletHitEnemy.play()
        } else {
            destroy()
            val bulletTank = bullet.tank
            if (bulletTank is PlayerTank) {
                bulletTank.player.score(value)
                bulletTank.player.stageScore.increment(this)
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