package com.kalikov.game

import com.kalikov.engine.Animation
import com.kalikov.engine.Pixel
import com.kalikov.engine.ScreenSurface
import com.kalikov.engine.px
import com.kalikov.engine.times

abstract class Explosion(
    private val animation: Animation,
    explosionSize: Pixel,
    x: Pixel = 0.px,
    y: Pixel = 0.px,
) : AbstractSprite(x, y, explosionSize, explosionSize) {
    protected abstract val image: ScreenSurface

    override fun updateHook() {
        if (!animation.isRunning) {
            animation.restart()
        }
        animation.update()
        if (animation.isCompleted) {
            destroy()
        }
    }

    override fun draw(surface: ScreenSurface) {
        surface.draw(x, y, image, (animation.frame - 1) * width, 0.px, width, height)
    }

    override fun dispose() {
        animation.stop()
    }
}