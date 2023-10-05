package com.kalikov.game

class Base(
    private val eventRouter: EventRouter,
    imageManager: ImageManager,
    x: Int,
    y: Int
) : Sprite(eventRouter, x, y, SIZE, SIZE) {
    companion object {
        const val SIZE = Globals.UNIT_SIZE
    }

    data class Hit(val base: Base) : Event()

    var isHit = false
        private set

    private val image = imageManager.getImage("base")

    override fun draw(surface: ScreenSurface) {
        surface.draw(x, y, image, if (isHit) width else 0, 0, width, height)
    }

    override fun dispose() {
    }

    fun hit() {
        if (isHit) {
            return
        }
        isHit = true
        eventRouter.fireEvent(Hit(this))
    }
}