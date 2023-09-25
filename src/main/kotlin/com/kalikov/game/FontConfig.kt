package com.kalikov.game

@kotlinx.serialization.Serializable
data class FontConfig(
    val path: String,
    val size: Int
)