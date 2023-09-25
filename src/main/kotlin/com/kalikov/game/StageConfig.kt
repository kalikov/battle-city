package com.kalikov.game

@kotlinx.serialization.Serializable
data class StageConfig(
    val map: String,
    val enemies: List<EnemyGroupConfig>
)
