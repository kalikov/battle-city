package com.kalikov.game

import com.kalikov.engine.Pixel
import com.kalikov.engine.px
import kotlinx.serialization.Serializable

@Serializable
data class PixelPoint(val x: Pixel = 0.px, val y: Pixel = 0.px) {
    fun translate(dx: Pixel, dy: Pixel) = PixelPoint(x + dx, y + dy)

    fun multiply(scale: Int) = multiply(scale, scale)

    fun multiply(sx: Int, sy: Int) = PixelPoint(x * sx, y * sy)
}