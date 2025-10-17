package com.kalikov.engine

abstract class LazyImage(
    val screen: Screen,
    val width: Pixel,
    val height: Pixel,
) {
    private var image: ScreenSurface? = null

    val target: ScreenSurface
        get() {
            return image ?: run {
                draw()
                image!!
            }
        }

    fun draw() {
        if (image == null) {
            val surface = screen.createSurface(width, height)
            surface.clear(ARGB.TRANSPARENT)
            draw(surface)
            image = surface
        }
    }

    protected abstract fun draw(target: ScreenSurface)

    fun dispose() {
        image?.dispose()
        image = null
    }

    class Blender(
        screen: Screen,
        val source: ScreenSurface,
        val blending: Blending,
    ) : LazyImage(screen, source.width, source.height) {
        override fun draw(target: ScreenSurface) {
            target.draw(px(0), px(0), source, blending)
        }
    }

    class Custom(
        screen: Screen,
        width: Pixel,
        height: Pixel,
        private val function: (ScreenSurface) -> Unit
    ) : LazyImage(screen, width, height) {
        override fun draw(target: ScreenSurface) = function.invoke(target)
    }
}