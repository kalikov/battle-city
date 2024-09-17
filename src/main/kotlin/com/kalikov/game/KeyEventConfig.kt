package com.kalikov.game

import kotlinx.serialization.Serializable

@Serializable
data class KeyEventConfig(
    val key: Keyboard.Key,
    val player: Int,
)