package com.kalikov.game

import com.kalikov.engine.LeaksDetector
import com.kalikov.engine.Pixel
import com.kalikov.engine.ScreenSurface
import com.kalikov.engine.px
import com.kalikov.engine.times

class Points(
    game: BattleCityGame,
    pauseManager: PauseManager,
    val value: Int,
    x: Pixel,
    y: Pixel,
    duration: Int
) : Sprite(game.eventManager, x, y, SIZE, SIZE) {
    companion object {
        val SIZE = t(2).toPixel()
    }

    private val timer = PauseAwareTimer(pauseManager, game.clock, duration, ::destroy)
    private val image = game.imageManager.getImage("points")

    init {
        LeaksDetector.add(this)

        z = 1000
    }

    override fun updateHook() {
        updateTimer()
    }

    private fun updateTimer() {
        if (timer.isStopped) {
            timer.restart()
        }
        timer.update()
    }

    override fun draw(surface: ScreenSurface) {
        surface.draw(x, y, image, (value / 100 - 1) * width, px(0), width, height)
    }

    override fun destroyHook() {
        timer.stop()
    }

    override fun dispose() {
        timer.stop()

        LeaksDetector.remove(this)
    }
}