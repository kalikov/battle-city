package com.kalikov.game

@kotlinx.serialization.Serializable
data class StageMapConfig(
    val objects: List<StageObject>,
    val base: Point,
    val playerSpawnPoints: List<Point>,
    val enemySpawnPoints: List<Point>
)