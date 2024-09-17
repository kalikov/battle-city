package com.kalikov.game

import kotlinx.serialization.Serializable

@Serializable
data class TilePoint(val x: Tile = t(0), val y: Tile = t(0)) {
    fun toPixelPoint(): PixelPoint {
        return PixelPoint(x * Globals.TILE_SIZE, y * Globals.TILE_SIZE)
    }

    fun translate(dx: Tile, dy: Tile): TilePoint {
        return TilePoint(x + dx, y + dy)
    }
}