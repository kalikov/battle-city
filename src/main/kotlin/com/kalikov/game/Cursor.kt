package com.kalikov.game

import com.kalikov.engine.BlinkTimer
import com.kalikov.engine.Pixel
import com.kalikov.engine.ScreenSurface
import com.kalikov.engine.px
import com.kalikov.engine.times

class Cursor(
    game: BattleCityGame,
    private val builder: BuilderHandler,
    x: Pixel = 0.px,
    y: Pixel = 0.px
) /*: Sprite(game.eventManager, x, y, Tank.SIZE, Tank.SIZE)*/ {

    private val blinkTimer = BlinkTimer(game.clock, 320)

    private val image = game.imageManager.playerOneTank

    val width get() = Tank.SIZE
    val height get() = Tank.SIZE

    var bounds = PixelRect(x, y, width, height)
        private set

    var x: Pixel = x
        private set
    var y: Pixel = y
        private set

    fun draw(surface: ScreenSurface) {
        if (blinkTimer.isOpaque) {
            surface.draw(x, y, image, Direction.UP.index * width, 0.px, width, height)
        }
    }

    fun update() {
        if (blinkTimer.isStopped) {
            blinkTimer.restart()
        }
        blinkTimer.update()
    }

    fun build() {
        builder.build(this)
    }

    fun buildNext() {
        builder.nextStructure()
        builder.build(this)
    }

    fun reset() {
        builder.reset()
    }

    fun setPosition(x: Pixel, y: Pixel) {
        if (x != this.x || y != this.y) {
            this.x = x
            this.y = y
            updateBounds()
        }
    }

    private fun updateBounds() {
        bounds = PixelRect(x, y, width, height)
    }
}