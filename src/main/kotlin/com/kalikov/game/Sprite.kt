package com.kalikov.game

abstract class Sprite(
    private val eventRouter: EventRouter,
    x: Int,
    y: Int,
    val width: Int,
    val height: Int
) {
    var x = x
        private set
    var y = y
        private set
    var z = 0

    var bounds = Rect(x, y, width, height)
        private set
    val left get() = x
    val right get() = x + width - 1
    val top get() = y

    val bottom get() = y + height - 1

    val center get() = Point(x + width / 2, y + height / 2)

    val position get() = Point(x, y)

    var static = false

    var isDestroyed = false
        private set

    fun setPosition(point: Point) {
        setPosition(point.x, point.y)
    }

    fun setPosition(x: Int, y: Int) {
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
        bounds = Rect(x, y, width, height)
    }

    protected open fun boundsHook() = Unit

    abstract fun dispose()

    data class Destroyed(val sprite: Sprite) : Event()
}