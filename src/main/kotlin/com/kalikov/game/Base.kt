package com.kalikov.game

class Base(
    private val eventRouter: EventRouter,
    imageManager: ImageManager,
    override val x: Pixel = px(0),
    override val y: Pixel = px(0),
) : BaseHandle {
    companion object {
        val SIZE = t(2).toPixel()
    }

    val width = SIZE
    val height = SIZE

    override val bounds = PixelRect(x, y, width, height)
    val center = PixelPoint(x + width / 2, y + height / 2)

    data class Hit(val base: BaseHandle) : Event()

    override var isHit = false
        private set

    override var isHidden = false

    private val image = imageManager.getImage("base")

    fun draw(surface: ScreenSurface) {
        if (!isHidden) {
            surface.draw(x, y, image, if (isHit) width else px(0), px(0), width, height)
        }
    }

    fun dispose() {
    }

    override fun hit() {
        if (isHit) {
            return
        }
        isHit = true
        eventRouter.fireEvent(Hit(this))
    }
}