package com.kalikov.game

interface MutableScreenSurfaceData : ScreenSurfaceData, AutoCloseable {
    fun setPixel(x: Pixel, y: Pixel, color: ARGB)

    fun setPixels(x: Pixel, y: Pixel, width: Pixel, height: Pixel, colors: IntArray)

    fun unlock()

    override fun close() {
        unlock()
    }
}