package com.kalikov.game

class Curtain {
    var height = Globals.CANVAS_HEIGHT / 2
    var position = 0

    val isFallen: Boolean get() = position >= height
    val isRisen: Boolean get() = position <= 0

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
            position = 0
        }
    }

    fun draw(surface: ScreenSurface) {
        surface.fillRect(0, 0, surface.width, position, ARGB.rgb(0x808080))
        surface.fillRect(0, surface.height - position, surface.width, surface.height, ARGB.rgb(0x808080))
    }
}