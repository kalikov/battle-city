package com.kalikov.game

enum class Direction(val index: Int) {
    UP(0),
    LEFT(1),
    DOWN(2),
    RIGHT(3);

    val isHorizontal get() = this == RIGHT || this == LEFT

    val isVertical get() = this == UP || this == DOWN
}