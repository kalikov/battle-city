package com.kalikov.game

import java.time.Clock

class Points(
    private val eventManager: EventManager,
    imageManager: ImageManager,
    clock: Clock,
    x: Int = 0,
    y: Int = 0,
    duration: Int = 20
) : Sprite(eventManager, x, y, Globals.UNIT_SIZE, Globals.UNIT_SIZE) {
    enum class Type {
        TANK,
        POWERUP
    }

    var value = 0
    var type = Type.TANK

    private val timer = PauseAwareTimer(eventManager, clock, duration, ::destroy)
    private val image = imageManager.getImage("points")

    data class Destroyed(val points: Points) : Event()

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

        eventManager.fireEvent(Destroyed(this))
    }

    override fun dispose() {
        timer.dispose()

        LeaksDetector.remove(this)
    }
}