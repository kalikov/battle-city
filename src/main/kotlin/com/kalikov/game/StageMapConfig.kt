package com.kalikov.game

@kotlinx.serialization.Serializable
data class StageMapConfig(
    val objects: List<StageObject>,
    val base: Point,
    val playerSpawnPoint: Point,
    val enemySpawnPoints: List<Point>
)