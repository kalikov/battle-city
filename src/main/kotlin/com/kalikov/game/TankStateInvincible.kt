package com.kalikov.game

class TankStateInvincible(
    private val game: Game,
    private val tank: Tank,
    shieldDuration: Int = 3000
) : TankStateNormal(game.imageManager, tank) {
    private companion object {
        private val animationFrames = intArrayOf(1, 2)
    }

    data class End(val tank: Tank) : Event()

    override val canBeDestroyed get() = false

    private val shieldAnimation = Animation.pauseAware(game.eventManager, frameLoopOf(*animationFrames), game.clock, 32)

    private val shieldTimer = PauseAwareTimer(game.eventManager, game.clock, shieldDuration, ::end)

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
            game.imageManager.getImage("shield"),
            tank.width * (shieldAnimation.frame - 1),
            px(0),
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
        game.eventManager.fireEvent(End(tank))
    }
}