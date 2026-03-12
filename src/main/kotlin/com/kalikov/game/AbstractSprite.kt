package com.kalikov.game

import com.kalikov.engine.Pixel

abstract class AbstractSprite(
    x: Pixel,
    y: Pixel,
    val width: Pixel,
    val height: Pixel
) : Sprite {
    private companion object {
        private var counter = 0
    }

    override val id = ++counter

    var x = x
        private set
    var y = y
        private set

    var bounds = PixelRect(x, y, width, height)
        private set

    val left get() = x
    val right get() = x + width - 1
    val top get() = y
    val bottom get() = y + height - 1

    val center get() = x + width / 2
    val middle get() = y + height / 2

    final override var isDestroyed = false
        private set

    fun setPosition(point: PixelPoint) {
        setPosition(point.x, point.y)
    }

    fun setPosition(x: Pixel, y: Pixel) {
        if (x != this.x || y != this.y) {
            this.x = x
            this.y = y
            updateBounds()
        }
    }

    fun update(): Boolean {
        if (isDestroyed) {
            dispose()
            return true
        }
        updateHook()
        return false
    }

    protected open fun updateHook() = Unit

    fun destroy() {
        isDestroyed = true
    }

    private fun updateBounds() {
        bounds = PixelRect(x, y, width, height)
        boundsHook()
    }

    protected open fun boundsHook() = Unit

    abstract fun dispose()
}