package com.kalikov.game

abstract class Explosion(
    eventRouter: EventRouter,
    private val animation: Animation,
    explosionSize: Int
) : Sprite(eventRouter, 0, 0, explosionSize, explosionSize) {
    init {
        z = 200
    }

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
        surface.draw(x, y, image, (animation.frame - 1) * width, 0, width, height)
    }

    override fun dispose() {
        animation.dispose()
    }
}