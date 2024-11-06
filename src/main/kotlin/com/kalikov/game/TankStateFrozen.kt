package com.kalikov.game

import java.time.Clock

class TankStateFrozen(
    private val eventManager: EventManager,
    imageManager: ImageManager,
    private val tank: Tank,
    clock: Clock,
) : TankStateNormal(imageManager, tank) {
    private companion object {
        private const val BLINK_INTERVAL = 128
        private const val BLINK_COUNT = 17
    }

    data class End(val tank: Tank) : Event()

    private val blinkTimer = BlinkTimer(clock, BLINK_INTERVAL)
    private val timeoutTimer = PauseAwareTimer(eventManager, clock, 2 * BLINK_INTERVAL * BLINK_COUNT, ::onTimerEnd)

    override val canMove get() = false

    fun restartTimer() {
        timeoutTimer.restart()
    }

    override fun update() {
        super.update()

        if (timeoutTimer.isStopped) {
            timeoutTimer.restart()
        }
        timeoutTimer.update()
        if (blinkTimer.isStopped) {
            blinkTimer.restart()
        }
        blinkTimer.update()
    }

    override fun draw(surface: ScreenSurface) {
        if (blinkTimer.isOpaque) {
            super.draw(surface)
        }
    }

    private fun onTimerEnd() {
        timeoutTimer.stop()
        blinkTimer.stop()
        eventManager.fireEvent(End(tank))
    }

    override fun dispose() {
        super.dispose()

        timeoutTimer.dispose()
    }
}