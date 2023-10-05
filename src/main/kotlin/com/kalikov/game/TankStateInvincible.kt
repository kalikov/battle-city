package com.kalikov.game

class TankStateInvincible(
    private val eventManager: EventManager,
    private val imageManager: ImageManager,
    private val tank: Tank,
    shieldDuration: Int = 3000
) : TankStateNormal(imageManager, tank) {
    private companion object {
        private val animationFrames = intArrayOf(1, 2)
    }

    data class End(val tank: Tank) : Event()

    override val canBeDestroyed get() = false

    private val shieldAnimation = Animation.pauseAware(eventManager, frameLoopOf(*animationFrames), tank.clock, 32)

    private val shieldTimer = PauseAwareTimer(eventManager, tank.clock, shieldDuration, ::end)

    override fun update() {
        super.update()
        if (!shieldAnimation.isRunning) {
            shieldAnimation.restart()
        }
        shieldAnimation.update()

        if (shieldTimer.isStopped) {
            shieldTimer.restart()
        }
        shieldTimer.update()
    }

    override fun draw(surface: ScreenSurface) {
        super.draw(surface)
        surface.draw(
            tank.x,
            tank.y,
            imageManager.getImage("shield"),
            tank.width * (shieldAnimation.frame - 1),
            0,
            tank.width,
            tank.height
        )
    }

    override fun dispose() {
        shieldAnimation.dispose()
        shieldTimer.dispose()
        super.dispose()
    }

    private fun end() {
        shieldTimer.stop()
        eventManager.fireEvent(End(tank))
    }
}