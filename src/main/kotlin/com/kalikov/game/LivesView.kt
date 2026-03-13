package com.kalikov.game

import com.kalikov.engine.ARGB
import com.kalikov.engine.Blending
import com.kalikov.engine.LazyImage
import com.kalikov.engine.Pixel
import com.kalikov.engine.ScreenSurface
import kotlin.math.max

class LivesView(
    game: BattleCityGame,
    private val players: List<Player>,
    private val x: Pixel,
    private val y: Pixel,
) {
    private companion object {
        private val blending = Blending { dst, src, _, _ -> src.and(ARGB.BLACK).over(dst) }
    }

    private val romanOne = LazyImage.Blender(
        game.screen,
        game.imageManager.romanOne,
        blending
    )

    private val romanTwo = LazyImage.Blender(
        game.screen,
        game.imageManager.romanTwo,
        blending
    )

    private val lives = game.imageManager.getImage("lives")

    private var lives0: Int = players[0].lives
    private var lives0Text = formatLives(lives0)

    private var lives1: Int = if (players.size > 1) players[1].lives else 0
    private var lives1Text = formatLives(lives1)

    fun draw(surface: ScreenSurface) {
        val xMain = x + 1.tiles.toPixel()

        if (players[0].lives != lives0) {
            lives0 = players[0].lives
            lives0Text = formatLives(lives0)
        }

        surface.draw(xMain - romanOne.width - 2, y, romanOne.target)
        surface.fillText("P", xMain + 1, y + 1.tiles.toPixel() - 1, ARGB.BLACK, Globals.FONT_REGULAR)
        surface.fillText(lives0Text, xMain + 1, y + 2.tiles.toPixel() - 1, ARGB.BLACK, Globals.FONT_REGULAR)
        surface.draw(x, y + 1.tiles.toPixel(), lives)

        if (players.size > 1) {
            if (players[1].lives != lives1) {
                lives1 = players[1].lives
                lives1Text = formatLives(lives1)
            }

            surface.draw(xMain - romanTwo.width - 1, y + 3.tiles.toPixel(), romanTwo.target)
            surface.fillText("P", xMain + 1, y + 4.tiles.toPixel() - 1, ARGB.BLACK, Globals.FONT_REGULAR)
            surface.fillText(lives1Text, xMain + 1, y + 5.tiles.toPixel() - 1, ARGB.BLACK, Globals.FONT_REGULAR)
            surface.draw(x, y + 4.tiles.toPixel(), lives)
        }
    }

    private fun formatLives(lives: Int): String = max(0, lives - 1).toString()

    fun dispose() {
        romanOne.dispose()
        romanTwo.dispose()
    }
}