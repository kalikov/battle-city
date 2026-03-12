package com.kalikov.game

import com.kalikov.engine.ARGB
import com.kalikov.engine.ScreenSurface
import com.kalikov.engine.px

class Curtain {
    var height = Globals.CANVAS_HEIGHT / 2
    var position = 0.px

    val isFallen get() = position >= height
    val isRisen get() = position <= 0

    fun reset() {
        height = Globals.CANVAS_HEIGHT / 2
        position = 0.px
    }

    fun fall() {
        if (isFallen) {
            return
        }

        position++

        if (isFallen) {
            position = height
        }
    }

    fun rise() {
        if (isRisen) {
            return
        }

        position--

        if (isRisen) {
            position = 0.px
        }
    }

    fun draw(surface: ScreenSurface) {
        surface.fillRect(0.px, 0.px, surface.width, position, ARGB.rgb(0x808080))
        surface.fillRect(0.px, surface.height - position, surface.width, surface.height, ARGB.rgb(0x808080))
    }
}