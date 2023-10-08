package com.kalikov.game

object Globals {
    const val UNIT_SIZE = 16
    const val TILE_SIZE = UNIT_SIZE / 2
    const val CANVAS_WIDTH = UNIT_SIZE * 16

    const val CANVAS_HEIGHT = UNIT_SIZE * 15

    const val FONT_REGULAR = "prstart"
    const val FONT_REGULAR_CORRECTION = TILE_SIZE - 1
    const val FONT_REGULAR_SIZE = TILE_SIZE

    const val FONT_BIG = "prstart-32"
    const val FONT_BIG_SIZE = 32
    const val FONT_BIG_CORRECTION = 4 * FONT_REGULAR_CORRECTION

    const val DATA_DIR = "data"
}
