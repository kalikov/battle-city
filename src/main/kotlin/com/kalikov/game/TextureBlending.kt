package com.kalikov.game

import com.kalikov.engine.ARGB
import com.kalikov.engine.Blending
import com.kalikov.engine.Pixel
import com.kalikov.engine.ScreenSurface

open class TextureBlending(private val image: ScreenSurface) : Blending {
    override fun blend(dst: ARGB, src: ARGB, x: Pixel, y: Pixel): ARGB {
        return image.getPixel(x % image.width, y % image.height).over(dst)
    }
}