package com.kalikov.game

import com.kalikov.engine.Event
import com.kalikov.engine.EventRouter
import com.kalikov.engine.Pixel
import com.kalikov.engine.ScreenSurface
import com.kalikov.engine.px

class Base(
    private val eventRouter: EventRouter,
    imageManager: ImageManager,
    override val x: Pixel = 0.px,
    override val y: Pixel = 0.px,
) : BaseHandle {
    companion object {
        val SIZE = 2.tiles.toPixel()
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
            surface.draw(x, y, image, if (isHit) width else 0.px, 0.px, width, height)
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