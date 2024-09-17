package com.kalikov.game

import kotlinx.serialization.Serializable

@Serializable
data class BrickTile(
    val x: Tile = t(0),
    val y: Tile = t(0),
    val integrity: Int = 0b1111
) {
    override fun toString(): String {
        return "BrickTile(x=$x, y=$y, integrity=0b${integrity.toString(2).padStart(4, '0')})"
    }
}