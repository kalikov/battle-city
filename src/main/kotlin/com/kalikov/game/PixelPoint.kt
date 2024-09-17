package com.kalikov.game

import kotlinx.serialization.Serializable

@Serializable
data class PixelPoint(val x: Pixel = px(0), val y: Pixel = px(0)) {
    fun translate(dx: Pixel, dy: Pixel) = PixelPoint(x + dx, y + dy)

    fun multiply(scale: Int) = multiply(scale, scale)

    fun multiply(sx: Int, sy: Int) = PixelPoint(x * sx, y * sy)
}