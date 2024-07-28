package com.kalikov.game

@kotlinx.serialization.Serializable
data class KeyEventConfig(
    val key: Keyboard.Key,
    val player: Int,
)