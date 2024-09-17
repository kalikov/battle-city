package com.kalikov.game

import kotlinx.serialization.Serializable

@Serializable
data class FontConfig(
    val path: String,
    val size: Int
)