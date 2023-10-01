package com.kalikov.game

import java.time.Clock

class PauseMessageView(
    private val eventManager: EventManager,
    private val x: Int,
    private val y: Int,
    clock: Clock
) : EventSubscriber {
    private companion object {
        private val subscriptions = setOf(PauseManager.Start::class, PauseManager.End::class)

        private val message = "PAUSE"
        private val dx = -(Globals.TILE_SIZE * message.length) / 2 + 1
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
        surface.fillText(message, x + dx, y + Globals.TILE_SIZE, ARGB.rgb(0xe44437), Globals.FONT_REGULAR)
    }

    fun dispose() {
        eventManager.removeSubscriber(this, subscriptions)
    }
}