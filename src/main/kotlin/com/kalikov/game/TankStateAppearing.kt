package com.kalikov.game

class TankStateAppearing(
    private val game: Game,
    private val tank: Tank,
    animationInterval: Int = DEFAULT_ANIMATION_INTERVAL
) : TankState {
    companion object {
        private const val DEFAULT_ANIMATION_INTERVAL = 64

        private val animationFrames = intArrayOf(1, 2, 3, 4, 3, 2, 1, 2, 3, 4, 3, 2, 1)

        val DEFAULT_ANIMATION_DURATION = animationFrames.size * DEFAULT_ANIMATION_INTERVAL
    }

    data class End(val tank: Tank) : Event()

    override val canMove get() = false
    override val canShoot get() = false
    override val canBeDestroyed get() = false

    override val isCollidable get() = false

    private val animation = Animation.pauseAware(
        game.eventManager,
        frameSequenceOf(*animationFrames),
        game.clock,
        animationInterval
    )
    private val image = game.imageManager.getImage("appear")

    override fun update() {
        if (!animation.isRunning) {
            animation.restart()
        }
        animation.update()
        if (animation.isCompleted) {
            game.eventManager.fireEvent(End(tank))
        }
    }

    override fun draw(surface: ScreenSurface) {
        surface.draw(tank.x, tank.y, image, tank.width * (animation.frame - 1), px(0), tank.width, tank.height)
    }

    override fun dispose() {
        animation.dispose()
    }
}