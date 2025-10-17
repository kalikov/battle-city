package com.kalikov.game

import com.kalikov.engine.ARGB
import com.kalikov.engine.Pixel
import com.kalikov.engine.ScreenSurface

class StageNumberView(
    imageManager: ImageManager,
    stageNumber: Int,
    private val x: Pixel,
    private val y: Pixel,
) {
    private val stageNumberText = "$stageNumber".padStart(2, ' ')

    private val flag = imageManager.getImage("flag")

    fun draw(surface: ScreenSurface) {
        surface.draw(x, y, flag)

        surface.fillText(stageNumberText, x + 1, y + flag.height + Globals.TILE_SIZE - 1, ARGB.BLACK, Globals.FONT_REGULAR)
    }
}