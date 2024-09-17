package com.kalikov.game

interface Render {
    fun clear(color: ARGB)

    fun clear(x: Pixel, y: Pixel, width: Pixel, height: Pixel, color: ARGB)

    fun draw(x: Pixel, y: Pixel, surface: ScreenSurface, quadrants: Int)

    fun draw(x: Pixel, y: Pixel, surface: ScreenSurface, blending: Blending? = null)

    fun draw(
        dstX: Pixel,
        dstY: Pixel,
        surface: ScreenSurface,
        srcX: Pixel,
        srcY: Pixel,
        width: Pixel,
        height: Pixel,
        blending: Blending? = null
    )

    fun drawRect(x: Pixel, y: Pixel, w: Pixel, h: Pixel, color: ARGB)

    fun fillRect(x: Pixel, y: Pixel, w: Pixel, h: Pixel, color: ARGB)

    fun drawLine(x1: Pixel, y1: Pixel, x2: Pixel, y2: Pixel, color: ARGB)

    fun fillText(text: String, x: Pixel, y: Pixel, color: ARGB, font: String, blending: Blending? = null)
}
