package com.kalikov.game

interface MutableScreenSurfaceData : ScreenSurfaceData, AutoCloseable {
    fun setPixel(x: Int, y: Int, color: ARGB)
    fun setPixels(x: Int, y: Int, width: Int, height: Int, colors: IntArray)

    fun unlock()

    override fun close() {
        unlock()
    }
}