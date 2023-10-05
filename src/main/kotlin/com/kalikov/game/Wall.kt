package com.kalikov.game

abstract class Wall(
    eventRouter: EventRouter,
    x: Int,
    y: Int
) : Sprite(eventRouter, x, y, Globals.TILE_SIZE, Globals.TILE_SIZE) {
    abstract val hitRect: Rect

    abstract fun hit(bullet: Bullet)

    override fun dispose() {
    }
}