package com.kalikov.game

import java.time.Clock

class PauseMessageView(
    private val eventManager: EventManager,
    private val x: Pixel,
    private val y: Pixel,
    clock: Clock
) : EventSubscriber {
    private companion object {
        private const val MESSAGE = "PAUSE"

        private val DX = t(-MESSAGE.length).toPixel() / 2 + 1

        private val subscriptions = setOf(PauseManager.Start::class, PauseManager.End::class)
    }

    private val blinkTimer = BlinkTimer(clock, 300)

    init {
        eventManager.addSubscriber(this, subscriptions)
    }

    override fun notify(event: Event) {
        if (event is PauseManager.Start) {
            blinkTimer.restart()
        } else if (event is PauseManager.End) {
            blinkTimer.stop()
        }
    }

    fun update() {
        blinkTimer.update()
    }

    fun draw(surface: ScreenSurface) {
        if (blinkTimer.isStopped || !blinkTimer.isOpaque) {
            return
        }
        surface.fillText(MESSAGE, x + DX, y + t(1).toPixel(), ARGB.rgb(0xe44437), Globals.FONT_REGULAR)
    }

    fun dispose() {
        eventManager.removeSubscriber(this, subscriptions)
    }
}