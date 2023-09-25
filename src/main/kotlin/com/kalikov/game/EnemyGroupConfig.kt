package com.kalikov.game

@kotlinx.serialization.Serializable
data class EnemyGroupConfig(
    val type: Tank.EnemyType,
    val count: Int
)
