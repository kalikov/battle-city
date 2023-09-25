package com.kalikov.game

@kotlinx.serialization.Serializable
data class StageObject(
    val type: String,
    val x: Int,
    val y: Int
)