package com.kalikov.game

import com.kalikov.engine.Animation
import com.kalikov.engine.Event
import com.kalikov.engine.ScreenSurface
import com.kalikov.engine.frameLoopOf
import com.kalikov.engine.px

class TankStateInvincible(
    private val game: BattleCityGame,
    pauseManager: PauseManager,
    private val tank: Tank,
    shieldDuration: Int = 3000
) : TankStateNormal(game.imageManager, tank) {
    private companion object {
        private val animationFrames = intArrayOf(1, 2)
    }

    data class End(val tank: Tank) : Event()

    override val canBeDestroyed get() = false

    private val shieldAnimation = Animation.pauseAware(pauseManager, frameLoopOf(*animationFrames), game.clock, 32)

    private val shieldTimer = PauseAwareTimer(pauseManager, game.clock, shieldDuration, ::end)

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
        shieldAnimation.stop()
        shieldTimer.stop()
        super.dispose()
    }

    private fun end() {
        shieldTimer.stop()
        game.eventManager.fireEvent(End(tank))
    }
}