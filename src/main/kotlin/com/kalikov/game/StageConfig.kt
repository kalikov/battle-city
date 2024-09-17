package com.kalikov.game

import kotlinx.serialization.Serializable

@Serializable
data class StageConfig(
    val map: String,
    val enemies: List<EnemyGroupConfig>,
    val enemySpawnDelay: Int? = null
)
