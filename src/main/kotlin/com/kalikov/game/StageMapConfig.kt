package com.kalikov.game

import kotlinx.serialization.Serializable

@Serializable
data class StageMapConfig(
    val ground: GroundConfig = GroundConfig(),
    val walls: WallsConfig = WallsConfig(),
    val trees: Set<TilePoint> = emptySet(),
    val base: TilePoint,
    val playerSpawnPoints: List<TilePoint>,
    val enemySpawnPoints: List<TilePoint>
)