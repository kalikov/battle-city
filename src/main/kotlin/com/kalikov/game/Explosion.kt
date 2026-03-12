package com.kalikov.game

import com.kalikov.engine.Animation
import com.kalikov.engine.Pixel
import com.kalikov.engine.ScreenSurface
import com.kalikov.engine.px
import com.kalikov.engine.times

abstract class Explosion(
    private val animation: Animation,
    explosionSize: Pixel,
    x: Pixel = px(0),
    y: Pixel = px(0),
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
        surface.draw(x, y, image, (animation.frame - 1) * width, px(0), width, height)
    }

    override fun dispose() {
        animation.stop()
    }
}