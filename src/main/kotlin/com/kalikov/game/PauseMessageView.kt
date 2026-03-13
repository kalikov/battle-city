package com.kalikov.game

import com.kalikov.engine.ARGB
import com.kalikov.engine.BlinkTimer
import com.kalikov.engine.Pixel
import com.kalikov.engine.ScreenSurface
import java.time.Clock

class PauseMessageView(
    private val pauseManager: PauseManager,
    private val x: Pixel,
    private val y: Pixel,
    clock: Clock
) {
    internal companion object {
        internal const val INTERVAL = 300

        private const val MESSAGE = "PAUSE"

        private val DX = (-MESSAGE.length).tiles.toPixel() / 2 + 1
    }

    private val blinkTimer = BlinkTimer(clock, INTERVAL)

    fun update() {
        if (pauseManager.isPaused && blinkTimer.isStopped) {
            blinkTimer.restart()
        } else if (!pauseManager.isPaused && !blinkTimer.isStopped) {
            blinkTimer.stop()
        }
        blinkTimer.update()
    }

    fun draw(surface: ScreenSurface) {
        if (blinkTimer.isStopped || !blinkTimer.isOpaque) {
            return
        }
        surface.fillText(MESSAGE, x + DX, y + 1.tiles.toPixel(), ARGB.rgb(0xe44437), Globals.FONT_REGULAR)
    }
}