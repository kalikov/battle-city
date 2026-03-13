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
) : AbstractSprite(x, y, SIZE, SIZE) {
    companion object {
        val SIZE = 2.tiles.toPixel()
    }

    private val timer = PauseAwareTimer(pauseManager, game.clock, duration, ::destroy)
    private val image = game.imageManager.getImage("points")

    init {
        LeaksDetector.add(this)
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
        surface.draw(x, y, image, (value / 100 - 1) * width, 0.px, width, height)
    }

    override fun dispose() {
        timer.stop()

        LeaksDetector.remove(this)
    }
}