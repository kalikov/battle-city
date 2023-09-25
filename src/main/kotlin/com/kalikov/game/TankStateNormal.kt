package com.kalikov.game

open class TankStateNormal(
    imageManager: ImageManager,
    private val tank: Tank,
    flashDuration: Int = 200
) : TankState {
    var isBright = true
        private set

    override val canMove get() = true
    override val canShoot get() = true
    override val canBeDestroyed get() = true

    override val isCollidable get() = true

    private val trackFrames = arrayOf(1, 2)

    private val flashTimer = BasicTimer(tank.clock, flashDuration, ::flashed)
    private val image = imageManager.getImage(if (tank.isPlayer()) "tank_player" else "tank_enemy")

    init {
        flashTimer.restart()
    }

    override fun update() {
        flashTimer.update()
        tank.updateColor()
    }

    override fun draw(surface: ScreenSurface) {
        val column = 2 * tank.direction.index + getTrackFrame() - 1
        val row = if (tank.isPlayer()) {
            tank.upgradeLevel
        } else {
            val typeOffset = 2 * (tank.enemyType?.index ?: 0)
            if (tank.isFlashing && isBright) {
                typeOffset + 1
            } else {
                typeOffset + if (tank.color.getColor() > 0) tank.color.getColor() + 1 else 0
            }
        }
        surface.draw(tank.x, tank.y, image, column * tank.width, row * tank.height, tank.width, tank.height)
    }

    private fun getTrackFrame(): Int {
        val index = tank.moveDistance % trackFrames.size
        return trackFrames[index]
    }

    private fun flashed(count: Int) {
        if (count % 2 != 0) {
            isBright = !isBright
        }
    }
}