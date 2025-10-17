package com.kalikov.engine

interface ScreenSurface : Render {
    val width: Pixel

    val height: Pixel

    fun getPixel(x: Pixel, y: Pixel): ARGB

    fun dispose()
}