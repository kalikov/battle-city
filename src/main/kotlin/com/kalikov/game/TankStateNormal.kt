package com.kalikov.game

open class TankStateNormal(
    imageManager: ImageManager,
    private val tank: Tank
) : TankState {
    override val canMove get() = true
    override val canShoot get() = true
    override val canBeDestroyed get() = true

    override val isCollidable get() = true

    private val trackFrames = arrayOf(1, 2)

    private val image = imageManager.getImage(tank.image)

    override fun update() {
    }

    override fun draw(surface: ScreenSurface) {
        val column = 2 * tank.direction.index + getTrackFrame() - 1
        val row = tank.imageMod
        surface.draw(tank.x, tank.y, image, column * tank.width, row * tank.height, tank.width, tank.height)
    }

    override fun dispose() {
    }

    private fun getTrackFrame(): Int {
        val index = tank.moveDistance % trackFrames.size
        return trackFrames[index]
    }
}