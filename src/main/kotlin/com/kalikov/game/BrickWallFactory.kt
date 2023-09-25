package com.kalikov.game

class BrickWallFactory(
    private val eventRouter: EventRouter,
    private val imageManager: ImageManager
) : WallFactory {
    override fun create(x: Int, y: Int): BrickWall {
        return BrickWall(eventRouter, imageManager, x, y)
    }
}