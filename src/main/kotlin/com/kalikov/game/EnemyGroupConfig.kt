package com.kalikov.game

import kotlinx.serialization.Serializable

@Serializable
data class EnemyGroupConfig(
    val type: EnemyTank.EnemyType,
    val count: Int
)
