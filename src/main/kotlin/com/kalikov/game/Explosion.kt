package com.kalikov.game

abstract class Explosion(
    eventRouter: EventRouter,
    private val animation: Animation,
    explosionSize: Pixel,
    x: Pixel = px(0),
    y: Pixel = px(0),
) : Sprite(eventRouter, x, y, explosionSize, explosionSize) {
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
        surface.draw(x, y, image, (animation.frame - 1) * width, px(0), width, height)
    }

    override fun dispose() {
        animation.dispose()
    }
}