package com.kalikov.game

import kotlin.math.max

class LivesView(
    private val imageManager: ImageManager,
    private val players: List<Player>,
    private val x: Int,
    private val y: Int
) {
    private companion object {
        private const val TS = Globals.TILE_SIZE
        private const val US = Globals.UNIT_SIZE
    }

    fun draw(surface: ScreenSurface) {
        val romanOne = imageManager.getImage("roman_one")
        surface.draw(x + TS - romanOne.width - 2, y, romanOne) { dst, src, _, _ ->
            src.and(ARGB.BLACK).over(dst)
        }
        surface.fillText("P", x + TS + 1, y + TS - 1, ARGB.BLACK, Globals.FONT_REGULAR)

        surface.fillLivesText(players[0].lives, x + TS + 1, y + US - 1)

        surface.draw(x, y + TS, imageManager.getImage("lives"))

        if (players.size > 1) {
            val romanTwo = imageManager.getImage("roman_two")
            surface.draw(x + TS - romanTwo.width - 1, y + 3 * TS, romanTwo) { dst, src, _, _ ->
                src.and(ARGB.BLACK).over(dst)
            }
            surface.fillText("P", x + TS + 1, y + 4 * TS - 1, ARGB.BLACK, Globals.FONT_REGULAR)

            surface.fillLivesText(players[1].lives, x + TS + 1, y + 5 * TS - 1)
            surface.draw(x, y + 4 * TS, imageManager.getImage("lives"))
        }
    }

    private fun ScreenSurface.fillLivesText(lives: Int, x: Int, y: Int) {
        this.fillText(
            max(0, lives - 1).toString(),
            x,
            y,
            ARGB.BLACK,
            Globals.FONT_REGULAR
        )
    }
}