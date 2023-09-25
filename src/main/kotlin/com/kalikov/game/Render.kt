package com.kalikov.game

interface Render {
    fun clear(color: ARGB)
    fun clear(x: Int, y: Int, width: Int, height: Int, color: ARGB)

    fun draw(x: Int, y: Int, surface: ScreenSurface, quadrants: Int)
    fun draw(x: Int, y: Int, surface: ScreenSurface, blending: Blending? = null)

    fun draw(
        dstX: Int,
        dstY: Int,
        surface: ScreenSurface,
        srcX: Int,
        srcY: Int,
        width: Int,
        height: Int,
        blending: Blending? = null
    )

    fun drawRect(x: Int, y: Int, w: Int, h: Int, color: ARGB)
    fun fillRect(x: Int, y: Int, w: Int, h: Int, color: ARGB)

    fun drawLine(x1: Int, y1: Int, x2: Int, y2: Int, color: ARGB)

    fun fillText(text: String, x: Int, y: Int, color: ARGB, font: String, blending: Blending? = null)
}
