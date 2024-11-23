package com.kalikov.game

interface ScreenSurface : Render {
    val width: Pixel

    val height: Pixel

    val pixels: IntArray

    fun getPixel(x: Pixel, y: Pixel): ARGB

    fun getPixels(x: Pixel, y: Pixel, width: Pixel, height: Pixel): IntArray

    fun dispose()
}