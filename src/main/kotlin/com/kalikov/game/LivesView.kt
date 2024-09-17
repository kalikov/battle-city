package com.kalikov.game

import kotlin.math.max

class LivesView(
    private val imageManager: ImageManager,
    private val players: List<Player>,
    private val x: Pixel,
    private val y: Pixel,
) {
    fun draw(surface: ScreenSurface) {
        val romanOne = imageManager.getImage("roman_one")
        surface.draw(x + t(1).toPixel() - romanOne.width - 2, y, romanOne) { dst, src, _, _ ->
            src.and(ARGB.BLACK).over(dst)
        }
        surface.fillText("P", x + t(1).toPixel() + 1, y + t(1).toPixel() - 1, ARGB.BLACK, Globals.FONT_REGULAR)

        surface.fillLivesText(players[0].lives, x + t(1).toPixel() + 1, y + t(2).toPixel() - 1)

        surface.draw(x, y + t(1).toPixel(), imageManager.getImage("lives"))

        if (players.size > 1) {
            val romanTwo = imageManager.getImage("roman_two")
            surface.draw(x + t(1).toPixel() - romanTwo.width - 1, y + t(3).toPixel(), romanTwo) { dst, src, _, _ ->
                src.and(ARGB.BLACK).over(dst)
            }
            surface.fillText("P", x + t(1).toPixel() + 1, y + t(4).toPixel() - 1, ARGB.BLACK, Globals.FONT_REGULAR)

            surface.fillLivesText(players[1].lives, x + t(1).toPixel() + 1, y + t(5).toPixel() - 1)
            surface.draw(x, y + t(4).toPixel(), imageManager.getImage("lives"))
        }
    }

    private fun ScreenSurface.fillLivesText(lives: Int, x: Pixel, y: Pixel) {
        this.fillText(
            max(0, lives - 1).toString(),
            x,
            y,
            ARGB.BLACK,
            Globals.FONT_REGULAR
        )
    }
}