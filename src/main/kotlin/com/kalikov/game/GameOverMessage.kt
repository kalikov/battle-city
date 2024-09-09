package com.kalikov.game

class GameOverMessage(
    override var x: Int = 0,
    override var y: Int = 0,
) : Moveable {
    var isVisible: Boolean = false

    fun draw(surface: ScreenSurface) {
        if (isVisible) {
            surface.fillText("GAME", x, y, ARGB.rgb(0xe44437), Globals.FONT_REGULAR)
            surface.fillText("OVER", x, y + Globals.TILE_SIZE, ARGB.rgb(0xe44437), Globals.FONT_REGULAR)
        }
    }
}