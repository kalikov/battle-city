package com.kalikov.game

class SteelWallFactory(
    private val eventRouter: EventRouter,
    private val imageManager: ImageManager,
) : WallFactory {
    override fun create(x: Int, y: Int): SteelWall {
        return SteelWall(eventRouter, imageManager, x, y)
    }
}