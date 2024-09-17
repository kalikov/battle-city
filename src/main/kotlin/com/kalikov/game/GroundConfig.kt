package com.kalikov.game

import kotlinx.serialization.Serializable

@Serializable
data class GroundConfig(
    val ice: Set<TilePoint> = emptySet(),
    val water: Set<TilePoint> = emptySet(),
)