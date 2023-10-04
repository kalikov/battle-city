package com.kalikov.game

import java.awt.event.KeyEvent

@kotlinx.serialization.Serializable
data class KeyboardConfig (
    val up: Int = KeyEvent.VK_UP,
    val down: Int = KeyEvent.VK_DOWN,
    val left: Int = KeyEvent.VK_LEFT,
    val right: Int = KeyEvent.VK_RIGHT,
    val start: Int = KeyEvent.VK_ENTER,
    val select: Int = KeyEvent.VK_BACK_SPACE,
    val action: Int = KeyEvent.VK_SPACE
)