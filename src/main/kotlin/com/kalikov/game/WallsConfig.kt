package com.kalikov.game

import kotlinx.serialization.Serializable

@Serializable
data class WallsConfig (
    val steel: Set<TilePoint> = emptySet(),
    val bricks: Set<BrickTile> = emptySet(),
)