package com.kalikov.game

@kotlinx.serialization.Serializable
data class Point(val x: Int = 0, val y: Int = 0) {
    fun translate(dx: Int, dy: Int) = Point(x + dx, y + dy)

    fun multiply(scale: Int) = multiply(scale, scale)

    fun multiply(sx: Int, sy: Int) = Point(x * sx, y * sy)
}