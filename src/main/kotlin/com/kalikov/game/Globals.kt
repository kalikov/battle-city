package com.kalikov.game

object Globals {
    val TILE_SIZE = px(8)

    val CANVAS_WIDTH = TILE_SIZE * 32

    val CANVAS_HEIGHT = TILE_SIZE * 30

    const val FONT_REGULAR = "prstart"
    val FONT_REGULAR_CORRECTION = TILE_SIZE - 1
    val FONT_REGULAR_SIZE = TILE_SIZE

    const val FONT_BIG = "prstart-32"
    val FONT_BIG_CORRECTION = 4 * FONT_REGULAR_CORRECTION

    const val DATA_DIR = "data"
}
