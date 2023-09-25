package com.kalikov.game

open class TextureBlending(private val image: ScreenSurfaceData) : Blending {
    override fun blend(dst: ARGB, src: ARGB, x: Int, y: Int): ARGB {
        return image.getPixel(x % image.width, y % image.height)
    }
}