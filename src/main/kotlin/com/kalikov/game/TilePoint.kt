package com.kalikov.game

import kotlinx.serialization.Serializable

@Serializable
data class TilePoint(val x: Tile = 0.tiles, val y: Tile = 0.tiles) {
    fun toPixelPoint(): PixelPoint {
        return PixelPoint(x * Globals.TILE_SIZE, y * Globals.TILE_SIZE)
    }

    fun translate(dx: Tile, dy: Tile): TilePoint {
        return TilePoint(x + dx, y + dy)
    }
}