package com.kalikov.game

data class Stage(
    val map: StageMapConfig,
    val enemies: List<EnemyGroupConfig>
)
