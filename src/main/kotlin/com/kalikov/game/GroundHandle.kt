package com.kalikov.game

interface GroundHandle {
    fun fillWaterTile(x: Tile, y: Tile)

    fun fillIceTile(x: Tile, y: Tile)

    fun clearTile(x: Tile, y: Tile)

    fun isTankOnIce(tank: Tank): Boolean

    fun collides(tank: Tank, dx: Pixel, dy: Pixel): Boolean
}