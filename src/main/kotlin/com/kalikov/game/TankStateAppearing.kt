package com.kalikov.game

class TankStateAppearing(
    private val eventManager: EventManager,
    imageManager: ImageManager,
    private val tank: Tank,
) : TankState {
    companion object {
        private const val ANIMATION_INTERVAL = 64

        private val animationFrameSequence = frameSequenceOf(1, 2, 3, 4, 3, 2, 1, 2, 3, 4, 3, 2, 1)

        val ANIMATION_DURATION = animationFrameSequence.size * ANIMATION_INTERVAL
    }

    data class End(val tank: Tank) : Event()

    override val canMove get() = false
    override val canShoot get() = false
    override val canBeDestroyed get() = false

    override val isCollidable get() = false

    private val animation = Animation.pauseAware(eventManager, animationFrameSequence, tank.clock, ANIMATION_INTERVAL)
    private val image = imageManager.getImage("appear")

    override fun update() {
        if (!animation.isRunning) {
            animation.restart()
        }
        animation.update()
        if (animation.isCompleted) {
            eventManager.fireEvent(End(tank))
        }
    }

    override fun draw(surface: ScreenSurface) {
        surface.draw(tank.x, tank.y, image, tank.width * (animation.frame - 1), 0, tank.width, tank.height)
    }
}