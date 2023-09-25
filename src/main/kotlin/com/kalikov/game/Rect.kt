package com.kalikov.game

data class Rect(
    val x: Int = 0,
    val y: Int = 0,
    val width: Int = 0,
    val height: Int = 0
) {
    constructor(x: Int, y: Int, size: Size) : this(x, y, size.width, size.height)

    constructor(position: Point, size: Size) : this(position.x, position.y, size.width, size.height)

    constructor(position: Point, width: Int, height: Int) : this(position.x, position.y, width, height)

    val left get() = x
    val right get() = x + width - 1
    val top get() = y
    val bottom get() = y + height - 1

    fun contains(other: Rect): Boolean {
        return other.left >= left
                && other.right <= right
                && other.bottom <= bottom
                && other.top >= top
    }

    fun intersects(other: Rect): Boolean {
        return !(left > other.right ||
                right < other.left ||
                top > other.bottom ||
                bottom < other.top)
    }

    fun move(x: Int, y: Int) = Rect(x, y, width, height)

    fun translate(dx: Int, dy: Int) = Rect(x + dx, y + dy, width, height)

    fun multiply(factor: Int) = Rect(x * factor, y * factor, width * factor, height * factor)
}
