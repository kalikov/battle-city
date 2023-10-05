package com.kalikov.game

class StageMessage(private val stageManager: StageManager) {
    var isVisible = false

    fun draw(surface: ScreenSurface) {
        if (!isVisible) {
            return
        }
        val x = Globals.CANVAS_WIDTH / 2 - 4 * 8
        val y = Globals.CANVAS_HEIGHT / 2 - 2
        val stage = stageManager.stageNumber
        surface.fillText("STAGE " + stage.toString().padStart(2, ' '), x, y, ARGB.BLACK, Globals.FONT_REGULAR)
    }

}