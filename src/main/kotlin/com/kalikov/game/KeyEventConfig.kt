package com.kalikov.game

import com.kalikov.engine.Keyboard
import kotlinx.serialization.Serializable

@Serializable
data class KeyEventConfig(
    val key: Keyboard.Key,
    val player: Int,
)