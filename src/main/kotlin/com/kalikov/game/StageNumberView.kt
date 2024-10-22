package com.kalikov.game

class StageNumberView(
    private val imageManager: ImageManager,
    private val stageNumber: Int,
    private val x: Pixel,
    private val y: Pixel
) {
    fun draw(surface: ScreenSurface) {
        val flag = imageManager.getImage("flag")
        surface.draw(x, y, flag)

        val stageNumber = "$stageNumber".padStart(2, ' ')
        surface.fillText(stageNumber, x + 1, y + flag.height + Globals.TILE_SIZE - 1, ARGB.BLACK, Globals.FONT_REGULAR)
    }
}