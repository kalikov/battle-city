package com.kalikov.game

interface WallsHandle {
    val config: WallsConfig

    fun clearTile(x: Tile, y: Tile)
    fun fillBrickTile(x: Tile, y: Tile)
    fun fillSteelTile(x: Tile, y: Tile)

    fun hit(bullet: Bullet): Boolean

    fun collides(tank: Tank, dx: Pixel, dy: Pixel): Boolean
    fun occupied(x: Tile, y: Tile): Boolean
}