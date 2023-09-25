package com.kalikov.game

interface ScreenSurfaceData : Render {
    val width: Int
    val height: Int

    fun getPixel(x: Int, y: Int): ARGB

    val pixels: IntArray

    fun getPixels(x: Int, y: Int, width: Int, height: Int): IntArray
}
