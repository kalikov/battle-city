package com.kalikov.game

data class TileRect(
    val x: Tile = t(0),
    val y: Tile = t(0),
    val width: Tile = t(0),
    val height: Tile = t(0),
) {
    constructor(position: TilePoint, width: Tile, height: Tile) : this(position.x, position.y, width, height)

    val left get() = x
    val right get() = x + width - 1
    val top get() = y
    val bottom get() = y + height - 1

    fun contains(other: TileRect): Boolean {
        return other.left >= left
                && other.right <= right
                && other.bottom <= bottom
                && other.top >= top
    }

    fun contains(x: Tile, y: Tile): Boolean {
        return x.toInt() in left.toInt()..right.toInt() && y.toInt() in top.toInt()..bottom.toInt()
    }

    fun intersects(other: TileRect): Boolean {
        return left <= other.right && right >= other.left && top <= other.bottom && bottom >= other.top
    }
}
