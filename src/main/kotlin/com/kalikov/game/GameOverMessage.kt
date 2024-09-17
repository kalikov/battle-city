package com.kalikov.game

class GameOverMessage(
    override var x: Pixel = px(0),
    override var y: Pixel = px(0),
) : Moveable {
    var isVisible: Boolean = false

    fun draw(surface: ScreenSurface) {
        if (isVisible) {
            surface.fillText("GAME", x, y, ARGB.rgb(0xe44437), Globals.FONT_REGULAR)
            surface.fillText("OVER", x, y + t(1).toPixel(), ARGB.rgb(0xe44437), Globals.FONT_REGULAR)
        }
    }
}