package com.kalikov.game

import java.time.Clock

class Points(
    eventManager: EventManager,
    imageManager: ImageManager,
    clock: Clock,
    x: Int,
    y: Int,
    duration: Int
) : Sprite(eventManager, x, y, SIZE, SIZE) {
    companion object {
        const val SIZE = Globals.UNIT_SIZE
    }

    var value = 0

    private val timer = PauseAwareTimer(eventManager, clock, duration, ::destroy)
    private val image = imageManager.getImage("points")

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
        surface.draw(x, y, image, (value / 100 - 1) * width, 0, width, height)
    }

    override fun destroyHook() {
        timer.stop()
    }

    override fun dispose() {
        timer.dispose()

        LeaksDetector.remove(this)
    }
}