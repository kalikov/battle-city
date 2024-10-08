package com.kalikov.game

abstract class Sprite(
    private val eventRouter: EventRouter,
    x: Pixel,
    y: Pixel,
    val width: Pixel,
    val height: Pixel
) {
    private companion object {
        private var counter = 0
    }

    val id = ++counter

    var x = x
        private set
    var y = y
        private set
    var z = 0

    var bounds = PixelRect(x, y, width, height)
        private set

    val left get() = x
    val right get() = x + width - 1
    val top get() = y
    val bottom get() = y + height - 1

    val center get() = x + width / 2
    val middle get() = y + height / 2

    var isDestroyed = false
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

    abstract fun draw(surface: ScreenSurface)

    fun update() {
        if (isDestroyed) {
            doDestroy()
            return
        }
        updateHook()
    }

    protected open fun updateHook() = Unit

    fun destroy() {
        isDestroyed = true
    }

    private fun doDestroy() {
        eventRouter.fireEvent(Destroyed(this))

        destroyHook()

        dispose()
    }

    protected open fun destroyHook() = Unit

    private fun updateBounds() {
        bounds = PixelRect(x, y, width, height)
        boundsHook()
    }

    protected open fun boundsHook() = Unit

    abstract fun dispose()

    data class Destroyed(val sprite: Sprite) : Event()
}