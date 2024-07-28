package com.kalikov.game

@kotlinx.serialization.Serializable
data class EnemyGroupConfig(
    val type: EnemyTank.EnemyType,
    val count: Int
)
