package com.kalikov.game

import java.time.Clock

class Cursor(
    eventRouter: EventRouter,
    imageManager: ImageManager,
    private val builder: Builder,
    clock: Clock,
    x: Int = 0,
    y: Int = 0
) : Sprite(eventRouter, x, y, Globals.UNIT_SIZE, Globals.UNIT_SIZE) {
    private val blinkTimer = BlinkTimer(clock, 320)

    private val image = imageManager.getImage("tank_player")

    init {
        z = 10000
    }

    override fun draw(surface: ScreenSurface) {
        if (blinkTimer.isOpaque) {
            surface.draw(x, y, image, Direction.UP.index * width, 0, width, height)
        }
    }

    override fun updateHook() {
        if (blinkTimer.isStopped) {
            blinkTimer.restart()
        }
        blinkTimer.update()
    }

    override fun dispose() {
    }

    fun build() {
        builder.build(this)
    }

    fun buildNext() {
        builder.nextStructure()
        builder.build(this)
    }
}