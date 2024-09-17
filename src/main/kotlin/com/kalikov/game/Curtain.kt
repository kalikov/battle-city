package com.kalikov.game

class Curtain {
    var height = Globals.CANVAS_HEIGHT / 2
    var position = px(0)

    val isFallen get() = position >= height
    val isRisen get() = position <= 0

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
            position = px(0)
        }
    }

    fun draw(surface: ScreenSurface) {
        surface.fillRect(px(0), px(0), surface.width, position, ARGB.rgb(0x808080))
        surface.fillRect(px(0), surface.height - position, surface.width, surface.height, ARGB.rgb(0x808080))
    }
}