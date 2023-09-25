package com.kalikov.game

class LivesView(
    private val imageManager: ImageManager,
    private val player: Player,
    private val x: Int,
    private val y: Int
) {
    fun draw(surface: ScreenSurface) {
        val romanOne = imageManager.getImage("roman_one")
        surface.draw(x + Globals.TILE_SIZE - romanOne.width - 2, y, romanOne) { dst, src, _, _ ->
            src.and(ARGB.BLACK).over(dst)
        }
        surface.fillText("P", x + Globals.TILE_SIZE + 1, y + Globals.TILE_SIZE - 1, ARGB.BLACK, Globals.FONT_REGULAR)

        surface.fillText(
            player.lives.toString(),
            x + Globals.TILE_SIZE + 1,
            y + Globals.UNIT_SIZE - 1,
            ARGB.BLACK,
            Globals.FONT_REGULAR
        )

        surface.draw(x, y + Globals.TILE_SIZE, imageManager.getImage("lives"))
    }
}