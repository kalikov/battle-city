package com.kalikov.game

class GameOverMessage(
    override var x: Int,
    override var y: Int
) : Moveable {

    fun draw(surface: ScreenSurface) {
        surface.fillText("GAME", x, y, ARGB.rgb(0xe44437), Globals.FONT_REGULAR)
        surface.fillText("OVER", x, y + Globals.TILE_SIZE, ARGB.rgb(0xe44437), Globals.FONT_REGULAR)
    }
}