package com.kalikov.game

data class PixelRect(
    val x: Pixel = px(0),
    val y: Pixel = px(0),
    val width: Pixel = px(0),
    val height: Pixel = px(0),
) {
    constructor(x: Pixel, y: Pixel, size: PixelSize) : this(x, y, size.width, size.height)

    constructor(position: PixelPoint, size: PixelSize) : this(position.x, position.y, size.width, size.height)

    constructor(position: PixelPoint, width: Pixel, height: Pixel) : this(position.x, position.y, width, height)

    val left get() = x
    val right get() = x + width - 1
    val top get() = y
    val bottom get() = y + height - 1

    val area get() = width * height

    fun contains(other: PixelRect): Boolean {
        return other.left >= left
                && other.right <= right
                && other.bottom <= bottom
                && other.top >= top
    }

    fun contains(x: Pixel, y: Pixel): Boolean {
        return x.toInt() in left.toInt()..right.toInt() && y.toInt() in top.toInt()..bottom.toInt()
    }

    fun intersects(other: PixelRect): Boolean {
        return left <= other.right && right >= other.left && top <= other.bottom && bottom >= other.top
    }

    fun move(x: Pixel, y: Pixel) = PixelRect(x, y, width, height)

    fun translate(dx: Pixel, dy: Pixel) = PixelRect(x + dx, y + dy, width, height)
}
