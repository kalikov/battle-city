package com.kalikov.game

data class Stage(
    val map: StageMapConfig,
    val enemySpawnDelay: Int,
    val enemies: List<EnemyGroupConfig>
)
