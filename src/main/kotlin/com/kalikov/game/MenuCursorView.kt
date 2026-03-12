package com.kalikov.game

import com.kalikov.engine.Animation
import com.kalikov.engine.Pixel
import com.kalikov.engine.ScreenSurface
import com.kalikov.engine.frameLoopOf
import com.kalikov.engine.px
import java.time.Clock

class MenuCursorView(
    imageManager: ImageManager,
    clock: Clock
) {
    companion object {
        val SIZE = Tank.SIZE
    }

    var visible = false

    private val trackAnimation = Animation.basic(frameLoopOf(1, 2), clock, 64)

    private val image = imageManager.playerOneTank

    fun update() {
        if (!trackAnimation.isRunning) {
            trackAnimation.restart()
        }
        trackAnimation.update()
    }

    fun draw(surface: ScreenSurface, x: Pixel, y: Pixel) {
        if (!visible) {
            return
        }
        surface.draw(
            x,
            y,
            image,
            Tank.SIZE * (5 + trackAnimation.frame),
            px(0),
            SIZE,
            SIZE
        )
    }
}