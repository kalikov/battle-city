package com.kalikov.game

import java.time.Clock

class MainMenuCursorView(
    imageManager: ImageManager,
    clock: Clock
) {
    companion object {
        val SIZE = Tank.SIZE
    }

    var visible = false

    private val trackAnimation = Animation.basic(frameLoopOf(1, 2), clock, 64)

    private val image = imageManager.getImage("tank_player1")

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

    fun dispose() {
        trackAnimation.dispose()
    }
}